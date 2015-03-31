package com.shudder.listeners;

import com.shudder.dto.TrackDTO;

/**
 * Created by Carlos on 30/03/2015.
 */
public abstract class LibraryListener {
    public abstract void OnSuccessfulLibraryFetch();
    public abstract void OnQueueChanged(TrackDTO addedTrack, TrackDTO deletedTrack);
    public abstract void OnError();
}