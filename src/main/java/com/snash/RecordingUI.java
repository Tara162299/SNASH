package com.snash;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class RecordingUI extends Group {

    private AudioRecording audioRecording;

    private Text recordingText;

    public RecordingUI() {

        Button stopRecordingButton = new Button("Stop");
        stopRecordingButton.setOnAction(actionEvent -> {
            //((MTableUI) getParent()).submit();
            //this.
        });

        Button returnButton = new Button("Return");
        returnButton.setOnAction(actionEvent -> {
            //((MTableUI) getParent()).submit();
        });

        Button restartButton = new Button("restart Recording");
        returnButton.setOnAction(actionEvent -> {
            //((MTableUI) getParent()).submit();
        });






        this.recordingText = new Text(0, 20, "Recording...");
        this.getChildren().add(recordingText);
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


