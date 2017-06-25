/*
 * (C) Copyright 2017 Boni Garcia (http://bonigarcia.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.github.bonigarcia;

import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.kurento.commons.PropertiesManager.getProperty;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.kurento.test.base.BrowserTest;
import org.kurento.test.browser.Browser;
import org.kurento.test.browser.BrowserType;
import org.kurento.test.browser.WebPage;
import org.kurento.test.config.BrowserConfig;
import org.kurento.test.config.BrowserScope;
import org.kurento.test.config.TestScenario;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Table;

/**
 * One-to-many test.
 *
 * @author Boni Garcia (boni.garcia@urjc.es)
 * @since 1.0.0
 */
public class One2ManyTest extends BrowserTest<WebPage> {

    private final Logger log = LoggerFactory.getLogger(One2ManyTest.class);

    public static final String APP_URL_PROP = "app.url";
    public static final String APP_URL_DEFAULT = "https://kurento.lab.fiware.org:8083/";
    public static final String NUM_VIEWERS_PROP = "num.viewers";
    public static final int NUM_VIEWERS_DEFAULT = 5;
    public static final String VIEWERS_RATE_PROP = "viewers.rate";
    public static final int VIEWERS_RATE_DEFAULT = 1000; // ms
    public static final String SESSION_TIME_PROP = "session.time";
    public static final int SESSION_TIME_DEFAULT = 5; // seconds
    public static final String OUTPUT_FOLDER_PROP = "output.folder";
    public static final String OUTPUT_FOLDER_DEFAULT = ".";
    public static final String NEW_TAB_TIMEOUT_PROP = "tab.timeout";
    public static final int NEW_TAB_TIMEOUT_DEFAULT = 10; // seconds

    public static String appUrl = getProperty(APP_URL_PROP, APP_URL_DEFAULT);
    public int numViewers = getProperty(NUM_VIEWERS_PROP, NUM_VIEWERS_DEFAULT);
    public int viewersRate = getProperty(VIEWERS_RATE_PROP,
            VIEWERS_RATE_DEFAULT);
    public int sessionTime = getProperty(SESSION_TIME_PROP,
            SESSION_TIME_DEFAULT);
    public String outputFolder = getProperty(OUTPUT_FOLDER_PROP,
            OUTPUT_FOLDER_DEFAULT);
    public int newTabTimeout = getProperty(NEW_TAB_TIMEOUT_PROP,
            NEW_TAB_TIMEOUT_DEFAULT);

