package shiftscope.controllers;

import com.google.gson.Gson;
import java.net.URI;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import utils.Request;

/**
 *
 * @author carlos
 */
public class ShiftScopeWebSocket extends WebSocketClient {
    private final Gson JSONParser = new Gson();
    
    public ShiftScopeWebSocket(URI serverURI) {
        super(serverURI);
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Conected...");
        Request request = new Request();
        request.setUserId(124);
        request.setType(2);
        request.setContent(null);
        request.setFrom("DESKTOP");
        request.setResponse(200);
        send(JSONParser.toJson(request));
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Message arrived: " + message);
        Request request = JSONParser.fromJson(message, Request.class);
        if (request.getType() != 5 && request.getType() != 6 ) {
            RequestHandler requestHandler = new RequestHandler(request);
            Request response = requestHandler.handle();
            if( response != null){
                send(JSONParser.toJson(response, Request.class));
            }
            
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
    
}
