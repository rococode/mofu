package com.edasaki.misakachan.web;

import org.jsoup.Connection;
import org.openqa.selenium.WebDriver;

public abstract class ExtraModifiers extends AbstractExtra {
    public abstract Connection modify(Connection conn);

    public abstract <E extends WebDriver> E modify(E driver);
}