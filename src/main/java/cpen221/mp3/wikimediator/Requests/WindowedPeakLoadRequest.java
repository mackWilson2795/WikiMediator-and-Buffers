package cpen221.mp3.wikimediator.Requests;

import java.util.ArrayList;

public class WindowedPeakLoadRequest extends AbstractRequest {

    private final ArrayList<String> queries = new ArrayList<>(1);

    /*
     * Abstraction Function :
     *
     * queries = The list of all arguments used for this request
     * */

    public WindowedPeakLoadRequest (Long timeInSeconds, int timeWindowInSeconds) {
        super(timeInSeconds, RequestType.WINDOWED_PEAK_LOAD);
        queries.add(String.valueOf(timeWindowInSeconds));
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
