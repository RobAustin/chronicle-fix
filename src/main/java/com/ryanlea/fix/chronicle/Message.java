package com.ryanlea.fix.chronicle;

import gnu.trove.map.TIntObjectMap;

public abstract class Message extends Fields {

    private Header header;

    private Trailer trailer;

    private TIntObjectMap<Group> groups;

    protected Group _group(int fid) {
        return groups.get(fid);
    }

}
