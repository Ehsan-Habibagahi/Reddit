package lammerbutnoob.reddit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class MainController {

    @FXML
    public BorderPane mainBorderBox;
    public static BorderPane mainBorderBox_static;
    @FXML
    public void initialize(){
        Reddit.primaryStage.setTitle("Reddit - Dive into anything");
        mainBorderBox_static = this.mainBorderBox;
        changeChildScene("home.fxml");

    }

    public void toggleHome(ActionEvent event) throws IOException {
//        System.out.println("hi");
//        Reddit.setScene("main.fxml");
        changeChildScene("home.fxml");
    }

    public void toggleProf(ActionEvent event) {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("profile.fxml"));
//            mainBorderBox.getChildren().remove(mainBorderBox.getCenter());
//            mainBorderBox.setCenter(null);
//            mainBorderBox.setCenter(fxmlLoader.load());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        changeChildScene("profile.fxml");
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
