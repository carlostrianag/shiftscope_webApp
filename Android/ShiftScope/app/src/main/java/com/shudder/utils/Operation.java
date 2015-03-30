package com.shudder.utils;

/**
 * Created by Carlos on 1/10/2015.
 */
public class Operation {
    private int operationType;
    private int id;
    private int userId;
    private int to;
    private int deviceIdentifier = 1;
    private int deviceId;
    private float value;
    private Sync sync;

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public void setDeviceIdentifier(int deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Sync getSync() {
        return sync;
    }

    public void setSync(Sync sync) {
        this.sync = sync;
    }
}
