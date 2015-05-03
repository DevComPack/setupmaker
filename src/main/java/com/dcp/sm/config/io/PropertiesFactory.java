package com.dcp.sm.config.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Properties files class loader
 * @author Said
 */
public class PropertiesFactory
{
    private File propertiesFile;
    private Properties prop;
    
    
    public PropertiesFactory(String propertiesPath) throws IOException
    {
        this.prop = new Properties();
        this.propertiesFile = new File(propertiesPath);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesPath);           
        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("Property file '" + propertiesPath + "' not found in the classpath");
        }
    }

    public File getPropertiesFile()
    {
        return propertiesFile;
    }
    
    public String getProperty(String name)
    {
        return prop.getProperty(name);
    }
    
    public static void main(String[] args) throws IOException
    {
        PropertiesFactory test = new PropertiesFactory("app.properties");
        System.out.println(test.getProperty("version"));
    }
    
}
