package com.shiftscope.views.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shiftscope.controllers.LoginController;
import com.shiftscope.dto.LoginCredentialsDTO;
import com.shiftscope.utils.interfaces.Communicator;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/4/2015.
 */
public class LoginDialog extends DialogFragment implements LoginController.LoginCommunicator {

    private TextView emailText;
    private TextView passwordText;
    private TextView errorText;
    private Communicator communicator;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator)activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LoginController.setCommunicator(this);
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
        return  builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog = (AlertDialog)getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginCredentialsDTO loginCredentials = new LoginCredentialsDTO();
                        loginCredentials.setEmail(emailText.getText().toString());
                        loginCredentials.setPassword(passwordText.getText().toString());
                        showProgressDialog();
                        LoginController.login(loginCredentials);
                    }
                });
    }
    @Override
    public void onSuccessfulLogin() {
        dismissProgressDialog();
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        communicator.nextActivity();
    }

    @Override
    public void onFailedLogin() {
        dismissProgressDialog();
        errorText.setVisibility(View.VISIBLE);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
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
