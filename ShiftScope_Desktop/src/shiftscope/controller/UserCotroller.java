/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.controller;

import com.ning.http.client.Response;
import shiftscope.model.User;
import shiftscope.services.UserService;
import shiftscope.util.LoginCredentials;

/**
 *
 * @author carlos
 */
public class UserCotroller {
    
    public static Response login(LoginCredentials credentials){
        return UserService.login(credentials);
    }
    
    public static Response createUser(User user){
        return UserService.createUser(user);
    }
}
