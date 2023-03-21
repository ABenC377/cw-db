package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SelectQueryTests {
    DBServer server;
    
    @BeforeEach
    public void setup() {
        // Set up the server and clear out the database directory
        server = new DBServer();
        try {
            server.dropAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    
        sendCommandToServer("CREATE DATABASE testdatabase;");
        sendCommandToServer("USE testdatabase;");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass, " +
            "worrisomedata);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE, 17" +
            ".4);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE, " +
            "fAlse);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE, " +
            "'willow;);");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE, " +
            "40);");
    }
    
    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
            "Server took too long to respond (probably stuck in an infinite loop)");
    }
    
    @Test
    public void testInvalidNestedConditions_emptyBrackets() {
        assertTrue(sendCommandToServer("SELECT mark FROM marks WHERE ();").contains("[ERROR]"));
    }
    
    @Test
    public void testInvalidNestedConditions_openParentheses() {
        assertTrue(sendCommandToServer("SELECT mark FROM marks WHERE (pass " +
            "== true;").contains("[ERROR]"));
    }
    
    @Test
    public void testInvalidNestedConditions_overClosedParentheses() {
        assertTrue(sendCommandToServer("SELECT mark FROM marks WHERE (pass " +
            "== true));").contains("[ERROR]"));
    }
    
    @Test
    public void testValidNestedConditions_one() {
        assertTrue(sendCommandToServer("SELECT mark FROM marks WHERE (pass ==" +
            " true);").contains("[OK]"));
    }
}
