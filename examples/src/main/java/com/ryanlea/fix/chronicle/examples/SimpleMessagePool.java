package com.ryanlea.fix.chronicle.examples;

import com.ryanlea.fix.chronicle.Message;
import com.ryanlea.fix.chronicle.pool.MessagePool;
import com.ryanlea.fix.chronicle.spec.FixSpec;
import com.ryanlea.fix.examples.fix42.*;

public class SimpleMessagePool implements MessagePool {

    private final FixSpec fixSpec;

    public SimpleMessagePool(FixSpec fixSpec) {
        this.fixSpec = fixSpec;
    }


    public Message acquire(String messageType) {
        if ("V".equals(messageType)) {
            return new MarketDataRequest(
                    fixSpec.getMessageDefinition(messageType),
                    new Header(fixSpec.getHeaderDefinition()),
                    new Trailer(fixSpec.getTrailerDefinition()));
        } else if ("W".equals(messageType)) {
            return new MarketDataSnapshotFullRefresh(
                    fixSpec.getMessageDefinition(messageType),
                    new Header(fixSpec.getHeaderDefinition()),
                    new Trailer(fixSpec.getTrailerDefinition()));
        } else if ("A".equals(messageType)) {
            return new Logon(
                    fixSpec.getMessageDefinition(messageType),
                    new Header(fixSpec.getHeaderDefinition()),
                    new Trailer(fixSpec.getTrailerDefinition()));
        } else if ("D".equals(messageType)) {
            return new NewOrderSingle(
                    fixSpec.getMessageDefinition(messageType),
                    new Header(fixSpec.getHeaderDefinition()),
                    new Trailer(fixSpec.getTrailerDefinition())
            );
        } else if ("0".equals(messageType)) {
            return new Heartbeat(
                    fixSpec.getMessageDefinition(messageType),
                    new Header(fixSpec.getHeaderDefinition()),
                    new Trailer(fixSpec.getTrailerDefinition()));
        } else if ("F".equals(messageType)) {
            return new SecurityStatus(
                    fixSpec.getMessageDefinition(messageType),
                    new Header(fixSpec.getHeaderDefinition()),
                    new Trailer(fixSpec.getTrailerDefinition()));
        }
        throw new IllegalArgumentException("Unexpected messageType: " + messageType);
    }

    private Header acquireHeader() {
        return new Header(fixSpec.getHeaderDefinition());
    }

    private Trailer acquirTrailer() {
        return new Trailer(fixSpec.getTrailerDefinition());
    }
}
