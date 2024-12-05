package com.tango.experiment.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ConfigUtils {
    private static Properties properties = new Properties();

    public ConfigUtils() {}

    public static String getValue(String key) {
        init();
        Object value = properties.get(key);
        if(value instanceof String) return (String) value;
        return null;
    }

    private static void init() {
        try (InputStream in = ConfigUtils.class.getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(in);
        } catch(IOException ex) {
            log.error("config.properties load error", ex);
        }
    }
}
