package com.shiftscope.views.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shiftscope.controllers.LoginController;
import com.shiftscope.dto.LoginCredentialsDTO;
import com.shiftscope.dto.UserDTO;

import org.apache.http.Header;
import org.json.JSONObject;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/4/2015.
 */
public class LoginDialog extends DialogFragment {
    private TextView emailText;
    private TextView passwordText;
    private TextView errorText;
    private Communicator communicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator)activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(v);
        emailText = (TextView)v.findViewById(R.id.emailText);
        emailText.setText("trianag24@gmail.com");
        passwordText  = (TextView)v.findViewById(R.id.passwordText);
        passwordText.setText("medellin0707");
        errorText = (TextView)v.findViewById(R.id.errorText);

        builder.setMessage("Log in")
                .setPositiveButton("Get in!", null)
                .setNegativeButton("Cancel", null);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog)getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                if(statusCode == 200) {
                                    Gson JSONParser = new Gson();
                                    UserDTO user = JSONParser.fromJson(response.toString(), UserDTO.class);
                                    communicator.successfulLogin(user);
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                if (statusCode == 404) {
                                    errorText.setVisibility(View.VISIBLE);
                                }
                            }
                        };
                        LoginCredentialsDTO loginCredentials = new LoginCredentialsDTO();
                        loginCredentials.setEmail(emailText.getText().toString());
                        loginCredentials.setPassword(passwordText.getText().toString());
                        LoginController.login(loginCredentials, responseHandler);
                    }
                });
    }

    public interface Communicator {
        public void successfulLogin(UserDTO user);
    }
}
