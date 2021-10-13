package com.snash;

import javafx.scene.Group;
import javafx.scene.control.Button;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MTableUI extends Group {

    private final static int MAX_FIELDS_PER_PAGE = 9;

    private final ArrayList<TablePage> pages = new ArrayList<>();
    private Metadata metadata = null;
    private String path = null;
    private int fieldsDisplayed = 0;

    public MTableUI() {
        // super(root);
        showConfigButtonPage();
    }

    private void showConfigButtonPage(){
        Button chooseConfigButton = new Button("Choose Configuration File");
        chooseConfigButton.setOnAction((event) -> {
            JFileChooser chooser = new JFileChooser();
            FileFilter xmlFilter = new FileNameExtensionFilter("XML file", "xml");
            chooser.setFileFilter(xmlFilter);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.showOpenDialog(null);
            if(chooser.getSelectedFile() != null){
                metadata = createMetadata(chooser.getSelectedFile());
            }
            moveToPage(0);
            this.getChildren().remove(chooseConfigButton);
        });
        this.getChildren().add(chooseConfigButton);
    }

    private void showPage(int pageNumber){
        // Remove previous page, but not when displaying first page.
        if(pages.size() > 1) {
            // Remove the currently displayed page before displaying a new one.
            this.getChildren().remove(0);
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
            TablePage newPage = new TablePage(i, nextXFields(metadata, MAX_FIELDS_PER_PAGE));
            pages.add(newPage);
        }
        showPage(pageNumber);
    }

    void submit() {
        if(path == null) { return; }
        if(path.isEmpty()) { return; }
        if(metadata == null) { return; }

        updateMetadataValues();
        metadata.setFilePath(path);

        RecordingUI recordingUI = new RecordingUI(metadata);
        this.getScene().setRoot(recordingUI);
        recordingUI.startRecording();
    }

    void setPath(String path){
        this.path = path;
    }

    // Returns a list of the next (at most) x undisplayed metadata fields.
    // The list will be shorter if there are not x more undisplayed fields.
    private List<Metadata.MetadataField> nextXFields(Metadata metadata, int x){
        List<Metadata.MetadataField> outputList = new ArrayList<>();
        List<Metadata.MetadataField> displayFields = metadata.displayFields();
        int i = 0;
        while (i < x && i + fieldsDisplayed < displayFields.size()){
            outputList.add(displayFields.get(i + fieldsDisplayed));
            i++;
        }
        fieldsDisplayed += i;
        return outputList;
    }

    private Metadata createMetadata(File xml) {
        try {
            ConfigurationData config = new ConfigurationData(xml);
            return new Metadata(config);
        } catch (Exception e){
            return null;
        }
    }

    private void updateMetadataValues(){
        for (TablePage page : pages){
            page.updateMetadataValues();
        }
    }
}
