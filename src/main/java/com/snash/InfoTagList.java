package com.snash;

import java.util.Locale;

public class InfoTagList {

    private record InfoTag(String tagName, String tagID){}

    private static final InfoTag[] TAGS = new InfoTag[]{

            // Most useful tags:
            new InfoTag("Artist", "IART"),
            new InfoTag("Comment", "ICMT"),
            new InfoTag("Comments", "COMM"),
            new InfoTag("Country", "ICNT"),
            new InfoTag("DateCreated", "ICRD"),
            new InfoTag("Directory", "DIRC"),
            new InfoTag("Keywords", "IKEY"),
            new InfoTag("Location", "LOCA"),
            new InfoTag("Software", "ISFT"),
            new InfoTag("Subject", "ISBJ"),
            new InfoTag("Title", "INAM"),
            new InfoTag("Part", "PRT1"),
            new InfoTag("URL", "TURL"),
            new InfoTag("Version", "TVER"),
            new InfoTag("Year", "YEAR"),

            // Possibly useful tags:
            new InfoTag("ArchivalLocation", "IARL"),
            new InfoTag("Commissioned", "ICMS"),
            new InfoTag("Copyright", "ICOP"),
            new InfoTag("DateTimeOriginal", "IDIT"),
            new InfoTag("Genre", "IGNR"),
            new InfoTag("Language", "ILNG"),
            new InfoTag("Length", "TLEN"),
            new InfoTag("NumberOfParts", "PRT2"),
            new InfoTag("Organization", "TORG"),
            new InfoTag("Rate", "RATE"),
            new InfoTag("Statistics", "STAT"),
            new InfoTag("TrackNumber", "TRCK"),

            // Least useful tags:
            new InfoTag("BaseURL", "IBSU"),
            new InfoTag("Cinematographer", "ICNM"),
            new InfoTag("CostumeDesigner", "ICDS"),
            new InfoTag("Cropped", "ICRP"),
            new InfoTag("Dimensions", "IDIM"),
            new InfoTag("DistributedBy", "IDST"),
            new InfoTag("DotsPerInch", "IDPI"),
            new InfoTag("EditedBy", "IEDT"),
            new InfoTag("EncodedBy", "IENC"),
            new InfoTag("EndTimecode", "TCDO"),
            new InfoTag("Engineer", "IENG"),
            new InfoTag("Lightness", "ILGT"),
            new InfoTag("LogoURL", "ILGU"),
            new InfoTag("LogoIconURL", "ILIU"),
            new InfoTag("MoreInfoBannerImage", "IMBI"),
            new InfoTag("MoreInfoBannerURL", "IMBU"),
            new InfoTag("MoreInfoText", "IMIT"),
            new InfoTag("MoreInfoURL", "IMIU"),
            new InfoTag("MusicBy", "IMUS"),
            new InfoTag("Medium", "IMED"),
            new InfoTag("NumColors", "IPLT"),
            new InfoTag("ProducedBy", "IPRO"),
            new InfoTag("Product", "IPRD"),
            new InfoTag("ProductionDesigner", "IPDS"),
            new InfoTag("ProductionStudio", "ISTD"),
            new InfoTag("Rated", "AGES"),
            new InfoTag("Rating", "IRTD"),
            new InfoTag("RippedBy", "IRIP"),
            new InfoTag("SecondaryGenre", "ISGN"),
            new InfoTag("Sharpness", "ISHP"),
            new InfoTag("SoundSchemeTitle", "DISP"),
            new InfoTag("Source", "ISRC"),
            new InfoTag("SourceForm", "ISRF"),
            new InfoTag("Starring", ""),
            new InfoTag("StartTimecode", "TCOD"),
            new InfoTag("TapeName", "TAPE"),
            new InfoTag("Technician", "ITCH"),
            new InfoTag("TimeCode", "ISMP"),
            new InfoTag("TrackNumber", "ITRK"),
            new InfoTag("VegasVersionMajor", "VMAJ"),
            new InfoTag("VegasVersionMinor", "VMIN"),
            new InfoTag("WatermarkURL", "IWMU"),
            new InfoTag("WrittenBy", "IWRI"),

            new InfoTag("FirstLanguage", "IAS1"),
            new InfoTag("First Language", "IAS1"),
            new InfoTag("SecondLanguage", "IAS2"),
            new InfoTag("Second Language", "IAS2"),
            new InfoTag("ThirdLanguage", "IAS3"),
            new InfoTag("Third Language", "IAS3"),
            new InfoTag("FourthLanguage", "IAS4"),
            new InfoTag("Fourth Language", "IAS4"),
            new InfoTag("FifthLanguage", "IAS5"),
            new InfoTag("Fifth Language", "IAS5"),
            new InfoTag("SixthLanguage", "IAS6"),
            new InfoTag("Sixth Language", "IAS6"),
            new InfoTag("SeventhLanguage", "IAS7"),
            new InfoTag("Seventh Language", "IAS7"),
            new InfoTag("EighthLanguage", "IAS8"),
            new InfoTag("Eighth Language", "IAS8"),
            new InfoTag("NinthLanguage", "IAS9"),
            new InfoTag("Ninth Language", "IAS9")
    };

    // Replaces the given text with its associated RIFF Info Tag if applicable.
    // exiftool.org/TagNames/RIFF.html#Info
    public static String stringToInfoTag(String text){
        // Remove spaces from input and convert to lowercase.
        String inputText = text.toLowerCase(Locale.ROOT).replaceAll("\\s", "");
        for (InfoTag tag : TAGS){
            String tagText = tag.tagName.toLowerCase(Locale.ROOT);
            if (inputText.equals(tagText)){
                return tag.tagID;
            }
        }
        return text;
    }

}
