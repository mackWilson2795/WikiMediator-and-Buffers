package cpen221.mp3.wikimediator.Requests;

import java.util.ArrayList;
import java.util.List;

public class PageRequest extends AbstractRequest {

    /*
     * Abstraction Function :
     *
     * queries = The list of all arguments used for this request
     * */

    private final ArrayList<String> queries = new ArrayList<>(1);

    public PageRequest (Long timeInSeconds, String title) {
        super (timeInSeconds, RequestType.GET_PAGE);
        queries.add(title);
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
