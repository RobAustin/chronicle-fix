package com.ryanlea.fix.chronicle.parser.impl;

import com.ryanlea.fix.chronicle.Fields;
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

    private final StringBuilder utcTimeStamp = new StringBuilder();

    private final StringInterner messageTypeInterner = new StringInterner(13);

    public SimpleFIXTextMessageParser(FixSpec fixSpec, MessagePool messagePool) {
        this.fixSpec = fixSpec;
        this.messagePool = messagePool;
    }

    @Override
    public Message parse(Bytes bytes) {
        Message message = acquireMessage(bytes);
        MessageDefinition messageDefinition = message.getMessageDefinition();
        parseFields(fixSpec.getHeaderDefinition(), message.getHeader(), bytes);
        parseFields(messageDefinition, message, bytes);
        parseFields(fixSpec.getTrailerDefinition(), message.getTrailer(), bytes);
        while (bytes.remaining() > 0) {
            long tag = bytes.parseLong();

            FieldDefinition fieldDefinition = fixSpec.getFieldDefinition((int)tag);


        }
        return message;
    }

    private void parseFields(EntityDefinition specDefinition, Fields fields, Bytes bytes) {
        while (bytes.remaining() > 0) {
            long position = bytes.position();
            int tag = (int) bytes.parseLong();
            if (specDefinition.hasField(tag)) {
                final FieldDefinition fieldDefinition = fixSpec.getFieldDefinition((int)tag);
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
                    case QTY:
                        fields.parseDecimal(tag, bytes);
                        break;
                    case SEQ_NUM:
                        fields.parseLong(tag, bytes);
                        break;
                    case U_T_C_TIMESTAMP:
                        fields.parseUTCTimestamp(tag, bytes);
                        break;
                    case NUM_IN_GROUP:
                        break;
                    default:
                        // this is me being lazy and not handling all cases - will come back to it
                        throw new IllegalStateException("Unexpected type: " + type);
                }
                bytes.stepBackAndSkipTo(StopCharTesters.FIX_TEXT);
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
