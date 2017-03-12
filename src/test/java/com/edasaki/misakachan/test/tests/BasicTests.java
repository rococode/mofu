package com.edasaki.misakachan.test.tests;

import com.edasaki.misakachan.test.annotations.TestClass;
import com.edasaki.misakachan.test.annotations.TestMethod;

import com.google.common.truth.Truth;

@TestClass(enabled = true)
public class BasicTests {

    @TestMethod
    public int alwaysWorks() {
        int x = 1;
        x--;
        return x;
    }

    @TestMethod
    public void testGoogleTruth() {
        String string = "awesome";
        Truth.assertThat(string).startsWith("awe");
        Truth.assertWithMessage("Without me, it's just aweso").that(string).contains("me");
    }

}
