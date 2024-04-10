package lammerbutnoob.reddit;

import java.sql.*;

public class Post {
    private static int id;
    static Connection con;

    public static void show(int id) {
        System.out.println("hey");
        //Changing the scene
        MainController mainController = new MainController();
        mainController.mainBorderBox = MainController.mainBorderBox_static;
        Post.id = id;
        mainController.changeChildScene("subview.fxml");
    }

    public static String getTitle() throws SQLException {
        System.out.println("sadf " + id);
        con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * from subreddits where id =" +id);
        con.close();
        return rs.getString("title");
    }
}
