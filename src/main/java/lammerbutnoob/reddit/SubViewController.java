package lammerbutnoob.reddit;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import org.json.JSONArray;

import java.io.IOException;
import java.sql.*;

public class SubViewController {
    @FXML
    Label postLabel;
    @FXML
    Label postDes;
    @FXML
    VBox subVBox;
    @FXML
    FlowPane subFlowPane;
    @FXML
    ScrollPane subScrollPane;
    @FXML
    Label nothingFoundPost;

    public void initialize() throws SQLException, IOException {
        // Set header
        postLabel.setText("r/" + SubView.getTitle());
        postDes.setText(SubView.getDes());
        //Add the posts
        Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * from posts where sub =" + SubView.id + " order by id desc");
        int i = 0;
        while (rs.next()) {
            ++i;
            //Add post
            HBox headbox = new HBox();
            Label postTitle = new Label();
            postTitle.setText(rs.getString("title"));
            postTitle.getStyleClass().add("PostTitle");
            headbox.getChildren().add(postTitle);
            Button deletebtn = new Button();
            deletebtn.getStyleClass().add("DeleteButton");
            deletebtn.setText("Delete");
            if (rs.getInt("author") == Account.currentUserID)
                headbox.getChildren().add(deletebtn);
            Button nsfwBtn = new Button();
            nsfwBtn.setText("NSFW");
            nsfwBtn.setId("nsfwBtn");

            headbox.setSpacing(5);
            headbox.setAlignment(Pos.CENTER_LEFT);
            subVBox.getChildren().add(headbox);
            //createdBy label
            Label createdBy = new Label();
            createdBy.setText("u/" + Account.getName(rs.getInt("author")));
            createdBy.getStyleClass().add("PostBy");
            subVBox.getChildren().add(createdBy);
            Label postText = new Label();
            postText.setText(rs.getString("description"));
            postText.getStyleClass().add("PostText");
            subVBox.getChildren().add(postText);
            if (rs.getInt("isNSFW") == 1) {
                headbox.getChildren().add(nsfwBtn);
                GaussianBlur gaussianBlur = new GaussianBlur();
                postText.setEffect(gaussianBlur);
                postText.setOnMouseClicked(event -> {
                    postText.setEffect(null);
                });
            }
            //Add comment
            int id = rs.getInt("id");
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("commentpane.fxml"));
            Parent comment = fxmlLoader.load();
            Button commentBtn = ((Button) comment.lookup("#commentButton"));
            commentBtn.setOnAction(event -> {
                TextArea txtarea = (TextArea) commentBtn.getParent().lookup("#commentTextArea");
                System.out.println(txtarea.getText());
            });
            subVBox.getChildren().add(comment);
            //Add vote
            if (rs.getInt("canVote") == 1) {
                FXMLLoader fxmlLoader1 = new FXMLLoader(MainController.class.getResource("votepane.fxml"));
                Pane vote = fxmlLoader1.load();
                ToggleButton upVote = (ToggleButton) vote.lookup("#upVote");
                ToggleButton downVote = (ToggleButton) vote.lookup("#downVote");
                int voteStat = SubView.voteStatus(id);
                if (voteStat == 1)
                    upVote.setSelected(true);
                else if (voteStat == -1)
                    downVote.setSelected(true);
                Label voteLabel = ((Label) vote.lookup("#voteLabel"));
                voteLabel.setText(String.valueOf(rs.getInt("votes")));
                upVote.setOnAction(event -> {
                    if (upVote.isSelected() == true) {
                        try {
                            voteUpped(id);
                            voteLabel.setText(String.valueOf(Integer.parseInt(voteLabel.getText()) + 1));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            voteUnUpped(id);
                            voteLabel.setText(String.valueOf(Integer.parseInt(voteLabel.getText()) - 1));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                downVote.setOnAction(event -> {
                    if (downVote.isSelected() == true) {
                        try {
                            voteDowned(id);
                            voteLabel.setText(String.valueOf(Integer.parseInt(voteLabel.getText()) - 1));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            voteUnDowned(id);
                            voteLabel.setText(String.valueOf(Integer.parseInt(voteLabel.getText()) + 1));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                subVBox.getChildren().add(vote);
            }
            // Add line
            HBox hBox = new HBox();
            Line line = new Line();
            line.startXProperty().bind(subScrollPane.widthProperty().multiply(0.1));
            line.endXProperty().bind(subScrollPane.widthProperty().multiply(0.9));
            line.setStrokeWidth(0.5);
            line.translateXProperty().bind(subScrollPane.widthProperty().multiply(0.1));
            line.setStroke(Color.web("#f2f4f5"));
            hBox.getChildren().add(line);
            hBox.setAlignment(Pos.CENTER);
            hBox.setPadding(new Insets(20, 0, 0, 0));
            subVBox.getChildren().add(hBox);
        }
        //Delete the last line
        if (i != 0) {
            subVBox.getChildren().removeLast();
            nothingFoundPost.setVisible(false);
        }
        rs.close();
        stmt.close();
        con.close();
    }

    public void newPost(ActionEvent event) throws IOException {
        MainController mainController = new MainController();
        mainController.mainBorderBox = MainController.mainBorderBox_static;
        mainController.changeChildScene("newpost.fxml");
    }

    public void voteUpped(int postId) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * from posts where id = " + postId);
        String upVote = rs.getString("upVote");
        String downVote = rs.getString("downVote");
        if (upVote == null || !upVote.startsWith("["))
            upVote = "[]";
        JSONArray ja = new JSONArray(upVote);
        JSONArray ja2 = new JSONArray(downVote);
        ja.put(Account.currentUserID);
        ja2.remove(ja2.toList().indexOf(Account.currentUserID));
        System.out.println(ja.toString());
        stmt.close();
        Statement statement = con.createStatement();
        statement.executeUpdate("update posts set upVote = '" + ja.toString() + "'where id = " + postId);
        statement.executeUpdate("update posts set downVote = '" + ja2.toString() + "'where id = " + postId);
        statement.executeUpdate("update posts set votes = votes+1 where id = " + postId);
        statement.close();
        con.close();
    }

    public void voteUnUpped(int postId) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * from posts where id = " + postId);
        String upVote = rs.getString("upVote");
        if (upVote == null || !upVote.startsWith("["))
            upVote = "[]";
        JSONArray ja = new JSONArray(upVote);
        ja.remove(ja.toList().indexOf(Account.currentUserID));
        System.out.println(ja.toString());
        stmt.close();
        Statement statement = con.createStatement();
        statement.executeUpdate("update posts set upVote = '" + ja.toString() + "'where id = " + postId);
        statement.executeUpdate("update posts set votes = votes-1 where id = " + postId);
        statement.close();
        con.close();
    }

    public void voteDowned(int postId) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * from posts where id = " + postId);
        String downVote = rs.getString("downVote");
        String upVote = rs.getString("upVote");
        if (downVote == null || !downVote.startsWith("["))
            downVote = "[]";
        JSONArray ja = new JSONArray(downVote);
        JSONArray ja2 = new JSONArray(upVote);
        ja.put(Account.currentUserID);
        ja2.remove(ja2.toList().indexOf(Account.currentUserID));
        System.out.println(ja.toString());
        stmt.close();
        Statement statement = con.createStatement();
        statement.executeUpdate("update posts set downVote = '" + ja.toString() + "'where id = " + postId);
        statement.executeUpdate("update posts set upVote = '" + ja2.toString() + "'where id = " + postId);
        statement.executeUpdate("update posts set votes = votes-1 where id = " + postId);
        statement.close();
        con.close();
    }

    public void voteUnDowned(int postId) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * from posts where id = " + postId);
        String downVote = rs.getString("downVote");
        if (downVote == null || !downVote.startsWith("["))
            downVote = "[]";
        JSONArray ja = new JSONArray(downVote);
        ja.remove(ja.toList().indexOf(Account.currentUserID));
        System.out.println(ja.toString());
        stmt.close();
        Statement statement = con.createStatement();
        statement.executeUpdate("update posts set downVote = '" + ja.toString() + "'where id = " + postId);
        statement.executeUpdate("update posts set votes = votes+1 where id = " + postId);
        statement.close();
        con.close();
    }
}
