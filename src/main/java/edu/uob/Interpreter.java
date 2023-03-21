package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.io.File.separator;

public class Interpreter {
    Database database;
    
    public Interpreter() {
        database = new Database();
    }
    
    public String interpret(AbstractSyntaxTree tree) throws IOException {
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
            case SELECT -> {
                return handleSelect(tree.getRoot().getLastChild());
            }
            case UPDATE -> handleUpdate(tree.getRoot().getLastChild());
            case DELETE -> handleDelete(tree.getRoot().getLastChild());
            case JOIN -> {
                return handleJoin(tree.getRoot().getLastChild());
            }
            default -> throw new IOException("[ERROR] - invalid query");
        }
        
        database.saveState();
        return "";
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
            database.loadDatabase(node.getLastChild().getValue());
        }
    }

    private void handleCreate(Node node) throws IOException {
        // Invalid queries
        if (nameIsInvalid(node.getChild(0).getValue())) {
            throw new IOException("[ERROR] - name of " +
                node.getChild(0).getValue() + " is invalid");
        }
        
        // if Database name, create a database
        if (node.getChild(0).getType() == NodeType.DATABASE_NAME) {
            String dbName = Paths.get("databases" + separator +
                node.getLastChild().getValue()).toAbsolutePath().toString();
            database.createDatabase(Paths.get(dbName));
        }
        
        // If a table name, create a table
        if (node.getChild(0).getType() == NodeType.TABLE_NAME) {
            // Catch error of creating a table outside of a database
            if (database.getDatabaseName() == "") {
                throw new IOException("[ERROR] - must be using a database to " +
                    "create a table");
            }
            
            // If attributes are provided, add them as well
            String tableName = node.getChild(0).getValue();
            database.addTable(tableName);
            if (node.getNumberChildren() == 2) {
                // Make an array of the values of the children to this
                // create node, and pass them to the addTable method of
                // the database
                Node attributesNode = node.getChild(1);
                for (int i = 0; i < attributesNode.getNumberChildren(); i++) {
                    database.addAttributeToTable(tableName, attributesNode.getChild(i));
                }
            }
        }
    }

    private void handleDrop(Node dropNode) throws IOException {
        // Catch possible errors in the command
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

    private String handleSelect(Node selectNode) throws IOException {
        if (selectNode.getNumberChildren() == 2) {
            return database.selectValues(selectNode.getChild(0),
                selectNode.getChild(1));
        } else if (selectNode.getNumberChildren() == 3) {
            return database.selectValuesWhere(selectNode.getChild(0),
                selectNode.getChild(1), selectNode.getChild(2));
        } else {
            throw new IOException("[ERROR] - invalid number of arguments to a" +
                " SELECT command");
        }
    }
    
    private void handleUpdate(Node updateNode) throws IOException {
        if (updateNode.getNumberChildren() == 3) {
            database.updateValues(updateNode);
        } else {
            throw new IOException("[ERROR] - UPDATE command requires three " +
                "arguments: table name, name value list, and condition");
        }
    }
    
    private void handleDelete(Node deleteNode) throws IOException {
        if (deleteNode.getNumberChildren() == 2) {
            database.deleteRows(deleteNode);
        } else {
            throw new IOException("[ERROR] - DELETE command requires two " +
                "arguments: table name and condition");
        }
    }
    
    private String handleJoin(Node joinNode) throws IOException {
        if (joinNode.getNumberChildren() == 4) {
            return database.joinTables(joinNode);
        } else {
            throw new IOException("[ERROR] - JOIN command requires four " +
                "arguments: two table names, and two attribute names");
        }
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
            name.equalsIgnoreCase("OR"));
    }
    
    
}
