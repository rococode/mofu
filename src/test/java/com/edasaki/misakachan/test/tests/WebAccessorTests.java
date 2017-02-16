package com.edasaki.misakachan.test.tests;

import org.jsoup.nodes.Document;

import com.edasaki.misakachan.test.annotations.TestClass;
import com.edasaki.misakachan.test.annotations.TestMethod;
import com.edasaki.misakachan.utils.logging.M;
import com.edasaki.misakachan.web.WebAccessor;

@TestClass(solo = true)
public class WebAccessorTests {

    @TestMethod
    public void testGet() {
        Document doc = WebAccessor.getURL("http://kissmanga.com/manga/yotsubato");
        M.debug(doc);
    }
}
