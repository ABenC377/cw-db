package edu.uob;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

import static java.nio.file.StandardOpenOption.APPEND;

public class Metadata {
    File metadataFile;
    
    public Metadata(Path databasePath) throws IOException {
        metadataFile = new File(databasePath.toString() + File.separator +
                "meta");
        
        if (!metadataFile.createNewFile()) {
            throw new IOException("[ERROR] - unable to create metadata file " +
                "in " + databasePath);
        }
    }
    
    public void updatePrimaryKey(String tableName, int newPrimaryKey) throws IOException {
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
                if (table.get(0).equalsIgnoreCase(tableName)) {
                    table.remove(1);
                    table.add(String.valueOf(newPrimaryKey));
                    String newLine = table.get(0) + "," + table.get(1);
                    lines.add(newLine);
                } else {
                    lines.add(currentLine);
                }
            }
        }
        bufferedReader.close();
        
        // rewrite it with the new values
        FileWriter writer = new FileWriter(metadataFile);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        for (String line : lines) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        bufferedWriter.close();
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
