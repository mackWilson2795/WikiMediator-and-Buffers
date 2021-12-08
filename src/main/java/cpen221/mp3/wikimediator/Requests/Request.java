package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Request extends Comparable<Request>{

    /**
     * Retrieves the type of this request
     *
     * @return a RequestType specifying the type of this request
     */
    public RequestType getType();

    /**
     * Retrieves the arguments given to the method call for this request
     *
     * @return a List of arguments given to the method call for this request
     */
    public List<String> getQueries();

    /**
     * Retrieves the time at which this request was processed
     *
     * @return the time in seconds at which this request was processed
     */
    public Long getTimeInSeconds();

    /**
     * Compares another Request to this Request
     *
     * @param o the Request to be compared to this Request
     * @return 1 if this Request is greater than or equal to Request o,
     *         -1 if this Request is less than Request o
     */
    @Override
    int compareTo(@NotNull Request o);
}
