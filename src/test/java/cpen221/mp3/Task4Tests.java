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
import java.util.*;
import java.util.concurrent.*;

public class Task4Tests {

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
    public void sendMultipleRequestsNoTimeout() throws InterruptedException, ExecutionException {
        client = new WikiMediatorClient(LOCAL_HOST, PORT);
        /* intArgs[0] is number of results to return
         intArgs[1] is timeWindow */
        int[] intArgs = {5, 3};
        ArrayList<String> results = new ArrayList<>();
        executor.submit(() ->
                client.sendRequest(null, "search for Desire Path",
                        "search", intArgs, "Desire Path"));
        results.add(executor.submit(() -> client.receiveResponse()).get());
        executor.submit(() ->
                client.sendRequest(null, "search for Barack Obama",
                        "search", intArgs, "Barack Obama"));
        results.add(executor.submit(() -> client.receiveResponse()).get());
        executor.submit(() ->
                client.sendRequest(null, "getPage for Barack Obama",
                        "getPage", intArgs, "Barack Obama"));
        results.add(executor.submit(() -> client.receiveResponse()).get());
        executor.submit(() ->
                client.sendRequest(null, "getPage for Barack Obama",
                        "getPage", intArgs, "Barack Obama"));
        results.add(executor.submit(() -> client.receiveResponse()).get());
        executor.submit(() ->
                client.sendRequest(null, "zeitgeist",
                        "zeitgeist", intArgs));
        results.add(executor.submit(() -> client.receiveResponse()).get());
        executor.submit(() ->
                client.sendRequest(null, "trending",
                        "trending", intArgs));
        results.add(executor.submit(() -> client.receiveResponse()).get());
        TimeUnit.SECONDS.sleep(5);
        executor.submit(() ->
                client.sendRequest(null, "search for Barack Obama",
                        "search", intArgs, "Barack Obama"));
        results.add(executor.submit(() -> client.receiveResponse()).get());
        executor.submit(() ->
                client.sendRequest(null, "window1",
                        "windowedPeakLoad", intArgs));
        results.add(executor.submit(() -> client.receiveResponse()).get());
        int[] intArgEmpty = new int[]{};
        executor.submit(() ->
                client.sendRequest(null, "window2",
                        "windowedPeakLoad", intArgEmpty));
        results.add(executor.submit(() -> client.receiveResponse()).get());
        Assertions.assertTrue(results.get(0).contains("Desire path"));
        Assertions.assertTrue(results.get(1).contains("Barack Obama"));
        Assertions.assertTrue(results.get(2).contains("Nine months later, he was named"));
        Assertions.assertEquals(Integer.toString(6),
                json.fromJson(results.get(7), JsonObject.class).get("response").getAsString());
        Assertions.assertEquals(Integer.toString(9),
                json.fromJson(results.get(8), JsonObject.class).get("response").getAsString());

        executor.submit(() -> client.done("ten"));
        String result = executor.submit(() -> client.receiveResponse()).get();
    }
}
