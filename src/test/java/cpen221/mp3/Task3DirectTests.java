package cpen221.mp3;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.TestBufferable;
import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class Task3DirectTests {
    private static WikiMediator wikiMediator1;
    private static String desirePath = "Trampling studies " +
            "have consistently documented that impacts on soil" +
            " and vegetation occur rapidly with initial use of desire paths.";
    private static String desirePathTitle = "Desire path";

    @BeforeAll
    public static void setupTests() {
        wikiMediator1 = new WikiMediator(10, 30);
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
        for (int i = 0; i < 10; i++) {
            wikiMediator1.search(Integer.toString(i), 1);
            if ((i % 2) == 0) {
                for (int j = i; j >= 0; j--) {
                    wikiMediator1.search(Integer.toString(j), 1);
                }
            }
        }
        List<String> expected = List.of(Integer.toString(8),
                Integer.toString(6), Integer.toString(4),
                Integer.toString(2), Integer.toString(0));
        Assertions.assertEquals(expected, wikiMediator1.zeitgeist(5));
    }
}
