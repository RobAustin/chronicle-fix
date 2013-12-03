package com.ryanlea.fix.chronicle;

import com.ryanlea.fix.chronicle.spec.FieldDefinition;
import com.ryanlea.fix.chronicle.spec.TrailerDefinition;

public abstract class Trailer extends Fields {

    protected Trailer(TrailerDefinition trailerDefinition) {
        super(trailerDefinition.getFieldDefinitions());
    }
}
