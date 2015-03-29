/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shudder.model;

import java.util.ArrayList;

/**
 *
 * @author carlos
 */
public class Library {
    private int id;
    private int user;
    private int device;
    private ArrayList<Folder> folders;
    
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

    /**
     * @return the folders
     */
    public ArrayList<Folder> getFolders() {
        return folders;
    }

    /**
     * @param folders the folders to set
     */
    public void setFolders(ArrayList<Folder> folders) {
        this.folders = folders;
    }
}
