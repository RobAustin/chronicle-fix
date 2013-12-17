package com.ryanlea.fix.chronicle.spec;

public interface EntityDefinition {

    void addFieldReference(FieldReference fieldReference);

    void addComponentReference(ComponentReference componentReference);

    /**
     * Does a direct field check on the entity
     *
     * @param tag
     * @return
     */
    boolean hasField(int tag);

    FieldDefinition[] getFieldDefinitions();

    GroupDefinition getGroupDefinition(int tag);

    /**
     * Checks if this entity contains the given tag by way of a component.
     *
     * This explcitly does not work perform a containment check for groups,
     * as they are fields in their own right.
     *
     * @param tag
     * @return
     */
    boolean embedsField(int tag);

    ComponentDefinition[] getComponentDefinitions();

    ComponentDefinition getComponentDefinition(String name);
}
