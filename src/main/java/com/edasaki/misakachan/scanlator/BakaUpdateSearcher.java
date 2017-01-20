package com.edasaki.misakachan.scanlator;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Scanner;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.edasaki.misakachan.utils.MStringUtils;
import com.edasaki.misakachan.utils.logging.M;

public class BakaUpdateSearcher {
    private static final String PREFIX = "http://www.google.com/search?q=";
    private static final String SUFFIX = "+site:mangaupdates.com";

    private static final String SELECTOR_REGEX = ".*\\Qurl?q=\\E.*\\Qmangaupdates.com/series.html\\E.*";
    private static final String SELECTOR = "a[href~=" + SELECTOR_REGEX + "]";
    private static final String LINK_REGEX = "\\Qhttp\\Es?\\Q://\\E(www)?.?\\Qmangaupdates.com\\E.*";

    private static final String CHARSET = "UTF-8";

    private static final String USER_AGENT = "misakachan (+http://misakachan.net)";

    protected Scanlator search(String title) {
        try {
            Thread.sleep((long) (Math.random() * 1000l));
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        Document doc;
        try {
            double bestSimilarity = 0;
            String bestTitle = null;
            String bestURL = null;
            final String connectionURL = PREFIX + URLEncoder.encode(title, CHARSET) + SUFFIX;
            Connection conn = Jsoup.connect(connectionURL);
            conn.userAgent(USER_AGENT).referrer("http://www.google.com").followRedirects(true);
            doc = conn.get();
            try {
                Thread.sleep((long) (Math.random() * 1000l));
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            Elements links = doc.select(SELECTOR);
            for (Element link : links) {
                String linkName = link.text();
                String linkHref = link.absUrl("href");
                linkHref = URLDecoder.decode(linkHref.substring(linkHref.indexOf('=') + 1, linkHref.indexOf('&')), CHARSET).trim();
                if (!linkHref.matches(LINK_REGEX))
                    continue;
                Document updateDoc = Jsoup.connect(linkHref).userAgent(USER_AGENT).get();
                Elements sCats = updateDoc.select(".sCat");
                for (Element e : sCats) {
                    if (e.text().equals("Associated Names")) {
                        Element assoc = e.nextElementSibling();
                        Scanner scan = new Scanner(Jsoup.clean(assoc.html(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false)));
                        while (scan.hasNextLine()) {
                            String s = scan.nextLine().trim();
                            if (s.length() == 0 || s.equals("N/A"))
                                continue;
                            double sim = MStringUtils.similarity(title, s);
                            if (sim > bestSimilarity) {
                                bestSimilarity = sim;
                                bestURL = linkHref;
                                bestTitle = linkName;
                            }
                        }
                        scan.close();
                        break;
                    }
                }
                double sim = MStringUtils.similarity(title, updateDoc.select(".releasestitle").text());
                if (sim > bestSimilarity) {
                    bestSimilarity = sim;
                    bestURL = linkHref;
                    bestTitle = linkName;
                }
                if (linkName.startsWith("Baka-Updates Manga - ")) {
                    sim = MStringUtils.similarity(title, linkName.substring("Baka-Updates Manga - ".length()));
                    if (sim > bestSimilarity) {
                        bestSimilarity = sim;
                        bestURL = linkHref;
                        bestTitle = linkName;
                    }
                }
            }
            M.debug("best result for " + title + ": " + bestTitle + " [" + bestURL + "] with a similarity of " + bestSimilarity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
