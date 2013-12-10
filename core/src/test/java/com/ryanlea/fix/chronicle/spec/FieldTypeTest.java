package com.ryanlea.fix.chronicle.spec;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class FieldTypeTest {

    @Test
    public void validValues() {
        assertThat(FieldType.fromString("String"), equalTo(FieldType.STRING));
        assertThat(FieldType.fromString("Length"), equalTo(FieldType.LENGTH));
        assertThat(FieldType.fromString("UTCTimestamp"), equalTo(FieldType.UTCTIMESTAMP));
        assertThat(FieldType.fromString("SeqNum"), equalTo(FieldType.SEQNUM));
        assertThat(FieldType.fromString("char"), equalTo(FieldType.CHAR));
        assertThat(FieldType.fromString("int"), equalTo(FieldType.INT));
        assertThat(FieldType.fromString("Qty"), equalTo(FieldType.QTY));
    }
}
