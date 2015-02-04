/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shudder.controllers;

import com.google.gson.Gson;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;
import java.util.ArrayList;
import javax.swing.SwingWorker;
import shudder.criteria.FolderCriteria;
import shudder.dto.FolderCreationDTO;
import shudder.dto.FolderDTO;
import shudder.listeners.FolderListener;
import shudder.model.Folder;
import shudder.netservices.HTTPService;
import shudder.util.SessionConstants;

/**
 *
 * @author carlos
 */
public class FolderController {
    
    private static ArrayList<FolderListener> listeners = new ArrayList<>();
    private static Gson JSONParser;
    
    
    public static void addListener(FolderListener listener) {
        listeners.add(listener);
    }
    
    public static Response createFolder(Folder folder){
        JSONParser = new Gson();
        String object = JSONParser.toJson(folder);
        return HTTPService.HTTPSyncPost("/folder/create", object);
    }
    
    public static void createFolderTracks(FolderCreationDTO folderTracks) {
        AsyncCompletionHandler<Void> responseHandler = new AsyncCompletionHandler<Void>() {

            @Override
            public Void onCompleted(Response response) throws Exception {
                System.out.println("Tracks creados asincronicamente ....");
                return null;
            }
        };
        JSONParser = new Gson();
        String object = JSONParser.toJson(folderTracks);
        HTTPService.HTTPPost("/folder/createFolderTracks", object, responseHandler);
    }    
    
    public static Response createFolderOptimized(FolderCreationDTO folder) {
        JSONParser = new Gson();
        String object = JSONParser.toJson(folder);
        return HTTPService.HTTPSyncPost("/folder/createOptimized", object);
    }
    
    public static void getFolderContentById(FolderCriteria criteria) {
        AsyncCompletionHandler<Void> responseHandler = new AsyncCompletionHandler<Void>() {
            @Override
            public Void onCompleted(Response response) throws Exception {
                if (response.getStatusCode() == 200) {
                    JSONParser = new Gson();
                    FolderDTO folderContent = JSONParser.fromJson(response.getResponseBody(), FolderDTO.class);
                    SessionConstants.PARENT_FOLDER_ID = folderContent.getParentFolder();
                    new FolderWorker(folderContent).execute();
                } else {
                    SessionConstants.PARENT_FOLDER_ID = -1;
                }
                return null;
            }
        };
        HTTPService.HTTPGet("/folder/getFolderContentById?id="+criteria.getId()+"&library="+criteria.getLibrary(), responseHandler);
        for (FolderListener listener : listeners) {
            listener.fetchingContent();
        }
    }
    
    public static void drawFolder(FolderDTO folderContent) {
        new FolderWorker(folderContent).execute();
    }
    
    private static class FolderWorker extends SwingWorker<Void, Void> {

        private FolderDTO fetchedFolder;

        public FolderWorker(FolderDTO fetchedFolder) {
            this.fetchedFolder = fetchedFolder;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            
            for (FolderListener listener : listeners) {
                listener.OnContentFetched(fetchedFolder);
            }
            return null;
        }
    }
}
