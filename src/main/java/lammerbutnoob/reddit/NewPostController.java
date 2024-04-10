package lammerbutnoob.reddit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.sql.*;

public class NewPostController {
    @FXML
    TextField titleNewPost;
    @FXML
    TextArea descriptionNewPost;
    @FXML
    Button newPostButton;
    @FXML
    ToggleButton nsfwToggle;
    @FXML
    ToggleButton voteToggle;
    @FXML
    Label inSubLabel;
    public void initialize() throws SQLException{
//        inSubLabel.setText("will be posted in r/"+SubView.getTitle());
    }
    public void textChange(KeyEvent keyEvent) {
        //Set listener for text fields
        descriptionNewPost.textProperty().addListener((observable, oldValue, newValue) -> {
            //Check if they ain't empty
            newPostButton.setDisable(newValue.trim().isEmpty() || titleNewPost.getText().trim().isEmpty());
        });
    }

    public void createPostButton(ActionEvent event){
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
            PreparedStatement preparedStatement = con.prepareStatement("INSERT into posts(title,description,author,isNSFW,canVote,sub) values (?,?,?,?,?,?)");
            preparedStatement.setString(1, titleNewPost.getText());
            preparedStatement.setString(2, descriptionNewPost.getText());
            preparedStatement.setInt(3, Account.currentUserID);
            preparedStatement.setInt(4, nsfwToggle.isSelected() ? 1 : 0);
            preparedStatement.setInt(5, voteToggle.isSelected() ? 1 : 0);
            preparedStatement.setInt(6, SubView.id);
            preparedStatement.executeUpdate();
            con.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Done");
            alert.setContentText("Post created!");
            alert.initOwner(Reddit.primaryStage);
            alert.show();
            SubView.show(SubView.id);
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("This post already exists");
                alert.setContentText("Try another name");
                alert.initOwner(Reddit.primaryStage);
                alert.show();
            } else throw new RuntimeException(e);
        }
    }

}
