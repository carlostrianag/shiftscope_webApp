package com.shiftscope.controllers;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.shiftscope.netservices.HTTPService;
import com.shiftscope.utils.SessionConstants;

/**
 * Created by Carlos on 1/4/2015.
 */
public class LibraryController {
    public static void getLibraryByDeviceId(ResponseHandlerInterface responseHandler) {
        RequestParams params = new RequestParams();
        params.add("device", String.valueOf(SessionConstants.DEVICE_ID));
        HTTPService.syncGet("library/getLibraryByDeviceId", params, responseHandler);
    }
}
