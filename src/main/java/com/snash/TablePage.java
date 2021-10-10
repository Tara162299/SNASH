package com.snash;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class TablePage extends Group {

    // How many "generic fields" should be on the page- i.e. not hardcoded by the devs.
    public static final int numFields = 9;
    // How many hardcoded lines come before the field grid, i.e. buttons or filepath field.
    public static final int fieldGridOffset = 1;

    // fieldNames should be made into Text objects once json files are readable.
    private final ArrayList<TextField> fieldNames = new ArrayList<>();
    private final ArrayList<TextField> fieldValues = new ArrayList<>();

    public TablePage(int pageNumber){

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Button chooseConfigButton = new Button("Choose Configuration File");
        chooseConfigButton.setOnAction((event) -> {
            JFileChooser chooser = new JFileChooser();
            FileFilter xmlFilter = new FileNameExtensionFilter("XML file", "xml");
            chooser.setFileFilter(xmlFilter);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.showOpenDialog(null);
            if(chooser.getSelectedFile() != null){
                ((MTableUI) getParent()).createMetadata(chooser.getSelectedFile());
            }
        });

        Button choosePathButton = new Button("Choose Save Location");
        choosePathButton.setOnAction((event) -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.showOpenDialog(null);
            if(chooser.getSelectedFile() != null){
                ((MTableUI) getParent()).setPath(chooser.getSelectedFile().getPath());
            }
        });

        grid.add(chooseConfigButton, 0, 0);
        grid.add(choosePathButton, 1, 0);

        for(int i = 0; i < numFields; i++){
            fieldNames.add(new TextField());
            fieldValues.add(new TextField());
            grid.add(fieldNames.get(i), 0, i + fieldGridOffset);
            grid.add(fieldValues.get(i), 1, i + fieldGridOffset);
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

        grid.add(previousButton, 0, numFields + fieldGridOffset);
        grid.add(nextButton, 1, numFields + fieldGridOffset);
        grid.add(recordButton, 0, numFields + fieldGridOffset + 1);
        grid.add(new Text("Page " + (pageNumber + 1)), 1, numFields + fieldGridOffset + 1);

        getChildren().add(grid);
    }

    public List<String> getFieldNames(){
        List<String> output = new ArrayList<>();
        for (TextField name : fieldNames) {
            if (!name.getText().isEmpty()) {
                output.add(name.getText());
            }
        }
        return output;
    }

    public List<String> getFieldValues(){
        List<String> output = new ArrayList<>();
        for (TextField value : fieldValues) {
            if (!value.getText().isEmpty()){
                output.add(value.getText());
            }
        }
        return output;
    }


}
