/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.services;

import com.google.gson.Gson;
import com.ning.http.client.Response;
import shiftscope.criteria.LibraryCriteria;
import shiftscope.model.Library;
import shiftscope.netservices.HTTPService;

/**
 *
 * @author carlos
 */
public class LibraryService {
    private static Gson JSONParser;
    
    public static Response createLibrary(Library library){
        JSONParser = new Gson();
        String object = JSONParser.toJson(library);
        return HTTPService.HTTPPost("/library/create", object);
    }
    
    public static Response getLibraryByDeviceId(LibraryCriteria criteria){
        return HTTPService.HTTPGet("/library/getLibraryByDeviceId?device="+criteria.getDevice());
    }     
}
