package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Table {
    private String tableName;
    private ArrayList<String> attributeNames;
    private ArrayList<ArrayList<String>> rows;
    private int primaryKeyValue = 1;

    // For creating a table with just a table name
    public Table(String name) throws IOException {
        this.tableName = name;
        this.attributeNames = new ArrayList<>();
        attributeNames.add("id");
        this.rows = new ArrayList<>();
    }
    

    // For populating a database with tables when loading a database from files
    public Table(File inputFile) throws IOException {
        // HANDLE META DATA FILE TO GET PRIMARY KEY VALUE
        
        
        // Open the file
        FileReader reader;
        try {
            reader = new FileReader(inputFile);
        } catch (FileNotFoundException e) {
            throw new IOException("[ERROR] - could not open " + inputFile.getName());
        }

        BufferedReader bReader = new BufferedReader(reader);
        String firstRow = bReader.readLine();

        // Populate the columns ArrayList with the elements of the first row
        if (firstRow != null) {
            this.attributeNames = new ArrayList<>(Arrays.asList(firstRow.split("\t")));
        } else {
            throw new IOException("[ERROR] - Cannot open table file that is " +
                "empty at " + inputFile.getName());
        }

        // Populate the rows while buffer reader still pumping out lines
        String current = null;
        rows = new ArrayList<>();
        boolean repeat = true;
        while (repeat) {
            try {
                current = bReader.readLine();
            } catch (IOException err) {
                repeat = false;
            }
            rows.add(new ArrayList<>(Arrays.asList(current.split("\t"))));
        }
        
        // Get the name of the file
        this.tableName = inputFile.getName();

        // Close things up like a responsible programmer
        try {
            bReader.close();
        } catch (IOException err) {
            throw err;
        }
    }
    
    public void addAttribute(Node attributeNode) throws IOException {
        if (attributeNode.getType() != NodeType.ATTRIBUTE_NAME) {
            throw new IOException("[ERROR] - invalid attribute name");
        }
        
        if (attributeNode.getChild(0).getType() == NodeType.TABLE_NAME) {
            if (attributeNode.getChild(0).getValue() != tableName) {
                throw new IOException("[ERROR] - attribute's table name must " +
                    "be the same as the name of the table that it is being " +
                    "added to");
            }
        }
        
        if (findIndexOfAttribute(attributeNode.getLastChild().getValue()) != -1) {
            throw new IOException("[ERROR] - Cannot add attribute to table, " +
                "as attribute is already present in this table");
        }
        
        attributeNames.add(attributeNode.getLastChild().getValue());
        for (ArrayList<String> row : rows) {
            row.add("");
        }
    }
    
    public void dropAttribute(Node attributeNode) throws IOException {
        // Check that there is an attribute
        if (attributeNode.getType() != NodeType.ATTRIBUTE_NAME) {
            throw new IOException("[ERROR] - invalid attribute name");
        }
    
        // Check it doesn't relate to a different table
        if (attributeNode.getChild(0).getType() == NodeType.TABLE_NAME) {
            if (attributeNode.getChild(0).getValue() != tableName) {
                throw new IOException("[ERROR] - attribute's table name must " +
                    "be the same as the name of the table that it is being " +
                    "added to");
            }
        }
        
        // Check attribute exists in this table
        String attributeName = attributeNode.getLastChild().getValue();
        int attributeIndex = findIndexOfAttribute(attributeName);
        if (attributeIndex == -1) {
            throw new IOException("[ERROR] - the attribute being dropped is " +
                "not present in this table");
        }
        
        // Now actually remove the attribute
        attributeNames.remove(attributeIndex);
        for (ArrayList<String> row : rows) {
            row.remove(attributeIndex);
        }
    }
    
    public String getName() {
        return tableName;
    }
    
    public int findIndexOfAttribute(String attributeName) {
        int index = 0;
        for (String currentName : attributeNames) {
            if (currentName.toLowerCase() == attributeName.toLowerCase()) {
                return index;
            }
            index++;
        }
        return -1;
    }
    
    public void insertValues(Node valuesList) throws IOException {
        if (valuesList.getNumberChildren() != (attributeNames.size() - 1)) {
            throw new IOException("[ERROR] - INSERT command must provide a " +
                "value for every attribute in the table");
        }
        
        // Add the primary key
        ArrayList<String> newRow = new ArrayList<>();
        newRow.add(String.valueOf(primaryKeyValue));
        primaryKeyValue++;
        
        // add the values
        for (int i = 0; i < valuesList.getNumberChildren(); i++) {
            newRow.add(valuesList.getChild(i).getChild(0).getValue());
        }
        
        // Add the row to the db data structure
        rows.add(newRow);
    }
    
    public String selectValues(ArrayList<String> selectAttributes) throws IOException {
        ArrayList<Integer> attributeIndexes = new ArrayList<>();
        for (String attributeName : selectAttributes) {
            attributeIndexes.add(findIndexOfAttribute(attributeName));
        }
        
        StringBuilder output = new StringBuilder();
        for (int index : attributeIndexes) {
            output.append(attributeNames.get(index));
            output.append("\t");
        }
        output.append(System.lineSeparator());
        
        for (ArrayList<String> row : rows) {
            for (int index : attributeIndexes) {
                output.append(row.get(index));
                output.append("\t");
            }
            output.append(System.lineSeparator());
        }
        
        return output.toString();
    }
    
    public String selectValues() throws IOException {
    
        StringBuilder output = new StringBuilder();
        for (String attributeName : attributeNames) {
            output.append(attributeName);
            output.append("\t");
        }
        output.append(System.lineSeparator());
    
        for (ArrayList<String> row : rows) {
            for (String value : row) {
                output.append(value);
                output.append("\t");
            }
            output.append(System.lineSeparator());
        }
    
        return output.toString();
    }
    
    public String selectValuesWhere(ArrayList<String> selectAttributes,
                                    Node condition) throws IOException {
        ArrayList<Integer> attributeIndexes = new ArrayList<>();
        for (String attributeName : selectAttributes) {
            attributeIndexes.add(findIndexOfAttribute(attributeName));
        }
    
        StringBuilder output = new StringBuilder();
        for (int index : attributeIndexes) {
            output.append(attributeNames.get(index));
            output.append("\t");
        }
        output.append(System.lineSeparator());
    
        for (ArrayList<String> row : rows) {
            if (passesCondition(row, condition)) {
                for (int index : attributeIndexes) {
                    output.append(row.get(index));
                    output.append("\t");
                }
                output.append(System.lineSeparator());
            }
        }
    
        return output.toString();
    }
    
    public String selectValuesWhere(Node condition) throws IOException {
        StringBuilder output = new StringBuilder();
        for (String attributeName : attributeNames) {
            output.append(attributeName);
            output.append("\t");
        }
        output.append(System.lineSeparator());
        
        for (ArrayList<String> row : rows) {
            if (passesCondition(row, condition)) {
                for (String value : row) {
                    output.append(value);
                    output.append("\t");
                }
                output.append(System.lineSeparator());
            }
        }
        
        return output.toString();
    }
    
    private boolean passesCondition(ArrayList<String> row,
                                    Node conditionNode) throws IOException {
        // Check condition node is correctly formed
        if (conditionNode.getNumberChildren() == 1 &&
            conditionNode.getLastChild().getType() == NodeType.CONDITION) {
            return passesCondition(row, conditionNode.getLastChild());
        } else if (conditionNode.getNumberChildren() != 3) {
            throw new IOException("[ERROR] - incorrectly formed condition " +
                "statement");
        }
        
        // Recursively descend the condition tree
        if (conditionNode.getChild(0).getType() == NodeType.ATTRIBUTE_NAME) {
            return solveComparison(row, conditionNode);
        } else if (conditionNode.getChild(1).getValue() == "AND") {
            return (passesCondition(row, conditionNode.getChild(0)) &&
                    passesCondition(row, conditionNode.getChild(2)));
        } else if (conditionNode.getChild(1).getValue() == "OR") {
            return (passesCondition(row, conditionNode.getChild(0)) ||
                    passesCondition(row, conditionNode.getChild(2)));
        } else {
            throw new IOException("[ERROR] - incorrectly formed condition " +
                "statement");
        }
        
    }
    
    private boolean solveComparison(ArrayList<String> row,
                                    Node conditionNode) throws IOException {
        // Check for invalid condition node (we have already checked child()
        // before this is called)
        if (conditionNode.getChild(1).getType() != NodeType.COMPARATOR ||
            conditionNode.getChild(2).getType() != NodeType.VALUE) {
            throw new IOException("[ERROR] - incorrectly formed condition " +
                "statement");
        } else if (conditionNode.getChild(0).getNumberChildren() == 2 &&
            conditionNode.getChild(0).getChild(0).getValue() != tableName) {
            throw new IOException("[ERROR] - cannot access attribute from " +
                "different table");
        }
        
        // Get the string from the table that we are comparing, and the
        // result of the comparison
        String argument = row.get(findIndexOfAttribute(conditionNode
                                .getChild(0).getLastChild().getValue()));
        int comparisonResult = argument.compareTo(conditionNode
                .getChild(2).getLastChild().getValue());
        // Now a simple switch statement on the basis of the value of the
        // comparator node
        switch(conditionNode.getChild(1).getValue()) {
            case "<" -> {
                return (comparisonResult < 0);
            }
            case ">" -> {
                return (comparisonResult > 0);
            }
            case "<=" -> {
                return (comparisonResult <= 0);
            }
            case ">=" -> {
                return (comparisonResult >= 0);
            }
            case "==" -> {
                return (comparisonResult == 0);
            }
            case "!=" -> {
                return (comparisonResult != 0);
            }
            case "LIKE" -> {
                return (argument.contains(conditionNode.getChild(2)
                        .getLastChild().getValue()));
            }
            default -> {
                return false;
            }
        }
    }
    
    public void updateTable(Node listNode,
                            Node conditionNode) throws IOException {
        // Set up array lists of attribute indices and values so we can
        // easily iterate over them later on
        ArrayList<Integer> attributeIndexes = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        
        for (int i = 0; i < listNode.getNumberChildren(); i++) {
            // Check for obvious errors in the query
            if (findIndexOfAttribute(listNode.getChild(i).getChild(0)
                .getLastChild().getValue()) == -1) {
                throw new IOException("[ERROR] - cannot update attribute that" +
                    " does not exist in the listed table");
            } else if (listNode.getChild(i).getChild(0)
                       .getNumberChildren() == 2 &&
                       listNode.getChild(i).getChild(0).getChild(0)
                       .getValue() != tableName) {
                throw new IOException("[ERROR] - cannot update attribute in " +
                    "table other than the one listed");
            }
            // Populate the array lists
            attributeIndexes.add(findIndexOfAttribute(listNode.getChild(i)
                .getChild(0).getLastChild().getValue()));
            values.add(listNode.getChild(i).getChild(1).getValue());
        }
        
        // Make the changes to each row if the row passes the condition
        for (ArrayList<String> row : rows) {
            if (passesCondition(row, conditionNode)) {
                for (int i = 0; i < attributeIndexes.size(); i++) {
                    row.set(attributeIndexes.get(i), values.get(i));
                }
            }
        }
    }
    
    public void deleteRows(Node conditionNode) throws IOException {
        ArrayList<ArrayList<String>> rowsToDelete = new ArrayList<>();
        for (ArrayList<String> row : rows) {
            if (passesCondition(row, conditionNode)) {
                rowsToDelete.add(row);
            }
        }
        
        for (ArrayList<String> rowToDelete : rowsToDelete) {
            rows.remove(rowToDelete);
        }
    }
    
    public ArrayList<String> getAttributes() {
        return attributeNames;
    }
    
    public ArrayList<ArrayList<String>> getRows() {
        return rows;
    }
    
    public void saveTable(String databasePathName) throws IOException {
        String tablePath = databasePathName + File.separator + tableName +
            ".tab";
        File tableFile =
            new File(tablePath);
        try {
            tableFile.createNewFile();
        } catch (IOException err) {
            throw new IOException("[ERROR] - cannot save table file to the " +
                "database directory");
        }
        
        // Bosh in the attribute names
        FileWriter writer = new FileWriter(tableFile);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        for (String attribute : attributeNames) {
            bufferedWriter.write(attribute + "\t");
        }
        bufferedWriter.newLine();
        
        // Bang in the rows
        for (ArrayList<String> row : rows) {
            for (String value : row) {
                bufferedWriter.write(value + "\t");
            }
            bufferedWriter.newLine();
        }
        
        // Close things off
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}

