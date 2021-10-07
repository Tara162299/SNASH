package com.snash;

public class Metadata {

    // The true names of each metadata field- i.e. "artist", "album", "genre", etc.
    private String[] trueNames;
    // The aliased (displayed) name of each field- i.e. "callsign", "event", etc.
    private String[] aliases;
    // The value of each metadata field.
    private String[] values = null;
    // The location to save audio data to. A new folder will be created in this location.
    private String filePath = null;

    public Metadata(String[] trueNames, String[] aliases){
        assert trueNames.length == aliases.length;
        this.trueNames = trueNames;
        this.aliases = aliases;
        this.values = new String[trueNames.length];
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public String getFilePath(){
        return filePath;
    }

    // Sets the value of the field with the given true name. Throws an error if the true name is not present.
    public void setValueOfTrueName(String value, String trueName){
        values[positionOfTrueName(trueName)] = value;
    }

    // Sets the value of the field with the given alias. Throws an error if the alias is not present.
    public void setValueOfAlias(String value, String alias){
        values[positionOfAlias(alias)] = value;
    }

    private int positionOfTrueName(String trueName){
        for (int i = 0; i < trueNames.length; i++){
            if (trueNames[i].equals(trueName)){
                return i;
            }
        }
        return -1;
    }

    private int positionOfAlias(String alias){
        for (int i = 0; i < aliases.length; i++){
            if (aliases[i].equals(alias)){
                return i;
            }
        }
        return -1;
    }
}
