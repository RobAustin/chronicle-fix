package com.ryanlea.fix.chronicle.parser;

import com.ryanlea.fix.chronicle.Message;
import net.openhft.lang.io.Bytes;

public interface MessageParser {

    Message parse(Bytes bytes);

}
