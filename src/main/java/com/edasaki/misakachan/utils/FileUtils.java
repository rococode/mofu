package com.edasaki.misakachan.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.edasaki.misakachan.Launcher;
import com.edasaki.misakachan.utils.logging.ELog;

import spark.utils.IOUtils;

public class FileUtils {

    public static File getResourceAsFile(String path) throws IOException {
        InputStream in = Launcher.class.getResourceAsStream(path);
        File tempFile = File.createTempFile("misaka-", ".tmp");
        tempFile.deleteOnExit();

        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy(in, out);
        out.close();

        ELog.debug("created temp file for " + path + " at " + tempFile.getPath());

        return tempFile;
    }

}
