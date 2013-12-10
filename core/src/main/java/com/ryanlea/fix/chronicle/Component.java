package com.ryanlea.fix.chronicle;

import com.ryanlea.fix.chronicle.spec.ComponentDefinition;

public abstract class Component extends Fields {

    private ComponentDefinition componentDefinition;

    protected Component(ComponentDefinition componentDefinition) {
        super(componentDefinition.getFieldDefinitions());
        this.componentDefinition = componentDefinition;
    }

    public ComponentDefinition getComponentDefinition() {
        return componentDefinition;
    }
}
