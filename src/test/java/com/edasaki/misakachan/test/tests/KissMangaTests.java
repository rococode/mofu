package com.edasaki.misakachan.test.tests;

import com.edasaki.misakachan.source.english.KissManga;
import com.edasaki.misakachan.test.annotations.TestClass;
import com.edasaki.misakachan.test.annotations.TestMethod;
import com.google.common.truth.Truth;

@TestClass(enabled = true)
public class KissMangaTests {

    private static final KissManga km = new KissManga();

    @TestMethod
    public void testMatches() {
        Truth.assertThat(km.match("http://kissmanga.com/Manga/Onepunch-Man")).isTrue();
        Truth.assertThat(km.match("http://kissmango.com/Manga/Onepunch-Man")).isFalse();
        Truth.assertThat(km.matchInfo("http://kissmanga.com/Manga/Onepunch-Man")).isTrue();
        Truth.assertThat(km.matchInfo("kissmanga/blah")).isFalse();
        Truth.assertThat(km.matchInfo("http://kissmanga.com/")).isFalse();
    }

    @TestMethod
    public void testFetchURL() {
        //        try {
        //            AntiAntiBotCloudFlare scraper = new ApacheHttpAntiAntibotCloudFlareFactory().createInstance();
        //            MTimer timer = new MTimer();
        //            String html = scraper.getUrl("http://kissmanga.com/Manga/Gekkan-Shojo-Nozaki-kun");
        //            timer.output("fetched page");
        //            //            M.debug(html);
        //            Document doc = Jsoup.parse(html);
        //            timer.reset();
        //            doc = Jsoup.parse(scraper.getUrl("http://kissmanga.com/Manga/Gekkan-Shojo-Nozaki-kun/Ch-083?id=334362"));
        //            doc = Jsoup.parse(scraper.getUrl("http://kissmanga.com/Manga/Gekkan-Shojo-Nozaki-kun/Ch-083?id=334362"));
        //            doc = Jsoup.parse(scraper.getUrl("http://kissmanga.com/Manga/Gekkan-Shojo-Nozaki-kun/Ch-083?id=334362"));
        //            doc = Jsoup.parse(scraper.getUrl("http://kissmanga.com/Manga/Gekkan-Shojo-Nozaki-kun/Ch-083?id=334362"));
        //            timer.output("single page");
        //            M.debug(doc);
        //            scraper.close();
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
        //        Series series = km.getSeries("http://kissmanga.com/Manga/Gekkan-Shojo-Nozaki-kun");
    }

}
