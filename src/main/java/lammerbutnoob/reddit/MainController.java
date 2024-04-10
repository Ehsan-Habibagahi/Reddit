package lammerbutnoob.reddit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.SQLException;

public class MainController {
    @FXML
    Label welcomeLabel;
    @FXML
    FlowPane postFlowPane;
    @FXML
    public BorderPane mainBorderBox;
    public static BorderPane mainBorderBox_static;
    @FXML
    public void initialize() throws SQLException, IOException {
        Reddit.primaryStage.setTitle("Reddit - Dive into anything");
        welcomeLabel.setText("Welcome " + Account.getName() + "!");
        for (int i = 0; i < 3; i++) {
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("postpane.fxml"));
            Pane postPane = fxmlLoader.load();
            postPane.setId(String.valueOf(i));
            postFlowPane.getChildren().add(postPane);
        }
        mainBorderBox_static = this.mainBorderBox;
    }

    public void toggleHome(ActionEvent event) throws IOException {
        System.out.println("hi");
        Reddit.setScene("main.fxml");
    }

    public void togglePost(ActionEvent event) {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("post.fxml"));
//            mainBorderBox.getChildren().remove(mainBorderBox.getCenter());
//            mainBorderBox.setCenter(null);
//            mainBorderBox.setCenter(fxmlLoader.load());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        changeChildScene("post.fxml");
    }

    public void toggleSub(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sub.fxml"));
            mainBorderBox.getChildren().remove(mainBorderBox.getCenter());
            mainBorderBox.setCenter(null);
            mainBorderBox.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logOutButton(ActionEvent event) throws IOException {
        Account.currentUserID = 0;
        Reddit.setScene("login.fxml");
    }
    public void changeChildScene(String scene) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(scene));
            mainBorderBox.getChildren().remove(mainBorderBox.getCenter());
            mainBorderBox.setCenter(null);
            mainBorderBox.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
