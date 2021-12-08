package cpen221.mp3.wikimediator.Requests;

import java.util.ArrayList;
import java.util.List;

public class TrendingRequest extends AbstractRequest {

    private final ArrayList<String> queries = new ArrayList<>(2);

    /*
     * Abstraction Function :
     *
     * queries = The list of all arguments used for this request
     * */

    public TrendingRequest (Long timeInSeconds, int timeLimitInSeconds, int maxItems) {
        super(timeInSeconds, RequestType.TRENDING);
        queries.add(String.valueOf(timeLimitInSeconds));
        queries.add(String.valueOf(maxItems));
    }

    /**
     * Retrieves the arguments used for this request
     *
     * @return a List of arguments used for this request
     */
    @Override
    public List<String> getQueries() {
        return queries;
    }

}
