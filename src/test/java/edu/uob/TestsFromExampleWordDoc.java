package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class TestsFromExampleWordDoc {
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
    }
    
    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000),
            () -> { return server.handleCommand(command);},
            "Server took too long to respond (probably stuck in an infinite loop)");
    }
    
    @Test
    public void exampleTest_1() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
    }
    
    @Test
    public void exampleTest_2() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_3() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_4() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_5() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_6() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_7() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_8() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t55\tTRUE\t\n" +
            "3\tBob\t35\tFALSE\t\n" +
            "4\tClive\t20\tFALSE\t\n", sendCommandToServer("SELECT * FROM marks;"));
    }
    
    @Test
    public void exampleTest_9() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "3\tBob\t35\tFALSE\t\n" +
            "4\tClive\t20\tFALSE\t\n", sendCommandToServer("SELECT * FROM " +
            "marks WHERE name != 'Dave';"));
    }
    
    @Test
    public void exampleTest_10() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "2\tDave\t55\tTRUE\t\n", sendCommandToServer("SELECT * FROM " +
            "marks WHERE pass == TRUE;"));
    }
    
    @Test
    public void exampleTest_11() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertEquals("[OK]\n" +
            "id\ttask\tsubmission\t\n" +
            "1\tOXO\t3\t\n" +
            "2\tDB\t1\t\n" +
            "3\tOXO\t4\t\n" +
            "4\tSTAG\t2\t\n", sendCommandToServer("SELECT * FROM coursework;"));
    }
    
    @Test
    public void exampleTest_12() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertEquals("[OK]\n" +
            "id\tcoursework.task\tmarks.name\tmarks.mark\tmarks.pass\n" +
            "1\tOXO\tBob\t35\tFALSE\n" +
            "2\tDB\tSteve\t65\tTRUE\n" +
            "3\tOXO\tClive\t20\tFALSE\n" +
            "4\tSTAG\tDave\t55\tTRUE\n", sendCommandToServer("JOIN coursework AND marks " +
            "ON submission AND id;"));
    }
    
    @Test
    public void exampleTest_13() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +
            "name == 'Clive';").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_14() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +
            "name == 'Clive';").contains("[OK]"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "4\tClive\t38\tFALSE\t\n", sendCommandToServer("SELECT * FROM " +
            "marks WHERE name == 'Clive';"));
    }
    
    @Test
    public void exampleTest_15() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +
            "name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +
            "'Dave';").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_16() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +
            "name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +
            "'Dave';").contains("[OK]"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "3\tBob\t35\tFALSE\t\n" +
            "4\tClive\t38\tFALSE\t\n", sendCommandToServer("SELECT * FROM " +
            "marks;"));
    }
    
    @Test
    public void exampleTest_17() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +
            "name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +
            "'Dave';").contains("[OK]"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "4\tClive\t38\tFALSE\t\n", sendCommandToServer("SELECT * FROM " +
            "marks WHERE (pass == FALSE) AND (mark > 35);"));
    }
    
    @Test
    public void exampleTest_18() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +
            "name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +
            "'Dave';").contains("[OK]"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n" +
            "4\tClive\t38\tFALSE\t\n", sendCommandToServer("SELECT * FROM " +
            "marks WHERE name LIKE 've';"));
    }
    
    @Test
    public void exampleTest_19() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +
            "name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +
            "'Dave';").contains("[OK]"));
        assertEquals("[OK]\n" +
            "id\t\n" +
            "3\t\n" +
            "4\t\n", sendCommandToServer("SELECT id FROM " +
            "marks WHERE pass == FALSE;"));
    }
    
    @Test
    public void exampleTest_20() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +
            "name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +
            "'Dave';").contains("[OK]"));
        assertEquals("[OK]\n" +
            "name\t\n" +
            "Steve\t\n", sendCommandToServer("SELECT name FROM marks WHERE " +
            "mark>60;"));
    }
    
    @Test
    public void exampleTest_21() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +
            "name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +
            "'Dave';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE mark<" +
            "40;").contains("[OK]"));
    }
    
    @Test
    public void exampleTest_22() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +
            "name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +
            "'Dave';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE mark<" +
            "40;").contains("[OK]"));
        assertEquals("[OK]\n" +
            "id\tname\tmark\tpass\t\n" +
            "1\tSteve\t65\tTRUE\t\n", sendCommandToServer("SELECT * FROM marks;"));
    }
    
    @Test
    public void exampleTest_23() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +
            "name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +
            "'Dave';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE mark<" +
            "40;").contains("[OK]"));
        assertTrue(sendCommandToServer("SELECT * FROM marks")
            .contains("[ERROR]"));
    }
    
    @Test
    public void exampleTest_24() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +
            "name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +
            "'Dave';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE mark<" +
            "40;").contains("[OK]"));
        assertTrue(sendCommandToServer("SELECT * FROM crew;")
            .contains("[ERROR]"));
    }
    
    @Test
    public void exampleTest_25() {
        assertTrue(sendCommandToServer("CREATE DATABASE markbook;").contains(
            "[OK]"));
        assertTrue(sendCommandToServer("USE markbook;").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE marks (name, mark, pass);")
            .contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Steve', " +
            "65, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Dave', " +
            "55, TRUE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Bob', " +
            "35, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO marks VALUES ('Clive', " +
            "20, FALSE);").contains("[OK]"));
        assertTrue(sendCommandToServer("CREATE TABLE coursework (task, " +
            "submission);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 3);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('DB', 1);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('OXO', 4);").contains("[OK]"));
        assertTrue(sendCommandToServer("INSERT INTO coursework VALUES " +
            "('STAG', 2);").contains("[OK]"));
        assertTrue(sendCommandToServer("UPDATE marks SET mark = 38 WHERE " +
            "name == 'Clive';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE name == " +
            "'Dave';").contains("[OK]"));
        assertTrue(sendCommandToServer("DELETE FROM marks WHERE mark<" +
            "40;").contains("[OK]"));
        assertTrue(sendCommandToServer("SELECT * FROM marks pass == TRUE;")
            .contains("[ERROR]"));
    }
}
