package com.edasaki.misakachan.test.tests;

import com.edasaki.misakachan.test.Test;
import com.google.common.truth.Truth;

public class TestTests {

    @Test
    public int alwaysWorks() {
        int x = 1;
        x--;
        return x;
    }

    @Test
    public void testGoogleTruth() {
        String string = "awesome";
        Truth.assertThat(string).startsWith("awe");
        Truth.assertWithMessage("Without me, it's just aweso").that(string).contains("me");
    }

}
