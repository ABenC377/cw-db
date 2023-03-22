package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class AlteringTableTests {
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
    public void testChangingTable_1() {
        assertEquals("[OK]\n", sendCommandToServer("Alter Table MARKS ADD " +
            "Other;"));
    }
    
    @Test
    public void testChangingTable_2() {
        assertEquals("[OK]\n", sendCommandToServer("Alter Table MARKS ADD " +
            "Other;"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\tworrisomedata\tOther\t\n" +
            "1\tSteve\t65\tTRUE\t17.4\tNULL\t\n" +
            "2\tDave\t55\tTRUE\tFALSE\tNULL\t\n" +
            "3\tBob\t35\tFALSE\twillow\tNULL\t\n" +
            "4\tClive\t20\tFALSE\t40\tNULL\t\n", sendCommandToServer("SELECT * FROM marks;"));
    }
    
    @Test
    public void testChangingTable_3() {
        assertEquals("[OK]\n", sendCommandToServer("Alter Table MARKS ADD " +
            "Other;"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\tworrisomedata\tOther\t\n" +
            "1\tSteve\t65\tTRUE\t17.4\tNULL\t\n" +
            "2\tDave\t55\tTRUE\tFALSE\tNULL\t\n" +
            "3\tBob\t35\tFALSE\twillow\tNULL\t\n" +
            "4\tClive\t20\tFALSE\t40\tNULL\t\n", sendCommandToServer("SELECT * FROM marks;"));
        assertEquals("[OK]\n", sendCommandToServer("DELETE FROM marks WHERE " +
            "mark < " +
            "50;"));
    }
    
    @Test
    public void testChangingTable_4() {
        assertEquals("[OK]\n", sendCommandToServer("Alter Table MARKS ADD " +
            "Other;"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\tworrisomedata\tOther\t\n" +
            "1\tSteve\t65\tTRUE\t17.4\tNULL\t\n" +
            "2\tDave\t55\tTRUE\tFALSE\tNULL\t\n" +
            "3\tBob\t35\tFALSE\twillow\tNULL\t\n" +
            "4\tClive\t20\tFALSE\t40\tNULL\t\n", sendCommandToServer("SELECT * FROM marks;"));
        assertEquals("[OK]\n", sendCommandToServer("DELETE FROM marks WHERE " +
            "mark < 50;"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\tworrisomedata\tOther\t\n" +
            "1\tSteve\t65\tTRUE\t17.4\tNULL\t\n" +
            "2\tDave\t55\tTRUE\tFALSE\tNULL\t\n", sendCommandToServer("SELECT * FROM marks;"));
    }
    
    @Test
    public void testChangingTable_5() {
        assertEquals("[OK]\n", sendCommandToServer("Alter Table MARKS ADD " +
            "Other;"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\tworrisomedata\tOther\t\n" +
            "1\tSteve\t65\tTRUE\t17.4\tNULL\t\n" +
            "2\tDave\t55\tTRUE\tFALSE\tNULL\t\n" +
            "3\tBob\t35\tFALSE\twillow\tNULL\t\n" +
            "4\tClive\t20\tFALSE\t40\tNULL\t\n", sendCommandToServer("SELECT * FROM marks;"));
        assertEquals("[OK]\n", sendCommandToServer("DELETE FROM marks WHERE " +
            "mark < 50;"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\tworrisomedata\tOther\t\n" +
            "1\tSteve\t65\tTRUE\t17.4\tNULL\t\n" +
            "2\tDave\t55\tTRUE\tFALSE\tNULL\t\n", sendCommandToServer("SELECT * FROM marks;"));
        assertEquals("[OK]\n", sendCommandToServer("INSERT INTO marks VALUES " +
            "('Gus', 80, true, 5.22, 'other');"));
    }
    
    @Test
    public void testChangingTable_6() {
        assertEquals("[OK]\n", sendCommandToServer("Alter Table MARKS ADD " +
            "Other;"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\tworrisomedata\tOther\t\n" +
            "1\tSteve\t65\tTRUE\t17.4\tNULL\t\n" +
            "2\tDave\t55\tTRUE\tFALSE\tNULL\t\n" +
            "3\tBob\t35\tFALSE\twillow\tNULL\t\n" +
            "4\tClive\t20\tFALSE\t40\tNULL\t\n", sendCommandToServer("SELECT * FROM marks;"));
        assertEquals("[OK]\n", sendCommandToServer("DELETE FROM marks WHERE " +
            "mark < 50;"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\tworrisomedata\tOther\t\n" +
            "1\tSteve\t65\tTRUE\t17.4\tNULL\t\n" +
            "2\tDave\t55\tTRUE\tFALSE\tNULL\t\n", sendCommandToServer("SELECT * FROM marks;"));
        assertEquals("[OK]\n", sendCommandToServer("INSERT INTO marks VALUES " +
            "('Gus', 80, true, 5.22, 'other');"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\tworrisomedata\tOther\t\n" +
            "1\tSteve\t65\tTRUE\t17.4\tNULL\t\n" +
            "2\tDave\t55\tTRUE\tFALSE\tNULL\t\n" +
            "5\tGus\t80\tTRUE\t5.22\tother\t\n", sendCommandToServer("SELECT" +
            " * FROM marks;"));
    }
    
    @Test
    public void testChangingTable_7() {
        assertEquals("[OK]\n", sendCommandToServer(
            "UPDATE courses SET difficult=TRUE WHERE teacher LIKE 'Simon';"));
    }
    
    @Test
    public void testChangingTable_8() {
        assertEquals("[OK]\n", sendCommandToServer(
                "UPDATE courses SET difficult=TRUE WHERE teacher LIKE 'Simon';"));
        assertEquals("[OK]\n" +
            "id\tname\tteacher\tstudents\tdifficult\t\n" +
            "1\tCompArch\tAnas\t120\tTRUE\t\n" +
            "2\tJava\tSimon\t100\tTRUE\t\n" +
            "3\tC\tNeill\t115\tTRUE\t\n" +
            "4\tOverview of SWE\tRuzana\t90\tFALSE\t\n", sendCommandToServer("SELECT" +
            " * FROM courses;"));
    }
}
