package com.edasaki.misakachan.test.tests;

import com.edasaki.misakachan.source.SearchAction;
import com.edasaki.misakachan.source.english.MangaHere;
import com.edasaki.misakachan.test.annotations.TestClass;
import com.edasaki.misakachan.test.annotations.TestMethod;
import com.edasaki.misakachan.utils.logging.M;

@TestClass(enabled = false)
public class SiteSearchTests {

    @TestMethod
    public void testMangaHereSearch() {
        SearchAction sa = new MangaHere().getSearch();
        M.debug(sa.search("yuru"));
        M.debug(sa.search("nozaki"));
    }

}
