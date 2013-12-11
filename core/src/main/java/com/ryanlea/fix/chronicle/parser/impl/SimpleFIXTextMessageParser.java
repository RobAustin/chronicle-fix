package com.ryanlea.fix.chronicle.parser.impl;

import com.ryanlea.fix.chronicle.Component;
import com.ryanlea.fix.chronicle.Fields;
import com.ryanlea.fix.chronicle.Group;
import com.ryanlea.fix.chronicle.Message;
import com.ryanlea.fix.chronicle.parser.MessageParser;
import com.ryanlea.fix.chronicle.pool.MessagePool;
import com.ryanlea.fix.chronicle.spec.*;
import net.openhft.lang.io.Bytes;
import net.openhft.lang.io.StopCharTesters;
import net.openhft.lang.pool.StringInterner;

public class SimpleFIXTextMessageParser implements MessageParser {

    private final FixSpec fixSpec;

    private final MessagePool messagePool;

    private final StringBuilder messageType = new StringBuilder();

    private final StringInterner messageTypeInterner = new StringInterner(13);

    public SimpleFIXTextMessageParser(FixSpec fixSpec, MessagePool messagePool) {
        this.fixSpec = fixSpec;
        this.messagePool = messagePool;
    }

    @Override
    public Message parse(Bytes bytes) {
        Message message = acquireMessage(bytes);
        MessageDefinition messageDefinition = message.getMessageDefinition();
        long startTime = System.nanoTime();
        parseFields(fixSpec.getHeaderDefinition(), message.getHeader(), bytes);
        long endHeader = System.nanoTime();
        parseFields(messageDefinition, message, bytes);
        long endBody = System.nanoTime();
        parseFields(fixSpec.getTrailerDefinition(), message.getTrailer(), bytes);
        long endTime = System.nanoTime();
        System.out.println("Parsed message type [" + message.getMessageDefinition().getType() + "]. Total: [" + (endTime - startTime) + "]ns. Header: [" + (endHeader - startTime) + "]ns. Body: [" + (endBody - endHeader) + "]ns. Trailer: [" + (endTime - endBody) + "]ns.");
        return message;
    }

    private void parseFields(EntityDefinition specDefinition, Fields fields, Bytes bytes) {
        while (bytes.remaining() > 0) {
            long position = bytes.position();
            int tag = (int) bytes.parseLong();
            if (specDefinition.hasField(tag) && !fields.exists(tag)) {
                final FieldDefinition fieldDefinition = fixSpec.getFieldDefinition(tag);
                final FieldType type = fieldDefinition.getType();
                switch(type) {
                    case STRING:
                        fields.parseString(tag, bytes, StopCharTesters.FIX_TEXT);
                        break;
                    case CHAR:
                        fields.parseChar(tag, bytes);
                        break;
                    case INT:
                    case LENGTH:
                        fields.parseInt(tag, bytes);
                        break;
                    case PRICE:
                    case QTY:
                        fields.parseDecimal(tag, bytes);
                        break;
                    case SEQNUM:
                        fields.parseLong(tag, bytes);
                        break;
                    case UTCTIMESTAMP:
                        fields.parseUTCTimestamp(tag, bytes);
                        break;
                    case NUMINGROUP:
                        long numInGroup = bytes.parseLong();
                        Group group = fields.getGroup(tag);
                        for (int i = 0; i < numInGroup; i++) {
                            parseFields(group.getGroupDefinition(), group.getFields(i), bytes);
                        }
                        break;
                    case BOOLEAN:
                        fields.parseBoolean(tag, bytes);
                        break;
                    default:
                        // this is me being lazy and not handling all cases - will come back to it
                        throw new IllegalStateException("Unexpected type: " + type);
                }
                bytes.stepBackAndSkipTo(StopCharTesters.FIX_TEXT);
            } else if (specDefinition.embedsField(tag)) {
                Component component = fields.getComponent(tag);
                parseFields(component.getComponentDefinition(), component, bytes);
            } else {
                bytes.position(position);
                return;
            }
        }
    }

    private Message acquireMessage(Bytes bytes) {
        skipTag(bytes);
        skipTag(bytes);
        assertMessageType(bytes);
        bytes.parseUTF(messageType, StopCharTesters.FIX_TEXT);

        Message message = messagePool.acquire(messageTypeInterner.intern(messageType));
        bytes.position(0);
        return message;
    }

    private void assertMessageType(Bytes bytes) {
        long tag = bytes.parseLong();
        if (tag != 35) {
            throw new IllegalArgumentException("Message Type (35) is expected as the third field in the message.");
        }
    }

    private void skipTag(Bytes bytes) {
        if (!bytes.skipTo(StopCharTesters.FIX_TEXT)) {
            throw new IllegalArgumentException("Attempted to skip to message type, failed when skipping first key-value pair.");
        }
    }
}
