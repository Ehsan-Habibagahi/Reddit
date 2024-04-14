package lammerbutnoob.reddit;

import javafx.beans.NamedArg;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomeController {
    @FXML
    Label welcomeLabel;
    @FXML
    FlowPane postFlowPane;
    @FXML
    ChoiceBox<String> filterChoiceBox;
    @FXML
    VBox postVBox;
    @FXML
    ScrollPane homeScrollPane;
    @FXML
    HBox nothingFoundPost;
    @FXML
    TextField searchBar;
    String[] filters = {"Trending", "New", "My posts"};

    public void initialize() throws SQLException, IOException {
        try {
            welcomeLabel.setText("Welcome " + Account.getName() + "!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        filterChoiceBox.getItems().addAll(filters);
        filterChoiceBox.setOnAction(event -> {
            filterOnAction();
        });
        filterChoiceBox.setValue("Trending");
    }

    public void filterOnAction() {
        int filter = 0;
        if (Objects.equals(filterChoiceBox.getValue(), "Trending")) {
            filter = 1;
        } else if (Objects.equals(filterChoiceBox.getValue(), "New")) {
            filter = 2;
        } else {
            filter = 3;
        }
        try {
            showPost(filter);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showPost(int filter) throws SQLException, IOException {
        //Add posts
        Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
        Statement stmt = con.createStatement();
        ResultSet rs = null;
        if (filter == 1) {
            rs = stmt.executeQuery("SELECT * from posts ORDER by votes DESC, id DESC LIMIT 10");
        } else if (filter == 2) {
            rs = stmt.executeQuery("SELECT * from posts ORDER by id DESC LIMIT 10");
        } else if (filter == 3) {
            rs = stmt.executeQuery("SELECT * from posts where author = " + Account.currentUserID + " ORDER by id DESC");
        }
        postFlowPane.getChildren().clear();
        postFlowPane.getChildren().add(postVBox);
        postFlowPane.setAlignment(Pos.CENTER);
        postVBox.getChildren().clear();
        int i = 0;
        while (rs.next()) {
            ++i;
            int id = rs.getInt("id");
            //Add post
            HBox headbox = new HBox();
            Label postTitle = new Label();
            postTitle.setText(rs.getString("title"));
            postTitle.getStyleClass().add("PostTitleSubView");
            headbox.getChildren().add(postTitle);
            Button nsfwBtn = new Button();
            nsfwBtn.setText("NSFW");
            nsfwBtn.setId("nsfwBtn");
            headbox.setSpacing(5);
            headbox.setAlignment(Pos.CENTER_LEFT);
            postVBox.getChildren().add(headbox);
            //createdBy label
            Label createdBy = new Label();
            createdBy.setText("u/" + Account.getName(rs.getInt("author")));
            createdBy.getStyleClass().add("PostBy");
            postVBox.getChildren().add(createdBy);
            Label postText = new Label();
            postText.setText(rs.getString("description"));
            postText.getStyleClass().add("PostTextSubView");
            postVBox.getChildren().add(postText);
            if (rs.getInt("isNSFW") == 1) {
                headbox.getChildren().add(nsfwBtn);
                GaussianBlur gaussianBlur = new GaussianBlur();
                postText.setEffect(gaussianBlur);
                postText.setOnMouseClicked(event -> {
                    postText.setEffect(null);
                });
            }
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
                SubViewController sub = new SubViewController();
                upVote.setOnAction(event -> {
                    if (upVote.isSelected() == true) {
                        try {
                            sub.voteUpped(id);
                            voteLabel.setText(String.valueOf(Integer.parseInt(voteLabel.getText()) + 1));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            sub.voteUnUpped(id);
                            voteLabel.setText(String.valueOf(Integer.parseInt(voteLabel.getText()) - 1));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                downVote.setOnAction(event -> {
                    if (downVote.isSelected() == true) {
                        try {
                            sub.voteDowned(id);
                            voteLabel.setText(String.valueOf(Integer.parseInt(voteLabel.getText()) - 1));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            sub.voteUnDowned(id);
                            voteLabel.setText(String.valueOf(Integer.parseInt(voteLabel.getText()) + 1));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                postVBox.getChildren().add(vote);
            }
            // Add line
            HBox hBox = new HBox();
            Line line = new Line();
            line.startXProperty().bind(homeScrollPane.widthProperty().multiply(0.1));
            line.endXProperty().bind(homeScrollPane.widthProperty().multiply(0.9));
            line.setStrokeWidth(0.5);
            line.translateXProperty().bind(homeScrollPane.widthProperty().multiply(0.1));
            line.setStroke(Color.web("#f2f4f5"));
            hBox.getChildren().add(line);
            hBox.setAlignment(Pos.CENTER);
            hBox.setPadding(new Insets(20, 0, 0, 0));
            postVBox.getChildren().add(hBox);
        }
        //Delete the last line
        if (i != 0) {
            postVBox.getChildren().removeLast();
        } else {
            postFlowPane.setAlignment(Pos.CENTER);
            postFlowPane.getChildren().add(nothingFoundPost);
            nothingFoundPost.setVisible(true);
        }
        rs.close();
        stmt.close();
        con.close();
    }

    public void search() throws SQLException, IOException {
        String search = searchBar.getText();
        if (!search.isEmpty()) {
            Connection con = DriverManager.getConnection("jdbc:sqlite:src/main/resources/database.db");
            //Search subs
            if (search.matches("^r/.*")) {
                search = search.substring(2);
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM subreddits where title like ? or title like ? or description like ? or description like ?");
                //Avoiding injections
                search = search
                        .replace("!", "!!")
                        .replace("%", "!%")
                        .replace("_", "!_")
                        .replace("[", "![");
                stmt.setString(1,search + "%");
                stmt.setString(2," "+search + "%");
                stmt.setString(3,search + "%");
                stmt.setString(4, " " + search + "%");
                ResultSet rs = stmt.executeQuery();
                //Buffer results
                int i = 0;
                postVBox.getChildren().clear();
                postFlowPane.getChildren().clear();
                postFlowPane.setAlignment(Pos.TOP_LEFT);
                while (rs.next()) {
                    ++i;
                    FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("postpane.fxml"));
                    Pane postPane = fxmlLoader.load();
                    Label panePostTitle = (Label) postPane.lookup("#panePostTitle");
                    panePostTitle.setText("r/" + rs.getString("title"));
                    Label panePostText = (Label) postPane.lookup("#panePostText");
                    panePostText.setText(rs.getString("description"));
                    if (rs.getInt("admin") == Account.currentUserID)
                        postPane.lookup("#deleteButton").setVisible(true);
                    int id = rs.getInt("id");
                    postPane.setId(String.valueOf(id));
                    postPane.setOnMouseClicked((event -> {
                        SubView.show(id);
                    }));
                    Button deleteButton = (Button) postPane.lookup("#deleteButton");
                    deleteButton.setOnAction(event -> {
                        SubredditController.deleteButton(id);
                    });
                    postFlowPane.getChildren().add(postPane);
                }
                if (i == 0) {
                    postFlowPane.setAlignment(Pos.CENTER);
                    postFlowPane.getChildren().add(nothingFoundPost);
                    nothingFoundPost.setVisible(true);
                }
                rs.close();
                stmt.close();
            }
            //Search user posts
            else if (search.matches("^u/.*")) {
                search = search.substring(2);
                PreparedStatement stmt = con.prepareStatement("SELECT * from users where username like ? or username like ?");
                search = search
                        .replace("!", "!!")
                        .replace("%", "!%")
                        .replace("_", "!_")
                        .replace("[", "![");
                stmt.setString(1,search + "%");
                stmt.setString(2," "+ search + "%");
                ResultSet rs2 = stmt.executeQuery();
                List<String> searchUsers = new ArrayList<>();
                while (rs2.next()) {
                    searchUsers.add(rs2.getString("id"));
                }
                String qSearch = searchUsers.stream().collect(Collectors.joining(","));
                rs2.close();
                stmt.close();
                Statement statement = con.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * from posts where author in(" + qSearch + ")");
                postVBox.getChildren().clear();
                postFlowPane.getChildren().clear();
                postFlowPane.getChildren().add(postVBox);
                postFlowPane.setAlignment(Pos.CENTER_LEFT);
                int i = 0;
                while (rs.next()) {
                    ++i;
                    int id = rs.getInt("id");
                    //Add post
                    HBox headbox = new HBox();
                    Label postTitle = new Label();
                    postTitle.setText(rs.getString("title"));
                    postTitle.getStyleClass().add("PostTitleSubView");
                    headbox.getChildren().add(postTitle);
                    Button nsfwBtn = new Button();
                    nsfwBtn.setText("NSFW");
                    nsfwBtn.setId("nsfwBtn");
                    headbox.setSpacing(5);
                    headbox.setAlignment(Pos.CENTER_LEFT);
                    postVBox.getChildren().add(headbox);
                    //createdBy label
                    Label createdBy = new Label();
                    createdBy.setText("u/" + Account.getName(rs.getInt("author")));
                    createdBy.getStyleClass().add("PostBy");
                    postVBox.getChildren().add(createdBy);
                    Label postText = new Label();
                    postText.setText(rs.getString("description"));
                    postText.getStyleClass().add("PostTextSubView");
                    postVBox.getChildren().add(postText);
                    if (rs.getInt("isNSFW") == 1) {
                        headbox.getChildren().add(nsfwBtn);
                        GaussianBlur gaussianBlur = new GaussianBlur();
                        postText.setEffect(gaussianBlur);
                        postText.setOnMouseClicked(event -> {
                            postText.setEffect(null);
                        });
                    }
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
                        SubViewController sub = new SubViewController();
                        upVote.setOnAction(event -> {
                            if (upVote.isSelected() == true) {
                                try {
                                    sub.voteUpped(id);
                                    voteLabel.setText(String.valueOf(Integer.parseInt(voteLabel.getText()) + 1));
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                try {
                                    sub.voteUnUpped(id);
                                    voteLabel.setText(String.valueOf(Integer.parseInt(voteLabel.getText()) - 1));
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                        downVote.setOnAction(event -> {
                            if (downVote.isSelected() == true) {
                                try {
                                    sub.voteDowned(id);
                                    voteLabel.setText(String.valueOf(Integer.parseInt(voteLabel.getText()) - 1));
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                try {
                                    sub.voteUnDowned(id);
                                    voteLabel.setText(String.valueOf(Integer.parseInt(voteLabel.getText()) + 1));
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                        postVBox.getChildren().add(vote);
                    }
                    // Add line
                    HBox hBox = new HBox();
                    Line line = new Line();
                    line.startXProperty().bind(homeScrollPane.widthProperty().multiply(0.1));
                    line.endXProperty().bind(homeScrollPane.widthProperty().multiply(0.9));
                    line.setStrokeWidth(0.5);
                    line.translateXProperty().bind(homeScrollPane.widthProperty().multiply(0.1));
                    line.setStroke(Color.web("#f2f4f5"));
                    hBox.getChildren().add(line);
                    hBox.setAlignment(Pos.CENTER);
                    hBox.setPadding(new Insets(20, 0, 0, 0));
                    postVBox.getChildren().add(hBox);
                }
                //Delete the last line
                if (i != 0) {
                    postVBox.getChildren().removeLast();
                } else {
                    postFlowPane.setAlignment(Pos.CENTER);
                    postFlowPane.getChildren().add(nothingFoundPost);
                    nothingFoundPost.setVisible(true);
                }
            }
            con.close();
        } else // If Search empty
        {
            filterOnAction();
        }
    }
}
