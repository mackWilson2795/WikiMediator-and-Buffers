package cpen221.mp3.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cpen221.mp3.wikimediator.Requests.Request;
import cpen221.mp3.wikimediator.WikiMediator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.*;

public class WikiMediatorServer {
    private final int maxClients;
    private int currentThreads = 0;
    private final ServerSocket serverSocket;
    private final WikiMediator wikiMediator;
    // TODO: create premade "success" and "failed" JsonObjects

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

    public void serve() {
        try {
            while (true) {
                final Socket clientSocket = serverSocket.accept();
                if (currentThreads < maxClients){
                    createNewThread(clientSocket);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("Error connecting to ServerSocket.");
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
                    throw new RuntimeException("Encountered IOException when" +
                            " creating new thread");
                }
            }
        });
        handler.start();
        currentThreads--; // TODO: double check
    }

    private void handleClient(Socket socket) throws IOException {
        Gson gson = new Gson();
        String nextLine;

        // TODO: I want to try clean this up / maybe break some methods off
        try (
                BufferedReader inputStream = new BufferedReader(new InputStreamReader
                        (socket.getInputStream()));
                PrintWriter outputStream = new PrintWriter(new OutputStreamWriter
                        (socket.getOutputStream()));
             ) {
            while ((nextLine = inputStream.readLine()) != null) {
                // Request -> JSON
                JsonObject request = gson.fromJson(nextLine, JsonObject.class);
                Object result = null;
                JsonObject response = new JsonObject();
                response.add("id", request.get("id"));

                // Catch "stop"
                if (Objects.equals(request.get("type").toString(), "stop")){
                    response.add("response", gson.toJsonTree("bye"));
                    close();
                }

                // Read JSON
                // Handle request
                try {
                    result  = handleRequest(request);
                } catch (TimeoutException | ExecutionException | InterruptedException  e) {
                    if (e instanceof TimeoutException) {
                        // TODO: send failed notice
                    } else {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }

                // Receive request
                // Write JSON response
                if (result != null){
                    // TODO != null????
                    response.add("status", gson.toJsonTree("success"));
                    response.add("response", gson.toJsonTree(result));
                }

                // Send response
                outputStream.println(gson.toJson(response));
            }
        }
    }

    private Object handleRequest(JsonObject request) throws
            TimeoutException, ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor(); // TODO: remove?
        Future<Object> future = new FutureTask<>(new RequestHandler(request));

        if (request.get("timeout")  != null) {
            future.get(request.get("timeout").getAsLong(), TimeUnit.SECONDS);
        }

        return future.get();
    }

    private void close() {
        // TODO: this
        // Call close() in wikiMediator -- this writes to file
        // Close serverSocket -- is this enough?
    }

    class RequestHandler implements Callable<Object> {
        private JsonObject request;

        public RequestHandler(JsonObject request) {
            this.request = request;
        }

        public Object call() {
            String requestType = request.get("type").toString();
            Object toReturn;

            switch (requestType) {
                // TODO: double check spelling of all entries etc...
                case "search":
                    return wikiMediator.search(
                            request.get("query").toString(),
                            request.get("limit").getAsInt());
                case "getPage":
                    return wikiMediator.getPage(
                            request.get("pageTitle").toString());
                case "zeitgeist":
                    return wikiMediator.zeitgeist(
                            request.get("limit").getAsInt());
                case "trending":
                    return wikiMediator.trending(
                            request.get("timeLimitInSeconds").getAsInt(),
                            request.get("maxItems").getAsInt());
                case "windowedPeakLoad":
                    if (request.has("timeWindowInSeconds")) {
                        return wikiMediator.windowedPeakLoad(
                                request.get("timeWindowInSeconds").getAsInt());
                    } else {
                        return wikiMediator.windowedPeakLoad();
                    }
                case "shortestPath":
                    break; // TODO: implement this !!!
            }

            return null; // TODO: fix
        }
    }
}
