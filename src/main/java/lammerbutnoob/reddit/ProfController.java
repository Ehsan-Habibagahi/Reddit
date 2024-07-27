package lammerbutnoob.reddit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.sql.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfController {
    @FXML
    TextField username;
    @FXML
    TextField email;
    @FXML
    PasswordField password;
    @FXML
    Button updateUsername;
    @FXML
    Button updateEmail;
    @FXML
    Button updatePassword;
    @FXML
    Button deleteAccountButton;
    @FXML
    TextField sureDelete;


    public void initialize() throws SQLException {
        username.setText(Account.getName());
        email.setText(Account.getEmail());
    }

    public void updateEmail(ActionEvent event) {
       Account.updateEmail(email.getText());
    }

    public void updateUsername(ActionEvent event) {
       Account.updateUsername(username.getText());
    }

    public void updatePassword(ActionEvent event) {
            Account.updatePassword(password.getText());
    }

    public void checkUsernameValidation(KeyEvent keyEvent) {
        if (username.getText().length() < 3) {
            updateUsername.setDisable(true);
            username.setStyle("-fx-border-color: #fb133a");
        } else {
            username.setStyle("-fx-border-width: 0");
            updateUsername.setDisable(false);
        }
    }

    public void checkEmailValidation(KeyEvent keyEvent) {
        String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email.getText());
        if (!matcher.matches()) {
            updateEmail.setDisable(true);
            email.setStyle("-fx-border-color: #fb133a");
        } else {
            updateEmail.setDisable(false);
            email.setStyle("-fx-border-width: 0");
        }
    }

    public void checkPassValidation(KeyEvent keyEvent) {
        if (password.getText().length() < 8) {
            updatePassword.setDisable(true);
            password.setStyle("-fx-border-color: #fb133a");
        } else {
            password.setStyle("-fx-border-width: 0");
            updatePassword.setDisable(false);
        }
    }

    public void checkDeleteAccount(KeyEvent keyEvent) {
        if(Objects.equals(sureDelete.getText(), "I'm sure to delete my account"))
            deleteAccountButton.setDisable(false);
        else
            deleteAccountButton.setDisable(true);
    }

    public void deleteAccount(ActionEvent event) {
        Account.deleteAccount();
    }
}