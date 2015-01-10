/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.controller;

import com.ning.http.client.Response;
import shiftscope.criteria.FolderCriteria;
import shiftscope.dto.FolderCreationDTO;
import shiftscope.model.Folder;
import shiftscope.services.FolderService;

/**
 *
 * @author carlos
 */
public class FolderController {
    
    public static Response createFolder(Folder folder){
        return FolderService.createFolder(folder);
    }
    
    public static Response createFolderTracks(FolderCreationDTO folderTracks) {
        return FolderService.createFolderTracks(folderTracks);
    }    
    
    public static Response createFolderOptimized(FolderCreationDTO folder) {
        return FolderService.createFolderOptimized(folder);
    }
    
    public static Response getFolderFoldersById(FolderCriteria criteria){
        return FolderService.getFolderFoldersById(criteria);
    }
    
    public static Response getFolderTracksById(FolderCriteria criteria){
        return FolderService.getFolderTracksById(criteria);
    }    
    
    public static Response getFolderParentId(FolderCriteria criteria){
        return FolderService.getFolderParentId(criteria);
    }
    
    public static Response getFolderContentById(FolderCriteria criteria) {
        return FolderService.getFolderContentById(criteria);
    }


}
