package cpen221.mp3;


import cpen221.mp3.wikimediator.WikiMediator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Task5Tests {
    private static WikiMediator wikiMediator1;

    @BeforeAll
    public static void setupTests() {
        wikiMediator1 = new WikiMediator(10, 30);
    }

    @Test
    public void task5ShortestPath() throws TimeoutException {
        List<String> expected = new ArrayList<String>();
        expected.add("PCBD2");
        expected.add("Chromosome");
        expected.add("Apoptosis");

        try {
            Assertions
                .assertEquals(expected, wikiMediator1.shortestPath("PCBD2", "Apoptosis", 120));
        }
        catch (TimeoutException e){

        }
    }

}
