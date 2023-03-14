package edu.uob;

public class Parser {
    private Node current;
    private String command;
    int index;

    public Parser(Node root, String command) {
        this.command = command;
        this.current = root;
        this.index = 0;
    }

    public boolean populateTree() {
        // making a new child node for the potential commandtype
        current.addChild(new Node(current));
        current = current.getLastChild();

        // checking that there is a valid command type
        if (tryUse() || tryCreate() || tryDrop() || tryAlter() || tryInsert() ||
                trySelect() || tryUpdate() || tryDelete() || tryJoin()) {
            // moving back up to the parent before returning to ensure we don't drift
            // down the tree
            current = current.getParent();
            return true;
        } else {
            // getting rid of the impossible commandtype node before returning
            current = current.getParent();
            current.popChild();
            return false;
        }
    }

    // <Use> ::= "USE " [DatabaseName]
    private boolean tryUse() {
        // save the index so we can reset it before returning false
        int originalIndex = index;

        // check for the keyword
        skipWhiteSpace();
        if (!substringIsNext("USE ")) {
            index = originalIndex;
            return false;
        }

        // set up and move to a new child node for the potential database name
        current.setType(NodeType.USE);
        skipWhiteSpace();
        current.addChild(new Node(NodeType.DATABASE_NAME, current));
        current = current.getLastChild();

        // ________ To think about ___________
        // Can I use Supplier<T> to save on the repeated code below (in each of the try__() methods)
        if (tryPlainText()) {
            current = current.getParent();
            return true;
        } else {
            current = current.getParent();
            current.popChild();
            index = originalIndex;
            return false;
        }
    }

    // <Create> ::= <CreateDatabase> | <CreateTable>
    private boolean tryCreate() {
        return (tryCreateDatabase() || tryCreateTable());
    }

    // <CreateDatabase> ::= "CREATE DATABASE " [DatabaseName]
    private boolean tryCreateDatabase() {
        int originalIndex = index;
        skipWhiteSpace();
        // Check for the key word sequence
        if (!substringIsNext("CREATE TABLE ")) {
            index = originalIndex;
            return false;
        }

        // Check for the table name
        current.setType(NodeType.CREATE_TABLE);
        skipWhiteSpace();
        current.addChild(new Node(NodeType.TABLE_NAME, current));
        current = current.getLastChild();
        if (!tryPlainText()) {
            current = current.getParent();
            current.popChild();
            index = originalIndex;
            return false;
        } else {
            current = current.getParent();
        }

        // Check for a possible attribute list
        int intermediateIndex = index;
        current.addChild(new Node(NodeType.ATTRIBUTE_LIST, current));
        current = current.getLastChild();
        if (substringIsNext("(") && tryAttributeList() && substringIsNext(")")) {
            current = current.getParent();
        } else {
            index = intermediateIndex;
            current = current.getParent();
            current.popChild();
        }
        return true;
    }

    // <AttributeList> ::= [AttributeName] | [AttributeName] "," <AttributeList>
    private boolean tryAttributeList() {
        int resetIndex = index;
        current.addChild(new Node(NodeType.ATTRIBUTE_NAME, current));
        current = current.getLastChild();
        if (tryAttributeName()) {
            current = current.getParent();
        } else {
            current = current.getParent();
            current.popChild();
            index = resetIndex;
            return false;
        }
        resetIndex = index;
        if (substringIsNext(",") && !tryAttributeList()) {
            index = resetIndex;
        }
        return true;
    }

    // [AttributeName] ::= [PlainText] | [TableName] "." [PlainText]
    private boolean tryAttributeName() {
        int resetIndex = index;
        current.addChild(current);
        current = current.getLastChild();
        if (tryPlainText()) {

        }
    }

    // <CreateTable> ::= "CREATE TABLE " [TableName] | "CREATE TABLE " [TableName] "(" <AttributeList> ")"
    private boolean tryCreateTable() {

    }

    // <Drop> ::= "DROP DATABASE " [DatabaseName] | "DROP TABLE " [TableName]
    private boolean tryDrop() {

    }

    // <Alter> ::= "ALTER TABLE " [TableName] " " [AlterationType] " " [AttributeName]
    private boolean tryAlter() {

    }

    // <Insert> ::= "INSERT INTO " [TableName] " VALUES(" <ValueList> ")"
    private boolean tryInsert() {

    }

    // <Select> ::= "SELECT " <WildAttribList> " FROM " [TableName] | "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
    private boolean trySelect() {

    }

    // <Update> ::= "UPDATE " [TableName] " SET " <NameValueList> " WHERE " <Condition>
    private boolean tryUpdate() {

    }

    // <Delete> ::= "DELETE FROM " [TableName] " WHERE " [Condition]
    private boolean tryDelete() {

    }

    // <Join> ::= "JOIN " [TableName] " AND " [TableName] " ON " [AttributeName] " AND " [AttributeName]
    private boolean tryJoin() {

    }

    // [PlainText] ::= [Letter] | [Digit] | [PlainText] [Letter] | [PlainText] [Digit]
    private boolean tryPlainText() {
        char c = command.charAt(index);
        if (!isDigit(c) && !isLetter(c)) {
            return false;
        } else {
            int startingIndex = index;
            while (isDigit(c) || isLetter(c)) {
                index++;
                c = command.charAt(index);
            }
            current.setValue(command.substring(startingIndex, index));
        }
    }


    /*____________________HELPER FUNCTIONS BELOW________________________*/
    private void skipWhiteSpace() {
        while (Character.isWhitespace(command.charAt(index))) {
            index++;
        }
    }

    private boolean substringIsNext(String str) {
        if (command.startsWith(str, index)) {
            index += str.length();
            return true;
        } else {
            return false;
        }
    }

    private boolean isLetter(char c) {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
    }

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }
}
