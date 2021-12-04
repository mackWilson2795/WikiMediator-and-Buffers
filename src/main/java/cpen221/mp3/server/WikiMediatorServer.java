package cpen221.mp3.server;

import com.google.gson.Gson;
import cpen221.mp3.wikimediator.WikiMediator;
import okhttp3.Request;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WikiMediatorServer {
    private int maxClients;
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
        maxClients = n;
        // TODO: copy constructor here? - rep exposure
        this.wikiMediator = wikiMediator;
    }

    public void serve() throws IOException {
        while (true) {
            final Socket clientSocket = serverSocket.accept();

            //TODO: queue up requests
            if (currentThreads < maxClients){
                createNewThread(clientSocket);
            } else {

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
                        handleClient(socket);
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

    private void handleClient(Socket socket) throws IOException {
        Gson gson = new Gson();

        BufferedReader inputStream = new BufferedReader(new InputStreamReader
                (socket.getInputStream()));
        PrintWriter outputStream = new PrintWriter(new OutputStreamWriter
                (socket.getOutputStream()));
        String nextLine;

        while ((nextLine = inputStream.readLine()) != null){

        }

        //TODO: output pathing?
        // TODO: JSON -> Request
    }
}
