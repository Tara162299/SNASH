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
        private final ValueType valueType;
        private final SpecialValue specialType;
        private String value;

        public MetadataField(DataField dataField) {
            name = InfoTagList.stringToInfoTag(dataField.name());

            if (dataField.alias() != null){
                alias = dataField.alias();
            } else {
                alias = name;
            }

            valueType = dataField.valueType();
            specialType = dataField.specialValue();
            value = dataField.value();
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

        public SpecialValue getSpecialType() { return specialType; }
        public ValueType getValueType() { return valueType; }

        @Override
        public String toString(){
            return "n: " + name + " a: " + alias + " v: " + value;
        }
    }

    private final List<MetadataField> metadataFields;
    private String filePath = null;

    public Metadata(ConfigurationData configData){
        metadataFields = new ArrayList<>();
        for (DataField configField : configData.getMetadataFields()){
            metadataFields.add(new MetadataField(configField));
        }
    }

    public List<MetadataField> displayFields(){
        List<MetadataField> outputList = new ArrayList<>();
        for (MetadataField field : metadataFields) {
            if (field.valueType == ValueType.NORMAL){
                outputList.add(field);
            }
        }
        return outputList;
    }

    public List<MetadataField> specialFields(){
        List<MetadataField> outputList = new ArrayList<>();
        for (MetadataField field : metadataFields) {
            if (field.valueType == ValueType.SPECIAL){
                outputList.add(field);
            }
        }
        return outputList;
    }

    public int length(){
        return metadataFields.size();
    }

    public MetadataField get(int i){
        return metadataFields.get(i);
    }

    public List<MetadataField> getMetadataFields(){
        return new ArrayList<>(metadataFields);
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
