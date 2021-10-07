package com.snash;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Control extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Enter Metadata");
        stage.setScene(new Scene(new MTableUI()));
        stage.show();
        // System.out.println(tableScene.getFilePath());
    }
}
