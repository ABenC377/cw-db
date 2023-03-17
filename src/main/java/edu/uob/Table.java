package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Table {
    private String name;
    private ArrayList<String> columns;
    private ArrayList<ArrayList<String>> rows;
    private int primaryKeyValue = 1;
    private int primaryKeyIndex;

    // For creating a table with just a table name
    public Table(String name) throws IOException {
        this.name = name;
        this.columns = new ArrayList<>();
        columns.add("id");
        this.rows = new ArrayList<>();
    }
    
    // For creating a table with a table name and a list of attributes
    public Table(String[] values) throws IOException {
    
    }
    
    // For populating a database with tables when loading a database from files
    public Table(File inputFile) throws IOException {
        String rawFileName = inputFile.getName();
        if (rawFileName.contains(".tab")) {
            // trim the .tab suffix from the file name (for aesthetics)
            this.name = rawFileName.toLowerCase().substring(0, rawFileName.lastIndexOf(".tab"));
        } else {
            // throw InvalidFileTypeException();
        }

        FileReader reader = null;
        try {
            reader = new FileReader(inputFile);
        } catch (FileNotFoundException e) {
            // throw new RuntimeException(e);
        }

        BufferedReader bReader = null;
        if (reader != null) {
            bReader = new BufferedReader(reader);
        }

        String firstRow = null;
        if (bReader != null) {
            try {
                firstRow = bReader.readLine();
            } catch (IOException e) {
                // throw new RuntimeException(e);
            }
        }

        if (firstRow != null) {
            this.columns = new ArrayList<>(Arrays.asList(firstRow.split("\t")));
        } else {
            // something
        }

        //Checking that there is a column called "id" - as specified in the specification.  I'm being flexible
        // and allowing this to be in any case
        boolean hasPrimaryKey = false;
        int index = 0;
        for (String col : this.columns) {
            if (col.toLowerCase() == "id") {
                primaryKeyIndex = index;
                hasPrimaryKey = true;
            }
            index++;
        }
        if (!hasPrimaryKey) {
            // throw an excpetion or something
        }

        String current = null;
        try {
            current = bReader.readLine();
            rows = new ArrayList<>();
        } catch (IOException e) {
            // throw new RuntimeException(e);
        }

        while (current != null) {
            ArrayList<String> rowList = new ArrayList<>(Arrays.asList(current.split("\t")));
            this.primaryKeyValue = Math.max(Integer.parseInt(rowList.get(primaryKeyIndex)), this.primaryKeyValue);
            rows.add(rowList);
        }

        try {
            bReader.close();
        } catch (IOException e) {
            // throw new RuntimeException(e);
        }
    }
}
