package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ParserValidQueryTests {


    @BeforeEach
    public void setup() {

    }

    @Test
    public void testUse() throws IOException {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("USE test;");
        assertEquals("<command>\n" +
                "└── <use>\n" +
                "    └── [database name](value = test)\n", ast.toString());
    }
    @Test
    public void testCreateDatabase() throws IOException {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("CREATE DATABASE test;");
        assertEquals("<command>\n" +
                "└── <create>\n" +
                "    └── [database name](value = test)\n", ast.toString());
    }
    @Test
    public void testCreateTable() throws IOException {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("CREATE TABLE test;");
        assertEquals("<command>\n" +
                "└── <create>\n" +
                "    └── [table name](value = test)\n", ast.toString());
    }
    @Test
    public void testCreateTableWithAttributes() throws IOException {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("CREATE TABLE test " +
            "(name, age, job, height);");
        System.out.println("testing <create> with attributes");
        System.out.println(ast);
        assertTrue(true);
    }
    @Test
    public void testDrop() throws IOException {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("DROP DATABASE name;");
        assertEquals("<command>\n" +
                "└── <drop>\n" +
                "    └── [database name](value = name)\n", ast.toString());
    }
    @Test
    public void testAlter() throws IOException {
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
    public void testInsert() throws IOException {
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
    public void testSelectSimple() throws IOException {
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
    public void testSelectWhere() throws IOException {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("SELECT * FROM table WHERE (country LIKE 'rance');");
        assertEquals("<command>\n" +
                "└── <select>\n" +
                "    ├── <wild attribute list>(value = *)\n" +
                "    ├── [table name](value = table)\n" +
                "    └── <condition>\n" +
                "        └── <condition>\n" +
                "            └── <condition>\n" +
                "                ├── [attribute name]\n" +
                "                │   └── [plain text](value = country)\n" +
                "                ├── [comparator](value = LIKE)\n" +
                "                └── [value]\n" +
                "                    └── [string literal](value = rance)\n", ast.toString());
    }


    @Test
    public void testUpdate() throws IOException {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("UPDATE fancyTableName  SET fancyTableName.income = NULL WHERE (age > 2 OR (country LIKE 'rance' AND name == 'james'));");
        assertEquals("<command>\n" +
                "└── <update>\n" +
                "    ├── [table name](value = fancyTableName)\n" +
                "    ├── <name value list>\n" +
                "    │   └── <name value pair>\n" +
                "    │       ├── [attribute name]\n" +
                "    │       │   ├── [table name](value = fancyTableName)\n" +
                "    │       │   └── [plain text](value = income)\n" +
                "    │       └── [value](value = NULL)\n" +
                "    └── <condition>\n" +
                "        └── <condition>\n" +
                "            ├── <condition>\n" +
                "            │   ├── [attribute name]\n" +
                "            │   │   └── [plain text](value = age)\n" +
                "            │   ├── [comparator](value = >)\n" +
                "            │   └── [value]\n" +
                "            │       └── [integer literal](value = 2)\n" +
                "            ├── [boolean operator](value = OR)\n" +
                "            └── <condition>\n" +
                "                ├── <condition>\n" +
                "                │   ├── [attribute name]\n" +
                "                │   │   └── [plain text](value = country)\n" +
                "                │   ├── [comparator](value = LIKE)\n" +
                "                │   └── [value]\n" +
                "                │       └── [string literal](value = rance)\n" +
                "                ├── [boolean operator](value = AND)\n" +
                "                └── <condition>\n" +
                "                    ├── [attribute name]\n" +
                "                    │   └── [plain text](value = name)\n" +
                "                    ├── [comparator](value = ==)\n" +
                "                    └── [value]\n" +
                "                        └── [string literal](value = james)\n", ast.toString());
    }

    @Test
    public void testDelete() throws IOException {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("DELETE  FROM efsfsenfsenoi WHERE name LIKE ' erg %$ PL12';");
        assertEquals("<command>\n" +
                "└── <delete>\n" +
                "    ├── [table name](value = efsfsenfsenoi)\n" +
                "    └── <condition>\n" +
                "        └── <condition>\n" +
                "            ├── [attribute name]\n" +
                "            │   └── [plain text](value = name)\n" +
                "            ├── [comparator](value = LIKE)\n" +
                "            └── [value]\n" +
                "                └── [string literal](value =  erg %$ PL12)\n", ast.toString());
    }

    @Test
    public void testJoin() throws IOException {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("JOIN tableOne AND tableTwo ON id AND id;");
        assertEquals("<command>\n" +
                "└── <join>\n" +
                "    ├── [table name](value = tableOne)\n" +
                "    ├── [table name](value = tableTwo)\n" +
                "    ├── [attribute name]\n" +
                "    │   └── [plain text](value = id)\n" +
                "    └── [attribute name]\n" +
                "        └── [plain text](value = id)\n", ast.toString());
    }

    /*
    @Test
    public void testFormat() throws IOException {
        AbstractSyntaxTree ast = new AbstractSyntaxTree("UPDATE fancyTableName  SET fancyTableName.income = NULL WHERE (age > 2 OR (country LIKE 'rance' AND name == 'james'));");
        System.out.println("testing <update>");
        System.out.println(ast);
        assertTrue(true);
    }

     */

}
