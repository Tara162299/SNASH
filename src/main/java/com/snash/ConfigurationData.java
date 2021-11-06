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
        NodeList dataFields =  root.getElementsByTagName("metadata");
        for (int i = 0; i < dataFields.getLength(); i++) {
            Node field = dataFields.item(i);
            if (field.getNodeType() == Node.ELEMENT_NODE) {
                // TODO handle missing unnecessary fields rather than requiring all
                Element fieldElement = (Element) field;
                String name = null, alias = null, fixedValue = null, defaultValue = null;
                SpecialValue specialValue = null;
                NodeList tags = fieldElement.getElementsByTagName("name");
                if (tags.getLength() > 0) {
                    name = tags.item(0).getTextContent();
                }
                tags = fieldElement.getElementsByTagName("alias");
                if (tags.getLength() > 0) {
                    alias = tags.item(0).getTextContent();
                }
                tags = fieldElement.getElementsByTagName("fixedValue");
                if (tags.getLength() > 0) {
                    fixedValue = tags.item(0).getTextContent();
                }
                tags = fieldElement.getElementsByTagName("defaultValue");
                if (tags.getLength() > 0) {
                     defaultValue = tags.item(0).getTextContent();
                }
                tags = fieldElement.getElementsByTagName("specialValue");
                if (tags.getLength() > 0) {
                    specialValue = stringToSpecialValue(tags.item(0).getTextContent());
                }

                Objects.requireNonNull(name, "Every metadata tag must have a true name.");
                DataField newData = new DataField(name, alias, defaultValue, fixedValue, specialValue);

                if (name.startsWith("FileName")) {
                    int num = Integer.parseInt(name.replaceAll("\\D+",""));
                    fileNameFields.put(num, newData);
                }
                else {
                    metadataFields.put(name, newData);
                }
            }
        }

    }

    public Collection<DataField> getMetadataFields(){
        return metadataFields.values();
    }

    public DataField getMetadataField(String name) {
        return metadataFields.get(name);
    }

    public boolean hasMetadataField(String name) {
        return metadataFields.containsKey(name);
    }

    public DataField getFileNameField(int index) {
        return fileNameFields.get(index);
    }

    public boolean hasFileNameField(int index) {
        return fileNameFields.containsKey(index);
    }

    private SpecialValue stringToSpecialValue(String string) {
        String str = string.toLowerCase();
        switch (str) {
            case "time":
                return SpecialValue.Time;
            case "date":
                return SpecialValue.Date;
            case "timezone":
                return SpecialValue.Timezone;
            default:
                return null;
        }
    }
}
