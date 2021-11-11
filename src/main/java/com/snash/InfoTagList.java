package com.snash;

public class InfoTagList {

    private static InfoTag[] tags = new InfoTag[]{
            new InfoTag("Artist", "IART"),
            new InfoTag("Genre", "GENR"),
            new InfoTag("Country", "ICNT"),
            new InfoTag("Comment", "CMNT"),
            new InfoTag("Title", "INAM"),
    };

    // Replaces the given text with its associated RIFF Info Tag if applicable.
    // exiftool.org/TagNames/RIFF.html#Info
    public static String stringToInfoTag(String text){
        for (InfoTag tag : tags){
            if (text.equals(tag.tagName)){
                return tag.tagID;
            }
        }
        return text;
    }

    private record InfoTag(String tagName, String tagID){}
}
