package com.ryanlea.fix.chronicle.spec;

import com.google.common.base.CaseFormat;

public enum FieldType {
    STRING,
    LENGTH,
    UTCTIMESTAMP,
    UTCDATEONLY,
    UTCTIMEONLY,
    UTCDATE,
    SEQNUM,
    CHAR,
    INT,
    QTY,
    NUMINGROUP,
    PRICE,
    AMT,
    CURRENCY,
    MULTIPLEVALUESTRING,
    EXCHANGE,
    BOOLEAN,
    LOCALMKTDATE,
    DATA,
    FLOAT,
    PERCENTAGE,
    PRICEOFFSET,
    MONTHYEAR,
    DAYOFMONTH,
    COUNTRY;

    public static FieldType fromString(String str) {
        // This uses Guava and could be re-written to remove such a dependency
//        return FieldType.valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, str));
        return FieldType.valueOf(str.toUpperCase());
    }
}
