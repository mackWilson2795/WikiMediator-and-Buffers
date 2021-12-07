package cpen221.mp3.wikimediator.Requests;

import java.util.ArrayList;

public class WindowedPeakLoadRequest extends AbstractRequest {

    private final ArrayList<String> query = new ArrayList<>(1);

    public WindowedPeakLoadRequest (Long timeInSeconds, int timeWindowInSeconds) {
        super(timeInSeconds, RequestType.WINDOWED_PEAK_LOAD);
        query.add(String.valueOf(timeWindowInSeconds));
    }

    @Override
    public ArrayList<String> getQuery() {
        return query;
    }
}
