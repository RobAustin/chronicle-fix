package com.ryanlea.fix.chronicle;

import gnu.trove.map.TIntCharMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import net.openhft.lang.io.MutableDecimal;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableDateTime;

public abstract class Fields {

    private TIntObjectMap<StringBuilder> strings;

    private TIntObjectMap<MutableDecimal> decimals;

    private TIntIntMap ints;

    private TIntObjectMap<MutableDateTime> dateTimes;

    private TIntCharMap chars;

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

}
