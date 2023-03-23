package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserInvalidQueryTests {
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
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE, 17.4);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE, " +
            "fAlse);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE, " +
            "'willow');");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE, " +
            "40);");
        sendCommandToServer("CREATE TABLE courses (name, teacher, students, " +
            "difficult);");
        sendCommandToServer("INSERT INTO courses VALUES ('CompArch', 'Anas', " +
            "120, true);");
        sendCommandToServer("INSERT INTO courses VALUES ('Java', 'Simon', " +
            "100," +
            " fAlse);");
        sendCommandToServer("INSERT INTO courses VALUES ('C', 'Neill', 115, " +
            "truE);");
        sendCommandToServer("INSERT INTO courses VALUES ('Overview of SWE', " +
            "'Ruzana', 90, FalsE);");
    }
    
    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000),
            () -> { return server.handleCommand(command);},
            "Server took too long to respond (probably stuck in an infinite loop)");
    }
    
    @Test
    public void queryTooLong_TestingOtherwiseOkay() {
        assertTrue(sendCommandToServer("INSERT INTO courses VALUES (1, 2, 3, " +
            "4);").contains("[OK]"));
    }
    
    @Test
    public void queryTooLong_TestingFailure() {
        assertTrue(sendCommandToServer("INSERT INTO courses VALUES (1, 2, 3, " +
            "4); INSERT INTO courses VALUES (5, 6, 7, 8);").contains("[ERROR" +
            "]"));
    }
    
    @Test
    public void testTwoDecimalPlacesInValue_TestingFailure() {
        assertTrue(sendCommandToServer("INSERT INTO courses VALUES (1.3.2, 2," +
            " 3, 4);").contains("[ERROR]"));
    }
    
    @Test
    public void attributeNameWithNonAlphaNumericChars_TestingFailure() {
        assertTrue(sendCommandToServer("ALTER TABLE marks ADD attr/bute;").contains("[ERROR]"));
    }
    
    @Test
    public void tableNameWithNonAlphaNumericChars_TestingFailure() {
        assertTrue(sendCommandToServer("CREATE TABLE tabl@;").contains(
            "[ERROR]"));
    }
}
