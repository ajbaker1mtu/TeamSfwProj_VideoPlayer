package team6;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import javafx.scene.Cursor;

public class PrimaryController {

    // CONSTANTS
    public static final int START = 0;
    public final int SKIPSEC = 10;
    public static final int TIMECONVERTER = 60;

    // The media player
    @FXML
    MediaPlayer mediaplayer;

    // Time slider and others
    @FXML
    private Slider slider;
    private boolean isPlayed = false;
    private boolean sliderPause = false;

    // Media file
    private Media currentMedia;
    private static String current_path = null;

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

    // The pause/play button
    @FXML
    private Button buttonPlay;

    // Looping elements
    @FXML
    private CheckMenuItem loopCheckMenuItem;
    private boolean loop = false;

    // Volume-modifying elements
    @FXML
    private Slider volume;
    @FXML
    private CheckBox muteCheckBox;

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

        // All dynamic events
        mediaplayerEvents();
        sliderEvents();
        skipEvents();
        volumeEvents();
        loopEvents();

        // Tries to reload the video if it failed to
        if (videoTimeNeg.getText() == "00:00:00") {
            VideoPlayer.setRoot("primary");
        }
    }

    // -------------------------------EVENT FUNCTIONS-------------------------------

    /**
     * <p>
     * These are all the mediaplayer events
     * </p>
     * <o1>
     * <li>1. Listener</li>
     * <li>2. On Ready</li>
     * <li>3. On End</li>
     * </o1>
     */
    private void mediaplayerEvents() {
        // 1. Creates a listener to check if the slider has been moved and updates it
        mediaplayer.currentTimeProperty().addListener(((obvValue, oldVal, newVal) -> {
            if (!slider.isValueChanging()) {
                slider.setValue(newVal.toSeconds());
                vidTime(slider.getValue(), videoTimeNeg);
            }
        }));

        // 2. When the media player is started run this function
        mediaplayer.setOnReady(() -> {
            Duration totalDuration = currentMedia.getDuration();
            slider.setMax(totalDuration.toSeconds());
            vidTime(currentMedia.getDuration().toSeconds(), videoTime);
        });

        // 3. Looping functionality
        mediaplayer.setOnEndOfMedia(() -> {
            checkLoop();
        });
    }

    /**
     * <p>
     * These are all the slider events
     * </p>
     * <o1>
     * <li>1. Listener</li>
     * <li>2. On Mouse Pressed</li>
     * <li>3. On Mouse Dragged</li>
     * <li>4. On Mouse Released</li>
     * </o1>
     */
    private void sliderEvents() {
        // 1. Handle slider dragging (update video time)
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            vidTime(newVal.doubleValue(), videoTimeNeg);
        });

        // 2. When the slider has been pressed
        slider.setOnMousePressed(event -> {

            // Grips onto slider
            slider.setCursor(Cursor.CLOSED_HAND);

            // Finds time after slider has been moved
            setTime(slider.getValue());

            // sliderPause saves the original state
            sliderPause = isPlayed;

            // Pause video
            isPlayed = true;
            buttonPlay();
        });

        // 3. Changes the time to current slider position
        slider.setOnMouseDragged(event -> {
            setTime(slider.getValue());
        });

        // 4. Seek when user releases the slider
        slider.setOnMouseReleased(event -> {

            // Opens hand since slider has been released
            slider.setCursor(Cursor.OPEN_HAND);

            // Makes video go to new time
            setTime(slider.getValue());

            // State of the video is recovered
            isPlayed = !sliderPause;

            // Resumes the original state of the video at a new time
            buttonPlay();
        });
    }

    /**
     * <p>
     * These are all the skip events
     * </p>
     * <o1>
     * <li>1. On Mouse Pressed (backward 10)</li>
     * <li>2. On Mouse Pressed (forward 10)</li>
     * </o1>
     */
    private void skipEvents() {

        // 1.
        backward10.setOnMouseClicked(event -> {
            // Case 1: normal
            if (slider.getValue() >= SKIPSEC) {
                // Goes back ten seconds
                setTime(slider.getValue() - SKIPSEC);
            } else /* Case 2: not normal */ {
                // Goes back to start
                setTime(START);
            }
        });

        // 2.
        forward10.setOnMouseClicked(event -> {
            double endTime = getEndTime();

            // Case 1: normal
            if (slider.getValue() <= endTime - SKIPSEC) {
                // Goes forward ten seconds
                setTime(slider.getValue() + SKIPSEC);
            } else /* Case 2: not normal */ {
                // Goes to end
                setTime(endTime);
            }
        });
    }

    /**
     * <p>
     * These are all the volume-related events
     * </p>
     * <o1>
     * <li>1. On Mouse Pressed (volume)</li>
     * <li>2. On Mouse Dragged (volume)</li>
     * <li>3. On Mouse Released (volume)</li>
     * <li>4. On Mouse Pressed (muteCheckBox)</li>
     * </o1>
     */
    private void volumeEvents() {
        // 1-3 is all volume changes
        // 1.
        volume.setOnMousePressed(event -> {
            changeVolume();
        });

        // 2.
        volume.setOnMouseDragged(event -> {
            changeVolume();
        });

        // 3.
        volume.setOnMouseReleased(event -> {
            changeVolume();
        });

        // 4. Mute button
        muteCheckBox.setOnMousePressed(event -> {
            mute();
        });
    }

    /**
     * <p>
     * These are all the loop events
     * </p>
     * <o1>
     * <li>1. onSelected</li>
     * </o1>
     */
    private void loopEvents() {

        // 1.
        loopCheckMenuItem.setOnAction(event -> {
            loop = !loop;

            // Checks if looping can occur
            checkLoop();
        });

    }

    // ------------------------------HELPER FUNCTIONS------------------------------

    /**
     * Formats and updates the time labels
     * 
     * @param value The time value in seconds
     * @param video the label to update
     */
    @FXML
    public void vidTime(double value, Label video) {
        int timeDouble = (int) value;
        int minutes = timeDouble / TIMECONVERTER;
        int seconds = timeDouble % TIMECONVERTER;
        String formattedTime = minutes + ":" + String.format("%02d", seconds);
        video.setText(formattedTime);

    }

    /**
     * Changes the volume of the media player
     */
    private void changeVolume() {
        // The slider interacts good when squared
        mediaplayer.setVolume(volume.getValue());
    }

    /**
     * Mutes the player volume
     */
    private void mute() {
        mediaplayer.setMute(!mediaplayer.isMute());
    }

    /**
     * Gets end time of video
     * 
     * @return end time in seconds
     */
    private double getEndTime() {
        return currentMedia.getDuration().toSeconds();
    }

    /**
     * Checks if the video should loop or not
     */
    private void checkLoop() {
        // Is loop selected and is the video at the end?
        if (loop && mediaplayer.getCurrentTime().toSeconds() == getEndTime()) {
            setTime(START);
        }
    }

    private void setTime(double time) {
        slider.setValue(time);
        mediaplayer.seek(Duration.seconds(slider.getValue()));
    }

    // -------------------------------FXML FUNCTIONS-------------------------------

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
        checkLoop();
    }

    // -------------------------------PATH FUNCTIONS-------------------------------

    /**
     * Sets path of selected file to current path of the player
     * 
     * @param path Path that current_path will be set to
     */
    public static void setPath(String path) {
        current_path = path;
    }

    /**
     * Retrives file path
     * 
     * @return Current file path
     */
    public static String getPath() {
        return current_path;
    }
}