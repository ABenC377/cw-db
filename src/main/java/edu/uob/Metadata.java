package edu.uob;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

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
    
    public void updatePrimaryKey(String tableName, int newPrimaryKey) {
    
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
        FileWriter writer = new FileWriter(metadataFile);
        writer.write("\n" + tableName + ",1");
        writer.flush();
        writer.close();
    }
    
    public void dropTable(String tableName) {
    
    }
}
