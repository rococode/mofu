package com.edasaki.misakachan.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import com.edasaki.misakachan.Misaka;
import com.edasaki.misakachan.persistence.options.Option;

public class OptionManager extends AbstractPersistentFileManager {

    private HashMap<Option, String> options = new HashMap<Option, String>();

    public OptionManager() {
        loadDefaultValues();
        readStoredOptions();
    }

    private void loadDefaultValues() {
        for (Option o : Option.values())
            options.put(o, o.getDefault());
    }

    private void readStoredOptions() {
        Scanner scan = Misaka.getInstance().persist.getFileScanner(MisakaFile.OPTIONS);
        if (scan != null) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                try {
                    Option opt = Option.valueOf(line.substring(0, line.indexOf(':')));
                    String val = line.substring(line.indexOf(':') + 1);
                    options.put(opt, val);
                } catch (Exception e) {
                    e.printStackTrace();
                    Misaka.error("Error on options file line: " + line);
                }
            }
        } else {
            File f = Misaka.instance().persist.saveFile(getFileType(), getSaveContents());
            Misaka.update("Created options save file at " + f.getPath());
        }
    }

    public String getOption(Option option) {
        if (!options.containsKey(option)) {
            return option.getDefault();
        }
        return options.get(option);
    }

    @Override
    public MisakaFile getFileType() {
        return MisakaFile.OPTIONS;
    }

    @Override
    protected List<String> getSaveContents() {
        List<String> res = new ArrayList<String>();
        for (Entry<Option, String> e : options.entrySet())
            res.add(e.getKey().name() + ":" + e.getValue());
        return res;
    }

}
