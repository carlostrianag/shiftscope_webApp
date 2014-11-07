package shiftscope.controllers;

import java.net.URI;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 *
 * @author carlos
 */
public class ShiftScopeWebSocket extends WebSocketClient {

    public ShiftScopeWebSocket(URI serverURI) {
        super(serverURI);
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Conectado");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("LLEGO FROM SERVER: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Error");
    }
    
}
