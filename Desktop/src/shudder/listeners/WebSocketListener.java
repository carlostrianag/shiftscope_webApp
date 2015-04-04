/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.listeners;

import javafx.application.Platform;
import shudder.views.MainView;

/**
 *
 * @author Carlos
 */
public class WebSocketListener {
    public void OnOpened() {};
    public void OnError(String error) {};
    public void loading() {};
    public void loaded() {};
    public void OnClose(String message){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnClose('"+message+"');");
            }
        });
    }
}
