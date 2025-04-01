package team6;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */

public class VideoPlayer extends Application {

    // ----------------------------------CONSTANTS----------------------------------
    // Initial aspect ratio of window
    private static final int INITWIDTH = 1000;
    private static final int INITHEIGHT = 800;

    // --------------------------------FXML ELEMENTS--------------------------------
    private static Scene scene;

    // ----------------------------------FUNCTIONS----------------------------------
    /**
     * Starts the javaFX window
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("secondary"), INITWIDTH, INITHEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Sets the root of the scene
     * 
     * @param fxml FXML file to be loaded
     * @throws IOException thrown if the FXML file is not found
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Loads the FXML file
     * 
     * @param fxml FXML file to be loaded
     * @return FXML parent
     * @throws IOException thrown if FXML file is not found
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(VideoPlayer.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * <p>
     * Gets scene and returns it
     * </p>
     * *For the file opener
     * 
     * @return scene
     */
    public static Scene getScene() {
        return scene;
    }

    /**
     * The program begins here
     */
    public static void main(String[] args) {
        launch();
    }

}
