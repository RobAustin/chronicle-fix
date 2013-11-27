package com.ryanlea.fix.chronicle;

import net.openhft.chronicle.*;
import net.openhft.lang.io.StopCharTester;
import net.openhft.lang.io.StopCharTesters;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;

public class FixParsingTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private Chronicle chronicle;

    @Before
    public void init() throws IOException {
        chronicle = new IndexedChronicle(folder.getRoot().getAbsolutePath() + File.separator + "fixparsing",
                ChronicleConfig.TEST);
        readFileIntoChronicle(chronicle, "/fixmessages.txt");
    }

    @Test
    public void parseMessages() throws IOException {
        ExcerptTailer tailer = chronicle.createTailer();

        while (tailer.nextIndex()) {
            while (tailer.remaining() > 0) {
                long tag = tailer.parseLong();
                String value = tailer.parseUTF(StopCharTesters.FIX_TEXT);
                System.out.print(tag + "=" + value + ",");
            }
            System.out.println();
        }
    }

    private void readFileIntoChronicle(Chronicle chronicle, String resource) throws IOException {
        ExcerptAppender appender = chronicle.createAppender();

        InputStream is = FixParsingTest.class.getResourceAsStream("/fixmessages.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            appender.startExcerpt(8191);
            appender.write(line.getBytes());
            appender.finish();
        }

        appender.close();
    }

}
