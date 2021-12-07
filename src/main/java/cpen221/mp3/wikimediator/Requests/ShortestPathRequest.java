package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShortestPathRequest extends AbstractRequest {

    private final ArrayList<String> query = new ArrayList<>(3);

    public ShortestPathRequest(Long timeInSeconds, String pageTitle1, String pageTitle2, int timeOut) {
        super(timeInSeconds, RequestType.SHORTEST_PATH);
        query.add(pageTitle1);
        query.add(pageTitle2);
        query.add(String.valueOf(timeOut));
    }

    @Override
    public List<String> getQuery() {
        return query;
    }

}
