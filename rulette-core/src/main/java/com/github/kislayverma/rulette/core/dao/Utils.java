package com.github.kislayverma.rulette.core.dao;

import com.github.kislayverma.rulette.core.ruleinput.value.InputDataType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author kislay
 */
public class Utils {

    /**
     * Read a properties file from the classpath and return a Properties object
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static Properties readProperties(String filename) throws IOException {
        Properties props = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(filename);
        props.load(stream);
        return props;
    }

    public static final InputDataType getRuleInputDataTypeFromName(String name) {
        switch (name.toLowerCase()) {
            case("number"):
                return InputDataType.NUMBER;
            case("date"):
                return InputDataType.DATE;
            case("string"):
                return InputDataType.STRING;
            default:
                return null;
        }
    }
}
