package com.ryanlea.fix.chronicle.spec.impl;

import com.ryanlea.fix.chronicle.spec.FixSpec;
import com.ryanlea.fix.chronicle.spec.parser.impl.StaxFixSpecParser;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class StaxFixSpecParserTest {

    @Test
    public void parseFxSpotStream() {
        StaxFixSpecParser parser = new StaxFixSpecParser();
        InputStream inputStream = StaxFixSpecParserTest.class.getResourceAsStream("/fx.fix.spec.xml");
        FixSpec fixSpec = parser.parse(inputStream);
        assertThat(fixSpec, notNullValue());
    }
}
