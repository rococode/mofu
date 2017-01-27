package com.edasaki.misakachan.changelog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChangelogManager {

    private static final String ChANGELOG_URL = "https://raw.githubusercontent.com/edasaki/misakachan/master/CHANGELOG";

    public List<String> getChangelog() {
        List<String> lines = new ArrayList<String>();
        try {
            URL url = new URL(ChANGELOG_URL);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
    
}
