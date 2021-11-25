package com.snash;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.snash.ConfigurationData.DataField;
import com.snash.ConfigurationData.ValueType;
import com.snash.ConfigurationData.SpecialValue;

public class Metadata implements Iterable<Metadata.MetadataField> {

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
