package cpen221.mp3.wikimediator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import org.fastily.jwiki.core.*;
import org.fastily.jwiki.dwrap.*;

public class WikiMediator {
Wiki wiki;
FSFTBuffer<WikiPage> cache;
ConcurrentHashMap<String, StringCounter> countMap;



        /* TODO: Implement this datatype

        You must implement the methods with the exact signatures
        as provided in the statement for this mini-project.

        You must add method signatures even for the methods that you
        do not plan to implement. You should provide skeleton implementation
        for those methods, and the skeleton implementation could return
        values like null.

     */

    public WikiMediator(int capacity, int stalenessInterval){
        wiki = new Wiki.Builder().build();
        cache = new FSFTBuffer<>(capacity, stalenessInterval);
        countMap = new ConcurrentHashMap<>();
    }

    public List<String> search(String query, int limit){
        ArrayList<String> pageTitles = new ArrayList<>();
        pageTitles = wiki.search(query,limit);
        return pageTitles;
    }

    public String getPage(String pageTitle){
        String text = wiki.getPageText(pageTitle);
        cache.put(new WikiPage(pageTitle, text));
        return text;
    }

    public List<String> zeitgeist(int limit){
        countMap.keySet()
        return null;
    }

    public List<String> trending(int timeLimitInSeconds, int maxItems){
        return null;
    }

    public int windowedPeakLoad(int timeWindowInSeconds){
        return -1;
    }

    public int windowedPeakLoad(){
        return -1;
    }
}
