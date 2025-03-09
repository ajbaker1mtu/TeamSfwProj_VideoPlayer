package team6;

import java.io.IOException;
import javafx.fxml.FXML;
import java.io.File;

public class PrimaryController {

    private static File currentVideo;

    @FXML
    private void switchToSecondary() throws IOException {
        VideoPlayer.setRoot("secondary");
    }

    public static void setCurrentVideo(String path) {
        currentVideo = new File(path);
    }
}
