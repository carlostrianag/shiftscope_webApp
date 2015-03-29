/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.dto;

import java.util.ArrayList;
import shudder.model.Folder;
import shudder.model.Track;

/**
 *
 * @author Carlos
 */
public class FolderCreationDTO {
    private Folder folder;
    private ArrayList<Track> tracks;

    /**
     * @return the folder
     */
    public Folder getFolder() {
        return folder;
    }

    /**
     * @param folder the folder to set
     */
    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    /**
     * @return the tracks
     */
    public ArrayList<Track> getTracks() {
        return tracks;
    }

    /**
     * @param tracks the tracks to set
     */
    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }
    
}
