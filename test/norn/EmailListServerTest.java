/* Copyright (c) 2015-2017 MIT 6.005/6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package norn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Tests for EmailList server
 */
public class EmailListServerTest {
    // Testing Strategy
    /*
     * # of emails displayed: 0, 1, >1
     * # of expression in sequence: 0, 1, >1
     * # of elements in initial environment: 0, 1, >1
     * changes environment: yes, no
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    // # of emails displayed: 1
    // # of expressions in sequence: 1
    // # of elements in initial environment: 0
    // changes environment: no
    public void testServerSingleEmail() throws IOException {
        final EmailWebServer server = new EmailWebServer();
        server.startWebServer(Collections.emptyMap());

        final String valid = "http://localhost:" + server.SERVER_PORT + "/eval/a@b";
        final URL url = new URL(valid);

        final InputStream input = url.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = reader.readLine();
        String emails = line.substring(51);
        assertEquals("correct emails", "a@b", emails);
        server.stop();
    }
    
    @Test
    // # of emails displayed: 0
    // # of expressions in sequence: 1
    // # of elements in initial environment: 0
    // changes environment: no
    public void testServerSingleNewListName() throws IOException {
        final EmailWebServer server = new EmailWebServer();
        Map<String, EmailList> environment = new HashMap<>();
        server.startWebServer(environment);

        final String valid = "http://localhost:" + server.SERVER_PORT + "/eval/a";
        final URL url = new URL(valid);
        try {
            url.openStream();
            assertTrue(false);
        } catch(FileNotFoundException e) {
            server.stop();
        }
    }
    
    @Test
    // # of emails displayed: >1
    // # of expressions in sequence: 1
    // # of elements in initial environment: >1
    // changes environment: no
    public void testServerFullEnvironment() throws IOException {
        final EmailWebServer server = new EmailWebServer();
        Map<String, EmailList> environment = new HashMap<>();
        environment.put("a", EmailList.single("a@test"));
        environment.put("b", EmailList.single("b@test"));
        server.startWebServer(environment);

        final String valid = "http://localhost:" + server.SERVER_PORT + "/eval/a,b";
        final URL url = new URL(valid);

        final InputStream input = url.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = reader.readLine();
        assertEquals("correct emails", "<a href=\"mailto:a@test,b@test\">email these recipients</a><br>a@test,b@test", line);
        server.stop();
    }
    
    @Test
    // # of emails displayed: >1
    // # of expressions in sequence: >1
    // # of elements in initial environment: 1
    // changes environment: yes
    public void testServerMultiExpressions() throws IOException {
        final EmailWebServer server = new EmailWebServer();
        Map<String, EmailList> environment = new HashMap<>();
        environment.put("a", EmailList.single("a@test"));
        server.startWebServer(environment);

        final String valid = "http://localhost:" + server.SERVER_PORT + "/eval/b=b@test;c=c@test;d=a,b,c";
        final URL url = new URL(valid);

        final InputStream input = url.openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = reader.readLine();
        assertEquals("correct emails", "<a href=\"mailto:a@test,c@test,b@test\">email these recipients</a><br>a@test,c@test,b@test", line);
        server.stop();
        
        EmailList emailA = EmailList.single("a@test");
        EmailList emailB = EmailList.single("b@test");
        EmailList emailC = EmailList.single("c@test");
        assertTrue("environment is correct", environment.get("a").equals(emailA));
        assertTrue("environment is correct", environment.get("b").equals(emailB));
        assertTrue("environment is correct", environment.get("c").equals(emailC));
        assertTrue("environment is correct", environment.get("d").toString().equals("((a) , (b)) , (c)"));
    }
    

}
