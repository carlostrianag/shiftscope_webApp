/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.services;

import com.google.gson.Gson;
import com.ning.http.client.Response;
import java.util.ArrayList;
import shiftscope.criteria.FolderCriteria;
import shiftscope.dto.FolderCreationDTO;
import shiftscope.model.Folder;
import shiftscope.model.Track;
import shiftscope.netservices.HTTPService;

/**
 *
 * @author carlos
 */
public class FolderService {
    
    private static Gson JSONParser = new Gson();
    
    public static Response createFolder(Folder folder){
        JSONParser = new Gson();
        String object = JSONParser.toJson(folder);
        return HTTPService.HTTPPost("/folder/create", object);
    }
    
    public static Response createFolderTracks(FolderCreationDTO tracksToCreate) {
        JSONParser = new Gson();
        String object = JSONParser.toJson(tracksToCreate);
        return HTTPService.HTTPPost("/folder/createFolderTracks", object);
    }    


    public static Response getFolderFoldersById(FolderCriteria criteria) {
        return HTTPService.HTTPGet("/folder/getFolderFoldersById?id="+criteria.getId()+"&library="+criteria.getLibrary()+"&page="+criteria.getPage());
    }

    public static Response getFolderTracksById(FolderCriteria criteria) {
        return HTTPService.HTTPGet("/folder/getFolderTracksById?id="+criteria.getId()+"&page="+criteria.getPage());
    }

    public static Response getFolderParentId(FolderCriteria criteria) {
        return HTTPService.HTTPGet("/folder/getFolderParent?id="+criteria.getId()+"&library="+criteria.getLibrary());
    }

    public static Response getFolderContentById(FolderCriteria criteria) {
        return HTTPService.HTTPGet("/folder/getFolderContentById?id="+criteria.getId()+"&library="+criteria.getLibrary());
    }

    public static Response createFolderOptimized(FolderCreationDTO folder) {
        JSONParser = new Gson();
        String object = JSONParser.toJson(folder);
        return HTTPService.HTTPPost("/folder/createOptimized", object);
    }


}
