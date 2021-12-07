package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRequest implements Request, Comparable<Request> {

    static int requestNumber = 2000;

    private final RequestType requestType;
    private final ArrayList<String> query = new ArrayList<>();
    private final Long timeInSeconds;
    private final int identifier;

    public AbstractRequest (Long timeInSeconds, RequestType requestType) {
        this.timeInSeconds = timeInSeconds;
        this.requestType = requestType;
        this.identifier = requestNumber++;
    }

    public Long getTimeInSeconds(){
        return timeInSeconds;
    }

    public RequestType getType(){
        return requestType;
    }

    public List<String> getQuery() {
        return query;
    }

    @Override
    public int compareTo(@NotNull Request o) {
        if (this.getTimeInSeconds().compareTo(o.getTimeInSeconds()) == 0){
            return 1;
        } else {
            return this.getTimeInSeconds().compareTo(o.getTimeInSeconds());
        }
    }

    // @Override
    // public boolean equals(Object o) {
    //     if (!(o instanceof Request)) {
    //         return false;
    //     }
    //     Request request = (Request) o;
    //     if (this.getIdentifier() == request.getIdentifier()) {
    //         return true;
    //     }
    //     return false;
    // }
//
    // @Override
    // public int hashCode() {
    //     return identifier;
    // }
}
