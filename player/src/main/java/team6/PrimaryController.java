package team6;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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

    // ------------------------------HELPER VARIABLES-------------------------------
    // Time slider booleans
    private boolean isPlayed = false;
    private boolean sliderPause = false;
    private boolean isFullscreen = false;

    // Current path
    private static String current_path = null;

    // Loop on/off boolean
    private boolean loop = false;

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

        /**
         * 
         * Handles Fullscreen and hitting esc to exit it
         */
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
                if (event.getCode() == KeyCode.ESCAPE){
                    if (isFullscreen) {
                        toggleFullScreen();
                    }
                }
            });
        });

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
     * <li>1. On Mouse Clicked (backward 10)</li>
     * <li>2. On Mouse Clicked (forward 10)</li>
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
     * <li>4. On Mouse Clicked (muteCheckBox)</li>
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
        muteCheckBox.setOnMouseClicked(event -> {
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

    /**
     * Sets the window to fullscreen 
     */
    @FXML
    private void toggleFullScreen() {
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
            mediaview.setFitHeight(VideoPlayer.getScene().getHeight() - 200);
        }
    }
    @FXML
    private void arrowSkip(){
        Scene scene = slider.getScene();
        scene.setOnKeyPressed(event ->{
            if(event.getCode() == KeyCode.A){
                if (slider.getValue() >= SKIPSEC) {
                    // Goes back ten seconds
                    setTime(slider.getValue() - SKIPSEC);
                } else /* Case 2: not normal */ {
                    // Goes back to start
                    setTime(START);
                }
            }
            if(event.getCode() == KeyCode.D){
                double endTime = getEndTime();

                // Case 1: normal
                if (slider.getValue() <= endTime - SKIPSEC) {
                    // Goes forward ten seconds
                    setTime(slider.getValue() + SKIPSEC);
                } else /* Case 2: not normal */ {
                    // Goes to end
                    setTime(endTime);
                }
            }
        });
    
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
        } else /* Pauses video */ {
            buttonPlay.setText("Play");
            mediaplayer.pause();
            isPlayed = false;
        }

        // Checks if video needs to loop
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