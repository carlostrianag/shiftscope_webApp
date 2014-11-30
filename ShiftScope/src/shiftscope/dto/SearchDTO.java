/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.dto;

import java.util.ArrayList;
import shiftscope.model.Track;

/**
 *
 * @author carlos
 */
public class SearchDTO {
    private ArrayList<Track> tracks;

    public SearchDTO() {
        tracks = new ArrayList<>();
    }

    /**
     * @return the tracks
     */
    public ArrayList<Track> getTracks() {
        return tracks;
    }

    /**
     * @param tracks the tracks to set
     */
    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }
    
    public void addTracks(ArrayList<Track> tracks) {
        this.tracks.addAll(tracks);
    }
}
