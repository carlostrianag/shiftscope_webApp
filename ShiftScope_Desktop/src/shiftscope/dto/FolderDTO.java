/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.dto;

import java.util.ArrayList;
import shiftscope.model.Folder;
import shiftscope.model.Track;

/**
 *
 * @author carlos
 */
public class FolderDTO {
    //private String title;
    private ArrayList<Track> tracks;
    private ArrayList<Folder> folders;
    private int parentFolder;   

    public FolderDTO() {
        tracks = new ArrayList<>();
        folders = new ArrayList<>();
    }
    

    
    /**
     * @return the parentFolder
     */
    public int getParentFolder() {
        return parentFolder;
    }

    /**
     * @param parentFolder the parentFolder to set
     */
    public void setParentFolder(int parentFolder) {
        this.parentFolder = parentFolder;
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

    /**
     * @return the folders
     */
    public ArrayList<Folder> getFolders() {
        return folders;
    }

    /**
     * @param folders the folders to set
     */
    public void setFolders(ArrayList<Folder> folders) {
        this.folders = folders;
    }
    
    public void addFolders(ArrayList<Folder> folders){
        this.folders.addAll(folders);
    }
    
    public void addTracks(ArrayList<Track> tracks){
        this.tracks.addAll(tracks);
    }
}
