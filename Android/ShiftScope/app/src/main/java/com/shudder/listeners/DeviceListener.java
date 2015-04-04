package com.shudder.listeners;

import com.shudder.dto.DeviceDTO;

import java.util.ArrayList;

/**
 * Created by Carlos on 04/04/2015.
 */
public abstract class DeviceListener {
    public void OnSuccessfulDeviceFetch(ArrayList<DeviceDTO> devices) {OnLoaded();};
    public void OnFailedDeviceFetch() {OnLoaded();};
    public void OnLoading() {};
    public void OnLoaded() {};
}
