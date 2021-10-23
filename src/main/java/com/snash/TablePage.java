package com.snash;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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

        public Text getAliasText(){
            return aliasText;
        }

        public TextField getValueField(){
            return valueField;
        }

        public void updateMetadataField(){
            metadataField.setValue(valueField.getText());
        }
    }

    // How many hardcoded lines come before the field grid, i.e. buttons or filepath field.
    public static final int fieldGridOffset = 1;


    private final List<FieldDisplay> displays = new ArrayList<>();

    public TablePage(int pageNumber, List<MetadataField> fields){

        GridPane grid = new GridPane();

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);


        Button choosePathButton = new Button("Choose Save Location");
        choosePathButton.setOnAction((event) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.showOpenDialog(null);
            if(chooser.getSelectedFile() != null){
                ((MTableUI) getParent()).setPath(chooser.getSelectedFile().getPath());
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

        Button previousButton = new Button("Previous");
        previousButton.setOnAction((event) ->
                ((MTableUI) getParent()).moveToPage(pageNumber - 1));

        Button nextButton = new Button("Next");
        nextButton.setOnAction((event) ->
                ((MTableUI) getParent()).moveToPage(pageNumber + 1));

        grid.add(previousButton, 0, displays.size() + fieldGridOffset);
        grid.add(nextButton, 1, displays.size() + fieldGridOffset);
        grid.add(recordButton, 0, displays.size() + fieldGridOffset + 1);
        grid.add(new Text("Page " + (pageNumber + 1)), 1, displays.size() + fieldGridOffset + 1);

        getChildren().add(grid);
    }

    public void updateMetadataValues() {
        for (FieldDisplay display : displays) {
            display.updateMetadataField();
        }
    }
}
