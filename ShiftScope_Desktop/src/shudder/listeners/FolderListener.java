/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.listeners;

import shudder.dto.FolderDTO;

/**
 *
 * @author Carlos
 */
public abstract class FolderListener {
    public void fetchingContent(){};
    public void OnContentFetched(FolderDTO folderContent){};
    public abstract void OnError(String error);
}
