package com.shiftscope.controllers;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shiftscope.dto.LibraryDTO;
import com.shiftscope.netservices.HTTPService;
import com.shiftscope.utils.constants.ControllerEvent;
import com.shiftscope.utils.constants.SessionConstants;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by Carlos on 1/4/2015.
 */
public class LibraryController {

    private static LibraryCommunicator communicator;

    public static void setCommunicator(Fragment fragment) {
        communicator = (LibraryCommunicator) fragment;
    }
    public static void getLibraryByDeviceId() {
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(statusCode == 200) {
                    new LibraryWorker(ControllerEvent.ON_SUCCESSFUL_LIBRARY_FETCH, response).execute();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.v("MIO", "errror libreria:" + responseString);
            }
        };

        RequestParams params = new RequestParams();
        params.add("device", String.valueOf(SessionConstants.DEVICE_ID));
        HTTPService.get("library/getLibraryByDeviceId", params, responseHandler);
    }

    private static class LibraryWorker extends AsyncTask<Void, Void, Void> {

        private ControllerEvent event;
        private JSONObject response;
        public LibraryWorker(ControllerEvent event, JSONObject response) {
            super();
            this.event = event;
            this.response = response;
        }

        @Override
        protected Void doInBackground(Void... params) {
            switch(event) {
                case ON_SUCCESSFUL_LIBRARY_FETCH:
                    Gson JSONParser = new Gson();
                    LibraryDTO library = JSONParser.fromJson(response.toString(), LibraryDTO.class);
                    SessionConstants.LIBRARY_ID = library.getId();
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch(event) {
                case ON_SUCCESSFUL_LIBRARY_FETCH:
                    communicator.onSuccessfulLibraryFetch();
                    break;
            }
        }
    };

    public interface LibraryCommunicator {
        public void onSuccessfulLibraryFetch();
        public void onFailedLibraryFetch();
    }
}
