package com.ryanlea.fix.chronicle.examples;

import com.ryanlea.fix.chronicle.Message;
import com.ryanlea.fix.chronicle.MessageHandler;
import com.ryanlea.fix.chronicle.Router;
import com.ryanlea.fix.chronicle.parser.MessageParser;
import com.ryanlea.fix.chronicle.parser.impl.SimpleFIXTextMessageParser;
import com.ryanlea.fix.chronicle.pool.MessagePool;
import com.ryanlea.fix.chronicle.spec.FixSpec;
import com.ryanlea.fix.chronicle.spec.parser.FixSpecParser;
import com.ryanlea.fix.chronicle.spec.parser.impl.StaxFixSpecParser;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleConfig;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.IndexedChronicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class SimpleRouterMain {

    private static final String message = "8=FIX4.4\0019=478\00135=V\00149=client\00156=server\00134=1\00152=20080528-13:18:26\001262=request1\001263=1\00110=051\001";

    private static final Logger log = LoggerFactory.getLogger(SimpleRouterMain.class);

    private static final String basePath = System.getProperty("java.io.tmpdir") + File.separator + "simplerouter-" + System.currentTimeMillis();

    private static final String fixSpecResource = "/specs/fix44.spec.xml";

    private static final String fixMessages = "/messages/fix44.msgs";

    public static void main(String[] args) {
        try {
            Thread.sleep(10000);
            FixSpecParser fixSpecParser = new StaxFixSpecParser();
            FixSpec fixSpec = fixSpecParser.parse(SimpleRouterMain.class.getResourceAsStream(fixSpecResource));
            MessagePool messagePool = new SimpleMessagePool(fixSpec);
            Chronicle chronicle = new IndexedChronicle(basePath, ChronicleConfig.TEST);
            loadMessages(chronicle);
            MessageParser messageParser = new SimpleFIXTextMessageParser(fixSpec, messagePool);
            Router router = new Router(chronicle, messageParser);
            router.registerMessageHandler(new MessageHandler() {
                public void handle(Message message) {
                    log.info("Received message: {}", message);
                }
            });
            router.start();
            Thread.sleep(10000);
            router.shutdown();
        } catch (Throwable t) {
            log.error("Fatal error.", t);
        }
    }

    private static void loadMessages(Chronicle chronicle) throws IOException {
        InputStream is = SimpleRouterMain.class.getResourceAsStream(fixMessages);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ExcerptAppender appender = chronicle.createAppender();

        String line;
        while ((line = reader.readLine()) != null) {
            appender.startExcerpt(6144);
//            appender.write(message.getBytes());
            appender.write(line.getBytes());
            appender.finish();
        }

        appender.close();
        reader.close();
        is.close();
    }
}
