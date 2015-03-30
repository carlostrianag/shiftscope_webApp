package com.shudder.dto;

/**
 * Created by Carlos on 1/4/2015.
 */
public class FolderDTO {

    private int id;
    private int parentFolder;
    private int library;
    private int count;
    private float size;
    private String title;
    private String path;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
