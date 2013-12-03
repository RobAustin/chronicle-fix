package com.ryanlea.fix.chronicle.examples;

import com.ryanlea.fix.chronicle.Message;
import com.ryanlea.fix.chronicle.pool.MessagePool;
import com.ryanlea.fix.chronicle.spec.FixSpec;
import com.ryanlea.fix.examples.fix44.Header;
import com.ryanlea.fix.examples.fix44.MarketDataRequest;
import com.ryanlea.fix.examples.fix44.Trailer;

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
