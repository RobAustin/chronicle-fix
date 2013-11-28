package com.ryanlea.fix.chronicle.spec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixSpec {

    private int major;

    private int minor;
    private HeaderDefinition headerDefinition;
    private TrailerDefinition trailerDefinition;
    private List<FieldDefinition> fieldDefinitions = new ArrayList<FieldDefinition>();
    private List<MessageDefinition> messageDefinitions = new ArrayList<MessageDefinition>();
    private Map<String, FieldDefinition> fieldDefinitionsByName = new HashMap<String, FieldDefinition>();

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public void setHeaderDefinition(HeaderDefinition headerDefinition) {
        this.headerDefinition = headerDefinition;
    }

    public HeaderDefinition getHeaderDefinition() {
        return headerDefinition;
    }

    public void setTrailerDefinition(TrailerDefinition trailerDefinition) {
        this.trailerDefinition = trailerDefinition;
    }

    public TrailerDefinition getTrailerDefinition() {
        return trailerDefinition;
    }

    public void addFieldDefinition(FieldDefinition fieldDefinition) {
        fieldDefinitions.add(fieldDefinition);
    }

    public Iterable<? extends MessageDefinition> messageDefinitions() {
        return messageDefinitions;
    }

    public void addMessageDefinition(MessageDefinition messageDefinition) {
        messageDefinitions.add(messageDefinition);
    }

    public Iterable<FieldDefinition> getFieldDefinitions() {
        return fieldDefinitions;
    }

    public FieldDefinition getFieldDefinition(FieldReference fieldReference) {
        return fieldDefinitionsByName.get(fieldReference.getName());
    }

    public void init() {
        for (FieldDefinition fieldDefinition : fieldDefinitions) {
            fieldDefinitionsByName.put(fieldDefinition.getName(), fieldDefinition);
        }
    }
}
