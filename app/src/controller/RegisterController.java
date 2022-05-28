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
import view.Main;

/**
 * FXML Controller class
 *
 * @author josem
 */
public class RegisterController implements Initializable {

    @FXML
    private MFXPasswordField textFieldPassword;
    @FXML
    private MFXButton buttonRegister;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onActionRegister(ActionEvent event) {
        if (textFieldUsername.getText().equals("") || textFieldPassword.getText().equals("") || textFieldemail.getText().equals("")) {
            textFieldError.setText("Los campos no pueden estar vacíos.");
        } else {
            if (texxtFieldRepeatPassword.getText().equals(textFieldPassword.getText())) {
                ConnectionManager.out.println("CL:" + "register:" + textFieldUsername.getText() + ":" + textFieldPassword.getText() + ":" + textFieldemail.getText());
                String fromServer;
                try {
                    fromServer = ConnectionManager.in.readLine();
                    if (fromServer.split(":")[2].equals("true")) {
                        textFieldError.setText("Usuario creado.");
                    } else {
                        textFieldError.setText("Error al crear el ususario.");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(RegisterController.class.getName()).log(Level.SEVERE, null, ex);
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
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Main.changeScene(root, "Login");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
