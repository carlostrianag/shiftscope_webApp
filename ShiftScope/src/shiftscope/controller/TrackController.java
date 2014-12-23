/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.controller;

import org.apache.http.HttpResponse;
import shiftscope.criteria.TrackCriteria;
import shiftscope.model.Track;
import shiftscope.services.TrackService;

/**
 *
 * @author carlos
 */
public class TrackController {
    public static HttpResponse createTrack(Track t) {
        return TrackService.createTrack(t);
    }
    
    public static HttpResponse getTrackById(TrackCriteria criteria){
        return TrackService.getTrackById(criteria);
    }
    
    public static HttpResponse searchTrack(TrackCriteria criteria) {
        return TrackService.searchTrack(criteria);
    }
}
