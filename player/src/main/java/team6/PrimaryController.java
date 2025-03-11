package team6;

import java.io.IOException;
import javafx.fxml.FXML;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class PrimaryController {

    @FXML
    private Button buttonPlay;

    @FXML
    MediaPlayer mediaplayer;

    private Media currentMedia;
    private static String current_path;
    private boolean isPlayed = false;

    @FXML
    MediaView mediaview;

    @FXML
    private void switchToSecondary() throws IOException {
        VideoPlayer.setRoot("secondary");
        isPlayed = false;
        mediaplayer.stop();
    }

    @FXML
    public void initialize() {
        // This if statement lets us switch views without selecting a file.
        if (current_path == null || current_path.isEmpty()) {
            System.out.println("Error: No media file selected.");
            return;
        }
        currentMedia = new Media(new File(current_path).toURI().toASCIIString());
        mediaplayer = new MediaPlayer(currentMedia);
        mediaview.setMediaPlayer(mediaplayer);

        // Resizes the video. Still a bit bugged will fix later
        Scene size = VideoPlayer.getScene();
        mediaview.fitWidthProperty().bind(size.widthProperty());
        mediaview.fitHeightProperty().bind(size.heightProperty());

    }

    @FXML
    public void buttonPlay(MouseEvent event) {
        if (!isPlayed) {
            buttonPlay.setText("Pause");
            mediaplayer.play();
            isPlayed = true;
        } else {
            buttonPlay.setText("Play");
            mediaplayer.pause();
            isPlayed = false;
        }
    }

    public static void setPath(String path) {
        current_path = path;
    }
}
