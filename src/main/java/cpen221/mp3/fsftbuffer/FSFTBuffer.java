package cpen221.mp3.fsftbuffer;

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

    private final HashMap <String, T> lookUpMap = new HashMap<>(); // TODO : thread safety
    private final LinkedList<String> LRUQueue = new LinkedList<>();
    private final HashMap<String, Long> timeOutMap = new HashMap<>(); // TODO : thread safety
    private final int capacity;
    private final int timeout;

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
        if (lookUpMap.containsKey(t.id())) { // TODO : thread safety
            return false;
        }
        if (LRUQueue.size() == capacity) { // TODO : thread safety
            removeLRU();
        }
        add(t);
        return true;
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
        if (lookUpMap.containsKey(id)) { // TODO : thread safety
            timeOutMap.put(id, (System.currentTimeMillis() / MS_CONVERSION) + timeout);
            return true;
        }
        return false;
    }

    /**
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the
     * buffer
     */
    public Object get(String id) throws NotFoundException {
        clean();
        if (!lookUpMap.containsKey(id)) { //TODO : thread safety - object gets removed through put
            throw new NotFoundException("Object is not in the cache!");
        }
        LRUQueue.remove(id);
        LRUQueue.addFirst(id);
        return lookUpMap.get(id);
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
        clean();
        if (lookUpMap.containsKey(t.id())) { // TODO : thread safety
            lookUpMap.put(t.id(), t);
            timeOutMap.put(t.id(), (System.currentTimeMillis() / MS_CONVERSION) + timeout);
            return true;
        }
        return false;
    }

    /** TODO: temp spec
     * Removes all expired entries in the FSFTBuffer.
     */
    private void clean() { // TODO : Thread Safety
        for (String id : timeOutMap.keySet()) {
            long time = System.currentTimeMillis() / MS_CONVERSION;
            if (timeOutMap.get(id) >= time) {
                timeOutMap.remove(id);
                LRUQueue.remove(lookUpMap.remove(id));
            }
        }
    }

    private void removeLRU() {
        String id = LRUQueue.removeFirst();
        lookUpMap.remove(id);
        timeOutMap.remove(id);
    }

    private void add(T t) {
        LRUQueue.addLast(t.id()); // TODO : Thread Safety last object
        lookUpMap.put(t.id(), t);
        timeOutMap.put(t.id(), (System.currentTimeMillis() / MS_CONVERSION) + timeout);
    }
}
