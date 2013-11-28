package com.ryanlea.fix.chronicle.spec;

public interface EntityDefinition {

    void addFieldReference(FieldReference fieldReference);

    boolean hasField(int tag);

}
