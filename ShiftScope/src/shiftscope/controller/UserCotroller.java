/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiftscope.controller;

import org.apache.http.HttpResponse;
import shiftscope.services.UserService;
import shiftscope.util.LoginCredentials;

/**
 *
 * @author carlos
 */
public class UserCotroller {
    
    public static HttpResponse login(LoginCredentials credentials){
        return UserService.login(credentials);
    }
}
