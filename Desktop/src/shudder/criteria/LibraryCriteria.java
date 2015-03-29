/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shudder.criteria;

/**
 *
 * @author carlos
 */
public class LibraryCriteria {
    private int id;
    private int user;
    private int device;

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

    /**
     * @return the user
     */
    public int getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(int user) {
        this.user = user;
    }

    /**
     * @return the device
     */
    public int getDevice() {
        return device;
    }

    /**
     * @param device the device to set
     */
    public void setDevice(int device) {
        this.device = device;
    }
}
