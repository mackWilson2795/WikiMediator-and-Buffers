package cpen221.mp3;

import cpen221.mp3.server.WikiMediatorServer;
import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

public class Task4Tests {

    WikiMediatorServer server;

    @BeforeAll
    public void setupTests() {
        WikiMediator wm = new WikiMediator(24, 120);
        try {
            server = new WikiMediatorServer(9012, 10, wm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
