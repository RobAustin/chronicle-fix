package com.ryanlea.fix.chronicle.spec.impl;

import com.ryanlea.fix.chronicle.spec.FixSpec;
import com.ryanlea.fix.chronicle.spec.parser.impl.StaxFixSpecParser;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class StaxFixSpecParserTest {

    @Test
    public void parseSpec() {
        StaxFixSpecParser parser = new StaxFixSpecParser();
        InputStream inputStream = StaxFixSpecParserTest.class.getResourceAsStream("/fix44.spec.xml");
        FixSpec fixSpec = parser.parse(inputStream);
        assertThat(fixSpec, notNullValue());
    }
}
