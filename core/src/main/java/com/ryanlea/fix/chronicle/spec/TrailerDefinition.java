package com.ryanlea.fix.chronicle.spec;

import java.util.ArrayList;
import java.util.List;

public class TrailerDefinition implements EntityDefinition {

    private final List<FieldReference> fields = new ArrayList<FieldReference>();

    public void addFieldReference(FieldReference fieldReference) {
        fields.add(fieldReference);
    }

    public void addGroupDefinition(GroupDefinition groupDefinition) {
        // could throw an exception here - groups are really allowed in a trailer either
    }
}
