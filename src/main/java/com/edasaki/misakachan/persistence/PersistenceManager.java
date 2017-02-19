package com.edasaki.misakachan.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

import com.edasaki.misakachan.Misaka;
import com.edasaki.misakachan.manga.MangaPage;
import com.edasaki.misakachan.multithread.MultiThreadTaskManager;
import com.edasaki.misakachan.utils.logging.M;

public class PersistenceManager {

    private static final String PATH = "./misaka";
    private File dir;
    private HashMap<MisakaFile, File> loadedFiles = new HashMap<MisakaFile, File>();

    public PersistenceManager() {
        dir = new File(PATH);
        if (!dir.exists())
            dir.mkdirs();
    }

    public void get() {

    }

    public void loadFiles() {
        for (File f : dir.listFiles()) {
            if (f.isDirectory())
                continue;
            String name = f.getName();
            try {
                name = name.toUpperCase();
                name = name.substring(0, name.lastIndexOf('.'));
                MisakaFile mf = MisakaFile.valueOf(name);
                loadedFiles.put(mf, f);
            } catch (Exception e) {
                e.printStackTrace();
                Misaka.update("Unrecognized persistent file " + name + ".");
            }
        }
    }

    protected File getFile(MisakaFile file) {
        if (!loadedFiles.containsKey(file)) {
            Misaka.update("Could not find persistent file " + file.toString() + ".");
            return null;
        }
        return loadedFiles.get(file);
    }

    protected Scanner getFileScanner(MisakaFile mf) {
        File f = getFile(mf);
        if (f == null)
            return null;
        try {
            return new Scanner(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Misaka.error("Could not find file " + mf.name());
        }
        return null;
    }

    protected File saveFile(MisakaFile mf, List<String> fileContents) {
        File f = new File(PATH, mf.name().toLowerCase() + ".txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Misaka.error("Failed to create file " + mf.name());
            }
        }
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)))) {
            for (String s : fileContents)
                out.println(s);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Misaka.error("Failed to write to file " + mf.name());
        }
        return f;
    }

    public void saveChapter(String title, String source, String chapterNumber, List<MangaPage> pages) {
        Object[][] pagesAsObjArr = new Object[pages.size()][2];
        for (int k = 0; k < pagesAsObjArr.length; k++) {
            MangaPage page = pages.get(k);
            pagesAsObjArr[k][0] = page.getPageNumber();
            pagesAsObjArr[k][1] = page.getURL();
        }
        saveChapter(title, source, chapterNumber, pagesAsObjArr);
    }

    public void saveChapter(String title, String source, String chapterNumber, Object[][] pages) {
        String url = Misaka.instance().baka.getURL(title);
        M.debug("got " + url);
        int index = 0;
        if (!(chapterNumber.startsWith("ch") || chapterNumber.startsWith("Ch"))) {
            chapterNumber = "Chapter " + chapterNumber;
        }
        File dir = new File(PATH + File.separator + "library" + File.separator + title + " - " + source + File.separator + chapterNumber);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        M.debug("Writing to dir " + dir);
        for (Object[] o : pages) {
            @SuppressWarnings("unused")
            int num = (int) o[0];
            String src = (String) o[1];
            String name = String.format("%03d", ++index);
            MultiThreadTaskManager.queueTask(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    try {
                        URL url = new URL(src);
                        M.debug(src);
                        String ext = src;
                        if (ext.contains("/")) {
                            ext = ext.substring(ext.lastIndexOf('/'));
                        }
                        if (ext.contains(".")) {
                            ext = ext.substring(ext.lastIndexOf("."));
                        }
                        if (ext.contains("?")) {
                            ext = ext.substring(0, ext.indexOf('?'));
                        }
                        if (ext.contains("&")) {
                            ext = ext.substring(0, ext.indexOf('&'));
                        }
                        if (!ext.startsWith(".")) {
                            ext = "." + ext;
                        }
                        if (ext.length() != 4 && ext.length() != 5) {
                            // extensions including . should be 4 or 5 chars
                            // .png, .jpg, .jpeg, .bmp, etc.
                            Misaka.update("Please report this to Misaka!");
                            Misaka.update("Unrecognized file extension in: " + src);
                            ext = ".png";
                        }
                        File saveFile = new File(dir, name + ext);
                        M.debug("Writing to " + saveFile);
                        InputStream in = new BufferedInputStream(url.openStream());
                        OutputStream out = new BufferedOutputStream(new FileOutputStream(saveFile));
                        for (int i; (i = in.read()) != -1;) {
                            out.write(i);
                        }
                        in.close();
                        out.close();
                        M.debug("Saved " + saveFile.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

            });
        }
    }
}
