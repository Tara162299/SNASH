package com.snash;

import javax.sound.sampled.*;
import java.io.*;

        //Define an audio format of the sound source to be captured, using the class AudioFormat.
        //Create a DataLine.Info object to hold information of a data line.
        //Obtain a TargetDataLine object which represents an input data line from which audio data can be captured, using the method getLineInfo(DataLine.Info) of the AudioSystem class.
        //Open and start the target data line to begin capturing audio data.
        //Create an AudioInputStream object to read data from the target data line.


public class AudioRecording implements Runnable{
    // record duration, in milliseconds
    static final long RECORD_TIME = 4000;  // 4 seconds

    Metadata metadata;

    File wavFile;

    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    TargetDataLine line;

    // the UI that created this
    RecordingUI recordingUI;

    public AudioRecording(RecordingUI recordingUI, Metadata metadata) {
        this.recordingUI = recordingUI;
        this.metadata = metadata;
    }

    // Defines an audio format
    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        return new AudioFormat(sampleRate, sampleSizeInBits,
                channels, true, true);
    }

    // Captures the sound and record into a WAV file

    void start() {
        try {
            wavFile = new File(metadata.getFilePath() + "tempfilename.wav");
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing

            System.out.println("Start capturing...");

            AudioInputStream ais = new AudioInputStream(line);

            System.out.println("Start recording...");

            // start recording
            AudioSystem.write(ais, fileType, wavFile);

        } catch (LineUnavailableException | IOException ex) {
            ex.printStackTrace();
        }
    }

    // Closes the target data line to finish capturing and recording

    void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
        this.recordingUI.notifyFinished();
    }

    // Entry to run the program
//
//    public static void main(String[] args) {
//       AudioRecording test = new AudioRecording();
//       test.run();
//    }

    @Override
    public void run() {
        // creates a new thread that waits for a specified
        // of time before stopping
        Thread stopper = new Thread(() -> {
            try {
                Thread.sleep(RECORD_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            this.finish();
        });

        stopper.start();
        this.start();
    }
}