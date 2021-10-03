module com.snash {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    //requires javafx.web;

    opens com.snash to javafx.fxml;
    exports com.snash;
}
