/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shudder.util;

import java.util.ArrayList;
import shudder.model.Track;

/**
 *
 * @author carlos
 */
public class Sync {
    //DEBERIA MANDAR UN CURRENT SONG TIPO TRACK Y YA
    private String currentSongName;
    private String currentSongArtist;
    private String currentSongDuration;
    private int currentSongId;
    private int currentVolume;
    private boolean isPlayingPlaylist;
    private boolean isPlaying;
    private boolean isPaused;
    private boolean newFolders;
    private ArrayList<Track> currentPlaylist;
    private Track addedTrack;
    private Track deletedTrack;

    /**
     * @return the currentSongName
     */
    public String getCurrentSongName() {
        return currentSongName;
    }

    /**
     * @param currentSongName the currentSongName to set
     */
    public void setCurrentSongName(String currentSongName) {
        this.currentSongName = currentSongName;
    }

    /**
     * @return the currentSongArtist
     */
    public String getCurrentSongArtist() {
        return currentSongArtist;
    }

    /**
     * @param currentSongArtist the currentSongArtist to set
     */
    public void setCurrentSongArtist(String currentSongArtist) {
        this.currentSongArtist = currentSongArtist;
    }

    /**
     * @return the currentSongDuration
     */
    public String getCurrentSongDuration() {
        return currentSongDuration;
    }

    /**
     * @param currentSongDuration the currentSongDuration to set
     */
    public void setCurrentSongDuration(String currentSongDuration) {
        this.currentSongDuration = currentSongDuration;
    }

    /**
     * @return the currentSongId
     */
    public int getCurrentSongId() {
        return currentSongId;
    }

    /**
     * @param currentSongId the currentSongId to set
     */
    public void setCurrentSongId(int currentSongId) {
        this.currentSongId = currentSongId;
    }

    /**
     * @return the currentVolume
     */
    public int getCurrentVolume() {
        return currentVolume;
    }

    /**
     * @param currentVolume the currentVolume to set
     */
    public void setCurrentVolume(int currentVolume) {
        this.currentVolume = currentVolume;
    }

    /**
     * @return the isPlayingPlaylist
     */
    public boolean isIsPlayingPlaylist() {
        return isPlayingPlaylist;
    }

    /**
     * @param isPlayingPlaylist the isPlayingPlaylist to set
     */
    public void setIsPlayingPlaylist(boolean isPlayingPlaylist) {
        this.isPlayingPlaylist = isPlayingPlaylist;
    }

    /**
     * @return the isPlaying
     */
    public boolean isIsPlaying() {
        return isPlaying;
    }

    /**
     * @param isPlaying the isPlaying to set
     */
    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    /**
     * @return the isPaused
     */
    public boolean isIsPaused() {
        return isPaused;
    }

    /**
     * @param isPaused the isPaused to set
     */
    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    /**
     * @return the newFolders
     */
    public boolean isNewFolders() {
        return newFolders;
    }

    /**
     * @param newFolders the newFolders to set
     */
    public void setNewFolders(boolean newFolders) {
        this.newFolders = newFolders;
    }

    /**
     * @return the currentPlaylist
     */
    public ArrayList<Track> getCurrentPlaylist() {
        return currentPlaylist;
    }

    /**
     * @param currentPlaylist the currentPlaylist to set
     */
    public void setCurrentPlaylist(ArrayList<Track> currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    /**
     * @return the addedTrack
     */
    public Track getAddedTrack() {
        return addedTrack;
    }

    /**
     * @param addedTrack the addedTrack to set
     */
    public void setAddedTrack(Track addedTrack) {
        this.addedTrack = addedTrack;
    }

    /**
     * @return the deletedTrack
     */
    public Track getDeletedTrack() {
        return deletedTrack;
    }

    /**
     * @param deletedTrack the deletedTrack to set
     */
    public void setDeletedTrack(Track deletedTrack) {
        this.deletedTrack = deletedTrack;
    }

    
}
