package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Objects;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private final String storageFolderPath;
    private final Interpreter interpreter;

    public static void main(String[] args) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder" +
                " " + storageFolderPath);
        }
    
        interpreter = new Interpreter();
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) {
        // Handle the query commands from the input String onto the temporary instance of the DB
        AbstractSyntaxTree ast;
        try {
            ast = new AbstractSyntaxTree(command);
            // System.out.println(ast); // debugging
        } catch (IOException err) {
            return err.getMessage();
        }
        try {
            String queryOutput = interpreter.interpret(ast);
            return ("[OK]\n" + queryOutput);
        } catch (IOException err) {
            return err.getMessage();
        }
    }


    public void dropAll() throws IOException {
        File databasesDirectory = new File(Paths.get("databases").toAbsolutePath().toString());
        for (File database : Objects.requireNonNull(databasesDirectory.listFiles())) {
            Database.delete(database.getName());
        }
    }
    
    @Override
    public String toString() {
        Database database = new Database();
        File databaseDirectory =
            new File(Paths.get("databases").toAbsolutePath().toString());
        if (!databaseDirectory.exists()) {
            return "No databases";
        }
        
        StringBuilder output = new StringBuilder();
        for (File db : Objects.requireNonNull(databaseDirectory.listFiles())) {
            try {
                database.loadDatabase(db.getName());
            } catch (IOException err) { }
            output.append(database);
        }
        return output.toString();
    }


    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
