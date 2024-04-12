package lammerbutnoob.reddit;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.skin.LabeledSkinBase;
import javafx.scene.layout.Pane;
import org.json.JSONArray;

import java.io.IOException;
import java.sql.*;

public class SubView {
    public static int id;

    public static void show(int id) {
        System.out.println("hey");
        //Changing the scene
        MainController mainController = new MainController();
        mainController.mainBorderBox = MainController.mainBorderBox_static;
        SubView.id = id;
        mainController.changeChildScene("subview.fxml");
    }

    public static String getTitle() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * from subreddits where id =" + id);
        String result = rs.getString("title");
        con.close();
        return result;
    }

    public static String getDes() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * from subreddits where id =" + id);
        String result = rs.getString("description");
        con.close();
        return result;
    }

    public static void comment(String text, int postId) {
        if (!text.isEmpty()) {
            try {
                Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
                PreparedStatement preparedStatement = con.prepareStatement("INSERT into comments(text,author,post) values (?,?,?)");
                preparedStatement.setString(1, text);
                preparedStatement.setInt(2, Account.currentUserID);
                preparedStatement.setInt(3, postId);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                con.close();
                Alert commentAlert = new Alert(Alert.AlertType.INFORMATION);
                commentAlert.initOwner(Reddit.primaryStage);
                commentAlert.setHeaderText("Done!");
                commentAlert.setContentText("Commit Submitted");
                commentAlert.show();
                show(id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Return 1 for up voted -1, for down voted and 0 for none
    public static int voteStatus(int id) {
        try {
            Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from posts where id = " + id);
            String upVote = rs.getString("upVote");
            String downVote = rs.getString("downVote");
            JSONArray ja = new JSONArray(upVote);
            JSONArray ja2 = new JSONArray(downVote);
            rs.close();
            stmt.close();
            con.close();
            if (ja.toList().contains(Account.currentUserID))
                return 1;
            if (ja2.toList().contains(Account.currentUserID))
                return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public static void deletePost(int id) {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
            Statement stmt = con.createStatement();
            stmt.executeUpdate("delete from posts where id = "+ id);
            int count = stmt.executeUpdate("delete from comments where post = "+id);
            stmt.close();
            con.close();
            Alert deletePostAlert = new Alert(Alert.AlertType.INFORMATION);
            deletePostAlert.initOwner(Reddit.primaryStage);
            deletePostAlert.setHeaderText("Done!");
            deletePostAlert.setContentText("Post with "+count +" comments deleted!");
            deletePostAlert.show();
            show(SubView.id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
