package lammerbutnoob.reddit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpController {
    @FXML
    Button submmitSignUp;
    @FXML
    TextField emailField;
    @FXML
    PasswordField passwordField;
    @FXML
    ToggleGroup toggle;
    @FXML
    public void initialize() {
        submmitSignUp.prefWidthProperty().bind(emailField.widthProperty());
    }

    @FXML
    public void login(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) emailField.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Reddit.class.getResource("login.fxml"));
        Scene loginScene = new Scene(fxmlLoader.load(), 960, 540);
        stage.setScene(loginScene);
    }
    @FXML
    public void signup(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) emailField.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Reddit.class.getResource("signup.fxml"));
        Scene signUpScene = new Scene(fxmlLoader.load(), 960, 540);
        stage.setScene(signUpScene);
    }

    public void checkEmailValidation() {
        String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(emailField.getText());
        if (!matcher.matches()) {
            submmitSignUp.setDisable(true);
            emailField.setStyle("-fx-border-color: #fb133a");
        } else {
            if(passwordField.getText().length()>=8)
                submmitSignUp.setDisable(false);
            emailField.setStyle("-fx-border-width: 0");
        }
    }

    public void checkPassValidation() {
        if (passwordField.getText().length() < 8) {
            submmitSignUp.setDisable(true);
            passwordField.setStyle("-fx-border-color: #fb133a");
        } else {
            String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(emailField.getText());
            if(matcher.matches())
                submmitSignUp.setDisable(false);
            passwordField.setStyle("-fx-border-width: 0");
        }
    }

    public void submmitSignUp(ActionEvent event) {
        String str = ((RadioButton) toggle.getSelectedToggle()).getText();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(str);
        alert.show();
    }

    public void submmitLogin(ActionEvent event) {

    }
}