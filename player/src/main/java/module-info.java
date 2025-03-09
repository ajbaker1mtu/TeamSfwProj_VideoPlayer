module team6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;

    opens team6 to javafx.fxml;
    exports team6;
}
