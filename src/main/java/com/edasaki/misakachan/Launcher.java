package com.edasaki.misakachan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import com.edasaki.misakachan.utils.MFileUtils;
import com.edasaki.misakachan.version.Version;

public class Launcher {

    public static final Version VERSION;

    static {
        VERSION = getCurrentVersion();
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        Misaka.update("Successfully loaded misakachan " + VERSION + "!");
        Misaka.initialize(VERSION);
    }

    private static Version getCurrentVersion() {
        File changelog = new File("CHANGELOG");
        if (!changelog.exists()) {
            try {
                File f = MFileUtils.getResourceAsFile("CHANGELOG");
                if (f.exists()) {
                    return getChangelogVersion(changelog);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return getChangelogVersion(changelog);
        }
        return Version.UNKNOWN;
    }

    private static Version getChangelogVersion(File f) {
        try (Scanner scan = new Scanner(f)) {
            String s = scan.nextLine();
            return Version.getVersion(s.substring(0, s.indexOf('-')).trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Version.UNKNOWN;
    }

}