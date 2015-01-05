package com.shiftscope.netservices;


import android.util.Log;

import com.shiftscope.utils.SessionConstants;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by Carlos on 1/4/2015.
 */
public class TCPService{

    public static WebSocketConnection webSocket;

    public static void initTCPService() {

        webSocket = new WebSocketConnection();
        try {
            webSocket.connect(SessionConstants.WEB_SOCKET_SERVER, new WebSocketHandler(){
                @Override
                public void onOpen() {
                    Log.v("WEBSOCKET", "CONNECTED");
                }

                @Override
                public void onClose(int code, String reason) {
                    super.onClose(code, reason);
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.v("WEBSOCKET", payload);
                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }
}
