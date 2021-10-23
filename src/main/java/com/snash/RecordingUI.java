package com.snash;

import javafx.scene.Group;
import javafx.scene.text.Text;

public class RecordingUI extends Group {

    private AudioRecording audioRecording;

    private final Text recordingText;

    private final Metadata metadata;

    public RecordingUI(Metadata metadata) {
        this.metadata = metadata;
        this.recordingText = new Text(0, 20, "Recording...");
        this.getChildren().add(recordingText);
    }

    public AudioRecording startRecording() {
        this.audioRecording = new AudioRecording(this, metadata);
        audioRecording.run();
        return audioRecording;
    }

    public void notifyFinished() {
        this.recordingText.setText("Recording Finished");
    }

}
