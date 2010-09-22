/**
 * 
 */
package de.devsurf.injection.guice.integrations.configuration;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.googlecode.rocoto.simpleconfig.SimpleConfigurationModule;

import de.devsurf.injection.guice.configuration.Configuration;
import de.devsurf.injection.guice.scanner.AnnotationListener;

@Singleton
public class ConfigurationListener implements AnnotationListener {
    private Logger _logger = Logger.getLogger(ConfigurationListener.class.getName());
    @Inject
    private SimpleConfigurationModule module;
    
    @Override
    public void found(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
        if (annotations.containsKey(Configuration.class.getName())) {
    	Configuration config = (Configuration) annotations.get(Configuration.class.getName());
    	URL url;
    	switch (config.pathType()) {
    	case FILE:
    	    module.addProperties(new File(config.path()));
    	    break;
    	case URL:
    	    try {
    		url = new URL(config.path());
    	    } catch (MalformedURLException e) {
    		_logger.log(Level.WARNING, "Ignoring Configuration " + config.name()
    			+ " in " + config.path() + ". It has an illegal URL-Format.", e);
    		return;
    	    }
    	    if (config.path().endsWith(".xml")) {
    		module.addXMLProperties(url);
    	    } else if (config.path().endsWith(".properties")) {
    		module.addProperties(url);
    	    } else {
    		_logger.log(Level.WARNING, "Ignoring Configuration " + config.name()
    			+ " in " + config.path()
    			+ ", because is doesn't end with .xml or .properties.");
    	    }
    	    break;
    	case CLASSPATH:
    	default:
    	    System.out.println(this.getClass().getResource("/."));
    	    url = this.getClass().getResource(config.path());
    	    if (url != null) {
    		try {
    		    module.addProperties(new File(url.toURI()));
    		} catch (URISyntaxException e) {
    		    _logger
    			.log(Level.WARNING, "Ignoring Configuration " + config.name()
    				+ " in " + config.path()
    				+ ". It has an illegal URL-Format.", e);
    		}
    	    } else {
    		_logger.log(Level.WARNING, "Ignoring Configuration " + config.name()
    			+ " in " + config.path()
    			+ ", because is couldn't be found in the Classpath.");
    	    }

    	    break;
    	}
        }
    }
}