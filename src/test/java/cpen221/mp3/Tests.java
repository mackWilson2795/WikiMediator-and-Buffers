package cpen221.mp3;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cpen221.mp3.server.WikiMediatorClient;
import cpen221.mp3.server.WikiMediatorServer;
import cpen221.mp3.wikimediator.WikiMediator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tests {

    public static final String LOCAL_HOST = "127.0.0.1";
    public static final int PORT = 9012;
    public static ExecutorService executor;
    public static WikiMediatorServer server;
    public static WikiMediatorClient client;
    public static Gson json = new Gson();
    private static final File allRequestsFile = new File("local/allRequests.txt");
    private static final File countMapFile = new File("local/countMap.txt");

    @BeforeAll
    public static void setupTests() {
        /* Clear cache contents */
        if (allRequestsFile.exists()) {
            allRequestsFile.delete();
        }
        if (countMapFile.exists()) {
            countMapFile.delete();
        }
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
    public void timeoutTest() throws ExecutionException, InterruptedException {
        client = new WikiMediatorClient(LOCAL_HOST, PORT);
        int[] intArgs = {5, 3};
        executor.submit(() ->
                client.sendRequest(5L, "search for Desire Path",
                        "search", intArgs, "Desire Path"));
        JsonObject requestJson = json.fromJson(
                executor.submit(() -> client.receiveResponse()).get(), JsonObject.class);
        Assertions.assertEquals("success", requestJson.get("status").getAsString());
        executor.submit(() ->
                client.sendRequest(5L, "search for Desire Path",
                        "search", intArgs, "43124256564344678847647857452632565246543"));
        JsonObject requestJson2 = json.fromJson(
                executor.submit(() -> client.receiveResponse()).get(), JsonObject.class);
        Assertions.assertEquals("success", requestJson2.get("status").getAsString());
        executor.submit(() ->
                client.sendRequest(5L, "windowedPeakLoad",
                        "windowedPeakLoad", intArgs));
        JsonObject requestJson3 = json.fromJson(
                executor.submit(() -> client.receiveResponse()).get(), JsonObject.class);
        Assertions.assertEquals("success", requestJson3.get("status").getAsString());
        executor.submit(() ->
                client.sendRequest(180L, "shortest path",
                        "shortestPath", intArgs, "PCBD2", "Apoptosis"));
        executor.submit(() -> client.receiveResponse()).get();
    }
}
