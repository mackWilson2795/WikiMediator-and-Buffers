package cpen221.mp3.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class WillBeRunnable implements Runnable{
    private Socket socket;

    WillBeRunnable(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try{
            try{
                handleRequest();
            } finally {
                socket.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
