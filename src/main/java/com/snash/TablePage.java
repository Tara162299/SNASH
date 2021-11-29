package com.snash;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.snash.Metadata.MetadataField;

public class TablePage extends Group {

    public class FieldDisplay{
        private final MetadataField metadataField;
        private final Text aliasText;
        private final TextField valueField;

        public FieldDisplay(Metadata.MetadataField metadataField){
            this.metadataField = metadataField;
            aliasText = new Text(metadataField.getAlias());
            valueField = new TextField(metadataField.getValue());
        }

        public void updateMetadataField(){
            metadataField.setValue(valueField.getText());
        }
    }

    // How many hardcoded lines come before the field grid, i.e. buttons or filepath field.
    public static final int fieldGridOffset = 1;



    private final GridPane grid = new GridPane();
    private final List<FieldDisplay> displays = new ArrayList<>();

    public TablePage(int pageNumber, List<MetadataField> fields, boolean isLastPage, String userWarning){
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Button choosePathButton = new Button("Choose Save Location");
        choosePathButton.setOnAction((event) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.showOpenDialog(null);
            if(chooser.getSelectedFile() != null){
                ((MTableUI) getParent()).setFile(chooser.getSelectedFile());
            }
        });

        grid.add(choosePathButton, 1, 0);

        for(int i = 0; i < fields.size(); i++){
            FieldDisplay newDisplay = new FieldDisplay(fields.get(i));
            displays.add(newDisplay);
            grid.add(newDisplay.aliasText, 0, i + fieldGridOffset);
            grid.add(newDisplay.valueField, 1, i + fieldGridOffset);
        }

        Button recordButton = new Button("Start Recording");
        recordButton.setOnAction(actionEvent ->
            ((MTableUI) getParent()).submit());

        if (pageNumber != 0) {
            grid.add(previousButton(pageNumber), 0, displays.size() + fieldGridOffset);
        }

        if (!isLastPage) {
            grid.add(nextButton(pageNumber), 1, displays.size() + fieldGridOffset);
        }

        grid.add(recordButton, 0, displays.size() + fieldGridOffset + 1);
        grid.add(new Text("Page " + (pageNumber + 1)), 1, displays.size() + fieldGridOffset + 1);

        getChildren().add(grid);

        if (!(userWarning == null || userWarning.isEmpty())){
            setWarning(userWarning);
        }

    }

    private Button previousButton(int pageNumber){
        Button previousButton = new Button("Previous");
        previousButton.setOnAction((event) ->
                ((MTableUI) getParent()).moveToPage(pageNumber - 1));
        return previousButton;
    }

    private Button nextButton(int pageNumber){
        Button nextButton = new Button("Next");
        nextButton.setOnAction((event) ->
                ((MTableUI) getParent()).moveToPage(pageNumber + 1));
        return nextButton;
    }

    /**
     * Adds a warning message to the page.
     * @throws NullPointerException if the message is null
     * @throws IllegalArgumentException if the message is empty
     * @param warningMessage - the message to display
     */
    public void setWarning(String warningMessage){
        Objects.requireNonNull(warningMessage);
        if (warningMessage.isEmpty()) { throw new IllegalArgumentException(); }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(new Text(warningMessage));
        GridPane.setRowSpan(scrollPane, 2);
        GridPane.setColumnSpan(scrollPane, 2);
        grid.add(scrollPane, 0, displays.size() + fieldGridOffset + 2);
    }

    public void updateMetadataValues() {
        for (FieldDisplay display : displays) {
            display.updateMetadataField();
        }
    }
}
