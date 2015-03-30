package com.shudder.controllers;

import android.support.v4.app.DialogFragment;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shudder.dto.LoginCredentialsDTO;
import com.shudder.dto.UserDTO;
import com.shudder.netservices.HTTPService;
import com.shudder.utils.constants.SessionConstants;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by Carlos on 1/4/2015.
 */
public class LoginController {

    private static LoginCommunicator communicator;

    public static void setCommunicator(DialogFragment fragment) {
        communicator = (LoginCommunicator)fragment;
    }

    public static void login(LoginCredentialsDTO loginCredentials) {
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(statusCode == 200) {
                    Gson JSONParser = new Gson();
                    UserDTO user = JSONParser.fromJson(response.toString(), UserDTO.class);
                    SessionConstants.USER_ID = user.getId();
                    communicator.onSuccessfulLogin();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == 404) {
                    communicator.onFailedLogin();
                }
            }
        };
        RequestParams params = new RequestParams();
        params.add("email", loginCredentials.getEmail());
        params.add("password", loginCredentials.getPassword());
        HTTPService.post("user/login", params, responseHandler);
    }

    public interface LoginCommunicator{
        public void onSuccessfulLogin();
        public void onFailedLogin();
    }
}
