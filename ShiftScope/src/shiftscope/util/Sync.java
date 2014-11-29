/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.util;

/**
 *
 * @author carlos
 */
public class Sync {
    private String currentSongName;
    private String currentSongArtist;
    private String currentSongDuration;
    private int currentSongId;
    private boolean isPlayingPlaylist;
    private boolean isPlaying;
    private boolean isPaused;

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
    
}
