package shiftscope.model;

import java.util.ArrayList;

/**
 *
 * @author carlos
 */

public class LibraryElement {

    private int id;
    private String title;
    private String albumTitle;
    private String artist;
    private String absolutePath;
    private String parentFolder;
    private boolean isFolder;

    public LibraryElement() {
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
     * @param parentFolder the parentFolder to set
     */
    public void setParentFolder(String parentFolder) {
        this.parentFolder = parentFolder;
    }

    /**
     * @return the parentFolder
     */
    public String getParentFolder() {
        return parentFolder;
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
     * @return the albumTitle
     */
    public String getAlbumTitle() {
        return albumTitle;
    }

    /**
     * @param albumTitle the albumTitle to set
     */
    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
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
     * @return the isFolder
     */
    public boolean isIsFolder() {
        return isFolder;
    }

    /**
     * @param isFolder the isFolder to set
     */
    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }
    
    
}
