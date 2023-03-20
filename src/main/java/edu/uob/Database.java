package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class Database {
    private final ArrayList<Table> tables;
    private String databaseName;
    private Metadata metadata;
    
    public Database() {
        tables = new ArrayList<>();
        databaseName = "";
    }
    
    public void clearDatabase() {
        tables.clear();
        databaseName = "";
    }
    
    public void loadDatabase(Path path) throws IOException {
        if (Files.exists(path)) {
            databaseName = path.getRoot().toString();
            tables.clear();
            for (File table : path.toFile().listFiles()) {
                if (table == null) {
                    throw new IOException("[ERROR] - internal database error " +
                        "(table has a null pointer)");
                } else {
                    if (!table.getName().equals("meta")) {
                        addTable(table);
                    }
                }
            }
        } else {
            throw new IOException("[ERROR] - cannot open database at " +
                path + " as one does not exist");
        }
    }
    
    public void createDatabase(Path path) throws IOException {
        if (Files.exists(path)) {
            throw new IOException("[ERROR] - cannot open database at " +
                path + " as one already exist");
        } else {
            // Create the database directory
            try {
                Files.createDirectories(path);
            } catch (IOException err) {
                throw new IOException("[ERROR] - cannot open new database " +
                    "directory at " + path);
            }
            
            // Create the metadata file in the directory
            metadata = new Metadata(path);
        }
    }
    
    public void addTable(String tableName) throws IOException {
        tables.add(new Table(tableName));
    }
    
    public void addTable(String name,
                         String[] attributes) throws IOException {
        tables.add(new Table(name, attributes));
    }
    
    private void addTable(File table) throws IOException {
        tables.add(new Table(table));
    }
    
    public String getDatabaseName() {
        return databaseName;
    }
    
    public void dropTable(String tableName) throws IOException {
        boolean tableExists = false;
        for (Table table : tables) {
            if (table.getName().equals(tableName)) {
                tables.remove(table);
                tableExists = true;
            }
        }
        if (!tableExists) {
            throw new IOException("[ERROR] - could not drop table " + tableName
                + " because it does not exist in the current database " +
                "(current database is " + databaseName + ")");
        }
    }
    
    public boolean tableExists(String tableName) {
        for (Table table : tables) {
            if (table.getName().equals(tableName)) {
                return true;
            }
        }
        return false;
    }
    
    public void addAttributeToTable(String tableName,
                                    Node attributeNode) throws IOException {
        Table toAlter = getTable(tableName);
        toAlter.addAttribute(attributeNode);
    }
    
    public void dropAttributeFromTable(String tableName,
                                       Node attributeNode) throws IOException {
        Table toAlter = getTable(tableName);
        toAlter.dropAttribute(attributeNode);
    }
    
    public void insertRow(Node insertNode) throws IOException {
        if (insertNode.getNumberChildren() != 2) {
            throw new IOException("[ERROR] - INSERT command requires a table " +
                "name and a list of values");
        }
        Table toInsert = getTable(insertNode.getChild(0).getValue());
        toInsert.insertValues(insertNode.getLastChild());
    }
    
    private Table getTable(String tableName) throws IOException {
        for (Table table : tables) {
            if (Objects.equals(table.getName(), tableName)) {
                return table;
            }
        }
        throw new IOException("[ERROR] - table of name " + tableName + " does" +
            " not exist in the current database");
    }
    
    public String selectValues(Node attributeList,
                               Node tableName) throws IOException {
        Table table = getTable(tableName.getValue());
        ArrayList<String> selectAttributes = new ArrayList<>();
        for (int i = 0; i < attributeList.getNumberChildren(); i++) {
            selectAttributes.add(attributeList.getChild(i).getValue());
        }
        return table.selectValues(selectAttributes);
    }
    
    public String selectValuesWhere(Node attributeList,
                                    Node tableName,
                                    Node condition) throws IOException {
        Table table = getTable(tableName.getValue());
        ArrayList<String> selectAttributes = new ArrayList<>();
        for (int i = 0; i < attributeList.getNumberChildren(); i++) {
            selectAttributes.add(attributeList.getChild(i).getValue());
        }
        return table.selectValuesWhere(selectAttributes, condition);
    }
    
    public void updateValues(Node updateNode) throws IOException {
        Table table = getTable(updateNode.getChild(0).getValue());
        Node listNode = updateNode.getChild(1);
        Node conditionNode = updateNode.getChild(2);
        table.updateTable(listNode, conditionNode);
    }

    public void deleteRows(Node deleteNode) throws IOException {
        Table table = getTable(deleteNode.getChild(0).getValue());
        table.deleteRows(deleteNode.getLastChild());
    }
    
    public String joinTables(Node joinNode) throws IOException {
        Table tableOne = getTable(joinNode.getChild(0).getValue());
        Table tableTwo = getTable(joinNode.getChild(1).getValue());
        
        if ((joinNode.getChild(2).getNumberChildren() == 2 &&
            !joinNode.getChild(2).getChild(0).getValue().equals(tableOne.getName())) ||
            (joinNode.getChild(3).getNumberChildren() == 2 &&
            !joinNode.getChild(3).getChild(0).getValue().equals(tableOne.getName()))) {
            throw new IOException("[ERROR] - cannot select an attribute from " +
                "a table different to the one specified");
        }
        String attributeOne = joinNode.getChild(2).getLastChild().getValue();
        String attributeTwo = joinNode.getChild(3).getLastChild().getValue();
        return getJoin(tableOne, tableTwo, attributeOne, attributeTwo);
    }
    
    private String getJoin(Table tableOne,
                           Table tableTwo,
                           String attributeOne,
                           String attributeTwo) {
        StringBuilder outputTable = new StringBuilder();
        // Make the attribute column
        outputTable.append("id");
        for (String attribute : tableOne.getAttributes()) {
            if (!attribute.equals("id") && !attribute.equals(attributeOne)) {
                outputTable.append("\t|\t");
                outputTable.append(tableOne.getName());
                outputTable.append(".");
                outputTable.append(attribute);
            }
        }
        for (String attribute : tableTwo.getAttributes()) {
            if (!attribute.equals("id") && !attribute.equals(attributeTwo)) {
                outputTable.append("\t|\t");
                outputTable.append(tableTwo.getName());
                outputTable.append(".");
                outputTable.append(attribute);
            }
        }
        outputTable.append(System.lineSeparator());
        
        // Now start adding rows
        int joinID = 1;
        int attributeOneIndex = tableOne.findIndexOfAttribute(attributeOne);
        int attributeTwoIndex = tableTwo.findIndexOfAttribute(attributeTwo);
        for (ArrayList<String> rowOne : tableOne.getRows()) {
            String joinValueOne = rowOne.get(attributeOneIndex);
            for (ArrayList<String> rowTwo : tableTwo.getRows()) {
                String joinValueTwo = rowTwo.get(attributeTwoIndex);
                if (joinValueOne.equals(joinValueTwo)) {
                    outputTable.append(makeJoinRow(joinID, rowOne, rowTwo,
                        attributeOneIndex, attributeTwoIndex));
                }
            }
        }
        
        return outputTable.toString();
    }
    
    private String makeJoinRow(int id,
                               ArrayList<String> rowOne,
                               ArrayList<String> rowTwo,
                               int indexOne,
                               int indexTwo) {
        StringBuilder row = new StringBuilder();
        row.append(id);
        for (int i = 1; i < rowOne.size(); i++) {
            if (i != indexOne) {
                row.append("\t|\t");
                row.append(rowOne.get(i));
            }
        }
        for (int i = 1; i < rowTwo.size(); i++) {
            if (i != indexTwo) {
                row.append("\t|\t");
                row.append(rowTwo.get(i));
            }
        }
        row.append(System.lineSeparator());
        return row.toString();
    }
    
    public void saveState() throws IOException {
        String pathName = ("databases" + File.pathSeparator + databaseName);
        File directory = new File(pathName);
        if (directory.exists()) {
            for (File table : directory.listFiles()) {
                if (table == null) {
                    throw new IOException("[ERROR] - table is null");
                }  else if (!table.delete()) {
                    throw new IOException("[ERROR] - unable to re-save table "
                        + table.getName());
                }
            }
        }
        for (Table table : tables) {
            table.saveTable(pathName);
        }
        metadata.saveMetaData();
    }
    
    // Static methods
    public static boolean exists(String databaseName) {
        Path databasePath =
            Paths.get("databases" + File.pathSeparator + databaseName);
        return Files.exists(databasePath);
    }
    
    public static void delete(String name) throws IOException {
        String pathName = "databases" + File.pathSeparator + name;
        File databaseDirectory = new File(pathName);
        
        if (databaseDirectory.exists() && databaseDirectory.isDirectory()) {
            for (File table : databaseDirectory.listFiles()) {
                if (table == null || !table.delete()) {
                    throw new IOException("[ERROR] - unable to delete table "
                        + table.getName());
                }
            }
        }
        
        if (databaseDirectory.exists() && !databaseDirectory.delete()) {
            throw new IOException("[ERROR] - unable to delete database " + name);
        }
    }
}
