package cpen221.mp3.wikimediator.Requests;

import java.util.ArrayList;
import java.util.List;

public class ZeitgeistRequest extends AbstractRequest {

    private final ArrayList<String> query = new ArrayList<>(1);

    public ZeitgeistRequest(Long time, int id, int limit) {
        super(time, id, RequestType.ZEITGEIST);
        query.add(String.valueOf(limit));
    }

    @Override
    public List<String> getQuery() {
        return query;
    }

}
