/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.services;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import shiftscope.model.User;
import shiftscope.netservices.HTTPService;
import shiftscope.util.LoginCredentials;

/**
 *
 * @author carlos
 */
public class UserService {
    private static Gson JSONParser;
    
    public static HttpResponse login(LoginCredentials credentials){
        JSONParser = new Gson();
        String object = JSONParser.toJson(credentials);
        return HTTPService.HTTPPost("/user/login", object);
    }

    public static HttpResponse createUser(User user) {
        JSONParser = new Gson();
        String object = JSONParser.toJson(user);
        return HTTPService.HTTPPost("/user/create", object);        
    }
}
