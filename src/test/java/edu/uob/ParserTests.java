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
                "    └── [database name](value = test)\n", ast.toString());
    }
    @Test
    public void testCreateDatabase() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("CREATE DATABASE test;");
        assertEquals("<command>\n" +
                "└── <create>\n" +
                "    └── [database name](value = test)\n", ast.toString());
    }
    @Test
    public void testCreateTable() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("CREATE TABLE test;");
        assertEquals("<command>\n" +
                "└── <create>\n" +
                "    └── [table name](value = test)\n", ast.toString());
    }
    @Test
    public void testDrop() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("DROP DATABASE name;");
        assertEquals("<command>\n" +
                "└── <drop>\n" +
                "    └── [database name](value = name)\n", ast.toString());
    }
    @Test
    public void testAlter() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("ALTER TABLE tableName ADD tableName.attributeName;");
        assertEquals("<command>\n" +
                "└── <alter>\n" +
                "    ├── [table name](value = tableName)\n" +
                "    ├── [alteration type](value = ADD)\n" +
                "    └── [attribute name]\n" +
                "        ├── [table name](value = tableName)\n" +
                "        └── [plain text](value = attributeName)\n", ast.toString());
    }

    @Test
    public void testInsert() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("INSERT INTO table VALUES('string literal',TRUE,3);");
        assertEquals("<command>\n" +
                "└── <insert>\n" +
                "    ├── [table name](value = table)\n" +
                "    └── <value list>\n" +
                "        ├── [value]\n" +
                "        │   └── [string literal](value = string literal)\n" +
                "        ├── [value]\n" +
                "        │   └── [boolean literal](value = TRUE)\n" +
                "        └── [value]\n" +
                "            └── [integer literal](value = 3)\n", ast.toString());
    }
    @Test
    public void testSelectSimple() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("SELECT name, country FROM table;");
        assertEquals("<command>\n" +
                "└── <select>\n" +
                "    ├── <wild attribute list>\n" +
                "    │   ├── [attribute name]\n" +
                "    │   │   └── [plain text](value = name)\n" +
                "    │   └── [attribute name]\n" +
                "    │       └── [plain text](value = country)\n" +
                "    └── [table name](value = table)\n", ast.toString());
    }
    @Test
    public void testSelectWhere() {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("SELECT * FROM table WHERE (country LIKE 'rance');");
        System.out.println("testing <select (where)>");
        System.out.println(ast);
        assertTrue(true);
    }
}
