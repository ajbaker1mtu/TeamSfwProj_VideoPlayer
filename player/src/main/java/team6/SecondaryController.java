package team6;

import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SecondaryController {

    FileChooser fc = new FileChooser();

    @FXML
    private void switchToPrimary() throws IOException {
        VideoPlayer.setRoot("primary");
    }

    @FXML
    private void openFile() throws IOException {
        Window window = VideoPlayer.getScene().getWindow();
        if(window instanceof Stage) {
            Stage stage = (Stage) window;
            fc.setTitle("Choose Video to play");
            fc.setInitialDirectory(new File("C:\\"));
            File file = fc.showOpenDialog(stage);
            if(file != null) {
                //primaryController now has the file
                System.out.println(file.getAbsolutePath() + " selected"); //remove eventually
                PrimaryController.setPath(file.getAbsolutePath());
            }
        }
    }
}
