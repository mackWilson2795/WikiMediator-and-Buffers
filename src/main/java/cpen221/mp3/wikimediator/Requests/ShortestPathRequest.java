package cpen221.mp3.wikimediator.Requests;

import java.util.ArrayList;
import java.util.List;

public class ShortestPathRequest extends AbstractRequest {

    private final ArrayList<String> queries = new ArrayList<>(3);

    /*
     * Abstraction Function :
     *
     * queries = The list of all arguments used for this request
     * */

    public ShortestPathRequest(Long timeInSeconds, String pageTitle1, String pageTitle2, int timeOut) {
        super(timeInSeconds, RequestType.SHORTEST_PATH);
        queries.add(pageTitle1);
        queries.add(pageTitle2);
        queries.add(String.valueOf(timeOut));
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
