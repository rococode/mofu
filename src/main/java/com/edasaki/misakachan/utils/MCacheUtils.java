package com.edasaki.misakachan.utils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class MCacheUtils {

    private static boolean initialized = false;

    private static LoadingCache<String, Document> documentCache;

    private synchronized static void initialize() {
        if (initialized) {
            return;
        }
        documentCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<String, Document>() {
            public Document load(String url) {
                try {
                    return Jsoup.connect(url).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        initialized = true;
    }

    static {
        initialize();
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
            e.printStackTrace();
        }
        return null;
    }
}
