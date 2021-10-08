package com.snash;

import java.io.File;
import java.util.HashMap;

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
        private String title;

        private String defaultValue;

        private String fixedValue;

        private SpecialValue specialValue;

        public String getName() {
            return name;
        }

        public String getTitle() {
            return title;
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
    public ConfigurationData(File configFile) {

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
}
