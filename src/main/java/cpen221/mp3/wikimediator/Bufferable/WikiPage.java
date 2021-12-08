package cpen221.mp3.wikimediator.Bufferable;

import cpen221.mp3.wikimediator.Bufferable.Bufferable;
import org.fastily.jwiki.core.Wiki;

public class WikiPage implements Bufferable {
    private String title;
    private String text;

    /*
     * Abstraction Function :
     *
     * title = the title of the Wiki page
     *
     * text = the text of the Wiki page
     *
     * */

    /*
     * Rep - Invariant :
     *
     * calling .getPageText(title) on a wiki object should return text
     *
     * */

    public boolean checkRep() {
        Wiki wiki = new Wiki.Builder().build();
        if (wiki.getPageText(title).equals(text)) {
            return true;
        }
        return false;
    }

    /**
     * Creates a new WikiPage given a title and text
     *
     * @param title the given title
     * @param text the given text
     */
    public WikiPage(String title, String text){
        this.title = title;
        this.text = text;
    }

    /**
     * Retrieves the title of the WikiPage
     *
     * @return the title of the WikiPage
     */
    public String id() {
        return title;
    }

    /**
     * Retrieves the text of the WikiPage
     *
     * @return the text of the WikiPage
     */
    public String getText() {
        return text;
    }
}
