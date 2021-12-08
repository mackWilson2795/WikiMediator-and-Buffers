package cpen221.mp3;

import cpen221.mp3.wikimediator.Requests.*;
import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Task3DirectTests {


    // TODO: test windowedPeakLoad with empty structure
    // TODO: test windowed peak load with all requests at one time
    // TODO: test search with input 0


    private static ScheduledExecutorService scheduledExecutorService;
    private static WikiMediator wikiMediator1;
    private static String desirePath = "Trampling studies " +
            "have consistently documented that impacts on soil" +
            " and vegetation occur rapidly with initial use of desire paths.";
    private static String desirePathTitle = "Desire path";
    private static final File allRequestsFile = new File("local/allRequests.txt");
    private static final File countMapFile = new File("local/countMap.txt");

    @BeforeAll
    public static void setupTests() {
        if (allRequestsFile.exists()) {
            allRequestsFile.delete();
        }
        if (countMapFile.exists()) {
            countMapFile.delete();
        }
        wikiMediator1 = new WikiMediator(10, 30);
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Test
    public void task3SimpleWikiSearches() {
        List<String> searchResult = new ArrayList<String>();
        searchResult = wikiMediator1.search("Desire Path", 5);
        String pageData = wikiMediator1.getPage(desirePathTitle);
        Assertions.assertEquals(true, searchResult.contains(desirePathTitle));
        Assertions.assertEquals(true, pageData.contains(desirePath));
    }

    @Test
    public void task3MoreRequestsThenZeitgeist() {
        for (int i = 0; i <= 6; i++) {
            wikiMediator1.search(Integer.toString(i), 1);
            if ((i % 2) == 0) {
                for (int j = i/2; j >= 0; j = j - 2) {
                    wikiMediator1.search(Integer.toString(i), 1);
                }
            }
        }
        List<String> expected = List.of(Integer.toString(6),
                Integer.toString(4), Integer.toString(2),
                Integer.toString(0));
        Assertions.assertEquals(expected, wikiMediator1.zeitgeist(5));
    }

    @Test
    public void directRequestObjects() {
        ReferenceRequest referenceRequest = new ReferenceRequest(1L);
        referenceRequest.getQueries();
        TrendingRequest trendingRequest = new TrendingRequest(1L, 3,3);
        trendingRequest.getQueries();
        WindowedPeakLoadRequest windowedPeakLoadRequest = new WindowedPeakLoadRequest(1L, 1);
        windowedPeakLoadRequest.getQueries();
        ZeitgeistRequest zeitgeistRequest = new ZeitgeistRequest(1L, 1);
        zeitgeistRequest.getQueries();
        wikiMediator1.checkRep();
        ShortestPathRequest shortestPathRequest = new ShortestPathRequest(1L, "string1", "string 2", 1);
        shortestPathRequest.getQueries();
    }

    @Test
    public void task3TimedTest() {
        // TODO: hehre
    }

    static class RunnableForTiming implements Runnable {
        public void run() {

        }
    }
}
