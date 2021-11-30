package com.snash;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioRecording extends Thread {

    // flag to indicate that the recorder is requested to be stopped
    private boolean stopRequest = false;
    private boolean isRecording = false;

    // path of the wav file
    private final File wavFile;

    // format of audio file
    private final AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line which audio data is captured
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

    public File getWaveFile() {
        return wavFile;
    }

    // Defines an audio format
    private AudioFormat audioFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 1;
        return new AudioFormat(sampleRate, sampleSizeInBits,
                channels, true, false);
    }

    // Method to stop recording when user hit "Stop" button
    public void requestStop() {
        stopRequest = true;
        this.stopRecording();
    }

    private void startCapture() {
        isRecording = true;

        try {
            AudioFormat format = audioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line is not supported!");
                System.exit(0);
            }

            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            // start capturing
            line.start();

            System.out.println("Start capturing...");

            isRecording = true;

            // TODO: consider buffer size
            byte[] buffer = new byte[line.getBufferSize() / 5];

            OutputFile outputFile = OutputFile.from(metadata, audioFormat());

            while (!this.stopRequest) {
                line.read(buffer, 0, buffer.length);
                if (!outputFile.appendAudioData(buffer)) {
                    outputFile.close();
                    outputFile = outputFile.nextFile();
                }
            }
            outputFile.close();
        } catch (LineUnavailableException | IOException ex) {
            ex.printStackTrace();
        }
    }

    // Closes the target data line to finish capturing and recording
    private void finish() {
        isRecording = false;
        line.stop();
        line.flush();
        line.close();
        System.out.println("Finished!");
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
        this.startCapture();

    }
}