    private Robot robot;
    public Table<Integer, Integer, String> csvTable = null;

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        TestScenario test = new TestScenario();
        test.addBrowser(BrowserConfig.PRESENTER,
                new Browser.Builder().browserType(BrowserType.CHROME)
                        .scope(BrowserScope.LOCAL).url(appUrl).build());
        test.addBrowser(BrowserConfig.VIEWER,
                new Browser.Builder().browserType(BrowserType.CHROME)
                        .scope(BrowserScope.LOCAL).url(appUrl).build());
        return Arrays.asList(new Object[][] { { test } });
    }

    @Before
    public void setup() throws AWTException {
        robot = new Robot();
        if (!outputFolder.endsWith(File.separator)) {
            outputFolder += File.separator;
        }

        // Place browsers
        java.awt.Dimension screenSize = Toolkit.getDefaultToolkit()
                .getScreenSize();
        int width = (int) screenSize.getWidth() / 2;
        int height = (int) screenSize.getHeight();
        Dimension dimension = new Dimension(width, height);
        Point origin = new Point(0, 0);
        Point middle = new Point(width, 0);

        getPresenter().getBrowser().getWebDriver().manage().window()
                .setSize(dimension);
        getPresenter().getBrowser().getWebDriver().manage().window()
                .setPosition(origin);
        getViewer().getBrowser().getWebDriver().manage().window()
                .setSize(dimension);
        getViewer().getBrowser().getWebDriver().manage().window()
                .setPosition(middle);
    }

    @Test
    public void test() throws Exception {
        // Sync presenter and viewer time
        log.info(
                "Starting test. Synchronizing presenter and viewer ... please wait");
        WebPage[] browsers = { getPresenter(), getViewer() };
        String[] videoTags = { "video", "video" };
        String[] peerConnections = { "webRtcPeer.peerConnection",
                "webRtcPeer.peerConnection" };
        syncTimeForOcr(browsers, videoTags, peerConnections);

        getPresenter().getBrowser().getWebDriver()
                .findElement(By.id("presenter")).click();
        getPresenter().subscribeEvent("video", "playing");
        getPresenter().waitForEvent("playing");

        // Log in the browser console to identify presenter and viewer
        final WebDriver viewerDriver = getViewer().getBrowser().getWebDriver();
        getPresenter().getBrowser().getJs()
                .executeScript("console.log('*** PRESENTER ***')");
        getViewer().getBrowser().getJs()
                .executeScript("console.log('*** VIEWER ***')");
        setFocusOnViewerFirstTab(viewerDriver);

        // Open a new tab for every new viewer
        final CountDownLatch allTabsLatch = new CountDownLatch(numViewers);
        ExecutorService executor = Executors.newFixedThreadPool(numViewers);
        for (int i = 0; i < numViewers; i++) {
            final CountDownLatch oneTabLatch = new CountDownLatch(1);
            if (i != 0) {
                waitMilliSeconds(viewersRate);
            }

            final int index = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (index != 0) {
                            openNewTab(viewerDriver, index);
                        }

                        // Click on viewer button
                        viewerDriver.findElement(By.id("viewer")).click();

                        if (index == 0) {
                            getViewer().subscribeEvent("video", "playing");
                            getViewer().waitForEvent("playing");

                            // OCR for E2E latency
                            getPresenter().startOcr();
                            getViewer().startOcr();
                        }

                    } catch (Exception e) {
                        log.error("Exception in session {}", index, e);
                    } finally {
                        oneTabLatch.countDown();
                        allTabsLatch.countDown();
                    }
                }
            });
            oneTabLatch.await();
        }
        allTabsLatch.await();

        waitSeconds(sessionTime);

        executor.shutdown();

        // Get OCR results and statistics
        setFocusOnViewerFirstTab(viewerDriver);

        getPresenter().endOcr();
        getViewer().endOcr();

        Map<String, Map<String, Object>> presenterMap = getPresenter()
                .getOcrMap();
        Map<String, Map<String, Object>> viewerMap = getViewer().getOcrMap();

        log.debug("presenterMap size {}", presenterMap.size());
        log.debug("viewerMap size {}", viewerMap.size());

        csvTable = processOcrAndStats(presenterMap, viewerMap);

    }

    private void setFocusOnViewerFirstTab(WebDriver viewerDriver) {
        ArrayList<String> handlesList = new ArrayList<>(
                viewerDriver.getWindowHandles());
        viewerDriver.switchTo().window(handlesList.get(0));
    }

    private void openNewTab(WebDriver driver, int index) {
        log.debug("Openning viewer {} in new tab", index);

        // Send control-t to the GUI
        sendControlT();

        // Wait to the new tab to be opened
        WebDriverWait wait = new WebDriverWait(driver, newTabTimeout);
        wait.until(ExpectedConditions.numberOfWindowsToBe(index + 1));

        // Switch to new tab
        ArrayList<String> list = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(list.get(index));

        // Open other URL in second tab
        driver.get(appUrl);
        wait.until(ExpectedConditions.urlToBe(appUrl));
    }

    private void sendControlT() {
        // If Mac OS X, the key combination is CMD+t, otherwise is CONTROL+t
        int vkControl = IS_OS_MAC ? KeyEvent.VK_META : KeyEvent.VK_CONTROL;

        robot.keyPress(vkControl);
        robot.keyPress(KeyEvent.VK_T);
        robot.keyRelease(vkControl);
        robot.keyRelease(KeyEvent.VK_T);
    }

    @After
    public void writeCsv() throws IOException {
        if (csvTable != null) {
            String outputCsvFile = outputFolder
                    + this.getClass().getSimpleName() + ".csv";
            log.info("End of test, writing results in {}", outputCsvFile);
            writeCSV(outputCsvFile, csvTable);
        }
    }

}
