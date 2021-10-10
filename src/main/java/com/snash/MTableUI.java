package com.snash;

import javafx.scene.Group;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MTableUI extends Group {

    private final ArrayList<TablePage> pages = new ArrayList<>();
    private Metadata metadata = null;
    private String path = null;

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
    }

    void submit() {
        if(path.isEmpty()) { return; }
/*      if(metadata == null) { return; }

        setValues(metadata);
        metadata.setFilePath(finalFilePath);*/

        RecordingUI recordingUI = new RecordingUI(null);
        this.getScene().setRoot(recordingUI);
        recordingUI.startRecording();
    }

    void setPath(String path){
        this.path = path;
    }

    void createMetadata(File xml) {
        try {
            ConfigurationData config = new ConfigurationData(xml);
        } catch (Exception e){
            // Don't create any metadata.
        }
    }

    private void setValues(Metadata metadata){
        ArrayList<String> fieldNames = new ArrayList<>();
        ArrayList<String> fieldValues = new ArrayList<>();
        for (TablePage page : pages){
            fieldNames.addAll(page.getFieldNames());
            fieldValues.addAll(page.getFieldValues());
        }
        for (int i = 0; i < fieldValues.size(); i++){
            metadata.setValueOfAlias(fieldValues.get(i), fieldNames.get(i));
        }
    }
}
