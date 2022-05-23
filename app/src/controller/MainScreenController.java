/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.OfferItem;

/**
 * FXML Controller class
 *
 * @author josem
 */
public class MainScreenController implements Initializable {

    @FXML
    private Label titleText;
    @FXML
    private HBox hboxOffer;
    @FXML
    private MFXScrollPane scrollPaneOffer;
    @FXML
    private GridPane gridPaneOffer;
    
    private List<OfferItem> offers = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    /*private List<OfferItem> getData(){
        Main.out.println("CL:showOffer:" + Login.latitud + ":" + Login.longitud);      
    }*/
    
}
