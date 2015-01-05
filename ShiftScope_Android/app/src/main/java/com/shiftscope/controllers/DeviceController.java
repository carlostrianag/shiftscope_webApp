package com.shiftscope.controllers;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shiftscope.netservices.HTTPService;

/**
 * Created by Carlos on 1/4/2015.
 */
public class DeviceController {

    public static void getDevicesByUserId(int id, AsyncHttpResponseHandler responseHandler){
        RequestParams params = new RequestParams();
        params.add("userId", String.valueOf(id));
        HTTPService.get("device/getDevicesByUserId", params, responseHandler);
    }
}
