package com.edasaki.misakachan.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.jsoup.nodes.Document;

import com.edasaki.misakachan.utils.logging.M;
import com.edasaki.misakachan.web.WebAccessor;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import spark.Spark;

public class MCache {

    private static boolean initialized = false;

    private static LoadingCache<String, Document> documentCache;

    private static LoadingCache<String, File> images;

    private synchronized static void initialize() {
        if (initialized) {
            return;
        }
        documentCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build(new CacheLoader<String, Document>() {
            public Document load(String url) {
                return WebAccessor.getURL(url);
            }
        });
        images = CacheBuilder.newBuilder().build(new CacheLoader<String, File>() {
            public File load(String url) {
                File f = WebAccessor.download(url);
                if (f == null) {
                    M.debug("Error: " + url + " downloaded null.");
                    try {
                        return File.createTempFile("FAILED-" + System.nanoTime(), ".png");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                return f;
            }
        });
        initialized = true;
    }

    static {
        initialize();
    }

    public static final Map<String, String> cachedURLToLocalImage = new HashMap<String, String>();

    public static boolean isCached(String url) {
        return cachedURLToLocalImage.containsKey(url);
    }

    public static String getCachedImage(String url) {
        return getCachedImage(url, "#");
    }

    public static String getCachedImage(String url, String defaultResult) {
        return cachedURLToLocalImage.getOrDefault(url, defaultResult);
    }

    public static void cacheImageToURL(String urlKey, String imageUrl) {
        if (!initialized) {
            initialize();
        }
        if (urlKey == null || imageUrl == null) {
            cachedURLToLocalImage.put(urlKey, "");
            cachedURLToLocalImage.put(imageUrl, "");
            return;
        }
        try {
            File f = images.get(imageUrl);
            if (f == null) {
                M.edb("ERROR: Null file for " + imageUrl);
                return;
            }
            String name = "cached-" + f.getName().replaceAll("[^a-zA-Z0-9]", "");
            final byte[] bytes = Files.readAllBytes(f.toPath());
            Spark.get("/" + name, (req, res) -> {
                HttpServletResponse raw = res.raw();
                raw.getOutputStream().write(bytes);
                raw.getOutputStream().flush();
                raw.getOutputStream().close();
                return res;
            });
            M.edb("Cached " + imageUrl + " to " + name);
            cachedURLToLocalImage.put(urlKey, name);
            cachedURLToLocalImage.put(imageUrl, name);
        } catch (Exception e) {
            M.edb("Failed to cache " + imageUrl);
            e.printStackTrace();
            cachedURLToLocalImage.put(urlKey, "");
            cachedURLToLocalImage.put(imageUrl, "");
        }
    }

    public static Document getDocument(String url) {
        if (!initialized) {
            initialize();
        }
        if (url == null) {
            return null;
        }
        try {
            return documentCache.get(url);
        } catch (ExecutionException e) {
            M.edb("Failed on " + url);
            e.printStackTrace();
        }
        return null;
    }
}
