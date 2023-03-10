package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Table {
    private String name;
    private ArrayList<String> columns;
    private int numRows;
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

        this.columns = new ArrayList<>(Arrays.asList(firstRow.split("\t")));

    }
}
