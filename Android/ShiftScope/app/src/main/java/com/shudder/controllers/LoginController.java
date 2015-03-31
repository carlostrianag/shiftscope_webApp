package com.shudder.controllers;

import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shudder.dto.LoginCredentialsDTO;
import com.shudder.dto.UserDTO;
import com.shudder.listeners.LoginListener;
import com.shudder.netservices.HTTPService;
import com.shudder.utils.constants.SessionConstants;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Carlos on 1/4/2015.
 */
public class LoginController {

    private static ArrayList<LoginListener> listeners = new ArrayList<>();

    public static void addListener(LoginListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(LoginListener listener) {
        listeners.remove(listener);
    }

    private static void invokeOnSuccess() {
        for(LoginListener listener : listeners) {
            listener.OnSuccess();
        }
    }

    private static void invokeOnFailed() {
        for(LoginListener listener : listeners) {
            listener.OnFailed();
        }
    }

    private static void invokeOnError(String message) {
        for(LoginListener listener : listeners) {
            listener.OnError(message);
        }
    }

    public static void login(LoginCredentialsDTO loginCredentials) {
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(statusCode == 200) {
                    Gson JSONParser = new Gson();
                    UserDTO user = JSONParser.fromJson(response.toString(), UserDTO.class);
                    SessionConstants.USER_ID = user.getId();
                    invokeOnSuccess();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (statusCode == 0) {
                    invokeOnError("No internet connection detected, please check and try again.");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                invokeOnFailed();
            }
        };

        RequestParams params = new RequestParams();
        params.add("email", loginCredentials.getEmail());
        params.add("password", loginCredentials.getPassword());
        for (LoginListener listener : listeners) {
            listener.OnLoading();
        }
        HTTPService.post("user/login", params, responseHandler);
    }
}
