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
public abstract class LoginListener {
    public void OnInit(){};
    public void OnSuccessfulLogin(){                
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnSuccessfulLogin();");
            }
        });
    };
    public void OnFailedLogin(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnFailedLogin();");
            }
        });
    };
    public void loading() {};
    public void laoded() {};
    public void OnError(String error) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MainView.mainBrowser.execute("OnError();");
            }
        });    
    };
}
