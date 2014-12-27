/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.controller;

import com.ning.http.client.Response;
import shiftscope.criteria.LibraryCriteria;
import shiftscope.model.Library;
import shiftscope.services.LibraryService;

/**
 *
 * @author carlos
 */
public class LibraryController {
    public static Response createLibrary(Library library){
        return LibraryService.createLibrary(library);
    }
    
    public static Response getLibraryByDeviceId(LibraryCriteria criteria) {
        return LibraryService.getLibraryByDeviceId(criteria);
    }
}
