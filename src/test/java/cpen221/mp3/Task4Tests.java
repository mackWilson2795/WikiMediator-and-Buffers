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
    public static ExecutorService executor;
    public static WikiMediatorServer server;
    public static WikiMediatorClient client;

    @BeforeAll
    public static void setupTests() {
        WikiMediator wm = new WikiMediator(24, 120);
        executor = Executors.newSingleThreadScheduledExecutor();
        try {
            server = new WikiMediatorServer(9012, 10, wm);
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void initializeServerConnectClient() throws InterruptedException {
        server.serve();
        TimeUnit.SECONDS.sleep(3);
        client = new WikiMediatorClient(LOCAL_HOST, 9012);
        TimeUnit.SECONDS.sleep(3);
        int[] intArgs = {5};
        client.sendRequest(null, "search for Desire Path", "search", intArgs, "Desire Path");
        client.receiveResponse();
    }
}
