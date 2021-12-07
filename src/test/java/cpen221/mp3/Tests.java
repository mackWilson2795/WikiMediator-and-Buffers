package cpen221.mp3;

import com.google.gson.Gson;
import cpen221.mp3.wikimediator.Requests.SearchRequest;
import cpen221.mp3.wikimediator.WikiMediator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.List;

public class Tests {

    /*
        You can add your tests here.
        Remember to import the packages that you need, such
        as cpen221.mp3.fsftbuffer.
     */

    private static ScheduledExecutorService scheduledExecutorService;
    private static WikiMediator wikiMediator1;
    private static String desirePath = "Trampling studies " +
            "have consistently documented that impacts on soil" +
            " and vegetation occur rapidly with initial use of desire paths.";
    private static String desirePathTitle = "Desire path";

    @BeforeAll
    public static void setupTests() {
        wikiMediator1 = new WikiMediator(10, 30);
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Test
    public void writeTest() {
        WikiMediator wiki = new WikiMediator(10, 10);
        wiki.write();
    }

    @Test
    public void newTest() {
        WikiMediator wiki = new WikiMediator(10, 10);
        List<String> expected = List.of(Integer.toString(8),
                Integer.toString(6), Integer.toString(4),
                Integer.toString(2));
        Assertions.assertEquals(expected, wiki.zeitgeist(5));
    }

    @Test
    public void searchRequestTest() {
        SearchRequest searchRequest = new SearchRequest(10L,"me", 10);
        Gson json = new Gson();
        String output = json.toJson(searchRequest);
    }

    @Test
    public void task3MoreRequestsThenZeitgeist() {
        for (int i = 1; i < 10; i++) {
            wikiMediator1.search(Integer.toString(i), 1);
            if ((i % 2) == 0) {
                for (int j = i/2; j >= 0; j--) {
                    wikiMediator1.search(Integer.toString(i), 1);
                }
            }
        }
        List<String> expected = List.of(Integer.toString(8),
                Integer.toString(6), Integer.toString(4),
                Integer.toString(2));
        Assertions.assertEquals(expected, wikiMediator1.zeitgeist(5));
        wikiMediator1.write();
    }
}
