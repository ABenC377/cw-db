package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;

public class AbstractSyntaxTree {
    private Node root;

    public AbstractSyntaxTree(String command) {
        ArrayList<String> tokens = this.tokenise(command);
        this.root = this.populateTree(tokens);
    }

    private ArrayList<String> tokenise(String command) {
        this.addCommands(command);

    }

    private Node populateTree(ArrayList<String> tokens) {

    }


}
