package cpen221.mp3.fsftbuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
public class FSFTBuffer<T extends Bufferable> {

    /* the default buffer size is 32 objects */
    public static final int DSIZE = 32;

    /* the default timeout value is 3600s */
    public static final int DTIMEOUT = 3600;

    /* Conversion factor between milliseconds and seconds */
    // TODO: Added this
    public static final int MS_CONVERSION = 1000;

    /* TODO: not a real abstraction function just me trying to understand variables
        Abstraction Function:
        lookUpMap: id -> T
        cache: T's in order
        timeOutMap: ids -> staleness time (last time touched)
        workingOn: for thread safety? current list of things being modified right now?
     */

    /* TODO: fake rep invariant
        For all String1s in lookUpMap, there should be a String2 in timeOutMap
        such that: String1 = String2.
        For all Integer
     */

    private HashMap <String, T> lookUpMap;
    private LinkedList<T> cache;//maybe queue
    private HashMap<String, Long> timeOutMap;
    private int capacity;
    private int timeout;
    private ArrayList<Integer> workingOn; // TODO: maybe should be a string?

    /**
     * Create a buffer with a fixed capacity and a timeout value.
     * Objects in the buffer that have not been refreshed within the
     * timeout period are removed from the cache.
     *
     * @param capacity the number of objects the buffer can hold
     * @param timeout  the duration, in seconds, an object should
     *                 be in the buffer before it times out
     */
    public FSFTBuffer(int capacity, int timeout) {
        this.capacity = capacity;
        this.timeout = timeout;
        lookUpMap = new HashMap();
        cache = new LinkedList();
        timeOutMap = new HashMap();
        workingOn = new ArrayList<>();
    }

    /**
     * Create a buffer with default capacity and timeout values.
     */
    public FSFTBuffer() {
        this(DSIZE, DTIMEOUT);
    }

    /**
     * Add a value to the buffer.
     * If the buffer is full then remove the least recently accessed
     * object to make room for the new object.
     */
    public boolean put(T t) {
        clean();
        int index; //Todo: make this cleaner with the whole working on thing
        workingOn.add(cache.size()); // Todo: ignore workingOn
        index = cache.size();
        if(cache.size() == capacity){
            lookUpMap.remove(cache.removeFirst().id());
            timeOutMap.remove(t.id()); // does this still remove from linked list
            workingOn.add(cache.size() - 1);
            workingOn.remove(index); // TODO: I like the idea of a *PRIVATE* "remove" method to do this
            index = cache.size() - 1;
        }
        if(lookUpMap.containsKey(t.id())) {
            return false
        }
        cache.addLast(t);
        lookUpMap.put(t.id(), t);
        timeOutMap.put(t.id(), 1000 * System.currentTimeMillis() + timeout);
        workingOn.remove(index);
        return true;
    }

    /** TODO: temp spec
     * Removes all expired entries in the FSFTBuffer.
     */
    private void clean(){
        for (String id : timeOutMap.keySet()) {
            // TODO: 1000 should be a global variable - also are we converting ms to us here? Should it be /1000?
            long time = System.currentTimeMillis()/MS_CONVERSION;
            if(timeOutMap.get(id) >= time){
                timeOutMap.remove(id);
                cache.remove(lookUpMap.remove(id));
            }
        }
    }

    /**
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the
     * buffer
     */
    public Object get(String id) throws NotFoundException {
        clean();
        if(!lookUpMap.containsKey(id)){
            throw new NotFoundException("Object is not in the cache!");
        }
        //should update LRU
        return lookUpMap.get(id);
    }

    /**
     * Update the last refresh time for the object with the provided id.
     * This method is used to mark an object as "not stale" so that its
     * timeout is delayed.
     *
     * @param id the identifier of the object to "touch"
     * @return true if successful and false otherwise
     */
    public boolean touch(String id) {
        clean();
        if(lookUpMap.containsKey(id)){
            timeOutMap.remove(id);
            timeOutMap.put(id, 1000*System.currentTimeMillis() + timeout);
            return true;
        }
        return false;
    }

    /**
     * Update an object in the buffer.
     * This method updates an object and acts like a "touch" to
     * renew the object in the cache.
     *
     * @param t the object to update
     * @return true if successful and false otherwise
     */
    public boolean update(T t) {

        return touch(id);
    }
}
