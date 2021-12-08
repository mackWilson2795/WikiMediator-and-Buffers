package cpen221.mp3.wikimediator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.google.gson.*;
import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.Exceptions.NotFoundException;
import cpen221.mp3.wikimediator.Bufferable.WikiPage;
import cpen221.mp3.wikimediator.Requests.*;
import org.fastily.jwiki.core.*;

public class WikiMediator {

    private static final int MS_CONVERSION = 1000;
    private static final int DEFAULT_TIME_WINDOW_IN_SECONDS = 30;

    private final File localDir = new File("local");
    private final File allRequestsFile = new File("local/allRequests.txt");
    private final File countMapFile = new File("local/countMap.txt");
    private final Wiki wiki = new Wiki.Builder().build();
    private final FSFTBuffer<WikiPage> cache;
    private final ConcurrentHashMap<String, Integer> countMap = new ConcurrentHashMap<>();
    private final SortedSet<Request> allRequests = new TreeSet<>();

    /*
     * Abstraction Function :
     *
     * countMap = The set of all strings used in search and getPage methods,
     *            where each string is mapped to a number representing the
     *            total amount of times it has been used in search and getPage.
     *            This map will store the information pertaining to any
     *            wikiMediator instantiated while maintaining the same Local
     *            Directory.
     *
     * allRequests = The set of all requests made to any wikiMediators made while
     *                maintaining the same Local Directory.
     * */

    /*
     * Rep - Invariant :
     *
     *                  The sum of the values stored in countMap should always be
     *                  equal to the total number of SearchRequest and PageRequest
     *                  objects stored in allRequests.
     * */

    public boolean checkRep() {
        int totalCountMap = countMap.values().stream().mapToInt(integer -> integer).sum();
        int totalCountSet = (int) allRequests.stream().filter(request -> request.getType() == RequestType.SEARCH || request.getType() == RequestType.GET_PAGE).count();
        if(totalCountSet == totalCountMap){
            return true;
        }
        return false;
    }

    /**
     * Creates a new instance of WikiMediator.
     * @param capacity the capacity of the cache in the WikiMediator
     * @param stalenessInterval the amount of time in seconds a wiki page will
     *                          be cached in the wikiMediator after getPage is
     *                          called.
     */
    public WikiMediator(int capacity, int stalenessInterval) {
        cache = new FSFTBuffer<>(capacity, stalenessInterval);
        read();
    }

    /**
     * Finds a list of Wiki page titles matching the given query
     *
     * @param query the given query
     * @param limit the size of the list to be returned
     * @return a list of wiki page titles, of size limit, that match the given query
     */
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

    /**
     * Finds the text of the Wiki page corresponding to the given page title
     *
     * @param pageTitle The given page title
     * @return a String containing the text of the Wikipedia
     *         page matching the given String pageTitle. If the
     *         page does not exist, returns an empty String.
     */
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

    /**
     * Creates a list of the nth most frequent queries used in getPage and search, ordered from
     * most to least frequent
     *
     * @param limit the given size of the list
     * @return a list of the most frequent queries used in getPage and search,
     *         of size limit, ordered from most to least frequent
     */
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

    /**
     * Creates a list of the nth most frequent queries used in getPage and search in the past specified
     * amount of time, the list is ordered from most to least frequent
     *
     * @param timeLimitInSeconds the specified amount of time in seconds
     * @param maxItems the given size of the list
     * @returna list of the most frequent queries used in getPage and search
     *          in the past timeLimitInSeconds, of size limit, ordered from
     *          most to least frequent
     */
    public List<String> trending(int timeLimitInSeconds, int maxItems) {

        synchronized (allRequests) {
            allRequests.add(new TrendingRequest(System.currentTimeMillis() / MS_CONVERSION, timeLimitInSeconds, maxItems));
        }

        SortedSet<Request> requestsInTimeWindow;
        synchronized (allRequests) { //inclusive?
            requestsInTimeWindow = allRequests.subSet(new ReferenceRequest(System.currentTimeMillis() / MS_CONVERSION - timeLimitInSeconds), new ReferenceRequest((System.currentTimeMillis() / MS_CONVERSION)));
        }
        return trim(count(append(requestsInTimeWindow)), maxItems);
    }

