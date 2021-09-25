package com.snash;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class TablePage {

    // How many "generic fields" should be on the page- i.e. not hardcoded by the devs.
    public static final int numFields = 9;
    // How many hardcoded lines come before the field grid, i.e. buttons or filepath field.
    public static final int fieldGridOffset = 1;

    private GridPane grid;
    private ArrayList<TextField> fieldNames = new ArrayList<>();
    private ArrayList<TextField> fieldValues = new ArrayList<>();
    private TextField pathField = new TextField();

    // Buttons are passed as parameters, as they interact with MTableUI code and can't be initialized here.
    public TablePage(){
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Text pathText = new Text("Save Location");
        grid.add(pathText, 0, 0);
        grid.add(pathField, 1, 0);

        for(int i = 0; i < numFields; i++){
            fieldNames.add(new TextField());
            fieldValues.add(new TextField());
            grid.add(fieldNames.get(i), 0, i + fieldGridOffset);
            grid.add(fieldValues.get(i), 1, i + fieldGridOffset);
        }
    }

    public GridPane getGrid() { return grid; }

    public String getPathFieldText(){
        return pathField.getText();
    }
    public void setPathFieldText(String pathFieldText){
        pathField.setText(pathFieldText);
    }


    public void setPreviousButton(Button previousButton){
        grid.add(previousButton, 0, numFields + fieldGridOffset);
    }
    public void setNextButton(Button nextButton){
        grid.add(nextButton, 1, numFields + fieldGridOffset);
    }
    public void setDoneButton(Button doneButton){
        grid.add(doneButton, 0, numFields + fieldGridOffset + 1);
    }
    public void setPageNumber(int pageNumber){
        grid.add(new Text("Page " + (pageNumber + 1)), 1, numFields + fieldGridOffset + 1);
    }


}
