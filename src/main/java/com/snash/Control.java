package com.snash;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Control {

    public static void main(String[] args) {
        MTableUI table = new MTableUI();
        table.runUI(args);
        // Not working yet- all of table's variables seem to be set to null after closing the stage.
        // Need to find a way to store data, close the stage, then have the data retrieved.
        String pathFile = table.getPath();
        System.out.println(pathFile);

    }
}
