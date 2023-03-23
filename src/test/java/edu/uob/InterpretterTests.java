package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class InterpretterTests {
    DBServer server;
    
    @BeforeEach
    public void setup() {
        server = new DBServer();
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
            "'willow');");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE, " +
            "40);");
    }
    
    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
            "Server took too long to respond (probably stuck in an infinite loop)");
    }
    
    @Test
    public void testValidCreation() {
        assertEquals("""
            [OK]
            id\tname\tmark\tpass\tworrisomedata\t
            1\tSteve\t65\tTRUE\t17.4\t
            2\tDave\t55\tTRUE\tFALSE\t
            3\tBob\t35\tFALSE\twillow\t
            4\tClive\t20\tFALSE\t40\t
            """, sendCommandToServer("SELECT * FROM marks;"));
    }
    
    @Test
    public void testSignedInts() {
        assertEquals("[OK]\n", sendCommandToServer("INSERT INTO marks VALUES" +
            "('Ollie', +69, TRUE, -69);"));
        assertEquals("""
            [OK]
            id\tname\tmark\tpass\tworrisomedata\t
            1\tSteve\t65\tTRUE\t17.4\t
            2\tDave\t55\tTRUE\tFALSE\t
            3\tBob\t35\tFALSE\twillow\t
            4\tClive\t20\tFALSE\t40\t
            5\tOllie\t69\tTRUE\t-69\t
            """, sendCommandToServer("SELECT * FROM marks;"));
    }
    
    @Test
    public void testNotEqualToNull() {
        assertEquals("""
            [OK]
            id\tname\tmark\tpass\tworrisomedata\t
            1\tSteve\t65\tTRUE\t17.4\t
            2\tDave\t55\tTRUE\tFALSE\t
            3\tBob\t35\tFALSE\twillow\t
            4\tClive\t20\tFALSE\t40\t
            """, sendCommandToServer("SELECT * FROM marks WHERE pass != nuLL;"));
    }
    
    @Test
    public void testNotEqualToInt() {
        assertEquals("""
            [OK]
            id\tname\tmark\tpass\tworrisomedata\t
            1\tSteve\t65\tTRUE\t17.4\t
            2\tDave\t55\tTRUE\tFALSE\t
            3\tBob\t35\tFALSE\twillow\t
            4\tClive\t20\tFALSE\t40\t
            """, sendCommandToServer("SELECT * FROM marks WHERE pass != 7;"));
    }
    
    @Test
    public void testNotEqualToFloat() {
        assertEquals("""
            [OK]
            id\tname\tmark\tpass\tworrisomedata\t
            1\tSteve\t65\tTRUE\t17.4\t
            2\tDave\t55\tTRUE\tFALSE\t
            3\tBob\t35\tFALSE\twillow\t
            4\tClive\t20\tFALSE\t40\t
            """, sendCommandToServer("SELECT * FROM marks WHERE pass != 7.8;"));
    }
    
    @Test
    public void testNotEqualToString() {
        assertEquals("""
            [OK]
            id\tname\tmark\tpass\tworrisomedata\t
            1\tSteve\t65\tTRUE\t17.4\t
            2\tDave\t55\tTRUE\tFALSE\t
            3\tBob\t35\tFALSE\twillow\t
            4\tClive\t20\tFALSE\t40\t
            """, sendCommandToServer("SELECT * FROM marks WHERE pass != " +
            "'true';"));
    }
    
    @Test
    public void testNotEqualToBoolean() {
        assertEquals("""
            [OK]
            id\tname\tmark\tpass\tworrisomedata\t
            1\tSteve\t65\tTRUE\t17.4\t
            2\tDave\t55\tTRUE\tFALSE\t
            3\tBob\t35\tFALSE\twillow\t
            4\tClive\t20\tFALSE\t40\t
            """, sendCommandToServer("SELECT * FROM marks WHERE name != " +
            "true;"));
    }
}
