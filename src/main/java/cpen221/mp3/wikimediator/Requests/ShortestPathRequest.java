package cpen221.mp3.wikimediator.Requests;

import java.math.BigInteger;

public class ShortestPathRequest extends Request{
    private String page1;
    private String page2;
    private int timeOut;

    public ShortestPathRequest(BigInteger time, int id, String pageTitle1, String pageTitle2, int timeOut){
        super(time, RequestType.SHORTESTPATH, id);
        page1 = pageTitle1;
        page2 = pageTitle2;
        this.timeOut = timeOut;
    }

    public String getPage1() {
        return page1;
    }

    public String getPage2() {
        return page2;
    }
}
