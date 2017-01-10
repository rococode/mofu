package com.edasaki.misakachan;

import java.io.IOException;
import java.net.URISyntaxException;

import com.edasaki.misakachan.version.Version;

public class Launcher {

    private static final Version VERSION = Version.getVersion("0.0.4");

    public static void main(String[] args) throws IOException, URISyntaxException {
        Misaka.initialize(VERSION);
    }

}