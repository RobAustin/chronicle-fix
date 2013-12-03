package com.ryanlea.fix.chronicle;

import com.ryanlea.fix.chronicle.spec.HeaderDefinition;

public abstract class Header extends Fields {

    private final HeaderDefinition headerDefinition;

    protected Header(HeaderDefinition headerDefinition) {
        super(headerDefinition.getFieldDefinitions());
        this.headerDefinition = headerDefinition;
    }

    public HeaderDefinition getHeaderDefinition() {
        return headerDefinition;
    }
}
