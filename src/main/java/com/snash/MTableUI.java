package com.snash;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MTableUI extends Application {

    private Stage stage;

    private ArrayList<TablePage> pages = new ArrayList<>();
    private int currentPageNumber = 0;
    private String finalPathFieldText = null;

    public String getPath(){
        return finalPathFieldText;
    }

    public void runUI(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage inputStage) {
        stage = inputStage;
        stage.setTitle("Enter Metadata");
        pages.add(newPage(0));
        showPage(0);
    }

    private void close(){
        stage.close();
    }

    private void showPage(int pageNumber){
        StackPane root = new StackPane();
        root.getChildren().add(pages.get(pageNumber).getGrid());
        stage.setScene(new Scene(root));
        stage.show();
    }

    // This is a replacement for "nextPage" and "previousPage" to reduce duplicate code.
    // Safeguards are in place for bad page numbers, but for now, stick to one more or less than the current page.
    private void moveToPage(int pageNumber){
        if (pageNumber < 0){
            return;
        }

        String pathFieldText = pathFieldText(currentPageNumber);

        // If the page number is too high, add more pages.
        for (int i = pages.size(); i <= pageNumber; i++){
            TablePage newPage = newPage(i);
            pages.add(newPage);
        }
        pages.get(pageNumber).setPathFieldText(pathFieldText);
        showPage(pageNumber);
        currentPageNumber = pageNumber;
    }

    private void submit() {
        finalPathFieldText = pathFieldText(currentPageNumber);
        System.out.println("finalPathFieldText = " + finalPathFieldText);
        stage.close();
    }

    // Returns a new page with the specified pageNumber.
    // Page number is passed (rather than read from global) as not to be dependent on when currentPageNumber is incremented.
    private TablePage newPage(int pageNumber){
        Button doneButton = new Button("Submit Metadata");
        doneButton.setOnAction((event) ->
                submit());

        Button previousButton = new Button("Previous");
        previousButton.setOnAction((event) ->
                moveToPage(pageNumber - 1));

        Button nextButton = new Button("Next");
        nextButton.setOnAction((event) ->
                moveToPage(pageNumber + 1));

        TablePage outputPage = new TablePage();
        outputPage.setPreviousButton(previousButton);
        outputPage.setNextButton(nextButton);
        outputPage.setDoneButton(doneButton);
        outputPage.setPageNumber(pageNumber);

        return outputPage;
    }

    private String pathFieldText(int pageNumber){
        return pages.get(pageNumber).getPathFieldText();
    }
}
