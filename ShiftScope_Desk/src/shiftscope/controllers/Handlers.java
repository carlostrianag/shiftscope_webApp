package shiftscope.controllers;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import shiftscope.model.LibraryElement;
import utils.IDGenerator;

public class Handlers {
    private static Gson JSONParser = new Gson();
    
    public static void folderFetch(String path) {
        File f = new File(path);
        for (File file : f.listFiles()) {
            if(file.getName().endsWith(".mp3")) {
                try {
                    MP3File mp3 = new MP3File(file);
                    LibraryElement libraryElement = new LibraryElement();
                    libraryElement.setId(IDGenerator.nextId());
                    libraryElement.setParentFolder(path);
                    libraryElement.setAbsolutePath(file.getAbsolutePath());
                    libraryElement.setTitle(mp3.getID3v1Tag().getSongTitle());
                    libraryElement.setArtist(mp3.getID3v1Tag().getArtist());
                    Library.library.add(libraryElement);
                } catch (IOException ex) {
                    Logger.getLogger(Handlers.class.getName()).log(Level.SEVERE, null, ex);
                } catch (TagException ex) {
                    Logger.getLogger(Handlers.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
       
    }
    
    public static LibraryElement getLibraryElementById(int id) {
        for(LibraryElement l : Library.library){
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }
    
    public static void playSong(int id, String path) {
        ShiftScopePlayer.play(path);
    }
    
    public static void pause() {
        ShiftScopePlayer.pause();
    }
    
    public static void resume() {
        ShiftScopePlayer.resume();
    }
    
    public static ArrayList<LibraryElement> fetchLibrary() {
        return Library.library;
    }
}
