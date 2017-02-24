package com.edasaki.misakachan.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
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
import com.edasaki.misakachan.persistence.PersistenceManager;
import com.edasaki.misakachan.utils.logging.M;
import com.edasaki.misakachan.utils.logging.MTimer;
import com.edasaki.misakachan.web.FinishedCondition.FinishedResponseCondition;
import com.edasaki.misakachan.web.FinishedCondition.FinishedStringCondition;

import io.github.bonigarcia.wdm.PhantomJsDriverManager;

public final class WebAccessor {

    private static final Object PHANTOM_LOCK = new Object();

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String[] PHANTOMJS_ARGS = {
            "--ignore-ssl-errors=true",
            "--ssl-protocol=all",
    };
    private static final DesiredCapabilities DESIRED_CAPS = new DesiredCapabilities();
    private static final Object[][] PRELOAD_URLS = {
            {
                    "http://kissmanga.com/", //manga/yotsubato",
                    new FinishedCondition[] {
                            new FinishedStringCondition() {

                                @Override
                                public boolean finished(String res) {
                                    return res.contains("class=\"listing\"");
                                }
                            }
                    }
            },
    };
    private static PhantomJSDriver PHANTOM;
    // this is the default getpagesource result if the page isn't loaded  yet
    private static final String NOT_YET_LOADED_REGEX = "(?s)\\s*\\Q<html>\\E\\s*\\Q<head>\\E\\s*\\Q</head>\\E\\s*\\Q<body>\\E\\s*\\Q</body>\\E\\s*\\Q</html>\\E\\s*";
    // html and body opening tags shouldn't be closed - maybe they have attributes
    private static final String NOT_YET_LOADED_REGEX_EMPTY_BODY = "(?s).*\\Q<body\\E\\s*\\Q</body>\\E.*";
    private static final String WELL_FORMED_HTML = "(?s).*\\Q<html\\E.*\\Q<body\\E.*\\Q</body>\\E.*\\Q</html>\\E.*";
    // matches if the page is cloudflare protected
    private static final String[] CLOUDFLARE_REGEX = {
            "(?s).*\\Q<title>\\E\\s*\\QPlease wait 5 seconds...\\E\\s*\\Q</title>\\E.*"
    };
    private static final Map<String, String> COOKIES = new HashMap<String, String>();

    private static volatile boolean initialized = false;

    private static volatile boolean startedInitializing = false;

