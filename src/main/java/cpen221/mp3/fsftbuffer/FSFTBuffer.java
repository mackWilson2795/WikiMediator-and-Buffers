package cpen221.mp3.fsftbuffer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class FSFTBuffer<T extends Bufferable> {



    /* the default buffer size is 32 objects */
    public static final int DSIZE = 32;

    /* the default timeout value is 3600s */
    public static final int DTIMEOUT = 3600;

    /* Conversion factor between milliseconds and seconds */
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

    /* TODO: Thread Safety Argument
        1. Immutable variables:
        DSIZE, DTIMEOUT,MS_CONVERSION, capacity, and timeout are immutable constants that are thread safe.
        2. Thread Safety Data Types:
        why? lookUpMap, LRUDeque, and timeoutMap use thread safe ADT's that ensure thread safety.
        3. Synchronization:
        All methods are synchronized

        What are the main problems:
        1. statics are being used
        2. quick fix - wrappers for collections
        3. Large problem - cleaner function - could we use a state variable - maybe but unlikely
        4. Due to cleaner function, synchronization of overall methods is best start
        5. Synchronization of get, put, update, touch - either state variable or java.concurrent add on

     */

    private final ConcurrentHashMap<String, T> lookUpMap = new ConcurrentHashMap<>();
    private final LinkedBlockingDeque<String> LRUQueue = new LinkedBlockingDeque<>();
    private final ConcurrentHashMap<String, Long> timeOutMap = new ConcurrentHashMap<>();
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
    synchronized public boolean put(T t) {
        clean();
        if (lookUpMap.containsKey(
            t.id())) {
            return false;
        }
        if (LRUQueue.size() == capacity) {
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
    synchronized public boolean touch(String id) { //second point of interest
        // clean();
        if (lookUpMap.containsKey(id) && timeOutMap.get(id) < System.currentTimeMillis() / MS_CONVERSION) {
            timeOutMap.put(id, (System.currentTimeMillis() / MS_CONVERSION) +
                timeout);
            return true;
        }
        return false;
    }

    /**
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the
     * buffer
     */
    synchronized public Object get(String id) throws NotFoundException {
        clean();
        if (!lookUpMap.containsKey(id)) {
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
    synchronized public boolean update(T t) { //point of interest

        //clean();
        if (timeOutMap.containsKey(t.id()) && timeOutMap.get(t.id()) < (System.currentTimeMillis() / MS_CONVERSION)) {
            lookUpMap.put(t.id(), t);
            timeOutMap.put(t.id(), (System.currentTimeMillis() / MS_CONVERSION) + timeout);
            return true;
        }
        return false;
    }


    /** TODO: temp spec
     * Removes all expired entries in the FSFTBuffer.
     */
    private void clean() {
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
        LRUQueue.addLast(t.id());
        lookUpMap.put(t.id(), t);
        timeOutMap.put(t.id(), (System.currentTimeMillis() / MS_CONVERSION) + timeout);
    }
}
