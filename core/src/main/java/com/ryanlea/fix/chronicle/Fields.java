package com.ryanlea.fix.chronicle;

import com.ryanlea.fix.chronicle.spec.FieldDefinition;
import gnu.trove.map.TIntCharMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntCharHashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.openhft.lang.io.Bytes;
import net.openhft.lang.io.MutableDecimal;
import net.openhft.lang.io.StopCharTester;
import net.openhft.lang.io.StopCharTesters;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;

public abstract class Fields {

    private TIntObjectMap<StringBuilder> strings;

    private TIntObjectMap<MutableDecimal> decimals;

    private TIntIntMap ints;

    private TIntObjectMap<MutableDateTime> dateTimes;

    private TIntCharMap chars;

    private TIntLongMap longs;

    private final StringBuilder timestamp = new StringBuilder();

    protected Fields(FieldDefinition[] fieldDefinitions) {
        // use the field definitions to make some guesses around sizes of the data types
        strings = new TIntObjectHashMap<>();
        decimals = new TIntObjectHashMap<>();
        ints = new TIntIntHashMap();
        dateTimes = new TIntObjectHashMap<>();
        chars = new TIntCharHashMap();
        longs = new TIntLongHashMap();
    }


    protected CharSequence _string(int fid) {
        return strings.get(fid);
    }

    protected Number _decimal(int fid) {
        return decimals.get(fid);
    }

    protected int _int(int fid) {
        return ints.get(fid);
    }

    protected ReadableDateTime _dateTime(int fid) {
        return dateTimes.get(fid);
    }

    protected char _char(int fid) {
        return chars.get(fid);
    }

    protected long _long(int fid) {
        return longs.get(fid);
    }


    public void parseString(int tag, Bytes bytes, StopCharTester stopCharTester) {
        StringBuilder builder = strings.get(tag);
        if (builder == null) {
            builder = new StringBuilder();
            strings.put(tag, builder);
        }
        bytes.parseUTF(builder, stopCharTester);
    }

    public void parseChar(int tag, Bytes bytes) {
        chars.put(tag, (char) bytes.readByte());
    }

    public void parseInt(int tag, Bytes bytes) {
        ints.put(tag, (int) bytes.parseLong());
    }

    public void parseLong(int tag, Bytes bytes) {
        longs.put(tag, bytes.parseLong());
    }

    public void parseDecimal(int tag, Bytes bytes) {
        MutableDecimal decimal = decimals.get(tag);
        if (decimal == null) {
            decimal = new MutableDecimal();
            decimals.put(tag, decimal);
        }
        bytes.parseDecimal(decimal);
    }

    public void parseUTCTimestamp(int tag, Bytes bytes) {
        MutableDateTime mutableDateTime = dateTimes.get(tag);
        if (mutableDateTime == null) {
            mutableDateTime = new MutableDateTime(DateTimeZone.UTC);
            dateTimes.put(tag, mutableDateTime);
        }

        // This entire method is a bit naff but I needed something.  I'm sure there's a much better way to parse a
        // UTC Timestamp that doesn't create loads of garbage - at 10pm, I'm not sure what that is
        int yyyy = parse(bytes, 4);
        int MM = parse(bytes, 2);
        int dd = parse(bytes, 2);
        char yearTimeSeparator = (char) bytes.readByte();
        int HH = parse(bytes, 2);
        char timeSeparator = (char) bytes.readByte();
        int mm = parse(bytes, 2);
        timeSeparator = (char) bytes.readByte();
        int ss = parse(bytes, 2);

        final char millisSeparator = (char) bytes.readByte();
        int SSS = 0;
        if (millisSeparator == '.') {
            SSS = parse(bytes, 3);
        }

        mutableDateTime.setYear(yyyy);
        mutableDateTime.setMonthOfYear(MM);
        mutableDateTime.setDayOfMonth(dd);
        mutableDateTime.setHourOfDay(HH);
        mutableDateTime.setMinuteOfHour(mm);
        mutableDateTime.setSecondOfMinute(ss);
        mutableDateTime.setMillisOfSecond(SSS);
    }

    private int parse(Bytes bytes, int length) {
        int result = 0;
        for (int i = 0; i < length; i++) {
            byte b = bytes.readByte();
            result = result * 10 + b - '0';
        }
        return result;
    }
}
