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

    public void populateTree() {
        if (this.isUse()) {
            this.handleUse();
        } else if (this.isCreate()) {
            this.handleCreate();
        } else if (this.isDrop()) {
            this.handleDrop();
        } else if (this.isAlter()) {
            this.handleAlter();
        } else if (this.isInsert()) {
            this.handleInsert();
        } else if (this.isSelect()) {
            this.handleSelect();
        } else if (this.isUpdate()) {
            this.handleUpdate();
        } else if (this.isDelete()) {
            this.handleDelete();
        } else if (this.isJoin()) {
            this.handleJoin();
        } else {
            // error
        }
    }

}
