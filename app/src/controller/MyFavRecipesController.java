package controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.text.Text;
import model.OfferItem;
import model.RecipeItem;
import util.ConnectionManager;
import view.WarnMaketApp;

/**
 * FXML Controller class
 *
 * @author josem
 */
public class MyFavRecipesController implements Initializable {

    @FXML
    private Label titleText;
    @FXML
    private MFXTextField textFieldSearch;
    @FXML
    private HBox hboxOffer;
    @FXML
    private MFXScrollPane scrollPane;
    @FXML
    private GridPane gridPane;
    @FXML
    private FontAwesomeIconView iconLoginExit;
    @FXML
    private FontAwesomeIconView returnIcon;

    private List<RecipeItem> totalRecetas;
    private String username = "";
    @FXML
    private Text noRecipeFav;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void search(KeyEvent event) {
        filterData();
    }

    @FXML
    private void onClickLoginExit(MouseEvent event) {
        if (event.getSource() == iconLoginExit) {
            System.exit(0);
        }
    }

    @FXML
    private void onClickReturn(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            WarnMaketApp.changeScene(root, "WarnMarket");
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void filterData() {
        int row = 1;
        int column = 0;
        gridPane.getChildren().clear();
        String[] quitarEspacio = textFieldSearch.getText().trim().replaceAll(" +", " ").split(" ");

        if (!textFieldSearch.getText().equals("")) {
            try {
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
            } catch (IOException ex) {
                Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            gridPane.getChildren().clear();
            showRecipe();

        }
    }

    public void showRecipe() {
        titleText.setText("Recetas guardadas");
        gridPane.getChildren().clear();
        totalRecetas = getRecipeData();

        if (!totalRecetas.isEmpty()) {
            int row = 1;
            int column = 0;
            noRecipeFav.setText("");

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
        } else {
            noRecipeFav.setText("No tienes ninguna receta guardada aún :(");
        }
    }

    private List<RecipeItem> getRecipeData() {
        getUser();
        totalRecetas = new ArrayList<>();
        ConnectionManager.out.println("CL:getAllLikeRecipe");

        String fromServer = "";
        try {
            fromServer = ConnectionManager.in.readLine();
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Separo la información usando ':'. Así se quedaría dividido en todas las ofertas existentes.
        String[] recipesSeparated = fromServer.split(":");

        //Divido cada una de las ofertas. Las ofertas empiezan en la posición 2.
        for (int i = 2; i < recipesSeparated.length; i++) {
            String[] recipeFromServer = recipesSeparated[i].split("_");
            List<String> products = new ArrayList<>();
            //Obtengo los productos, que están desde la posición 9 hasta el final
            for (int j = 9; j < recipeFromServer.length; j++) {
                products.add(recipeFromServer[j].toLowerCase());
            }

            RecipeItem recipe = new RecipeItem(Integer.parseInt(recipeFromServer[0]), recipeFromServer[1], recipeFromServer[2], products, Integer.parseInt(recipeFromServer[3]), recipeFromServer[4], Integer.parseInt(recipeFromServer[5]), recipeFromServer[6], "\\\\" + ConnectionManager.IP + "\\" + recipeFromServer[7], recipeFromServer[8]);
            totalRecetas.add(recipe);
        }

        return totalRecetas;
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

}
