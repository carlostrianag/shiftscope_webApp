/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.listeners;

import javafx.application.Platform;
import shudder.views.MainView;

/**
 *
 * @author Carlos
 */
public abstract class PlayerListener {
    public void OnOpened(String totalTime, int totalSeconds) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnOpened('"+totalTime+"',"+totalSeconds+");");
            }
        });
    };
    public void OnProgress(String elapsedTime, int currentSecond) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnProgress('"+elapsedTime+"',"+currentSecond+");");
            }
        });    
    };
    public void OnVolumeChanged(int value) {};
    public void OnPlaying(String songName, String artistName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnPlaying('"+songName+"','"+artistName+"');");
            }
        });
    };
    public void OnQueueChanged() {};
}
