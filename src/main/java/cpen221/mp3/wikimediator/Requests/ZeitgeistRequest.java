package cpen221.mp3.wikimediator.Requests;

import java.util.ArrayList;
import java.util.List;

public class ZeitgeistRequest extends AbstractRequest {

    private final ArrayList<String> queries = new ArrayList<>(1);

    /*
     * Abstraction Function :
     *
     * queries = The list of all arguments used for this request
     * */

    public ZeitgeistRequest(Long time, int limit) {
        super(time, RequestType.ZEITGEIST);
        queries.add(String.valueOf(limit));
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
