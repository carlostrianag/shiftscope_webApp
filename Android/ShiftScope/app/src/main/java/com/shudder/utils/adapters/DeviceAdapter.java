package com.shudder.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shudder.R;
import com.shudder.dto.DeviceDTO;

import java.util.ArrayList;


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
        ImageView deviceStatus = (ImageView) v.findViewById(R.id.statusBox);
        deviceName.setText(device.getName());
        if(device.isOnline()) {
            deviceStatus.setBackground(getContext().getResources().getDrawable(R.drawable.radius_corners_fluor_pink));
        } else {
            deviceStatus.setBackground(getContext().getResources().getDrawable(R.drawable.radius_corners));
        }
        return v;
    }
}
