package edu.uob;

import java.util.ArrayList;

public class Node {
    private NodeType type;
    private Node parent;
    private final ArrayList<Node> children;
    private String value;
    private boolean complete;



    public Node() {
        this.type = null;
        this.parent = null;
        this.children = new ArrayList<>();
        this.value = null;
        this.complete = false;
    }
    public Node(Node parent) {
        this.type = null;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.value = null;
        this.complete = false;
    }
    public Node(NodeType type, Node parent) {
        this.type = type;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.value = null;
        this.complete = false;
    }

    public void clearChildren() {
        children.clear();
    }

    // Getters
    public Node getParent() {
        return parent;
    }
    public Node getLastChild() {
        if (!(children.isEmpty())) {
            return children.get(children.size() - 1);
        } else {
            return null;
        }
    }
    public Node getChild(int index) {
        if (index < children.size() && index >= 0) {
            return children.get(index);
        } else {
            return null;
        }
    }
    public void popChild() {
        if (!(children.isEmpty())) {
            children.remove(children.size() - 1);
        }
    }
    public void popChild(int index) {
        if (index < children.size() && index >= 0) {
            children.remove(index);
        }
    }
    public int getNumberChildren() {
        return children.size();
    }
    public String getValue() {
        return value;
    }
    public NodeType getType() {
        return type;
    }
    public boolean isComplete() {
        return complete;
    }
    public ArrayList<Node> getChildren() {
        return children;
    }

    // Setters
    public void setType(NodeType type) {
        this.type = type;
    }
    public void setParent(Node parent) {
        this.parent = parent;
    }
    public void addChild(Node child) {
        this.children.add(child);
    }
    public void setValue(String value) {
        this.value = value;
    }
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}

