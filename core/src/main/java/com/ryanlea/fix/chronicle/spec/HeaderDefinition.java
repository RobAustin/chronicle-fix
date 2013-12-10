package com.ryanlea.fix.chronicle.spec;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

public class HeaderDefinition implements EntityDefinition {

    private final List<FieldReference> fields = new ArrayList<FieldReference>();

    private final TIntObjectMap<FieldDefinition> fieldsByNumber = new TIntObjectHashMap<>();

    private FieldDefinition[] fieldDefinitions;

    public void addFieldReference(FieldReference fieldReference) {
        fields.add(fieldReference);
    }

    @Override
    public void addComponentReference(ComponentReference componentReference) {
        // Is this actually correct?
        throw new UnsupportedOperationException("Cannot add components to the header.");
    }

    public void init(FixSpec fixSpec) {
        fieldDefinitions = new FieldDefinition[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            final FieldReference fieldReference = fields.get(i);
            final FieldDefinition fieldDefinition = fixSpec.getFieldDefinition(fieldReference);
            fieldsByNumber.put(fieldDefinition.getNumber(), fieldDefinition);
            fieldDefinitions[i] = fieldDefinition;
        }
    }

    @Override
    public boolean hasField(int tag) {
        return fieldsByNumber.containsKey(tag);
    }

    public Iterable<? extends FieldReference> getFieldReferences() {
        return fields;
    }

    @Override
    public FieldDefinition[] getFieldDefinitions() {
        return fieldDefinitions;
    }

    @Override
    public GroupDefinition getGroupDefinition(int tag) {
        throw new UnsupportedOperationException("Headers cannot contain groups.");
    }

    @Override
    public boolean embedsField(int tag) {
        return false;
    }
}
