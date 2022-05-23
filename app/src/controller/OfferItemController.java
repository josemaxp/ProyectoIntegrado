/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author josem
 */
public class OfferItemController implements Initializable {

    @FXML
    private ImageView image;
    @FXML
    private Text market;
    @FXML
    private Text Tags;
    @FXML
    private Text distance;
    @FXML
    private Text Price;
    @FXML
    private Text PriceUnity;
    @FXML
    private Text user;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
