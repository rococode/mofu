package com.edasaki.misakachan.backend;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        startWeb();
        // testing eclipse git setup
        //        String url = "http://www.mangahere.co/manga/red_storm/c224/2.html";
        //        MangaHere mh = new MangaHere();
        //        if (mh.match(url)) {
        //            mh.getChapter(url);
        //        }
        //        Document doc = Jsoup.connect("http://www.mangahere.co/manga/red_storm/c224/2.html").get();
        //        Document doc = Jsoup.connect("http://dynasty-scans.com/chapters/how_to_make_a_love_letter#4").get();
        //        Elements newsHeadlines = doc.select("#mp-itn b a");
        //        System.out.println(doc);
        //        Element lastImg = doc.select("img").last();
        //        System.out.println(lastImg.absUrl("src"));
        //        Elements options = doc.select("select.wid60 > option");
        //        for (Element element : options) {
        //            System.out.println(element.text());
        //        }
    }

    public static void startWeb() throws IOException, URISyntaxException {
        Spark.port(10032);
        Spark.staticFileLocation("/public");
        Spark.get("/hello", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                return "Hsello Spark MVC Framework!<br/> test<font color=\"red\">test red</font>s";
            }
        });

        Spark.get("/goodbye", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                return "Goodbye Spark MVC Framework!";
            }
        });

        Spark.get("/parameter/:param", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                StringBuffer myParam = new StringBuffer(request.params(":param"));
                return "I reversed your param for ya \"" + myParam.reverse() + "\"";
            }
        });
        Spark.get("/hello", (req, res) -> "blah");
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("http://127.0.0.1:10032"));
        } else {
            System.exit(1);
        }
    }

}