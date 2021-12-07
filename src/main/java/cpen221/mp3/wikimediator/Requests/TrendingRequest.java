package cpen221.mp3.wikimediator.Requests;

import java.util.ArrayList;
import java.util.List;

public class TrendingRequest extends AbstractRequest {

    private final ArrayList<String> query = new ArrayList<>(2);

    public TrendingRequest (Long timeInSeconds, int timeLimitInSeconds, int maxItems) {
        super(timeInSeconds, RequestType.TRENDING);
        query.add(String.valueOf(timeLimitInSeconds));
        query.add(String.valueOf(maxItems));
    }

    @Override
    public List<String> getQuery() {
        return query;
    }

}
