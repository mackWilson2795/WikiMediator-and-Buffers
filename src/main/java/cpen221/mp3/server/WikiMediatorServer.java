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
        threadPoolExecutor = Executors.newFixedThreadPool(n);
        serverExecutor = Executors.newSingleThreadExecutor();
        // TODO: copy constructor here? - rep exposure
        this.wikiMediator = wikiMediator;
    }

    public void serve() {
        // TODO: wikimediator inside try with resources?
        serverExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        final Socket clientSocket = serverSocket.accept();
                        // TODO: going to implement w/ executor - double check tmrw
                        createNewThread(clientSocket);
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    throw new RuntimeException("Error connecting to ServerSocket.");
                }
            }
        });
    }

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
                    outputStream.println(gson.toJson(response));
                    // TODO: think about this - can't call executor termination from inside a thread the executor controls.
                    Thread closingThread = new Thread(close());
                    closingThread.start();
                }

                // Read JSON
                // Handle request
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
        Future<Object> future = new FutureTask<>(new RequestHandler(request));

        if (request.get("timeout")  != null) {
            future.get(request.get("timeout").getAsLong(), TimeUnit.SECONDS);
        }

        return future.get();
    }

    private Runnable close() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    threadPoolExecutor.shutdown();
                    if (!threadPoolExecutor.awaitTermination(20, TimeUnit.SECONDS)){ // TODO: consider time to wait
                        threadPoolExecutor.shutdownNow();
                    }
                } catch (InterruptedException ignored) {
                    /* This exception is ignored, termination can continue... */
                }

                try {
                    serverExecutor.shutdown();
                    if (!serverExecutor.awaitTermination(3, TimeUnit.SECONDS)){
                        serverExecutor.shutdownNow();
                    }
                } catch (InterruptedException ignored) {
                    /* This exception is ignored  */
                }

                try {
                    serverSocket.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                // wikiMediator.close();
                // TODO: implement close() here !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
        };
    }

    class RequestHandler implements Callable<Object> {
        private final JsonObject request;

        public RequestHandler(JsonObject request) {
            this.request = request;
        }

        public Object call() throws TimeoutException { // TODO: TimeoutException
            String requestType = request.get("type").toString();
            Object result = null; // TODO: null? idk...

            switch (requestType) {
                // TODO: double check spelling of all entries etc...
                case "search":
                    result = wikiMediator.search(
                            request.get("query").toString(),
                            request.get("limit").getAsInt());
                    break;
                case "getPage":
                    result = wikiMediator.getPage(
                            request.get("pageTitle").toString());
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
                case "shortestPath":
                    break; // TODO: implement this !!!
            }

            return result; // TODO: fix
        }
    }
}
