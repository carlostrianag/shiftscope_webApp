package com.shiftscope.utils;

import com.shiftscope.dto.TrackDTO;

import java.util.ArrayList;

/**
 * Created by Carlos on 1/10/2015.
 */
public class Sync {
    private String currentSongName;
    private String currentSongArtist;
    private String currentSongDuration;
    private int currentSongId;
    private int currentVolume;
    private boolean isPlayingPlaylist;
    private boolean isPlaying;
    private boolean isPaused;
    private boolean newFolders;
    private TrackDTO addedTrack;
    private TrackDTO deletedTrack;
    private ArrayList<TrackDTO> currentPlaylist;

    public String getCurrentSongName() {
        return currentSongName;
    }

    public void setCurrentSongName(String currentSongName) {
        this.currentSongName = currentSongName;
    }

    public String getCurrentSongArtist() {
        return currentSongArtist;
    }

    public void setCurrentSongArtist(String currentSongArtist) {
        this.currentSongArtist = currentSongArtist;
    }

    public String getCurrentSongDuration() {
        return currentSongDuration;
    }

    public void setCurrentSongDuration(String currentSongDuration) {
        this.currentSongDuration = currentSongDuration;
    }

    public int getCurrentSongId() {
        return currentSongId;
    }

    public void setCurrentSongId(int currentSongId) {
        this.currentSongId = currentSongId;
    }

    public int getCurrentVolume() {
        return currentVolume;
    }

    public void setCurrentVolume(int currentVolume) {
        this.currentVolume = currentVolume;
    }

    public boolean isPlayingPlaylist() {
        return isPlayingPlaylist;
    }

    public void setPlayingPlaylist(boolean isPlayingPlaylist) {
        this.isPlayingPlaylist = isPlayingPlaylist;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    public boolean isNewFolders() {
        return newFolders;
    }

    public void setNewFolders(boolean newFolders) {
        this.newFolders = newFolders;
    }

    public TrackDTO getAddedTrack() {
        return addedTrack;
    }

    public void setAddedTrack(TrackDTO addedTrack) {
        this.addedTrack = addedTrack;
    }

    public TrackDTO getDeletedTrack() {
        return deletedTrack;
    }

    public void setDeletedTrack(TrackDTO deletedTrack) {
        this.deletedTrack = deletedTrack;
    }

    public ArrayList<TrackDTO> getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(ArrayList<TrackDTO> currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }
}
