package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRequest implements Request, Comparable<Request> {

    static int requestNumber = 2000;

    private final RequestType requestType;
    private final transient ArrayList<String> queries = new ArrayList<>();
    private final Long timeInSeconds;

    public AbstractRequest (Long timeInSeconds, RequestType requestType) {
        this.timeInSeconds = timeInSeconds;
        this.requestType = requestType;
    }

    public Long getTimeInSeconds(){
        return timeInSeconds;
    }

    public RequestType getType(){
        return requestType;
    }

    public List<String> getQueries() {
        return queries;
    }

    @Override
    public int compareTo(@NotNull Request o) {
        if (this.getTimeInSeconds().compareTo(o.getTimeInSeconds()) == 0){
            return 1;
        } else {
            return this.getTimeInSeconds().compareTo(o.getTimeInSeconds());
        }
    }
}
