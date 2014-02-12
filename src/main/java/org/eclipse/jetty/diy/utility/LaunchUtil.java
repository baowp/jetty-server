package org.eclipse.jetty.diy.utility;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import static org.apache.log4j.Logger.getLogger;

/**
 * Created with IntelliJ IDEA.
 * User: baowp
 * Date: 11/8/13
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class LaunchUtil {

    private static final Logger logger = getLogger(LaunchUtil.class);

    private static final String CONFIG_PROPERTIES = "launch.properties";
    private static volatile Properties PROPERTIES;


    public static String getProperty(String key) {
        String value = System.getProperty(key);
        if (value != null && value.length() > 0) {
            return value;
        }
        Properties properties = getProperties();
        return properties.getProperty(key);
    }

    public static Properties getProperties() {
        if (PROPERTIES == null) {
            synchronized (LaunchUtil.class) {
                if (PROPERTIES == null) {
                    PROPERTIES = loadProperties(CONFIG_PROPERTIES);
                }
            }
        }
        return PROPERTIES;
    }

    public static Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        if (fileName.startsWith("/")) {
            try {
                FileInputStream input = new FileInputStream(fileName);
                try {
                    properties.load(input);
                } finally {
                    input.close();
                }
            } catch (Throwable e) {
                logger.warn("Failed to load " + fileName + " file from " + fileName + "(ingore this file): " + e.getMessage(), e);
            }
            return properties;
        }


        List<URL> list = new ArrayList<URL>();
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(fileName);
            list = new ArrayList<URL>();
            while (urls.hasMoreElements()) {
                list.add(urls.nextElement());
            }
        } catch (Throwable t) {
            logger.warn("Fail to load " + fileName + " file: " + t.getMessage(), t);
        }

        if (list.size() == 0) {
            logger.warn("No " + fileName + " found on the class path.");
            return properties;
        }

        //if(! allowMultiFile) {
        if (list.size() > 1) {
            String errMsg = String.format("only 1 %s file is expected, but %d dubbo.properties files found on class path: %s",
                    fileName, list.size(), list.toString());
            logger.warn(errMsg);
            // throw new IllegalStateException(errMsg); // see http://code.alibabatech.com/jira/browse/DUBBO-133
        }

        // fall back to use method getResourceAsStream
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
        } catch (Throwable e) {
            logger.warn("Failed to load " + fileName + " file from " + fileName + "(ingore this file): " + e.getMessage(), e);
        }
        return properties;
        //}

       /* logger.info("load " + fileName + " properties file from " + list);

        for(java.net.URL url : list) {
            try {
                Properties p = new Properties();
                InputStream input = url.openStream();
                if (input != null) {
                    try {
                        p.load(input);
                        properties.putAll(p);
                    } finally {
                        try {
                            input.close();
                        } catch (Throwable t) {}
                    }
                }
            } catch (Throwable e) {
                logger.warn("Fail to load " + fileName + " file from " + url + "(ingore this file): " + e.getMessage(), e);
            }
        }

        return properties;*/
    }
}
