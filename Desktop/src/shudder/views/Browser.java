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
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import shudder.controllers.FolderController;
import shudder.controllers.PlayerController;
import shudder.controllers.TCPController;
import shudder.controllers.UserController;
import shudder.listeners.FolderListener;
import shudder.listeners.LoginListener;
import shudder.listeners.PlayerListener;
import shudder.listeners.WebSocketListener;
import shudder.util.Constants;
import shudder.util.Debugger;
import shudder.util.OSValidator;

/**
 *
 * @author Carlos
 */
public class Browser extends Region {

    final Debugger mainDebugger;
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    final Stage stage;
    final ChangeListener<? super Worker.State> changeListener = (ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) -> {
        
          
        if (newState == Worker.State.SUCCEEDED) {
            
        } else if(newState == Worker.State.RUNNING) {
                setControllers();
        }
    };

    public Browser(Stage stage) {
        this.stage = stage;
        mainDebugger = new Debugger();
        getStyleClass().add("browser");
        webEngine.getLoadWorker().stateProperty().addListener(changeListener);
        getChildren().add(browser);
        if (OSValidator.isWindows()) {
            openHTML("index.html");
        } else if (OSValidator.isMac() || OSValidator.isUnix()) {
            openHTMLOnMac("index.html");
        }
    }

    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
    
    private void setControllers() {
        JSObject javaScriptObject = (JSObject) webEngine.executeScript("window");
        UserController userController = new UserController();
        FolderController folderController = new FolderController(stage);
        LoginListener loginListener = new LoginListener() {};
        FolderListener folderListener = new FolderListener() {};
        folderController.addListener(folderListener);
        PlayerController playerController = new PlayerController();
        PlayerListener playerListener = new PlayerListener() {};
        playerController.addListener(playerListener);
        userController.addListener(loginListener);
        TCPController tcpController = new TCPController();
        tcpController.addListener(new WebSocketListener());
        javaScriptObject.setMember("UserController", userController);
        javaScriptObject.setMember("FolderController", folderController);
        javaScriptObject.setMember("PlayerController", playerController);
        javaScriptObject.setMember("TCPController", tcpController);
        javaScriptObject.setMember("Debugger", mainDebugger);
    }
    

    
    public void execute(String code) {
        webEngine.executeScript(code);
    }

    public void openHTML(String file) {
        String path = "file:/" + Constants.USER_DIR + "/html/" + file;
        System.out.println(path);
        webEngine.load(path);
    }
    
    public void openHTMLOnMac(String file) {
        String path = "file://" + Constants.USER_DIR + "/html/" + file;
        System.out.println(path);
        webEngine.load(path);
    }
    
    public String openFile(String file) {
        String path = Constants.USER_DIR + "/html/" + file;
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
        return 820;
    }

    @Override
    protected double computePrefHeight(double width) {
        return 450;
    }
}

