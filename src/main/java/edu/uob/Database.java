package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Database {
    private final ArrayList<Table> tables;
    private String databaseName;
    
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
                addTable(table);
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
                    "directory at " + path.toString());
            }
            
            // Create the metadata file in the directory
            try {
                Files.createFile(Paths.get(path.toString() + File.separator +
                    "meta.csv"));
            } catch (IOException err) {
                throw new IOException("[ERROR] - unable to make metadata file" +
                    " in new database directory");
            }
        }
    }
    
    public void addTable(String tableName) throws IOException {
        tables.add(new Table(tableName));
    }
    
    public void addTable(String name, String[] attributes) throws IOException {
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
            if (table.getName() == tableName) {
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
        boolean tableExists = false;
        for (Table table : tables) {
            if (table.getName() == tableName) {
                tableExists = true;
            }
        }
        return tableExists;
    }
    
    public void addAttributeToTable(String tableName, Node attributeNode) throws IOException {
        Table toAlter = getTable(tableName);
        toAlter.addAttribute(attributeNode);
    }
    
    public void dropAttributeFromTable(String tableName, Node attributeNode) throws IOException {
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
            if (table.getName() == tableName) {
                return table;
            }
        }
        throw new IOException("[ERROR] - table of name " + tableName + " does" +
            " not exist in the current database");
    }
    
    // Static methods
    public static boolean exists(String databaseName) {
        Path databasePath =
            Paths.get("databases" + File.separator + databaseName);
        return Files.exists(databasePath);
    }
    
    public static void delete(String databaseName) throws IOException {
        File databaseDirectory = new File("databases" + File.separator + databaseName);
        if (databaseDirectory.exists() && databaseDirectory.isDirectory()) {
            for (File table : databaseDirectory.listFiles())
                if (!table.delete()) {
                    throw new IOException("[ERROR] - unable to delete table " + table.toString());
                };
        }
        if (databaseDirectory.exists() && !databaseDirectory.delete()) {
            throw new IOException("[ERROR] - unable to delete database " + databaseName);
        }
    
        /* TODO ---- Delete from metadata! */
    }
}
