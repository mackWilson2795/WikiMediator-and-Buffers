package cpen221.mp3.wikimediator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.wikimediator.Requests.ReferenceRequest;
import cpen221.mp3.wikimediator.Requests.Request;
import cpen221.mp3.wikimediator.Requests.RequestType;
import kotlin.jvm.Synchronized;
import org.fastily.jwiki.core.*;

public class WikiMediator {

    private static final int MS_CONVERSION = 1000;

    private final Wiki wiki;
    private final FSFTBuffer<WikiPage> cache;
    private final ConcurrentHashMap<String, Integer> countMap;
    private final SortedSet<Request> allRequests;


    /* TODO: Implement this datatype

        You must implement the methods with the exact signatures
        as provided in the statement for this mini-project.

        You must add method signatures even for the methods that you
        do not plan to implement. You should provide skeleton implementation
        for those methods, and the skeleton implementation could return
        values like null.

     */

    public WikiMediator(int capacity, int stalenessInterval) {
        wiki = new Wiki.Builder().build();
        cache = new FSFTBuffer<>(capacity, stalenessInterval);
        countMap = new ConcurrentHashMap<>();
        allRequests = Collections.synchronizedSortedSet(new TreeSet<Request>());
        read();


    }

    private ConcurrentHashMap<String, Integer> append(SortedSet<Request> requests) { // make this return a new map and take in a set
        synchronized (countMap){
            synchronized (allRequests){
                for (Request request :allRequests) {
                    if(request.getType() == RequestType.GETPAGE || request.getType() == RequestType.SEARCH){

                    }
                }
            }
        }
        return null;
    }

    private void read() {
        //make iterations over allRequest syncronized
    }

    private void write() {
        //""
    }

    public List<String> search(String query, int limit) {
        ArrayList<String> pageTitles = new ArrayList<>();
        pageTitles = wiki.search(query,limit);
        return pageTitles;
    }

    public String getPage(String pageTitle) {
        String text = wiki.getPageText(pageTitle);
        cache.put(new WikiPage(pageTitle, text));
        return text;
    }

    public List<String> zeitgeist(int limit) {
        ArrayList<String> queries;
        synchronized (countMap) {
            queries = count(countMap);
        }
        return trim(queries, limit);
    }

    public ArrayList<String> count(ConcurrentHashMap<String, Integer> toCount) {
        return toCount.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<String> trim(ArrayList<String> toTrim, int desiredSize) {
        if (toTrim.size() > desiredSize){
            return toTrim.subList(0, desiredSize-1);
        }
        return toTrim;
    }

    public List<String> trending(int timeLimitInSeconds, int maxItems) {
        SortedSet<Request> requestsInTimeWindow;
        synchronized (allRequests) {
            requestsInTimeWindow = allRequests.subSet(new ReferenceRequest(System.currentTimeMillis() / MS_CONVERSION), new ReferenceRequest((System.currentTimeMillis() / MS_CONVERSION) - timeLimitInSeconds));
        }
        return trim(count(append(requestsInTimeWindow)), maxItems);
    }

    public int windowedPeakLoad(int timeWindowInSeconds){
        return -1;
    }

    public int windowedPeakLoad(){
        return -1;
    }
}
