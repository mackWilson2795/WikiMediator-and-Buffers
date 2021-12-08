package cpen221.mp3;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cpen221.mp3.fsftbuffer.BufferThreadTest;
import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.wikimediator.Bufferable.TestBufferable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class Task2Tests {
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
    public void task2test() throws InterruptedException {

        BufferThreadTest testerA = new BufferThreadTest();

        for(int i = 0; i < 1000; i++) {
          if(i % 10 == 0){
                TimeUnit.MILLISECONDS.sleep(25);
          }
            Thread t = new Thread(testerA);
            t.start();

        }
        Assertions.assertFalse(testerA.getIsPut());
    }

}
