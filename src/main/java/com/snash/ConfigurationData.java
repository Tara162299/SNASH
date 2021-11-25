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

    private static final int MAX_FIELDS = 100;
    private static final int MAX_TAG_LENGTH = 50;

    // Warning strings to be used on their own, with no identifier of which field or tag is wrong.
    private static final String TOO_MANY_FIELDS_WARNING_NAMELESS = "The maximum number of fields is " + MAX_FIELDS + ". All further fields will be ignored.";
    private static final String METADATA_NO_NAME_WARNING_NAMELESS = "A metadata field has no name. It will be ignored.";
    private static final String FILENAME_NO_NAME_WARNING_NAMELESS = "A filename field has no name, alias, fixed value, or special value. It will be ignored.";
    private static final String INVALID_BOTH_WARNING_NAMELESS = "A field has a both tag whose value is not \"true\". The tag will be ignored.";
    private static final String DUPLICATE_TAG_WARNING_NAMELESS = "A field has more than one of the same tag. All but the first will be ignored.";

    // Warning strings that have space for a name in front.
    private static final String INVALID_CHARACTER_WARNING = " has a tag with an invalid character. The tag will be ignored.";
    private static final String TOO_LONG_FIELD_WARNING = " has a tag with length over " + MAX_TAG_LENGTH + ". The field will be ignored.";
    private static final String INVALID_NAME_LENGTH_WARNING = " has a name with a length not equal to 4. The field will be ignored.";
    private static final String INVALID_SPECIAL_WARNING = " has an invalid special value. It will be ignored.";
    private static final String EMPTY_TAG_WARNING = " has an empty tag. It will be ignored.";

    private static final String FIXED_SPECIAL_DEFAULT_WARNING = " has both a fixed, special, and default value. In this case, the default and special values will be ignored.";
    private static final String FIXED_SPECIAL_WARNING = " has both a fixed and special value. In this case, the special value will be ignored.";
    private static final String FIXED_DEFAULT_WARNING = " has both a fixed and default value. In this case, the default value will be ignored.";
    private static final String SPECIAL_DEFAULT_WARNING = " has both a special and default value. In this case, the default value will be ignored.";


    private final StringBuilder warningBuilder = new StringBuilder();

    public enum SpecialValue {
        Time,
        Date,
        Timezone
    }

    // What type of value a DataField has.
    public enum ValueType {
        // In case of collision, fixed values take the highest priority. In this case, the value is final after configuration.
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
                            boolean isMetadata, boolean isFilename,
                            ValueType valueType) {}

    private final List<DataField> dataFields = new ArrayList<>();


    // creates configuration data from a config file
    public ConfigurationData(File configFile) throws ParserConfigurationException, IOException, SAXException {
        // TODO handle incorrectly formatted xml files

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(configFile);
        Element root = doc.getDocumentElement();

        NodeList nodeList = root.getElementsByTagName("metadata");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                DataField dataField = metadataFieldFromElement((Element) node);
                if (dataField != null) {
                    dataFields.add(dataField);
                }
            }
        }

        nodeList = root.getElementsByTagName("filename");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                DataField dataField = filenameFieldFromElement((Element) node);
                if (dataField != null) {
                    dataFields.add(dataField);
                }
            }
        }
    }

    private DataField metadataFieldFromElement(Element element){
        DataFieldParser dfp = new DataFieldParser().from(element);

        String name = validateName(dfp.name());
        if (name == null){
            return null;
        }

        if (dfp.fixedValue() != null){
            return new DataField(name, dfp.alias(), dfp.fixedValue(), null, true, dfp.isBoth(), ValueType.FIXED);
        } else if (dfp.specialValue() != null){
            return new DataField(name, dfp.alias(), null, dfp.specialValue(), true, dfp.isBoth(), ValueType.SPECIAL);
        } else {
            return new DataField(name, dfp.alias(), dfp.defaultValue(), null, true, dfp.isBoth(), ValueType.NORMAL);
        }
    }

    private DataField filenameFieldFromElement(Element element){
        DataFieldParser dfp = new DataFieldParser().from(element);

        String name = dfp.name();
        if (dfp.isBoth()){
            name = validateName(name);
        }
        if (name == null && dfp.alias() == null && dfp.fixedValue() == null && dfp.specialValue() == null){
            warningBuilder.append(FILENAME_NO_NAME_WARNING_NAMELESS).append('\n');
            return null;
        }

        if (dfp.fixedValue() != null){
            return new DataField(name, dfp.alias(), dfp.fixedValue(), null, dfp.isBoth(), true, ValueType.FIXED);
        } else if (dfp.specialValue() != null){
            return new DataField(name, dfp.alias(), null, dfp.specialValue(), dfp.isBoth(), true, ValueType.SPECIAL);
        } else {
            return new DataField(name, dfp.alias(), dfp.defaultValue(), null, dfp.isBoth(), true, ValueType.NORMAL);
        }

    }

    private class DataFieldParser {
        private String name;
        private String alias;
        private String fixedValue;
        private SpecialValue specialValue;
        private String defaultValue;
        private boolean isBoth;

        private DataFieldParser(String name, String alias, String fixedValue, SpecialValue specialValue,
                                String defaultValue, boolean isBoth){
            this.name = name;
            this.alias = alias;
            this.fixedValue = fixedValue;
            this.specialValue = specialValue;
            this.defaultValue = defaultValue;
            this.isBoth = isBoth;
        }

        public DataFieldParser(){}

        public DataFieldParser from(Element element){
            String name = tagTextFromElement("name", element);
            String alias = validateTag(name, tagTextFromElement("alias", element));
            String fixedValue = validateTag(name, tagTextFromElement("fixedValue", element));
            String defaultValue = validateTag(name, tagTextFromElement("defaultValue", element));
            SpecialValue specialValue = stringToSpecialValue(name, tagTextFromElement("specialValue", element));
            String both = tagTextFromElement("both", element);

            boolean isBoth = validateBoth(both);

            boolean hasFixed = fixedValue != null && !fixedValue.isEmpty();
            boolean hasSpecial = specialValue != null;
            boolean hasDefault = defaultValue != null && !defaultValue.isEmpty();

            String collisionWarning = valueTagWarningMessage(name, hasFixed, hasSpecial, hasDefault);
            if (collisionWarning != null && !collisionWarning.isEmpty()){
                warningBuilder.append(valueTagWarningMessage(name, hasFixed, hasSpecial, hasDefault)).append('\n');
            }

            return new DataFieldParser(name, alias, fixedValue, specialValue, defaultValue, isBoth);
        }

        public String name(){ return name; }
        public String alias(){ return alias; }
        public String fixedValue(){ return fixedValue; }
        public SpecialValue specialValue(){ return specialValue; }
        public String defaultValue(){ return defaultValue; }
        public boolean isBoth(){ return isBoth; }
    }

    private String validateTag(String name, String tag){
        String identifier = identifier(name);
        if (tag == null){
            return null;
        }
        if (tag.isEmpty()){
            warningBuilder.append(identifier).append(EMPTY_TAG_WARNING).append('\n');
        }
        if (tag.length() > MAX_TAG_LENGTH) {
            warningBuilder.append(identifier).append(TOO_LONG_FIELD_WARNING).append('\n');
            return null;
        }

        for (char c : tag.toCharArray()){
            if (c < 32 || c > 126){
                warningBuilder.append(identifier).append(INVALID_CHARACTER_WARNING).append('\n');
                return null;
            }
        }
        return tag;
    }

    // Returns the input if valid. Writes to the warning builder and returns null if not.
    private String validateName(String name){
        if (name == null || name.isEmpty()){
            warningBuilder.append(METADATA_NO_NAME_WARNING_NAMELESS).append('\n');
            return null;
        }

        String identifier = identifier(name);
        String parsedName = InfoTagList.stringToInfoTag(name);

        if (parsedName.length() != 4){
            warningBuilder.append(identifier).append(INVALID_NAME_LENGTH_WARNING).append('\n');
            return null;
        }
        for (char c : name.toCharArray()){
            if (c < 32 || c > 126){
                warningBuilder.append(identifier).append(INVALID_CHARACTER_WARNING).append('\n');
                return null;
            }
        }
        return parsedName;
    }

    private boolean validateBoth(String both){
        if (both != null){
            if (both.equals("true")){
                return true;
            } else {
                warningBuilder.append(INVALID_BOTH_WARNING_NAMELESS).append('\n');
            }
        }
        return false;
    }

    // Returns a warning message; empty if the configuration data does not have problems.
    public String warningMessage(){
        if (dataFields.size() > MAX_FIELDS) {
            warningBuilder.append(TOO_MANY_FIELDS_WARNING_NAMELESS).append('\n');
        }

        // Delete final newline.
        if (!warningBuilder.isEmpty()){
            warningBuilder.deleteCharAt(warningBuilder.length() - 1);
        }

        return warningBuilder.toString();
    }

    private String valueTagWarningMessage(String name, boolean hasFixed, boolean hasSpecial, boolean hasDefault) {
        String identifier = identifier(name);
        if (hasFixed) {
            return valueTagWarningMessageFixed(name, hasSpecial, hasDefault);
        } else if (hasDefault && hasSpecial){
            return identifier + SPECIAL_DEFAULT_WARNING;
        } else {
            return "";
        }
    }

    private String valueTagWarningMessageFixed(String name, boolean hasSpecial, boolean hasDefault){
        String identifier = identifier(name);
        if (hasDefault){
            if (hasSpecial){
                return identifier + FIXED_SPECIAL_DEFAULT_WARNING;
            } else {
                return identifier + FIXED_DEFAULT_WARNING;
            }
        } else if (hasSpecial) {
            return identifier + FIXED_SPECIAL_WARNING;
        } else {
            return "";
        }
    }
        private String identifier(String name){
            if (name == null || name.isEmpty()){
                return "A field";
            } else {
                return "Field " + name;
            }
        }

    private String tagTextFromElement(String tag, Element element){
        NodeList tags = element.getElementsByTagName(tag);
        if (tags.getLength() > 0) {
            if (tags.getLength() > 1){
                warningBuilder.append(DUPLICATE_TAG_WARNING_NAMELESS).append('\n');
            }
            return tags.item(0).getTextContent();
        } else {
            return null;
        }
    }

    public Collection<DataField> dataFields(){
        return dataFields;
    }

    // Parses a special value from a string. Returns null if the string is null or invalid.
    private SpecialValue stringToSpecialValue(String name, String string) {
        String identifier = identifier(name);
        if (string == null){
            return null;
        }

        String str = string.toLowerCase();
        SpecialValue output;
        switch (str) {
            case "time" -> output = SpecialValue.Time;
            case "date" -> output = SpecialValue.Date;
            case "timezone", "time zone" -> output = SpecialValue.Timezone;
            default -> {
                warningBuilder.append(identifier).append(INVALID_SPECIAL_WARNING).append('\n');
                output = null;
            }
        }
        return output;
    }
}