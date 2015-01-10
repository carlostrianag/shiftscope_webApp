/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shiftscope.util.comparators;

import java.util.Comparator;
import shiftscope.model.Track;

/**
 *
 * @author Carlos
 */
public class TitleComparator implements Comparator<Track>{

    @Override
    public int compare(Track t, Track t1) {
        return t.getTitle().toLowerCase().compareTo(t1.getTitle().toLowerCase());
    }
    
}
