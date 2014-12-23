/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.util;

/**
 *
 * @author carlos
 */
public class Operation {
    private int operationType;
    private int id;
    private int userId;
    private int to;
    private int deviceIdentifier = 2;
    private int deviceId;
    private float value;
    private Sync sync;

    /**
     * @return the operationType
     */
    public int getOperationType() {
        return operationType;
    }

    /**
     * @param operationType the operationType to set
     */
    public void setOperationType(int operationType) {
        this.operationType = operationType;
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

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the to
     */
    public int getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(int to) {
        this.to = to;
    }

    /**
     * @return the deviceIdentifier
     */
    public int getDeviceIdentifier() {
        return deviceIdentifier;
    }

    /**
     * @param deviceIdentifier the deviceIdentifier to set
     */
    public void setDeviceIdentifier(int deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    /**
     * @return the deviceId
     */
    public int getDeviceId() {
        return deviceId;
    }

    /**
     * @param deviceId the deviceId to set
     */
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * @return the sync
     */
    public Sync getSync() {
        return sync;
    }

    /**
     * @param sync the sync to set
     */
    public void setSync(Sync sync) {
        this.sync = sync;
    }

    /**
     * @return the value
     */
    public float getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(float value) {
        this.value = value;
    }
    
    
    
}
