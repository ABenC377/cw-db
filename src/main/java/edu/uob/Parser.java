package edu.uob;

public class Parser {
    private Node current;
    private String command;
    int index;

    public Parser(Node root, String command) {
        this.command = command;
        this.current = root;
        this.index = 0;
    }

    public boolean populateTree() {
        // making a new child node for the potential commandtype
        current.setChild1(new Node(current));
        current = current.getChild1();

        // checking that there is a valid command type
        if (tryUse() || tryCreate() || tryDrop() || tryAlter() || tryInsert() ||
                trySelect() || tryUpdate() || tryDelete() || tryJoin()) {
            // moving back up to the parent before returning to ensure we don't drift
            // down the tree
            current = current.getParent();
            return true;
        } else {
            // getting rid of the impossible commandtype node before returning
            current = current.getParent();
            current.setChild1(null);
            return false;
        }
    }


    private boolean tryUse() {
        // save the index so we can reset it before returning false
        int originalIndex = index;

        // check for the keyword
        skipWhiteSpace();
        if (!substringIsNext("USE ")) {
            index = originalIndex;
            return false;
        }

        // set up and move to a new child node for the potential database name
        current.setType(NodeType.USE);
        skipWhiteSpace();
        current.setChild1(new Node(current));
        current = current.getChild1();

        // ________ To think about ___________
        // Can I use Supplier<T> to save on the repeated code below (in each of the try__() methods)
        if (tryPlainText()) {
            current = current.getParent();
            return true;
        } else {
            current = current.getParent();
            current.setChild1(null);
            index = originalIndex;
            return false;
        }
    }

    private boolean tryCreate() {

    }

    private boolean tryDrop() {

    }

    private boolean tryAlter() {

    }

    private boolean tryInsert() {

    }

    private boolean trySelect() {

    }

    private boolean tryUpdate() {

    }

    private boolean tryDelete() {

    }

    private boolean tryJoin() {

    }

    private void skipWhiteSpace() {
        while (Character.isWhitespace(command.charAt(index))) {
            index++;
        }
    }

    private boolean substringIsNext(String str) {
        return (command.substring(index, index + str.length()).equals(str));
    }
}
