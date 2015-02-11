/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.listeners;

import java.util.ArrayList;
import shudder.dto.FolderDTO;
import shudder.model.Track;

/**
 *
 * @author Carlos
 */
public abstract class FolderListener {
    public abstract void OnProgressUpdated(int progress);
    public abstract void OnFilesScanned(int filesCount);
    public void fetchingContent(){};
    public void OnContentFetched(FolderDTO folderContent){};
    public abstract void OnError(String error);

    public void OnBuildFolderFinished() {
        
    }

    public void drawSearchResults(ArrayList<Track> tracks) {
        
    }
}
