package com.ryanlea.fix.chronicle;

import com.ryanlea.fix.chronicle.spec.MessageDefinition;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public abstract class Message extends Fields {

    private final MessageDefinition messageDefinition;

    private final Header header;

    private final Trailer trailer;

    protected Message(MessageDefinition messageDefinition, Header header, Trailer trailer) {
        super(messageDefinition.getFieldDefinitions());
        this.messageDefinition = messageDefinition;

        this.header = header;
        this.trailer = trailer;

    }

    public Header getHeader() {
        return header;
    }

    public Trailer getTrailer() {
        return trailer;
    }

    public MessageDefinition getMessageDefinition() {
        return messageDefinition;
    }
}
