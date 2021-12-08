package cpen221.mp3;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.Exceptions.NotFoundException;
import cpen221.mp3.wikimediator.Bufferable.TestBufferable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class Task1Tests {

    private static ScheduledExecutorService scheduledExecutorService;
    private static FSFTBuffer<TestBufferable> smallBuffer;
    private static FSFTBuffer<TestBufferable> shortTimeBuffer;
    private static FSFTBuffer<TestBufferable> defaultBuffer;
    private static TestBufferable first;
    private static TestBufferable second;
    private static TestBufferable third;

    @BeforeAll
    public static void setupTests() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        smallBuffer = new FSFTBuffer<>(2, 15);
        shortTimeBuffer = new FSFTBuffer<>(32, 1);
        defaultBuffer = new FSFTBuffer<>();
        first = new TestBufferable("first");
        second = new TestBufferable("second");
        third = new TestBufferable("third");
    }

    @Test
    public void task1PutPutGet() {
        smallBuffer.put(first);
        smallBuffer.put(second);
        smallBuffer.put(third);
        try {
            Assertions.assertEquals(second, smallBuffer.get("second"));
            Assertions.assertEquals(third, smallBuffer.get("third"));
        } catch (NotFoundException e){
        }
        Assertions.assertThrows(NotFoundException.class, () -> smallBuffer.get("first"));
    }

    @Test
    public void task1RunAllMethods() {
        smallBuffer.put(first);
        smallBuffer.touch("first");
        smallBuffer.update(first);
        smallBuffer.put(second);
        smallBuffer.touch("first");
        smallBuffer.update(first);
        Assertions.assertEquals(false, smallBuffer.put(first));
        smallBuffer.checkRep();
    }

    @Test
    public void task1StalenessTest() {
        shortTimeBuffer.put(first);
        Assertions.assertFalse(shortTimeBuffer.put(first));
        ScheduledFuture<Boolean> put = scheduledExecutorService.schedule(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        return shortTimeBuffer.put(first);
                    }
                }, 2, TimeUnit.SECONDS);
        try {
            Assertions.assertEquals(true, put.get());
        } catch (InterruptedException | ExecutionException ignored){
        }
    }
}
