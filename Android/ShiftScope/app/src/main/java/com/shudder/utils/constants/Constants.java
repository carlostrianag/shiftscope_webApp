package com.shudder.utils.constants;

import com.shudder.utils.MenuEntry;

import java.util.ArrayList;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/3/2015.
 */
public class Constants {
    public static int MAX_X_POSITION;
    public static ArrayList<MenuEntry> ENTRIES = new ArrayList<>();

    public static void init(){
        MenuEntry library = new MenuEntry();
        library.setEntryText("Library");
        library.setImageId(R.drawable.inbox);
        ENTRIES.add(library);
        MenuEntry playlist = new MenuEntry();
        playlist.setEntryText("Playlist");
        playlist.setImageId(R.drawable.ic_headphones);
        ENTRIES.add(playlist);
        MenuEntry devices = new MenuEntry();
        devices.setEntryText("Devices");
        devices.setImageId(R.drawable.ic_device);
        ENTRIES.add(devices);

    }
}
