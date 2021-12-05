package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRequest implements Request, Comparable<Request> {

    private final RequestType requestType;
    private final ArrayList<String> query = new ArrayList<>();
    private final Long timeInSeconds;
    private final int id;

    public AbstractRequest (Long timeInSeconds, int id, RequestType requestType) {
        this.timeInSeconds = timeInSeconds;
        this.requestType = requestType;
        this.id = id;
    }

    public AbstractRequest(Long timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
        id = -1;
        requestType = RequestType.REFERENCE;
    }

    public Long getTimeInSeconds(){
        return timeInSeconds;
    }

    public RequestType getType(){
        return requestType;
    }

    public int getId() {
        return id;
    }

    public List<String> getQuery() {
        return query;
    }

    @Override
    public int compareTo(@NotNull Request o) {
        return this.getTimeInSeconds().compareTo(o.getTimeInSeconds());
    }
}
