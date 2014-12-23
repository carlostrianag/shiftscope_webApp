/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.controller;

import org.apache.http.HttpResponse;
import shiftscope.criteria.FolderCriteria;
import shiftscope.model.Folder;
import shiftscope.services.FolderService;

/**
 *
 * @author carlos
 */
public class FolderController {
    
    public static HttpResponse createFolder(Folder folder){
        return FolderService.createFolder(folder);
    }
    
    public static HttpResponse getFolderFoldersById(FolderCriteria criteria){
        return FolderService.getFolderFoldersById(criteria);
    }
    
    public static HttpResponse getFolderTracksById(FolderCriteria criteria){
        return FolderService.getFolderTracksById(criteria);
    }    
    
    public static HttpResponse getFolderParentId(FolderCriteria criteria){
        return FolderService.getFolderParentId(criteria);
    }
}
