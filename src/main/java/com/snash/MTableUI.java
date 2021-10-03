package com.snash;

import javafx.scene.Group;

import java.util.ArrayList;

public class MTableUI extends Group {

    private ArrayList<TablePage> pages = new ArrayList<>();
    private int currentPageNumber = 0;
    private String finalFilePath = null;

    public MTableUI() {
        // super(root);
        pages.add(new TablePage(0));
        showPage(0);
    }

    public String getFilePath(){
        return finalFilePath;
    }

    private void showPage(int pageNumber){
        if(pages.size() > 1) {
            // Remove the currently displayed page before displaying a new one.
            this.getChildren().remove(0);
        } else {
            // Don't remove pages on displaying first page.
        }
        this.getChildren().add(pages.get(pageNumber));
    }

    // This is a replacement for "nextPage" and "previousPage" to reduce duplicate code.
    // Safeguards are in place for bad page numbers, but for now, stick to one more or less than the current page.
    void moveToPage(int pageNumber){
        if (pageNumber < 0){
            return;
        }

        String pathFieldText = pathFieldText(currentPageNumber);

        // If the page number is too high, add more pages.
        for (int i = pages.size(); i <= pageNumber; i++){
            TablePage newPage = new TablePage(i);
            pages.add(newPage);
        }
        pages.get(pageNumber).setPathFieldText(pathFieldText);
        showPage(pageNumber);
        currentPageNumber = pageNumber;
    }

    void submit() {
        finalFilePath = pathFieldText(currentPageNumber);
        RecordingUI recordingUI = new RecordingUI();
        this.getScene().setRoot(recordingUI);
        recordingUI.startRecording();
    }

    private String pathFieldText(int pageNumber){
        return pages.get(pageNumber).getPathFieldText();
    }
}
