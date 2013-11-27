package com.ryanlea.fix.chronicle.spec;

import java.util.ArrayList;
import java.util.List;

public class FieldDefinition {

    private final int number;

    private final String name;

    private final boolean required;

    private final List<ValueDefinition> values = new ArrayList<ValueDefinition>();

    public FieldDefinition(int number, String name, boolean required) {
        this.number = number;
        this.name = name;
        this.required = required;
    }

    public void addValue(ValueDefinition valueDefinition) {
        values.add(valueDefinition);
    }
}
