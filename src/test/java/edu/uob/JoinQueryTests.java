package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class JoinQueryTests {
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
        assertEquals("[OK]\n" +
            "id\tmarks.name\tmarks.mark\tmarks.worrisomedata\tcourses.name\tcourses.teacher\tcourses.students\n" +
            "1\tSteve\t65\t17.4\tCompArch\tAnas\t120\n" +
            "2\tSteve\t65\t17.4\tC\tNeill\t115\n" +
            "3\tDave\t55\tFALSE\tCompArch\tAnas\t120\n" +
            "4\tDave\t55\tFALSE\tC\tNeill\t115\n" +
            "5\tBob\t35\twillow\tJava\tSimon\t100\n" +
            "6\tBob\t35\twillow\tOverview of SWE\tRuzana\t90\n" +
            "7\tClive\t20\t40\tJava\tSimon\t100\n" +
            "8\tClive\t20\t40\tOverview of SWE\tRuzana\t90\n", sendCommandToServer("JOIN marks AND courses ON " +
            "pass AND difficult;"));
    }
    @Test
    public void joinTest_2() {
        assertEquals("[OK]\n" +
            "id\tmarks.name\tmarks.mark\tmarks.pass\tmarks.worrisomedata\tcourses.name\tcourses.teacher\tcourses.students\n" +
            "1\tDave\t55\tTRUE\tJava\tSimon\t100\n" +
            "2\tDave\t55\tTRUE\tOverview of SWE\tRuzana\t90\n", sendCommandToServer("JOIN marks AND courses ON " +
            "worrisomeData AND difficult;"));
    }
    @Test
    public void joinTest_3() {
        assertTrue(sendCommandToServer("JOIN marks AND courses ON " +
            "worrisomeData and name;").contains("[OK]"));
    }
    @Test
    public void joinTest_4() {
        assertTrue(sendCommandToServer("JOIN marks AND courses ON " +
            "difficult AND name;").contains("[ERROR]"));
    }
    
    
}
