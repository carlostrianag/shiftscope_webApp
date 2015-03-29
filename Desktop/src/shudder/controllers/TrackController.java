/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shudder.controllers;

import com.ning.http.client.Response;
import shudder.criteria.TrackCriteria;

import shudder.netservices.HTTPService;

/**
 *
 * @author carlos
 */
public class TrackController {

    public static Response getTrackById(TrackCriteria criteria){
        return HTTPService.HTTPSyncGet("/track/getTrackById?id="+criteria.getId());
    }
    
}
