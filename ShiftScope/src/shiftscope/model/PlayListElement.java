/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.model;

/**
 *
 * @author carlos
 */
public class PlayListElement {
    private int id;
    private Track track;
    private int order;

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
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
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
