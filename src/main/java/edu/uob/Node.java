package edu.uob;

public class Node {
    private Token type;
    private Node parent;
    private Node child1;
    private Node child2;
    private Node child3;
    private Node child4;
    private String value;

    public Node(Token type, Node parent) {
        this.type = type;
        this.parent = parent;
        this.child1 = null;
        this.child2 = null;
        this.child3 = null;
        this.child4 = null;
        this.value = null;
    }



    // Getters
    public Node getParent() {
        return parent;
    }
    public Node getChild1() {
        return child1;
    }
    public Node getChild2() {
        return child2;
    }
    public Node getChild3() {
        return child3;
    }
    public Node getChild4() {
        return child4;
    }
    public String getValue() {
        return value;
    }
    public Token getType() {
        return type;
    }

    // Setters
    public void setParent(Node parent) {
        this.parent = parent;
    }
    public void setChild1(Node child1) {
        this.child1 = child1;
    }
    public void setChild2(Node child2) {
        this.child2 = child2;
    }
    public void setChild3(Node child3) {
        this.child3 = child3;
    }
    public void setChild4(Node child4) {
        this.child4 = child4;
    }
    public void setValue(String value) {
        this.value = value;
    }
}

