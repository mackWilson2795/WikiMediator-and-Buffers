package cpen221.mp3.fsftbuffer;

import cpen221.mp3.fsftbuffer.Exceptions.NotFoundException;
import cpen221.mp3.wikimediator.Bufferable.Bufferable;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class FSFTBuffer<T extends Bufferable> {

    /* the default buffer size is 32 objects */
    public static final int DSIZE = 32;

    /* the default timeout value is 3600s */
    public static final int DTIMEOUT = 3600;

    /* Conversion factor between milliseconds and seconds */
    public static final int MS_CONVERSION = 1000;




    /* Thread Safety Argument:
        This class is thread safe because of the following implementations:
        1. Immutable variables:
        DSIZE, DTIMEOUT, MS_CONVERSION, capacity, and timeout are immutable constants that are thread safe.
        Thus, since their states do not change, the usage of these by multiple threads at the same time will
        not lead to any read and write errors since they can't be written to.
        These are immutable constants since they use final and are primitive data types.
        2. Thread Safety Data Types:
        lookUpMap, LRUDeque, and timeoutMap use thread safe ADT's.
        LookUpMap and timeoutMap use a ConcurrentHashMap, LRUDeque uses LinkedBlockingDeque.
        The coupling of thread Safety Data types and Synchronization leads to thread safety,
        ensuring that there are no read-write or write-write data races.
        3. Synchronization:
        The put, get, update and touch methods are all synchronized using the object as a lock.
        Thus, these methods are unable to run at the same time, ensuring that there are
        no read-write or write-write data races.
        This class is thread safe because of overall use of Immutable Variables, Thread Safe ADT's, and Synchronization.


     */

    private final ConcurrentHashMap<String, T> lookUpMap = new ConcurrentHashMap<>();
    private final LinkedBlockingDeque<String> LRUQueue = new LinkedBlockingDeque<>();
    private final ConcurrentHashMap<String, Long> timeOutMap = new ConcurrentHashMap<>();
    private final int capacity;
    private final int timeout;

    /*
     * Abstraction Function :
     *
     * lookUpMap = maps the id of every object in the FSFT Buffer to
     *             the object itself
     *
     * timeOutMap = maps the id of every object in the FSFT Buffer to
     *              the time in seconds at which the object will time out
     *
     * LRUQueue = a first in first out queue of the ids of every object currently
     *            in the FSFT Buffer
     * */

    /*
     * Abstraction Function :
     *
     * lookUpMap = maps the id of every object currently in the FSFT Buffer to
     *             the object itself
     *
     * timeOutMap = maps the id of every object currently in the FSFT Buffer to
     *              the time in seconds at which the object will time out
     *
     * LRUQueue = a first in first out queue of the ids of every object currently
     *            in the FSFT Buffer
     * */

    /*
     * Rep - Invariant :
     *
     * lookUpMap.keySet() should always be equal to timeOutMap.keySet() which
     * should only ever contain every entry found in LRUQueue. Every time in
     * timeOutMap.values() should be greater than the current time in seconds
     * */

    public boolean checkRep() {
        clean();
        if (!lookUpMap.keySet().equals(timeOutMap.keySet())){
            return false;
        }
        HashSet comparison = new HashSet(LRUQueue);
        if (!lookUpMap.keySet().equals(comparison)){
            return false;
        }
        for (String id: lookUpMap.keySet()) {
            if(timeOutMap.get(id) < (System.currentTimeMillis() / MS_CONVERSION)) {
                return false;
            }
        }
        return true;
    }



    /**
     * Create a buffer with a fixed capacity and a timeout value.
     * Objects in the buffer that have not been refreshed within the
     * timeout period are removed from the cache.
     *
     * @param capacity the number of objects the buffer can hold,
     *                 capacity must be greater than zero
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
    synchronized public boolean touch(String id) {
        if (lookUpMap.containsKey(id) && timeOutMap.get(id) < System.currentTimeMillis() / MS_CONVERSION) {
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
    synchronized public boolean update(T t) {
        if (timeOutMap.containsKey(t.id()) && timeOutMap.get(t.id()) < (System.currentTimeMillis() / MS_CONVERSION)) {
            lookUpMap.put(t.id(), t);
            timeOutMap.put(t.id(), (System.currentTimeMillis() / MS_CONVERSION) + timeout);
            return true;
        }
        return false;
    }


    /**
     * Helper function, removes all entries in the FSFT Buffer that have
     * timed out
     */
    private void clean() {

        synchronized(this) {
            for (String id : timeOutMap.keySet()) {
                long time = System.currentTimeMillis() / MS_CONVERSION;
                if (timeOutMap.get(id) <= time) {
                    timeOutMap.remove(id);
                    LRUQueue.remove(lookUpMap.remove(id));
                }
            }
        }
    }

    /**
     * Helper function, removes the least recently used object from
     * the FSFT Buffer
     */

    private void removeLRU() {
        synchronized(this) {
            if (LRUQueue.size() > 0) {
                String id = LRUQueue.removeFirst();
                lookUpMap.remove(id);
                timeOutMap.remove(id);
            }
        }
    }

    /**
     * Helper function, adds an object to the FSFT Buffer
     * @param t the object to be added
     */

    private void add(T t) {
        LRUQueue.addLast(t.id());
        lookUpMap.put(t.id(), t);
        timeOutMap.put(t.id(), (System.currentTimeMillis() / MS_CONVERSION) + timeout);
    }
}
