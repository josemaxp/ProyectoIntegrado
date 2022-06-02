package controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.RecipeItem;
import util.ConnectionManager;
import view.WarnMaketApp;

/**
 * FXML Controller class
 *
 * @author josem
 */
public class RecipeItemController implements Initializable {

    @FXML
    private ImageView image;
    @FXML
    private Text recipeName;
    @FXML
    private Text user;
    @FXML
    private Text likes;
    @FXML
    private FontAwesomeIconView people;
    @FXML
    private Text time;
    @FXML
    private Text peopleText;
    @FXML
    private FontAwesomeIconView report;
    @FXML
    private FontAwesomeIconView delete;

    private String username = "";
    private String usernmaeUpload = "";
    private int recipeID = -1;
    private boolean menuClicked = false;
    private RecipeItem RecipeItem;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setData(RecipeItem RecipeItem) {
        String[] timeSeparated = RecipeItem.getTime().trim().split("\\.");
        String newTime = "";

        if (timeSeparated[0].equals("00")) {
            newTime = timeSeparated[1] + " minuto(s)";
        } else {
            newTime = timeSeparated[0] + " hora(s) y " + timeSeparated[1] + " minuto(s)";
        }

        recipeName.setText(RecipeItem.getName());
        user.setText(RecipeItem.getUsername());
        likes.setText(RecipeItem.getLikes() + "");
        peopleText.setText(RecipeItem.getPeople() + "");
        time.setText(newTime);

        //Image offerImage = new Image(offerItem.getImage());
        //if (offerItem.getImage().equals("")) {
        Image offerImage = new Image("/images/noImageFound.jpg");
        //} 

        image.setImage(offerImage);

        usernmaeUpload = RecipeItem.getUsername();
        recipeID = RecipeItem.getId();

        init();

        this.RecipeItem = RecipeItem;
    }

    @FXML
    private void onClickReport(MouseEvent event) {
        menuClicked = true;
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Reportar receta");
        alert.setContentText("¿Está seguro de que desea reportar esta receta?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            reportRecipe();
        }
    }

    @FXML
    private void onClickDelete(MouseEvent event) {
        menuClicked = true;
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Borrar receta");
        alert.setContentText("¿Está seguro de que desea borrar esta receta?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            deleteRecipe();
        }
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

    private void deleteRecipe() {
        ConnectionManager.out.println("CL:" + "deleteRecipe:" + recipeID);
        String fromServer = null;
        try {
            fromServer = ConnectionManager.in.readLine();
        } catch (IOException ex) {
            Logger.getLogger(RecipeItemController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void reportRecipe() {
        ConnectionManager.out.println("CL:" + "reportRecipe:" + recipeID);
        String fromServer = null;
        try {
            fromServer = ConnectionManager.in.readLine();
        } catch (IOException ex) {
            Logger.getLogger(RecipeItemController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void init() {
        getUser();
        if (!username.equals("")) {
            if (!usernmaeUpload.equals(username)) {
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

    @FXML
    private void onClickRecipe(MouseEvent event) {
        if (menuClicked) {
            menuClicked = false;
        } else {
            try {

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/RecipeView.fxml"));
                fxmlLoader.load();

                Parent root = fxmlLoader.getRoot();
                WarnMaketApp.changeScene(root, "Recipe");

                RecipeViewController controller = fxmlLoader.getController();
                controller.setRecipeItem(RecipeItem);

            } catch (IOException ex) {
                Logger.getLogger(RecipeItemController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public RecipeItem getRecipeItem() {
        return RecipeItem;
    }

}
