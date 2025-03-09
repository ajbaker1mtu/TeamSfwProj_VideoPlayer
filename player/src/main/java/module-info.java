module team6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens team6 to javafx.fxml;
    exports team6;
}
