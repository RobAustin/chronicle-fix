package com.ryanlea.fix.chronicle.spec.parser;

import com.ryanlea.fix.chronicle.spec.FixSpec;
import com.ryanlea.fix.chronicle.spec.FixSpec;

import java.io.InputStream;

public interface FixSpecParser {

    FixSpec parse(InputStream inputStream);

}
