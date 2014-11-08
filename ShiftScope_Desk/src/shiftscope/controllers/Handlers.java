package shiftscope.controllers;

import java.io.File;
import java.util.ArrayList;
import org.farng.mp3.MP3File;
import shiftscope.model.LibraryElement;
import utils.IDGenerator;

public class Handlers {
    
    
    public static void buildLibraryTree(ArrayList<String> paths){
        for (String p : paths) {
            File f = new File(p);
            LibraryElement folder = new LibraryElement();
            folder.setIsFolder(true);
            folder.setAbsolutePath(p);
            folder.setId(IDGenerator.nextId());
            folder.setParentFolder("ROOT");
            folder.setTitle(f.getName());
            Library.library.add(folder);
            folderFetch(p, p);
            
        }
        
    }
    public static void folderFetch(String path, String parentFolder) {
        File f = new File(path);
        LibraryElement libraryElement;
        MP3File mp3;
        for (File file : f.listFiles()) {
            
            if(file.getName().endsWith(".mp3")) {
                libraryElement = new LibraryElement();
                try {
                    mp3 = new MP3File(file);
                } catch (Exception ex) {
                    continue;
                    //Logger.getLogger(Handlers.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    if (!mp3.getID3v1Tag().getSongTitle().equals("")){
                        libraryElement.setTitle(mp3.getID3v1Tag().getSongTitle());
                        libraryElement.setArtist(mp3.getID3v1Tag().getArtist());
                    } else {
                        libraryElement.setTitle(file.getName());
                    }

                }catch(Exception ex){
                    libraryElement.setTitle(file.getName());
                }
                    
                libraryElement.setId(IDGenerator.nextId());
                libraryElement.setParentFolder(parentFolder);
                libraryElement.setAbsolutePath(file.getAbsolutePath());
                libraryElement.setIsFolder(false);
                Library.library.add(libraryElement);
            } else if (file.isDirectory()) {
                libraryElement = new LibraryElement();
                libraryElement.setId(IDGenerator.nextId());
                libraryElement.setParentFolder(parentFolder);
                libraryElement.setAbsolutePath(file.getAbsolutePath());
                libraryElement.setTitle(file.getName());
                libraryElement.setIsFolder(true);
                Library.library.add(libraryElement);
                folderFetch(file.getAbsolutePath(), file.getAbsolutePath());
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
    
    public static String getParentFolderOf(String path) {
        for(LibraryElement l : Library.library) {
            if (l.isIsFolder() && l.getAbsolutePath().equals(path)) {
                return l.getParentFolder();
            }
        }
        return null;
    }
    
    public static LibraryElement getLibraryElementByAbsolutePath(String path) {
        for(LibraryElement l : Library.library) {
            if (l.getAbsolutePath().equals(path)) {
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
    
    
    public static ArrayList<LibraryElement> fetchLibraryByParentFolder(String parentFolder) {
        ArrayList<LibraryElement> fetched = new ArrayList();
        for (LibraryElement lE : Library.library) {
            if (lE.getParentFolder().equals(parentFolder)) {
                fetched.add(lE);
            }
        }
        System.out.println(fetched.size());
        return fetched;
    }
   
    public static ArrayList<LibraryElement> fetchLibrary() {
        return Library.library;
    }
}
