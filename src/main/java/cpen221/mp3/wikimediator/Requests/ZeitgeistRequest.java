package cpen221.mp3.wikimediator.Requests;

import java.util.ArrayList;
import java.util.List;

public class ZeitgeistRequest extends AbstractRequest {

    private final ArrayList<String> queries = new ArrayList<>(1);

    public ZeitgeistRequest(Long time, int limit) {
        super(time, RequestType.ZEITGEIST);
        queries.add(String.valueOf(limit));
    }

    @Override
    public List<String> getQueries() {
        return queries;
    }

}
