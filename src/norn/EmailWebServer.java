package norn;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class EmailWebServer {
    
    // Thread Safety Argument for WebServer:
    // In the web server, the environment is the only variable that is shared, and since it is synchronized,
    // only one thread can acquire the lock, read, and mutate environment at a time.

    
    
    /*
     * Port where web server is listening.
     */
    public final int SERVER_PORT = 5021;
    private HttpServer server;
    
    /*
     * Starts the web server.
     * @param port port number where web server will listen for HTTP requests
     * @param environment global map of list names to email list objects
     * @throws IOException if server can't start up, e.g. if the port is already busy
     */
    public void startWebServer(Map<String, EmailList> environment) throws IOException {
        server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        server.createContext("/eval/", new HttpHandler(){
            public void handle(HttpExchange exchange) throws IOException{
                handleEvaluation(exchange, environment);
            }
        });
        // start the server
        server.start();
        System.err.println("Server is listening on http://localhost:" + SERVER_PORT);
    }
    
    /*
     * Handle a /eval/EmailList expression request.
     * 
     * If EmailList expression is a valid list expression after parsing, then render an HTML response 
     * that displays a mailto: option to email all the recipients, and displays the list of recipients in that EmailList expression. 
     * If the EmailList Expression is empty, then renders an HTML page saying that the EmailList expression is empty.
     * If the EmailList Expression is invalid, then renders an Error Response Page.
     * 
     * @param exchange HTTP request/response. Modified by this method to send
     * a response to the client and close the exchange.
     */
    private void handleEvaluation(final HttpExchange exchange, Map<String, EmailList> environment) throws IOException {
        // if you want to know the requested path:
        final String path = exchange.getRequestURI().getPath();
        
        // it will always start with the base path from server.createContext():
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);
        synchronized (environment) {
            final String listExpressionString = path.substring(base.length());
            final EmailList evaluatedEmailList = EmailList.evaluate(listExpressionString, environment);
            final Set<String> recipients = evaluatedEmailList.recipients(environment);
            if (!recipients.isEmpty()) {
                exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
                exchange.sendResponseHeaders(200, 0);
                PrintWriter out = new PrintWriter(exchange.getResponseBody(), true);
                final String recipientsString = String.join(",", recipients);
                out.println("<a href=\"mailto:" + recipientsString + "\">email these recipients</a><br>" + recipientsString);
            } else {
                // respond with HTTP code 404 to indicate an error
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(404, 0);
                PrintWriter err = new PrintWriter(exchange.getResponseBody(), true);
                err.println("the resulting list name is empty: " + listExpressionString);            
            }
            exchange.close();
        }
    }
    
    public void stop() {
        server.stop(0);
    }
}
