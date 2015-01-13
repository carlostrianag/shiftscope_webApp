package com.shiftscope.dto;

import java.util.ArrayList;

/**
 * Created by Carlos on 1/4/2015.
 */
public class FolderContentDTO {
    private int parentFolder;
    private ArrayList<FolderDTO> folders;
    private ArrayList<TrackDTO> tracks;

    public FolderContentDTO() {
        folders = new ArrayList<>();
        tracks = new ArrayList<>();
    }

    public int getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(int parentFolder) {
        this.parentFolder = parentFolder;
    }

    public ArrayList<FolderDTO> getFolders() {
        return folders;
    }

    public void setFolders(ArrayList<FolderDTO> folders) {
        this.folders = folders;
    }

    public ArrayList<TrackDTO> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<TrackDTO> tracks) {
        this.tracks = tracks;
    }
}
