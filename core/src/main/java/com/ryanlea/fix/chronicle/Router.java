package com.ryanlea.fix.chronicle;

import com.ryanlea.fix.chronicle.parser.MessageParser;
import com.ryanlea.fix.chronicle.spec.FixSpec;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ExcerptTailer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Router {

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final Chronicle chronicle;

    private final MessageParser messageParser;

    private final List<MessageHandler> messageHandlers = new ArrayList<>();

    private Router.ChronicleReader chronicleReader;

    public Router(Chronicle chronicle, MessageParser messageParser) {
        this.chronicle = chronicle;
        this.messageParser = messageParser;
    }

    public void registerMessageHandler(MessageHandler handler) {
        messageHandlers.add(handler);
    }

    public void start() throws IOException {
        chronicleReader = new ChronicleReader();
        executorService.execute(chronicleReader);
    }

    public void shutdown() {
        chronicleReader.running = false;
        executorService.shutdown();
    }

    private class ChronicleReader implements Runnable {

        private boolean running;

        private final ExcerptTailer tailer;

        private ChronicleReader() throws IOException {
            this.running = true;
            tailer = chronicle.createTailer();
        }

        @Override
        public void run() {
            while (running) {
                List<MessageHandler> messageHandlers = Router.this.messageHandlers;
                while (tailer.nextIndex()) {
                    Message message = messageParser.parse(tailer);
                    for (int i = 0; i < messageHandlers.size(); i++) {
                        messageHandlers.get(i).handle(message);
                    }
                }
            }
        }
    }
}
