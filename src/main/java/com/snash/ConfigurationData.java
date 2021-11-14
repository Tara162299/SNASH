package com.snash;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigurationData {
    // TODO add upload configuration data

    public enum SpecialValue {
        Time,
        Date,
        Timezone
    }

    /**
     * @param name  The name of the tag attached to the file, i.e. "artist"
     * @param alias The alias to be displayed to the user, i.e. "callsign"
     * @param defaultValue  The default value, or null if there is none.
     * @param fixedValue    The predetermined value, or null if it should be user-defined.
     * @param specialValue  The type of special value, or null if it is not one of them.
     */
    public record DataField(String name, String alias,
                            String defaultValue, String fixedValue,
                            SpecialValue specialValue) {

        public boolean hasFixedValue() {
            return fixedValue != null;
        }
    }

    private final Map<String, DataField> metadataFields;
    private final Map<Integer, DataField> fileNameFields;


    // creates configuration data from a config file
    public ConfigurationData(File configFile) throws ParserConfigurationException, IOException, SAXException {
        // TODO handle incorrectly formatted xml files
        metadataFields = new HashMap<>();
        fileNameFields = new HashMap<>();

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(configFile);
        Element root = doc.getDocumentElement();

        NodeList nodeList = root.getElementsByTagName("metadata");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                DataField dataField = dataFieldFromElement((Element) node);

                if (dataField.name().startsWith("FileName")) {
                    int num = Integer.parseInt(dataField.name().replaceAll("\\D+",""));
                    fileNameFields.put(num, dataField);
                }
                else {
                    metadataFields.put(dataField.name(), dataField);
                }
            }
        }
    }

    private DataField dataFieldFromElement(Element element){
        // TODO handle missing unnecessary fields rather than requiring all
        String name = tagTextFromElement("name", element);
        String alias = tagTextFromElement("alias", element);
        String fixedValue = tagTextFromElement("fixedValue", element);
        String defaultValue = tagTextFromElement("defaultValue", element);
        SpecialValue specialValue = stringToSpecialValue(tagTextFromElement("specialValue", element));

        Objects.requireNonNull(name, "Every metadata tag must have a true name.");
        return new DataField(name, alias, defaultValue, fixedValue, specialValue);
    }

    private String tagTextFromElement(String tag, Element element){
        NodeList tags = element.getElementsByTagName(tag);
        if (tags.getLength() > 0) {
            return tags.item(0).getTextContent();
        } else {
            return null;
        }
    }

    public Collection<DataField> getMetadataFields(){
        return metadataFields.values();
    }

    // Parses a special value from a string. Returns null if the string is null or invalid.
    private SpecialValue stringToSpecialValue(String string) {
        if (string == null){
            return null;
        }

        String str = string.toLowerCase();
        return switch (str) {
            case "time" -> SpecialValue.Time;
            case "date" -> SpecialValue.Date;
            case "timezone", "time zone" -> SpecialValue.Timezone;
            default -> null;
        };
    }
}