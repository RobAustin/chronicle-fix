package com.ryanlea.fix.chronicle.pool;

import com.ryanlea.fix.chronicle.Message;

public interface MessagePool {

    Message acquire(String messageType);

}
