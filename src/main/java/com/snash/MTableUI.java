package com.snash;

import javafx.scene.Group;

import javax.swing.*;
import java.util.ArrayList;

public class MTableUI extends Group {

    private ArrayList<TablePage> pages = new ArrayList<>();
    private int currentPageNumber = 0;
    private Metadata metadata = null;
    private String filePath = null;

    public MTableUI() {
        // super(root);
        pages.add(new TablePage(0));
        showPage(0);
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

        // If the page number is too high, add more pages.
        for (int i = pages.size(); i <= pageNumber; i++){
            TablePage newPage = new TablePage(i);
            pages.add(newPage);
        }
        showPage(pageNumber);
        currentPageNumber = pageNumber;
    }

    void chooseFilePath(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    void submit() {
        metadata = temporaryMetadataREMOVETHIS();

        if(filePath == null) { return; }
        if(metadata == null) { return; }

        metadata.setFilePath(filePath);

        RecordingUI recordingUI = new RecordingUI(metadata);
        this.getScene().setRoot(recordingUI);
        recordingUI.startRecording();
    }

    // A blank metadata to bypass the metadata == null check, just for now.
    // Remove this once JSON -> Metadata is implemented!!
    private Metadata temporaryMetadataREMOVETHIS(){
        return new Metadata(new String[0], new String[0]);
    }
}
