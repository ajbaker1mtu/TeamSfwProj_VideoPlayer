package team6;

import java.io.IOException;
import javafx.fxml.FXML;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PrimaryController {

    // The pause/play button
    @FXML
    private Button buttonPlay;

    // The media player
    @FXML
    MediaPlayer mediaplayer;

    private Media currentMedia;
    private static String current_path;
    private boolean isPlayed = false;

    // The media viewer
    @FXML
    MediaView mediaview;

    // Total seconds of the video
    @FXML
    Label videoTime;

    // Skipping buttons
    @FXML
    private Button forward10;

    @FXML
    private Button backward10;

    @FXML
    private void switchToSecondary() throws IOException {
        // Moves root to the secondary view
        VideoPlayer.setRoot("secondary");

        // Stops video
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

        // Gets video file
        currentMedia = new Media(new File(current_path).toURI().toASCIIString());
        mediaplayer = new MediaPlayer(currentMedia);

        mediaview.setMediaPlayer(mediaplayer);

        // TODO
        // Got it to always show the buttons but the media viewer is small. Even with
        // the window defaulting to a larger display, the video is rather small.
        Scene size = VideoPlayer.getScene();
        mediaview.fitWidthProperty().bind(size.widthProperty());
        // mediaview.fitHeightProperty().bind(size.heightProperty());

        // mediaview.setFitWidth(1920);
        mediaview.setFitHeight(VideoPlayer.getScene().getHeight() - 200);

        // Preserves the ratio of the video
        mediaview.setPreserveRatio(true);

        // TODO
        // Should get video time and put it in the label after the slider, but it
        // doesn't
        videoTime();
    }

    /**
     * This function sets the text of the total time label to the total time of the
     * video playing
     */
    @FXML
    public void videoTime() {
        // Total seconds of the video stored
        double totalSeconds = Math.floor(currentMedia.getDuration().toSeconds());
        int totalMinutes = 0, totalHours = 0;

        // Checks if seconds are 60 or above
        if (totalSeconds >= 60) {
            // Convert to minutes and seconds
            totalMinutes = (int) Math.floor(totalSeconds / 60);
            totalSeconds = totalSeconds % 60;
        }

        // Checks if minutes are 60 or above
        if (totalMinutes >= 60) {
            // Convert to hours and minutes
            totalHours = (int) Math.floor(totalMinutes / 60);
            totalMinutes = totalMinutes % 60;
        }

        // Time that will be formatted correctly
        String time = formatTime(totalSeconds, totalMinutes, totalHours);

        // Sets time in label
        videoTime.setText(time);

    }

    /**
     * This function takes in the seconds, minutes, and hours of a video and outputs
     * the correct formatting of it
     * 
     * @param tS Second(s) on the video
     * @param tM Minute(s) on the video
     * @param tH Hour(s) on the video
     * @return Time with correct formatting
     */
    private String formatTime(double tS, int tM, int tH) {
        String ret = "";
        if (tH < 10) {
            ret += "0";
        }
        ret += tH + ":";

        if (tM < 10) {
            ret += "0";
        }
        ret += tM + ":";

        if (tS < 10) {
            ret += "0";
        }
        ret += (int) tS;

        return ret;
    }

    /**
     * Toggles the video pause/play feature
     * 
     * @param event
     */
    @FXML
    public void buttonPlay() {
        // Plays video
        if (!isPlayed) {
            buttonPlay.setText("Pause");
            mediaplayer.play();
            isPlayed = true;

            // Pauses video
        } else {
            buttonPlay.setText("Play");
            mediaplayer.pause();
            isPlayed = false;
        }

        videoTime();
    }

    /**
     * Sets path of selected file to current path of the player
     * 
     * @param path Path that current_path will be set to
     */
    public static void setPath(String path) {
        current_path = path;
    }
}
