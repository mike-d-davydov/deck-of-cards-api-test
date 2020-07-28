package com.mikedd.env;

import java.io.IOException;
import java.util.Properties;

/**
 *
 */
public class Environment {
    static Properties props = new Properties();

    static {
        try {
            String propertyFileName = System.getProperty("configFile");
            System.out.println("File path is:" + propertyFileName);
            // So that it would work directly from IDEA too
            propertyFileName = propertyFileName == null ? "prod.properties" : propertyFileName;

            props.load(Environment.class.getClassLoader().getResourceAsStream(propertyFileName));
        } catch (IOException ioe){
            throw new RuntimeException("Failed to load property file", ioe);
        }
    }

    public static String getBaseUrl(){
        return props.get("baseUrl").toString();
    }

}