    /**
     * Finds the maximum amount of requests made in a given time window
     *
     * @param timeWindowInSeconds the given time window in seconds
     * @return an integer corresponding to the maximum amount of requests made in any time window
     *         of size timeWindowInSeconds.
     */
    public int windowedPeakLoad(int timeWindowInSeconds) {
        long referenceTime = 0;
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
        window.addLast(requestList.pop());

        while(!requestList.isEmpty() && (requestList.peek().getTimeInSeconds().longValue() - referenceTime) < timeWindowInSeconds){
            window.addLast(requestList.pop());
        }

        maxSize = window.size();

        while(!requestList.isEmpty()){
            while(!window.isEmpty() && Objects.equals(window.peek().getTimeInSeconds(), referenceTime)) {
                window.pop();
            }
            if (!window.isEmpty()) {
                referenceTime = window.peek().getTimeInSeconds();
            } else {
                referenceTime = requestList.peek().getTimeInSeconds();
            }

            while(!requestList.isEmpty() && requestList.peek().getTimeInSeconds() - referenceTime < timeWindow){
               window.addLast(requestList.pop());
            }
            if (window.size() > maxSize) {
               maxSize = window.size();
            }
        }
        return maxSize;
    }

    /**
     * Finds the maximum amount of requests made in a 30-second time window
     *
     * @return an integer corresponding to the maximum amount of requests made
     * in any 30-second time window
     */
    public int windowedPeakLoad(){
        return windowedPeakLoad(DEFAULT_TIME_WINDOW_IN_SECONDS);
    }

    /**
     * Prepares an instance of a WikiMediator to be terminated, by
     * saving the data collected by the WikiMediator to a local Directory
     */
    public void close() {
        this.write();
    }

