package com.shudder.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 31/03/2015.
 */
public class ConnectionLostDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_two_buttons, null);
        builder.setView(v);
        return builder.create();
    }
}
