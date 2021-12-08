package cpen221.mp3.fsftbuffer;

import cpen221.mp3.fsftbuffer.Exceptions.NotFoundException;
import cpen221.mp3.wikimediator.Bufferable.Bufferable;
import cpen221.mp3.wikimediator.Bufferable.TestBufferable;

public class BufferThreadTest implements Runnable{

    private FSFTBuffer<Bufferable> tester = new FSFTBuffer();
    private TestBufferable first = new TestBufferable("first");
    private TestBufferable second = new TestBufferable("second");
    private TestBufferable third = new TestBufferable("third");
    private Object T;
    private Boolean isPut;

    public BufferThreadTest(){
        tester = new FSFTBuffer();
    }

    public void run(){
        tester.put(third);
        tester.put(second);
        try{
            T = tester.get("third");
        }
        catch (NotFoundException ignored){
        }
        tester.put(first);
        tester.touch("second");
        tester.update(first);
        tester.put(third);
        tester.put(second);
        try{
            T = tester.get("second");
        }
        catch (NotFoundException e){

        }
        tester.put(first);
        tester.touch("second");
        tester.update(first);
        tester.put(third);

        isPut = tester.put(second);

        tester.put(second);
        try{
            T = tester.get("second");
        }
        catch (NotFoundException e){
        }
        tester.put(first);
        tester.touch("second");
        tester.update(first);
        tester.put(third);
        tester.put(second);
    }

    public boolean getIsPut(){
        return isPut;
    }
}