package edu.uob;

/*
GO THROUGH AND MAKE SURE THAT IT ALLOWS FOR MULTIPLE SPACES BETWEEN WORDS.  UFF
 */


import java.util.function.Supplier;

public class Parser {
    private Node current;
    private final String command;
    int index;

    public Parser(Node root, String command) {
        this.command = command;
        this.current = root;
        this.index = 0;
    }

    public boolean populateTree() {
        // making a new child node for the potential command type
        current.addChild(new Node(current));
        current = current.getLastChild();

        // checking that there is a valid command type
        if (tryUse()) {
            current.setType(NodeType.USE);
            current = current.getParent();
            return true;
        } else if (tryCreate()) {
            current.setType(NodeType.CREATE);
            current = current.getParent();
            return true;
        } else if (tryDrop()) {
            current.setType(NodeType.DROP);
            current = current.getParent();
            return true;
        } else if (tryAlter()) {
            current.setType(NodeType.ALTER);
            current = current.getParent();
            return true;
        } else if (tryInsert()) {
            current.setType(NodeType.INSERT);
            current = current.getParent();
            return true;
        } else if (trySelect()) {
            current.setType(NodeType.SELECT);
            current = current.getParent();
            return true;
        } else if (tryUpdate()) {
            current.setType(NodeType.UPDATE);
            current = current.getParent();
            return true;
        } else if (tryDelete()) {
            current.setType(NodeType.DELETE);
            current = current.getParent();
            return true;
        } else if (tryJoin()) {
            current.setType(NodeType.JOIN);
            current = current.getParent();
            return true;
        } else {
            // getting rid of the impossible command type node before returning
            current = current.getParent();
            current.clearChildren();
            return false;
        }
    }

    // <Use> ::= "USE " [DatabaseName]
    private boolean tryUse() {
        // save the index, so we can reset it before returning false
        int resetIndex = index;

        // check for the keyword
        skipWhiteSpace();
        if (!substringIsNext("USE ")) {
            index = resetIndex;
            return false;
        }

        // set up and move to a new child node for the potential database name
        current.setType(NodeType.USE);
        skipWhiteSpace();
        return checkForGrammar(NodeType.DATABASE_NAME, this::tryPlainText, resetIndex, true);
    }

    // <Create> ::= <CreateDatabase> | <CreateTable>
    private boolean tryCreate() {
        return (tryCreateDatabase() || tryCreateTable());
    }

    // <CreateDatabase> ::= "CREATE DATABASE " [DatabaseName]
    private boolean tryCreateDatabase() {
        // Check for the key word sequence
        int resetIndex = index;
        skipWhiteSpace();
        if (!substringIsNext("CREATE ")) {
            index = resetIndex;
            return false;
        }
        if (!substringIsNext("DATABASE ")) {
            index = resetIndex;
            return false;
        }
        current.setType(NodeType.CREATE_TABLE);
        skipWhiteSpace();

        // check for the database name
        return checkForGrammar(NodeType.DATABASE_NAME, this::tryPlainText, resetIndex, true);
    }

    // <AttributeList> ::= [AttributeName] | [AttributeName] "," <AttributeList>
    private boolean tryAttributeList() {
        // check for at least one Attribute name
        int resetIndex = index;
        skipWhiteSpace();
        if (!checkForGrammar(NodeType.ATTRIBUTE_NAME, this::tryAttributeName, resetIndex, true)) {
            return false;
        }

        // Now we have that, keep on checking for additional attribute names
        resetIndex = index;
        skipWhiteSpace();
        while (substringIsNext(",")) {
            skipWhiteSpace();
            if (!checkForGrammar(NodeType.ATTRIBUTE_NAME, this::tryAttributeName, resetIndex, true)) {
                return true;
            }
            resetIndex = index;
            skipWhiteSpace();
        }

        // return true regardless
        index = resetIndex;
        return true;
    }

    // [AttributeName] ::= [PlainText] | [TableName] "." [PlainText]
    private boolean tryAttributeName() {
        // Check for a first bit of plain text (this could be the sole plain text or the table name, we will
        // deal with the distinction later
        int resetIndex = index;
        if (!checkForGrammar(NodeType.PLAIN_TEXT, this::tryPlainText, resetIndex, true)) {
            return false;
        }

        // If there is a full stop and another plain text we need to change the type of teh first child
        // Otherwise, we just return the current 1-child AttributeName node (hence the false passed into the
        // completeReset parameter in checkForGrammar() below)
        resetIndex = index;
        if (substringIsNext(".") && checkForGrammar(NodeType.PLAIN_TEXT, this::tryPlainText, resetIndex, false)) {
            current.getChild(0).setType(NodeType.TABLE_NAME);
        }

        return true;
    }

