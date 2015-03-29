package com.shiftscope.netservices;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;
import com.shiftscope.utils.constants.SessionConstants;

/**
 * Created by carlos on 12/12/14.
 */
public class HTTPService {


    private static final String BASE_URL = SessionConstants.SERVER_URL;

    private static AsyncHttpClient client;
    private static SyncHttpClient syncClient;

    public static void initHTTPService() {
        client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        syncClient = new SyncHttpClient();
        syncClient.addHeader("Accept", "application/json");
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.v("MIO", getAbsoluteUrl(url));
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void syncGet(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        syncClient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
