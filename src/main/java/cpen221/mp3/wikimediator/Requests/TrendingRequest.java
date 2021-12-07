package cpen221.mp3.wikimediator.Requests;

import java.util.ArrayList;
import java.util.List;

public class TrendingRequest extends AbstractRequest {

    private final ArrayList<String> queries = new ArrayList<>(2);

    public TrendingRequest (Long timeInSeconds, int timeLimitInSeconds, int maxItems) {
        super(timeInSeconds, RequestType.TRENDING);
        queries.add(String.valueOf(timeLimitInSeconds));
        queries.add(String.valueOf(maxItems));
    }

    @Override
    public List<String> getQueries() {
        return queries;
    }

}
