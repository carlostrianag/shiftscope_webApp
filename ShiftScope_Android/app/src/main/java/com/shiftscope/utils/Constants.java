package com.shiftscope.utils;

import java.util.ArrayList;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/3/2015.
 */
public class Constants {
    public static ArrayList<MenuEntry> ENTRIES = new ArrayList<>();

    public static void init(){
        MenuEntry library = new MenuEntry();
        library.setEntryText("Library");
        library.setImageId(R.drawable.inbox);
        ENTRIES.add(library);
        MenuEntry playlist = new MenuEntry();
        playlist.setEntryText("Playlist");
        playlist.setImageId(R.drawable.music);
        ENTRIES.add(playlist);
        MenuEntry devices = new MenuEntry();
        devices.setEntryText("Devices");
        devices.setImageId(R.drawable.laptop);
        ENTRIES.add(devices);

    }
}
