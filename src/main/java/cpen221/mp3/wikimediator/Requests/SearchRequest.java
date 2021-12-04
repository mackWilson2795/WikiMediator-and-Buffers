package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchRequest implements Request, Comparable<Request> {
    private final ArrayList<String> query;
    private final Long timeInSeconds;
    private final int id;
    public SearchRequest(Long timeInSeconds, int id, String query, int limit){
        this.query = new ArrayList<String>(2);
        this.query.add(query);
        this.query.add(String.valueOf(limit));
        this.timeInSeconds = timeInSeconds;
        this.id = id;
    }

    public List<String> getQuery(){
        return query;
    }

    public Long getTimeInSeconds() {
        return timeInSeconds;
    }


    public int getId() {
        return id;
    }


    public RequestType getType() {
        return RequestType.SEARCH;
    }

    @Override
    public int compareTo(@NotNull Request o) {
        return (this.getTimeInSeconds().compareTo(o.getTimeInSeconds()));
    }
}
