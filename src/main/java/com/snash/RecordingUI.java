package com.snash;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class RecordingUI extends Group {

    private AudioRecording audioRecording;
    private final Metadata metadata;

    private Text recordingText;
    private Text stopText;

    private Button doneButton;
    private Button restartButton;
    private Button stopButton;
    private Button startButton;
    private Button pauseButton;
    private GridPane grid;

    public RecordingUI(Metadata metadata) {

        this.metadata = metadata;

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        stopButton = new Button("Stop");
        stopButton.setStyle("-fx-background-color: #940505; ");
        stopButton.setOnAction(actionEvent -> {
            RecordingUI recordingUI = new RecordingUI(metadata);
            this.getScene().setRoot(recordingUI);
            recordingUI.stopRecording();

        });

        doneButton = new Button("Done!");
        doneButton.setOnAction(actionEvent -> {
            // should take us to uploading 
        });

        pauseButton = new Button("Pause");
        pauseButton.setOnAction(actionEvent -> {

        });

        restartButton = new Button("Restart");
        restartButton.setOnAction(actionEvent -> {

        });

        startButton = new Button("Start");
        startButton.setStyle("-fx-background-color: #02730d; ");
        startButton.setOnAction(actionEvent -> {
            RecordingUI recordingUI = new RecordingUI(metadata);
            this.getScene().setRoot(recordingUI);
            recordingUI.startRecording();
        });

        grid.add(startButton, 0, 1);
        grid.add(pauseButton, 1, 1);
        grid.add(restartButton, 3, 1);
        grid.add(stopButton, 4, 1);
        grid.add(doneButton, 5, 1);
        this.getChildren().add(grid);


        this.recordingText = new Text(0, 20, "Recording...");
//        this.getChildren().add(recordingText);
    }

    public AudioRecording startRecording() {
        this.audioRecording = new AudioRecording(this, metadata);
        audioRecording.start();
        return audioRecording;
    }

    public void stopRecording() {
        audioRecording.requestStop();
        this.stopText = new Text(10, 20, "Record stopped");
        this.getChildren().add(stopText);

    }
}
