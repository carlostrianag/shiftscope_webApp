/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.model;

import java.util.Date;
import shiftscope.util.Weekday;

/**
 *
 * @author carlos
 */
public class Moment {
    private int id;
    private Date dateTime;
    private Weekday weekday;
    //private String weather;

    /**
     * @return the dateTime
     */
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * @param dateTime the dateTime to set
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return the weekday
     */
    public Weekday getWeekday() {
        return weekday;
    }

    /**
     * @param weekday the weekday to set
     */
    public void setWeekday(Weekday weekday) {
        this.weekday = weekday;
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
