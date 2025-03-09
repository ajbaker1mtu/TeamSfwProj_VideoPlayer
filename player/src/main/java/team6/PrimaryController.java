package team6;

import java.io.IOException;
import javafx.fxml.FXML;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class PrimaryController {

    private static Media currentMedia;

    MediaView mediaview = (MediaView) VideoPlayer.getScene().lookup("#mediaview");

    @FXML
    private void switchToSecondary() throws IOException {
        VideoPlayer.setRoot("secondary");
    }

    public static void setCurrentVideo(String path) {
        currentMedia = new Media(new File(path).toURI().toASCIIString());
        MediaPlayer mediaplayer = new MediaPlayer(currentMedia);
    }
}