    public static void initialize() {
        initialized = false;
        startedInitializing = true;
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
            DESIRED_CAPS.setJavascriptEnabled(true);
            System.setProperty("phantomjs.page.settings.userAgent", USER_AGENT);
            DESIRED_CAPS.setCapability("phantomjs.page.settings.userAgent", USER_AGENT);
            DESIRED_CAPS.setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS, PHANTOMJS_ARGS);
            PHANTOM = new PhantomJSDriver(DESIRED_CAPS);
            timer2.outputAndReset("blah2");
            for (Object[] o : PRELOAD_URLS) {
                M.debug("PRELOADING " + o[0]);
                WebAccessor.getCookies((String) o[0], false);
                timer2.outputAndReset("Finished preloading " + o[0]);
            }
            Misaka.update("Finished initializing WebAccessor in " + timer.getTimeSeconds() + ".");
            initialized = true;
            return null;
        });
    }

    public static void appendCookies(JSONObject jo) {
        JSONArray arr = new JSONArray();
        for (Entry<String, String> e : COOKIES.entrySet()) {
            JSONObject tuple = new JSONObject();
            tuple.put("name", e.getKey());
            tuple.put("value", e.getValue());
            arr.put(tuple);
        }
        jo.put("extraCookies", arr);
    }

    private static void waitInitialize() {
        while (!initialized) {
            if (!startedInitializing) {
                initialize();
            }
            try {
                Thread.sleep(25L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static Connection getConnection(String url, Method method, AbstractExtra[] conditions) {
        Connection conn = Jsoup.connect(url);
        conn.method(method);
        conn.userAgent(USER_AGENT);
        conn.ignoreHttpErrors(true);
        conn.ignoreContentType(true);
        conn.timeout(10000);
        // add all loaded cookies
        conn.cookies(COOKIES);
        if (conditions != null) {
            for (AbstractExtra a : conditions) {
                if (a instanceof ExtraModifiers) {
                    conn = ((ExtraModifiers) a).modify(conn);
                }
            }
        }
        return conn;
    }

    public static Document postURL(String url, AbstractExtra... conditions) {
        return exec(url, Method.POST, conditions);
    }

    public static Document getURL(String url, AbstractExtra... conditions) {
        return exec(url, Method.GET, conditions);
    }

    private static interface Blah<E> {
        public E execute(Response response);

        public E execute(String src);
    }

    public static File download(String url, AbstractExtra... conditions) {
        return connectAndExecute(url, Method.GET, new Blah<File>() {
            @Override
            public File execute(Response response) {
                File f = null;
                try {
                    String fileName = url.replaceAll("\\W+", "") + System.nanoTime() + ".png";
                    f = new File(PersistenceManager.getCacheDir(), fileName);
                    f.deleteOnExit();
                    FileOutputStream out = new FileOutputStream(f);
                    out.write(response.bodyAsBytes());
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return f;
            }

            @Override
            public File execute(String src) {
                return null;
            }
        }, conditions);
    }

    public static Document exec(String url, Method method, AbstractExtra... conditions) {
        return connectAndExecute(url, method, new Blah<Document>() {

            @Override
            public Document execute(Response response) {
                try {
                    return response.parse();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Document execute(String src) {
                return null;
            }

        }, conditions);
    }

    public static <E> E connectAndExecute(String url, Method method, Blah<E> blah, AbstractExtra... conditions) {
        M.debug("WebAccessor: Getting " + url);
        Connection conn = getConnection(url, method, conditions);
        E res = null;
        try {
            Response response = conn.execute();
            if (response.statusCode() == 200) {
                //                M.edb("Success - Didn't need Selenium!");
                return blah.execute(response);
            } else if (response.statusCode() == 503) { // oh no, cloudflare!
                waitInitialize(); // maybe it'll be preloaded!
                conn = getConnection(url, method, conditions);
                response = conn.execute();
                if (response.statusCode() == 200) {
                    M.edb("it was preloaded!");
                    return blah.execute(response);
                } else {
                    // wasn't preloaded :(
                    M.edb("wasn't preloaded, oh well");
                    getCookies(url);
                    conn = getConnection(url, method, conditions);
                    response = conn.execute();
                    if (response.statusCode() == 200) {
                        M.edb("done!");
                        return blah.execute(response);
                    } else {
                        M.edb("FAILED ON " + url + ", code " + response.statusCode());
                    }
                }
            } else {
                Misaka.update("Unknown error.");
                M.debug("Unknown error.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally { // this is so ugly but whatevs :v)
            boolean success = true;
            if (conditions != null && res != null) {
                for (AbstractExtra ae : conditions) {
                    if (res instanceof String && ae instanceof FinishedStringCondition) {
                        success &= ((FinishedStringCondition) ae).finished((String) res);
                    } else if (res instanceof Response && ae instanceof FinishedResponseCondition) {
                        success &= ((FinishedResponseCondition) ae).finished((Response) res);
                    }
                }
            }
            if (!success) {
                res = getWithPhantom(url, blah, conditions);
            }
        }
        return null;
    }

    private static Map<String, String> getCookies(String url) {
        waitInitialize();
        return getCookies(url, true);
    }

    private static <E> E getWithPhantom(String url, Blah<E> exec, AbstractExtra... conditions) {
        synchronized (PHANTOM_LOCK) {
            M.edb("Getting with phantom: " + url);
            for (AbstractExtra ae : conditions) {
                if (ae instanceof ExtraModifiers) {
                    PHANTOM = ((ExtraModifiers) ae).modify(PHANTOM);
                }
            }
            //            PHANTOM.manage().
            try {
                PHANTOM.executeScript("document.removeChild(document.documentElement);");
                Thread.sleep(15L);
                PHANTOM.get(url);
                waitFullLoad(PHANTOM, null); // no conditions on first fetch
                String src = PHANTOM.getPageSource();
                int counter = 0;
                while (counter++ < 100) {
                    if (!isCloudflare(src)) { // probably shouldn't happen lul
                        src = waitFullLoad(PHANTOM, conditions);
                        return exec.execute(src);
                    } else {
                        //                        PHANTOM.manage().deleteAllCookies();
                        Thread.sleep(250L);
                        src = PHANTOM.getPageSource();
                        Set<Cookie> cookies = PHANTOM.manage().getCookies();
                        for (Cookie c : cookies) {
                            if (c.getName().contains("cf_clearance")) {
                                M.edb("FOUND COOKIES!");
                                PHANTOM.navigate().refresh();
                                while (isCloudflare(src = PHANTOM.getPageSource())) {
                                    Thread.sleep(25L);
                                }
                                src = waitFullLoad(PHANTOM, conditions); // only wait for conditions before return
                                return exec.execute(src);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static Map<String, String> getCookies(String url, boolean wait) {
        synchronized (PHANTOM_LOCK) {
            M.edb("Getting cookies for " + url);
            Map<String, String> map = new HashMap<String, String>();
            try {
                PHANTOM.executeScript("document.removeChild(document.documentElement);");
                Thread.sleep(15L);
                PHANTOM.get(url);
                waitFullLoad(PHANTOM, null); // no conditions on first fetch
                String src = PHANTOM.getPageSource();
                int counter = 0;
                while (counter++ < 100) {
                    if (!isCloudflare(src)) { // probably shouldn't happen lul
                        return map;
                    } else {
                        //                        PHANTOM.manage().deleteAllCookies();
                        Thread.sleep(250L);
                        src = PHANTOM.getPageSource();
                        Set<Cookie> cookies = PHANTOM.manage().getCookies();
                        for (Cookie c : cookies) {
                            if (c.getName().contains("cf_clearance")) {
                                M.edb("FOUND COOKIES!");
                                for (Cookie c2 : cookies) {
                                    map.put(c2.getName(), c2.getValue());
                                }
                                return map;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!map.isEmpty()) {
                    COOKIES.putAll(map);
                    M.debug("cookiemap: " + COOKIES);
                }
            }
            return map;
        }
    }

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

    private static String waitFullLoad(WebDriver driver, AbstractExtra[] conditions) {
        checkPendingRequests(driver);
        waitDOMLoad(driver);
        if (conditions != null) {
            int counter = 0;
            while (counter++ < 1000) {
                String src = driver.getPageSource();
                boolean done = true;
                for (AbstractExtra fc : conditions) {
                    if (fc instanceof FinishedCondition) {
                        done &= ((FinishedCondition) fc).finished(src);
                        if (!done) {
                            break;
                        }
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
        return "";
    }

    private static boolean isCloudflare(String src) {
        for (String s : CLOUDFLARE_REGEX) {
            if (src.matches(s)) {
                return true;
            }
        }
        return false;
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
