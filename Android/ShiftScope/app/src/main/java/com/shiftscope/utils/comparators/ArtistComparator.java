package com.shiftscope.utils.comparators;

import com.shiftscope.dto.TrackDTO;

import java.util.Comparator;

/**
 * Created by Carlos on 1/11/2015.
 */
public class ArtistComparator implements Comparator<TrackDTO> {
    @Override
    public int compare(TrackDTO t, TrackDTO t1) {
        int artistComparation = t.getArtist().toLowerCase().compareTo(t1.getArtist().toLowerCase());
        if (artistComparation != 0) {
            return artistComparation;
        } else {
            return t.getTitle().toLowerCase().compareTo(t1.getTitle().toLowerCase());
        }
    }
}
