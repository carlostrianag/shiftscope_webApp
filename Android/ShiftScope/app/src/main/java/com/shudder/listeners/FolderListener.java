package com.shudder.listeners;

import android.os.Parcelable;

import com.shudder.utils.adapters.LibraryAdapter;

/**
 * Created by Carlos on 31/03/2015.
 */
public abstract class FolderListener {
    public void OnSuccessfulFolderFetch(LibraryAdapter adapter, Parcelable restoredState) {OnLoaded();};
    public abstract void OnLoading();
    public abstract void OnLoaded();
    public abstract void OnFailedFolderFetch();
    public abstract void OnOrder(LibraryAdapter adapter);
}