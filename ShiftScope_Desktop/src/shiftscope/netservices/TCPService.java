/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shiftscope.netservices;

import com.google.gson.Gson;
import com.ning.http.client.Response;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import shiftscope.controller.TrackController;
import shiftscope.criteria.TrackCriteria;
import shiftscope.main.Main;
import shiftscope.model.Track;
import shiftscope.util.Operation;
import shiftscope.util.OperationType;
import shiftscope.util.SessionConstants;

/**
 *
 * @author carlos
 */
public class TCPService extends WebSocketClient {

    private Gson JSONParser;

    public TCPService(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Operation request = new Operation();
        request.setUserId(SessionConstants.USER_ID);
        request.setOperationType(OperationType.CONNECT);
        request.setDeviceId(SessionConstants.DEVICE_ID);
        sendRequest(request);
        System.out.println("Conected...");
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
                Main.home.pause();
                break;

            case OperationType.RESUME:
                Main.home.resume();
                break;

            case OperationType.STOP:
                Main.home.stop();
                break;

            case OperationType.NEXT:
                Main.home.next();
                break;

            case OperationType.BACK:
                Main.home.back();
                break;

            case OperationType.PLAY:

                criteria = new TrackCriteria();
                criteria.setId(request.getId());
                response = TrackController.getTrackById(criteria);
                try {
                    t = JSONParser.fromJson(response.getResponseBody(), Track.class);
                    Main.home.playSong(t, false);
                } catch (IllegalStateException ex) {
                    Logger.getLogger(TCPService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(TCPService.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;

            case OperationType.PLAY_FROM_PLAYLIST:
                criteria = new TrackCriteria();
                criteria.setId(request.getId());
                response = TrackController.getTrackById(criteria);
                try {
                    t = JSONParser.fromJson(response.getResponseBody(), Track.class);
                    Main.home.playSong(t, true);
                } catch (IOException ex) {
                    Logger.getLogger(TCPService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalStateException ex) {
                    Logger.getLogger(TCPService.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case OperationType.REMOVE_FROM_PLAYLIST:
                criteria = new TrackCriteria();
                criteria.setId(request.getId());
                System.out.println("entro");
                //int order = (int) request.getValue();
                response = TrackController.getTrackById(criteria);
                try {
                    t = JSONParser.fromJson(response.getResponseBody(), Track.class);
                    Main.home.dequeueSong(t);
                } catch (IOException ex) {
                    Logger.getLogger(TCPService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalStateException ex) {
                    Logger.getLogger(TCPService.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;

            case OperationType.ENQUEUE:
                criteria = new TrackCriteria();
                criteria.setId(request.getId());
                response = TrackController.getTrackById(criteria);
                try {
                    t = JSONParser.fromJson(response.getResponseBody(), Track.class);
                    Main.home.enqueueSong(t);
                } catch (IOException ex) {
                    Logger.getLogger(TCPService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalStateException ex) {
                    Logger.getLogger(TCPService.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case OperationType.VOLUME_DOWN:
                Main.home.volumeDown();
                break;

            case OperationType.VOLUME_UP:
                Main.home.volumeUp();
                break;

            case OperationType.SET_VOLUME:
                Main.home.setVolumeFromValue(request.getValue(), false);

            case OperationType.SYNC:
                request = new Operation();
                request.setOperationType(OperationType.SYNC);
                request.setUserId(SessionConstants.USER_ID);
                request.setSync(Main.home.getSync());
                sendRequest(request);

        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

    public void sendRequest(Operation request) {

        try {
            JSONParser = new Gson();
            send(JSONParser.toJson(request, Operation.class));
        } catch (Exception ex) {

        }

    }
}
