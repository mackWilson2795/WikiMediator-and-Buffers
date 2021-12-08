package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRequest implements Request, Comparable<Request> {

    static int requestNumber = 2000;

    private final RequestType requestType;
    private final transient ArrayList<String> queries = new ArrayList<>();
    private final Long timeInSeconds;

    /*
     * Abstraction Function :
     *
     * requestType = the type of request this object represents
     *
     * queries = The list of all arguments used for this request
     *
     * timeInSeconds = the time in seconds at which this request was processed
     * */

    /**
     * Default constructor for a Request, creates a request of a given type
     * occurring at a given time
     *
     * @param timeInSeconds the time in seconds at which the request was processed
     * @param requestType the type of the request
     */
    public AbstractRequest (Long timeInSeconds, RequestType requestType) {
        this.timeInSeconds = timeInSeconds;
        this.requestType = requestType;
    }

    /**
     * Retrieves the time at which this request was processed
     *
     * @return the time in seconds at which this request was processed
     */
    public Long getTimeInSeconds(){
        return timeInSeconds;
    }

    /**
     * Retrieves the type of this request
     *
     * @return a RequestType specifying the type of this request
     */
    public RequestType getType(){
        return requestType;
    }

    /**
     * Retrieves the arguments used for this request
     *
     * @return a List of arguments used for this request
     */
    public List<String> getQueries() {
        return queries;
    }

    /**
     * Compares another Request to this Request
     *
     * @param o the Request to be compared to this Request
     * @return 1 if this Request is greater than or equal to Request o,
     *         -1 if this Request is less than Request o
     */
    @Override
    public int compareTo(@NotNull Request o) {
        if (this.getTimeInSeconds().compareTo(o.getTimeInSeconds()) == 0){
            return 1;
        } else {
            return this.getTimeInSeconds().compareTo(o.getTimeInSeconds());
        }
    }
}
