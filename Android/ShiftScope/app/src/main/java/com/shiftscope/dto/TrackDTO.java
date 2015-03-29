package com.shiftscope.dto;

/**
 * Created by Carlos on 1/4/2015.
 */
public class TrackDTO {

    private int id;
    private int parentFolder;
    private int library;
    private String path;
    private String genre;
    private String artist;
    private String title;
    private String duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(int parentFolder) {
        this.parentFolder = parentFolder;
    }

    public int getLibrary() {
        return library;
    }

    public void setLibrary(int library) {
        this.library = library;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
