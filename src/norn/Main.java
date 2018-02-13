package norn;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;

public class Main {
    // Thread Safety Argument:
    //      The only shared variable is the environment map.
    //      There is a synchronized block for the environment in all methods that read and mutate environment, for both
    //      the console server and the web server, so environment can only be accessed by a single thread at a time.
    //      Only one thread can have access to the lock on environment at a time.
    
    /**
     * Main method. Starts the web server and the email user console interface with the given command line arguments.
     * If the given command line arguments are valid space-separated files to load from, loads the files.
     * @param args command-line arguments. If args are valid space-separated files to load from, loads the files.
     */
    public static void main(String[] args) throws IOException { 
        final Map<String, EmailList> environment = new HashMap<>();
        new EmailWebServer().startWebServer(environment);
        EmailConsoleServer.startConsole(environment, args);
    }

}

