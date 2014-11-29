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
public class Statistic {
    private int id;
    private Track track;
    private ArrayList<Moment> moments;

    /**
     * @return the track
     */
    public Track getTrack() {
        return track;
    }

    /**
     * @param track the track to set
     */
    public void setTrack(Track track) {
        this.track = track;
    }

    /**
     * @return the moments
     */
    public ArrayList<Moment> getMoments() {
        return moments;
    }

    /**
     * @param moments the moments to set
     */
    public void setMoments(ArrayList<Moment> moments) {
        this.moments = moments;
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
