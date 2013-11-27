package com.ryanlea.fix.chronicle.spec;

import java.util.ArrayList;
import java.util.List;

public class HeaderDefinition implements EntityDefinition {

    private final List<FieldReference> fields = new ArrayList<FieldReference>();

    public void addFieldReference(FieldReference fieldReference) {
        fields.add(fieldReference);
    }

    public void addGroupDefinition(GroupDefinition groupDefinition) {
        // could throw an exception instead here - groups aren't allowed in headers traditionally
    }
}
