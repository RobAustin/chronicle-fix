package com.ryanlea.fix.chronicle.spec;

public class FieldReference {

    private String name;

    private boolean required;

    public FieldReference(String name, boolean required) {
        this.name = name;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public void init(FixSpec fixSpec) {
        // do nothing
    }
}
