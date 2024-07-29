package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Paths;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private static final String WEB_ROOT = "www"; // Directory to serve files from

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {


            String requestLine = in.readLine();
            if (requestLine != null) {
                System.out.println("Request received: " + requestLine);

                String[] requestParts = requestLine.split(" ");
                String path = "/";
                if (requestParts.length > 1) {
                    path = requestParts[1];
                }


                if (path.equals("/")) {
                    path = "index.html";
                }
                String pathName="src/main/java/org/example/www/"+path;
                File file = new File(pathName);
                if (file != null  && !file.isDirectory()) {
                    // Read file contents
                    FileInputStream fis = new FileInputStream(file);
                    byte[] fileContent = fis.readAllBytes();
                    fis.close();

                    // Send HTTP response with the file content
                    String contentType = "text/html";
                    String responseHeader = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + contentType + "\r\n" +
                            "Content-Length: " + fileContent.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n";
                    out.write(responseHeader.getBytes());
                    out.write(fileContent);
                    out.flush();
                } else {
                    // Send 404 Not Found response
                    String responseBody = "<html><body><h1>404 Not Found</h1></body></html>";
                    String responseHeader = "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Type: text/html\r\n" +
                            "Content-Length: " + responseBody.length() + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n";

                    out.write(responseHeader.getBytes());
                    out.write(responseBody.getBytes());
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
