package lammerbutnoob.reddit;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;

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
    TextField usernameField;
    @FXML
    VBox vBox;

    @FXML
    public void initialize() {
        submmitSignUp.prefWidthProperty().bind(emailField.widthProperty());
    }

    @FXML
    public void login(MouseEvent mouseEvent) throws IOException {
        Reddit.setScene("login.fxml");
    }

    @FXML
    public void signup(MouseEvent mouseEvent) throws IOException {
        Reddit.setScene("signup.fxml");
    }

    public void checkEmailValidation() {
        String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(emailField.getText());
        if (!matcher.matches()) {
            submmitSignUp.setDisable(true);
            emailField.setStyle("-fx-border-color: #fb133a");
        } else {
            if (passwordField.getText().length() >= 8 && (!vBox.getChildren().contains(usernameField) || usernameField.getText().length() >= 3))
                submmitSignUp.setDisable(false);
            emailField.setStyle("-fx-border-width: 0");
        }
    }

    public void checkPassValidation() {
        if (passwordField.getText().length() < 8) {
            submmitSignUp.setDisable(true);
            passwordField.setStyle("-fx-border-color: #fb133a");
        } else {
            passwordField.setStyle("-fx-border-width: 0");
            String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(emailField.getText());
            if (matcher.matches() && (!vBox.getChildren().contains(usernameField) || usernameField.getText().length() >= 3))
                submmitSignUp.setDisable(false);
        }
    }

    public void submmitSignUp(ActionEvent event) {
        String gender = ((RadioButton) toggle.getSelectedToggle()).getText();
        //Running the database actions on another thread
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                String gender_in_thread = gender;
                int result = Account.signUp(usernameField.getText(), emailField.getText(), passwordField.getText(), gender);
                System.out.println(result);
                if (result == 1) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert newAccountAlert = new Alert(Alert.AlertType.INFORMATION);
                            newAccountAlert.initOwner(submmitSignUp.getScene().getWindow());
                            newAccountAlert.setHeaderText("Signed Up!");
                            newAccountAlert.setContentText("Go back to login to continue");
                            newAccountAlert.show();
                        }
                    });
                } else if (result == -1) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setContentText("Enter the login");
                            alert.setHeaderText("You already have an account!");
                            alert.initOwner(submmitSignUp.getScene().getWindow());
                            alert.show();
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    public void submmitLogin(ActionEvent event) {
        //Running the database actions on another thread
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int result;
                result = Account.login(emailField.getText(), passwordField.getText());
                System.out.println(result);
                if (result == 1) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //Change to main scene
                                Reddit.setScene("main.fxml");
                            }//If changing stage throws an exception
                            catch (IOException e) {
                                System.out.println("Error in scene change");
                                throw new RuntimeException(e);
                            }
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Welcome");
                            alert.setHeaderText("Valid!");
                            alert.initOwner(Reddit.primaryStage);
                            alert.show();
                        }
                    });
                } else if (result == -1) { //Invalid password
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Invalid password");
                            alert.setContentText("Try again...");
                            alert.initOwner(submmitSignUp.getScene().getWindow());
                            alert.show();
                        }
                    });
                } else if (result == -2) {//Invalid credentials
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Invalid credentials");
                            alert.setContentText("Try again...");
                            alert.initOwner(submmitSignUp.getScene().getWindow());
                            alert.show();
                        }
                    });
                } else if (result == -3) {//SQL exception(Invalid Email)
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Invalid Email");
                            alert.setContentText("Try again...");
                            alert.initOwner(submmitSignUp.getScene().getWindow());
                            alert.show();
                        }
                    });
                } else if (result == -4) {//authentication exception
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Authentication failed :-(");
                            alert.setContentText("Try again...");
                            alert.initOwner(submmitSignUp.getScene().getWindow());
                            alert.show();
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    public void checkUsernameValidation(KeyEvent keyEvent) {
        if (usernameField.getText().length() < 3) {
            submmitSignUp.setDisable(true);
            usernameField.setStyle("-fx-border-color: #fb133a");
        } else {
            String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(emailField.getText());
            if (matcher.matches() && passwordField.getText().length() >= 8) submmitSignUp.setDisable(false);
            usernameField.setStyle("-fx-border-width: 0");
        }
    }
}