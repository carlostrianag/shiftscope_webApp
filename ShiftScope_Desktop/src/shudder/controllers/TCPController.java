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
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import shudder.criteria.TrackCriteria;
import shudder.listeners.WebSocketListener;
import shudder.model.Track;
import shudder.util.Constants;
import shudder.util.Operation;
import shudder.util.OperationType;
import shudder.util.SessionConstants;

/**
 *
 * @author Carlos
 */
public class TCPController {

    private static Gson JSONParser;
    private static ArrayList<WebSocketListener> listeners = new ArrayList<>();
    private static WebSocketClient webSocketService;

    public static void init() {
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
                            PlayerController.pause();
                            break;

                        case OperationType.RESUME:
                            PlayerController.resume();
                            break;

                        case OperationType.STOP:
                            PlayerController.stop();
                            break;

                        case OperationType.NEXT:
                            PlayerController.next();
                            break;

                        case OperationType.BACK:
                            PlayerController.back();
                            break;

                        case OperationType.PLAY:

                            criteria = new TrackCriteria();
                            criteria.setId(request.getId());
                            response = TrackController.getTrackById(criteria);
                            try {
                                t = JSONParser.fromJson(response.getResponseBody(), Track.class);
                                PlayerController.playSong(t, false);
                            } catch (IllegalStateException ex) {
                                for (WebSocketListener listener : listeners) {
                                    listener.OnError(ex.getMessage());
                                }
                            } catch (IOException ex) {
                                for (WebSocketListener listener : listeners) {
                                    listener.OnError(ex.getMessage());
                                }
                            }
                            break;

                        case OperationType.PLAY_FROM_PLAYLIST:
                            criteria = new TrackCriteria();
                            criteria.setId(request.getId());
                            response = TrackController.getTrackById(criteria);
                            try {
                                t = JSONParser.fromJson(response.getResponseBody(), Track.class);
                                PlayerController.playSong(t, true);
                            } catch (IOException ex) {
                                for (WebSocketListener listener : listeners) {
                                    listener.OnError(ex.getMessage());
                                }
                            } catch (IllegalStateException ex) {
                                for (WebSocketListener listener : listeners) {
                                    listener.OnError(ex.getMessage());
                                }
                            }
                            break;
                        case OperationType.REMOVE_FROM_PLAYLIST:
                            criteria = new TrackCriteria();
                            criteria.setId(request.getId());
                            //int order = (int) request.getValue();
                            response = TrackController.getTrackById(criteria);
                            try {
                                t = JSONParser.fromJson(response.getResponseBody(), Track.class);
                                PlayerController.dequeueSong(t);
                            } catch (IOException ex) {
                                for (WebSocketListener listener : listeners) {
                                    listener.OnError(ex.getMessage());
                                }
                            } catch (IllegalStateException ex) {
                                for (WebSocketListener listener : listeners) {
                                    listener.OnError(ex.getMessage());
                                }
                            }
                            break;

                        case OperationType.ENQUEUE:
                            criteria = new TrackCriteria();
                            criteria.setId(request.getId());
                            response = TrackController.getTrackById(criteria);
                            try {
                                t = JSONParser.fromJson(response.getResponseBody(), Track.class);
                                PlayerController.enqueueSong(t);
                            } catch (IOException ex) {
                                for (WebSocketListener listener : listeners) {
                                    listener.OnError(ex.getMessage());
                                }
                            } catch (IllegalStateException ex) {
                                for (WebSocketListener listener : listeners) {
                                    listener.OnError(ex.getMessage());
                                }
                            }
                            break;
                        case OperationType.VOLUME_DOWN:
                            PlayerController.volumeDown();
                            break;

                        case OperationType.VOLUME_UP:
                            PlayerController.volumeUp();
                            break;

                        case OperationType.SET_VOLUME:
                            PlayerController.setVolumeFromValue(request.getValue(), false);

                        case OperationType.SYNC:
                            request = new Operation();
                            request.setOperationType(OperationType.SYNC);
                            request.setUserId(SessionConstants.USER_ID);
                            request.setSync(PlayerController.getSync());
                            TCPController.sendRequest(request);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {

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

    public static void addListener(WebSocketListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(WebSocketListener listener) {
        listeners.remove(listener);
    }
    
    public static void sendRequest(Operation request) {
        try {
            JSONParser = new Gson();
            webSocketService.send(JSONParser.toJson(request, Operation.class));
        } catch (Exception ex) {
            
        }
    }
    
    public static void closeConnection() {
        if (webSocketService != null) {
            webSocketService.closeConnection(5, "APPLICATION CLOSED");
        }
    }
    
}
