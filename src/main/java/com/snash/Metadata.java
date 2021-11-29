package com.snash;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.snash.ConfigurationData.DataField;
import com.snash.ConfigurationData.ValueType;
import com.snash.ConfigurationData.SpecialValue;

public class Metadata implements Iterable<Metadata.MetadataField> {

    private static final String CHARACTER_INVALID_WARNING = " has the invalid character ";
    private static final String FIELD_EMPTY_WARNING = " has been left blank.";
    private static final String FIELD_TOO_LONG_WARNING = " has more than 50 characters.";

    public static class MetadataField {
        private final String name;
        private final String alias;
        private String value;
        private final boolean isMetadata;
        private final boolean isFilename;
        private final ValueType valueType;
        private final SpecialValue specialType;

        public MetadataField(DataField dataField) {
            name = dataField.name();

            if (dataField.alias() != null){
                alias = dataField.alias();
            } else {
                alias = name;
            }
            isFilename = dataField.isFilename();
            isMetadata = dataField.isMetadata();

            value = dataField.value();
            valueType = dataField.valueType();
            specialType = dataField.specialValue();

        }

        public void setValue(String value){
            if(valueType == ValueType.NORMAL){
                this.value = value;
            }
        }

        public String getName(){
            return name;
        }
        public String getAlias(){
            return alias;
        }
        public String getValue(){
            return value;
        }
        public boolean isMetadata(){ return isMetadata; }
        public boolean isFilename(){ return isFilename; }

        public SpecialValue getSpecialType() { return specialType; }
        public ValueType getValueType() { return valueType; }

        @Override
        public String toString(){
            return "n: " + name + " a: " + alias + " v: " + value;
        }
    }

    private final List<MetadataField> dataFields;
    private String filePath = null;

    public Metadata(ConfigurationData configData){
        dataFields = new ArrayList<>();
        for (DataField configField : configData.dataFields()){
            dataFields.add(new MetadataField(configField));
        }
    }

    public List<MetadataField> displayFields(){
        List<MetadataField> outputList = new ArrayList<>();
        for (MetadataField field : dataFields) {
            if (field.valueType == ValueType.NORMAL){
                outputList.add(field);
            }
        }
        return outputList;
    }

    public List<MetadataField> specialFields(){
        List<MetadataField> outputList = new ArrayList<>();
        for (MetadataField field : dataFields) {
            if (field.valueType == ValueType.SPECIAL){
                outputList.add(field);
            }
        }
        return outputList;
    }

    public int length(){
        return dataFields.size();
    }

    public MetadataField get(int i){
        return dataFields.get(i);
    }

    public List<MetadataField> getDataFields(){
        return new ArrayList<>(dataFields);
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public String getFilePath(){
        return filePath;
    }

    public String getWarning(){
        StringBuilder warningBuilder = new StringBuilder();
        for (MetadataField field : dataFields){
            String value = field.getValue();
            if (value == null || value.isEmpty()){
                if (field.getValueType() != ValueType.SPECIAL) {
                    warningBuilder.append(identifier(field)).append(FIELD_EMPTY_WARNING).append('\n');
                }
            } else if (value.length() > 50){
                warningBuilder.append(identifier(field)).append(FIELD_TOO_LONG_WARNING).append('\n');
            }
            List<String> illegalCharacterWarnings = illegalCharacterWarnings(field);
            for (String warning : illegalCharacterWarnings){
                warningBuilder.append(warning).append('\n');
            }
        }
        return warningBuilder.toString();
    }

    private List<String> illegalCharacterWarnings(MetadataField field){
        String name = field.getName();
        String alias = field.getAlias();
        String value = field.getValue();
        if (value == null || value.isEmpty()) { return new ArrayList<>(); }

        List<String> warnings = new ArrayList<>();
        for (char possiblyIllegalCharacter : value.toCharArray()){
            if (possiblyIllegalCharacter < 32 || possiblyIllegalCharacter > 126){
                warnings.add(identifier(field) + CHARACTER_INVALID_WARNING + possiblyIllegalCharacter);
            }
        }
        return warnings;
    }

    private String identifier(MetadataField field){
        String name = field.getName();
        String alias = field.getAlias();

        if (alias == null || alias.isEmpty()){
            if (name == null || name.isEmpty()){
                return "A field";
            } else {
                return "Field " + name;
            }
        } else {
            return "Field " + alias;
        }
    }


    @Override
    public Iterator<MetadataField> iterator() {
        return new MetadataIterator<>();
    }

    public class MetadataIterator<T> implements Iterator<T>{
        int currentPos = 0;

        @Override
        public boolean hasNext() {
            return Metadata.this.length() > currentPos;
        }

        @Override
        public T next() {
            T nextItem = (T) Metadata.this.get(currentPos);
            currentPos++;
            return nextItem;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
