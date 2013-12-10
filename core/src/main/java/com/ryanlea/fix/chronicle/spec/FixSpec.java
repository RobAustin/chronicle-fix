package com.ryanlea.fix.chronicle.spec;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixSpec {

    private int major;

    private int minor;
    private HeaderDefinition headerDefinition;
    private TrailerDefinition trailerDefinition;
    private final List<FieldDefinition> fieldDefinitions = new ArrayList<>();
    private final List<MessageDefinition> messageDefinitions = new ArrayList<>();
    private final List<ComponentDefinition> componentDefinitions = new ArrayList<>();
    private final TIntObjectMap<MessageDefinition> messageDefinitionsByType = new TIntObjectHashMap<>();
    private final Map<String, FieldDefinition> fieldDefinitionsByName = new HashMap<>();
    private final TIntObjectMap<FieldDefinition> fieldDefinitionsByNumber = new TIntObjectHashMap<>();
    private final Map<String, ComponentDefinition> componentDefinitionsByName = new HashMap<>();
    private final Map<String, List<ComponentDefinitionListener>> componentDefinitionListeners = new HashMap<>();

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

    public void addComponentDefinition(ComponentDefinition componentDefinition) {
        componentDefinitions.add(componentDefinition);
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
            fieldDefinitionsByNumber.put(fieldDefinition.getNumber(), fieldDefinition);
        }

        for (ComponentDefinition componentDefinition : componentDefinitions) {
            componentDefinition.init(this);
            componentDefinitionsByName.put(componentDefinition.getName(), componentDefinition);
            register(componentDefinition);
        }

        for (MessageDefinition messageDefinition : messageDefinitions) {
            messageDefinition.init(this);
            messageDefinitionsByType.put(messageDefinition.getType().hashCode(), messageDefinition);
        }

        headerDefinition.init(this);
        trailerDefinition.init(this);
    }

    private void register(ComponentDefinition componentDefinition) {
        List<ComponentDefinitionListener> componentDefinitionListeners =
                this.componentDefinitionListeners.get(componentDefinition.getName());
        if (componentDefinitionListeners != null) {
            for (int i = 0; i < componentDefinitionListeners.size(); i++) {
                componentDefinitionListeners.get(i).registered(componentDefinition);
            }
        }
    }

    void subscribe(String name, ComponentDefinitionListener componentDefinitionListener) {
        List<ComponentDefinitionListener> componentDefinitionListeners = this.componentDefinitionListeners.get(name);
        if (componentDefinitionListeners == null) {
            componentDefinitionListeners = new ArrayList<>();
            this.componentDefinitionListeners.put(name, componentDefinitionListeners);
        }
        componentDefinitionListeners.add(componentDefinitionListener);
    }

    public FieldDefinition getFieldDefinition(int tag) {
        return fieldDefinitionsByNumber.get(tag);
    }

    public MessageDefinition getMessageDefinition(String messageType) {
        return messageDefinitionsByType.get(messageType.hashCode());
    }

    public ComponentDefinition getComponentDefinition(ComponentReference componentReference) {
        return componentDefinitionsByName.get(componentReference.getName());
    }

    public Iterable<? extends ComponentDefinition> getComponentDefinitions() {
        return componentDefinitions;
    }
}
