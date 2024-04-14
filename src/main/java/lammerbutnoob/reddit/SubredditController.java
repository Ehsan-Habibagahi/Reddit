package lammerbutnoob.reddit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.sql.*;

public class SubredditController {
    @FXML
    TextField titleNewSub;
    @FXML
    TextArea descirptionNewSub;
    @FXML
    Button newSubButton;
    @FXML
    FlowPane postFlowPane;
    @FXML
    FlowPane otherPostFlowPane;
    @FXML
    Label nothingFoundMySub;
    @FXML
    Label nothingFoundOtherSub;

    @FXML
    public void initialize() throws SQLException, IOException {
        //My subs
        Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * from subreddits where admin =" + Account.currentUserID + " ORDER BY id DESC");
        int i = 0;
        while (rs.next()) {
            ++i;
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("postpane.fxml"));
            Pane postPane = fxmlLoader.load();
            Label panePostTitle = (Label) postPane.lookup("#panePostTitle");
            panePostTitle.setText("r/" + rs.getString("title"));
            Label panePostText = (Label) postPane.lookup("#panePostText");
            panePostText.setText(rs.getString("description"));
            postPane.lookup("#deleteButton").setVisible(true);
            int id = rs.getInt("id");
            postPane.setId(String.valueOf(id));
            postPane.setOnMouseClicked((event -> {
                subView(id);
            }));
            Button deleteButton = (Button) postPane.lookup("#deleteButton");
            deleteButton.setOnAction(event -> {
                SubredditController.deleteButton(id);
            });
            postFlowPane.getChildren().add(postPane);
        }
        if (i != 0)
            nothingFoundMySub.setVisible(false);
        System.out.println("My subs: " + String.valueOf(i));
        //Other's subs
        rs = stmt.executeQuery("SELECT * from subreddits where admin !=" + Account.currentUserID + " ORDER BY id DESC");
        i = 0;
        while (rs.next()) {
            ++i;
            System.out.println(rs.getString("title"));
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("postpane.fxml"));
            Pane postPane = fxmlLoader.load();
            Label panePostTitle = (Label) postPane.lookup("#panePostTitle");
            panePostTitle.setText("r/" + rs.getString("title"));
            Label panePostText = (Label) postPane.lookup("#panePostText");
            panePostText.setText(rs.getString("description"));
            int id = rs.getInt("id");
            postPane.setId(String.valueOf(id));
            postPane.setOnMouseClicked((event -> {
                subView(id);
            }));
            otherPostFlowPane.getChildren().add(postPane);
        }
        if (i != 0)
            nothingFoundOtherSub.setVisible(false);
        System.out.println("Other's subs: " + String.valueOf(i));
        //        String name = rs.getString("username");
//        con.close();
//        for (int i = 0; i < 3; i++) {
//            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("postpane.fxml"));
//            Pane postPane = fxmlLoader.load();
//            postPane.setId(String.valueOf(i));
//            postFlowPane.getChildren().add(postPane);
//        }
    }

    public void newSubButton(ActionEvent event) {
        MainController mainController = new MainController();
        mainController.mainBorderBox = MainController.mainBorderBox_static;
        mainController.changeChildScene("newsub.fxml");
    }


    public  static void deleteButton(int id) {
        try {
            Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
            //Prepared statement
            PreparedStatement preparedStatement = con.prepareStatement("delete from subreddits where id = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            PreparedStatement stmt = con.prepareStatement("delete from posts where sub = ?");
            stmt.setInt(1, id);
            int count = stmt.executeUpdate();
            preparedStatement.close();
            stmt.close();
            con.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Done");
            alert.setContentText("Subreddit with " + count + " posts deleted");
            alert.initOwner(Reddit.primaryStage);
            alert.show();
            MainController mainController = new MainController();
            mainController.mainBorderBox = MainController.mainBorderBox_static;
            mainController.changeChildScene("sub.fxml");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void subView(int id) {
        SubView.show(id);
    }

}
