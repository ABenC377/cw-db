package edu.uob;

import java.io.IOException;

public class Interpreter {

    Database db;
    public Interpreter() {}

    public void interpret(AbstractSyntaxTree tree) throws IOException {
        db = new Database();
        if (tree.getRoot() == null || tree.getRoot().getType() != NodeType.COMMAND) {
            throw new IOException("[ERROR] - invalid query");
        }
        switch (tree.getRoot().getLastChild().getType()) {
            case USE:
                handleUse(tree.getRoot().getLastChild());
                break;
            case CREATE:
                handleCreate(tree.getRoot().getLastChild());
                break;
            case DROP:
                handleDrop(tree.getRoot().getLastChild());
                break;
            case ALTER:
                handleAlter(tree.getRoot().getLastChild());
                break;
            case INSERT:
                handleInsert(tree.getRoot().getLastChild());
                break;
            case SELECT:
                handleSelect(tree.getRoot().getLastChild());
                break;
            case UPDATE:
                handleUpdate(tree.getRoot().getLastChild());
                break;
            case DELETE:
                handleDelete(tree.getRoot().getLastChild());
                break;
            case JOIN:
                handleJoin(tree.getRoot().getLastChild());
                break;
            default:
                throw new IOException("[ERROR] - invalid query");
                break;
        }
    }

    private void handleUse(Node node) throws IOException {
        if (node.getNumberChildren() > 1 || node.getLastChild().getType() != NodeType.DATABASE_NAME) {
            throw new IOException("[ERROR] - <use> should have one (and only one) argument, which is a database name");
        } else {
            String dbName = node.getLastChild().getValue();

        }
    }
}
