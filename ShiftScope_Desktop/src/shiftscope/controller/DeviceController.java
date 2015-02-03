/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.controller;

import com.google.gson.Gson;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;
import shiftscope.criteria.DeviceCriteria;
import shiftscope.model.Device;
import shiftscope.netservices.HTTPService;
import shiftscope.services.DeviceService;

/**
 *
 * @author carlos
 */
public class DeviceController {
    private static Gson JSONParser;
        
    public static void createDevice(Device device){
        JSONParser = new Gson();
        String object = JSONParser.toJson(device);
        AsyncCompletionHandler<Void> responseHandler = new AsyncCompletionHandler<Void>() {
            @Override
            public Void onCompleted(Response response) throws Exception {
                return null;
            };
            
        };
        HTTPService.HTTPPost("/device/create", object, responseHandler); 
    }
    
    public static void getDeviceByUUID(DeviceCriteria criteria) {
        AsyncCompletionHandler<Void> responseHandler = new AsyncCompletionHandler<Void>() {

            @Override
            public Void onCompleted(Response response) throws Exception {
                return null;
            }
        };
        HTTPService.HTTPGet("/device/getDeviceByUUID?UUID="+criteria.getUUID(), responseHandler);
    }

    public static Response connectDevice(DeviceCriteria criteria) {
        return DeviceService.connectDevice(criteria);
    }
    
    
}
