package com.ryanlea.fix.chronicle.spec;

import com.google.common.base.CaseFormat;

public enum FieldType {
    STRING,
    LENGTH,
    U_T_C_TIMESTAMP,
    SEQ_NUM,
    CHAR,
    INT,
    QTY,
    NUM_IN_GROUP;

    public static FieldType fromString(String str) {
        // This uses Guava and could be re-written to remove such a dependency
        return FieldType.valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, str));
    }
}
