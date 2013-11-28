package com.ryanlea.fix.chronicle.spec;

import java.util.ArrayList;
import java.util.List;

public class MessageDefinition implements EntityDefinition {

    private final List<FieldReference> fields = new ArrayList<FieldReference>();

    private final String name;

    private final String type;

    private final String category;

    public MessageDefinition(String name, String type, String category) {
        this.name = name;
        this.type = type;
        this.category = category;
    }

    public void addFieldReference(FieldReference fieldReference) {
        fields.add(fieldReference);
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
