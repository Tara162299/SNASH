package com.snash;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Control extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Enter Metadata");
        MTableUI mTableUI = new MTableUI();
        BorderPane.setAlignment(mTableUI, Pos.CENTER);
        stage.setScene(new Scene(new BorderPane(mTableUI)));
        stage.setMaximized(true);
        stage.show();
    }
}
