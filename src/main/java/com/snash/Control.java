package com.snash;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Control extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Enter Metadata");
        MTableUI tableScene = new MTableUI(new Group());
        stage.setScene(tableScene);
        stage.show();
        System.out.println(tableScene.getFilePath());
    }
}
