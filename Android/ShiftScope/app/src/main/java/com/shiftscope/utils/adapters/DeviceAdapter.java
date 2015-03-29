package com.shiftscope.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shiftscope.dto.DeviceDTO;

import java.util.ArrayList;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/4/2015.
 */
public class DeviceAdapter extends ArrayAdapter<DeviceDTO> {
    private ArrayList<DeviceDTO> objects;
    private LayoutInflater inflater;
    public DeviceAdapter(Context context, int resource, ArrayList<DeviceDTO> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.objects = objects;
        this.inflater = inflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.item_device, parent, false);
        DeviceDTO device = objects.get(position);
        TextView deviceName = (TextView)v.findViewById(R.id.deviceNameText);
        ImageView deviceStatus = (ImageView) v.findViewById(R.id.deviceStatusImage);
        deviceName.setText(device.getName());
//        if(device.isOnline()) {
//            deviceStatus.setImageResource(R.drawable.ic_action_computer); //CAMBIAR POR ICONO ONLINE
//        } else {
//            deviceStatus.setImageResource(R.drawable.ic_action_computer);
//        }
        return v;
    }
}
