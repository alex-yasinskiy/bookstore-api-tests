package com.bookstore.utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = ConfigReader.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                throw new RuntimeException("Couldn't find application.properties within resources");
            }
            properties.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't read properties file", e);
        }
    }

    public static String getBaseUri() {
        String targetEnv = System.getProperty("env", properties.getProperty("env"));

        String targetKey = "base.uri." + targetEnv;

        String uri = properties.getProperty(targetKey);

        if (uri == null) {
            throw new RuntimeException("Could not find following environment: " + targetEnv);
        }

        return uri;
    }
}