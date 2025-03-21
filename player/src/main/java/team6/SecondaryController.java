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

    private static final int RECENT_VIDEO_DISPLAY_SIZE = 5; //amount of recently opened files to display
    //list of allowed extensions, to edit the list of extensions: Ex: {"*.mp4"} -> {"*.mp4", "*.mkv"}
    private static final ExtensionFilter ALLOWED_EXTENSIONS = new ExtensionFilter("Video Files", new String[] {"*.mp4"});
    private static final String INITIAL_PATH = "C:\\"; //initial path for the file chooser
    private static final boolean SHOW_FULL_PATH = true; //whether or not to show full path for open recent file

    FileChooser fc = new FileChooser();

    private static ArrayList<String> previousVideos = new ArrayList<String>(); //list storing previously opened files

    @FXML
    private ComboBox<String> videoDrop; //dropdown menu for recently opened files

    @FXML
    private void switchToPrimary() throws IOException {
        if(PrimaryController.getPath() != null) {
            VideoPlayer.setRoot("primary");
        }
    }

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
            if (file != null) {
                // primaryController now has the file
                //System.out.println(file.getAbsolutePath() + " selected");
                previousVideos.add(0,file.getAbsolutePath());
                PrimaryController.setPath(file.getAbsolutePath());
            }
        }
    }

    @FXML
    public void initialize() {
        //show recently opened files
        int s = previousVideos.size();
        if(RECENT_VIDEO_DISPLAY_SIZE < s) { //get the minimum of RECENT_VIDEO_DISPLAY_SIZE and amount of recent files
            s = RECENT_VIDEO_DISPLAY_SIZE;
        }
        String[] displayList = new String[s];
        for(int i = 0; i < s; i++) {
            if(!SHOW_FULL_PATH) { //to shorten file path to just file name
                String ds = previousVideos.get(i);
                for(int j = 0;j < ds.length(); j++) { //loop through and get substring at '\'
                    if(ds.charAt(j) == '\\') {
                        ds = ds.substring(j+1, ds.length());
                    }
                }
                displayList[i] = ds;
            }else { //show full file path
                displayList[i] = previousVideos.get(i);
            }
        }
        videoDrop.getItems().addAll(displayList);
        videoDrop.setOnAction(event -> {
            if(!SHOW_FULL_PATH) { //to shorten file path to just file name
                for(int i = 0; i < videoDrop.getItems().size(); i++) {
                    if(!SHOW_FULL_PATH) {
                        String ds = previousVideos.get(i);
                        for(int j = 0;j < ds.length(); j++) {//loop through and get substring at '\'
                            if(ds.charAt(j) == '\\') {
                                ds = ds.substring(j+1, ds.length());
                            }
                        }
                        if(ds == videoDrop.getSelectionModel().getSelectedItem().toString()) { //get the path of the video
                            PrimaryController.setPath(previousVideos.get(i));
                            break;
                        }
                    }
                }
            }else { //show full file path
                    PrimaryController.setPath(videoDrop.getSelectionModel().getSelectedItem().toString());
            }
        });
    }
}
