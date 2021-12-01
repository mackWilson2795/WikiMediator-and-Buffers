package cpen221.mp3.wikimediator;


import org.jetbrains.annotations.NotNull;

public class StringCounter implements Comparable<StringCounter>{
    private String id;
    private int counter;
    public StringCounter(String id, int count){
      id = id;
       counter = count;
    }
    public void newInstance() {
        ++counter;
    }

    public int getCounter(){
       return counter;
    }

    public String getId() {
        return id;
    }

    @Override
    public int compareTo(@NotNull StringCounter o) {
        return Integer.compare(this.getCounter(), o.getCounter());
    }
}
