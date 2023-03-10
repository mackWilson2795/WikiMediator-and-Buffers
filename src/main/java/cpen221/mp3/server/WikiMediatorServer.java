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
    private final ServerSocket serverSocket;
    private final WikiMediator wikiMediator;
    ExecutorService threadPoolExecutor;
    ExecutorService serverExecutor;


    /**
     * Start a server at a given port number, with the ability to process
     * up to n requests concurrently.
     *
     * @param port the port number to bind the server to, 9000 <= {@code port} <= 9999
     * @param n the number of concurrent requests the server can handle, 0 < {@code n} <= 32
     * @param wikiMediator the WikiMediator instance to use for the server, {@code wikiMediator} is not {@code null}
     */
    public WikiMediatorServer(int port, int n,
                              WikiMediator wikiMediator) throws IOException {
        serverSocket = new ServerSocket(port);
        threadPoolExecutor = Executors.newFixedThreadPool(n);
        serverExecutor = Executors.newSingleThreadExecutor();
        // TODO: copy constructor here? - rep exposure
        this.wikiMediator = wikiMediator;
    }

    /**
     * Activates the server and polls the client socket for new clients
     */
    public void serve() {
        serverExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        final Socket clientSocket = serverSocket.accept();
                        createNewThread(clientSocket);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    throw new RuntimeException("Error connecting to ServerSocket.");
                }
            }
        });
    }

    /**
     * Makes connection to client at a given socket and begins handling request
     *
     * @param socket the given socket
     */
    private void createNewThread (Socket socket) {
        threadPoolExecutor.execute(new Runnable() {
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
    }

    /**
     * Deals with the client in a given socket and blocks until it receives a request
     * from a client
     *
     * @param socket the given socket
     * @throws IOException when the connection to the client cannot be established
     */
    private void handleClient(Socket socket) throws IOException {
        Gson gson = new Gson();
        String nextLine;

        try (
                BufferedReader inputStream = new BufferedReader(new InputStreamReader
                        (socket.getInputStream()));
                PrintWriter outputStream = new PrintWriter(new OutputStreamWriter
                        (socket.getOutputStream()));
             ) {
            System.out.println("Client connected...");
            while ((nextLine = inputStream.readLine()) != null) {
                // Request -> JSON
                JsonObject request = gson.fromJson(nextLine, JsonObject.class);
                Object result = null;
                JsonObject response = new JsonObject();
                response.add("id", request.get("id"));

                // Catch "stop"
                if (Objects.equals(request.get("type").getAsString(), "stop")){
                    response.add("response", gson.toJsonTree("bye"));
                    outputStream.print(gson.toJson(response) + "\n");
                    Thread closingThread = new Thread(close());
                    closingThread.start();
                    return;
                }

                try {
                    result  = handleRequest(request);
                } catch (TimeoutException | ExecutionException | InterruptedException  e) {
                    if (e instanceof TimeoutException) {
                        response.add("status", gson.toJsonTree("failed"));
                        response.add("response", gson.toJsonTree("Operation timed out"));
                    } else {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
                if (result != null){
                    response.add("status", gson.toJsonTree("success"));
                    response.add("response", gson.toJsonTree(result));
                }
                outputStream.print(gson.toJson(response) + "\n");
                outputStream.flush();
            }
        }
    }

    /**
     * Handles a clients given request
     *
     * @param request the given request
     * @return an Object representing the response to the client's request
     * @throws TimeoutException If the request could not be handled in a time
     *                          window specified by the client
     * @throws ExecutionException If the thread was unable to execute properly
     * @throws InterruptedException If the execution of the thread is interrupted
     */
    private Object handleRequest(JsonObject request) throws
            TimeoutException, ExecutionException, InterruptedException {
        FutureTask<Object> future = new FutureTask<>(new RequestHandler(request));
        future.run();

        if (request.get("timeout")  != null) {
            future.get(request.get("timeout").getAsLong(), TimeUnit.SECONDS);
        }

        return future.get();
    }

    /**
     * Creates a runnable Object to shut down all threads, close the server socket,
     * and prepare WikiMediator to shut down
     *
     * @return the runnable Object
     */
    private Runnable close() {
        return () -> {
            wikiMediator.close();
            try {
                serverSocket.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            try {
                threadPoolExecutor.shutdown();
                if (!threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS)){
                    threadPoolExecutor.shutdownNow();
                }
            } catch (InterruptedException ignored) {
                threadPoolExecutor.shutdownNow();
            }

            try {
                serverExecutor.shutdown();
                if (!serverExecutor.awaitTermination(10, TimeUnit.SECONDS)){
                    serverExecutor.shutdownNow();
                }
            } catch (InterruptedException ignored) {
                serverExecutor.shutdownNow();
            }
        };
    }

    class RequestHandler implements Callable<Object> {
        private final JsonObject request;

        /**
         * Creates a new RequestHandler object with a given JsonObject request
         *
         * @param request
         */
        public RequestHandler(JsonObject request) {
            this.request = request;
        }

        /**
         * Helper Function, handles request and produces a result using WikiMediator
         *
         * @return and Object representing the result of handling the request
         * @throws TimeoutException if the request could not be handled within a time
         *         window specified by the client
         */
        public Object call() throws TimeoutException {
            String requestType = request.get("type").getAsString();
            Object result = null;

            switch (requestType) {
                case "search":
                    result = wikiMediator.search(
                            request.get("query").getAsString(),
                            request.get("limit").getAsInt());
                    break;
                case "getPage":
                    result = wikiMediator.getPage(
                            request.get("pageTitle").getAsString());
                    break;
                case "zeitgeist":
                    result = wikiMediator.zeitgeist(
                            request.get("limit").getAsInt());
                    break;
                case "trending":
                    result = wikiMediator.trending(
                            request.get("timeLimitInSeconds").getAsInt(),
                            request.get("maxItems").getAsInt());
                    break;
                case "windowedPeakLoad":
                    if (request.has("timeWindowInSeconds")) {
                        result = wikiMediator.windowedPeakLoad(
                                request.get("timeWindowInSeconds").getAsInt());
                    } else {
                        result = wikiMediator.windowedPeakLoad();
                    }
                    break;
                default:
                    result = wikiMediator.shortestPath(request.get("pageTitle1").getAsString(),
                            request.get("pageTitle2").getAsString(), request.get("timeout").getAsInt());
                    break;
            }

            return result;
        }
    }
}
