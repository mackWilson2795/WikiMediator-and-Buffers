package cpen221.mp3;

import cpen221.mp3.server.WikiMediatorClient;
import cpen221.mp3.server.WikiMediatorServer;
import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.*;

public class Task4Tests {

    public static final String LOCAL_HOST = "127.0.0.1";
    public static final int PORT = 9012;
    public static ExecutorService executor;
    public static WikiMediatorServer server;
    public static WikiMediatorClient client;

    @BeforeAll
    public static void setupTests() {
        WikiMediator wm = new WikiMediator(24, 120);
        executor = Executors.newFixedThreadPool(12);
        try {
            server = new WikiMediatorServer(PORT, 10, wm);
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void initializeServerConnectClient() throws InterruptedException {
        client = new WikiMediatorClient(LOCAL_HOST, PORT);
        int[] intArgs = {5};
        client.sendRequest(null, "search for Desire Path", "search", intArgs, "Desire Path");
        client.receiveResponse();
    }

    @Test
    public void sendMultipleRequests() {
        client = new WikiMediatorClient(LOCAL_HOST, PORT);
        int[] intArgs = {5};
        executor.submit(() ->
                client.sendRequest(null, "search for Desire Path",
                        "search", intArgs, "Desire Path"));
        executor.submit(() ->
                client.sendRequest(null, "search for Barack Obama",
                        "search", intArgs, "Barack Obama"));
        executor.submit(() ->
                client.sendRequest(null, "getPage for Barack Obama",
                        "getPage", intArgs, "Barack Obama"));
    }
}
