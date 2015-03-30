package com.shudder.views.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shudder.controllers.LoginController;
import com.shudder.dto.LoginCredentialsDTO;
import com.shudder.listeners.LoginListener;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 30/03/2015.
 */
public class LoginActivity extends FragmentActivity implements View.OnClickListener {

    private TextView emailText;
    private TextView passwordText;
    private Button getInBtn;
    private ProgressDialog progressDialog;
    private LoginListener loginListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailText = (TextView) findViewById(R.id.emailText);
        emailText.setText("trianag24@gmail.com");
        passwordText  = (TextView) findViewById(R.id.passwordText);
        passwordText.setText("medellin0707");
        getInBtn = (Button) findViewById(R.id.getInBtn);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getInBtn.setOnClickListener(this);
        loginListener = new LoginListener() {

            @Override
            public void OnSuccess() {
                dismissProgressDialog();
                Intent selectDeviceIntent = new Intent(getApplicationContext(), SelectDeviceActivity.class);
                startActivity(selectDeviceIntent);
            }

            @Override
            public void OnFailed() {

            }
        };
        LoginController.addListener(loginListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LoginController.removeListener(loginListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginController.removeListener(loginListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getInBtn:
                LoginCredentialsDTO loginCredentials = new LoginCredentialsDTO();
                loginCredentials.setEmail(emailText.getText().toString());
                loginCredentials.setPassword(passwordText.getText().toString());
                showProgressDialog();
                LoginController.login(loginCredentials);
                break;
        }
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Validating your information, please wait ...");
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
