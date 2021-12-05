package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchRequest extends AbstractRequest {

    private final ArrayList<String> query = new ArrayList<>(2);

    public SearchRequest(Long timeInSeconds, int id, String query, int limit){
        super(timeInSeconds, id, RequestType.SEARCH);
        this.query.add(query);
        this.query.add(String.valueOf(limit));
    }

    @Override
    public List<String> getQuery() {
        return query;
    }

}
