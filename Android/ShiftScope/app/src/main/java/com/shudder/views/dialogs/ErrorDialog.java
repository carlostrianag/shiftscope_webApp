package com.shudder.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 31/03/2015.
 */
public class ErrorDialog extends DialogFragment {


    private String messageText;
    public static ErrorDialog newInstance(String messageText) {
        ErrorDialog dialog = new ErrorDialog();
        Bundle args = new Bundle();
        args.putString("message", messageText);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_one_button, null);
        TextView messageText = (TextView) v.findViewById(R.id.textMessage);
        messageText.setText(getArguments().getString("message"));
        builder.setView(v);
        return builder.create();
    }

}
