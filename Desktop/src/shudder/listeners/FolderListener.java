/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.listeners;

import com.google.gson.Gson;
import java.util.ArrayList;
import javafx.application.Platform;
import shudder.dto.FolderDTO;
import shudder.model.Track;
import shudder.views.MainView;

/**
 *
 * @author Carlos
 */
public abstract class FolderListener {
    public void OnProgressUpdated(int progress){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnProgressUpdated("+progress+");");
            }
        });
    };
    public void OnFilesScanned(int filesCount){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnFilesScanned("+filesCount+");");
            }
        });    
    };
    public void OnContentFetched(FolderDTO folderContent){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Gson JSONParser = new Gson();
                String JSONObject = JSONParser.toJson(folderContent);
                MainView.mainBrowser.execute("OnLoaded(); OnContentFetched("+JSONObject+");");
            }
        });
    };
    
    public void OnError(String error) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnError('"+error+"');");
            }
        });    
    };

    public void OnBuildFolderFinished() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnBuildFolderFinished();");
            }
        });
    }
    
    public void OnLoading() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnLoading();");
            }
        });        
    }
    
    public void OnLoaded() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnLoaded();");
            }
        });        
    }
    
    public void OnFolderDeleted(String deletedFolder) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnFolderDeleted("+deletedFolder+");");
            }
        });
    }

    public void drawSearchResults(ArrayList<Track> tracks) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Gson JSONParser = new Gson();
                String JSONObject = JSONParser.toJson(tracks);
                MainView.mainBrowser.execute("drawSearchResults("+JSONObject+");");
            }
        });        
    }
}
