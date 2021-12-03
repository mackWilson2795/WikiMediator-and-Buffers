package cpen221.mp3.wikimediator.Requests;

import java.math.BigInteger;

public class PageRequest extends Request {
    private String pageTitle;
    public PageRequest(BigInteger time, int id, String title){
        super(time, RequestType.GETPAGE, id);
        pageTitle = title;
    }
    public String getPageTitle(){
        return pageTitle;
    }
}
