package com.edasaki.misakachan.scanlator;

public class ScanGroup {
    private int id;
    private String name;

    public ScanGroup(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof ScanGroup))
            return false;
        if (other == this)
            return true;
        return this.id == ((ScanGroup) other).id;
    }

    @Override
    public String toString() {
        return "GROUP:" + name;
    }

}
