package com.ryanlea.fix.chronicle.examples;

import com.ryanlea.fix.chronicle.Message;
import com.ryanlea.fix.chronicle.pool.MessagePool;
import com.ryanlea.fix.examples.fix44.MarketDataRequest;

public class SimpleMessagePool implements MessagePool {

    public Message acquire(String messageType) {
        if ("V".equals(messageType)) {
            return new MarketDataRequest();
        }
        throw new IllegalArgumentException("Unexpected messageType: " + messageType);
    }
}
