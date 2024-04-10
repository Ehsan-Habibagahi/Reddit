/*======================================================+
 |~-~-~-~-~-~-~-~-بسم الله الرحمن الرحیم-~-~-~-~-~-~-~-~|
 |و اگر دیدی، به ناگاه خون من بر سنگ فرش خیابان جاری ست؛|
 |~-~بخند؛زیرا خنده تو برای دستان من، شمشیری است آخته~-~|
 |~-~-~-~-~-~-~-~REDDIT WINDOWS PROJECT🤖~-~-~-~-~-~-~-~|
 |~-~-~-~-~-~-~-~-~AUTHOR: ⲶH54N(3|-|)-~-~-~-~-~-~-~-~-~|
 |~-~-~-~-~-~-~-CREATED WITH LOTS OF LOVE!-~-~-~-~-~-~-~|
 +======================================================*/
package lammerbutnoob.reddit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

// <dependency>
//            <groupId>org.xerial</groupId>
//            <artifactId>sqlite-jdbc</artifactId>
//            <version>3.44.1.0</version>
//        </dependency>
public class Reddit extends Application {
    public static Stage primaryStage;
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Reddit.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 540);
        stage.setTitle("Reddit");
        stage.getIcons().add(new Image("reddit-logo-round.png"));
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
    public static void setScene(String url) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Reddit.class.getResource(url));
        Scene newScene = new Scene(fxmlLoader.load(), 960, 540);
        primaryStage.setScene(newScene);
    }
}