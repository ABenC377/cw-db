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
        
    }
    
    /*
SELECT * FROM coursework;
[OK]
id	task	submission
1	OXO	3
2	DB	1
3	OXO	4
4	STAG	2



// For JOINs: discard the ids from the original tables;
// discard the columns that the tables were matched on;
// create a new unique id for each of row of the table produces
// attribute names should be prepended with the name of the table from which they originated

JOIN coursework AND marks ON submission AND id;
[OK]
id	coursework.task	marks.name	marks.mark	marks.pass
1	OXO			Bob		35		FALSE
2	DB			Steve		65		TRUE
3	OXO			Clive		20		FALSE
4	STAG			Dave		55		TRUE

UPDATE marks SET mark = 38 WHERE name == 'Clive';
[OK]

SELECT * FROM marks WHERE name == 'Clive';
[OK]
id	name	mark	pass
4	Clive	38	FALSE

DELETE FROM marks WHERE name == 'Dave';
[OK]

SELECT * FROM marks;
[OK]
id	name	mark	pass
1	Steve	65	TRUE
3	Bob	35	FALSE
4	Clive	38	FALSE

SELECT * FROM marks WHERE (pass == FALSE) AND (mark > 35);
[OK]
id	name	mark	pass
4	Clive	38	FALSE

SELECT * FROM marks WHERE name LIKE 've';
[OK]
id	name	mark	pass
1	Steve	65	TRUE
4	Clive	38	FALSE

SELECT id FROM marks WHERE pass == FALSE;
[OK]
id
3
4

SELECT name FROM marks WHERE mark>60;
[OK]
name
Steve

DELETE FROM marks WHERE mark<40;
[OK]

SELECT * FROM marks;
[OK]
id	name	mark	pass
1	Steve	65	TRUE

SELECT * FROM marks
[ERROR]: Semi colon missing at end of line (or similar message !)

// Assuming there is NOT a table called “crew” in the database
SELECT * FROM crew;
[ERROR]: Table does not exist (or similar message !)

SELECT * FROM marks pass == TRUE;
[ERROR]: Invalid query (or similar message !)

     */
    
}
