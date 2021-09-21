module com.example.snash {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens com.snash to javafx.fxml;
    exports com.snash;
}