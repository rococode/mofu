package com.edasaki.misakachan;

import java.util.ArrayList;
import java.util.List;

import com.edasaki.misakachan.changelog.ChangelogManager;
import com.edasaki.misakachan.gui.GuiManager;
import com.edasaki.misakachan.history.HistoryManager;
import com.edasaki.misakachan.persistence.OptionManager;
import com.edasaki.misakachan.persistence.PersistenceManager;
import com.edasaki.misakachan.scanlator.BakaUpdateManager;
import com.edasaki.misakachan.source.AbstractSource;
import com.edasaki.misakachan.source.english.KissManga;
import com.edasaki.misakachan.source.english.MangaHere;
import com.edasaki.misakachan.source.french.BleachMx;
import com.edasaki.misakachan.source.french.LireScan;
import com.edasaki.misakachan.version.Version;
import com.edasaki.misakachan.web.WebAccessor;
import com.edasaki.misakachan.web.spark.SparkManager;

public class Misaka {

    public static final AbstractSource[] SOURCES = {
            new MangaHere(),
            new KissManga(),
            new BleachMx(),
            new LireScan()
            //            new TestSource(),
    };

    private static final List<String> MESSAGE_QUEUE = new ArrayList<String>();

    private static Misaka instance;

    public GuiManager gui;
    public PersistenceManager persist;
    public OptionManager options;
    public BakaUpdateManager baka;
    public ChangelogManager changelog;
    public HistoryManager history;

    public SparkManager spark;

    protected static void initialize(Version version) {
        Misaka m = new Misaka();
        instance = m;
        m.init();
    }

    private void init() {
        WebAccessor.initialize();
        this.gui = new GuiManager();
        this.gui.showFrame();
        for (String s : MESSAGE_QUEUE)
            this.updateStatus(s);
        MESSAGE_QUEUE.clear();

        this.persist = new PersistenceManager();
        this.persist.loadFiles();

        this.options = new OptionManager();

        this.baka = new BakaUpdateManager();
        this.changelog = new ChangelogManager();

        this.history = new HistoryManager();

        // Do this last, since we want to be sure everything else is ready
        this.updateStatus("Initializing webserver...");
        this.spark = new SparkManager();
        this.spark.startWebserver();
        this.updateStatus("Webserver successfully started.");
        this.updateStatus("Now listening on 0.0.0.0:" + this.spark.getPort() + "...");
    }

    public static Misaka getInstance() {
        return instance;
    }

    public static Misaka instance() {
        return instance;
    }

    public static void update(String s) {
        if (instance != null)
            instance.updateStatus(s);
        else
            MESSAGE_QUEUE.add(s);
    }

    public static void error(String s) {
        update("[ERROR] " + s);
    }

    public void updateStatus(String s) {
        this.gui.updateStatus(s);
    }
}
