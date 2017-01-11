package com.edasaki.misakachan;

import com.edasaki.misakachan.gui.GuiManager;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.source.english.MangaHere;
import com.edasaki.misakachan.source.test.TestSource;
import com.edasaki.misakachan.spark.SparkManager;
import com.edasaki.misakachan.version.Version;

public class Misaka {

    private static final AbstractSource SOURCES[] = {
            new MangaHere(),
            new TestSource(),
    };

    private static Misaka instance;

    public GuiManager gui;
    public SparkManager spark;

    protected static void initialize(Version version) {
        Misaka m = new Misaka();

        m.gui = new GuiManager();
        m.gui.showFrame();

        m.updateStatus("Initializing webserver...");

        m.spark = new SparkManager(SOURCES);
        m.spark.startWebsever();

        m.updateStatus("Webserver successfully started.");
        m.updateStatus("Now listening on 0.0.0.0:" + m.spark.getPort() + "...");

        instance = m;
        // testing eclipse git setup
        //        String url = "http://www.mangahere.co/manga/red_storm/c224/2.html";
        //        MangaHere mh = new MangaHere();
        //        if (mh.match(url)) {
        //            mh.getChapter(url);
        //        }

    }

    public static Misaka getInstance() {
        return instance;
    }

    public void updateStatus(String s) {
        this.gui.updateStatus(s);
    }
}
