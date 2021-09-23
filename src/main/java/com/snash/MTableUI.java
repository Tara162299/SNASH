package com.snash;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MTableUI extends Application {

    // This should be a Metadata object, but that class isn't written yet.
    private Object metadata;
    private String filepath;

    private final int numStartingFields = 4;

    void runUI(String[] args){
        StackPane root = new StackPane();
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Enter Metadata");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button doneButton = new Button();
        doneButton.setText("Submit Metadata");
        doneButton.setOnAction((event) ->
            System.out.println("Done button pressed."));
        grid.add(doneButton, 0, 0);

        Text pathText = new Text("Save Location");
        TextField pathField = new TextField();

        grid.add(pathText, 0, 1);
        grid.add(pathField, 1, 1);


        ArrayList<TextField> fieldNames = new ArrayList<>();
        ArrayList<TextField> fieldValues = new ArrayList<>();

        for(int i = 0; i < numStartingFields; i++){
            fieldNames.add(new TextField());
            fieldValues.add(new TextField());
            grid.add(fieldNames.get(i), 0, i+2);
            grid.add(fieldValues.get(i), 1, i+2);
        }

        StackPane root = new StackPane();
        root.getChildren().add(grid);
        stage.setScene(new Scene(root, 300, 250));
        stage.show();
    }
}
