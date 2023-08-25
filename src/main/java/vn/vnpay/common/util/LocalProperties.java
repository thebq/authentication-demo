package vn.vnpay.common.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author thebq
 * Created: 13/08/2023
 */
public class LocalProperties {
    static FileReader reader;
    static Properties properties = new Properties();

    static {
        try {
            reader = new FileReader("src/main/resources/config.properties");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object get(String key) throws IOException {
        properties.load(reader);
        return properties.getProperty(key);
    }
}
