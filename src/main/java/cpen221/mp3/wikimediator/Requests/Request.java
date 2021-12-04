package cpen221.mp3.wikimediator.Requests;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Request extends Comparable<Request>{

    public RequestType getType();
    public List<String> getQuery();
    public Long getTimeInSeconds();
    public int getId();
    @Override
    int compareTo(@NotNull Request o);
}
