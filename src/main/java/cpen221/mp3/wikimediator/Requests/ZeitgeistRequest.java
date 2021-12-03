package cpen221.mp3.wikimediator.Requests;

import java.math.BigInteger;

public class ZeitgeistRequest extends Request{
    private int limit;
    public ZeitgeistRequest(BigInteger time, int id, int limit){
        super(time, RequestType.ZEITGEIST, id);
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }
}
