package team6;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class VideoPlayer extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("secondary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(VideoPlayer.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @FXML
    private void switchToPrimary() throws IOException {
        VideoPlayer.setRoot("primary");
    }

    //used for file opener
    public static Scene getScene() {
        return scene;
    }

    public static void main(String[] args) {
        launch();
    }

}
