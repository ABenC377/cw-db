package edu.uob;

/*
GO THROUGH AND MAKE SURE THAT IT ALLOWS FOR MULTIPLE SPACES BETWEEN WORDS.  UFF
 */



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
        // making a new child node for the potential command type
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
            // getting rid of the impossible command type node before returning
            current = current.getParent();
            current.popChild();
            return false;
        }
    }

    // <Use> ::= "USE " [DatabaseName]
    private boolean tryUse() {
        // save the index, so we can reset it before returning false
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
        if (!substringIsNext("CREATE DATABASE ")) {
            index = originalIndex;
            return false;
        }
        // Check for the database name
        current.setType(NodeType.CREATE_TABLE);
        skipWhiteSpace();
        current.addChild(new Node(NodeType.DATABASE_NAME, current));
        current = current.getLastChild();
        if (!tryPlainText()) {
            current = current.getParent();
            current.popChild();
            index = originalIndex;
            return false;
        } else {
            current = current.getParent();
            return true;
        }
    }

    // <AttributeList> ::= [AttributeName] | [AttributeName] "," <AttributeList>
    private boolean tryAttributeList() {
        int resetIndex = index;
        skipWhiteSpace();
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
        skipWhiteSpace();
        if (substringIsNext(",")) {
            skipWhiteSpace();
            if (tryAttributeList()) {
                return true;
            }
        }
        index = resetIndex;
        return false;
    }

    // [AttributeName] ::= [PlainText] | [TableName] "." [PlainText]
    private boolean tryAttributeName() {
        int resetIndex = index;
        current.addChild(current);
        current = current.getLastChild();
        // Check for at least one plain text instance.  If not there then clean up and return false
        if (!tryPlainText()) {
            current = current.getParent();
            current.popChild();
            index = resetIndex;
            return false;
        } else {
            // If there is one plain text instance, check for a full-stop and another one.  Either way return true
            resetIndex = index;
            if (substringIsNext(".")) {
                current.setType(NodeType.TABLE_NAME);
                current = current.getParent();
                current.addChild(new Node(NodeType.PLAIN_TEXT, current));
                if (tryPlainText()) {
                    current = current.getParent();
                } else {
                    current = current.getParent();
                    current.popChild();
                }
            } else {
                current.setType(NodeType.PLAIN_TEXT);
                current = current.getParent();
            }
            index = resetIndex;
            return true;
        }
    }

    // <CreateTable> ::= "CREATE TABLE " [TableName] | "CREATE TABLE " [TableName] "(" <AttributeList> ")"
    private boolean tryCreateTable() {
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

    // <Drop> ::= "DROP DATABASE " [DatabaseName] | "DROP TABLE " [TableName]
    private boolean tryDrop() {
        int resetIndex = index;
        skipWhiteSpace();
        // Check for either of the keywords
        boolean isDatabase = substringIsNext("DROP DATABASE ");
        boolean isTable = substringIsNext("DROP TABLE ");

        // If they are there, then look for the plaintext of the database/table name
        if (isDatabase || isTable) {
            skipWhiteSpace();
            current.addChild((isDatabase) ? new Node(NodeType.DATABASE_NAME, current) : new Node(NodeType.TABLE_NAME, current));
            current = current.getLastChild();
            if (tryPlainText()) {
                current = current.getParent();
                return true;
            }
            // If this has failed, then reset and return false
            current = current.getParent();
            current.popChild();
        }
        index = resetIndex;
        return false;
    }

    // <Alter> ::= "ALTER TABLE " [TableName] " " [AlterationType] " " [AttributeName]
    private boolean tryAlter() {
        int resetIndex = index;
        skipWhiteSpace();
        if (!substringIsNext("ALTER TABLE ")) {
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();
        current.addChild(new Node(NodeType.TABLE_NAME, current));
        current = current.getLastChild();
        if (!tryPlainText()) {
            current = current.getParent();
            current.popChild();
            index = resetIndex;
            return false;
        }
        current = current.getParent();
        skipWhiteSpace();
        current.addChild(new Node(NodeType.ALTERATION_TYPE, current));
        current = current.getLastChild();
        if (!previousCharacterWas(' ') || !tryAlterationType()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();
        current.addChild(new Node(NodeType.ATTRIBUTE_NAME, current));
        current = current.getLastChild();
        if (previousCharacterWas(' ') && tryAttributeName()) {
            current = current.getParent();
            return true;
        } else {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
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
        int resetIndex = index;
        skipWhiteSpace();
        if (!substringIsNext("INSERT INTO ")) {
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
        // Need to do a janky check for space because of the possibility of multiple whitespaces before the keyword
        skipWhiteSpace();
        if (!previousCharacterWas(' ') || !substringIsNext("VALUES(")) {
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();
        current.addChild(new Node(NodeType.VALUE_LIST, current));
        current = current.getLastChild();
        if (!tryValueList()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();
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
        int resetIndex = index;
        skipWhiteSpace();
        current.addChild(new Node(NodeType.VALUE, current));
        current = current.getLastChild();
        if (tryValue()) {
            current = current.getParent();
        } else {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        resetIndex = index;
        skipWhiteSpace();
        if (substringIsNext(",")) {
            skipWhiteSpace();
            if (tryValueList()) {
                return true;
            }
        }
        index = resetIndex;
        return false;
    }

    // [Value] ::= "'" [StringLiteral] | [BooleanLiteral] | [FloatLiteral] | [IntegerLiteral] | "NULL"
    // I have changed the way the grammar is described for the first of these, as it boils down to the same but is
    // easier to implement
    private boolean tryValue() {
        int resetIndex = index;
        if (substringIsNext("NULL")) {
            current.setValue("NULL");
            return true;
        }
        if (substringIsNext("'")) {
            current.addChild(new Node(NodeType.STRING_LITERAL, current));
            current = current.getLastChild();
            if (tryStringLiteral()) {
                current = current.getParent();
                return true;
            } else {
                current = current.getParent();
                current.clearChildren();
                index = resetIndex;
                return false;
            }
        }
        current.addChild(new Node(current));
        current = current.getLastChild();
        if (tryBooleanLiteral()) {
            current.setType(NodeType.BOOLEAN_LITERAL);
            current = current.getParent();
            return true;
        } else if (tryFloatLiteral()) { // It is important that float is before integer here, as tryInteger() would
                                        // return true for a float, but tryFloat() would not return true for an int
            current.setType(NodeType.FLOAT_LITERAL);
            current = current.getParent();
            return true;
        } else if (tryIntegerLiteral()) {
            current.setType(NodeType.INTEGER_LITERAL);
            current = current.getParent();
            return true;
        }
        current = current.getParent();
        current.clearChildren();
        index = resetIndex;
        return false;
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
        if (substringIsNext("+")) {
            index++;
        } else if (substringIsNext("-")) {
            index++;
            value.append("-");
        }
        char c = command.charAt(index);
        if (!isDigit(c)) {
            index = resetIndex;
            return false;
        }
        while (isDigit(c)) {
            value.append(c);
            index++;
            c = command.charAt(index);
        }
        if (!substringIsNext(".")) {
            index = resetIndex;
            return false;
        }
        value.append('.');
        c = command.charAt(index);
        if (!isDigit(c)) {
            index = resetIndex;
            return false;
        }
        while (isDigit(c)) {
            value.append(c);
            index++;
            c = command.charAt(index);
        }
        current.setValue(value.toString());
        return true;
    }

    // [IntegerLiteral] ::= [DigitSequence] | "-" [DigitSequence] | "+" [DigitSequence]
    private boolean tryIntegerLiteral() {
        int resetIndex = index;
        StringBuilder value = new StringBuilder();
        if (substringIsNext("+")) {
            index++;
        } else if (substringIsNext("-")) {
            index++;
            value.append("-");
        }
        char c = command.charAt(index);
        if (!isDigit(c)) {
            index = resetIndex;
            return false;
        }
        while (isDigit(c)) {
            value.append(c);
            index++;
            c = command.charAt(index);
        }
        current.setValue(value.toString());
        return true;
    }

    // [StringLiteral]   ::=  "'" | [CharLiteral] [StringLiteral]
    private boolean tryStringLiteral() {
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
        return (isDigit(c) || isLetter(c) || acceptedChars.contains(String.valueOf(c)));
    }

    // <Select> ::= "SELECT " <WildAttribList> " FROM " [TableName] | "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
    private boolean trySelect() {
        int resetIndex = index;
        skipWhiteSpace();
        if (!substringIsNext("SELECT ")) {
            return false;
        }
        skipWhiteSpace();
        current.addChild(new Node(NodeType.WILD_ATTRIBUTE_LIST, current));
        current = current.getLastChild();
        if (!tryWildAttributeList()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();
        skipWhiteSpace();
        if (!previousCharacterWas(' ') || !substringIsNext("FROM ")) {
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current.addChild(new Node(NodeType.TABLE_NAME, current));
        current = current.getLastChild();
        if (!tryPlainText()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();

        // Check for an optional WHERE statement
        resetIndex = index;
        skipWhiteSpace();
        current.addChild(new Node(NodeType.CONDITION, current));
        current = current.getLastChild();
        if (previousCharacterWas(' ') && substringIsNext("WHERE ") && tryConditionR()) {
            current = current.getParent();
        } else {
            current = current.getParent();
            current.popChild();
            index = resetIndex;
        }
        return true;
    }

    // <Condition> ::= "(" <Condition> [BoolOperator] <Condition> ")" | <Condition> [BoolOperator] <Condition> | "(" [AttributeName] [Comparator] [Value] ")" | [AttributeName] [Comparator] [Value]
    // The second option for <condition> is susceptible to a left-handed recursion infinite loop.
    // Therefore, I have two tryCondition methods: tryConditionR() (which allows all four types of <condition>;
    // and tryConditionNR() (which allows only the later two types of <condition>, and thus avoids the risk of infinite
    // loops)
    private boolean tryConditionR() {
        int resetIndex = index;
        skipWhiteSpace();
        boolean inBrackets = false;
        if (!substringIsNext("(")) {
            inBrackets = true;
            index++;
        }
        skipWhiteSpace();
        current.addChild(new Node(current));
        current = current.getLastChild();
        if (tryConditionNR()) {
            current.setType(NodeType.CONDITION);
            current = current.getParent();
            skipWhiteSpace();
            current.addChild(new Node(NodeType.BOOLEAN_OPERATOR, current));
            current = current.getLastChild();
            if (!tryBoolOperator()) {
                current = current.getParent();
                current.clearChildren();
                index = resetIndex;
                return false;
            }
            current = current.getParent();
            skipWhiteSpace();
            current.addChild(new Node(NodeType.CONDITION, current));
            current = current.getLastChild();
            if (!tryConditionR()) {
                current = current.getParent();
                current.clearChildren();
                index = resetIndex;
                return false;
            }
            current = current.getParent();
            return true;
        } else if (tryConditionNR()) { // !!!This is the same as tryConditionNR()!!!
            if (inBrackets && !substringIsNext(")")) {
                current.clearChildren();
                index = resetIndex;
                return false;
            } else {
                return true;
            }
        } else {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
    }

    private boolean tryConditionNR() {
        int resetIndex = index;
        skipWhiteSpace();
        boolean inBrackets = false;
        if (!substringIsNext("(")) {
            inBrackets = true;
            index++;
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
        current.addChild(new Node(NodeType.COMPARATOR, current));
        current = current.getLastChild();
        if (!tryComparator()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();
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
        if (!previousCharacterWas(' ') || !substringIsNext("SET ")) {
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        skipWhiteSpace();
        current.addChild(new Node(NodeType.NAME_VALUE_LIST, current));
        current = current.getLastChild();
        if (!tryNameValueList()) {
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
        if (!tryConditionR()) {
            current = current.getParent();
            current.clearChildren();
            index = resetIndex;
            return false;
        }
        current = current.getParent();
        return true;
    }

    // <NameValueList> ::= <NameValuePair> | <NameValuePair> "," <NameValueList>
    private boolean tryNameValueList() {
        int resetIndex = index;
        skipWhiteSpace();

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
}
