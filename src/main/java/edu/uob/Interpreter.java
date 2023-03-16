package edu.uob;

import java.io.IOException;

public class Interpreter {

    Database db;
    public Interpreter() {}

    public boolean interpret(AbstractSyntaxTree tree) throws IOException {
        db = new Database();
        if (tree.getRoot() == null || tree.getRoot().getType() != NodeType.COMMAND) {
            return false;
        }
        switch (tree.getRoot().getLastChild().getType()) {
            case USE:
                return handleUse(tree.getRoot().getLastChild());
            case CREATE:
                return handleCreate(tree.getRoot().getLastChild());
            case DROP:
                return handleDrop(tree.getRoot().getLastChild());
            case ALTER:
                return handleAlter(tree.getRoot().getLastChild());
            case INSERT:
                break;
            case SELECT:
                break;
            case UPDATE:
                break;
            case DELETE:
                break;
            case JOIN:
                break;
            default:
                return false;
        }
    }
}
