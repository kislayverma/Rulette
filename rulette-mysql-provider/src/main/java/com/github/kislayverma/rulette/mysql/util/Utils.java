package com.github.kislayverma.rulette.mysql.util;

import com.zaxxer.hikari.HikariConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Utility file for reading config and translating them to different configurations.
 *
 * @author kislay
 */
public class Utils {
    private static final String PROPERTY_MYSQL_DRIVER_CLASS = "driverClass";
    private static final String PROPERTY_JDBC_URL = "jdbcUrl";
    private static final String PROPERTY_USER_NAME = "username";
    private static final String PROPERTY_PASSWORD = "password";
    private static final String PROPERTY_MAX_POOL_SIZE = "maxPoolSize";
    private static final String PROPERTY_CONN_TIMEOUT = "connectionTimeout";

    /**
     * Read a properties file from the class path and return a Properties object
     *
     * @param fileName file to read
     * @return Properties object loaded with properties from the given file
     * @throws IOException on file reading error
     */
    public static Properties readProperties(String fileName) throws IOException {
        File f = new File(fileName);
        if (!f.canRead()) {
            throw new IOException("Could not read the datasource file");
        }

        URL url = f.toURI().toURL();
        InputStream in = url.openStream();
        Properties props = new Properties();
        props.load(in);

        return props;
    }

    /**
     * This method build a {@link HikariConfig} object from the given properties file.
     * @param fileName A property file containing the Hikari configurations
     */
    public static HikariConfig getHikariConfig(String fileName) throws IOException {
        return getHikariConfig(Utils.readProperties(fileName));
    }

    /**
     * This method build a {@link HikariConfig} object from the given properties file.
     * @param props An {@link Properties} object encapsulating Hikari properties
     */
    public static HikariConfig getHikariConfig(Properties props) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(props.getProperty(PROPERTY_MYSQL_DRIVER_CLASS));
        hikariConfig.setJdbcUrl(props.getProperty(PROPERTY_JDBC_URL));
        hikariConfig.setUsername(props.getProperty(PROPERTY_USER_NAME));
        hikariConfig.setPassword(props.getProperty(PROPERTY_PASSWORD));
        hikariConfig.setMaximumPoolSize(Integer.parseInt(props.getProperty(PROPERTY_MAX_POOL_SIZE)));
        hikariConfig.setConnectionTimeout(Long.parseLong(props.getProperty(PROPERTY_CONN_TIMEOUT)));

        return hikariConfig;
    }
}
