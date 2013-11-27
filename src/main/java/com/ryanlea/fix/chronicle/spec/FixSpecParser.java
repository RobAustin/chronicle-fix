package com.ryanlea.fix.chronicle.spec;

import java.io.InputStream;

public interface FixSpecParser {

    FixSpec parse(InputStream inputStream);

}
