package edu.uob;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class Metadata {
    File metadataFile;
    ArrayList<String> tableNames;
    ArrayList<Integer> ids;
    
    public Metadata(String databasePath) throws IOException {
        metadataFile = new File(databasePath + File.separator +
                "meta");
        tableNames = new ArrayList<>();
        ids = new ArrayList<>();

        if (metadataFile.exists()) {
            loadFromFile(metadataFile);
        } else if (!metadataFile.createNewFile()) {
            throw new IOException("[ERROR] - unable to create metadata file " +
                "in " + databasePath);
        }
    }
    
    
    public void loadFromFile(File file) throws IOException {
        metadataFile = file;
        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String current;
        while ((current = bufferedReader.readLine()) != null) {
            ArrayList<String> table =
                new ArrayList<>(Arrays.asList(current.split(",")));
            tableNames.add(table.get(0));
            ids.add(Integer.valueOf(table.get(1)));
        }
        bufferedReader.close();
    }
    
    public void saveMetaData() throws IOException {
        FileWriter writer = new FileWriter(metadataFile);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        for (int i = 0; i < tableNames.size(); i++) {
            bufferedWriter.write(tableNames.get(i) + "," + ids.get(i));
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }
    
    public void updatePrimaryKey(String tableName, int newPrimaryKey) throws IOException {
        // Read the current csv file
        int tableIndex = getTableIndex(tableName);
        if (tableIndex != -1) {
            ids.set(tableIndex, newPrimaryKey);
        } else {
            throw new IOException("[ERROR] - cannot find table in metadata " +
                "file");
        }
    }
    
    private int getTableIndex(String tableName) {
        for (int i = 0; i < tableNames.size(); i++) {
            if (tableNames.get(i).equalsIgnoreCase(tableName)) {
                return i;
            }
        }
        return -1;
    }
    
    public int getPrimaryKey(String tableName) throws IOException {
        FileReader reader = new FileReader(metadataFile);
        BufferedReader bufferedReader = new BufferedReader(reader);
        
        String currentLine = null;
        boolean repeat = true;
        while (repeat) {
            try {
                currentLine = bufferedReader.readLine();
            } catch (IOException err) {
                repeat = false;
            }
            if (repeat) {
                ArrayList<String> tables =
                    new ArrayList<>(Arrays.asList(currentLine.split(",")));
                if (tables.get(0).equalsIgnoreCase(tableName)) {
                    return Integer.parseInt(tables.get(1));
                }
            }
        }
        
        throw new IOException("[ERROR] - can't find table in metadata file");
    }
    
    public void addTable(String tableName) throws IOException {
        // Read the current csv file
        FileReader reader = new FileReader(metadataFile);
        BufferedReader bufferedReader = new BufferedReader(reader);
        ArrayList<String> lines = new ArrayList<>();
        boolean repeat = true;
        while (repeat) {
            String currentLine = null;
            try {
                currentLine = bufferedReader.readLine();
            } catch (IOException err) {
                repeat = false;
            }
            if (repeat) {
                lines.add(currentLine);
            }
        }
        lines.add(tableName + ",1");
        bufferedReader.close();
    
        // rewrite it with the new table line
        FileWriter writer = new FileWriter(metadataFile);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        for (String line : lines) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }
    
    public void dropTable(String tableName) throws IOException {
        // Read the current csv file
        FileReader reader = new FileReader(metadataFile);
        BufferedReader bufferedReader = new BufferedReader(reader);
        ArrayList<String> lines = new ArrayList<>();
        boolean repeat = true;
        while (repeat) {
            String currentLine = null;
            try {
                currentLine = bufferedReader.readLine();
            } catch (IOException err) {
                repeat = false;
            }
            if (repeat) {
                ArrayList<String> table =
                    new ArrayList<>(Arrays.asList(currentLine.split(",")));
                if (!(table.get(0).equalsIgnoreCase(tableName))) {
                    lines.add(currentLine);
                }
            }
        }
        bufferedReader.close();
    
        // rewrite it with table line removed
        FileWriter writer = new FileWriter(metadataFile);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        for (String line : lines) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
