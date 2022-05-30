/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import com.sun.imageio.plugins.common.ImageUtil;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import model.OfferItem;

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
    private Text Price;
    @FXML
    private Text PriceUnity;
    @FXML
    private Text user;
    @FXML
    private ImageView imageApproved;

    private OfferItem offerItem;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setData(OfferItem offerItem) {
        this.offerItem = offerItem;

        String tags = "";
        for (int i = 0; i < offerItem.getTags().size(); i++) {
            //Capitalizo las palabras para mostrarlas
            if (i < offerItem.getTags().size() - 1) {
                tags += offerItem.getTags().get(i).substring(0, 1).toUpperCase() + offerItem.getTags().get(i).substring(1) + " - ";
            } else {
                tags += offerItem.getTags().get(i).substring(0, 1).toUpperCase() + offerItem.getTags().get(i).substring(1);
            }
        }

        market.setText(offerItem.getMarket());
        Tags.setText(tags);
        Price.setText(offerItem.getPrice());
        PriceUnity.setText("(" + offerItem.getPriceUnity() + ")");
        user.setText(offerItem.getUsername());

        //Image offerImage = new Image(offerItem.getImage());
        //if (offerItem.getImage().equals("")) {
        Image offerImage = new Image("/images/noImageFound.jpg");
        //} 
        
        image.setImage(offerImage);
    }

}
