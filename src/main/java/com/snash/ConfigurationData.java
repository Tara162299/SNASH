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
import java.util.HashMap;
import java.util.Locale;

public class ConfigurationData {
    // TODO add upload configuration data

    private enum SpecialValue {
        Time,
        Date,
        Timezone
    }

    public class dataField {
        // name within actual metadata attached to a file
        private String name;

        // user displayed name
        private String alias;

        private String defaultValue;

        private String fixedValue;

        private SpecialValue specialValue;

        public dataField(String name, String alias, String defaultValue, String fixedValue, SpecialValue specialValue) {
            this.name = name;
            this.alias = alias;
            this.defaultValue = defaultValue;
            this.fixedValue = fixedValue;
            this.specialValue = specialValue;
        }

        public String getName() {
            return name;
        }

        public String getAlias() {
            return alias;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public String getFixedValue() {
            return fixedValue;
        }

        public boolean hasFixedValue() {
            return fixedValue != null;
        }
    }

    private HashMap<String, dataField> metadataFields;
    private HashMap<Integer, dataField> fileNameFields;

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
                NodeList tags = fieldElement.getElementsByTagName("name");
                String name = tags.item(0).getTextContent();
                tags = fieldElement.getElementsByTagName("title");
                String alias = tags.item(0).getTextContent();
                tags = fieldElement.getElementsByTagName("fixedValue");
                String fixedValue = tags.item(0).getTextContent();
                tags = fieldElement.getElementsByTagName("defaultValue");
                String defaultValue = tags.item(0).getTextContent();
                tags = fieldElement.getElementsByTagName("specialValue");
                SpecialValue specialValue = stringToSpecialValue(tags.item(0).getTextContent());

                dataField newData = new dataField(name, alias, fixedValue, defaultValue, specialValue);
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

    public dataField getMetadataField(String name) {
        return metadataFields.get(name);
    }

    public boolean hasMetadataField(String name) {
        return metadataFields.containsKey(name);
    }

    public dataField getFileNameField(int index) {
        return fileNameFields.get(index);
    }

    public boolean hasFileNameField(int index) {
        return fileNameFields.containsKey(index);
    }

    private SpecialValue stringToSpecialValue(String string) {
        String str = string.toLowerCase();
        return switch (str) {
            case "time" -> SpecialValue.Time;
            case "date" -> SpecialValue.Date;
            case "timezone" -> SpecialValue.Timezone;
            default -> null;
        };
    }
}
