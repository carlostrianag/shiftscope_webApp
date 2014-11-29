/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.controller;

import org.apache.http.HttpResponse;
import shiftscope.criteria.DeviceCriteria;
import shiftscope.model.Device;
import shiftscope.services.DeviceService;

/**
 *
 * @author carlos
 */
public class DeviceController {
        
    public static HttpResponse createDevice(Device device){
        return DeviceService.createDevice(device);
    }
    
    public static HttpResponse getDeviceByUUID(DeviceCriteria criteria) {
        return DeviceService.getDeviceByUUID(criteria);
    }
}
