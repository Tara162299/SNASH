package com.snash;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class RecordingUI extends Group {

    private AudioRecording audioRecording;

    private Text recordingText;

    private Button returnButton;
    private Button restartButton;
    private Button stopButton;
    private Button startButton;

    public RecordingUI() {

        this.stopButton = new Button("Stop");
        this.getChildren().add(stopButton);
        this.stopButton.setOnAction(actionEvent -> {

        });

        this.returnButton = new Button("Return");
        this.getChildren().add(returnButton);
        this.returnButton.setOnAction(actionEvent -> {

        });

        this.restartButton = new Button("Restart");
        this.getChildren().add(restartButton);
        this.restartButton.setOnAction(actionEvent -> {

        });

        this.startButton = new Button("Start");
        this.getChildren().add(startButton);
        this.startButton.setOnAction(actionEvent -> {

        });
        this.recordingText = new Text(0, 20, "Recording...");
//        this.getChildren().add(recordingText);
    }

    public AudioRecording startRecording() {
        this.audioRecording = new AudioRecording(this);
        audioRecording.run();
        return audioRecording;
    }

    public void notifyFinished() {
        this.recordingText.setText("Recording Finished");
    }

    public void notifyRecording() {
        this.recordingText.setText("Recording in session");
    }

    }


