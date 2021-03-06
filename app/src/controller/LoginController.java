package controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import util.ConnectionManager;
import view.WarnMaketApp;

/**
 * FXML Controller class
 *
 * @author josem
 */
public class LoginController implements Initializable {

    @FXML
    private FontAwesomeIconView iconLoginExit;
    @FXML
    private MFXTextField textFieldLoginUsername;
    @FXML
    private MFXPasswordField textFieldLoginPassword;
    @FXML
    private Text textFieldLoginError;
    @FXML
    private MFXButton buttonLogin;
    @FXML
    private Label textFieldLoginRegister;
    @FXML
    private Label textFieldGuest;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void onClickLoginExit(MouseEvent event) {
        if (event.getSource() == iconLoginExit) {
            System.exit(0);
        }
    }

    @FXML
    private void onActionLogin(ActionEvent event) throws SQLException {
        if (new ConnectionManager().getConnection()) {
            try {
                if (textFieldLoginUsername.getText().equals("") || textFieldLoginPassword.getText().equals("")) {
                    textFieldLoginError.setText("Los campos no pueden estar vacíos.");

                } else {
                    ConnectionManager.out.println("CL:" + "login:" + textFieldLoginUsername.getText() + ":" + textFieldLoginPassword.getText());

                    String fromServer = ConnectionManager.in.readLine();

                    if (fromServer.split(":")[2].equals("true")) {
                        textFieldLoginError.setText("Correct login.");

                        try {
                            Parent root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
                            WarnMaketApp.changeScene(root, "WarnMarket");
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        textFieldLoginError.setText("Error, comprueba tu usuario o contraseña.");
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            dialogIP();
        }
    }

    /**
     * Open register scene.
     */
    @FXML
    private void onClickLoginRegister(MouseEvent event) {
        if (new ConnectionManager().getConnection()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/Register.fxml"));
                WarnMaketApp.changeScene(root, "Register");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            dialogIP();
        }
    }

    @FXML
    private void onClickGuest(MouseEvent event) {
        if (new ConnectionManager().getConnection()) {
            try {
                ConnectionManager.out.println("CL:" + "guestUser");
                Parent root = FXMLLoader.load(getClass().getResource("/view/MainScreen.fxml"));
                WarnMaketApp.changeScene(root, "WarnMarket");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            dialogIP();
        }
    }

    @FXML
    private void onClickIP(MouseEvent event) {
        dialogIP();
    }

    private void dialogIP() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("IP");
        dialog.setHeaderText("Introduce la IP del servidor");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().equals("")) {
            ConnectionManager.setIP(result.get());
        }
    }
}
