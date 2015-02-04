/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.controller;

import com.google.gson.Gson;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;
import java.util.ArrayList;
import javax.swing.SwingWorker;
import listeners.DeviceListener;
import shiftscope.criteria.DeviceCriteria;
import shiftscope.model.Device;
import shiftscope.netservices.HTTPService;

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
                return null;
            }
        };
        HTTPService.HTTPPost("/device/connectDevice", object, responseHandler);
    }
    
    private static class DeviceWorker extends SwingWorker<Void, Void>{

        @Override
        protected Void doInBackground() throws Exception {
            return null;
        }
    }
}