    // <CreateTable> ::= "CREATE TABLE " [TableName] | "CREATE TABLE " [TableName] "(" <AttributeList> ")"
    private boolean tryCreateTable() {
        // Check for the key word sequence
        int resetIndex = index;
        skipWhiteSpace();
        if (!substringIsNext("CREATE ")) {
            index = resetIndex;
            return false;
        }
        if (!substringIsNext("TABLE ")) {
            index = resetIndex;
            return false;
        }
        current.setType(NodeType.CREATE_TABLE);
        skipWhiteSpace();

        // Check for the table name
        if (!checkForGrammar(NodeType.TABLE_NAME, this::tryPlainText, resetIndex, true)) {
            return false;
        }

        // Check for a possible attribute list.  This requires: 1) checking for opening parenthesis;
        // 2) checking for an attribute list; and 3) checking for a closing parenthesis
        resetIndex = index;
        if (!substringIsNext("(")) {
            return true;
        }
        skipWhiteSpace();
        if (!checkForGrammar(NodeType.ATTRIBUTE_LIST, this::tryAttributeList, resetIndex, false)) {
            return true;
        }
        skipWhiteSpace();
        if (!substringIsNext(")")) {
            index = resetIndex;
            current.popChild();
        }
        return true;
    }

    // <Drop> ::= "DROP DATABASE " [DatabaseName] | "DROP TABLE " [TableName]
    private boolean tryDrop() {
        // Check for the initial DROP keyword
        int resetIndex = index;
        skipWhiteSpace();
        if (!substringIsNext("DROP ")) {
            index = resetIndex;
            return false;
        }

        // Then depending on the next keyword, check for either a database name or a table name
        skipWhiteSpace();
        if (substringIsNext("DATABASE ")) {
            skipWhiteSpace();
            return (checkForGrammar(NodeType.DATABASE_NAME, this::tryPlainText, resetIndex, true));
        } else if (substringIsNext("TABLE ")) {
            skipWhiteSpace();
            return (checkForGrammar(NodeType.TABLE_NAME, this::tryPlainText, resetIndex, true));
        } else {
            index = resetIndex;
            return false;
        }
    }

