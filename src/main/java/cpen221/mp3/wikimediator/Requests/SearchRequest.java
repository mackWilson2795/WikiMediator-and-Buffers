package cpen221.mp3.wikimediator.Requests;

import java.math.BigInteger;

public class SearchRequest extends Request {
    private String query;
    private int limit;
    public SearchRequest(BigInteger time, int id, String query, int limit){
        super(time, RequestType.SEARCH, id);
        this.query = query;
        this.limit = limit;
    }

    public String getQuery(){
        return query; //still not clear on whether this needs a defensive copy or not?
    }

    public int getLimit() {
        return limit;
    }
}
