package lammerbutnoob.reddit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewSubController {
    @FXML
    TextField titleNewSub;
    @FXML
    TextArea descriptionNewSub;
    @FXML
    Button newSubButton;

    public void textChange(KeyEvent keyEvent) {
        //Set listener for text fields
        descriptionNewSub.textProperty().addListener((observable, oldValue, newValue) -> {
            //Check if they ain't empty
            newSubButton.setDisable(newValue.trim().isEmpty() || titleNewSub.getText().trim().isEmpty());
        });
    }
    public void createSubButton(ActionEvent event) {
        //If dedicated JDBC
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("FUBAR!!!");
            alert.setContentText("Error in Driver");
            alert.initOwner(Reddit.primaryStage);
            alert.show();
        }
        //Connection
        try {
            Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
            //Prepared statement
            PreparedStatement preparedStatement = con.prepareStatement("INSERT into subreddits(title,description,admin) values (?,?,?)");
            preparedStatement.setString(1, titleNewSub.getText());
            preparedStatement.setString(2, descriptionNewSub.getText());
            preparedStatement.setInt(3, Account.currentUserID);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            con.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Done");
            alert.setContentText("Subreddit created!");
            alert.initOwner(Reddit.primaryStage);
            alert.show();
            MainController mainController = new MainController();
            mainController.mainBorderBox = MainController.mainBorderBox_static;
            mainController.changeChildScene("sub.fxml");
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("This subreddit already exists");
                alert.setContentText("Try another name");
                alert.initOwner(Reddit.primaryStage);
                alert.show();
            } else
                throw new RuntimeException(e);
        }
    }


}
