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
            props.load(Environment.class.getClassLoader().getResourceAsStream("environment.properties"));
        } catch (IOException ioe){
            throw new RuntimeException("Failed to load property file", ioe);
        }
    }

    public static String getBaseUrl(){
        return props.get("baseUrl").toString();
    }

}
