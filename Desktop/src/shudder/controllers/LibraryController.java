/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shudder.controllers;

import com.google.gson.Gson;
import com.ning.http.client.Response;
import shudder.criteria.LibraryCriteria;
import shudder.model.Library;
import shudder.netservices.HTTPService;

/**
 *
 * @author carlos
 */
public class LibraryController {
    private static Gson JSONParser;
    public static Response createLibrary(Library library){
        JSONParser = new Gson();
        String object = JSONParser.toJson(library);
        return HTTPService.HTTPSyncPost("/library/create", object);
    }
    
    public static Response getLibraryByDeviceId(LibraryCriteria criteria) {
        return HTTPService.HTTPSyncGet("/library/getLibraryByDeviceId?device="+criteria.getDevice());
    }
    
    public static void showDialog() {

    }
}
