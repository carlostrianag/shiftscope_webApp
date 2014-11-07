package shiftscope.controllers;

import com.google.gson.Gson;
import java.io.File;

/**
 *
 * @author carlos
 */
public class FolderFetcher {
    Gson gSonParser;

    public FolderFetcher() {
        gSonParser = new Gson();
    }
    
    

    public String JSONfolderFetch(String folderPath) {
        File folder = new File(folderPath);
        String files[] = new String[10];
        if (folder.isDirectory()) {
            files = folder.list();
//            for (int i = 0; i < files.length; i++) {
//                System.out.println(files[i]);
//            }
        }
        return gSonParser.toJson(files);
    }
    
    public File[] folderFetch(String folderPath) {
        File folder = new File(folderPath);
        File files[] = new File[0];
        if (folder.isDirectory()) {
            files = folder.listFiles();
        }
        return files;
    }
    
}
