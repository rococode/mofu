package com.edasaki.misakachan;

import java.io.IOException;
import java.net.URISyntaxException;

import com.edasaki.misakachan.version.Version;

public class Launcher {

	public static final Version VERSION = Version.getVersion("0.0.4");

	public static void main(String[] args) throws IOException, URISyntaxException {
		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");
		Misaka.initialize(VERSION);
	}

}