package com.github.kislayverma.rulette.core.dao;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
}
