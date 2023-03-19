package edu.uob;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import static java.io.File.separator;

public class Interpreter {
    Database database;
    
    public Interpreter() {
        database = new Database();
    }
    
    public void interpret(AbstractSyntaxTree tree) throws IOException {
        if (tree.getRoot() == null ||
            tree.getRoot().getType() != NodeType.COMMAND) {
            throw new IOException("[ERROR] - invalid query");
        }
        
        switch (tree.getRoot().getLastChild().getType()) {
            case USE -> handleUse(tree.getRoot().getLastChild());
            case CREATE -> handleCreate(tree.getRoot().getLastChild());
            case DROP -> handleDrop(tree.getRoot().getLastChild());
            case ALTER -> handleAlter(tree.getRoot().getLastChild());
            case INSERT -> handleInsert(tree.getRoot().getLastChild());
            case SELECT -> handleSelect(tree.getRoot().getLastChild());
            case UPDATE -> handleUpdate(tree.getRoot().getLastChild());
            case DELETE -> handleDelete(tree.getRoot().getLastChild());
            case JOIN -> handleJoin(tree.getRoot().getLastChild());
            default -> throw new IOException("[ERROR] - invalid query");
        }
        
        saveState();
    }
    
    private void handleUse(Node node) throws IOException {
        // Check that the Use node has the correct children and that the name
        // is not invalid.
        // If that is okay, then make the database we are using the one
        // specified
        if (node.getNumberChildren() > 1 ||
            node.getLastChild().getType() != NodeType.DATABASE_NAME) {
            throw new IOException("[ERROR] - <use> should have one (and only one) argument, which is a database name");
        } else if (nameIsInvalid(node.getLastChild().getValue())) {
            throw new IOException("[ERROR] - database name of " +
                node.getLastChild().getValue() + " is invalid");
        } else {
            String dbName = Paths.get("databases" + separator +
                node.getLastChild().getValue()).toAbsolutePath().toString();
            database.loadDatabase(Paths.get(dbName));
        }
    }

    private void handleCreate(Node node) throws IOException {
        // Invalid queries
        if (node.getLastChild().getType() != NodeType.DATABASE_NAME &&
                node.getLastChild().getType() != NodeType.TABLE_NAME) {
            throw new IOException("[ERROR] = <create> should have one (and " +
                "only one) argument, which is either a table or database name");
        } else if (nameIsInvalid(node.getLastChild().getValue())) {
            throw new IOException("[ERROR] - name of " +
                node.getLastChild().getValue() + " is invalid");
        }
        // Create the databse/table (depending on child node type)
        if (node.getLastChild().getType() == NodeType.DATABASE_NAME) {
            String dbName = Paths.get("databases" + separator +
                node.getLastChild().getValue()).toAbsolutePath().toString();
            database.createDatabase(Paths.get(dbName));
        } else {
            // Catch error of creating a table outside of a database
            if (database.getDatabaseName() == "") {
                throw new IOException("[ERROR] - must be using a database to " +
                    "create a table");
            }
            if (node.getNumberChildren() == 1) {
                try {
                    database.addTable(node.getLastChild().getValue());
                } catch (IOException err) {
                    throw err;
                }
            } else {
                try {
                    // Make an array of the values of the children to this
                    // create node, and pass them to the addTable method of
                    // the database
                    ArrayList<Node> children = node.getChildren();
                    String tableName = children.get(0).getValue();
                    Node[] attributes =
                        (Node[]) children.get(1).getChildren().toArray();
                    ArrayList<String> attributeNames = new ArrayList<>();
                    for (Node attribute : attributes) {
                        if (attribute.getNumberChildren() == 1) {
                            attributeNames.add(attribute.getLastChild().getValue());
                        } else if (attribute.getNumberChildren() == 2) {
                            if (attribute.getChild(0).getValue() != tableName) {
                                throw new IOException("[ERROR] - cannot " +
                                    "create a table with an attribute " +
                                    "associated with another table");
                            } else {
                                attributeNames.add(attribute.getChild(1).getValue());
                            }
                        } else {
                            throw new IOException("[ERROR] - incorrectly " +
                                "formed [attribute]");
                        }
                    }
                    String[] attributeNamesArray =
                        new String[attributeNames.size()];
                    attributeNames.toArray(attributeNamesArray);
                    database.addTable(tableName, attributeNamesArray);
                } catch (IOException err) {
                    throw err;
                }
            }
        }
    }

