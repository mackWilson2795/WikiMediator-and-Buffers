package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShortestPathRequest implements Request, Comparable<Request> {
   private final ArrayList<String> query;
   private final Long timeInSeconds;
   private final int id;

    public ShortestPathRequest(Long timeInSeconds, int id, String pageTitle1, String pageTitle2, int timeOut){
        query = new ArrayList<>(3);
        this.query.add(pageTitle1);
        this.query.add(pageTitle2);
        this.query.add(String.valueOf(timeOut));
        this.id = id;
        this.timeInSeconds = timeInSeconds;

    }


    public Long getTimeInSeconds() {
        return timeInSeconds;
    }


    public int getId() {
        return id;
    }


    public List<String> getQuery() {
        return query;
    }


    public RequestType getType() {
        return RequestType.SHORTESTPATH;
    }

    @Override
    public int compareTo(@NotNull Request o) {
        return (this.getTimeInSeconds().compareTo(o.getTimeInSeconds()));
    }
}
