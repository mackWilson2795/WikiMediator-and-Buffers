package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GeneralRequest implements Comparable<GeneralRequest>, Request {
    private final Long timeInSeconds;
    private final ArrayList<String> query;
    private final int id;

    public GeneralRequest(Long time, RequestType requestType, int id){
        requestTimeInSeconds = time;
        this.requestType = requestType;
        this.id = id;
    }
    //make another constructor that doesnt take enum

    public Long getTime(){
        return requestTimeInSeconds;
    }

    public RequestType getType(){
        return requestType;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(@NotNull GeneralRequest o) {
        return this.getTime().compareTo(o.getTime());
    }
}
