/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rulesystem.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import rulesystem.ruleinput.rulevalue.RuleInputDataType;

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

    public static final RuleInputDataType getRuleInputDataTypeFromName(String name) {
        switch (name.toLowerCase()) {
            case("number"):
                return RuleInputDataType.NUMBER;
            case("date"):
                return RuleInputDataType.DATE;
            case("string"):
                return RuleInputDataType.STRING;
            default:
                return null;
        }
    }
}
