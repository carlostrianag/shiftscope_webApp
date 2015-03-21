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
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainView extends Application {

    private Scene scene;
    public static Browser mainBrowser;

    @Override
    public void start(Stage stage) {
        System.setProperty("prism.lcdtext", "false");
        stage.setTitle("Web View");
        mainBrowser = new Browser(stage);
        scene = new Scene(mainBrowser, 700, 450, Color.web("#666970"));
        stage.setScene(scene);
        stage.setMinWidth(700);
        stage.setMinHeight(450);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
