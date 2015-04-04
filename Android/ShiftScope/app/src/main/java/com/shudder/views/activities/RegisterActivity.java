package com.shudder.views.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.shudder.R;
import com.shudder.controllers.LoginController;
import com.shudder.dto.UserDTO;
import com.shudder.listeners.LoginListener;
import com.shudder.views.dialogs.ErrorDialog;

/**
 * Created by Carlos on 30/03/2015.
 */
public class RegisterActivity extends FragmentActivity implements View.OnClickListener {
    private Button registerBtn;
    private EditText name;
    private EditText lastName;
    private EditText password;
    private EditText email;
    private LoginListener loginListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerBtn = (Button)findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);
        name = (EditText) findViewById(R.id.nameText);
        lastName = (EditText) findViewById(R.id.lastNameText);
        password = (EditText) findViewById(R.id.passwordText);
        email = (EditText) findViewById(R.id.emailText);
        loginListener = new LoginListener() {
            @Override
            public void OnLoading() {
                showProgressDialog();
            }

            @Override
            public void OnLoaded() {
                dismissProgressDialog();
            }

            @Override
            public void OnRegistered() {
                super.OnRegistered();
                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginActivity);
                finish();
            }


            @Override
            public void OnError(String message) {
                super.OnError(message);
                ErrorDialog errorDialog = ErrorDialog.newInstance(message);
                errorDialog.show(getFragmentManager(), "ERROR");
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoginController.addListener(loginListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginController.removeListener(loginListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerBtn:
                UserDTO userDTO = new UserDTO();
                userDTO.setName(name.getText().toString());
                userDTO.setLastName(lastName.getText().toString());
                userDTO.setEmail(email.getText().toString());
                userDTO.setPassword(password.getText().toString());
                LoginController.register(userDTO);
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
