/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import shiftscope.criteria.DeviceCriteria;
import shiftscope.model.Device;
import shiftscope.netservices.HTTPService;

/**
 *
 * @author carlos
 */
public class DeviceService {
    private static Gson JSONParser;
    public static HttpResponse createDevice(Device device){
        JSONParser = new Gson();
        String object = JSONParser.toJson(device);
        return HTTPService.HTTPPost("/device/create", object);        
    }
    
    public static HttpResponse getDeviceByUUID(DeviceCriteria criteria){
        return HTTPService.HTTPGet("/device/getDeviceByUUID?UUID="+criteria.getUUID());
    }    
}
