package com.snash;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MTableUI extends Group {

    private final static int MAX_FIELDS_PER_PAGE = 10;

    private final ArrayList<TablePage> pages = new ArrayList<>();
    private Metadata metadata = null;
    private String path = null;
    private int fieldsDisplayed = 0;
    private int lastPage;

    public MTableUI() {
        // super(root);
        showConfigButtonPage();
    }

    private void showConfigButtonPage(){
        Button chooseConfigButton = new Button("Choose Configuration File");
        GridPane gridPane = new GridPane();
        this.getChildren().add(gridPane);

        chooseConfigButton.setOnAction((event) -> {
            File configFile = fileFromChooser();
            ConfigurationData configData;
            String warningMessage;
            try {
                configData = new ConfigurationData(configFile);
                metadata = createMetadata(configData);
                warningMessage = configData.warningMessage();
            } catch (Exception e){
                metadata = null;
                warningMessage = e.getMessage();
            }

            if (warningMessage.isEmpty()){
                if(metadata != null){
                    moveToPage(0);
                    this.getChildren().remove(gridPane);
                }
            } else {
                ScrollPane scrollPane = new ScrollPane(new Text(warningMessage));
                gridPane.add(scrollPane, 0, 1);
                if (metadata != null){
                    Button dismissWarningButton = new Button();
                    gridPane.add(dismissWarningButton, 0, 2);
                    dismissWarningButton.setText("Dismiss Warning");
                    dismissWarningButton.setOnAction((dismissEvent) -> {
                        moveToPage(0);
                        this.getChildren().remove(gridPane);
                    });
                }
            }
        });
        gridPane.add(chooseConfigButton, 0, 0);
    }

    private File fileFromChooser() {
        JFileChooser chooser = new JFileChooser();
        FileFilter xmlFilter = new FileNameExtensionFilter("XML file", "xml");
        chooser.setFileFilter(xmlFilter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showOpenDialog(null);
        return chooser.getSelectedFile();
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
            boolean isLastPage = (pageNumber == lastPage);
            TablePage newPage = new TablePage(i, nextFields(metadata), isLastPage);
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
    }

    void setPath(String path){
        this.path = path;
    }

    // Returns a list of the next undisplayed metadata fields, up to the max per page.
    // The list will be shorter if there are not that many more undisplayed fields.
    private List<Metadata.MetadataField> nextFields(Metadata metadata){
        List<Metadata.MetadataField> outputList = new ArrayList<>();
        List<Metadata.MetadataField> displayFields = metadata.displayFields();
        int i = 0;
        while (i < MAX_FIELDS_PER_PAGE && i + fieldsDisplayed < displayFields.size()){
            outputList.add(displayFields.get(i + fieldsDisplayed));
            i++;
        }
        fieldsDisplayed += i;
        return outputList;
    }

    private Metadata createMetadata(ConfigurationData config) {
        Metadata newMetadata = new Metadata(config);
        lastPage = newMetadata.displayFields().size() / MAX_FIELDS_PER_PAGE;
        return newMetadata;
    }

    private void updateMetadataValues(){
        for (TablePage page : pages){
            page.updateMetadataValues();
        }
    }
}
