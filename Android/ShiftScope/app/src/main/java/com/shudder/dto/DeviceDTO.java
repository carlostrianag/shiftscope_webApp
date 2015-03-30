package com.shudder.dto;

/**
 * Created by Carlos on 1/4/2015.
 */
public class DeviceDTO {
    private int id;
    private int ownerUser;
    private String name;
    private String UUID;
    private boolean online;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(int ownerUser) {
        this.ownerUser = ownerUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
