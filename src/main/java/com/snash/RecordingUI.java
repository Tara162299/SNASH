package com.snash;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class RecordingUI extends Group implements Runnable  {

    private AudioRecording audioRecording;

    public RecordingUI() {
    }

    @Override
    public void run() {

    }

    public AudioRecording startRecording() {
        this.audioRecording = new AudioRecording(this);
        audioRecording.run();
        return audioRecording;
    }

    public void notifyFinished() {

    }
}
