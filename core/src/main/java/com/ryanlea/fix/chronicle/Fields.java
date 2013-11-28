package com.ryanlea.fix.chronicle;

import gnu.trove.map.TIntCharMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import net.openhft.lang.io.Bytes;
import net.openhft.lang.io.MutableDecimal;
import net.openhft.lang.io.StopCharTester;
import net.openhft.lang.io.StopCharTesters;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;

public abstract class Fields {

    private TIntObjectMap<StringBuilder> strings;

    private TIntObjectMap<MutableDecimal> decimals;

    private TIntIntMap ints;

    private TIntObjectMap<MutableDateTime> dateTimes;

    private TIntCharMap chars;

    private TIntLongMap longs;

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
        chars.put(tag, bytes.readChar());
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
}
