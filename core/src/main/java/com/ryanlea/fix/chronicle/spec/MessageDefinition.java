package com.ryanlea.fix.chronicle.spec;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

public class MessageDefinition implements EntityDefinition {

    private final List<FieldReference> fields = new ArrayList<>();

    private final List<ComponentReference> components = new ArrayList<>();

    private final TIntObjectMap<FieldDefinition> fieldsByNumber = new TIntObjectHashMap<>();

    private final TIntObjectMap<FieldReference> fieldReferencesByNumber = new TIntObjectHashMap<>();

    private final List<ComponentDefinition> componentDefinitions = new ArrayList<>();

    private final String name;

    private final String type;

    private final String category;

    private FieldDefinition[] fieldDefinitions;

    public MessageDefinition(String name, String type, String category) {
        this.name = name;
        this.type = type;
        this.category = category;
    }

    public void addFieldReference(FieldReference fieldReference) {
        fields.add(fieldReference);
    }

    @Override
    public void addComponentReference(ComponentReference componentReference) {
        components.add(componentReference);
    }

    public void init(FixSpec fixSpec) {
        fieldDefinitions = new FieldDefinition[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            final FieldReference fieldReference = fields.get(i);
            final FieldDefinition fieldDefinition = fixSpec.getFieldDefinition(fieldReference);
            fieldsByNumber.put(fieldDefinition.getNumber(), fieldDefinition);
            fieldReference.init(fixSpec);
            fieldDefinitions[i] = fieldDefinition;
            fieldReferencesByNumber.put(fieldDefinition.getNumber(), fieldReference);
        }

        for (int i = 0; i < components.size(); i++) {
            final ComponentReference componentReference = components.get(i);
            final ComponentDefinition componentDefinition = fixSpec.getComponentDefinition(componentReference);
            componentDefinitions.add(componentDefinition);
        }
    }

    @Override
    public boolean hasField(int tag) {
        return fieldsByNumber.containsKey(tag);
    }

    @Override
    public FieldDefinition[] getFieldDefinitions() {
        return fieldDefinitions;
    }

    @Override
    public GroupDefinition getGroupDefinition(int tag) {
        return (GroupDefinition) fieldReferencesByNumber.get(tag);
    }

    @Override
    public boolean embedsField(int tag) {
        for (int i = 0; i < componentDefinitions.size(); i++) {
            final ComponentDefinition componentDefinition = componentDefinitions.get(i);
            if (componentDefinition.hasField(tag) || componentDefinition.embedsField(tag)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public Iterable<? extends FieldReference> getFieldReferences() {
        return fields;
    }
}
