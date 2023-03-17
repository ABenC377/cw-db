package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static java.io.File.separator;

public class Interpreter {
    Database database;
    public Interpreter() {}
    
    public void interpret(AbstractSyntaxTree tree) throws IOException {
        database = new Database();
        if (tree.getRoot() == null ||
            tree.getRoot().getType() != NodeType.COMMAND) {
            throw new IOException("[ERROR] - invalid query");
        }
        switch (tree.getRoot().getLastChild().getType()) {
            case USE -> handleUse(tree.getRoot().getLastChild());
            case CREATE -> handleCreate(tree.getRoot().getLastChild());
            case DROP -> handleDrop(tree.getRoot().getLastChild());
            case ALTER -> handleAlter(tree.getRoot().getLastChild());
            case INSERT -> handleInsert(tree.getRoot().getLastChild());
            case SELECT -> handleSelect(tree.getRoot().getLastChild());
            case UPDATE -> handleUpdate(tree.getRoot().getLastChild());
            case DELETE -> handleDelete(tree.getRoot().getLastChild());
            case JOIN -> handleJoin(tree.getRoot().getLastChild());
            default -> throw new IOException("[ERROR] - invalid query");
        }
    }
    
    private void handleUse(Node node) throws IOException {
        // Check that the Use node has the correct children
        // If it does, then make the database we are using the one specified
        if (node.getNumberChildren() > 1 ||
            node.getLastChild().getType() != NodeType.DATABASE_NAME) {
            throw new IOException("[ERROR] - <use> should have one (and only one) argument, which is a database name");
        } else if (databaseNameInvalid(node.getLastChild().getValue())) {
            throw new IOException("[ERROR] - databse name of " +
                node.getLastChild().getValue() + " is invalid");
        } else {
            String dbName = Paths.get("databases" + separator +
                node.getLastChild().getValue()).toAbsolutePath().toString();
            database.load(Paths.get(dbName));
        }
    }

    private void handleCreate(Node node) throws IOException {
    
    }




    /*
    ____________ HELPER METHODS! ___________
     */
    
    private boolean databaseNameInvalid(String name) {
        return (name.toUpperCase().contains("USE") ||
            name.toUpperCase().contains("CREATE") ||
            name.toUpperCase().contains("DROP") ||
            name.toUpperCase().contains("DATABASE") ||
            name.toUpperCase().contains("TABLE") ||
            name.toUpperCase().contains("ALTER") ||
            name.toUpperCase().contains("INSERT") ||
            name.toUpperCase().contains("INTO") ||
            name.toUpperCase().contains("VALUES") ||
            name.toUpperCase().contains("SELECT") ||
            name.toUpperCase().contains("FROM") ||
            name.toUpperCase().contains("WHERE") ||
            name.toUpperCase().contains("UPDATE") ||
            name.toUpperCase().contains("SET") ||
            name.toUpperCase().contains("DELETE") ||
            name.toUpperCase().contains("JOIN") ||
            name.toUpperCase().contains("AND") ||
            name.toUpperCase().contains("ON") ||
            name.toUpperCase().contains("TRUE") ||
            name.toUpperCase().contains("FALSE") ||
            name.toUpperCase().contains("NULL") ||
            name.toUpperCase().contains("LIKE") ||
            name.toUpperCase().contains("OR"));
    }
    
    
}
