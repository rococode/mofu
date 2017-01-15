package com.edasaki.misakachan.persistence;

public enum MisakaFile {
    OPTIONS("options")

    ;
    private String id;

    MisakaFile(String s) {
        this.id = s;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
