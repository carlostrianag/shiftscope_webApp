package com.shiftscope.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.shiftscope.netservices.TCPService;
import com.shiftscope.utils.Operation;
import com.shiftscope.utils.constants.RequestTypes;
import com.shiftscope.utils.constants.SessionConstants;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/10/2015.
 */
public class VolumeDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener{

    private SeekBar volumeSeekBar;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_volume, null);
        builder.setView(v);
        Dialog dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.y = 120;
        layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(layoutParams);
        volumeSeekBar = (SeekBar) v.findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setOnSeekBarChangeListener(this);
        return dialog;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (SessionConstants.VOLUME_FROM_USER) {
            float progressF = new Float(progress);
            float volValue = progressF/100f;
            Operation operation = new Operation();
            operation.setUserId(SessionConstants.USER_ID);
            operation.setTo(SessionConstants.DEVICE_ID);
            operation.setOperationType(RequestTypes.SET_VOLUME);
            operation.setValue(volValue);
            TCPService.send(operation);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void updateVolume() {
        Log.v("VOLUME", "ENTROOO");
        volumeSeekBar.setProgress(SessionConstants.PLAYER_VOLUME);
    }
}
