/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.listeners;

import com.google.gson.Gson;
import java.util.ArrayList;
import javafx.application.Platform;
import shudder.model.Track;
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
    
    public void OnPlaylistFetched(ArrayList<Track> playlist){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Gson JSONParser = new Gson();
                String JSONObject = JSONParser.toJson(playlist);
                MainView.mainBrowser.execute("OnPlaylistFetched("+JSONObject+");");
            }
        });
    };
    
    public void OnVolumeChanged(float value) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnVolumeChanged("+value+");");
            }
        });
    };
    public void OnPlaying(String songName, String artistName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnPlaying(\""+songName+"\",\""+artistName+"\");");
            }
        });
    };
    public void OnQueueChanged(Track addedTrack, Track deletedTrack) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Gson JSONParser = new Gson();
                if (addedTrack != null) {
                    MainView.mainBrowser.execute("OnQueueChanged("+JSONParser.toJson(addedTrack)+", null);");
                } else {
                    MainView.mainBrowser.execute("OnQueueChanged(null,"+JSONParser.toJson(deletedTrack)+");");
                }
                
                
            }
        });
    };
    
    public void OnPlayed() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnPlayed();");
            }
        });
    }
    
    public void OnPaused() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnPaused();");
            }
        });
    }   
    
    public void OnStopped() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnStopped();");
            }
        });
    }       
}
