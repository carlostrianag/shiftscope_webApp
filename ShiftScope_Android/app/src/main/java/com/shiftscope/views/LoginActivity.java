package com.shiftscope.views;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.shiftscope.dto.UserDTO;
import com.shiftscope.netservices.HTTPService;
import com.shiftscope.netservices.TCPService;
import com.shiftscope.utils.SessionConstants;
import com.shiftscope.views.dialogs.LoginDialog;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/4/2015.
 */
public class LoginActivity extends FragmentActivity implements View.OnClickListener, LoginDialog.Communicator{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        Button registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);
        HTTPService.initHTTPService();
        TCPService.initTCPService();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                LoginDialog loginDialog = new LoginDialog();
                loginDialog.show(getSupportFragmentManager(), "LOGIN");
                break;
        }
    }

    @Override
    public void successfulLogin(UserDTO user) {
        SessionConstants.USER_ID = user.getId();
        Intent deviceIntent = new Intent(this, SelectDeviceActivity.class);
        startActivity(deviceIntent);
    }
}
