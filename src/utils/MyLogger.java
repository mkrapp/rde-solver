package utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {

    public static Logger getLogger(String name, Level level) {
	Logger logger = Logger.getLogger(name);
	try {
	    FileHandler fh = new FileHandler("/tmp/" + name + ".log");
	    fh.setFormatter(new SimpleFormatter());
	    logger.addHandler(fh);
	} catch (SecurityException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	logger.setLevel(level);
	return logger;
    }

}
