package team6;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PrimaryController {

    // ----------------------------------CONSTANTS----------------------------------
    // Start time
    private static final int START = 0;

    // Skip time
    private final int SKIPSEC = 10;

    // Converter for seconds -> minutes -> hours and back
    private static final int TIMECONVERTER = 60;

    // Space for the margins (top and bottom)
    private static final int MARGIN = 200;

    // --------------------------------FXML ELEMENTS--------------------------------
    // The media player
    @FXML
    private MediaPlayer mediaplayer;

    // Time slider and others
    @FXML
    private Slider slider;

    // Media file
    private Media currentMedia;

    // The media viewer
    @FXML
    private MediaView mediaview;

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

    // Fullscreen Button
    @FXML
    private Button fullButton;

    // Looping elements
    @FXML
    private CheckMenuItem loopCheckMenuItem;

    // Volume-modifying elements
    @FXML
    private Slider volume;
    @FXML
    private CheckBox muteCheckBox;

    // Speed-modifying group
    @FXML
    private ToggleGroup speed;

    // Color adjustment for hue
    @FXML
    private Slider hueSlider;
    @FXML
    private Button resetHue;

    // ------------------------------HELPER VARIABLES-------------------------------
    // Time slider booleans
    private boolean isPlayed = false;
    private boolean sliderPause = false;
    private boolean isFullscreen = false;

    // Current path
    private static String current_path = null;

    // Loop on/off boolean
    private boolean loop = false;

    // ColorAdjust effect for hue adjustment
    private ColorAdjust colorAdjust = new ColorAdjust();

    // ------------------------------INITIALIZE WINDOW------------------------------
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
        mediaview.setFitHeight(VideoPlayer.getScene().getHeight() - MARGIN);

        // Preserves the ratio of the video
        mediaview.setPreserveRatio(true);

        // Linking effect for hue adjustment to the slider
        mediaview.setEffect(colorAdjust);

        // All dynamic events
        mediaplayerEvents();
        timeEvents();
        skipEvents();
        volumeEvents();
        loopEvents();
        fullscreenEvents();
        hueEvents();

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
    private void timeEvents() {
        // 1. Handle slider dragging (update video time)
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            vidTime(newVal.doubleValue(), videoTimeNeg);
        });

        // 2. When the slider has been pressed pause video
        slider.setOnMousePressed(event -> {
            sliderStartMoving();
        });

        // 3. Changes the time to current slider position
        slider.setOnMouseDragged(event -> {
            setTime(slider.getValue());
        });

        // 4. Seek when user releases the slider
        slider.setOnMouseReleased(event -> {
            sliderEndMoving();
        });
    }

    /**
     * <p>
     * These are all the skip events
     * </p>
     * <o1>
     * <li>1. On Mouse Clicked (backward 10)</li>
     * <li>2. On Mouse Clicked (forward 10)</li>
     * </o1>
     */
    private void skipEvents() {

        // 1.
        backward10.setOnMouseClicked(event -> {
            rewind(SKIPSEC);
        });

        // 2.
        forward10.setOnMouseClicked(event -> {
            fastForward(SKIPSEC);
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
     * <li>4. On Action (muteCheckBox)</li>
     * </o1>
     */
    private void volumeEvents() {
        // 1-3 is all volume changes
        // 1.
        volume.setOnMousePressed(event -> {
            volume.setCursor(Cursor.CLOSED_HAND);
            changeVolume();
        });

        // 2.
        volume.setOnMouseDragged(event -> {
            changeVolume();
        });

        // 3.
        volume.setOnMouseReleased(event -> {
            volume.setCursor(Cursor.OPEN_HAND);
            changeVolume();
        });

        // 4. Mute button
        muteCheckBox.setOnAction(event -> {
            mute();
        });
    }

    /**
     * <p>
     * These are all the loop events
     * </p>
     * <o1>
     * <li>1. On Selected</li>
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

    /**
     * <p>
     * These are all the loop events
     * </p>
     * <o1>
     * <li>1. Listener</li>
     * <li>1. On Key Pressed</li>
     * </o1>
     */
    private void fullscreenEvents() {
        Platform.runLater(() -> {

            Stage stage = (Stage) fullButton.getScene().getWindow();

            // Add event listener to detect fullscreen change
            stage.fullScreenProperty().addListener((obs, wasFullScreen, isNowFullScreen) -> {
                if (!isNowFullScreen) {
                    toggleFullScreen();
                }
            });

            // Listen for ESC key press to exit fullscreen
            stage.getScene().setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    if (isFullscreen) {
                        toggleFullScreen();
                    }
                }
            });
        });
    }

    /**
     * <p>
     * These are all the hue events
     * </p>
     * <o1>
     * <li>1. On Mouse Pressed</li>
     * <li>1. On Mouse Dragged</li>
     * <li>1. On Mouse Released</li>
     * <li>1. On Action (button)</li>
     * </o1>
     */
    private void hueEvents() {

        // 1. hueSlider changes hue of media player
        hueSlider.setOnMousePressed(event -> {
            startChangeHue();
        });

        // 2. hueSlider changes the hue live
        hueSlider.setOnMouseDragged(event -> {
            colorAdjust.setHue(hueSlider.getValue());
        });

        // 3. hueSlider changes the hue once slider is released
        hueSlider.setOnMouseReleased(event -> {
            endChangeHue();
        });

        // 4. Resets the hue of the media player
        resetHue.setOnAction(event -> {
            resetHue();
        });
    }

    // ------------------------------HELPER FUNCTIONS------------------------------

    // Time event helper functions

    /**
     * Inits the time slider starting to move
     */
    private void sliderStartMoving() {
        // Grips onto slider
        slider.setCursor(Cursor.CLOSED_HAND);

        // Finds time after slider has been moved
        setTime(slider.getValue());

        // sliderPause saves the original state
        sliderPause = isPlayed;

        // Pause video
        isPlayed = true;
        buttonPlay();
    }

    /**
     * Preps the media player to continue in the state it was in before using the
     * slider
     */
    private void sliderEndMoving() {
        // Opens hand
        slider.setCursor(Cursor.OPEN_HAND);

        // Makes video go to new time
        setTime(slider.getValue());

        // State of the video is recovered
        isPlayed = !sliderPause;

        // Resumes the original state of the video at a new time
        buttonPlay();
    }

    /**
     * Sets the time for the slider and media player
     */
    private void setTime(double time) {
        slider.setValue(time);
        mediaplayer.seek(Duration.seconds(slider.getValue()));
    }

    /**
     * Gets end time of video
     *
     * @return end time in seconds
     */
    private double getEndTime() {
        return currentMedia.getDuration().toSeconds();
    }

    // Skip event helper functions

    /**
     * Helper function to assist in skipping backward a given amount of seconds
     *
     * @param sec
     */
    public void rewind(int sec) {
        // Case 1: Can rewind back 10 seconds
        if (slider.getValue() >= sec) {
            // Goes back ten seconds
            setTime(slider.getValue() - sec);
        } else /* Case 2: Can not rewind 10 seconds */ {
            // Goes back to start
            setTime(START);
        }
    }

    /**
     * Helper function to assist in skipping forward a given amount of seconds
     *
     * @param sec
     */
    public void fastForward(int sec) {
        // Gets end time
        double endTime = getEndTime();

        // Case 1: Can skip 10 seconds forward
        if (slider.getValue() <= endTime - sec) {
            // Goes forward ten seconds
            setTime(slider.getValue() + sec);

        } else /* Case 2: Can not skip 10 seconds forward */ {
            // Goes to end
            setTime(endTime);
        }
    }

    // Volume event helper functions

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

    // Loop event helper functions

    /**
     * Checks if the video should loop or not
     */
    private void checkLoop() {
        // Is loop selected and is the video at the end?
        if (loop && mediaplayer.getCurrentTime().toSeconds() == getEndTime()) {
            setTime(START);
        }
    }

    // Hue event helper functions

    /**
     * Starts changing the hue based of hueSlider
     */
    private void startChangeHue() {
        // Range of hue: -1 to 1
        hueSlider.setCursor(Cursor.CLOSED_HAND);
        colorAdjust.setHue(hueSlider.getValue());
    }

    /**
     * Change the hue of the video after slider is relseased
     */
    private void endChangeHue() {
        // Range of hue: -1 to 1
        hueSlider.setCursor(Cursor.OPEN_HAND);
        colorAdjust.setHue(hueSlider.getValue());
    }

    /**
     * Resets the hue of the media player
     */
    private void resetHue() {
        hueSlider.setValue(0.0);
        colorAdjust.setHue(0.0);
    }

    // -------------------------------FXML FUNCTIONS-------------------------------
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
     * Sets the window to fullscreen
     */
    @FXML
    private void toggleFullScreen() {
        try {
            Stage stage = (Stage) fullButton.getScene().getWindow();
            isFullscreen = !isFullscreen;
            stage.setFullScreen(isFullscreen);

            if (isFullscreen) {
                mediaview.fitWidthProperty().bind(stage.widthProperty());
                mediaview.fitHeightProperty().bind(stage.heightProperty());

            } else {
                mediaview.fitWidthProperty().unbind();
                mediaview.fitHeightProperty().unbind();
                mediaview.setFitWidth(VideoPlayer.getScene().getWidth());
                mediaview.setFitHeight(VideoPlayer.getScene().getHeight() - MARGIN);
            }

        } catch (NullPointerException e) {
            System.out.println("Fix later");
        }

    }

    /**
     * Detects a key pressed and then skips ahead based on the key
     */
    @FXML
    private void keyPresses() {
        Scene scene = slider.getScene();

        scene.setOnKeyPressed(event -> {
            // If A is pressed rewind 10 secs
            if (event.getCode() == KeyCode.J) {
                rewind(SKIPSEC);
            }
            // If D is pressed fast forward 10 secs
            if (event.getCode() == KeyCode.L) {
                fastForward(SKIPSEC);
            }
            // If K is pressed play video
            if (event.getCode() == KeyCode.K) {
                buttonPlay();
            }
            // If F is pressed toggle fullscreen
            if (event.getCode() == KeyCode.F) {
                toggleFullScreen();
            }

            // Mute toggle
            if (event.getCode() == KeyCode.M) {
                mute();
                muteCheckBox.setSelected(!muteCheckBox.isSelected());
            }
        });

    }

    /**
     * Changes the video playback speed
     */
    @FXML
    private void changeSpeed() {
        // Takes the rate displayed by the selected menu item as the new speed
        RadioMenuItem rate = (RadioMenuItem) speed.getSelectedToggle();
        mediaplayer.setRate(Double.valueOf(rate.getText()));
    }

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
        } else /* Pauses video */ {
            buttonPlay.setText("Play");
            mediaplayer.pause();
            isPlayed = false;
        }

        // Checks if video needs to loop
        checkLoop();
    }

    /**
     * Shows the help menu **TODO**: Anyone can change the text to be more helpful
     */
    @FXML
    private void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("How to Use the Video Player");
        alert.setContentText("• Click 'Select Video File' to go back and select a file.\n" +
                             "• Use the playback controls to play, pause, and skip through the video.\n" +
                             "• Settings allow volume, mute, loop, adust speed, and filter control.\n" +
                             "• Fullscreen button toggles fullscreen mode. (escape to exit fullscreen)");
        alert.showAndWait();
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
