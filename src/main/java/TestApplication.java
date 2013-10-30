
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class TestApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        AnchorPane page = (AnchorPane) FXMLLoader.load(TestApplication.class.getResource("/video_fm.fxml"));
        Scene scene = new Scene(page);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Watermark");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}