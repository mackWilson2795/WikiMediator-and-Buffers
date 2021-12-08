package cpen221.mp3.wikimediator.Bufferable;


import cpen221.mp3.wikimediator.Bufferable.Bufferable;

public class TestBufferable implements Bufferable {

    String id;

    public TestBufferable(String id) {
        this.id = id;
    }
    public String id(){
        return id;
    }
}
