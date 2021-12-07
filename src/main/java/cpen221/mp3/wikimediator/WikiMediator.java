package cpen221.mp3.wikimediator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import cpen221.mp3.fsftbuffer.Bufferable;
import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.NotFoundException;
import cpen221.mp3.wikimediator.Requests.*;
import kotlin.jvm.Synchronized;
import org.fastily.jwiki.core.*;

public class WikiMediator {

    private static final int MS_CONVERSION = 1000;
    private static final int DEFAULT_TIME_WINDOW_IN_SECONDS = 30;

    private final Wiki wiki = new Wiki.Builder().build();
    private final FSFTBuffer<WikiPage> cache;
    private final ConcurrentHashMap<String, Integer> countMap;
    private final SortedSet<Request> allRequests;

// add new method requests for all methods
    /* TODO: Implement this datatype

        You must implement the methods with the exact signatures
        as provided in the statement for this mini-project.

        You must add method signatures even for the methods that you
        do not plan to implement. You should provide skeleton implementation
        for those methods, and the skeleton implementation could return
        values like null.

     */

    public WikiMediator(int capacity, int stalenessInterval) {
        cache = new FSFTBuffer<>(capacity, stalenessInterval);
        allRequests = Collections.synchronizedSortedSet(read());
        countMap = append(allRequests);
    }

    private ConcurrentHashMap<String, Integer> append(SortedSet<Request> requests) { // make this return a new map and take in a set
        ConcurrentHashMap<String, Integer> keepCount = new ConcurrentHashMap<>();
        for (Request request : requests) {
            if (request.getType() == RequestType.GET_PAGE || request.getType() == RequestType.SEARCH) {
                String query = request.getQuery().get(0);
                if (keepCount.containsKey(query)) {
                    Integer count = keepCount.get(query);
                    keepCount.put(query, ++count);
                } else {
                    keepCount.put(query, 1);
                }
            }
        }
        return keepCount;
    }

    private SortedSet<Request> read() { //file reader how does Gson handle nested objects, parser Api
        SortedSet<Request> requests = new TreeSet<>();
        Gson json = new Gson();
        String bye = json.fromJson("Hello", String.class);
        return new TreeSet<Request>();
    }

    private void write() {
        //""
    }

    public List<String> search(String query, int limit){
        synchronized (allRequests) {
            allRequests.add(new SearchRequest(System.currentTimeMillis() / MS_CONVERSION, query, limit));
        }
        synchronized (countMap) {
            if(countMap.containsKey(query)) {
                int count = countMap.get(query);
                countMap.put(query, ++count);
            } else {
                countMap.put(query, 1);
            }
        }

        ArrayList<String> pageTitles = new ArrayList<>();
        pageTitles = wiki.search(query,limit);
        return pageTitles;
    }

    public String getPage(String pageTitle){
        synchronized (allRequests) {
            allRequests.add(new PageRequest(System.currentTimeMillis() / MS_CONVERSION, pageTitle));
        }
        synchronized (countMap) {
            if(countMap.containsKey(pageTitle)) {
                int count = countMap.get(pageTitle);
                countMap.put(pageTitle, ++count);
            } else {
                countMap.put(pageTitle, 1);
            }
        }
        try {
            WikiPage page = (WikiPage) cache.get(pageTitle);
            return page.getText();
        } catch (NotFoundException e) {
            String text = wiki.getPageText(pageTitle);
            synchronized (cache) {
                cache.put(new WikiPage(pageTitle, text));
            }
            return text;
        }
    }

    public List<String> zeitgeist(int limit){

        synchronized (allRequests) {
            allRequests.add(new ZeitgeistRequest(System.currentTimeMillis() / MS_CONVERSION, limit));
        }

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

        synchronized (allRequests) {
            allRequests.add(new TrendingRequest(System.currentTimeMillis() / MS_CONVERSION, timeLimitInSeconds, maxItems));
        }

        SortedSet<Request> requestsInTimeWindow;
        synchronized (allRequests) { //inclusive?
            requestsInTimeWindow = allRequests.subSet(new ReferenceRequest(System.currentTimeMillis() / MS_CONVERSION), new ReferenceRequest((System.currentTimeMillis() / MS_CONVERSION) - timeLimitInSeconds));
        }
        return trim(count(append(requestsInTimeWindow)), maxItems);
    }

    public int windowedPeakLoad(int timeWindowInSeconds) {
        Long referenceTime = 0L;
        int timeWindow = timeWindowInSeconds;
        int maxSize = 0;
        LinkedList<Request> window = new LinkedList<>();
        LinkedList<Request> requestList = new LinkedList<>();
        synchronized (allRequests) {
            allRequests.add(new WindowedPeakLoadRequest(System.currentTimeMillis() / MS_CONVERSION,
                    timeWindowInSeconds));
            requestList.addAll(allRequests);
        }

        referenceTime = requestList.peek().getTimeInSeconds();

        while(!requestList.isEmpty() && (window.peekLast().getTimeInSeconds() - referenceTime) < timeWindowInSeconds){
            window.addLast(requestList.pop());
        } //TODO: find a way to not repeat

        maxSize = window.size();

        while(!requestList.isEmpty()){
            while(!window.isEmpty() && Objects.equals(window.peek().getTimeInSeconds(), referenceTime)) {
                window.pop();
            }
            referenceTime = window.peek().getTimeInSeconds();
            while(!requestList.isEmpty() && requestList.peek().getTimeInSeconds() - referenceTime < timeWindow){
               window.addLast(requestList.pop());
            }
            if (window.size() > maxSize) {
               maxSize = window.size();
            }
        }
        return maxSize;
    }


    public int windowedPeakLoad(){

        synchronized (allRequests) {
            allRequests.add(new WindowedPeakLoadRequest(System.currentTimeMillis() / MS_CONVERSION, DEFAULT_TIME_WINDOW_IN_SECONDS));
        }

        return windowedPeakLoad(DEFAULT_TIME_WINDOW_IN_SECONDS);
    }
}
