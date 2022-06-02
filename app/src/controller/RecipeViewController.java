package controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import model.RecipeItem;
import util.ConnectionManager;
import view.WarnMaketApp;

/**
 * FXML Controller class
 *
 * @author josem
 */
public class RecipeViewController implements Initializable {

    @FXML
    private Label titleText;
    @FXML
    private FontAwesomeIconView iconLoginExit;
    @FXML
    private FontAwesomeIconView returnIcon;
    @FXML
    private Text username;
    @FXML
    private Text likes;
    @FXML
    private Text people;
    @FXML
    private Text time;
    @FXML
    private Text steps;
    @FXML
    private Text products;
    @FXML
    private Text cookware;
    @FXML
    private ImageView image;
    @FXML
    private FontAwesomeIconView giveLike;
    @FXML
    private FontAwesomeIconView giveDislike;

    RecipeItem RecipeItem;
    private int recipeID = -1;
    private String currentUsername = "";
    private String uploadUsername = "";
    boolean recipeLiked = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    private void initData() {
        String[] timeSeparated = RecipeItem.getTime().trim().split("\\.");
        String newTime = "";

        if (timeSeparated[0].equals("00")) {
            newTime = timeSeparated[1] + " minuto(s)";
        } else {
            newTime = timeSeparated[0] + " hora(s) y " + timeSeparated[1] + " minuto(s)";
        }

        String products = "";
        for (int i = 0; i < RecipeItem.getProducts().size(); i++) {
            String[] product = RecipeItem.getProducts().get(i).split("\\|");
            products += "- " + product[0].substring(0, 1).toUpperCase() + product[0].substring(1) + ": " + product[1] + " " + product[2] + "\n";

        }

        titleText.setText(RecipeItem.getName());
        username.setText(RecipeItem.getUsername());
        likes.setText(RecipeItem.getLikes() + "");
        people.setText(RecipeItem.getPeople() + "");
        steps.setText(RecipeItem.getSteps().replace("|", "\n"));
        cookware.setText(RecipeItem.getCookware().replace("|", "\n"));
        time.setText(newTime);
        this.products.setText(products);

        //Image offerImage = new Image(offerItem.getImage());
        //if (offerItem.getImage().equals("")) {
        Image offerImage = new Image("/images/noImageFound.jpg");
        //} 

        image.setImage(offerImage);

        recipeID = RecipeItem.getId();
        uploadUsername = RecipeItem.getUsername();

        getUser();
        getLikeRecipe();
        if (!currentUsername.equals("")) {
            if (recipeLiked) {
                giveLike.setDisable(true);
                giveLike.setOpacity(0);
                giveDislike.setDisable(false);
                giveDislike.setOpacity(1);
            } else {
                giveLike.setDisable(false);
                giveLike.setOpacity(1);
                giveDislike.setDisable(true);
                giveDislike.setOpacity(0);
            }

        } else {
            giveLike.setDisable(true);
            giveLike.setOpacity(0);
            giveDislike.setDisable(true);
            giveDislike.setOpacity(0);
        }

    }

    public void setRecipeItem(RecipeItem RecipeItem) {
        this.RecipeItem = RecipeItem;

        initData();
    }

    @FXML
    private void onClickLoginExit(MouseEvent event) {
        if (event.getSource() == iconLoginExit) {
            System.exit(0);
        }
    }

    @FXML
    private void onClickReturn(MouseEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            WarnMaketApp.changeScene(root, "WarnMarket");
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onClickLike(MouseEvent event) {
        ConnectionManager.out.println("CL:likeRecipe:" + recipeID + ":" + uploadUsername);
        giveLike.setDisable(true);
        giveLike.setOpacity(0);
        giveDislike.setDisable(false);
        giveDislike.setOpacity(1);
    }

    @FXML
    private void onClickDislike(MouseEvent event) {
        ConnectionManager.out.println("CL:dislikeRecipe:" + recipeID + ":" + uploadUsername);
        giveLike.setDisable(false);
        giveLike.setOpacity(1);
        giveDislike.setDisable(true);
        giveDislike.setOpacity(0);
    }

    private void getUser() {
        ConnectionManager.out.println("CL:getUser");
        String[] fromServer = null;
        try {
            fromServer = ConnectionManager.in.readLine().split(":");
        } catch (IOException ex) {
            Logger.getLogger(RecipeItemController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (fromServer.length > 2) {
            currentUsername = fromServer[2];
        }
    }

    private void getLikeRecipe() {
        ConnectionManager.out.println("CL:getLikeRecipe:" + recipeID);
        String[] fromServer = null;
        try {
            fromServer = ConnectionManager.in.readLine().split(":");
        } catch (IOException ex) {
            Logger.getLogger(RecipeItemController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (fromServer.length > 2 && fromServer[2].equals("true")) {
            recipeLiked = true;
        }
    }

}
