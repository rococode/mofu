package com.edasaki.misakachan.test.tests;

import org.jsoup.nodes.Document;

import com.edasaki.misakachan.test.annotations.TestClass;
import com.edasaki.misakachan.test.annotations.TestMethod;
import com.edasaki.misakachan.utils.logging.M;
import com.edasaki.misakachan.utils.logging.MTimer;
import com.edasaki.misakachan.web.WebAccessor;

@TestClass(solo = true)
public class WebAccessorTests {

    @TestMethod
    public void testGet() {
        WebAccessor.preload();
        M.debug("Sleeping...");
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        M.debug("Starting...");
        MTimer timer = new MTimer();
        Document doc = WebAccessor.getURL("http://kissmanga.com/manga/yotsubato", (src) -> {
            //            System.out.println("checking " + src);
            return src.contains("class=\"listing\"");
        });
        M.debug(doc.select(".listing"));
        timer.output("WebAccessor done in: ");
        timer.reset();
        doc = WebAccessor.getURL("http://kissmanga.com/Manga/Gekkan-Shoujo-Nozaki-kun-Anthology");
        M.debug(doc.select(".listing"));
        timer.output("WebAccessor done in: ");
        timer.reset();
        doc = WebAccessor.getURL("http://kissmanga.com/Manga/Shinryaku-Ika-Musume");
        M.debug(doc.select(".listing"));
        timer.output("WebAccessor done in: ");
    }
}
