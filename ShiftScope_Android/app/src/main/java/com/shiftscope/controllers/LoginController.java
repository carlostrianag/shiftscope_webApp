package com.shiftscope.controllers;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shiftscope.dto.LoginCredentialsDTO;
import com.shiftscope.netservices.HTTPService;

/**
 * Created by Carlos on 1/4/2015.
 */
public class LoginController {
    public static void login(LoginCredentialsDTO loginCredentials, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.add("email", loginCredentials.getEmail());
        params.add("password", loginCredentials.getPassword());
        HTTPService.post("user/login", params, responseHandler);
    }
}
