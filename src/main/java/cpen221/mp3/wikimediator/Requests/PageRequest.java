package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PageRequest extends AbstractRequest {

    private final ArrayList<String> query = new ArrayList<>(1);

    public PageRequest (Long timeInSeconds, String title) {
        super (timeInSeconds, RequestType.GET_PAGE);
        query.add(title);
    }

    @Override
    public List<String> getQuery() {
        return query;
    }

}
