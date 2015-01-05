package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import controlling.RDEController;

public class PropertiesManager {
    
    protected static Logger logger = Logger.getLogger(RDEController.class
	    .getName());
    
    public static Properties loadProperties(String fileName) {
	File file = new File(fileName);
	Properties p = new Properties();
	if (file.exists()) {
	    try {
		FileReader fr = new FileReader(file);
		p.load(fr);
		fr.close();
	    } catch (final Exception ex) {
		ex.printStackTrace();
	    }
	} else {
	    logger.info("Created non existing \"" + fileName
		    + "\" with default values");
	    storeProperties(defaultRDEProperties(), fileName);
	}

	return p;
    }
    
    public static void storeProperties(Properties p, String fileName) {
	FileOutputStream fos;
	try {
	    fos = new FileOutputStream(fileName);
	    p.store(fos, "This is " + fileName);
	    fos.close();
	} catch (final FileNotFoundException e) {
	    e.printStackTrace();
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }
    
    private static Properties defaultRDEProperties() {
	Properties p = new Properties();
	p.setProperty("dimension", "0");
	p.setProperty("boundary_condition", "noflux");
	p.setProperty("time_step", "0.01");
	p.setProperty("spatial_step", "0.1");
	p.setProperty("x_dimension", "100");
	p.setProperty("y_dimension", "100");
	return p;
    }
    
    public static String getValue(Properties p, String key) {
    	return p.getProperty(key);
    }

}
