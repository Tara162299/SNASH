package com.snash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.*;

import com.snash.Metadata.MetadataField;

import javax.sound.sampled.AudioFormat;

public class OutputFile {

    static final int HEADER_LENGTH_NO_INFO = 44;
    static final long MAX_FILE_SIZE = 4294967295L;
    /**private File file;
    private int headerOffset;
    private byte[] header;*/
    private final Metadata metadata;
    private final AudioFormat audioFormat;
    private final int fileNumber;

    private final String date;
    private final String time;
    private final String timeZone;

    private final File file;

    private long fileSizeOffset;
    private long dataSizeOffset;

    private long fileSize;
    private long dataSize;

    private byte[] listChunkBytes;

    // Create a new OutputFile with the given metadata, and the file number 0.
    // Special fields are calculated at time of OutputFile creation!
    public static OutputFile from(Metadata metadata, AudioFormat audioFormat) throws IOException {
        return new OutputFile(metadata, audioFormat, 0);
    }

    private OutputFile(Metadata metadata, AudioFormat audioFormat, int fileNumber) throws IOException {
        this.metadata = metadata;
        this.audioFormat = audioFormat;
        this.fileNumber = fileNumber;

        //stackabuse.com/how-to-get-current-date-and-time-in-java/
        date = new SimpleDateFormat("dd-MM-yy").format(new Date());
        time = new SimpleDateFormat("HH-mm").format(new Date());
        //TODO: Finding this from the internet vs the operating system would be nice.
        timeZone = TimeZone.getDefault().getDisplayName();

        ListChunk chunk = new ListChunk();
        for (MetadataField field : metadata){
            System.out.println(field.toString());
            String value = field.getValue();
            if (field.getValueType() == ConfigurationData.ValueType.SPECIAL){
                value = specialTypeToString(field.getSpecialType());
            }
            chunk.addTag(field.getName(), value);
        }
        listChunkBytes = chunk.byteArray();

        byte[] fileBytes = new byte[HEADER_LENGTH_NO_INFO + listChunkBytes.length];

        // see http://soundfile.sapp.org/doc/WaveFormat/ for WAVE spec
        // create riff header
        fileBytes[0] = (byte) 'R';
        fileBytes[1] = (byte) 'I';
        fileBytes[2] = (byte) 'F';
        fileBytes[3] = (byte) 'F';

        fileSize = 44 + listChunkBytes.length;
        System.arraycopy(longToByteArray(fileSize), 0, fileBytes, 4, 4);
        fileSizeOffset = 4;

        fileBytes[8] = (byte) 'W';
        fileBytes[9] = (byte) 'A';
        fileBytes[10] = (byte) 'V';
        fileBytes[11] = (byte) 'E';

        // add info chunk
        System.arraycopy(listChunkBytes, 0, fileBytes, 12, listChunkBytes.length);

        // add "fmt " sub-chunk
        int off = listChunkBytes.length;
        fileBytes[12 + off] = (byte) 'f';
        fileBytes[13 + off] = (byte) 'm';
        fileBytes[14 + off] = (byte) 't';
        fileBytes[15 + off] = (byte) ' ';

        System.arraycopy(longToByteArray(16), 0, fileBytes, 16 + off, 4);

        System.arraycopy(longToByteArray(1), 0, fileBytes, 20 + off, 2);

        System.arraycopy(longToByteArray(audioFormat.getChannels()), 0, fileBytes, 22 + off, 2);

        System.arraycopy(longToByteArray((long) audioFormat.getSampleRate()), 0, fileBytes, 24 + off, 4);

        // ByteRate
        System.arraycopy(longToByteArray(audioFormat.getFrameSize() * (long) audioFormat.getFrameRate()), 0, fileBytes, 28 + off, 4);

        // BlockAlign
        System.arraycopy(longToByteArray(audioFormat.getFrameSize()), 0, fileBytes, 32 + off, 2);

        // BitsPerSample
        System.arraycopy(longToByteArray(audioFormat.getSampleSizeInBits()), 0, fileBytes, 34 + off, 2);

        // add "data" sub-chunk
        fileBytes[36 + off] = (byte) 'd';
        fileBytes[37 + off] = (byte) 'a';
        fileBytes[38 + off] = (byte) 't';
        fileBytes[39 + off] = (byte) 'a';

        // size is 0 at this point, so it doesn't need to be set
        dataSize = 0;
        dataSizeOffset = 40 + off;

        // write file to disk
        file = new File(metadata.getFilePath() + "\\" + fileName());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(fileBytes);
        fileOutputStream.close();
    }

