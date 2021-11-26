package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.Bufferable;

public class WikiPage implements Bufferable {
    private String title;
    private String text;

    public WikiPage(String title, String text){
        this.title = title;
        this.text = text;
    }

    public String id() {
        return title;
    }


}
