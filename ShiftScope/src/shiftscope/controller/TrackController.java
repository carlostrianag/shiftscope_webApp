/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.controller;

import com.ning.http.client.Response;
import shiftscope.criteria.TrackCriteria;
import shiftscope.model.Track;
import shiftscope.services.TrackService;

/**
 *
 * @author carlos
 */
public class TrackController {
    public static Response createTrack(Track t) {
        return TrackService.createTrack(t);
    }
    
    public static Response getTrackById(TrackCriteria criteria){
        return TrackService.getTrackById(criteria);
    }
    
    public static Response searchTrack(TrackCriteria criteria) {
        return TrackService.searchTrack(criteria);
    }
}
