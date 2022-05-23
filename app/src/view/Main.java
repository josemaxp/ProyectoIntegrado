/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author josem
 */
public class Main extends Application {

    private static double xOffset = 0.0;
    private static double yOffset = 0.0;
    private static Scene scene;
    private static Stage stage;
    private static String IP ="localhost";
    public static Socket kkSocket = null;
    public static PrintWriter out = null;
    public static BufferedReader in = null;

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
        Main.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));

        moveStage(root, Main.stage);

        scene = new Scene(root);
        Main.stage.setTitle("Login");
        Main.stage.initStyle(StageStyle.UNDECORATED);
        Main.stage.setScene(scene);
        Main.stage.show();
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
        try {
            kkSocket = new Socket(IP, 4444);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: .");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: .");
            System.exit(1);
        }

        launch(args);
    }
}
