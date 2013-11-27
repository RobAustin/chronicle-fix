package com.ryanlea.fix.chronicle.spec.impl;

import com.ryanlea.fix.chronicle.spec.FixSpec;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class StaxFixSpecParserTest {

    @Test
    public void parseFxSpotStream() {
        StaxFixSpecParser parser = new StaxFixSpecParser();
        InputStream inputStream = StaxFixSpecParserTest.class.getResourceAsStream("/fxspotstream.fix.spec-1.0.22.xml");
        FixSpec fixSpec = parser.parse(inputStream);
        assertThat(fixSpec, notNullValue());
    }
}
