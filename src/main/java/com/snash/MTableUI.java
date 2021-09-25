package com.snash;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MTableUI extends Application {

    // This should be a Metadata object, but that class isn't written yet.
    private Object metadata;
    private String filepath;

    private ArrayList<TablePage> pages = new ArrayList<>();
    private int currentPage = 0;

    void runUI(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setMaximized(true);

        stage.setTitle("Enter Metadata");

        pages.add(newPage());

        StackPane root = new StackPane();
        root.getChildren().add(pages.get(0).getGrid());
        stage.setScene(new Scene(root));
        stage.show();
    }

    void previousPage(){

    }

    void nextPage(){

    }

    void submit(){

    }

    private TablePage newPage(){
        Button doneButton = new Button();
        doneButton.setText("Submit Metadata");
        doneButton.setOnAction((event) ->
                submit());

        Button previousButton = new Button();
        previousButton.setText("Previous");
        previousButton.setOnAction((event) ->
                previousPage());

        Button nextButton = new Button();
        nextButton.setText("Next");
        nextButton.setOnAction((event) ->
                nextPage());

        return new TablePage(previousButton, nextButton, doneButton);
    }
}
