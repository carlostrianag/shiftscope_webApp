package com.shiftscope.netservices;


import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.shiftscope.utils.Operation;
import com.shiftscope.utils.constants.RequestTypes;
import com.shiftscope.utils.constants.SessionConstants;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by Carlos on 1/4/2015.
 */
public class TCPService{


    private static PlayerCommunicator playerCommunicator;
    private static WebSocketConnection webSocket;
    private static final String TAG  = "WEB_SOCKET";

    public static void setPlayerCommunicator(Activity activity){
        playerCommunicator = (PlayerCommunicator)activity;
    }

    public static void initTCPService() {
        webSocket = new WebSocketConnection();
        try {
            webSocket.connect(SessionConstants.WEB_SOCKET_SERVER, new WebSocketHandler(){
                public void initConnection() {
                    Operation operation = new Operation();
                    operation.setUserId(SessionConstants.USER_ID);
                    operation.setOperationType(RequestTypes.CONNECT);
                    operation.setTo(SessionConstants.DEVICE_ID);
                    TCPService.send(operation);

                    operation = new Operation();
                    operation.setUserId(SessionConstants.USER_ID);
                    operation.setOperationType(RequestTypes.SYNC);
                    operation.setTo(SessionConstants.DEVICE_ID);
                    TCPService.send(operation);
                }

                @Override
                public void onOpen() {
                    Log.v(TAG, "CONNECTED");
                    initConnection();
                }

                @Override
                public void onClose(int code, String reason) {
                    super.onClose(code, reason);
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.v(TAG, payload);
                    Gson JSONParser = new Gson();
                    Operation operation = JSONParser.fromJson(payload, Operation.class);
                    playerCommunicator.onSync(operation);
                }


            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    public static void send(Operation operation){
        Gson JSONParser = new Gson();
        webSocket.sendTextMessage(JSONParser.toJson(operation));
    }

    public interface PlayerCommunicator {
        public void onSync(Operation operation);
    }
}
