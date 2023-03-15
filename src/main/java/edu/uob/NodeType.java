package edu.uob;

public enum NodeType {
    // AST node types
    COMMAND("<command>"),
    COMMAND_TYPE("<command type>"),
    USE("<use>"),
    CREATE("<create>"),
    CREATE_DATABASE("<create database>"),
    CREATE_TABLE("<create table"),
    DROP("<drop>"),
    ALTER("<alter>"),
    INSERT("<alter>"),
    SELECT("<select>"),
    UPDATE("<update>"),
    DELETE("<delete>"),
    JOIN("<join>"),
    DIGIT("[digit]"),
    UPPER_CASE("[upper case]"),
    LOWER_CASE("[lower case]"),
    LETTER("[letter]"),
    PLAIN_TEXT("[plain text]"),
    SYMBOL("[symbol]"),
    SPACE("[space]"),
    NAME_VALUE_LIST("<name value list>"),
    NAME_VALUE_PAIR("<name value pair>"),
    ALTERATION_TYPE("[alteration type]"),
    VALUE_LIST("<value list>"),
    DIGIT_SEQUENCE("[digit sequence]"),
    INTEGER_LITERAL("[integer literal]"),
    FLOAT_LITERAL("[float literal]"),
    BOOLEAN_LITERAL("[boolean literal]"),
    CHAR_LITERAL("[char literal]"),
    STRING_LITERAL("[string literal]"),
    VALUE("[value]"),
    TABLE_NAME("[table name]"),
    ATTRIBUTE_NAME("[attribtue name]"),
    DATABASE_NAME("[database name]"),
    WILD_ATTRIBUTE_LIST("<wild attribute list>"),
    ATTRIBUTE_LIST("<attribute list>"),
    CONDITION("<condition>"),
    BOOLEAN_OPERATOR("[boolean operator]"),
    COMPARATOR("[comparator]");

    private final String string;

    NodeType(String name) {
        string = name;
    }

    @Override
    public String toString() {
        return string;
    }
}
