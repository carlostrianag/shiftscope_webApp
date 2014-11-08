package shiftscope.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.farng.mp3.MP3File;
import shiftscope.model.LibraryElement;
import utils.IDGenerator;

public class Handlers {

    private static ArrayList<String> paths = new ArrayList<>();

    public static void buildLibraryTree() {
        Library.library = new ArrayList();
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

    public static void putFoldersFirst() {
        ArrayList<LibraryElement> folders = new ArrayList<>();
        ArrayList<LibraryElement> files = new ArrayList<>();
        for (LibraryElement l : Library.library) {
            if (l.isIsFolder()) {
                folders.add(l);
            } else {
                files.add(l);
            }
        }
        Library.library = new ArrayList<>(folders);
        Library.library.addAll(files);
    }

    public static void folderFetch(String path, String parentFolder) {
        File f = new File(path);
        LibraryElement libraryElement;
        MP3File mp3;
        if (f.exists()) {
            for (File file : f.listFiles()) {

                if (file.getName().endsWith(".mp3") && (!file.isHidden())) {
                    libraryElement = new LibraryElement();
                    try {
                        mp3 = new MP3File(file);
                    } catch (Exception ex) {
                        continue;
                        //Logger.getLogger(Handlers.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    try {
                        if (!mp3.getID3v1Tag().getSongTitle().equals("")) {
                            libraryElement.setTitle(mp3.getID3v1Tag().getSongTitle());
                            libraryElement.setArtist(mp3.getID3v1Tag().getArtist());
                        } else {
                            libraryElement.setTitle(file.getName());
                        }

                    } catch (Exception ex) {
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

    }

    public static LibraryElement getLibraryElementById(int id) {
        for (LibraryElement l : Library.library) {
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }

    public static String getParentFolderOf(String path) {
        for (LibraryElement l : Library.library) {
            if (l.isIsFolder() && l.getAbsolutePath().equals(path)) {
                return l.getParentFolder();
            }
        }
        return null;
    }

    public static LibraryElement getLibraryElementByAbsolutePath(String path) {
        for (LibraryElement l : Library.library) {
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
        return fetched;
    }

    public static ArrayList<LibraryElement> fetchLibrary() {
        return Library.library;
    }

    public static void addPaths(ArrayList<String> p) {
        paths.addAll(p);
    }

    public static void buildLibraryTreeFromFile() {
        File f = new File("library.txt");
        if (f.exists()) {
            try {
                Path p = FileSystems.getDefault().getPath("", "library.txt");
                if (!Files.readAllLines(p).isEmpty()) {
                    Handlers.addPaths((ArrayList<String>) Files.readAllLines(p));
                    buildLibraryTree();
                    putFoldersFirst();
                }
            } catch (IOException ex) {
                Logger.getLogger(Handlers.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Handlers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
