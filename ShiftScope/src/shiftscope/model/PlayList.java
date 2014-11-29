/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.model;

import java.util.ArrayList;

/**
 *
 * @author carlos
 */
public class PlayList {
    private int id;
    private ArrayList<PlayListElement> tracks;  
    /**
     * @return the tracks
     */
    public ArrayList<PlayListElement> getTracks() {
        return tracks;
    }

    /**
     * @param tracks the tracks to set
     */
    public void setTracks(ArrayList<PlayListElement> tracks) {
        this.tracks = tracks;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
}
