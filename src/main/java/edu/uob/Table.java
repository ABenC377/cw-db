package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Table {
    private String name;
    private ArrayList<String> columns;
    private int numRows = 0;
    private int primaryKeyValue = 1;

    public Table(String name) {
        this.name = name;
    }

    public Table(File inputFile) {
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
        for (String col : this.columns) {
            if (col.toLowerCase() == "id") {
                hasPrimaryKey = true;
            }
        }
        if (!hasPrimaryKey) {
            // throw an excpetion or something
        }

        String current = null;
        try {
            current = bReader.readLine();
        } catch (IOException e) {
            // throw new RuntimeException(e);
        }
        while (current != null) {
            numRows += 1;

        }
    }
}
