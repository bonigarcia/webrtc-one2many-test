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

import static org.kurento.commons.PropertiesManager.getProperty;

/**
 * Configuration.
 *
 * @author Boni Garcia (boni.garcia@urjc.es)
 * @since 1.0.0
 */
public class Configuration {

    public static final String APP_URL_PROP = "app.url";
    public static final String APP_URL_DEFAULT = "https://kurento.lab.fiware.org:8083/";
    public static final String NUM_VIEWERS_PROP = "num.viewers";
    public static final int NUM_VIEWERS_DEFAULT = 10;
    public static final String VIEWERS_RATE_PROP = "viewers.rate";
    public static final int VIEWERS_RATE_DEFAULT = 5; // seconds
    public static final String SESSION_TIME_PROP = "session.time";
    public static final int SESSION_TIME_DEFAULT = 5; // seconds
    public static final String OUTPUT_FOLDER_PROP = "output.folder";
    public static final String OUTPUT_FOLDER_DEFAULT = ".";
    public static final String NEW_TAB_TIMEOUT_PROP = "tab.timeout";
    public static final int NEW_TAB_TIMEOUT_DEFAULT = 10; // seconds

    public static String appUrl = getProperty(APP_URL_PROP, APP_URL_DEFAULT);
    public static int numViewers = getProperty(NUM_VIEWERS_PROP,
            NUM_VIEWERS_DEFAULT);
    public static int viewersRate = getProperty(VIEWERS_RATE_PROP,
            VIEWERS_RATE_DEFAULT);
    public static int sessionTime = getProperty(SESSION_TIME_PROP,
            SESSION_TIME_DEFAULT);
    public static String outputFolder = getProperty(OUTPUT_FOLDER_PROP,
            OUTPUT_FOLDER_DEFAULT);
    public static int newTabTimeout = getProperty(NEW_TAB_TIMEOUT_PROP,
            NEW_TAB_TIMEOUT_DEFAULT);

}
