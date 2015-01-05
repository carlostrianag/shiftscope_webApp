package com.shiftscope.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shiftscope.controllers.DeviceController;
import com.shiftscope.dto.DeviceDTO;
import com.shiftscope.utils.DeviceAdapter;
import com.shiftscope.utils.SessionConstants;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/4/2015.
 */
public class SelectDeviceActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{
    private ListView deviceListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);
        getSupportActionBar().setTitle("Select Device");
        deviceListView = (ListView) findViewById(R.id.deviceList);
        deviceListView.setOnItemClickListener(this);
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if(statusCode == 200) {
                    ArrayList<DeviceDTO> devices;
                    Gson JSONParser = new Gson();
                    devices = JSONParser.fromJson(response.toString(), new TypeToken<ArrayList<DeviceDTO>>(){}.getType());
                    DeviceAdapter adapter = new DeviceAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, devices, getLayoutInflater());
                    deviceListView.setAdapter(adapter);

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.v("MIO", responseString);
            }
        };
        DeviceController.getDevicesByUserId(SessionConstants.USER_ID, responseHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DeviceDTO device = (DeviceDTO)parent.getItemAtPosition(position);
        if(device.isOnline()) {
            SessionConstants.DEVICE_ID = device.getId();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
