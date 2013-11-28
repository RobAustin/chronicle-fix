package com.ryanlea.fix.chronicle.spec;

import java.util.ArrayList;
import java.util.List;

public class FieldDefinition {

    private final int number;

    private final String name;

    private final FieldType type;

    private final List<ValueDefinition> values = new ArrayList<ValueDefinition>();

    public FieldDefinition(int number, String name, FieldType type) {
        this.number = number;
        this.name = name;
        this.type = type;
    }

    public void addValue(ValueDefinition valueDefinition) {
        values.add(valueDefinition);
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public FieldType getType() {
        return type;
    }

    public Iterable<ValueDefinition> getValues() {
        return values;
    }
}
