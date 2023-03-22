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

    // This function is miserable.  I know.  However, it works, and does so
    // quite efficiently.
    // Basically, this function recursively prints the children of a tree
    // in a vertical way to portray the structure of the tree
    private void print(StringBuilder buffer, String prefix,
                       String childrenPrefix, Node currentNode) {
        // Add the value of this node to the buffer
        buffer.append(prefix);
        if (currentNode.getType() == null) {
            buffer.append("NULL");
        } else {
            buffer.append(currentNode.getType().toString() +
                        ((currentNode.getValue() != null) ?
                            ("(value = " + currentNode.getValue() + ")") : ""));
        }
        buffer.append('\n');
        
        // Iterate through the children and add them to the buffer as well
        // Change the prefix for the children nodes depending on whether they
        // are the final child
        for (Iterator<Node> childIterator =
             currentNode.getChildren().iterator(); childIterator.hasNext();) {
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
