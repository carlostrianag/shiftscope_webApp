/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shudder.model;

/**
 *
 * @author carlos
 */
public class Track implements Comparable<Track>{
    private int id;
    private String path;
    private String genre;
    private String artist;
    private String title;
    private String duration;
    private int parentFolder;
    private int library;

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * @param genre the genre to set
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * @param artist the artist to set
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

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
     * @return the library
     */
    public int getLibrary() {
        return library;
    }

    /**
     * @param library the library to set
     */
    public void setLibrary(int library) {
        this.library = library;
    }

    @Override
    public int compareTo(Track o) {
        return getTitle().toLowerCase().compareTo(o.getTitle().toLowerCase());
    }
    
}
