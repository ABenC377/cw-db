package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvalidQueryTests {
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
    public void testAddingTooFewValues() {
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES (7, 14, 33" +
            ".3);").contains("[ERROR]"));
    }
    
    @Test
    public void testAddingTooManyValues() {
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES (7, 14, 33" +
            ".3, true, 'unwanted');").contains("[ERROR]"));
    }
    
    @Test
    public void testCreatingExistingDatabase() {
        assertTrue(sendCommandToServer("CREATE DATABASE testdatabase;").contains("[ERROR]"));
    }
    
    @Test
    public void testCreatingExistingTable() {
        assertTrue(sendCommandToServer("CREATE TABLE MArks;").contains("[ERROR]"));
    }
    
    @Test
    public void testCreatingAttributeWithReservedName() {
        assertTrue(sendCommandToServer("ALTER TABLE marks ADD delete;").contains(
            "[ERROR]"));
    }
    
    @Test
    public void testCreatingTableWithReservedName() {
        assertTrue(sendCommandToServer("CREATE TABLE alTEr;").contains(
            "[ERROR]"));
    }
    
    @Test
    public void testCreatingDatabaseWithReservedName() {
        assertTrue(sendCommandToServer("CREATE DATABASE falSE;").contains(
            "[ERROR]"));
    }
}
