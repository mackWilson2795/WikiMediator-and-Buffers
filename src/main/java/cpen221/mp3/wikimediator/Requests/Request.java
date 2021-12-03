package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class Request implements Comparable<Request> {
    private BigInteger requestTimeInSeconds; // do we forsure have to use big integer?
    RequestType requestType;
    int id;
    public Request(BigInteger time, RequestType requestType, int id){
        requestTimeInSeconds = time;
        this.requestType = requestType;
        this.id = id;
    }

    public BigInteger getTime(){
        return requestTimeInSeconds;
    }

    public RequestType getType(){
        return requestType;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(@NotNull Request o) {
        return this.getTime().compareTo(o.getTime());
    }
}
