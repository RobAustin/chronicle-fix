package com.ryanlea.fix.chronicle;

import com.ryanlea.fix.chronicle.spec.MessageDefinition;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public abstract class Message extends Fields {

    private final MessageDefinition messageDefinition;

    private final Header header;

    private final Trailer trailer;

    private final TIntObjectMap<Group> groups;

    protected Message(MessageDefinition messageDefinition, Header header, Trailer trailer) {
        super(messageDefinition.getFieldDefinitions());
        this.messageDefinition = messageDefinition;

        this.header = header;
        this.trailer = trailer;

        // use the message definition to guesstimate the number of groups required
        this.groups = new TIntObjectHashMap<>();
    }

    protected Group _group(int fid) {
        return groups.get(fid);
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
