package team6;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class PrimaryController {

    // The pause/play button
    @FXML
    private Button buttonPlay;

    // The media player
    @FXML
    MediaPlayer mediaplayer;

    @FXML
    private Slider slider;

    private Media currentMedia;
    private static String current_path = null;
    private boolean isPlayed = false;
    private boolean sliderPause = isPlayed;

    // The media viewer
    @FXML
    MediaView mediaview;

    // Total seconds of the video
    @FXML
    private Label videoTime;
    @FXML
    private Label videoTimeNeg;

    // Skipping buttons
    @FXML
    private Button forward10;

    @FXML
    private Button backward10;

    /**
     * Switches view to the select video file page
     * 
     * @throws IOException
     */
    @FXML
    private void switchToSecondary() throws IOException {
        // Moves root to the secondary view
        VideoPlayer.setRoot("secondary");

        // Stops video
        isPlayed = false;
        if (current_path != null) {
            mediaplayer.stop();
        }

    }

    /**
     * Creates the video player page
     * 
     * @throws IOException
     */
    @FXML
    public void initialize() throws IOException {
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

        // Creates a listener to check if the slider has been moved and updates it
        mediaplayer.currentTimeProperty().addListener(((obvValue, oldVal, newVal) -> {
            if (!slider.isValueChanging()) {
                slider.setValue(newVal.toSeconds());
                vidTime(slider.getValue(), videoTimeNeg);
            }
        }));

        // When the media player is started run this function
        mediaplayer.setOnReady(() -> {
            Duration totalDuration = currentMedia.getDuration();
            slider.setMax(totalDuration.toSeconds());
            vidTime(currentMedia.getDuration().toSeconds(), videoTime);
        });

        // Handle slider dragging (update video time)
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            vidTime(newVal.doubleValue(), videoTimeNeg);
        });

        // Seek when user releases the slider
        slider.setOnMouseReleased(event -> {

            // Makes video go to where the slider has ended from a drag
            mediaplayer.seek(Duration.seconds(slider.getValue()));

            // The sliderPause boolean remembers video state before the silder moved
            isPlayed = !sliderPause;

            // Resumes the original state of the video at a new time
            buttonPlay();
        });

        // Tries to reload the video if it failed to
        if (videoTimeNeg.getText() == "00:00:00") {
            VideoPlayer.setRoot("primary");
        }
    }

    /**
     * Formats and updates the time labels
     * 
     * @param value The time value in seconds
     * @param video the label to update
     */
    @FXML
    public void vidTime(double value, Label video) {
        int timeDouble = (int) value;
        int minutes = timeDouble / 60;
        int seconds = timeDouble % 60;
        String formattedTime = minutes + ":" + String.format("%02d", seconds);
        video.setText(formattedTime);

    }

    /**
     * Toggles the video pause/play feature
     * 
     * @param isPlayed true = playing, false = paused
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
    }

    /**
     * If slider is clicked move to that point in the video
     */
    @FXML
    public void slidePress() {
        mediaplayer.seek(Duration.seconds(slider.getValue()));
        sliderPause = isPlayed;

        isPlayed = true;
        buttonPlay();
    }

    /**
     * Sets path of selected file to current path of the player
     * 
     * @param path Path that current_path will be set to
     */
    public static void setPath(String path) {
        current_path = path;
    }

    public static String getPath() {
        return current_path;
    }
}
