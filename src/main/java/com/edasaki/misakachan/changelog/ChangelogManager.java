package com.edasaki.misakachan.changelog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.edasaki.misakachan.utils.logging.M;

public class ChangelogManager {

    private static final String ChANGELOG_URL = "https://raw.githubusercontent.com/edasaki/misakachan/master/CHANGELOG";

    private static final List<String> lines = new ArrayList<String>();
    private static boolean finished = false;

    static {
        new Thread(() -> {
            // might as well put it on a separate thread
            fetchChangelog();
        }).start();
    }

    private synchronized static final void fetchChangelog() {
        if (!lines.isEmpty()) {
            return;
        }
        M.debug("Getting changelog from URL.");
        try {
            URL url = new URL(ChANGELOG_URL);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lines.isEmpty()) {
            lines.add("0.0.0"); // in case of error fetching
        }
        M.debug("Finished getting changelog.");
        finished = true;
    }

    public synchronized List<String> getChangelog() {
        while (!finished) { // shouldn't ever happen, but just in case
            try {
                Thread.sleep(20L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

}
