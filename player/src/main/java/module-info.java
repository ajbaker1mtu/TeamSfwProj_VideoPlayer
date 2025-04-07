module team6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires org.json;
    requires com.google.gson;

    opens team6 to javafx.fxml;

    exports team6;
}
