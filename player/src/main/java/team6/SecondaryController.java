package team6;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonReader;

public class SecondaryController {

    // ----------------------------------CONSTANTS----------------------------------
    // Amount of recently opened files to display
    // Max amount: 100 <-- not implemented yet <-- subject to change
    private static int RECENT_VIDEO_DISPLAY_SIZE = 5;

    // To edit the list of extensions: Ex: {"*.mp4"} -> {"*.mp4", "*.mkv"}
    private static ExtensionFilter ALLOWED_EXTENSIONS = new ExtensionFilter("Video Files",
            new String[] { "*.mp4" });

    // Initial path for the file chooser
    private static String INITIAL_PATH = "C:\\";

    // Whether or not to show full path for open recent file
    private static boolean SHOW_FULL_PATH = false;

    private static final int PATH_INDEX = 0;
    private static final int FIRST_FILE_INDEX = 1;

    // Whether or not to use the JSON system
    private static final boolean USE_JSON = false;
    // The JSON file's path
    private static final String JSON_FILE = "player\\src\\main\\java\\team6\\config.json";

    // --------------------------------FXML ELEMENTS--------------------------------
    // Dropdown menu for recently opened files
    @FXML
    private ComboBox<String> videoDrop;

    // ------------------------------HELPER VARIABLES-------------------------------
    // For choosing a file
    private FileChooser fc = new FileChooser();

    // List storing previously opened files
    // For every element: path at index PATH_INDEX, file name at index FIRST_FILE_INDEX
    private static ArrayList<ArrayList<String>> previousVideos = new ArrayList<ArrayList<String>>();
    // List for storing previously opened files gotten from JSON file
    private ArrayList<ArrayList<String>> displayVideos = new ArrayList<ArrayList<String>>();

    // ------------------------------INITIALIZE WINDOW------------------------------
    /**
     * Initializes the SecondaryController window
     */
    @FXML
    public void initialize() {
        // load contents of JSON file
        if(USE_JSON) {
            loadJSON();
        }

        // Show recently opened files
        int s;
        if(USE_JSON) {
            s = displayVideos.size();
        }else {
            s = previousVideos.size();
        }

        // Get the minimum of RECENT_VIDEO_DISPLAY_SIZE and amount of recent files
        if (RECENT_VIDEO_DISPLAY_SIZE < s) {
            s = RECENT_VIDEO_DISPLAY_SIZE;
        }

        // List of files to be displayed
        String[] displayList = new String[s];

        // Loops through and adds all listed elements
        for (int i = 0; i < s; i++) {

            String file_name;
            String file_path;
            if(USE_JSON) {
                file_name = displayVideos.get(i).get(FIRST_FILE_INDEX);
                file_path = displayVideos.get(i).get(PATH_INDEX);
            }else {
                file_name = previousVideos.get(i).get(FIRST_FILE_INDEX);
                file_path = previousVideos.get(i).get(PATH_INDEX);
            }

            // For file name
            if (!SHOW_FULL_PATH) {
                displayList[i] = file_name;
            } else /* Show full file path */ {
                displayList[i] = file_path;
            }
        }

        // Load displayList into the recent video dropdown menu
        videoDrop.getItems().addAll(displayList);

        // When video list is clicked
        videoDrop.setOnAction(event -> {

            // Gets the selected video
            String selected_video = videoDrop.getSelectionModel().getSelectedItem().toString();
            
            // For file name
            if (!SHOW_FULL_PATH) {
                // Search video list for selected video, and get path of that file
                for (int i = 0; i < videoDrop.getItems().size(); i++) {
                    // Get file name
                    String file_name;
                    if(USE_JSON) {
                        file_name = displayVideos.get(i).get(FIRST_FILE_INDEX);
                    }else {
                        file_name = previousVideos.get(i).get(FIRST_FILE_INDEX);
                    }
                    
                    // Check to see if the current file is the selected video
                    if (file_name == selected_video) {
                        // Get full path
                        String full_path;
                        if(USE_JSON) {
                            full_path = displayVideos.get(i).get(PATH_INDEX);
                        }else {
                            full_path = previousVideos.get(i).get(PATH_INDEX);
                        }
                        PrimaryController.setPath(full_path);
                        break;
                    }
                }
            } else /* Show full file path */ {
                PrimaryController.setPath(selected_video);
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

            // Set up file chooser
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
                if(USE_JSON) {
                    // Create new JSON Object for storing the new video file
                    JsonObject newFileEntry = new JsonObject();
                    newFileEntry.addProperty("file name",ds);
                    newFileEntry.addProperty("file path",file.getAbsolutePath());

                    writeToJSON(newFileEntry);
                }else { // if USE_JSON is false
                    // Add file to previousVideos
                    path.add(FIRST_FILE_INDEX, ds);
                    previousVideos.add(0, path);
                }

                // Set primary controller's path to selected video
                PrimaryController.setPath(file.getAbsolutePath());
            }
        }
    }

    // Loads JSON files
    private void loadJSON() {
        try (JsonReader jr = new JsonReader(new FileReader(JSON_FILE))) {
            jr.beginObject();

            // Get recent video display size
            jr.nextName();
            RECENT_VIDEO_DISPLAY_SIZE = jr.nextInt();
    
            // Get allowed extensions
            jr.nextName();
            jr.beginArray();
            ArrayList<String> tempExt = new ArrayList<String>();
            while(jr.hasNext()) {
                tempExt.add(jr.nextString());
            }
            String[] ExtList = new String[tempExt.size()];
            for(int i = 0; i < tempExt.size();i++) {
                ExtList[i] = tempExt.get(i);
            }
            ALLOWED_EXTENSIONS = new ExtensionFilter("Video Files",ExtList);
            jr.endArray();
    
            // Get show full path
            jr.nextName();
            SHOW_FULL_PATH = jr.nextBoolean();
    
            // Get initial path
            jr.nextName();
            INITIAL_PATH = jr.nextString();

            // Get recent videos and add them to displayVideos
            String key = jr.nextName();
            jr.beginArray();
            while(jr.hasNext()) {
                jr.beginObject();
                jr.nextName();
                String fn = jr.nextString();
                jr.nextName();
                String fp = jr.nextString();
                jr.endObject();
                ArrayList<String> file_np = new ArrayList<String>();
                file_np.add(PATH_INDEX,fp);
                file_np.add(FIRST_FILE_INDEX,fn);
                displayVideos.add(file_np);
            }
            jr.endArray();
            jr.endObject();
    
        } catch (IOException e) {e.printStackTrace();}
    }

    // Write a given video (as JsonObject) into the JSON file
    private void writeToJSON(JsonObject newFileEntry) {
        // Update recent video array in JSON file
        try (FileReader r = new FileReader(JSON_FILE)) {
            // Load recent video array
            JsonObject mainObject = JsonParser.parseReader(r).getAsJsonObject();
            JsonArray videoArray = mainObject.getAsJsonArray("recent videos");
            JsonArray mainArray = new JsonArray();
    
            // Add new video to array
            mainArray.add(newFileEntry);
            mainArray.addAll(videoArray);
            mainObject.add("recent videos",mainArray);
    
            // Write new array to JSON file
            try (FileWriter fw = new FileWriter(JSON_FILE)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(mainObject,fw);
            } catch(IOException e) {e.printStackTrace();}
        } catch(IOException e) {e.printStackTrace();}
    }
}