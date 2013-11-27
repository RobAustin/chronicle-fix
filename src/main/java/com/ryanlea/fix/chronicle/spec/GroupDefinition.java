package com.ryanlea.fix.chronicle.spec;

import java.util.ArrayList;
import java.util.List;

public class GroupDefinition extends FieldReference {

    private final List<FieldReference> fields = new ArrayList<FieldReference>();

    public GroupDefinition(String name, boolean required) {
        super(name, required);
    }

    public void addFieldReference(FieldReference fieldReference) {
        fields.add(fieldReference);
    }
}
