package lammerbutnoob.reddit;

import javafx.fxml.FXMLLoader;
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

    public static void comment(int id, String text) {
        System.out.println("id: " + id + " text: " + text);
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
}
