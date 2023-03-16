package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Database {
    private final ArrayList<Table> tables;

    public Database() {
        tables = new ArrayList<>();
    }

    public void load(Path path) throws IOException {
        if (Files.exists(path)) {
            for (File table : path.toFile().listFiles()) {
                tables.add(new Table(table));
            }
        } else {
            try {
                Files.createDirectories(path);
            } catch (IOException err) {
                throw new IOException("[ERROR] cannot open new database directory at " + path.toString());
            }
        }
    }
}
