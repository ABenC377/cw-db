package edu.uob;

import java.io.IOException;
import java.util.Iterator;

public class AbstractSyntaxTree {
    private final Node root;
    private final String command;

    public AbstractSyntaxTree(String command) throws IOException {
        this.command = command;
        this.root = new Node(NodeType.COMMAND, null);
        try {
            this.parseCommand();
        } catch (IOException err) {
            throw new IOException(err.getMessage());
        }
    }

    private void parseCommand() throws IOException {
        Parser parser = new Parser(root, command);
        if (!parser.populateTree() || !parser.validateTree()) {
            throw new IOException("[ERROR] - Invalid query syntax");
        }
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(500);
        if (root.getNumberChildren() == 0) {
            return "Empty Tree";
        }
        print(buffer, "", "", root);
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix, Node rt) {
        buffer.append(prefix);
        buffer.append((rt.getType() != null) ? (rt.getType().toString() + ((rt.getValue() != null) ? ("(value = " + rt.getValue() + ")") : "") ) : "NULL");
        buffer.append('\n');
        for (Iterator<Node> childIterator = rt.getChildren().iterator(); childIterator.hasNext();) {
            Node next = childIterator.next();
            if (childIterator.hasNext()) {
                print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ", next);
            } else {
                print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ", next);
            }
        }
    }

    public Node getRoot() {
        return root;
    }
}
