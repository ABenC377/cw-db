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
        Parser fsm = new Parser(root);
        fsm.populateTree(command);
    }

}
