/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.ConnectionManager;

/**
 *
 * @author josem
 */
public class WarnMaketApp extends Application {

    private static double xOffset = 0.0;
    private static double yOffset = 0.0;
    private static Scene scene;
    private static Stage stage;

    /**
     * Move the stage
     */
    public static void moveStage(Parent root, Stage stage) {
        root.setOnMousePressed((MouseEvent t) -> {
            xOffset = t.getSceneX();
            yOffset = t.getSceneY();
        });

        root.setOnMouseDragged((MouseEvent t) -> {
            stage.setX(t.getScreenX() - xOffset);
            stage.setY(t.getScreenY() - yOffset);
        });
    }

    @Override
    public void start(Stage stage) throws Exception {
        WarnMaketApp.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));

        moveStage(root, WarnMaketApp.stage);

        scene = new Scene(root);
        WarnMaketApp.stage.setTitle("Login");
        WarnMaketApp.stage.initStyle(StageStyle.UNDECORATED);
        WarnMaketApp.stage.setScene(scene);
        WarnMaketApp.stage.show();
    }

    /**
     * Method to change the scene
     */
    public static void changeScene(Parent root, String nTitle) {
        scene = new Scene(root);
        moveStage(root, stage);
        stage.setTitle(nTitle);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        new ConnectionManager().getConnection();
        
        launch(args);
    }
}
