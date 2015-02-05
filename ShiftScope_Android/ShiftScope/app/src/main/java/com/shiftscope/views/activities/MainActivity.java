package com.shiftscope.views.activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shiftscope.controllers.FolderController;
import com.shiftscope.netservices.TCPService;
import com.shiftscope.utils.Operation;
import com.shiftscope.utils.Sync;
import com.shiftscope.utils.adapters.DrawerAdapter;
import com.shiftscope.utils.constants.Constants;
import com.shiftscope.utils.constants.RequestTypes;
import com.shiftscope.utils.constants.SessionConstants;
import com.shiftscope.views.dialogs.VolumeDialog;
import com.shiftscope.views.fragments.LibraryFragment;

import shiftscope.com.shiftscope.R;


public class MainActivity extends ActionBarActivity implements TCPService.PlayerCommunicator, View.OnClickListener, SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener{

    private ListView navListView;
    private TextView currentSongText;
    private AdapterView.OnItemClickListener navDrawerOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
        }
    };

    private ImageView nextBtn;
    private ImageView playBtn;
    private ImageView backBtn;
    private ImageView stopBtn;
    private ImageView volumeBtn;
    private SearchView searchView;
    private VolumeDialog volumeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setLogo(R.drawable.logo_shudder);

        TCPService.setPlayerCommunicator(this);
        Constants.init();
        DrawerAdapter navListAdapter = new DrawerAdapter(this, android.R.layout.simple_list_item_1, Constants.ENTRIES,  getLayoutInflater());
        navListView = (ListView)findViewById(R.id.drawerList);
        navListView.setAdapter(navListAdapter);
        navListView.setOnItemClickListener(navDrawerOnClickListener);
        currentSongText = (TextView) findViewById(R.id.currentSongTextView);
        nextBtn = (ImageView) findViewById(R.id.nextBtn);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        playBtn = (ImageView) findViewById(R.id.pauseBtn);
        stopBtn = (ImageView) findViewById(R.id.stopBtn);
        volumeBtn = (ImageView) findViewById(R.id.volBtn);
        nextBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        volumeBtn.setOnClickListener(this);

        LibraryFragment fragment = new LibraryFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        MenuItem orderByArtist = menu.findItem(R.id.action_order_by_artist);
        orderByArtist.setOnMenuItemClickListener(this);
        MenuItem orderByTitle = menu.findItem(R.id.action_order_by_title);
        orderByTitle.setOnMenuItemClickListener(this);
        return true;
    }

    @Override
    public void onSync(Operation operation) {
        switch (operation.getOperationType()) {
            case RequestTypes.SYNC:
                Sync syncObject = operation.getSync();
                if(syncObject.getCurrentSongName() != null && syncObject.getCurrentSongArtist() != null) {
                    currentSongText.setText(syncObject.getCurrentSongName() + " - " + syncObject.getCurrentSongArtist());
                } else {
                    currentSongText.setText("");
                }

                SessionConstants.PLAYER_VOLUME = syncObject.getCurrentVolume();
                if(volumeDialog != null) {
                    volumeDialog.updateVolume();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Operation operation = new Operation();
        operation.setUserId(SessionConstants.USER_ID);
        operation.setTo(SessionConstants.DEVICE_ID);
        switch(v.getId()) {
            case R.id.backBtn:
                operation.setOperationType(RequestTypes.BACK);
                break;
            case R.id.nextBtn:
                operation.setOperationType(RequestTypes.NEXT);
                break;
            case R.id.stopBtn:
                operation.setOperationType(RequestTypes.STOP);
                break;
            case R.id.pauseBtn:
                operation.setOperationType(RequestTypes.PAUSE);
                break;
            case R.id.volBtn:
                volumeDialog = new VolumeDialog();
                volumeDialog.show(getSupportFragmentManager(), "VolumeDialog");
                break;
        }
        TCPService.send(operation);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        FolderController.search(newText);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_order_by_artist:
                FolderController.orderByArtist();
                break;
            case R.id.action_order_by_title:
                FolderController.orderByTitle();
                break;
        }
        return false;
    }
}
