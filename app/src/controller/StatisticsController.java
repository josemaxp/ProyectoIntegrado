package controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import util.ConnectionManager;
import view.WarnMaketApp;

/**
 * FXML Controller class
 *
 * @author josem
 */
public class StatisticsController implements Initializable {

    @FXML
    private FontAwesomeIconView iconExit;
    @FXML
    private Text textFieldError;
    @FXML
    private FontAwesomeIconView iconBack;
    @FXML
    private Label userLikes;
    @FXML
    private PieChart chartTotalLikes;
    @FXML
    private Label userRecipes;
    @FXML
    private Label userTags;

    private String username = "";
    @FXML
    private Label userTotalLikes;
    @FXML
    private PieChart chartLikesRecipe;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getUser();
        initDataMostLikedUsers();
        initDataUserLikes();
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
            Parent root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
            WarnMaketApp.changeScene(root, "WarnMarketEstadísiticas");
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initDataMostLikedUsers() {
        ConnectionManager.out.println("CL:getMostLikedRecipes");

        try {
            String[] fromServer = ConnectionManager.in.readLine().split(":");
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            for (int i = 2; i < fromServer.length; i++) {
                String[] userData = fromServer[i].split("_");
                pieChartData.add(new PieChart.Data(userData[0]+" ("+userData[2]+")", Integer.valueOf(userData[1])));

                if (username.equals(userData[2])) {
                    userLikes.setText("Tu receta con más likes es: " + userData[0] + " (" + Integer.valueOf(userData[1]) + " likes)");
                }

            }

            chartLikesRecipe.setData(pieChartData);
            chartLikesRecipe.setTitle("Recetas con más likes");
            chartLikesRecipe.setLegendSide(Side.BOTTOM);
            chartLikesRecipe.setTitleSide(Side.TOP);
            chartLikesRecipe.setLabelLineLength(60);
            chartLikesRecipe.setLabelsVisible(false);

            pieChartData.forEach(data
                    -> data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), ": ", (int) data.pieValueProperty().get(), " likes"
                            )
                    )
            );

        } catch (IOException ex) {
            Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void initDataUserLikes() {
        ConnectionManager.out.println("CL:getUserLikes");

        try {
            String[] fromServer = ConnectionManager.in.readLine().split(":");
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            for (int i = 2; i < fromServer.length; i++) {
                String[] userData = fromServer[i].split("_");
                pieChartData.add(new PieChart.Data(userData[0], Integer.valueOf(userData[1])));

                if (username.equals(userData[0])) {
                    userTotalLikes.setText("Tienes un total de " + Integer.valueOf(userData[1]) + " likes");
                }

            }

            chartTotalLikes.setData(pieChartData);
            chartTotalLikes.setTitle("Likes totales de cada usuario");
            chartTotalLikes.setLegendSide(Side.BOTTOM);
            chartTotalLikes.setTitleSide(Side.TOP);
            chartTotalLikes.setLabelLineLength(60);
            chartTotalLikes.setLabelsVisible(false);

            pieChartData.forEach(data
                    -> data.nameProperty().bind(
                            Bindings.concat(
                                    data.getName(), ": ", (int) data.pieValueProperty().get(), " likes"
                            )
                    )
            );

        } catch (IOException ex) {
            Logger.getLogger(StatisticsController.class.getName()).log(Level.SEVERE, null, ex);
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

}
