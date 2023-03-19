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
    
    // For creating a table with a table name and a list of attributes
    public Table(String tableName,
                 String[] attributes) throws IOException {
        this.tableName = tableName;
        this.attributeNames = new ArrayList<>();
        attributeNames.add("id");
        attributeNames.addAll(Arrays.asList(attributes));
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
                "empty");
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
    
    private int findIndexOfAttribute(String attributeName) {
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
            newRow.add(valuesList.getChild(i).getValue());
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
            output.append("\t|\t");
        }
        output.append(System.lineSeparator());
        
        for (ArrayList<String> row : rows) {
            for (int index : attributeIndexes) {
                output.append(row.get(index));
                output.append("\t|\t");
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
            output.append("\t|\t");
        }
        output.append(System.lineSeparator());
    
        for (ArrayList<String> row : rows) {
            if (passesCondition(row, condition)) {
                for (int index : attributeIndexes) {
                    output.append(row.get(index));
                    output.append("\t|\t");
                }
                output.append(System.lineSeparator());
            }
        }
    
        return output.toString();
    }
    
    private boolean passesCondition(ArrayList<String> row,
                                    Node conditionNode) throws IOException {
        // check
        if (conditionNode.getNumberChildren() != 3) {
            throw new IOException("[ERROR] - incorrectly formed condition " +
                "statement");
        }
        
        
    }
}

