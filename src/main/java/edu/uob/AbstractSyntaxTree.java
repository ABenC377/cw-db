package edu.uob;

import java.util.Iterator;

public class AbstractSyntaxTree {
    private final Node root;
    private final String command;

    public AbstractSyntaxTree(String command) {
        this.command = command;
        this.root = new Node(NodeType.COMMAND, null);
        this.parseCommand();
    }

    private void parseCommand() {
        Parser fsm = new Parser(root, command);
        fsm.populateTree();
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder(500);
        print(buffer, "", "", root);
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix, Node rt) {
        buffer.append(prefix);
        buffer.append(rt.getType().toString());
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
}
