package com.shudder.views.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shudder.R;
import com.shudder.controllers.DeviceController;
import com.shudder.dto.DeviceDTO;
import com.shudder.listeners.DeviceListener;
import com.shudder.netservices.TCPService;
import com.shudder.utils.adapters.DeviceAdapter;
import com.shudder.utils.constants.SessionConstants;

import java.util.ArrayList;


/**
 * Created by Carlos on 1/4/2015.
 */
public class SelectDeviceActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{

    private ListView deviceListView;
    private ProgressDialog progressDialog;
    private DeviceListener deviceListener;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);
        getSupportActionBar().setTitle("Select Device");
        deviceListView = (ListView) findViewById(R.id.deviceList);
        deviceListView.setOnItemClickListener(this);
        deviceListener = new DeviceListener() {

            @Override
            public void OnLoading() {
                super.OnLoading();
                showProgressDialog();
            }

            @Override
            public void OnLoaded() {
                super.OnLoaded();
                dismissProgressDialog();
            }

            @Override
            public void OnSuccessfulDeviceFetch(ArrayList<DeviceDTO> devices) {
                super.OnSuccessfulDeviceFetch(devices);
                DeviceAdapter adapter = new DeviceAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, devices, getLayoutInflater());
                deviceListView.setAdapter(adapter);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        DeviceController.addListener(deviceListener);
        DeviceController.getDevicesByUserId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_select_device, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_refresh:
                DeviceController.getDevicesByUserId();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DeviceDTO device = (DeviceDTO)parent.getItemAtPosition(position);
        if(device.isOnline()) {
            SessionConstants.DEVICE_ID = device.getId();
            TCPService.initTCPService();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading, please wait ...");
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
