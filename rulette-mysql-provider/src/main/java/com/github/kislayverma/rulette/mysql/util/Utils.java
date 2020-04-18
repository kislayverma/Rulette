package com.github.kislayverma.rulette.mysql.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @author kislay
 */
public class Utils {

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

    public static HikariConfig getHikariConfig(String fileName) throws IOException {
        return getHikariConfig(Utils.readProperties(fileName));
    }

    public static HikariConfig getHikariConfig(Properties props) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(props.getProperty("driverClass"));
        hikariConfig.setJdbcUrl(props.getProperty("jdbcUrl"));
        hikariConfig.setUsername(props.getProperty("username"));
        hikariConfig.setPassword(props.getProperty("password"));
        hikariConfig.setMaximumPoolSize(new Integer((String) props.getProperty("maxPoolSize")));
        hikariConfig.setConnectionTimeout(new Long((String) props.getProperty("connectionTimeout")));

        return hikariConfig;
    }
}
