package uk.ac.ox.cs.guardedqa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

    private static final String file = "config.properties";
    private static Properties prop = null;

    private static boolean debugMode;
    private static QAType qaType;

    private static synchronized void initialize() {

        FileInputStream inStream = null;
        if (Configuration.prop == null)
            try {
                Configuration.prop = new Properties();
                inStream = new FileInputStream(Configuration.file);
                Configuration.prop.load(inStream);
            } catch (final IOException e) {
                Configuration.prop = null;
            } finally {
                if (inStream != null)
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                if (Configuration.prop != null) {
                    debugMode = Configuration.prop.containsKey("debug")
                            ? Boolean.parseBoolean(Configuration.prop.getProperty("debug"))
                            : false;

                    
                    qaType = Configuration.prop.containsKey("debug")
                        ? qaType.valueOf(Configuration.prop.getProperty("guarded_qa.approach"))
                        : QAType.SAT;

                }
            }
    }

    public static boolean isDebugMode() {

        Configuration.initialize();

        return debugMode;
    }

    public static  QAType getQAType() {

        Configuration.initialize();

        return qaType;
    }

    
}
