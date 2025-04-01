package team6;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SecondaryController {

    // ----------------------------------CONSTANTS----------------------------------
    // Amount of recently opened files to display
    private static final int RECENT_VIDEO_DISPLAY_SIZE = 5;

    // To edit the list of extensions: Ex: {"*.mp4"} -> {"*.mp4", "*.mkv"}
    private static final ExtensionFilter ALLOWED_EXTENSIONS = new ExtensionFilter("Video Files",
            new String[] { "*.mp4" });

    // Initial path for the file chooser
    private static final String INITIAL_PATH = "C:\\";

    // Whether or not to show full path for open recent file
    private static final boolean SHOW_FULL_PATH = false;

    private static final int PATH_INDEX = 0;
    private static final int FIRST_FILE_INDEX = 1;

    // --------------------------------FXML ELEMENTS--------------------------------
    // Dropdown menu for recently opened files
    @FXML
    private ComboBox<String> videoDrop;

    // ------------------------------HELPER VARIABLES-------------------------------
    // For choosing a file
    private FileChooser fc = new FileChooser();

    // List storing previously opened files
    // For every element: path at index 0, file name at index 1
    private static ArrayList<ArrayList<String>> previousVideos = new ArrayList<ArrayList<String>>();

    // ------------------------------INITIALIZE WINDOW------------------------------
    /**
     * Initializes the SecondaryController window
     */
    @FXML
    public void initialize() {
        // Show recently opened files
        int s = previousVideos.size();

        // Get the minimum of RECENT_VIDEO_DISPLAY_SIZE and amount of recent files
        if (RECENT_VIDEO_DISPLAY_SIZE < s) {
            s = RECENT_VIDEO_DISPLAY_SIZE;
        }

        // List of files to be displayed
        String[] displayList = new String[s];

        // Loops through and adds all listed elements
        for (int i = 0; i < s; i++) {

            // For file name
            if (!SHOW_FULL_PATH) {
                displayList[i] = previousVideos.get(i).get(FIRST_FILE_INDEX);
            } else /* Show full file path */ {
                displayList[i] = previousVideos.get(i).get(PATH_INDEX);
            }
        }

        videoDrop.getItems().addAll(displayList);

        // When video list is clicked
        videoDrop.setOnAction(event -> {
            // For file name
            if (!SHOW_FULL_PATH) {
                for (int i = 0; i < videoDrop.getItems().size(); i++) {
                    if (previousVideos.get(i).get(FIRST_FILE_INDEX) == videoDrop.getSelectionModel().getSelectedItem()
                            .toString()) {
                        PrimaryController.setPath(previousVideos.get(i).get(PATH_INDEX));
                        break;
                    }
                }
            } else /* Show full file path */ {
                PrimaryController.setPath(videoDrop.getSelectionModel().getSelectedItem().toString());
            }
        });
    }

    // -------------------------------FXML FUNCTIONS-------------------------------
    /**
     * Switches view to PrimaryController
     * 
     * @throws IOException Thrown if root is non-existant
     */
    @FXML
    private void switchToPrimary() throws IOException {
        if (PrimaryController.getPath() != null) {
            VideoPlayer.setRoot("primary");
        }
    }

    /**
     * Opens file
     * 
     * @throws IOException Thrown if the file is invalid
     */
    @FXML
    private void openFile() throws IOException {
        Window window = VideoPlayer.getScene().getWindow();
        if (window instanceof Stage) {
            Stage stage = (Stage) window;

            fc.setTitle("Choose Video to play");
            fc.setInitialDirectory(new File(INITIAL_PATH));
            fc.getExtensionFilters().addAll(ALLOWED_EXTENSIONS);
            fc.setSelectedExtensionFilter(ALLOWED_EXTENSIONS);

            File file = fc.showOpenDialog(stage);

            // Checks if file has a valid file
            if (file != null) {
                // PrimaryController now has the file
                // System.out.println(file.getAbsolutePath() + " selected");
                ArrayList<String> path = new ArrayList<String>();
                path.add(PATH_INDEX, file.getAbsolutePath());
                String ds = path.get(PATH_INDEX);

                // Loop through and get substring at '\'
                for (int j = 0; j < ds.length(); j++) {
                    if (ds.charAt(j) == '\\') {
                        ds = ds.substring(j + 1, ds.length());
                    }
                }

                path.add(FIRST_FILE_INDEX, ds);
                previousVideos.add(PATH_INDEX, path);
                PrimaryController.setPath(file.getAbsolutePath());
            }
        }
    }
}
