package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PageRequest implements Request, Comparable<Request> {
    private ArrayList<String> query;
    private Long timeInSeconds;
    private int id;

    public PageRequest(Long time, int id, String title){
        query = new ArrayList<String>(1);
        query.add(title);
        this.id = id;
        timeInSeconds = time;
    }

    public int getId(){
        return id;
    }

    public ArrayList<String> getQuery() {
        return query;
    }

    public RequestType getType(){
        return RequestType.GETPAGE;
    }

    public Long getTimeInSeconds() {
        return timeInSeconds;
    }

    @Override
    public int compareTo(@NotNull Request o) {
        return (this.getTimeInSeconds().compareTo(o.getTimeInSeconds()));
    }
}
