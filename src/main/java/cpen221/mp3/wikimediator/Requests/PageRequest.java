package cpen221.mp3.wikimediator.Requests;

import java.util.ArrayList;
import java.util.List;

public class PageRequest extends AbstractRequest {

    private final ArrayList<String> queries = new ArrayList<>(1);

    public PageRequest (Long timeInSeconds, String title) {
        super (timeInSeconds, RequestType.GET_PAGE);
        queries.add(title);
    }

    @Override
    public List<String> getQueries() {
        return queries;
    }

}
