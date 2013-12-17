package com.ryanlea.fix.chronicle.examples;

import com.ryanlea.fix.chronicle.Message;
import com.ryanlea.fix.chronicle.pool.MessagePool;
import com.ryanlea.fix.chronicle.spec.FixSpec;
import com.ryanlea.fix.examples.fix44.*;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * This is a an epically simplistic pool that doesn't reset messages.
 */
public class SimpleMessagePool implements MessagePool {

    private final FixSpec fixSpec;

    private final TIntObjectMap<Message> pool = new TIntObjectHashMap<Message>();

    public SimpleMessagePool(FixSpec fixSpec) {
        this.fixSpec = fixSpec;
    }

    public Message acquire(String messageType) {
        Message message = null;
        if (pool.containsKey(messageType.hashCode())) {
            return pool.get(messageType.hashCode());
        }

        if ("V".equals(messageType)) {
            message = new MarketDataRequest(
                    fixSpec.getMessageDefinition(messageType),
                    new Header(fixSpec.getHeaderDefinition()),
                    new Trailer(fixSpec.getTrailerDefinition()));
        } else if ("W".equals(messageType)) {
            message = new MarketDataSnapshotFullRefresh(
                    fixSpec.getMessageDefinition(messageType),
                    new Header(fixSpec.getHeaderDefinition()),
                    new Trailer(fixSpec.getTrailerDefinition()));
        } else if ("A".equals(messageType)) {
            message = new Logon(
                    fixSpec.getMessageDefinition(messageType),
                    new Header(fixSpec.getHeaderDefinition()),
                    new Trailer(fixSpec.getTrailerDefinition()));
        } else if ("D".equals(messageType)) {
            message = new NewOrderSingle(
                    fixSpec.getMessageDefinition(messageType),
                    new Header(fixSpec.getHeaderDefinition()),
                    new Trailer(fixSpec.getTrailerDefinition())
            );
        } else if ("0".equals(messageType)) {
            message = new Heartbeat(
                    fixSpec.getMessageDefinition(messageType),
                    new Header(fixSpec.getHeaderDefinition()),
                    new Trailer(fixSpec.getTrailerDefinition()));
        } else if ("F".equals(messageType)) {
            message = new SecurityStatus(
                    fixSpec.getMessageDefinition(messageType),
                    new Header(fixSpec.getHeaderDefinition()),
                    new Trailer(fixSpec.getTrailerDefinition()));
        }

        if (message == null) {
            throw new IllegalArgumentException("Unexpected messageType: " + messageType);
        }

//        pool.put(messageType.hashCode(), message);
        return message;
    }

    private Header acquireHeader() {
        return new Header(fixSpec.getHeaderDefinition());
    }

    private Trailer acquirTrailer() {
        return new Trailer(fixSpec.getTrailerDefinition());
    }
}
