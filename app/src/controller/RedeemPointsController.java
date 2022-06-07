/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import util.ConnectionManager;
import view.WarnMaketApp;

/**
 * FXML Controller class
 *
 * @author josem
 */
public class RedeemPointsController implements Initializable {

    @FXML
    private FontAwesomeIconView iconExit;
    @FXML
    private Text textFieldError;
    @FXML
    private FontAwesomeIconView iconBack;
    @FXML
    private Text textFieldPoints;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getUserInfo();
    }

    
    private void getUserInfo() {
        ConnectionManager.out.println("CL:userInfo");

        try {
            String[] fromServer = ConnectionManager.in.readLine().split(":");
            if (fromServer.length > 2) {
                textFieldPoints.setText("Puntos: " + fromServer[4]);
            }

        } catch (IOException ex) {
            Logger.getLogger(MyAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onClickExit(MouseEvent event) {
        if (event.getSource() == iconExit) {
            System.exit(0);
        }
    }

    @FXML
    private void onClickBack(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/MyAccount.fxml"));
            WarnMaketApp.changeScene(root, "Mi cuenta");
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
}
