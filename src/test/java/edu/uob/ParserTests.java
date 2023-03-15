package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ParserTests {


    @BeforeEach
    public void setup() {

    }

    /*
    ____________________________________________________________
    !!!      THE BELOW TESTS ARE FOR PRINTING OUT ASTs       !!!
    ____________________________________________________________
     */
    @Test
    public void testUse() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("USE test;");
        System.out.println("testing <Use>");
        System.out.println(ast);
        assertTrue(true);
    }
    @Test
    public void testCreateDatabase() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("CREATE DATABASE test;");
        System.out.println("testing <CreateDatabase>");
        System.out.println(ast);
        assertTrue(true);
    }
    @Test
    public void testDrop() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("DROP DATABASE name;");
        System.out.println("testing <Drop>");
        System.out.println(ast);
        assertTrue(true);
    }
    @Test
    public void testAlter() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("ALTER TABLE tableName ADD attributeName;");
        System.out.println("testing <Alter>");
        System.out.println(ast);
        assertTrue(true);
    }
}
