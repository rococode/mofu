package com.edasaki.misakachan.multithread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MultiThreadTaskManager {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public static <T> Future<T> queueTask(Callable<T> callable) {
        return EXECUTOR.submit(callable);
    }

    public static <T> List<Future<T>> queueTasks(Collection<Callable<T>> callables) {
        List<Future<T>> futures = new ArrayList<Future<T>>();
        for (Callable<T> c : callables)
            futures.add(EXECUTOR.submit(c));
        return futures;
    }

    public static <T> boolean allReady(List<Future<T>> futures) {
        for (Future<T> f : futures)
            if (!f.isDone())
                return false;
        return true;
    }

    public static List<Document> getDocuments(Collection<String> urls) {
        List<Future<Document>> futures = new ArrayList<Future<Document>>();
        for (String pageURL : urls) {
            Callable<Document> c = () -> {
                Connection conn = Jsoup.connect(pageURL);
                return conn.get();
            };
            futures.add(MultiThreadTaskManager.queueTask(c));
        }
        while (!allReady(futures)) { // lock main thread until fully loaded
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<Document> docs = new ArrayList<Document>();
        for (Future<Document> f : futures) {
            try {
                docs.add(f.get(3, TimeUnit.SECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
        return docs;
    }
}
