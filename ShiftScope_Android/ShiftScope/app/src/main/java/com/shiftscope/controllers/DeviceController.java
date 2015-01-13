package com.shiftscope.controllers;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shiftscope.dto.DeviceDTO;
import com.shiftscope.netservices.HTTPService;
import com.shiftscope.utils.constants.ControllerEvent;
import com.shiftscope.utils.constants.SessionConstants;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Carlos on 1/4/2015.
 */
public class DeviceController {


    private static DeviceCommunicator communicator;

    public static void setCommunicator(Activity activity) {
        communicator = (DeviceCommunicator)activity;
    }

    public static void getDevicesByUserId(){
        int id = SessionConstants.USER_ID;
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(statusCode == 200) {
                    new DeviceWorker(ControllerEvent.ON_SUCCESSFUL_DEVICE_FETCH, response).execute();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.v("MIO", responseString);
            }
        };
        RequestParams params = new RequestParams();
        params.add("userId", String.valueOf(id));
        HTTPService.get("device/getDevicesByUserId", params, responseHandler);
    }


    private static class DeviceWorker extends AsyncTask<Void, Void, Void>{

        private ControllerEvent event;
        private JSONArray jsonArray;
        private ArrayList<DeviceDTO> devices;

        public DeviceWorker(ControllerEvent event, JSONArray jsonArray) {
            super();
            this.event = event;
            this.jsonArray = jsonArray;
            this.devices = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Gson JSONParser = new Gson();
            switch(event) {
                case ON_SUCCESSFUL_DEVICE_FETCH:
                    devices = JSONParser.fromJson(jsonArray.toString(), new TypeToken<ArrayList<DeviceDTO>>(){}.getType());
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch(event) {
                case ON_SUCCESSFUL_DEVICE_FETCH:
                    communicator.onSuccessfulDeviceFetch(devices);
                    break;
            }
        }
    };


    public interface DeviceCommunicator {
        public void onSuccessfulDeviceFetch(ArrayList<DeviceDTO> devices);
        public void onFailedDeviceFetch();
    }
}
