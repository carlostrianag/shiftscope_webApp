/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.listeners;

/**
 *
 * @author Carlos
 */
public abstract class PlayerListener {
    public void OnOpened(String totalTime, int totalSeconds) {};
    public void OnProgress(String elapsedTime, int currentSecond) {};
    public void OnVolumeChanged(int value) {};
    public void OnPlaying(String songName, String artistName) {};
    public void OnQueueChanged() {};
}
