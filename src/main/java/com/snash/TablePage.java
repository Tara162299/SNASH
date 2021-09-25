package com.snash;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class TablePage {

    // How many "generic fields" should be on the page- i.e. not hardcoded by the devs.
    private static final int numFields = 9;
    // How many hardcoded lines come before the field grid, i.e. buttons or filepath field.
    private static final int fieldGridOffset = 1;

    private GridPane grid;
    ArrayList<TextField> fieldNames = new ArrayList<>();
    ArrayList<TextField> fieldValues = new ArrayList<>();

    // Buttons are passed as parameters, as they interact with MTableUI code and can't be initialized here.
    public TablePage(Button previousButton, Button nextButton, Button doneButton){
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        Text pathText = new Text("Save Location");
        TextField pathField = new TextField();
        grid.add(pathText, 0, 0);
        grid.add(pathField, 1, 0);

        for(int i = 0; i < numFields; i++){
            fieldNames.add(new TextField());
            fieldValues.add(new TextField());
            grid.add(fieldNames.get(i), 0, i + fieldGridOffset);
            grid.add(fieldValues.get(i), 1, i + fieldGridOffset);
        }

        grid.add(previousButton, 0, numFields + fieldGridOffset);
        grid.add(nextButton, 1, numFields + fieldGridOffset);
        grid.add(doneButton, 0, numFields + fieldGridOffset + 1);
    }

    public GridPane getGrid() { return grid; }
}
