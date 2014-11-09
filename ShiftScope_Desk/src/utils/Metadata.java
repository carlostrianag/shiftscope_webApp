package utils;

import java.util.ArrayList;
import shiftscope.model.LibraryElement;

/**
 *
 * @author carlos
 */
public class Metadata {
    private int id;
    private String absolutePath;
    private String currentSong;
    private String currentArtist;
    private String currentFolder;
    private String parentFolder;
    private ArrayList<LibraryElement> library;
    private ArrayList<LibraryElement> playlist;
    private boolean isPlaying;
    private int currentSongId;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the absolutePath
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * @param absolutePath the absolutePath to set
     */
    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    /**
     * @return the currentSong
     */
    public String getCurrentSong() {
        return currentSong;
    }

    /**
     * @param currentSong the currentSong to set
     */
    public void setCurrentSong(String currentSong) {
        this.currentSong = currentSong;
    }

    /**
     * @return the library
     */
    public ArrayList<LibraryElement> getLibrary() {
        return library;
    }

    /**
     * @param library the library to set
     */
    public void setLibrary(ArrayList<LibraryElement> library) {
        this.library = library;
    }

    /**
     * @return the currentArtist
     */
    public String getCurrentArtist() {
        return currentArtist;
    }

    /**
     * @param currentArtist the currentArtist to set
     */
    public void setCurrentArtist(String currentArtist) {
        this.currentArtist = currentArtist;
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
     * @return the parentFolder
     */
    public String getParentFolder() {
        return parentFolder;
    }

    /**
     * @param parentFolder the parentFolder to set
     */
    public void setParentFolder(String parentFolder) {
        this.parentFolder = parentFolder;
    }

    /**
     * @return the currentFolder
     */
    public String getCurrentFolder() {
        return currentFolder;
    }

    /**
     * @param currentFolder the currentFolder to set
     */
    public void setCurrentFolder(String currentFolder) {
        this.currentFolder = currentFolder;
    }

    /**
     * @return the playlist
     */
    public ArrayList<LibraryElement> getPlaylist() {
        return playlist;
    }

    /**
     * @param playlist the playlist to set
     */
    public void setPlaylist(ArrayList<LibraryElement> playlist) {
        this.playlist = playlist;
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
    
}