    // Create a new OutputFile with the current metadata, and the next file number.
    public OutputFile nextFile() throws IOException {
        return new OutputFile(metadata, audioFormat, fileNumber + 1);
    }

    public String fileName(){
        StringBuilder output = new StringBuilder();
        output.append("SNASH_");

        List<MetadataField> specialFields = metadata.specialFields();

        for (MetadataField field : specialFields){
            output.append(specialTypeToString(field.getSpecialType()));
            output.append('_');
        }
        output.append(fileNumber);
        output.append(".wav");
        return output.toString();
    }

    private String specialTypeToString(ConfigurationData.SpecialValue specialType){
        return switch (specialType) {
            case Date -> date;
            case Time -> time;
            case Timezone -> timeZone;
        };
    }

    public boolean appendAudioData(byte[] data) throws IOException {
        if (!canAppendAudioData(data)) {
            return false;
        }
        fileSize += data.length;
        dataSize += data.length;

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

        randomAccessFile.seek(fileSizeOffset);
        randomAccessFile.write(longToByteArray(fileSize), 0, 4);

        randomAccessFile.seek(dataSizeOffset);
        randomAccessFile.write(longToByteArray(dataSize), 0, 4);

        randomAccessFile.seek(randomAccessFile.length());
        randomAccessFile.write(data);

        randomAccessFile.close();

        return true;
    }

    public boolean canAppendAudioData(byte[] data) {
        return fileSize + data.length <= MAX_FILE_SIZE;
    }

    static byte[] stringToByteArray(String text){
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        for (char c : text.toCharArray()) {
            byteBuffer.putChar(c);
        }
        return byteBuffer.array();
    }

    static byte[] longToByteArray(long value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return  byteBuffer.putLong(value).array();
    }

    static long byteArrayToLong(byte[] array) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return byteBuffer.put(array).getLong(0);
    }

    public static class ListChunk {
        private static final byte[] LIST_CHUNK_ID = new byte[]{0x4C, 0x49, 0x53, 0x54};
        private byte[] size = new byte[4];

        private final List<Byte> subChunks = new ArrayList<>();

        public ListChunk(){
            subChunks.add((byte) 'I');
            subChunks.add((byte) 'N');
            subChunks.add((byte) 'F');
            subChunks.add((byte) 'O');
        }

        // Add a metadata field and its value to the chunk.
        // The name MUST be of length 4.
        // TODO: Can the tag name and value be any ASCII character, or just alphanumeric, or just letters?
        public void addTag(String name, String value) {
            validateTagInputs(name, value);

            List<Byte> tagBytes = new ArrayList<>(stringToByteList(name));

            // Add value's size as four bytes, little endian.
            // Value should never of length more than 50, but it can be up to 255 before this breaks.
            tagBytes.add((byte) value.length());
            for (int i = 0; i < 3; i++){
                tagBytes.add((byte) 0);
            }

            tagBytes.addAll(stringToByteList(value));
            subChunks.addAll(tagBytes);
        }

        private void validateTagInputs(String name, String value){
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);
            if (name.length() != 4) { throw new IllegalArgumentException("INFO tags must be of length 4."); }
            if (value.length() > 50) { throw new IllegalArgumentException("Metadata values cannot exceed length 50."); }
        }
        private List<Byte> stringToByteList(String string) {
            assert string.length() == 4;
            List<Byte> output = new ArrayList<>();
            for (char c : string.toCharArray()) {
                output.add((byte) c);
            }
            return output;
        }

        public byte[] byteArray(){
            size = sizeChunk(subChunks.size());

            byte[] output = new byte[subChunks.size() + 8];
            System.arraycopy(LIST_CHUNK_ID, 0, output, 0, 4);
            System.arraycopy(size, 0, output, 4, 4);
            for (int i = 0; i < subChunks.size(); i++){ output[i + 8] = subChunks.get(i); }
            return output;
        }

        // Returns a 4 byte chunk representing the size of the LIST chunk minus eight.
        // Size should never really be above 20000 at the most, but this should work if it is.
        private byte[] sizeChunk(int size){
            return new byte[] { (byte) (size % 256), (byte) (size / 256), (byte) (size / 65536), (byte) (size / 16777216)  };
        }
    }
}
