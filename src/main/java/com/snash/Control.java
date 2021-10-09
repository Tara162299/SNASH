package com.snash;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class Control extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws ParserConfigurationException, IOException, SAXException {
        ConfigurationData config = new ConfigurationData(new File("C:\\Users\\Daniel\\Desktop\\config.xml"));
        stage.setTitle("Enter Metadata");
        stage.setScene(new Scene(new MTableUI()));
        stage.show();
        // System.out.println(tableScene.getFilePath());
    }
}
