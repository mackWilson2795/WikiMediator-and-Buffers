package cpen221.mp3.wikimediator.Requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchRequest extends AbstractRequest {

    private final ArrayList<String> queries = new ArrayList<>(2);

    /*
     * Abstraction Function :
     *
     * queries = The list of all arguments used for this request
     * */

    public SearchRequest(Long timeInSeconds, String query, int limit) {
        super(timeInSeconds,
                RequestType.SEARCH);
        queries.add(query);
        queries.add(String.valueOf(limit));
    }

    /**
     * Retrieves the arguments used for this request
     *
     * @return a List of arguments used for this request
     */
   @Override
   public ArrayList<String> getQueries() {
        return queries;
   }
}
//new ArrayList<>(Arrays.asList(query, String.valueOf(limit)))