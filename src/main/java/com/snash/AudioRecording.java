package com.snash;

import javax.sound.sampled.*;
import java.io.*;

//Define an audio format of the sound source to be captured, using the class AudioFormat.
//Create a DataLine.Info object to hold information of a data line.
//Obtain a TargetDataLine object which represents an input data line from which audio data can be captured, using the method getLineInfo(DataLine.Info) of the AudioSystem class.
//Open and start the target data line to begin capturing audio data.
//Create an AudioInputStream object to read data from the target data line.


public class AudioRecording extends Thread {
    // record duration, in milliseconds
    static final long RECORD_TIME = 10000;  // 4 seconds

    // flag to indicate that the recorder is requested to be stopped
    private boolean stopRequest = false;
    private boolean isRecording = false;

    // path of the wav file
    private final File wavFile;

    // format of audio file
    private final AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    private TargetDataLine line;

    // the UI that created this
    private final RecordingUI recordingUI;

    // the metadata to be added to the wav file
    private final Metadata metadata;

    public AudioRecording(RecordingUI recordingUI, Metadata metadata) {
        this.recordingUI = recordingUI;
        this.metadata = metadata;
        wavFile = new File(metadata.getFilePath() + "\\Test.wav");
    }

    // Defines an audio format
    private AudioFormat audioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 1;
        return new AudioFormat(sampleRate, sampleSizeInBits,
                channels, true, true);
    }

    // Captures the sound and record into a WAV file

    public void requestStop() {
        stopRequest = true;
        this.interrupt();
    }

    private void startCapture() {
        isRecording = true;

        try {
            AudioFormat format = audioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);

            line.open(format);

            line.start();  // start capturing

            System.out.println("Start capturing...");

            isRecording = true;

            // TODO: consider buffer size
            byte[] buffer = new byte[line.getBufferSize()];
            while (!this.stopRequest) {
                line.read(buffer, 0, buffer.length);
                // TODO: complete writing, this is attempted code that needs WritableInputStream custom object (created
                //       in my local but not on origin)
                // WritableInputStream inputStream = new WritableInputStream(buffer);
                // AudioInputStream ais = new AudioInputStream(inputStream, format, inputStream.available());
                // AudioSystem.write(ais, fileType, wavFile);
            }

            isRecording = false;

        } catch (LineUnavailableException | IOException ex) {
            ex.printStackTrace();
        }
    }

    // Closes the target data line to finish capturing and recording

    private void finish() {
        line.stop();
        line.flush();
        line.close();
        System.out.println("Finished");
    }

    private void stopRecording() {
        if (isRecording) {
            isRecording = false;
            this.finish();
        } else {
            System.out.println("There is no record to be stopped");
        }
    }

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

            this.requestStop();
        });
        // stopper.start();
        this.startCapture();

    }
}
