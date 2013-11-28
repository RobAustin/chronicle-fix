package com.ryanlea.fix.chronicle;

import org.joda.time.ReadableDateTime;

public abstract class Group {

    private Fields[] fields;

    private int length;

    protected int length() {
        return length;
    }

    protected CharSequence _string(int idx, int fid) {
        return fields[idx]._string(fid);
    }

    protected int _int(int idx, int fid) {
        return fields[idx]._int(fid);
    }

    protected char _char(int idx, int fid) {
        return fields[idx]._char(fid);
    }

    protected ReadableDateTime _dateTime(int idx, int fid) {
        return fields[idx]._dateTime(fid);
    }

    protected Number _decimal(int idx, int fid) {
        return fields[idx]._decimal(fid);
    }

}
