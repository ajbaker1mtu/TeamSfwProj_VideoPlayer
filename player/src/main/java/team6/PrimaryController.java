package team6;

import java.io.IOException;
import javafx.fxml.FXML;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class PrimaryController {

    private Media currentMedia;
    private static String current_path;

    @FXML
    MediaView mediaview;

    @FXML
    private void switchToSecondary() throws IOException {
        VideoPlayer.setRoot("secondary");
    }

    @FXML
    public void initialize() {
        currentMedia = new Media(new File(current_path).toURI().toASCIIString());
        MediaPlayer mediaplayer = new MediaPlayer(currentMedia);
        mediaview.setMediaPlayer(mediaplayer);
    }

    public static void setPath(String path) {
        current_path = path;
    }
}
