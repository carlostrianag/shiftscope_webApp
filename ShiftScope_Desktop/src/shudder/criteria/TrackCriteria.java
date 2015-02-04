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
public class TrackCriteria {
    private int id;
    private String word;
    private int page;
    private int library;

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
     * @return the word
     */
    public String getWord() {
        return word;
    }

    /**
     * @param word the word to set
     */
    public void setWord(String word) {
        this.word = word;
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
