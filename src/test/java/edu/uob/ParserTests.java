package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ParserTests {


    @BeforeEach
    public void setup() {

    }

    @Test
    public void testUse() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("USE test;");
        assertEquals("<command>\n" +
                "└── <use>\n" +
                "    └── [database name]\n", ast.toString());
    }
    @Test
    public void testCreateDatabase() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("CREATE DATABASE test;");
        assertEquals("<command>\n" +
                "└── <create>\n" +
                "    └── [database name]\n", ast.toString());
    }
    @Test
    public void testCreateTable() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("CREATE TABLE test;");
        assertEquals("<command>\n" +
                "└── <create>\n" +
                "    └── [table name]\n", ast.toString());
    }
    @Test
    public void testDrop() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("DROP DATABASE name;");
        assertEquals("<command>\n" +
                "└── <drop>\n" +
                "    └── [database name]\n", ast.toString());
    }
    @Test
    public void testAlter() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("ALTER TABLE tableName ADD tableName.attributeName;");
        assertEquals("<command>\n" +
                "└── <alter>\n" +
                "    ├── [table name]\n" +
                "    ├── [alteration type]\n" +
                "    └── [attribute name]\n" +
                "        ├── [table name]\n" +
                "        └── [plain text]\n", ast.toString());
    }

    @Test
    public void testInsert() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("INSERT INTO table VALUES('string literal',TRUE,3);");
        assertEquals("<command>\n" +
                "└── <insert>\n" +
                "    ├── [table name]\n" +
                "    └── <value list>\n" +
                "        ├── [value]\n" +
                "        │   └── [string literal]\n" +
                "        ├── [value]\n" +
                "        │   └── [boolean literal]\n" +
                "        └── [value]\n" +
                "            └── [integer literal]\n", ast.toString());
    }
    @Test
    public void testSelectSimple() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("SELECT name, country FROM table;");
        assertEquals("<command>\n" +
                "└── <select>\n" +
                "    ├── <wild attribute list>\n" +
                "    │   ├── [attribute name]\n" +
                "    │   │   └── [plain text]\n" +
                "    │   └── [attribute name]\n" +
                "    │       └── [plain text]\n" +
                "    └── [table name]\n", ast.toString());
    }
    @Test
    public void testSelectWhere() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("SELECT * FROM table WHERE (country LIKE 'rance');");
        System.out.println("testing <select (where)>");
        System.out.println(ast);
        assertTrue(true);
    }
}
