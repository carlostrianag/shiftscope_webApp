/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shudder.controllers;

import com.google.gson.Gson;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;
import java.util.ArrayList;
import javax.swing.SwingWorker;
import shudder.listeners.DeviceListener;
import shudder.criteria.DeviceCriteria;
import shudder.model.Device;
import shudder.netservices.HTTPService;

/**
 *
 * @author carlos
 */
public class DeviceController {
    
    private static ArrayList<DeviceListener> listeners = new ArrayList<>();
    private static Gson JSONParser;
    
    public static void addListener(DeviceListener listener) {
        listeners.add(listener);
    }
    
    public static Response createDevice(Device device){
        JSONParser = new Gson();
        String object = JSONParser.toJson(device);
        return HTTPService.HTTPSyncPost("/device/create", object); 
    }
    
    public static Response getDeviceByUUID(DeviceCriteria criteria) {
        return HTTPService.HTTPSyncGet("/device/getDeviceByUUID?UUID="+criteria.getUUID());
    }

    public static void connectDevice(DeviceCriteria criteria) {
        JSONParser = new Gson();
        String object = JSONParser.toJson(criteria);
        AsyncCompletionHandler<Void> responseHandler = new AsyncCompletionHandler<Void>() {

            @Override
            public Void onCompleted(Response response) throws Exception {
                for (DeviceListener listener : listeners) {
                    listener.OnSuccessfulDeviceConnection();
                }
                return null;
            }
        };
        HTTPService.HTTPPost("/device/connectDevice", object, responseHandler);
    }
}
