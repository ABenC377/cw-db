package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class DBClient {

    private static final char END_OF_TRANSMISSION = 4;

    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Socket socket = new Socket("localhost", 8888);
        BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        while (!Thread.interrupted()) {
            handleNextCommand(input, socketReader, socketWriter);
        }
    }

    private static void handleNextCommand(BufferedReader commandLine, BufferedReader socketReader, BufferedWriter socketWriter) throws IOException {
        System.out.print("SQL:> ");
        String command = commandLine.readLine();
        socketWriter.write(command + "\n");
        socketWriter.flush();
        String incomingMessage = socketReader.readLine();
        if (incomingMessage == null) {
            throw new IOException("Server disconnected (end-of-stream)");
        }
        while (incomingMessage != null && !incomingMessage.contains("" + END_OF_TRANSMISSION + "")) {
            System.out.println(incomingMessage);
            incomingMessage = socketReader.readLine();
        }
    }
}
