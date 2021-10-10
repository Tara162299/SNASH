package com.snash;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.control.TextArea;

public class RecordingUI extends Group {

    private AudioRecording audioRecording;

    private Text recordingText;
    private Text stopText;

    private Button returnButton;
    private Button restartButton;
    private Button stopButton;
    private Button startButton;
    private GridPane grid;



    public RecordingUI() {

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        stopButton = new Button("Stop");
        stopButton.setOnAction(actionEvent -> {
            RecordingUI recordingUI = new RecordingUI();
            this.getScene().setRoot(recordingUI);
            recordingUI.stopRecording();

        });

        returnButton = new Button("Return");
        returnButton.setOnAction(actionEvent -> {

        });

        restartButton = new Button("Restart");
        restartButton.setOnAction(actionEvent -> {

        });

        startButton = new Button("Start");
        startButton.setOnAction(actionEvent -> {
            RecordingUI recordingUI = new RecordingUI();
            this.getScene().setRoot(recordingUI);
            recordingUI.startRecording();
        });

        grid.add(startButton, 0, 1);
        grid.add(stopButton, 1, 1);
        grid.add(returnButton, 3, 1);
        grid.add(restartButton, 4, 1);
        this.getChildren().add(grid);


        this.recordingText = new Text(0, 20, "Recording...");
//        this.getChildren().add(recordingText);
    }

    public AudioRecording startRecording() {
        this.audioRecording = new AudioRecording(this);
        audioRecording.run();
        return audioRecording;
    }

    public AudioRecording stopRecording() {
        this.audioRecording = new AudioRecording(this);
        audioRecording.stop();
        this.stopText = new Text(10, 20, "Record stopped");
        this.getChildren().add(stopText);
        return audioRecording;
    }

    public void notifyRecording() {
        this.recordingText.setText("Recording in session");
    }

    }


