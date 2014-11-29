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
public class Device {
    private int id;
    private String name;
    private String UUID;
    private boolean online;
    private int ownerUser;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the UUID
     */
    public String getUUID() {
        return UUID;
    }

    /**
     * @param UUID the UUID to set
     */
    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    /**
     * @return the online
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * @param online the online to set
     */
    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * @return the ownerUser
     */
    public int getOwnerUser() {
        return ownerUser;
    }

    /**
     * @param ownerUser the ownerUser to set
     */
    public void setOwnerUser(int ownerUser) {
        this.ownerUser = ownerUser;
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