    private void handleDrop(Node dropNode) throws IOException {
        // Catch possible errros in the command
        if (dropNode == null || dropNode.getType() != NodeType.DROP || dropNode.getNumberChildren() != 1) {
            throw new IOException("[ERROR] - incorrectly formed DROP command");
        }
        
        Node nameNode = dropNode.getLastChild();
        if (nameNode.getType() == NodeType.TABLE_NAME) {
            // Drop the table
            database.dropTable(nameNode.getValue());
        } else if (nameNode.getType() == NodeType.DATABASE_NAME) {
            // Drop the database
            String databaseName = nameNode.getValue();
            if (Database.exists(databaseName)) {
                Database.delete(databaseName);
            }
            if (database.getDatabaseName() == databaseName) {
                database.clearDatabase();
            }
        } else {
            throw new IOException("[ERROR] - DROP command must specify TABLE " +
                "or DATABASE");
        }
    }
    
    private void handleAlter(Node alterNode) throws IOException {
        if (alterNode.getNumberChildren() != 3) {
            throw new IOException("[ERROR] - ALTER command requires a table " +
                "name, an alteration type (i.e., ADD or DROP), and an " +
                "attribute name");
        }
        Node tableNode = alterNode.getChild(0);
        if (!database.tableExists(tableNode.getValue())) {
            throw new IOException("[ERROR] - table of name " +
                tableNode.getValue() + " does not exist in the current " +
                "database");
        }
        
        Node typeNode = alterNode.getChild(1);
        Node attributeNode = alterNode.getChild(2);
        if (typeNode.getValue() == "ADD") {
            database.addAttributeToTable(tableNode.getValue(),
                attributeNode);
        } else if (typeNode.getValue() == "DROP") {
            database.dropAttributeFromTable(tableNode.getValue(),
                attributeNode);
        } else {
            throw new IOException("[ERROR] - the only valid types of " +
                "alteration command are ADD and DROP");
        }
    }
    
    private void handleInsert(Node insertNode) throws IOException {
        database.insertRow(insertNode);
    }


    /*
    ____________ HELPER METHODS! ___________
     */
    
    private boolean nameIsInvalid(String name) {
        return (name.equalsIgnoreCase("USE") ||
            name.equalsIgnoreCase("CREATE") ||
            name.equalsIgnoreCase("DROP") ||
            name.equalsIgnoreCase("DATABSE") ||
            name.equalsIgnoreCase("TABlE") ||
            name.equalsIgnoreCase("ALTER") ||
            name.equalsIgnoreCase("INSERT") ||
            name.equalsIgnoreCase("INTO") ||
            name.equalsIgnoreCase("VALUES") ||
            name.equalsIgnoreCase("SELECT") ||
            name.equalsIgnoreCase("FROM") ||
            name.equalsIgnoreCase("WHERE") ||
            name.equalsIgnoreCase("UPDATE") ||
            name.equalsIgnoreCase("SET") ||
            name.equalsIgnoreCase("DELETE") ||
            name.equalsIgnoreCase("JOIN") ||
            name.equalsIgnoreCase("AND") ||
            name.equalsIgnoreCase("ON") ||
            name.equalsIgnoreCase("TRUE") ||
            name.equalsIgnoreCase("FALSE") ||
            name.equalsIgnoreCase("NULL") ||
            name.equalsIgnoreCase("LIKE") ||
            name.equalsIgnoreCase("OR")
    }
    
    
}