    /**
     * Helper function which writes the data collected by the WikiMediator to a
     * local Directory
     */
    private void write() {
        Gson json = new Gson();
        try {
            Files.deleteIfExists(Path.of(allRequestsFile.getPath()));
            Files.deleteIfExists(Path.of(countMapFile.getPath()));

            allRequestsFile.createNewFile();
            countMapFile.createNewFile();

            FileWriter allRequestWriter =  new FileWriter(allRequestsFile.getPath());
            FileWriter countMapWriter =  new FileWriter(countMapFile.getPath());

            String allRequestAsJSON = json.toJson(allRequests);
            String countMapAsJSON = json.toJson(countMap);

            allRequestWriter.write(allRequestAsJSON);
            countMapWriter.write(countMapAsJSON);

            allRequestWriter.close();
            countMapWriter.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Helper function, restores the WikiMediator data from a local Directory to a new instance
     * of WikiMediator
     */
    private void read() {
        Gson json = new Gson();

        try {
            if (allRequestsFile.exists()) {
                BufferedReader allRequestsReader = new BufferedReader(new FileReader(allRequestsFile));
                JsonArray allRequestsJsonArray = JsonParser.parseReader(allRequestsReader).getAsJsonArray();

                Iterator<JsonElement> iterator = allRequestsJsonArray.iterator();

                while (iterator.hasNext()) {
                    JsonObject next = iterator.next().getAsJsonObject();
                    allRequests.add(parseRequest(next));
                }
                allRequestsReader.close();
            }
            if (countMapFile.exists()) {
                BufferedReader countMapReader = new BufferedReader(new FileReader(countMapFile));
                HashMap<String, Double> tempMap =
                        new HashMap<String, Double>(json.fromJson(countMapReader, HashMap.class));
                for (String key : tempMap.keySet()) {
                    Integer nextVal = tempMap.get(key).intValue();
                    countMap.put(key, nextVal);
                }
                countMapReader.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Helper function which parses a given JsonObject into a request object
     * @param next the given JsonObject
     *
     * @return a request object
     */
    private Request parseRequest(JsonObject next) {
        JsonArray queriesJson = next.getAsJsonArray("queries");
        Long timeInSeconds = next.get("timeInSeconds").getAsLong();
        switch (next.get("requestType").getAsString()) {
            case "SEARCH" :
                return new SearchRequest(timeInSeconds,
                        queriesJson.get(0).getAsString(), queriesJson.get(1).getAsInt());
            case "SHORTEST_PATH" :
                return new ShortestPathRequest(timeInSeconds, queriesJson.get(0).toString(),
                        queriesJson.get(1).getAsString(), queriesJson.get(2).getAsInt());
            case "ZEITGEIST" :
                return new ZeitgeistRequest(timeInSeconds, queriesJson.get(0).getAsInt());
            case "TRENDING" :
                return new TrendingRequest(timeInSeconds,queriesJson.get(0).getAsInt(),
                        queriesJson.get(1).getAsInt());
            case "GET_PAGE" :
                return new PageRequest(timeInSeconds, queriesJson.get(0).getAsString());
            default :
                return new WindowedPeakLoadRequest(timeInSeconds, queriesJson.get(0).getAsInt());
        }
    }

    /**
     * Helper function which takes in a set of requests and populates a map, which maps
     * the queries used in getPage and Search, to a number representing the number of times
     * getPage or Search are called using that specific query
     *
     * @param requests the given set of requests
     * @return the populated map
     */
    private ConcurrentHashMap<String, Integer> append(SortedSet<Request> requests) { // make this return a new map and take in a set
        ConcurrentHashMap<String, Integer> keepCount = new ConcurrentHashMap<>();
        for (Request request : requests) {
            if (request.getType() == RequestType.GET_PAGE || request.getType() == RequestType.SEARCH) {
                String query = request.getQueries().get(0);
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


    public List<String> shortestPath(String pageTitle1, String pageTitle2, int timeout) throws TimeoutException {


        List<ArrayList<String>> queue = new ArrayList<ArrayList<String>>();
        List<String> path = new ArrayList<String>();
        Set<String> adjacentPages;
        ArrayList<String> tempList = new ArrayList<String>();
        String node;



        queue.add(new ArrayList<String>());
        queue.get(0).add(pageTitle1);

        while (queue.size() > 0) {
            path = queue.get(0);
            queue.remove(0);
            node = path.get(path.size()-1);

            if(node.compareTo(pageTitle2) == 0){
                return path;
            }

            adjacentPages = new HashSet<String>();
            adjacentPages = findAdjacents(node);

            for(String page: adjacentPages){
                tempList = new ArrayList<String>(path);
                tempList.add(page);
                queue.add(tempList);
            }

        }

        return null;
    }


    private TreeSet<String> findAdjacents(String pageTitle){

        TreeSet<String> adjPages = new TreeSet<String>();


        return null;
    }

    /**
     * Helper function which trims a given list from its current size to the given size.
     *
     * @param toTrim the given list
     * @param desiredSize the given size
     * @return the trimmed list
     */
    public List<String> trim(ArrayList<String> toTrim, int desiredSize) {
        if (toTrim.size() > desiredSize){
            return toTrim.subList(0, desiredSize-1);
        }
        return toTrim;
    }

    /**
     * Helper function which produces an ordered list of keys from a map in descending order
     * according to the value of their keys in the given map.
     *
     * @param toCount the given map
     * @return the list of ordered keys
     */
    private ArrayList<String> count(ConcurrentHashMap<String, Integer> toCount) {
        ArrayList<String> reverseOrdered = toCount.entrySet().stream().
                sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.reverse(reverseOrdered);
        return reverseOrdered;
    }
}
