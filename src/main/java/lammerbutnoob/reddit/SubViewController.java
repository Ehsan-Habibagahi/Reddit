package lammerbutnoob.reddit;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.sqlite.jdbc4.JDBC4Connection;

import java.io.IOException;
import java.sql.*;

public class SubView {
    @FXML
    Label postLabel;
    private static int id;

    public void initialize() throws SQLException {
        System.out.println(id);
//        Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
//        Statement stmt = con.createStatement();
//        ResultSet rs = stmt.executeQuery("SELECT * from subreddits where id =" + id);
//        postLabel.setText("r/" + rs.getString("title"));
//        con.close();
    }

    public static void subView(int id) {
        System.out.println("hey");
        MainController mainController = new MainController();
        mainController.mainBorderBox = MainController.mainBorderBox_static;
        SubView.id = id;
        mainController.changeChildScene("subview.fxml");
    }
}