    // <Alter> ::= "ALTER TABLE " [TableName] " " [AlterationType] " " [AttributeName]
    private boolean tryAlter() {
        // Check for the keywords
        int resetIndex = index;
        skipWhiteSpace();
        if (!substringIsNext("ALTER ")) {
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();
        if (!substringIsNext("TABLE ")) {
            index = resetIndex;
            return false;
        }
        // Check for a table name
        skipWhiteSpace();
        if (!checkForGrammar(NodeType.TABLE_NAME, this::tryPlainText, resetIndex, true)) {
            return false;
        }

        // Check for a space and the alteration type
        skipWhiteSpace();
        if (!previousCharacterWas(' ') || !checkForGrammar(NodeType.ALTERATION_TYPE, this::tryAlterationType, resetIndex, true)) {
            return false;
        }

        // Check for a space and the attribute name
        skipWhiteSpace();
        if (!previousCharacterWas(' ')) {
            return false;
        } else {
            return (checkForGrammar(NodeType.ATTRIBUTE_NAME, this::tryAttributeName, resetIndex, true));
        }
    }

    // [AlterationType] ::= "ADD" | "DROP"
    private boolean tryAlterationType() {
        if (substringIsNext("ADD")) {
            current.setValue("ADD");
            return true;
        } else if (substringIsNext("DROP")) {
            current.setValue("DROP");
            return true;
        } else {
            return false;
        }
    }

    // <Insert> ::= "INSERT INTO " [TableName] " VALUES(" <ValueList> ")"
    private boolean tryInsert() {
        // Check for keywords
        int resetIndex = index;
        skipWhiteSpace();
        if (!substringIsNext("INSERT ")) {
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();
        if (!substringIsNext("INTO ")) {
            index = resetIndex;
            return false;
        }

        // Check for a table name
        skipWhiteSpace();
        if (!checkForGrammar(NodeType.TABLE_NAME, this::tryPlainText, resetIndex, true)) {
            return false;
        }

        // Need to do a janky check for space because of the possibility of multiple whitespaces before the keyword
        skipWhiteSpace();
        if (!previousCharacterWas(' ') || !substringIsNext("VALUES(")) {
            current.clearChildren();
            index = resetIndex;
            return false;
        }

        // Check for a values list
        skipWhiteSpace();
        if (!checkForGrammar(NodeType.VALUE_LIST, this::tryValueList, resetIndex, true)) {
            return false;
        }

        // Finally, check for the closing parenthesis
        skipWhiteSpace();
        if (!substringIsNext(")")) {
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        return true;
    }

    // <ValueList> ::= [Value] | [Value] "," <ValueList>
    private boolean tryValueList() {
        // Check for an opening value
        int resetIndex = index;
        skipWhiteSpace();
        if (!checkForGrammar(NodeType.VALUE, this::tryValue, resetIndex, true)) {
            return false;
        }

        // Then do a while loop to check for additional values
        // -- careful to only reset the most resent child on a failure
        resetIndex = index;
        skipWhiteSpace();
        while (substringIsNext(",")) {
            if (!checkForGrammar(NodeType.VALUE, this::tryValue, resetIndex, false)) {
                return true;
            }
            resetIndex = index;
            skipWhiteSpace();
        }
        return true;
    }

    // [Value] ::= "'" [StringLiteral] | [BooleanLiteral] | [FloatLiteral] | [IntegerLiteral] | "NULL"
    // I have changed the way the grammar is described for the first of these, as it boils down to the same but is
    // easier to implement
    private boolean tryValue() {
        // Start by checking for a keyword to flag NULL or string literal
        // Then run through checking for the other types of value
        int resetIndex = index;
        if (substringIsNext("NULL")) {
            current.setValue("NULL");
            return true;
        } else if (substringIsNext("'")) {
            return checkForGrammar(NodeType.STRING_LITERAL, this::tryStringLiteral, resetIndex, true);
        } else {
            return (checkForGrammar(NodeType.BOOLEAN_LITERAL, this::tryBooleanLiteral, resetIndex, true) ||
                    checkForGrammar(NodeType.FLOAT_LITERAL, this::tryFloatLiteral, resetIndex, true) ||
                    checkForGrammar(NodeType.INTEGER_LITERAL, this::tryIntegerLiteral, resetIndex, true));
        }
    }

    // [BooleanLiteral] ::= "TRUE" | "FALSE"
    private boolean tryBooleanLiteral() {
        if (substringIsNext("TRUE")) {
            current.setValue("TRUE");
            return true;
        } else if (substringIsNext("FALSE")) {
            current.setValue("FALSE");
            return true;
        } else {
            return false;
        }
    }

    // [FloatLiteral] ::= [DigitSequence] "." [DigitSequence] | "-" [DigitSequence] "." [DigitSequence] | "+" [DigitSequence] "." [DigitSequence]
    private boolean tryFloatLiteral() {
        int resetIndex = index;
        StringBuilder value = new StringBuilder();
        // handle +/- signs
        if (substringIsNext("+")) {
        } else if (substringIsNext("-")) { value.append("-"); }
        // Handle the >1 digits
        if (!isDigit(command.charAt(index))) {
            index = resetIndex;
            return false;
        }
        while (isDigit(command.charAt(index))) {
            value.append(command.charAt(index));
            index++;
        }
        // Handle the decimal point
        if (!substringIsNext(".")) {
            index = resetIndex;
            return false;
        }
        value.append('.');
        // Handle the <1 digits
        if (!isDigit(command.charAt(index))) {
            index = resetIndex;
            return false;
        }
        while (isDigit(command.charAt(index))) {
            value.append(command.charAt(index));
            index++;
        }
        // Set the value
        current.setValue(value.toString());
        return true;
    }

    // [IntegerLiteral] ::= [DigitSequence] | "-" [DigitSequence] | "+" [DigitSequence]
    private boolean tryIntegerLiteral() {
        int resetIndex = index;
        StringBuilder value = new StringBuilder();
        // handle +/- signs
        if (substringIsNext("+")) {
        } else if (substringIsNext("-")) { value.append("-"); }
        // Handle the digits
        if (!isDigit(command.charAt(index))) {
            index = resetIndex;
            return false;
        }
        while (isDigit(command.charAt(index))) {
            value.append(command.charAt(index));
            index++;
        }
        // Set the value
        current.setValue(value.toString());
        return true;
    }

    // [StringLiteral]   ::=  "'" | [CharLiteral] [StringLiteral]
    private boolean tryStringLiteral() {
        // A bit janky, but needed to start from a clean sheet when we get to a string literal
        if (previousCharacterWas('\'')) {
            current.setValue("");
        }

        if (substringIsNext("'")) {
            return true;
        } else if (isCharLiteral()) {
            tryStringLiteral();
            return true;
        } else {
            return false;
        }
    }

    private boolean isCharLiteral() {
        char c = command.charAt(index++); // !!!Check that the "++" is applied after the charAt() method!!!
        String acceptedChars = " !#$%&()*+,-./:;>=<?@[\\]^_`{}~";
        if (isDigit(c) || isLetter(c) || acceptedChars.contains(String.valueOf(c))) {
            current.setValue(current.getValue() + c);
            return true;
        } else {
            return false;
        }
    }

    // <Select> ::= "SELECT " <WildAttribList> " FROM " [TableName] | "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
    private boolean trySelect() {
        // Look for keyword
        int resetIndex = index;
        skipWhiteSpace();
        if (!substringIsNext("SELECT ")) {
            return false;
        }
        // Check for a wild attribute list
        skipWhiteSpace();
        if (!checkForGrammar(NodeType.WILD_ATTRIBUTE_LIST, this::tryWildAttributeList, resetIndex, true)) {
            return false;
        }
        // Check for the next keyword
        skipWhiteSpace();
        if (!previousCharacterWas(' ') || !substringIsNext("FROM ")) {
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        // Check for table name
        if (!checkForGrammar(NodeType.TABLE_NAME, this::tryPlainText, resetIndex, true)) {
            return false;
        }
        // Check for an optional WHERE statement
        resetIndex = index;
        skipWhiteSpace();
        if (!previousCharacterWas(' ') || !substringIsNext("WHERE ")) {
            index = resetIndex;
            return true;
        }
        // Check for condition
        skipWhiteSpace();
        if (checkForGrammar(NodeType.CONDITION, this::tryCondition, resetIndex, false)) {
            return true;
        } else {
            return true;
        }
    }

    // <Condition> ::= "(" <Condition> [BoolOperator] <Condition> ")" | <Condition> [BoolOperator] <Condition> | "(" [AttributeName] [Comparator] [Value] ")" | [AttributeName] [Comparator] [Value]
    /*
    <condition> is the most difficult bit of the grammar for two reasons: one - the optional brackets; and
    2 - the potential for infinite recursion.  Each of these reasons make the obvious solution for the other
    impractical

    I think that what I'm going to do is to look for "[AttributeName] [Comparator] [Value]" groupings linked by
    boolean operators, iteratively.  When I hit an open parenthesis I'll make a new sub node, and when I hit a
    close parenthesis I'll move up to the parent node.
    When I run out of these groupings, or I'm moved to the parent of the original condition node, I'm done.

     */
    private boolean tryCondition() {

        // Check for the first comparator group (i.e., [AttributeName] [Comparator] [Value])
        int resetIndex = index;
        if (!skipWhiteSpaceAndCheckParentheses()) {
            return false;
        } else if (!checkForGrammar(NodeType.CONDITION, this::tryConditionIndividual, resetIndex, true)) {
            return false;
        }

        // Now we know there is a valid condition, we check for further optional parts to the condition
        resetIndex = index;
        boolean tryAgain = true;
        while (tryAgain) {
            if (!skipWhiteSpaceAndCheckParentheses()) {
                tryAgain = false;
            }
            if (tryAgain && !checkForGrammar(NodeType.BOOLEAN_OPERATOR, this::tryBoolOperator, resetIndex, false)) {
                tryAgain = false;
            }
            if (tryAgain && !skipWhiteSpaceAndCheckParentheses()) {
                tryAgain = false;
            }
            if (tryAgain && !checkForGrammar(NodeType.CONDITION, this::tryConditionIndividual, resetIndex, false)) {
                current.popChild();
                tryAgain = false;
            }
            resetIndex = index;
        }
        index = resetIndex;
        return true;

    }

    private boolean tryConditionIndividual() {
        int resetIndex = index;
        if (!checkForGrammar(NodeType.ATTRIBUTE_NAME, this::tryAttributeName, resetIndex, true)) {
            return false;
        }
        skipWhiteSpace();
        if (!checkForGrammar(NodeType.COMPARATOR, this::tryComparator, resetIndex, true)) {
            return false;
        }
        skipWhiteSpace();
        return (checkForGrammar(NodeType.VALUE, this::tryValue, resetIndex, true));
    }

    // <WildAttribList> ::= <AttributeList> | "*"
    private boolean tryWildAttributeList() {
        int resetIndex = index;
        skipWhiteSpace();
        if (substringIsNext("*")) {
            current.setValue("*");
            return true;
        } else if (tryAttributeList()) {
            return true;
        } else {
            index = resetIndex;
            return false;
        }
    }

    // [BoolOperator] ::= "AND" | "OR"
    private boolean tryBoolOperator() {
        if (substringIsNext("AND")) {
            current.setValue("AND");
            return true;
        } else if (substringIsNext("OR")) {
            current.setValue("OR");
            return true;
        } else {
            return false;
        }
    }

    // [Comparator] ::= "==" | ">" | "<" | ">=" | "<=" | "!=" | " LIKE "
    private boolean tryComparator() {
        if (substringIsNext("==")) {
            current.setValue("==");
            return true;
        } else if (substringIsNext(">")) {
            current.setValue(">");
            return true;
        } else if (substringIsNext("<")) {
            current.setValue("<");
            return true;
        } else if (substringIsNext(">=")) {
            current.setValue(">=");
            return true;
        } else if (substringIsNext("<=")) {
            current.setValue("<=");
            return true;
        } else if (substringIsNext("!=")) {
            current.setValue("!=");
            return true;
        } else if (previousCharacterWas(' ') && substringIsNext("LIKE ")) {
            current.setValue("LIKE");
            return true;
        } else {
            return false;
        }
    }

    // <Update> ::= "UPDATE " [TableName] " SET " <NameValueList> " WHERE " <Condition>
    private boolean tryUpdate() {
        int resetIndex = index;
        skipWhiteSpace();
        if (!substringIsNext("UPDATE ")) {
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();
        if (!checkForGrammar(NodeType.TABLE_NAME, this::tryPlainText, resetIndex, true)) {
            return false;
        }
        skipWhiteSpace();
        if (!previousCharacterWas(' ') || !substringIsNext("SET ")) {
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();
        if (!checkForGrammar(NodeType.NAME_VALUE_LIST, this::tryNameValueList, resetIndex, true)) {
            return false;
        }
        skipWhiteSpace();
        if (!previousCharacterWas(' ') || !substringIsNext("WHERE ")) {
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();
        return checkForGrammar(NodeType.CONDITION, this::tryCondition, resetIndex, true);
    }

    // <NameValueList> ::= <NameValuePair> | <NameValuePair> "," <NameValueList>
    private boolean tryNameValueList() {
        int resetIndex = index;
        skipWhiteSpace();
        current.addChild(new Node(NodeType.NAME_VALUE_PAIR, current));
        current = current.getLastChild();
        if (!tryNameValuePair()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();

        // Check for optional additional NameValuePairs
        resetIndex = index;
        while (substringIsNext(",")) {
            current.addChild(new Node(NodeType.NAME_VALUE_PAIR, current));
            current = current.getLastChild();
            if (!tryNameValuePair()) {
                current = current.getParent();
                current.popChild();
                index = resetIndex;
            }
            current = current.getParent();
            resetIndex = index;
        }
        return true;
    }

    // <NameValuePair> ::= [AttributeName] "=" [Value]
    private boolean tryNameValuePair() {
        int resetIndex = index;
        skipWhiteSpace();
        current.addChild(new Node(NodeType.ATTRIBUTE_NAME, current));
        current = current.getLastChild();
        if (!tryAttributeName()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();
        skipWhiteSpace();
        if (!substringIsNext("=")) {
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();
        current.addChild(new Node(NodeType.VALUE, current));
        current = current.getLastChild();
        if (!tryValue()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();
        return true;
    }

    // <Delete> ::= "DELETE FROM " [TableName] " WHERE " [Condition]
    private boolean tryDelete() {
        int resetIndex = index;
        skipWhiteSpace();
        if (!substringIsNext("DELETE ")) {
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();
        if (!substringIsNext("FROM ")) {
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();
        current.addChild(new Node(NodeType.TABLE_NAME, current));
        current = current.getLastChild();
        if (!tryPlainText()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();
        skipWhiteSpace();
        if (!previousCharacterWas(' ') || !substringIsNext("WHERE ")) {
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();
        current.addChild(new Node(NodeType.CONDITION, current));
        current = current.getLastChild();
        if (tryCondition()) {
            current = current.getParent();
            return true;
        }
        current = current.getParent();
        current.clearChildren();
        index = resetIndex;
        return false;
    }

    // <Join> ::= "JOIN " [TableName] " AND " [TableName] " ON " [AttributeName] " AND " [AttributeName]
    private boolean tryJoin() {
        int resetIndex = index;
        skipWhiteSpace();
        if (!substringIsNext("JOIN ")) {
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();

        current.addChild(new Node(NodeType.TABLE_NAME, current));
        current = current.getLastChild();
        if (!tryPlainText()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();
        skipWhiteSpace();

        if (!previousCharacterWas(' ') || !substringIsNext("AND ")) {
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();

        current.addChild(new Node(NodeType.TABLE_NAME, current));
        current = current.getLastChild();
        if (!tryPlainText()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();
        skipWhiteSpace();

        if (!previousCharacterWas(' ') || substringIsNext("ON ")) {
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();

        current.addChild(new Node(NodeType.ATTRIBUTE_NAME, current));
        current = current.getLastChild();
        if (!tryAttributeName()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();
        skipWhiteSpace();

        if (!previousCharacterWas(' ') || substringIsNext("AND ")) {
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();

        current.addChild(new Node(NodeType.ATTRIBUTE_NAME, current));
        current = current.getLastChild();
        if (!tryAttributeName()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();
        return true;
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
            return true;
        }
    }


    /*____________________HELPER FUNCTIONS BELOW________________________*/

    private boolean checkForGrammar(NodeType type, Supplier<Boolean> tryFunc, int resetIndex, boolean completeReset) {
        current.addChild(new Node(type, current));
        current = current.getLastChild();
        if (tryFunc.get()) {
            current = current.getParent();
            return true;
        } else {
            current = current.getParent();
            if (completeReset) {
                current.clearChildren();
            } else {
                current.popChild();
            }
            index = resetIndex;
            return false;
        }
    }


    private void skipWhiteSpace() {
        while (Character.isWhitespace(command.charAt(index))) {
            index++;
        }
    }

    // This method is a helper for the tryCondition() method.  It handels moving up and down the
    // syntax tree according to parentheses.  It has a boolean return value to tell the caller function
    // when to bail
    private boolean skipWhiteSpaceAndCheckParentheses() {
        char c = command.charAt(index);
        while (Character.isWhitespace(c) || c == ')' || c == '(') {
            if (command.charAt(index) == '(') {
                current.addChild(new Node(NodeType.CONDITION, current));
                current = current.getLastChild();
                index++;
            } else if (command.charAt(index) == ')') {
                index++;
                if (current.getParent().getType() == NodeType.CONDITION) {
                    current = current.getParent();
                } else {
                    return false;
                }
            } else if (Character.isWhitespace(command.charAt(index))) {
                index++;
            } else {
                return true;
            }
        }
        return true;
    }

    private boolean substringIsNext(String str) {
        if (command.startsWith(str, index)) {
            index += str.length();
            return true;
        } else {
            return false;
        }
    }

    private boolean previousCharacterWas(char c) {
        // catch an out-of-index error before it happens
        if (index < 1) {
            return false;
        }

        return (command.charAt(index - 1) == c);
    }

    private boolean isLetter(char c) {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
    }

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private int findMatchingParenthesis(int start) {
        int open = 1;
        int toCheck = index;
        do {
            if (command.charAt(toCheck) == '(') {
                open++;
            } else if (command.charAt(toCheck) == ')') {
                open--;
            }
            toCheck++;
        } while (toCheck < command.length() && open > 0);
        return (toCheck >= command.length()) ? -1 : toCheck;
    }
}
