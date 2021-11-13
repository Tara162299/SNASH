package com.snash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.*;

import com.snash.Metadata.MetadataField;

public class OutputFile {

    /**private File file;
    private int headerOffset;
    private byte[] header;*/
    private final Metadata metadata;
    private final int fileNumber;

    private final String date;
    private final String time;
    private final String timeZone;

    private byte[] listChunkBytes;

    // Create a new OutputFile with the given metadata, and the file number 0.
    // Special fields are calculated at time of OutputFile creation!
    public OutputFile from(Metadata metadata){
        return new OutputFile(metadata, 0);
    }

    private OutputFile(Metadata metadata, int fileNumber){
        this.metadata = metadata;
        this.fileNumber = fileNumber;

        //stackabuse.com/how-to-get-current-date-and-time-in-java/
        date = new SimpleDateFormat("dd-MM-yy").format(new Date());
        time = new SimpleDateFormat("HH:mm").format(new Date());
        //TODO: Finding this from the internet vs the operating system would be nice.
        timeZone = TimeZone.getDefault().getDisplayName();

        ListChunk chunk = new ListChunk();
        for (MetadataField field : metadata){
            chunk.addTag(field.getName(), field.getValue());
        }
        listChunkBytes = chunk.byteArray();
    }

    // Create a new OutputFile with the current metadata, and the next file number.
    public OutputFile nextFile(){
        return new OutputFile(metadata, fileNumber + 1);
    }

    public String fileName(){
        StringBuilder output = new StringBuilder();
        output.append("SNASH_");

        List<MetadataField> specialFields = metadata.specialFields();

        for (MetadataField field : specialFields){
            switch (field.getSpecialType()){
                case Date :
                    output.append(date);
                case Time:
                    output.append(time);
                case Timezone:
                    output.append(timeZone);
                default:
                    break;
            }
            output.append('_');
        }
        output.append(fileNumber);
        return output.toString();
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
            if (name.length() != 4) { throw new IllegalArgumentException("INFO tags must be of length 4."); }
            if (value.length() > 50) { throw new IllegalArgumentException("Metadata values cannot exceed length 50."); }
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);

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
            System.arraycopy(size, 4, output, 4, 4);
            for (int i = 8; i < subChunks.size() + 8; i++){ output[i] = subChunks.get(i); }
            return output;
        }

        // Returns a 4 byte chunk representing the size of the LIST chunk minus eight.
        // Size should never really be above 20000 at the most, but this should work if it is.
        private byte[] sizeChunk(int size){
            return new byte[] { (byte) (size / 16777216), (byte) (size / 65536), (byte) (size / 256), (byte) (size % 256) };
        }
    }
}
