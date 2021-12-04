package cpen221.mp3.server;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import cpen221.mp3.wikimediator.WikiMediator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class WikiMediatorServer {
    private int maxRequests;
    private int currentThreads = 0;
    private ServerSocket serverSocket;
    private WikiMediator wikiMediator;
    // TODO: create queue

    /**
     * Start a server at a given port number, with the ability to process
     * upto n requests concurrently.
     *
     * @param port the port number to bind the server to, 9000 <= {@code port} <= 9999
     * @param n the number of concurrent requests the server can handle, 0 < {@code n} <= 32
     * @param wikiMediator the WikiMediator instance to use for the server, {@code wikiMediator} is not {@code null}
     */
    public WikiMediatorServer(int port, int n,
                              WikiMediator wikiMediator) throws IOException {
        serverSocket = new ServerSocket(port);
        maxRequests = n;
        // TODO: copy constructor here? - rep exposure
        this.wikiMediator = wikiMediator;
    }

    public void serve() throws IOException {
        while (true) {
            //TODO: queue up requests
            if (currentThreads < maxRequests){
                final Socket clientSocket = serverSocket.accept();
                createNewThread(clientSocket);
            }
        }
    }

    private void createNewThread (Socket socket) {
        currentThreads++;
        Thread handler = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        handleRequest(socket);
                    } finally {
                        socket.close();
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        });
        handler.start();
    }

    private void handleRequest(Socket socket) throws IOException {
        Gson gson = new Gson();

        JsonReader reader = new JsonReader(new InputStreamReader
                (socket.getInputStream()));

        Request request = gson.fromJson(reader, );

        switch (request) {
            case 1:;
            case 2:;
        }

        //TODO: output pathing?
        // TODO: JSON -> Request

        try {

        }
    }
}
