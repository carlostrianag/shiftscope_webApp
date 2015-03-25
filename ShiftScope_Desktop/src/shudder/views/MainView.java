/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shudder.views;

/**
 *
 * @author Carlos
 */
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.ImageIcon;

public class MainView extends Application {

    private Scene scene;
    public static Browser mainBrowser;

    @Override
    public void start(Stage stage) {
        System.setProperty("prism.lcdtext", "false");
        stage.setTitle("Shudder Beta v1.0");
        
        stage.getIcons().add(new Image("file:/D:/Repositories/ShiftScope_Repo/ShiftScope_Desktop/src/shudder/views/html/assets/images/icon_144.png"));
        mainBrowser = new Browser(stage);
        scene = new Scene(mainBrowser, 700, 450, Color.web("#666970"));
        stage.setScene(scene);
        stage.setMinWidth(700);
        stage.setMinHeight(450);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                try {
                    
                    System.exit(0);
                } catch (Exception ex) {
                    Logger.getLogger(MainView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    protected final ImageIcon createImageIcon(String path,
            String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
