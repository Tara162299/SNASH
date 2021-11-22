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

    // What type of value a DataField has.
    public enum ValueType {
        // In case of collision, fixed values take highest priority. In this case, the value is final after configuration.
        FIXED,
        // In case of collision, special values take second priority. In this case, the value is programmatically determined.
        SPECIAL,
        // If there is no fixed or special value, the type is normal. In this case, the value is entered by the user.
        NORMAL
    }

    /**
     * @param name  The name of the tag attached to the file, i.e. "artist"
     * @param alias The alias to be displayed to the user, i.e. "callsign"
     * @param value  The default value or fixed value. Null if there is none, or if there is a special value.
     * @param specialValue  The type of special value, or null if there is no special value.
     * @param valueType The method of entry for values- config-defined, automatic, or user-defined.
     */
    public record DataField(String name, String alias,
                            String value, SpecialValue specialValue,
                            ValueType valueType) {}

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
        SpecialValue specialValue = stringToSpecialValue(name, tagTextFromElement("specialValue", element));

        Objects.requireNonNull(name, "Every metadata tag must have a true name.");

        if (fixedValue != null){
            return new DataField(name, alias, fixedValue, null, ValueType.FIXED);
        } else if (specialValue != null){
            return new DataField(name, alias, null, specialValue, ValueType.SPECIAL);
        } else {
            return new DataField(name, alias, defaultValue, null, ValueType.NORMAL);
        }
    }

    // Returns a warning message; empty if the configuration data does not have problems.
    public String warningMessage(){
        StringBuilder builder = new StringBuilder();

        for (DataField field : metadataFields.values()){
            boolean hasFixed = field.valueType() == ValueType.FIXED;
            boolean hasSpecial = field.valueType() == ValueType.FIXED;
            boolean hasDefault = field.valueType() == ValueType.FIXED;
            String message = warningMessage(field.name(), hasFixed, hasSpecial, hasDefault);
            if (message != null){
                builder.append(message);
                builder.append('\n');
            }
        }
        // Delete final newline.
        if (!builder.isEmpty()){
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    private String warningMessage(String name, boolean hasFixed, boolean hasSpecial, boolean hasDefault) {
        if (hasFixed) {
            return warningMessageFixed(name, hasSpecial, hasDefault);
        } else if (hasDefault && hasSpecial){
            return "Metadata field " + name + " has both a special and default value.\n" +
                   "In this case, the default value will be ignored.";
        } else {
            return null;
        }
    }

    private String warningMessageFixed(String name, boolean hasSpecial, boolean hasDefault){
        if (hasDefault){
            if (hasSpecial){
                return "Metadata field " + name + " has both a fixed, special, and default value.\n" +
                        "In this case, the default and special values will be ignored.";
            } else {
                return "Metadata field " + name + " has both a fixed and default value.\n" +
                        "In this case, the default value will be ignored.";
            }
        } else if (hasSpecial) {
            return "Metadata field " + name + " has both a fixed and special value.\n" +
                    "In this case, the special value will be ignored.";
        } else {
            return null;
        }
    }

    private String tagTextFromElement(String tag, Element element){
        NodeList tags = element.getElementsByTagName(tag);
        if (tags.getLength() > 0) {
            if (tags.getLength() > 1){
                System.out.println("A metadata field has two " + tag + " tags. All but the first will be ignored.");
            }
            return tags.item(0).getTextContent();
        } else {
            return null;
        }
    }

    public Collection<DataField> getMetadataFields(){
        return metadataFields.values();
    }

    // Parses a special value from a string. Returns null if the string is null or invalid.
    private SpecialValue stringToSpecialValue(String name, String string) {
        if (string == null){
            return null;
        }

        String str = string.toLowerCase();
        SpecialValue output;
        switch (str) {
            case "time" : output = SpecialValue.Time; break;
            case "date" : output = SpecialValue.Date; break;
            case "timezone", "time zone" : output = SpecialValue.Timezone; break;
            default :
                System.out.println("Metadata field " + name + " has an invalid special value. It will be ignored.");
                output = null;
        }
        return output;
    }
}