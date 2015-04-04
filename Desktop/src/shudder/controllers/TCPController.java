/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.controllers;

import com.google.gson.Gson;
import com.ning.http.client.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javafx.application.Platform;
import shudder.criteria.TrackCriteria;
import shudder.listeners.WebSocketListener;
import shudder.model.Track;
import shudder.util.Constants;
import shudder.util.Operation;
import shudder.util.OperationType;
import shudder.util.SessionConstants;
import shudder.util.java.org.java_websocket.client.WebSocketClient;
import shudder.util.java.org.java_websocket.handshake.ServerHandshake;
import shudder.views.MainView;

/**
 *
 * @author Carlos
 */
public class TCPController {

    private Gson JSONParser;
    private ArrayList<WebSocketListener> listeners = new ArrayList<>();
    private WebSocketClient webSocketService;

    public void init() {
        try {
            webSocketService = new WebSocketClient(new URI(Constants.SOCKET_SERVER)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Operation request = new Operation();
                    request.setUserId(SessionConstants.USER_ID);
                    request.setOperationType(OperationType.CONNECT);
                    request.setDeviceId(SessionConstants.DEVICE_ID);
                    sendRequest(request);
                    for (WebSocketListener listener : listeners) {
                        listener.OnOpened();
                    }
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Message arrived: " + message);
                    JSONParser = new Gson();
                    Track t;
                    TrackCriteria criteria;
                    Response response;
                    Operation request = JSONParser.fromJson(message, Operation.class);
                    switch (request.getOperationType()) {
                        case OperationType.PAUSE:
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    MainView.mainBrowser.execute("PlayerController.pause();");
                                }
                            });

                            break;

                        case OperationType.RESUME:
                            //PlayerController.resume();
                            break;

                        case OperationType.STOP:
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    MainView.mainBrowser.execute("PlayerController.stop();");
                                }
                            });
                            break;

                        case OperationType.NEXT:
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    MainView.mainBrowser.execute("PlayerController.next();");
                                }
                            });
                            break;

                        case OperationType.BACK:
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    MainView.mainBrowser.execute("PlayerController.back();");
                                }
                            });
                            break;

                        case OperationType.PLAY:
                            criteria = new TrackCriteria();
                            criteria.setId(request.getId());
                            response = TrackController.getTrackById(criteria);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String JSON = response.getResponseBody();
                                        MainView.mainBrowser.execute("PlayerController.play(JSON.stringify(" + JSON + "), false);");
                                    } catch (IOException ex) {
                                        for (WebSocketListener listener : listeners) {
                                            listener.OnError(ex.getMessage());
                                        }
                                    }
                                }
                            });
                            break;

                        case OperationType.PLAY_FROM_PLAYLIST:
                            criteria = new TrackCriteria();
                            criteria.setId(request.getId());
                            response = TrackController.getTrackById(criteria);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String JSON = response.getResponseBody();
                                            MainView.mainBrowser.execute("PlayerController.play(JSON.stringify(" + JSON + "), true);");
                                        } catch (IOException ex) {
                                            for (WebSocketListener listener : listeners) {
                                                listener.OnError(ex.getMessage());
                                            }
                                        }
                                    }
                                });
                            break;
                        case OperationType.REMOVE_FROM_PLAYLIST:
                            criteria = new TrackCriteria();
                            criteria.setId(request.getId());
                            //int order = (int) request.getValue();
                            response = TrackController.getTrackById(criteria);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String JSON = response.getResponseBody();
                                        MainView.mainBrowser.execute("PlayerController.dequeueSong(JSON.stringify(" + JSON + "));");
                                    } catch (IOException ex) {
                                        for (WebSocketListener listener : listeners) {
                                            listener.OnError(ex.getMessage());
                                        }
                                    }
                                }
                            });
                            break;

                        case OperationType.ENQUEUE:
                            criteria = new TrackCriteria();
                            criteria.setId(request.getId());
                            response = TrackController.getTrackById(criteria);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String JSON = response.getResponseBody();
                                        MainView.mainBrowser.execute("PlayerController.enqueueSong(JSON.stringify(" + JSON + "));");
                                    } catch (IOException ex) {
                                        for (WebSocketListener listener : listeners) {
                                            listener.OnError(ex.getMessage());
                                        }
                                    }
                                }
                            });
                            break;
                        case OperationType.VOLUME_DOWN:
                            //PlayerController.volumeDown();
                            break;

                        case OperationType.VOLUME_UP:
                            //PlayerController.volumeUp();
                            break;

                        case OperationType.SET_VOLUME:
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    MainView.mainBrowser.execute("OnVolumeChanged("+request.getValue()*100+"); PlayerController.setVolumeFromValue("+request.getValue()+", false);");      
                                }
                            });
                            

                        case OperationType.SYNC:
                            Operation syncRequest = new Operation();
                            syncRequest.setOperationType(OperationType.SYNC);
                            syncRequest.setUserId(SessionConstants.USER_ID);
                            syncRequest.setSync(SessionConstants.sync);
                            sendRequest(syncRequest);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("WebSocket Closed ...");
                    for (WebSocketListener listener : listeners) {
                        listener.OnClose("Websocket connection has been closed. What do you want to do?");
                    }                    
                }

                @Override
                public void onError(Exception ex) {
                    for (WebSocketListener listener : listeners) {
                        listener.OnError(ex.getMessage());
                    }
                }
            };
        } catch (URISyntaxException ex) {
            for (WebSocketListener listener : listeners) {
                listener.OnError(ex.getMessage());
            }
        }
        webSocketService.connect();
    }

    public void addListener(WebSocketListener listener) {
        listeners.add(listener);
    }

    public void removeListener(WebSocketListener listener) {
        listeners.remove(listener);
    }

    public void sendRequest(Operation request) {
        try {
            JSONParser = new Gson();
            webSocketService.send(JSONParser.toJson(request, Operation.class));
        } catch (Exception ex) {

        }
    }

    public void sendRequest(String operation) {
        System.out.println(operation);
        try {
            webSocketService.send(operation);
        } catch (Exception ex) {

        }
    }

    public static void sendJSRequest(Operation request) {
        try {
            Gson JSONParser = new Gson();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Gson JSONParser = new Gson();
                    String JSONObject = JSONParser.toJson(request);
                    MainView.mainBrowser.execute("TCPController.sendRequest(JSON.stringify(" + JSONObject + "));");
                }
            });
        } catch (Exception ex) {

        }
    }

    public void closeConnection() {
        if (webSocketService != null) {
            webSocketService.closeConnection(5, "APPLICATION CLOSED");
        }
    }

}
