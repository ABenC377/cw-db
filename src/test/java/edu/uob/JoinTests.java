package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class JoinTests {
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
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
            "Server took too long to respond (probably stuck in an infinite loop)");
    }
    
    @Test
    public void joinTest_1() {
        System.out.println("_____________TESTING JOIN____________");
        System.out.println(sendCommandToServer("JOIN marks AND courses ON " +
            "pass AND difficult;"));
    }
    
    
}
