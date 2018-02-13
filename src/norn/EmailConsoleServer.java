package norn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

/**
 * Console interface to the expression system.
 * 
 * <p>You are free to change this user interface class.
 */
public class EmailConsoleServer {
    // Thread Safety Argument for ConsoleServer:
    // In the console server, the environment is the only variable that is shared, and since it is synchronized,
    // only one thread can acquire the lock, read, and mutate environment at a time.
    
    // Example of console input tests:
    //      > hobbits = bilbo@shire, frodo@shire, sam@shire, merry@shire, pippin@shire
    //      bilbo@shire, frodo@shire, merry@shire, pippin@shire, sam@shire
    //      > gandalf@cosmos, hobbits
    //      bilbo@shire, frodo@shire, gandalf@cosmos, merry@shire, pippin@shire, sam@shire
    //      > bagginses = bilbo@shire, frodo@shire
    //      bilbo@shire, frodo@shire
    //      > hobbits !bagginses
    //      merry@shire, pippin@shire, sam@shire
    //
    //      *** New Session ***
    //
    //      > x = (a@mit.edu, b@MIT.edu, c@mit.edu, A@MIT.edU); y = (C@mit.edu, d@mit.edu);
    //      
    //      > x
    //      a@mit.edu, b@mit.edu, c@mit.edu
    //      > y
    //      c@mit.edu, d@mit.edu
    //      > x, y
    //      a@mit.edu, b@mit.edu, c@mit.edu, d@mit.edu
    //      > z = (x ! y); z
    //      a@mit.edu, b@mit.edu
    //      > x*y
    //      c@mit.edu
    //Example of console input tests for loading and saving:
    //   1) Basic saving and loading of listname assigned to emails
    //      > a = a@b
    //        a@b
    //      > b = c@d
    //        c@d
    //      > !save test1.txt
    //      **** New Session ***
    //      > !load test1.txt
    //      > a
    //        a@b
    //      > b
    //        c@d
    //   2) Simple saving and loading of listname assigned to listnames
    //      > a=akuang@mit.edu
    //        akuang@mit.edu
    //      > b=xinyic@mit.edu
    //        xinyic@mit.edu
    //      > c=a,b
    //       xinyic@mit.edu, akuang@mit.edu
    //      > !save test2.txt
    //      **** New Session ***
    //      > !load test2.txt
    //      > a
    //       akuang@mit.edu
    //      > b
    //       xinyic@mit.edu
    //      > c
    //       xinyic@mit.edu, akuang@mit.edu
    //   3) Existing listname overridden by file load, same session
    //      > a=initial@b.com
    //        initial@b.com
    //      > b= initialtwo@x.com
    //        initialtwo@x.com
    //      > c= etc@o.com
    //        etc@o.com
    //      > a
    //        initial@b.com
    //      > b
    //        initialtwo@x.com
    //      > c
    //        etc@o.com
    //      > !load test2.txt
    //      > a
    //        akuang@mit.edu
    //      > b
    //        xinyic@mit.edu
    //      > c
    //        xinyic@mit.edu, akuang@mit.edu
    //      > 
    //    4) Loading a file that does not exist (hello.txt in this example)
    //      > !load hello.txt
    //        File does not exist
    /**
     * Read expression and command inputs from the console and output results.
     * In the cases of invalid !save command (folder does not exist, illegal characters in name)
     * or invalid !load commands (improperly formatted list expressions inside the file), prints an error message.
     * @param environment the environment in which the value of mailing lists are stored
     * @param filenames the list of filenames to load
     * @throws IOException if there is an error reading the input
     */
    public static void startConsole(Map<String, EmailList> environment, String[] filenames) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for(String filename: filenames){
            try{
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                String line = reader.readLine(); 
                synchronized (environment) {
                    while(line!=null){
                        EmailList.evaluate(line, environment);
                        line = reader.readLine();
                    }
                    reader.close();
                }
            }
            catch(FileNotFoundException e){
                System.out.println("File does not exist");
            }
            catch(IllegalArgumentException e){
                System.out.println("Improperly formatted line cannot be parsed");
            }

        }
        
        while (true) {
            System.out.print("> ");
            final String input = in.readLine();
            if(input.length()==0){
                System.out.println("");
                continue;
            }
            if(input.charAt(0)=='!'){
                if (input.substring(0,5).equals("!save")) {
                    final String fileName = input.substring(6); 
                    Writer writer = null;
                    try {
                        writer = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(fileName), "utf-8"));
                        // write each key and value pair of environment as an assignment
                        //  separated by semicolons
                        synchronized (environment) {
                            for (String listName: environment.keySet()) {
                                String line = listName + "=" + environment.get(listName).toString() + ";";
                                writer.write(line);
//                                System.out.println(line);
                            }
                        }
                        writer.flush();
                        writer.close();
                    } catch (IOException ex) {
                       //ex.printStackTrace();
                        System.out.println("Either the folder does not exist or the filename contains illegal characters.");
                    } 
                }
                
                else if (input.substring(0,5).equals("!load")) {
                    final String filename = input.substring(6);
                    try{
                        BufferedReader reader = new BufferedReader(new FileReader(filename));
                        String line = reader.readLine(); 
                        synchronized (environment) {
                            while(line!=null){
                                EmailList.evaluate(line, environment);
                                line = reader.readLine();
                            }
                        }
                        reader.close();
                    }
                    catch(FileNotFoundException e){
                        System.out.println("File does not exist");
                    }
                    catch(IllegalArgumentException e){
                        System.out.println("Improperly formatted line cannot be parsed");
                    }
                } 
            }else {
                try {
                    synchronized (environment) {
                        final String output;
                        final EmailList emailList = EmailList.evaluate(input, environment);
                        Set<String> recipientsSet = emailList.recipients(environment);
                        String[] recipientsArray = recipientsSet.toArray(new String[recipientsSet.size()]);
                        output = String.join(", ", recipientsArray);    
                        System.out.println(output);
                    }
                } catch (RuntimeException re) {
                    System.out.println(re.getClass().getName() + ": " + re.getMessage());
                }
            }          
        }
    }
 
}
