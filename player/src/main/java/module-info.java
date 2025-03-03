module team6 {
    requires javafx.controls;
    requires javafx.fxml;

    opens team6 to javafx.fxml;
    exports team6;
}
