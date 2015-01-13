package com.shiftscope.utils.comparators;

import com.shiftscope.dto.TrackDTO;

import java.util.Comparator;

/**
 * Created by Carlos on 1/11/2015.
 */
public class TitleComparator implements Comparator<TrackDTO> {

    @Override
    public int compare(TrackDTO t, TrackDTO t1) {
        return t.getTitle().toLowerCase().compareTo(t1.getTitle().toLowerCase());
    }
}
