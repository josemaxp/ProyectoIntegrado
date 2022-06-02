package controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.OfferItem;
import util.ConnectionManager;

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
    @FXML
    private FontAwesomeIconView report;
    @FXML
    private FontAwesomeIconView delete;
    
    private String username = "";
    private String usernameUpload = "";
    private int offerID = -1;
       
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    public void setData(OfferItem offerItem) {
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
        
        offerID = offerItem.getId();
        usernameUpload = offerItem.getUsername();
        
        Image approvedOffer = new Image("/images/pulgar_arriba.png");
        if(offerItem.isApprovedOffer()){
            imageApproved.setImage(approvedOffer);
        }
        
        init();
    }
    
    private void getUser() {
        ConnectionManager.out.println("CL:" + "getUser");
        String[] fromServer = null;
        try {
            fromServer = ConnectionManager.in.readLine().split(":");
        } catch (IOException ex) {
            Logger.getLogger(RecipeItemController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (fromServer.length > 2) {
            username = fromServer[2];
        }
    }

    private void deleteOffer() {
        ConnectionManager.out.println("CL:" + "deleteOffer:" + offerID);
        String fromServer = null;
        try {
            fromServer = ConnectionManager.in.readLine();
        } catch (IOException ex) {
            Logger.getLogger(RecipeItemController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void reportOffer() {
        ConnectionManager.out.println("CL:" + "reportOffer:" + offerID);
        String fromServer = null;
        try {
            fromServer = ConnectionManager.in.readLine();
        } catch (IOException ex) {
            Logger.getLogger(RecipeItemController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onClickReport(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Reportar oferta");
        alert.setContentText("¿Está seguro de que desea reportar esta oferta?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            reportOffer();
        }
    }

    @FXML
    private void onClickDelete(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Borrar oferta");
        alert.setContentText("¿Está seguro de que desea borrar esta oferta?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            deleteOffer();
        }
    }
    
    private void init(){
    getUser();
        if (!username.equals("")) {
            if (!usernameUpload.equals(username)) {
                delete.setDisable(true);
                delete.setOpacity(0);
                report.setDisable(false);
                report.setOpacity(1);
            } else {
                delete.setDisable(false);
                delete.setOpacity(1);
                report.setDisable(true);
                report.setOpacity(0);
            }
        } else {
            delete.setDisable(true);
            delete.setOpacity(0);
            report.setDisable(true);
            report.setOpacity(0);
        }
    }

}
