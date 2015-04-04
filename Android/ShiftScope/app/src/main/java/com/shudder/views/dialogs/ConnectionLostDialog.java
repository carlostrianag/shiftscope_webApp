package com.shudder.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.shudder.R;


/**
 * Created by Carlos on 31/03/2015.
 */
public class ConnectionLostDialog extends DialogFragment implements View.OnClickListener{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_two_buttons, null);
        Button positive = (Button)v.findViewById(R.id.positiveBtnText);
        positive.setOnClickListener(this);
        Button negative = (Button)v.findViewById(R.id.negativeBtnText);
        negative.setOnClickListener(this);
        builder.setView(v);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.positiveBtnText:
                getActivity().finish();
                break;
            case R.id.negativeBtnText:
                System.exit(0);
                break;
        }
    }
}
