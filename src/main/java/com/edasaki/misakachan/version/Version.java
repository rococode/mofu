package com.edasaki.misakachan.version;

import java.util.HashMap;

public class Version implements Comparable<Version> {
    private static HashMap<String, Version> versions = new HashMap<String, Version>();

    private int major, minor, mini;

    public static final Version UNKNOWN = new Version("0.0.0");

    private Version(String s) {
        String[] data = s.split("\\.");
        major = Integer.parseInt(data[0].replaceAll("[^0-9]", ""));
        minor = Integer.parseInt(data[1].replaceAll("[^0-9]", ""));
        mini = Integer.parseInt(data[2].replaceAll("[^0-9]", ""));
    }

    public static Version getVersion(String s) {
        if (versions.containsKey(s)) {
            return versions.get(s);
        }
        Version ver = new Version(s);
        versions.put(s, ver);
        return ver;
    }

    @Override
    public String toString() {
        return "v" + major + "." + minor + "." + mini;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (!(other instanceof Version))
            return false;
        Version v = (Version) other;
        if (this.major == v.major && this.minor == v.minor && this.mini == v.mini)
            return true;
        return false;
    }

    @Override
    public int compareTo(Version other) {
        if (this.major == other.major && this.minor == other.minor && this.mini == other.mini)
            return 0;
        if (this.major < other.major)
            return -1;
        else if (this.major > other.major)
            return 1;
        if (this.major == other.major && this.minor < other.minor)
            return -1;
        else if (this.major == other.major && this.minor > other.minor)
            return 1;
        if (this.major == other.major && this.minor == other.minor && this.mini < other.mini)
            return -1;
        else if (this.major == other.major && this.minor == other.minor && this.mini > other.mini)
            return 1;
        return 0;
    }

}
