package com.edasaki.misakachan.web;

import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.PhantomJsDriverManager;

public final class WebAccessor {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String[] PHANTOMJS_ARGS = { "--ignore-ssl-errors=true", "--ssl-protocol=all" };
    private static final DesiredCapabilities dcaps = new DesiredCapabilities();
    private static PhantomJSDriver PHANTOM;
    static {
        PhantomJsDriverManager.getInstance().setup();
        PHANTOM = new PhantomJSDriver(dcaps);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                PHANTOM.quit();
            }
        });
        System.setProperty("phantomjs.page.settings.userAgent", USER_AGENT);
        dcaps.setCapability("phantomjs.page.settings.userAgent", USER_AGENT);
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS, PHANTOMJS_ARGS);
    }
    private static final String NOT_YET_LOADED_REGEX = "(?s)\\s*\\Q<html>\\E\\s*\\Q<head>\\E\\s*\\Q</head>\\E\\s*\\Q<body>\\E\\s*\\Q</body>\\E\\s*\\Q</html>\\E\\s*";

    private static final String[] CLOUDFLARE_REGEX = {
            "(?s).*\\Q<title>\\E\\s*\\QPlease wait 5 seconds...\\E\\s*\\Q</title>\\E.*"
    };

    private static void waitToLoad(WebDriver driver) {
        int counter = 0;
        while (counter++ < 100) { // 10 seconds
            if (!driver.getPageSource().matches(NOT_YET_LOADED_REGEX)) {
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static Document getURL(String url) {
        WebDriver driver = null;
        try {
            driver = new PhantomJSDriver(dcaps);
            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, 20);
            ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
                }
            };
            wait.until(jsLoad);
            waitToLoad(driver);
            String src = driver.getPageSource();
            final String[] CLOUDFLARE_REGEX = {
                    "(?s).*\\Q<title>\\E\\s*\\QPlease wait 5 seconds...\\E\\s*\\Q</title>\\E.*" };
            boolean cloudflare = false;
            for (String regex : CLOUDFLARE_REGEX) {
                if (src.matches(regex)) {
                    cloudflare = true;
                    break;
                }
            }
            Set<Cookie> cookies = driver.manage().getCookies();
            if (cloudflare) {
                int counter = 0;
                while (counter++ < 80) {
                    Thread.sleep(500);
                    src = driver.getPageSource();
                    cloudflare = false;
                    for (String regex : CLOUDFLARE_REGEX) {
                        if (src.matches(regex)) {
                            cloudflare = true;
                            break;
                        }
                    }
                    cookies = driver.manage().getCookies();
                    boolean done = false;
                    for (Cookie c : cookies) {
                        if (c.getName().contains("cf_clearance")) {
                            done = true;
                            break;
                        }
                    }
                    if (done) {
                        driver.quit();
                        driver = new PhantomJSDriver(dcaps);
                        for (Cookie c : cookies) {
                            driver.manage().addCookie(c);
                        }
                        driver.get(url);
                        waitToLoad(driver);
                        src = driver.getPageSource();
                        driver.quit();
                        return Jsoup.parse(src);
                    }
                    if (!cloudflare) {
                        return Jsoup.parse(src);
                    }
                    counter++;
                }
            } else {
                return Jsoup.parse(src);
            }
        } catch (

        InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        //        Set<Cookie> cookies = driver.manage().getCookies();
        //        driver.close();
        //        driver.quit();
        //        driver = new PhantomJSDriver(dcaps);
        //        for (Cookie c : cookies) {
        //            System.out.println("Adding cookie: " + c.toString());
        //            driver.manage().addCookie(c);
        //        }
        //        driver.get(url);
        //        System.out.println(driver.getPageSource());
        //        driver.close();
        //        driver.quit();
        return null;
    }
//    public static Document getURL(String url) {
//        try {
//            PhantomJsDriverManager.getInstance().setup();
//            String userAgent = "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.41 Safari/535.1";
//            System.setProperty("phantomjs.page.settings.userAgent", userAgent);
//            DesiredCapabilities dcaps = new DesiredCapabilities();
//            String[] phantomJsArgs = { "--ignore-ssl-errors=true", "--ssl-protocol=all" };
//            dcaps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36");
//            dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS, phantomJsArgs);
//            PhantomJSDriver driver = new PhantomJSDriver(dcaps);
//            PHANTOM = driver;
//            PHANTOM.get(url);
//            WebDriverWait wait = new WebDriverWait(PHANTOM, 20);
//            ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
//                @Override
//                public Boolean apply(WebDriver driver) {
//                    return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
//                }
//            };
//            wait.until(jsLoad);
//            waitToLoad(PHANTOM);
//            String src = PHANTOM.getPageSource();
//            boolean cloudflare = false;
//            for (String regex : CLOUDFLARE_REGEX) {
//                if (src.matches(regex)) {
//                    cloudflare = true;
//                    break;
//                }
//            }
//            Set<Cookie> cookies = PHANTOM.manage().getCookies();
//            if (!cloudflare) {
//                return Jsoup.parse(src);
//            } else {
//                int counter = 0;
//                while (counter++ < 80) {
//                    Thread.sleep(500);
//                    src = PHANTOM.getPageSource();
//                    System.out.println("current source " + counter + src);
//                    cloudflare = false;
//                    for (String regex : CLOUDFLARE_REGEX) {
//                        if (src.matches(regex)) {
//                            cloudflare = true;
//                            break;
//                        }
//                    }
//                    cookies = PHANTOM.manage().getCookies();
//                    boolean done = false;
//                    for (Cookie c : cookies) {
//                        if (c.getName().contains("cf_clearance")) {
//                            done = true;
//                            M.debug("FOUND CLEARANCE");
//                            break;
//                        }
//                    }
//                    //                    PHANTOM.manage().deleteAllCookies();
//                    if (done) {
//                        PHANTOM.quit();
//                        PHANTOM = new PhantomJSDriver(DCAPS);
//                        for (Cookie c : cookies) {
//                            System.out.println("Adding cookie: " + c.toString());
//                            PHANTOM.manage().addCookie(c);
//                        }
//                        PHANTOM.get(url);
//                        waitToLoad(PHANTOM);
//                        src = PHANTOM.getPageSource();
//                        return Jsoup.parse(src);
//                    }
//                    if (!cloudflare) {
//                        return Jsoup.parse(src);
//                    }
//                }
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
