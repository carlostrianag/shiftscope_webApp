/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.services;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import shiftscope.criteria.FolderCriteria;
import shiftscope.model.Folder;
import shiftscope.netservices.HTTPService;

/**
 *
 * @author carlos
 */
public class FolderService {
    
    private static Gson JSONParser = new Gson();
    
    public static HttpResponse createFolder(Folder folder){
        JSONParser = new Gson();
        String object = JSONParser.toJson(folder);
        return HTTPService.HTTPPost("/folder/create", object);
    }


    public static HttpResponse getFolderFoldersById(FolderCriteria criteria) {
        return HTTPService.HTTPGet("/folder/getFolderFoldersById?id="+criteria.getId()+"&library="+criteria.getLibrary()+"&page="+criteria.getPage());
    }

    public static HttpResponse getFolderTracksById(FolderCriteria criteria) {
        return HTTPService.HTTPGet("/folder/getFolderTracksById?id="+criteria.getId()+"&page="+criteria.getPage());
    }

    public static HttpResponse getFolderParentId(FolderCriteria criteria) {
        return HTTPService.HTTPGet("/folder/getFolderParent?id="+criteria.getId()+"&library="+criteria.getLibrary());
    }
}
