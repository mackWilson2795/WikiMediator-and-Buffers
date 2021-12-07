package cpen221.mp3.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cpen221.mp3.wikimediator.Requests.RequestType;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class WikiMediatorClient {
    private Socket socket;
    private BufferedReader inputStream;
    private PrintWriter outputStream;

    public WikiMediatorClient(String hostname, int port){
        try {
            socket = new Socket(hostname, port);
            inputStream = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            outputStream = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Client threw IOException");
        }
    }

    public String receiveResponse() {
        String response = "Failed to read.";
        try {
            response = inputStream.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response);
        return response;
    }

    public void sendRequest(Long timeout, String id, String type, int[] intArgs,
                            String... strings){
        Gson json = new Gson();

        JsonObject request = new JsonObject();

        request.add("id", json.toJsonTree(id));
        request.add("type", json.toJsonTree(type));

        if (!Objects.isNull(timeout)) {
            request.add("timeout", json.toJsonTree(timeout));
        }

        // TODO: syntax correctness
        switch (type){
            case "search" :
                request.add("query", json.toJsonTree(strings[0]));
                request.add("limit", json.toJsonTree(intArgs[0]));
                break;
            case "getPage" :
                request.add("pageTitle", json.toJsonTree(strings[0]));
                break;
            case "zeitgeist" :
                request.add("limit", json.toJsonTree(intArgs[0]));
                break;
            case "trending" :
                request.add("maxItems", json.toJsonTree(intArgs[0]));
                request.add("timeLimitInSeconds", json.toJsonTree(intArgs[1]));
                break;
            case "windowedPeakLoad" :
                if (intArgs.length != 0){
                    request.add("timeWindowInSeconds", json.toJsonTree(intArgs[1]));
                }
                break;
            case "shortestPath" :
                request.add("pageTitle1", json.toJsonTree(strings[0]));
                request.add("pageTitle2", json.toJsonTree(strings[1]));
                break;
        }

        System.out.println(request);
        outputStream.print(request + "\n");
        outputStream.flush();
    }
}
