package com.edasaki.misakachan.test.tests;

import java.util.Set;

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

import io.github.bonigarcia.wdm.PhantomJsDriverManager;

@TestClass(enabled = true)
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
}
