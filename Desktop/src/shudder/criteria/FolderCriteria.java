/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shudder.criteria;

/**
 *
 * @author carlos
 */
public class FolderCriteria {
    private int id;
    private int parentFolder;
    private int library;
    private int page;

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

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }
}
