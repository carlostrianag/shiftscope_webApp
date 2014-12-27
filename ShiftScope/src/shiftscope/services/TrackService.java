/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.services;

import com.google.gson.Gson;
import com.ning.http.client.Response;
import shiftscope.criteria.TrackCriteria;
import shiftscope.model.Track;
import shiftscope.netservices.HTTPService;

/**
 *
 * @author carlos
 */
public class TrackService {
    private static Gson JSONParser;
    
    public static Response createTrack(Track t){
        JSONParser = new Gson();
        String object = JSONParser.toJson(t);
        return HTTPService.HTTPPost("/track/create", object);
    }

    public static Response getTrackById(TrackCriteria criteria) {
        return HTTPService.HTTPGet("/track/getTrackById?id="+criteria.getId());
    }

    public static Response searchTrack(TrackCriteria criteria) {
        return HTTPService.HTTPGet("/track/searchTrack?word="+criteria.getWord()+"&library="+criteria.getLibrary()+"&page="+criteria.getPage());
    }
}
