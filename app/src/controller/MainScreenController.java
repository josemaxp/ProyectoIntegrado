package controller;

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.OfferItem;
import util.ConnectionManager;

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
    @FXML
    private MFXLegacyComboBox<String> comboBoxPoblacion;
    @FXML
    private MFXLegacyComboBox<String> comboBoxProvincia;
    @FXML
    private MFXLegacyComboBox<String> comboBoxComunidadAutonoma;

    private List<OfferItem> totalOfertas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        totalOfertas.addAll(getData());
        int column = 0;
        int row = 0;

        try {
            for (int i = 0; i < totalOfertas.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/OfferItem.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                OfferItemController offerItemController = new OfferItemController();
                offerItemController.setData(totalOfertas.get(i));

                if (column == 1) {
                    column = 0;
                    row++;
                }

                gridPaneOffer.add(anchorPane, i, i);
                gridPaneOffer.setMargin(anchorPane, new Insets(10));

            }
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<OfferItem> getData() {
        ConnectionManager.out.println("CL:showOffer:" + 0 + ":" + 0);
        List<OfferItem> totalOfertas = new ArrayList<>();

        String fromServer = "";
        try {
            fromServer = fromServer = ConnectionManager.in.readLine();
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Separo la información usando ':'. Así se quedaría dividido en todas las ofertas existentes.
        String[] allOffers = fromServer.split(":");

        //Divido cada una de las ofertas. Las ofertas empiezan en la posición 2.
        for (int i = 2; i < allOffers.length; i++) {
            String[] ofertaFromServer = allOffers[i].split("_");
            List<String> tags = new ArrayList<>();
            //Obtengo las etiquetas, que están desde la posición 13 hasta el final
            for (int j = 13; j < ofertaFromServer.length; j++) {
                tags.add(ofertaFromServer[j].toLowerCase());
            }
            boolean approvedOffer = false;
            if (ofertaFromServer[6].equals("true")) {
                approvedOffer = true;
            }

            OfferItem oferta = new OfferItem(ofertaFromServer[3], tags, ofertaFromServer[1], ofertaFromServer[2], ofertaFromServer[0], "\\\\" + ConnectionManager.IP + "\\" + ofertaFromServer[5], approvedOffer, Integer.parseInt(ofertaFromServer[7]), ofertaFromServer[10], ofertaFromServer[11], ofertaFromServer[12]);
            totalOfertas.add(oferta);
        }

        return totalOfertas;
    }
}
