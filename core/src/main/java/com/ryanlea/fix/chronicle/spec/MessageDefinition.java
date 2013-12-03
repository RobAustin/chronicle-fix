package com.ryanlea.fix.chronicle.spec;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

public class MessageDefinition implements EntityDefinition {

    private final List<FieldReference> fields = new ArrayList<FieldReference>();

    private final TIntObjectMap<FieldDefinition> fieldsByNumber = new TIntObjectHashMap<>();

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

    public void init(FixSpec fixSpec) {
        fieldDefinitions = new FieldDefinition[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            final FieldReference fieldReference = fields.get(i);
            final FieldDefinition fieldDefinition = fixSpec.getFieldDefinition(fieldReference);
            fieldsByNumber.put(fieldDefinition.getNumber(), fieldDefinition);
            fieldReference.init(fixSpec);
            fieldDefinitions[i] = fieldDefinition;
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
