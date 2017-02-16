package com.edasaki.misakachan.test.tests;

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

import com.edasaki.misakachan.test.annotations.TestClass;
import com.edasaki.misakachan.test.annotations.TestMethod;
import com.edasaki.misakachan.utils.logging.MTimer;

import io.github.bonigarcia.wdm.PhantomJsDriverManager;

@TestClass(solo = false)
public class ExperimentalTests {

    @TestMethod(enabled = false)
    public void testCloudflareBypass() throws Exception {
        String url = "http://kissmanga.com";
        System.out.println("test");
        PhantomJsDriverManager.getInstance().setup();
        String userAgent = "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.41 Safari/535.1";
        System.setProperty("phantomjs.page.settings.userAgent", userAgent);
        DesiredCapabilities dcaps = new DesiredCapabilities();
        String[] phantomJsArgs = { "--ignore-ssl-errors=true", "--ssl-protocol=all" };
        dcaps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36");
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS, phantomJsArgs);
        WebDriver driver = new PhantomJSDriver(dcaps);
        // And now use this to visit Google
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, 20);
        // wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
                } catch (Exception e) {
                    return true;
                }
            }
        };

        // wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
            }
        };
        boolean a = wait.until(jQueryLoad) && wait.until(jsLoad);
        System.out.println("res: " + a);
        Thread.sleep(7000L);
        System.out.println(driver.getPageSource());
        Set<Cookie> cookies = driver.manage().getCookies();
        driver.close();
        driver.quit();
        driver = new PhantomJSDriver(dcaps);
        for (Cookie c : cookies) {
            System.out.println("Adding cookie: " + c.toString());
            driver.manage().addCookie(c);
        }
        driver.get(url);
        Thread.sleep(7000L);
        System.out.println(driver.getPageSource());
        driver.close();
        driver.quit();
    }

    private static final String userAgent = "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.41 Safari/535.1";
    private static final String[] phantomJsArgs = { "--ignore-ssl-errors=true", "--ssl-protocol=all" };
    private static final DesiredCapabilities dcaps = new DesiredCapabilities();
    static {
        PhantomJsDriverManager.getInstance().setup();
        System.setProperty("phantomjs.page.settings.userAgent", userAgent);
        dcaps.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36");
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS, phantomJsArgs);
    }

    @TestMethod
    public static void getWithCloudflareBypass() {
        MTimer timer = new MTimer();
        System.out.println(getWithCloudflareBypassHelper());
        timer.output("Cloudflare bypass");
    }

    private static final String NOT_YET_LODAED_REGEX = "(?s)\\s*\\Q<html>\\E\\s*\\Q<head>\\E\\s*\\Q</head>\\E\\s*\\Q<body>\\E\\s*\\Q</body>\\E\\s*\\Q</html>\\E\\s*";

    public static void waitToLoad(WebDriver driver) {
        int counter = 0;
        while (counter++ < 20) {
            if (!driver.getPageSource().matches(NOT_YET_LODAED_REGEX)) {
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static Document getWithCloudflareBypassHelper() {
        String url = "http://kissmanga.com/Manga/Gekkan-Shojo-Nozaki-kun";
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
                System.out.println("didnt' match " + regex);
            }
            Set<Cookie> cookies = driver.manage().getCookies();
            for (Cookie c : cookies) {
                System.out.println(c);
            }
            System.out.println("cloudflare: " + cloudflare);
            if (cloudflare) {
                int counter = 0;
                while (true) {
                    Thread.sleep(500);
                    src = driver.getPageSource();
                    System.out.println("current source " + counter + src);
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
                            System.out.println("Adding cookie: " + c.toString());
                            driver.manage().addCookie(c);
                        }
                        driver.get(url);
                        waitToLoad(driver);
                        src = driver.getPageSource();
                        System.out.println(src);
                        driver.quit();
                        return Jsoup.parse(src);
                    }
                    if (!cloudflare) {
                        return Jsoup.parse(src);
                    }
                    counter++;
                    if (counter >= 20) {
                        try {
                            throw new TimeoutException("Failed to fetch Cloudflare-protected page.");
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
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
}
