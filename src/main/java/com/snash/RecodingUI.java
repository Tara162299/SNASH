package com.snash;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.Sound;

import java.util.ArrayList;

public class RecordingUI extends Application {

    //private Scene scene;

    public void Start(Stage inputStage) {
      GridPane grid = new GridPane();
      grid.setAlignment(Pos.CENTER);
      grid.setHgap(10);
      grid.setVgap(10);
      grid.setPadding(new Insets(25, 25, 25, 25));
      Scene scene = new Scene(grid, 300, 275);
      stage.setTitle("Recording");
      stage.setScene(scene);
      stage.sizeToScene();
      stage.show();
    }

    private void addRecordButton(Grid grid) {
      Button btn = new Button("Record");
      HBox hbBtn = new HBox(10);
      hbBtn.setAlignment(Pos.BOTTOM_MIDDLE);
      hbBtn.getChildren().add(btn);
      grid.add(hbBtn, 1, 4);
    }

    private void addStopButton(Grid grid) {
      Button btn = new Button("Stop");
      HBox hbBtn = new HBox(10);
      hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
      hbBtn.getChildren().add(btn);
      grid.add(hbBtn, 1, 4);
    }

    // private ArrayList<TablePage> pages = new ArrayList<>();
    // private int currentPageNumber = 0;
    // private String finalPathFieldText = null;
    //
    // public String getPath(){
    //     return finalPathFieldText;
    // }
    //
    // public void runUI(String[] args){
    //     launch(args);
    // }
    //
    // @Override
    // public void start(Stage inputStage) {
    //     stage = inputStage;
    //     stage.setTitle("Enter Metadata");
    //     pages.add(newPage(0));
    //     showPage(0);
    // }
    // /*
    // private void close(){
    //     stage.close();
    // }
    // */
    // private void showPage(int pageNumber){
    //     StackPane root = new StackPane();
    //     root.getChildren().add(pages.get(pageNumber).getGrid());
    //     stage.setScene(new Scene(root));
    //     stage.setMaximized(true);
    //     stage.show();
    // }
    //
    // // This is a replacement for "nextPage" and "previousPage" to reduce duplicate code.
    // // Safeguards are in place for bad page numbers, but for now, stick to one more or less than the current page.
    // private void moveToPage(int pageNumber){
    //     if (pageNumber < 0){
    //         return;
    //     }
    //
    //     String pathFieldText = pathFieldText(currentPageNumber);
    //
    //     // If the page number is too high, add more pages.
    //     for (int i = pages.size(); i <= pageNumber; i++){
    //         TablePage newPage = newPage(i);
    //         pages.add(newPage);
    //     }
    //     pages.get(pageNumber).setPathFieldText(pathFieldText);
    //     showPage(pageNumber);
    //     currentPageNumber = pageNumber;
    // }
    //
    // private void submit() {
    //     finalPathFieldText = pathFieldText(currentPageNumber);
    //     System.out.println("finalPathFieldText = " + finalPathFieldText);
    //     stage.close();
    // }
    //
    // // Returns a new page with the specified pageNumber.
    // // Page number is passed (rather than read from global) as not to be dependent on when currentPageNumber is incremented.
    // private TablePage newPage(int pageNumber){
    //     Button doneButton = new Button("Start Recording");
    //     doneButton.setOnAction((event) ->
    //             submit());
    //
    //     Button previousButton = new Button("Previous");
    //     previousButton.setOnAction((event) ->
    //             moveToPage(pageNumber - 1));
    //
    //     Button nextButton = new Button("Next");
    //     nextButton.setOnAction((event) ->
    //             moveToPage(pageNumber + 1));
    //
    //     TablePage outputPage = new TablePage();
    //     outputPage.setPreviousButton(previousButton);
    //     outputPage.setNextButton(nextButton);
    //     outputPage.setDoneButton(doneButton);
    //     outputPage.setPageNumber(pageNumber);
    //
    //     return outputPage;
    // }
    //
    // private String pathFieldText(int pageNumber){
    //     return pages.get(pageNumber).getPathFieldText();
    // }
}
