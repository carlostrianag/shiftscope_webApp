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
public class Folder {
    private int id;
    private int count;
    private float size;
    private String path;
    private String title;
    private int parentFolder;
    private int library;

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the size
     */
    public float getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(float size) {
        this.size = size;
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
        
}
