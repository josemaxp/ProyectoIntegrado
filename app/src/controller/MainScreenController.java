package controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyComboBox;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.OfferItem;
import model.RecipeItem;
import util.ConnectionManager;
import view.Toast;
import view.WarnMaketApp;

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
    private MFXScrollPane scrollPane;
    @FXML
    private GridPane gridPane;
    @FXML
    private MFXLegacyComboBox<String> comboBoxPoblacion;
    @FXML
    private MFXLegacyComboBox<String> comboBoxProvincia;
    @FXML
    private MFXLegacyComboBox<String> comboBoxComunidadAutonoma;
    @FXML
    private FontAwesomeIconView iconLoginExit;
    @FXML
    private MFXTextField textFieldSearch;
    @FXML
    private FontAwesomeIconView iconMenu;
    @FXML
    private VBox vboxMenu;
    @FXML
    private FontAwesomeIconView returnIcon;
    @FXML
    private AnchorPane ap;
    @FXML
    private FontAwesomeIconView fav;
    @FXML
    private Text favRecipes;

    private List<OfferItem> totalOfertas;
    private List<RecipeItem> totalRecetas;
    private final ObservableList<String> comunidadesAutonomas = FXCollections.observableArrayList();
    private final ObservableList<String> provincias = FXCollections.observableArrayList();
    private final ObservableList<String> poblaciones = FXCollections.observableArrayList();
    private boolean activePane = false; //false = offers // true = recipes
    private String username = "";
    private boolean comprobarTexto = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showOffer();
        initComboBox();
        fav.setDisable(true);
        fav.setOpacity(0);
        favRecipes.setDisable(true);
        favRecipes.setOpacity(0);
    }

    private void initComboBox() {
        comunidadesAutonomas.clear();
        comboBoxProvincia.setDisable(true);
        comboBoxPoblacion.setDisable(true);
        ConnectionManager.out.println("CL:comunidadesAutonomas");

        String[] fromServer = null;
        try {
            fromServer = ConnectionManager.in.readLine().split(":");
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (fromServer.length > 2) {
            for (int i = 2; i < fromServer.length; i++) {
                comunidadesAutonomas.add(fromServer[i]);
            }

            comboBoxComunidadAutonoma.setItems(comunidadesAutonomas);
        }
    }

    private List<OfferItem> getOfferData() {
        totalOfertas = new ArrayList<>();
        ConnectionManager.out.println("CL:showOffer:" + 0 + ":" + 0);

        String fromServer = "";
        try {
            fromServer = ConnectionManager.in.readLine();
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Separo la informaci??n usando ':'. As?? se quedar??a dividido en todas las ofertas existentes.
        String[] allOffers = fromServer.split(":");

        //Divido cada una de las ofertas. Las ofertas empiezan en la posici??n 2.
        for (int i = 2; i < allOffers.length; i++) {
            String[] ofertaFromServer = allOffers[i].split("_");
            List<String> tags = new ArrayList<>();
            //Obtengo las etiquetas, que est??n desde la posici??n 13 hasta el final
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

    private void showOffer() {
        gridPane.getChildren().clear();
        totalOfertas = getOfferData();
        if (!totalOfertas.isEmpty()) {
            int row = 1;
            int column = 0;

            try {
                for (int i = 0; i < totalOfertas.size(); i++) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/OfferItem.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();

                    OfferItemController offerItemController = fxmlLoader.getController();
                    offerItemController.setData(totalOfertas.get(i));

                    if (column == 2) {
                        column = 0;
                        row++;
                    }

                    gridPane.add(anchorPane, column++, row);
                    gridPane.setMargin(anchorPane, new Insets(10));
                }
            } catch (IOException ex) {
                Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private List<RecipeItem> getRecipeData() {
        totalRecetas = new ArrayList<>();
        ConnectionManager.out.println("CL:getRecipes");

        String fromServer = "";
        try {
            fromServer = ConnectionManager.in.readLine();
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Separo la informaci??n usando ':'. As?? se quedar??a dividido en todas las ofertas existentes.
        String[] recipesSeparated = fromServer.split(":");

        //Divido cada una de las ofertas. Las ofertas empiezan en la posici??n 2.
        for (int i = 2; i < recipesSeparated.length; i++) {
            String[] recipeFromServer = recipesSeparated[i].split("_");
            List<String> products = new ArrayList<>();
            //Obtengo los productos, que est??n desde la posici??n 9 hasta el final
            for (int j = 9; j < recipeFromServer.length; j++) {
                products.add(recipeFromServer[j].toLowerCase());
            }

            RecipeItem recipe = new RecipeItem(Integer.parseInt(recipeFromServer[0]), recipeFromServer[1], recipeFromServer[2], products, Integer.parseInt(recipeFromServer[3]), recipeFromServer[4], Integer.parseInt(recipeFromServer[5]), recipeFromServer[6], "\\\\" + ConnectionManager.IP + "\\" + recipeFromServer[7], recipeFromServer[8]);
            totalRecetas.add(recipe);
        }

        return totalRecetas;
    }

    private void showRecipe() {
        gridPane.getChildren().clear();
        totalRecetas = getRecipeData();
        if (!totalRecetas.isEmpty()) {
            int row = 1;
            int column = 0;

            Collections.sort(totalRecetas);

            try {
                for (int i = 0; i < totalRecetas.size(); i++) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/RecipeItem.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();

                    RecipeItemController recipeItemController = fxmlLoader.getController();
                    recipeItemController.setData(totalRecetas.get(i));

                    if (column == 2) {
                        column = 0;
                        row++;
                    }

                    gridPane.add(anchorPane, column++, row);
                    gridPane.setMargin(anchorPane, new Insets(10));
                }
            } catch (IOException ex) {
                Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void onClickSelectCA(ActionEvent event) {

        if (comprobarTexto) {
            textFieldSearch.setText("");
            comprobarTexto = false;
        }

        provincias.clear();
        comboBoxProvincia.setDisable(false);

        ConnectionManager.out.println("CL:provincias:" + comboBoxComunidadAutonoma.getValue());

        String[] fromServer = null;
        try {
            fromServer = ConnectionManager.in.readLine().split(":");
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (fromServer.length > 2) {
            for (int i = 2; i < fromServer.length; i++) {
                provincias.add(fromServer[i]);
            }
            comboBoxProvincia.setItems(provincias);
        }

        int row = 1;
        int column = 0;
        try {
            for (int i = 0; i < totalOfertas.size(); i++) {
                if (totalOfertas.get(i).getComunidadAutonoma().equals(comboBoxComunidadAutonoma.getValue())) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/OfferItem.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();

                    OfferItemController offerItemController = fxmlLoader.getController();
                    offerItemController.setData(totalOfertas.get(i));

                    if (column == 2) {
                        column = 0;
                        row++;
                    }

                    gridPane.add(anchorPane, column++, row);
                    gridPane.setMargin(anchorPane, new Insets(10));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void onClickSelectProvincia(ActionEvent event
    ) {
        gridPane.getChildren().clear();
        poblaciones.clear();
        comboBoxPoblacion.setDisable(false);

        ConnectionManager.out.println("CL:poblaciones:" + comboBoxProvincia.getValue());

        String[] fromServer = null;
        try {
            fromServer = ConnectionManager.in.readLine().split(":");

        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        if (fromServer.length > 2) {
            for (int i = 2; i < fromServer.length; i++) {
                poblaciones.add(fromServer[i]);
            }

            comboBoxPoblacion.setItems(poblaciones);
        }

        int row = 1;
        int column = 0;

        try {
            for (int i = 0; i < totalOfertas.size(); i++) {
                if (totalOfertas.get(i).getProvincia().equals(comboBoxProvincia.getValue())) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/OfferItem.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();

                    OfferItemController offerItemController = fxmlLoader.getController();
                    offerItemController.setData(totalOfertas.get(i));

                    if (column == 2) {
                        column = 0;
                        row++;
                    }

                    gridPane.add(anchorPane, column++, row);
                    gridPane.setMargin(anchorPane, new Insets(10));

                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onClickSelectPoblacion(ActionEvent event
    ) {
        gridPane.getChildren().clear();
        int row = 1;
        int column = 0;

        try {
            for (int i = 0; i < totalOfertas.size(); i++) {
                if (totalOfertas.get(i).getPoblacion().equals(comboBoxPoblacion.getValue())) {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/OfferItem.fxml"));
                    AnchorPane anchorPane = fxmlLoader.load();

                    OfferItemController offerItemController = fxmlLoader.getController();
                    offerItemController.setData(totalOfertas.get(i));

                    if (column == 2) {
                        column = 0;
                        row++;
                    }

                    gridPane.add(anchorPane, column++, row);
                    gridPane.setMargin(anchorPane, new Insets(10));

                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void onClickLoginExit(MouseEvent event
    ) {
        if (event.getSource() == iconLoginExit) {
            System.exit(0);
        }
    }

    @FXML
    private void search(KeyEvent event
    ) {
        filterData();
    }

    private void filterData() {
        gridPane.getChildren().clear();
        String[] quitarEspacio = textFieldSearch.getText().trim().replaceAll(" +", " ").split(" ");
        if (!textFieldSearch.getText().equals("")) {

            int row = 1;
            int column = 0;

            try {
                if (!activePane) {

                    comboBoxComunidadAutonoma.getSelectionModel().clearSelection();
                    comboBoxPoblacion.getSelectionModel().clearSelection();
                    comboBoxProvincia.getSelectionModel().clearSelection();

                    comboBoxPoblacion.setDisable(true);
                    comboBoxProvincia.setDisable(true);

                    comprobarTexto = true;

                    List<OfferItem> currentItems = new ArrayList<>();
                    for (int i = 0; i < totalOfertas.size(); i++) {
                        for (String s : quitarEspacio) {
                            if (totalOfertas.get(i).getMarket().toLowerCase().contains(s.toLowerCase())) {
                                if (!currentItems.contains(totalOfertas.get(i))) {
                                    FXMLLoader fxmlLoader = new FXMLLoader();
                                    fxmlLoader.setLocation(getClass().getResource("/view/OfferItem.fxml"));
                                    AnchorPane anchorPane = fxmlLoader.load();
                                    OfferItemController offerItemController = fxmlLoader.getController();
                                    offerItemController.setData(totalOfertas.get(i));

                                    if (column == 2) {
                                        column = 0;
                                        row++;
                                    }

                                    gridPane.add(anchorPane, column++, row);
                                    gridPane.setMargin(anchorPane, new Insets(10));

                                    currentItems.add(totalOfertas.get(i));
                                }
                            }

                            for (int j = 0; j < totalOfertas.get(i).getTags().size(); j++) {
                                if (totalOfertas.get(i).getTags().get(j).contains(s.toLowerCase())) {

                                    if (!currentItems.contains(totalOfertas.get(i))) {
                                        FXMLLoader fxmlLoader = new FXMLLoader();
                                        fxmlLoader.setLocation(getClass().getResource("/view/OfferItem.fxml"));
                                        AnchorPane anchorPane = fxmlLoader.load();
                                        OfferItemController offerItemController = fxmlLoader.getController();
                                        offerItemController.setData(totalOfertas.get(i));

                                        if (column == 2) {
                                            column = 0;
                                            row++;
                                        }

                                        gridPane.add(anchorPane, column++, row);
                                        gridPane.setMargin(anchorPane, new Insets(10));

                                        currentItems.add(totalOfertas.get(i));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    List<RecipeItem> currentItems = new ArrayList<>();
                    for (int i = 0; i < totalRecetas.size(); i++) {
                        for (String s : quitarEspacio) {
                            if (totalRecetas.get(i).getName().toLowerCase().contains(s.toLowerCase())) {
                                if (!currentItems.contains(totalRecetas.get(i))) {
                                    FXMLLoader fxmlLoader = new FXMLLoader();
                                    fxmlLoader.setLocation(getClass().getResource("/view/RecipeItem.fxml"));
                                    AnchorPane anchorPane = fxmlLoader.load();
                                    RecipeItemController recipeItemController = fxmlLoader.getController();
                                    recipeItemController.setData(totalRecetas.get(i));

                                    if (column == 2) {
                                        column = 0;
                                        row++;
                                    }

                                    gridPane.add(anchorPane, column++, row);
                                    gridPane.setMargin(anchorPane, new Insets(10));

                                    currentItems.add(totalRecetas.get(i));
                                }
                            }

                            for (int j = 0; j < totalRecetas.get(i).getProducts().size(); j++) {
                                if (totalRecetas.get(i).getProducts().get(j).contains(s.toLowerCase())) {

                                    if (!currentItems.contains(totalRecetas.get(i))) {
                                        FXMLLoader fxmlLoader = new FXMLLoader();
                                        fxmlLoader.setLocation(getClass().getResource("/view/RecipeItem.fxml"));
                                        AnchorPane anchorPane = fxmlLoader.load();
                                        RecipeItemController recipeItemController = fxmlLoader.getController();
                                        recipeItemController.setData(totalRecetas.get(i));

                                        if (column == 2) {
                                            column = 0;
                                            row++;
                                        }

                                        gridPane.add(anchorPane, column++, row);
                                        gridPane.setMargin(anchorPane, new Insets(10));

                                        currentItems.add(totalRecetas.get(i));

                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(MainScreenController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            gridPane.getChildren().clear();
            textFieldSearch.setText("");
            if (!activePane) {
                showOffer();
            } else {
                showRecipe();
            }
        }
    }

    @FXML
    private void onClickMenu(MouseEvent event) {
        vboxMenu.setLayoutX(vboxMenu.getWidth() * -1);
        this.slideMenu(vboxMenu.getWidth());
        vboxMenu.setDisable(false);
        vboxMenu.setOpacity(1);
        iconMenu.setDisable(true);
    }

    private void slideMenu(Double width) {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.8));
        slide.setNode(vboxMenu);
        slide.setToX(width);
        slide.play();
    }

    private void resetMenuProperties() {
        vboxMenu.setOpacity(0);
        vboxMenu.setDisable(true);
        iconMenu.setDisable(false);
        this.slideMenu(vboxMenu.getWidth() * -1);
    }

    @FXML
    private void onClickOfferButton(ActionEvent event) {
        fav.setDisable(true);
        fav.setOpacity(0);
        favRecipes.setDisable(true);
        favRecipes.setOpacity(0);
        this.resetMenuProperties();

        titleText.setText("Ofertas");
        showOffer();
        activePane = false;

        comboBoxComunidadAutonoma.setDisable(false);
        if (!comboBoxPoblacion.getItems().isEmpty()) {
            comboBoxPoblacion.setDisable(false);
        }
        if (!comboBoxPoblacion.getItems().isEmpty()) {
            comboBoxProvincia.setDisable(false);
        }
    }

    @FXML
    private void onClickRecipeButton(ActionEvent event) {
        fav.setDisable(false);
        fav.setOpacity(1);
        favRecipes.setDisable(false);
        favRecipes.setOpacity(1);
        this.resetMenuProperties();

        titleText.setText("Recetas");
        showRecipe();
        activePane = true;
        comboBoxComunidadAutonoma.setDisable(true);
        comboBoxPoblacion.setDisable(true);
        comboBoxProvincia.setDisable(true);
    }

    @FXML
    private void onClickMyAccountButton(ActionEvent event) {
        getUser();

        this.resetMenuProperties();
        if (!username.equals("")) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/MyAccount.fxml"));
                WarnMaketApp.changeScene(root, "Mi cuenta");

            } catch (IOException ex) {
                Logger.getLogger(MainScreenController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Stage stage = (Stage) ap.getScene().getWindow();

            String toastMsg = "Inicia sesi??n para acceder a tu cuenta.";
            int toastMsgTime = 1500; //1.5 seconds
            int fadeInTime = 500; //0.5 seconds
            int fadeOutTime = 500; //0.5 seconds
            Toast.makeText(stage, toastMsg, toastMsgTime, fadeInTime, fadeOutTime);
        }
    }

    @FXML
    private void onClickReturn(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            WarnMaketApp.changeScene(root, "Login");

        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getUser() {
        ConnectionManager.out.println("CL:" + "getUser");
        String[] fromServer = null;
        try {
            fromServer = ConnectionManager.in.readLine().split(":");

        } catch (IOException ex) {
            Logger.getLogger(RecipeItemController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        if (fromServer.length > 2) {
            username = fromServer[2];
        }
    }

    @FXML
    private void onClickFav(MouseEvent event) {
        getUser();
        if (!username.equals("")) {
            try {

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/MyFavRecipes.fxml"));
                fxmlLoader.load();

                Parent root = fxmlLoader.getRoot();
                WarnMaketApp.changeScene(root, "Recetas guardadas");

                MyFavRecipesController controller = fxmlLoader.getController();
                controller.showRecipe();

            } catch (IOException ex) {
                Logger.getLogger(MainScreenController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Stage stage = (Stage) ap.getScene().getWindow();

            String toastMsg = "Inicia sesi??n para ver tus recetas guardadas.";
            int toastMsgTime = 1500; //1.5 seconds
            int fadeInTime = 500; //0.5 seconds
            int fadeOutTime = 500; //0.5 seconds
            Toast.makeText(stage, toastMsg, toastMsgTime, fadeInTime, fadeOutTime);
        }
    }

    @FXML
    private void onClickStatisticstButton(ActionEvent event) {
        getUser();
        if (!username.equals("")) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/Statistics.fxml"));
                WarnMaketApp.changeScene(root, "WarnMarket");
            } catch (IOException ex) {
                Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Stage stage = (Stage) ap.getScene().getWindow();

            String toastMsg = "Inicia sesi??n para ver las estad??sticas.";
            int toastMsgTime = 1500; //1.5 seconds
            int fadeInTime = 500; //0.5 seconds
            int fadeOutTime = 500; //0.5 seconds
            Toast.makeText(stage, toastMsg, toastMsgTime, fadeInTime, fadeOutTime);
        }
    }

    @FXML
    private void onClickRefresh(MouseEvent event) {
        if (!activePane) {
            comboBoxComunidadAutonoma.getSelectionModel().clearSelection();
            comboBoxPoblacion.getSelectionModel().clearSelection();
            comboBoxProvincia.getSelectionModel().clearSelection();

            comboBoxPoblacion.setDisable(true);
            comboBoxProvincia.setDisable(true);

            showOffer();
        }
    }
}
