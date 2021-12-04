package cpen221.mp3.wikimediator.Requests;

public class ZeitgeistRequest extends GeneralRequest {
    private int limit;
    public ZeitgeistRequest(Long time, int id, int limit){
        super(time, RequestType.ZEITGEIST, id);
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }
}
