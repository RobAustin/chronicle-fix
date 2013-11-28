package com.ryanlea.fix.chronicle.spec;

public class ValueDefinition {

    private final String enumValue; // is this a String or a char?

    private final String description;

    public ValueDefinition(String enumValue, String description) {
        this.enumValue = enumValue;
        this.description = description;
    }
}
