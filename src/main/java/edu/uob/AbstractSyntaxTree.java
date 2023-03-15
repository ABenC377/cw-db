package edu.uob;

public class AbstractSyntaxTree {
    private Node root;
    private String command;

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
        return toString(root);
    }

    private String toString(Node rt) {
        StringBuilder buffer = new StringBuilder(500);
        print(buffer, "", "", rt);
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix, Node rt) {
        buffer.append(prefix);
        buffer.append(rt.getType().toString());
        buffer.append('\n');
        for (Iterator<Node> it = children.iterator(); it.hasNext();) {
            TreeNode next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }
}
