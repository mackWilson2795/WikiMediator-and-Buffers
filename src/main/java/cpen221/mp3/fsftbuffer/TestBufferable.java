package cpen221.mp3.fsftbuffer;


import cpen221.mp3.wikimediator.Bufferable.Bufferable;

public class TestBufferable implements Bufferable {
    // TODO: for testing only...
    String id;
    public TestBufferable(String id) {
        this.id = id;
    }
    public String id(){
        return id;
    }
}
