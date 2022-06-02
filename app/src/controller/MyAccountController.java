package controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import util.ConnectionManager;
import view.WarnMaketApp;

/**
 * FXML Controller class
 *
 * @author josem
 */
public class MyAccountController implements Initializable {

    @FXML
    private MFXPasswordField textFieldPassword;
    @FXML
    private MFXButton buttonUpdate;
    @FXML
    private MFXTextField textFieldUsername;
    @FXML
    private FontAwesomeIconView iconExit;
    @FXML
    private Text textFieldError;
    @FXML
    private MFXPasswordField texxtFieldRepeatPassword;
    @FXML
    private FontAwesomeIconView iconBack;
    @FXML
    private MFXTextField textFieldemail;
    @FXML
    private Text textFieldPoints;
    @FXML
    private MFXButton buttonMyOffers;
    @FXML
    private MFXButton buttonMyRecipes;
    @FXML
    private MFXButton buttonRedeem;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getUserInfo();
    }

    @FXML
    private void onActionUpdate(ActionEvent event) {
        if (textFieldemail.getText().equals("") || textFieldUsername.getText().equals("")) {
            textFieldError.setText("Los campos de usuario o email no pueden estar vacíos.");
        } else {
            if (texxtFieldRepeatPassword.getText().equals(textFieldPassword.getText())) {
                boolean update = updateUser();

                if (update) {
                    textFieldError.setText("Usuario actualizado correctamete.");
                } else {
                    textFieldError.setText("Error al actualizar el usuario.");
                }

            } else {
                textFieldError.setText("Las contraseñas no coinciden.");
            }
        }
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
            WarnMaketApp.changeScene(root, "WarnMarket");
        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onActionMyOffers(ActionEvent event) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/MyUploads.fxml"));
            fxmlLoader.load();

            Parent root = fxmlLoader.getRoot();
            WarnMaketApp.changeScene(root, "Mis Ofertas");

            MyUploadsController controller = fxmlLoader.getController();
            controller.showOffer();

        } catch (IOException ex) {
            Logger.getLogger(RecipeItemController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onActionMyRecipes(ActionEvent event) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/MyUploads.fxml"));
            fxmlLoader.load();

            Parent root = fxmlLoader.getRoot();
            WarnMaketApp.changeScene(root, "Mis Recetas");

            MyUploadsController controller = fxmlLoader.getController();
            controller.showRecipe();

        } catch (IOException ex) {
            Logger.getLogger(RecipeItemController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onActionRedeem(ActionEvent event) {
    }

    private void getUserInfo() {
        ConnectionManager.out.println("CL:userInfo");

        try {
            String[] fromServer = ConnectionManager.in.readLine().split(":");
            if (fromServer.length > 2) {
                textFieldUsername.setText(fromServer[2]);
                textFieldemail.setText(fromServer[3]);
                textFieldPoints.setText("Puntos: " + fromServer[4]);
            }

        } catch (IOException ex) {
            Logger.getLogger(MyAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean updateUser() {
        ConnectionManager.out.println("CL:" + "updateUser:" + textFieldUsername.getText() + ":" + textFieldPassword.getText() + ":" + textFieldemail.getText());
        String[] fromServer = null;
        try {
            fromServer = ConnectionManager.in.readLine().split(":");
        } catch (IOException ex) {
            Logger.getLogger(MyAccountController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (fromServer[2].equals("true")) {
            getUserInfo();
            textFieldPassword.setText("");
            texxtFieldRepeatPassword.setText("");

            return true;
        } else {
            textFieldPassword.setText("");
            texxtFieldRepeatPassword.setText("");

            return false;
        }
    }

}
