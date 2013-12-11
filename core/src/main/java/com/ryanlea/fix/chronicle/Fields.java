package com.ryanlea.fix.chronicle;

import com.ryanlea.fix.chronicle.spec.FieldDefinition;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
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

    // tag -> string value
    private TIntObjectMap<StringBuilder> strings;

    // tag -> decimal value
    private TIntObjectMap<MutableDecimal> decimals;

    // tag -> int value
    private TIntIntMap ints;

    // tag -> date time value
    private TIntObjectMap<MutableDateTime> utcTimestamps;

    // tag -> char value
    private TIntCharMap chars;

    // tag -> long value
    private TIntLongMap longs;

    // tag -> boolean value
    private TIntObjectMap<Boolean> booleans;

    // group tag -> group
    private TIntObjectMap<Group> groups;

    // each fid within a component is mapped to said component
    private TIntObjectMap<Component> components;

    // whether a tag exists
    private TIntList fields;

    protected Fields(FieldDefinition[] fieldDefinitions) {
        // use the field definitions to make some guesses around sizes of the data types
        strings = new TIntObjectHashMap<>();
        decimals = new TIntObjectHashMap<>();
        ints = new TIntIntHashMap();
        utcTimestamps = new TIntObjectHashMap<>();
        chars = new TIntCharHashMap();
        longs = new TIntLongHashMap();
        booleans = new TIntObjectHashMap<>();
        groups = new TIntObjectHashMap<>();
        fields = new TIntArrayList();
        components = new TIntObjectHashMap<>();

        for (FieldDefinition fieldDefinition : fieldDefinitions) {
            switch (fieldDefinition.getType()) {
                case PRICE:
                case QTY:
                    decimals.put(fieldDefinition.getNumber(), new MutableDecimal());
                    break;
                case UTCTIMESTAMP:
                    utcTimestamps.put(fieldDefinition.getNumber(), new MutableDateTime(DateTimeZone.UTC));
                    break;
                case STRING:
                    strings.put(fieldDefinition.getNumber(), new StringBuilder());
                    break;
            }
        }
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
        return utcTimestamps.get(fid);
    }

    protected char _char(int fid) {
        return chars.get(fid);
    }

    protected long _long(int fid) {
        return longs.get(fid);
    }

    protected Group _group(int fid) {
        return groups.get(fid);
    }

    protected void _group(int fid, Group group) {
        groups.put(fid, group);
    }

    protected void _component(int fid, Component component) {
        components.put(fid, component);
    }

    public void parseString(int tag, Bytes bytes, StopCharTester stopCharTester) {
        StringBuilder builder = strings.get(tag);
        if (builder == null) {
            builder = new StringBuilder();
            strings.put(tag, builder);
        }
        bytes.parseUTF(builder, stopCharTester);
        fields.add(tag);
    }

    public void parseChar(int tag, Bytes bytes) {
        chars.put(tag, (char) bytes.readByte());
        fields.add(tag);
    }

    public void parseInt(int tag, Bytes bytes) {
        ints.put(tag, (int) bytes.parseLong());
        fields.add(tag);
    }

    public void parseLong(int tag, Bytes bytes) {
        longs.put(tag, bytes.parseLong());
        fields.add(tag);
    }

    public void parseDecimal(int tag, Bytes bytes) {
        MutableDecimal decimal = decimals.get(tag);
        if (decimal == null) {
            decimal = new MutableDecimal();
            decimals.put(tag, decimal);
        }
        bytes.parseDecimal(decimal);
        fields.add(tag);
    }

    public void parseUTCTimestamp(int tag, Bytes bytes) {
        MutableDateTime mutableDateTime = utcTimestamps.get(tag);
        if (mutableDateTime == null) {
            mutableDateTime = new MutableDateTime(DateTimeZone.UTC);
            utcTimestamps.put(tag, mutableDateTime);
        }

        // This entire method is a bit naff but I needed something.  I'm sure there's a much better way to parse a
        // UTC Timestamp - at 10pm, I'm not sure what that is
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

        fields.add(tag);
    }

    private int parse(Bytes bytes, int length) {
        int result = 0;
        for (int i = 0; i < length; i++) {
            byte b = bytes.readByte();
            result = result * 10 + b - '0';
        }
        return result;
    }

    public boolean exists(int tag) {
        return fields.contains(tag);
    }

    public Group getGroup(int tag) {
        return groups.get(tag);
    }

    public Component getComponent(int tag) {
        return components.get(tag);
    }

    public void parseBoolean(int tag, Bytes bytes) {
        booleans.put(tag, bytes.parseBoolean(StopCharTesters.FIX_TEXT));
    }
}
