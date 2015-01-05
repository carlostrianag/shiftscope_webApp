package com.shiftscope.views;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.shiftscope.utils.Constants;
import com.shiftscope.utils.DrawerAdapter;
import com.shiftscope.views.fragments.LibraryFragment;

import shiftscope.com.shiftscope.R;


public class MainActivity extends ActionBarActivity {

    private ListView navListView;
    private AdapterView.OnItemClickListener navDrawerOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constants.init();
        DrawerAdapter navListAdapter = new DrawerAdapter(this, android.R.layout.simple_list_item_1, Constants.ENTRIES,  getLayoutInflater());
        navListView = (ListView)findViewById(R.id.drawerList);
        navListView.setAdapter(navListAdapter);
        navListView.setOnItemClickListener(navDrawerOnClickListener);
        LibraryFragment fragment = new LibraryFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainContent, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
