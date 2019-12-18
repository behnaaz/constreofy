package priority;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {
    private static String reducePath;
    private static Properties properties;
    private static Boolean debugEnabled;
    public static boolean debug() {
        if (debugEnabled != null) {
            return debugEnabled;
        }
        try {
            loadProperty();
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
        debugEnabled = Boolean.TRUE.toString().equalsIgnoreCase(properties.getProperty("debug"));
        return debugEnabled;
    }
    public static String reduceProgram() {
        if (StringUtils.isBlank(reducePath)) {
            try {
                loadProperty();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
            reducePath = properties.getProperty("reduce");
        }
        return reducePath;
    }

    private static void loadProperty() throws IOException {
        if (properties != null) {
            return;
        }

        InputStream is = PropertyReader.class.getClassLoader().getResourceAsStream("properties.yml");
        if (is == null) {
            throw new RuntimeException("Property file not read");
        }
        properties = new Properties();
        properties.load(is);
    }
}
