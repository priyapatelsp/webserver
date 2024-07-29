package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BasicHttpServer {

    private static final int PORT = 80;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected");


                    var clientHandler = new ClientHandler(clientSocket);
                    new Thread(clientHandler).start();

                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
