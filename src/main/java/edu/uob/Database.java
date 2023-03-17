package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Database {
    private final ArrayList<Table> tables;
    private String databaseName;
    
    public Database() {
        tables = new ArrayList<>();
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
            try {
                Files.createDirectories(path);
            } catch (IOException err) {
                throw new IOException("[ERROR] cannot open new database directory at " + path.toString());
            }
        }
    }
    
    public void addTable(String tableName) throws IOException {
        tables.add(new Table(tableName));
    }
    
    public void addTable(String[] values) throws IOException {
        tables.add(new Table(values));
    }
    
    private void addTable(File table) throws IOException {
        tables.add(new Table(table));
    }
    
    public String getDatabaseName() {
        return databaseName;
    }
}
