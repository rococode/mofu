package com.edasaki.misakachan.persistence;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.edasaki.misakachan.Misaka;

public class PersistenceManager {

    private static final String PATH = "./misaka";
    private File dir;
    private HashMap<MisakaFile, File> loadedFiles = new HashMap<MisakaFile, File>();

    public PersistenceManager() {
        dir = new File(PATH);
        if (!dir.exists())
            dir.mkdirs();
    }

    public void loadFiles() {
        for (File f : dir.listFiles()) {
            System.out.println("name: " + f.getName());
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
}
