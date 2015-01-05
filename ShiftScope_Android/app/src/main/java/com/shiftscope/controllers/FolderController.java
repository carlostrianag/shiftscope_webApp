package com.shiftscope.controllers;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shiftscope.netservices.HTTPService;
import com.shiftscope.utils.SessionConstants;

/**
 * Created by Carlos on 1/4/2015.
 */
public class FolderController {
    public static void getFolderContentById(int id, AsyncHttpResponseHandler responseHandler){
        RequestParams params = new RequestParams();
        params.add("id", String.valueOf(id));
        params.add("library", String.valueOf(SessionConstants.LIBRARY_ID));
        HTTPService.get("folder/getFolderContentById", params, responseHandler);
    }

}
