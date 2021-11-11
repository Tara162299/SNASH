package com.snash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.snash.Metadata.MetadataField;

public class OutputFile {

    /**private File file;
    private int headerOffset;
    private byte[] header;*/
    private final Metadata metadata;
    private int fileNumber;

    private final String date;
    private final String time;
    private String timeZone = "TIMEZONES-NOT-IMPLEMENTED";

    // Create a new OutputFile with the given metadata, and the file number 0.
    // Special fields are calculated at time of OutputFile creation!
    public OutputFile(Metadata metadata){
        this.metadata = metadata;
        fileNumber = 0;
        date = new SimpleDateFormat("dd-MM-yy").format(new Date());
        time = new SimpleDateFormat("HH:mm").format(new Date());
        initializeSpecialValues();
    }

    private OutputFile(Metadata metadata, int fileNumber){
        this(metadata);
        this.fileNumber = fileNumber;
    }

    //stackabuse.com/how-to-get-current-date-and-time-in-java/
    private void initializeSpecialValues(){
        //TODO: Time Zones
    }

/** WIP: Still need to figure out how exactly RIFF bytes work, particularly chunks and spacing.
 *  private List<String> header(){
        List<String> headerStrings = new ArrayList<>();
        for (MetadataField field : metadata){
            String fieldTag = InfoTagList.stringToInfoTag(field.getName());
            output.add(fieldTag);
        }
    }*/

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
}
