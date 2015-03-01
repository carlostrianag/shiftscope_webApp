/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.views;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import shudder.controllers.UserController;
import shudder.listeners.LoginListener;
import shudder.util.Debugger;

/**
 *
 * @author Carlos
 */
public class Browser extends Region {

    final Debugger mainDebugger;
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    final ChangeListener<? super Worker.State> changeListener = (ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) -> {
          
        if (newState == Worker.State.SUCCEEDED) {
            
        } else if(newState == Worker.State.RUNNING) {
                setControllers();
        }
    };

    public Browser() {       
        mainDebugger = new Debugger();
        getStyleClass().add("browser");
        webEngine.getLoadWorker().stateProperty().addListener(changeListener);
        getChildren().add(browser);
        openHTML("index.html");
        
    }

    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
    
    private void setControllers() {
        JSObject javaScriptObject = (JSObject) webEngine.executeScript("window");
        UserController userController = new UserController();
        LoginListener listener = new LoginListener() {};
        userController.addListener(listener);
        javaScriptObject.setMember("UserController", userController);
        javaScriptObject.setMember("Debugger", mainDebugger);
    }
    
    public void execute(String code) {
        webEngine.executeScript(code);
    }

    public void openHTML(String file) {
        String path = getClass().getResource("html/" + file).toExternalForm();
        path = path.substring(6);
        path = "file:///" + path;        
        webEngine.load(path);
    }
    
    public String openFile(String file) {
        String path = getClass().getResource("html/" + file).toExternalForm();
        path = path.substring(6);
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    protected double computePrefWidth(double height) {
        return 600;
    }

    @Override
    protected double computePrefHeight(double width) {
        return 400;
    }
    

}

