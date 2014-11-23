package org.ahuh.flickr.sorter.helper;

import java.util.Properties;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.PropertyConfigurator;

/**
 * Properties file Helper
 * @author Julien
 *
 */
public class PropertiesHelper {	
	
	public final static String VERSION_FILE_PATH = "Version.properties";
	public final static String APP_FILE_PATH = "FlickrSorter.properties";
		
    private static CompositeConfiguration conf;
    
    /**
     * Configure the logger file
     */
    public static void configureLogger() {    	
    	try {
    		Properties properties = ConfigurationConverter.getProperties(getSingleton());
			PropertyConfigurator.configure(properties);
		} catch (Exception e) {
			System.out.println(String.format("ERROR : impossible to initiliaze log4j with configuration file '%s'", APP_FILE_PATH));			
			e.printStackTrace();
		}
    }

    /**
     * Get Singleton
     * @return
     * @throws Exception
     */
    private static CompositeConfiguration getSingleton() throws Exception {    	
    	if (conf == null) {
    		conf = new CompositeConfiguration();
    		conf.addConfiguration(new PropertiesConfiguration(VERSION_FILE_PATH));
    		conf.addConfiguration(new PropertiesConfiguration(APP_FILE_PATH));
    	}
    	return conf;
    }
    
    
    /**
     * Reset configuration
     * @throws Exception
     */
    public static void resetConfiguration() throws Exception {
    	// Reset properties
    	resetProperties();
    	// Reset logger configuration
    	configureLogger();
    }
    
    /**
     * Reset Properties
     */
    public static void resetProperties() {
    	if (conf != null) {
    		conf = null;
    	}
    }
    
    /**
     * getVersion
     * @return
     * @throws Exception
     */
    public static String getVersion() throws Exception {
    	return getSingleton().getString("flickr.sorter.version");
    }
    
    /**
     * getReleaseDate
     * @return
     * @throws Exception
     */
    public static String getReleaseDate() throws Exception {
    	return getSingleton().getString("flickr.sorter.release.date");
    }
    
    /**
     * getAuthToken
     * @return
     * @throws Exception
     */
    public static String getAuthToken() throws Exception {
    	return getSingleton().getString("auth.token");
    }
    
    /**
     * getAuthTokenSecret
     * @return
     * @throws Exception
     */
    public static String getAuthTokenSecret() throws Exception {
    	return getSingleton().getString("auth.token.secret");
    }
    
    /**
     * Save authentication token to configuration file
     * @param token
     * @param tokenSecret
     * @throws Exception
     */
    public static void saveAuthToken(String token, String tokenSecret) throws Exception {
    	PropertiesConfiguration appConf = new PropertiesConfiguration(APP_FILE_PATH);
    	appConf.setProperty("auth.token", token);
    	appConf.setProperty("auth.token.secret", tokenSecret);
    	appConf.save();
    	
    	// Reset properties
    	resetProperties();
    }
}

