package com.shiftscope.controllers;

import android.util.Log;

import com.shiftscope.dto.TrackDTO;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Carlos on 26/03/2015.
 */
public class PlaylistController {
    private static HashMap<Integer, TrackDTO> queue = new HashMap<>();

    public static void setPlaylist(ArrayList<TrackDTO> tracks) {
        queue = new HashMap<>();
        if(tracks != null) {
            for(TrackDTO track : tracks) {
                queue.put(track.getId(), track);
            }
        }
    }

    public static boolean contains(int id) {
        return queue.containsKey(id);
    }
}
