package com.edasaki.misakachan.persistence;

import java.util.List;

import com.edasaki.misakachan.Misaka;

public abstract class PersistentFileManager {
    public abstract MisakaFile getFileType();

    protected abstract List<String> getSaveContents();

    public void save() {
        MisakaFile mf = getFileType();
        List<String> contents = getSaveContents();
        Misaka.instance().persist.saveFile(mf, contents);
    }
}
