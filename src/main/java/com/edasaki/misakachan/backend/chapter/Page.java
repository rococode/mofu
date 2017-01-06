package com.edasaki.misakachan.backend.chapter;

public class Page {
    private int num; // page number, one-indexed
    private String url; // url of the page

    public Page(int num, String url) {
        this.num = num;
        this.url = url;
    }
}
