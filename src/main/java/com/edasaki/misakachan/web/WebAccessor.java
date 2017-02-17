package com.edasaki.misakachan.web;

import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.edasaki.misakachan.Misaka;
import com.edasaki.misakachan.multithread.MultiThreadTaskManager;
import com.edasaki.misakachan.utils.logging.M;
import com.edasaki.misakachan.utils.logging.MTimer;

import io.github.bonigarcia.wdm.PhantomJsDriverManager;

public final class WebAccessor {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String[] PHANTOMJS_ARGS = {
            "--ignore-ssl-errors=true",
            "--ssl-protocol=all",
    };
    private static final DesiredCapabilities dcaps = new DesiredCapabilities();
    private static PhantomJSDriver PHANTOM;
    private static final Object[][] PRELOAD = {
            {
                    "http://kissmanga.com/manga/yotsubato",
                    new FinishedCondition[] {
                            (src) -> {
                                return src.contains("class=\"listing\"");
                            }
                    }
            },
    };

    public static void initialize() {
        MultiThreadTaskManager.queueTask(() -> {
            MTimer timer = new MTimer();
            MTimer timer2 = new MTimer();
            PhantomJsDriverManager.getInstance().setup();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    if (PHANTOM != null)
                        PHANTOM.quit();
                }
            });
            timer2.outputAndReset("blah");
            dcaps.setJavascriptEnabled(true);
            System.setProperty("phantomjs.page.settings.userAgent", USER_AGENT);
            dcaps.setCapability("phantomjs.page.settings.userAgent", USER_AGENT);
            dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS, PHANTOMJS_ARGS);
            PHANTOM = new PhantomJSDriver(dcaps);
            timer2.outputAndReset("blah2");
            for (Object[] o : PRELOAD) {
                M.debug("PRELOADING " + o[0]);
                WebAccessor.getURL((String) o[0], (FinishedCondition[]) o[1]);
                timer2.outputAndReset("Finished preloading " + o[0]);
            }
            Misaka.update("Finished initializing WebAccessor in " + timer.getTimeSeconds() + ".");
            return null;
        });
    }

    private static final String NOT_YET_LOADED_REGEX = "(?s)\\s*\\Q<html>\\E\\s*\\Q<head>\\E\\s*\\Q</head>\\E\\s*\\Q<body>\\E\\s*\\Q</body>\\E\\s*\\Q</html>\\E\\s*";
    private static final String NOT_YET_LOADED_REGEX_EMPTY_BODY = "(?s).*\\Q<body>\\E\\s*\\Q</body>\\E.*";
    //html and body opening tags shouldn't be closed - maybe they have attributes
    private static final String WELL_FORMED_HTML = "(?s).*\\Q<html\\E.*\\Q<body\\E.*\\Q</body>\\E.*\\Q</html>\\E.*";

    private static final String[] CLOUDFLARE_REGEX = {
            "(?s).*\\Q<title>\\E\\s*\\QPlease wait 5 seconds...\\E\\s*\\Q</title>\\E.*"
    };

    private static void waitDOMLoad(WebDriver driver) {
        int counter = 0;
        while (counter++ < 100) { // 10 seconds
            String src = driver.getPageSource();
            if (src.matches(WELL_FORMED_HTML) && !src.matches(NOT_YET_LOADED_REGEX) && !src.matches(NOT_YET_LOADED_REGEX_EMPTY_BODY)) {
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        M.debug("too long lol");
    }

    private static String waitFullLoad(WebDriver driver, FinishedCondition... conditions) {
        checkPendingRequests(driver);
        waitDOMLoad(driver);
        if (conditions != null) {
            int counter = 0;
            while (counter++ < 1000) {
                String src = driver.getPageSource();
                boolean done = true;
                for (FinishedCondition fc : conditions) {
                    done &= fc.finished(src);
                    if (!done) {
                        break;
                    }
                }
                if (done) {
                    return driver.getPageSource();
                } else {
                    try {
                        Thread.sleep(25L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            return driver.getPageSource();
        }
        return null;
    }

    private static boolean isCloudflare(String src) {
        for (String s : CLOUDFLARE_REGEX) {
            if (src.matches(s)) {
                return true;
            }
        }
        return false;
    }

    // TODO: Make this pull from a pool of phantoms rather than just one, and make it block in the method, not synchronized
    public synchronized static Document getURL(String url, FinishedCondition... conditions) {
        M.debug("WebAccessor: Getting " + url);
        try {
            PHANTOM.executeScript("document.removeChild(document.documentElement);");
            PHANTOM.get(url);
            waitFullLoad(PHANTOM); // no conditions on first fetch
            String src = PHANTOM.getPageSource();
            int counter = 0;
            while (counter++ < 100) {
                if (!isCloudflare(src)) {
                    waitFullLoad(PHANTOM, conditions); // only wait for conditions before return
                    return Jsoup.parse(src);
                } else {
                    Thread.sleep(250L);
                    src = PHANTOM.getPageSource();
                    Set<Cookie> cookies = PHANTOM.manage().getCookies();
                    for (Cookie c : cookies) {
                        if (c.getName().contains("cf_clearance")) {
                            //                            M.edb("REFRESHING");
                            PHANTOM.navigate().refresh();
                            while (isCloudflare(src = PHANTOM.getPageSource())) {
                                Thread.sleep(25L);
                            }
                            src = waitFullLoad(PHANTOM, conditions); // only wait for conditions before return
                            return Jsoup.parse(src);
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void checkPendingRequests(WebDriver driver) {
        int timeoutInSeconds = 5;
        try {
            if (driver instanceof JavascriptExecutor) {
                JavascriptExecutor jsDriver = (JavascriptExecutor) driver;
                for (int i = 0; i < timeoutInSeconds * 10; i++) {
                    Object numberOfAjaxConnections = jsDriver.executeScript("return window.openHTTPs");
                    // return should be a number
                    if (numberOfAjaxConnections instanceof Long) {
                        Long n = (Long) numberOfAjaxConnections;
                        //                        System.out.println("Number of active calls: " + n);
                        if (n.longValue() == 0L)
                            break;
                    } else {
                        // If it's not a number, the page might have been freshly loaded indicating the monkey
                        // patch is replaced or we haven't yet done the patch.
                        monkeyPatchXMLHttpRequest(driver);
                    }
                    Thread.sleep(25L);
                }
            } else {
                System.out.println("Web driver: " + driver + " cannot execute javascript");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void monkeyPatchXMLHttpRequest(WebDriver driver) {
        try {
            if (driver instanceof JavascriptExecutor) {
                JavascriptExecutor jsDriver = (JavascriptExecutor) driver;
                Object numberOfAjaxConnections = jsDriver.executeScript("return window.openHTTPs");
                if (numberOfAjaxConnections instanceof Long) {
                    return;
                }
                String script = "  (function() {" + "var oldOpen = XMLHttpRequest.prototype.open;" + "window.openHTTPs = 0;" + "XMLHttpRequest.prototype.open = function(method, url, async, user, pass) {" + "window.openHTTPs++;" + "this.addEventListener('readystatechange', function() {" + "if(this.readyState == 4) {" + "window.openHTTPs--;" + "}" + "}, false);" + "oldOpen.call(this, method, url, async, user, pass);" + "}" + "})();";
                jsDriver.executeScript(script);
            } else {
                //                System.out.println("Web driver: " + driver + " cannot execute javascript");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
