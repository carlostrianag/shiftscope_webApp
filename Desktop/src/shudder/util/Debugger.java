/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.util;

import shudder.views.MainView;

/**
 *
 * @author Carlos
 */
public class Debugger {    
    
    public void display(String message) {
        System.err.println(message);
    }
    
    public String openFile(String page) {
        return MainView.mainBrowser.openFile(page);
    }

}
