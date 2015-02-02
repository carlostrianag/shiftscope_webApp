package com.shiftscope.views.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.shiftscope.netservices.HTTPService;
import com.shiftscope.utils.interfaces.Communicator;
import com.shiftscope.views.dialogs.LoginDialog;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/4/2015.
 */
public class LoginActivity extends FragmentActivity implements View.OnClickListener, Communicator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        Button registerBtn = (Button) findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        HTTPService.initHTTPService();
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
    public void nextActivity() {
        Intent deviceIntent = new Intent(this, SelectDeviceActivity.class);
        startActivity(deviceIntent);
    }
}