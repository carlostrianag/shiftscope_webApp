/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.controller;

import com.ning.http.client.Response;
import shiftscope.criteria.DeviceCriteria;
import shiftscope.model.Device;
import shiftscope.services.DeviceService;

/**
 *
 * @author carlos
 */
public class DeviceController {
        
    public static Response createDevice(Device device){
        return DeviceService.createDevice(device);
    }
    
    public static Response getDeviceByUUID(DeviceCriteria criteria) {
        return DeviceService.getDeviceByUUID(criteria);
    }

    public static Response connectDevice(DeviceCriteria criteria) {
        return DeviceService.connectDevice(criteria);
    }
}
