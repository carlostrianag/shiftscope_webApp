/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.services;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import shiftscope.criteria.TrackCriteria;
import shiftscope.model.Track;
import shiftscope.netservices.HTTPService;

/**
 *
 * @author carlos
 */
public class TrackService {
    private static Gson JSONParser;
    
    public static HttpResponse createTrack(Track t){
        JSONParser = new Gson();
        String object = JSONParser.toJson(t);
        return HTTPService.HTTPPost("/track/create", object);
    }

    public static HttpResponse getTrackById(TrackCriteria criteria) {
        return HTTPService.HTTPGet("/track/getTrackById?id="+criteria.getId());
    }

    public static HttpResponse searchTrack(TrackCriteria criteria) {
        return HTTPService.HTTPGet("/track/searchTrack?word="+criteria.getWord()+"&library="+criteria.getLibrary()+"&page="+criteria.getPage());
    }
}
