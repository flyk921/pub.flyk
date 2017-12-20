package pub.flyk.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggerFactory {
	static{
		try {
			String file = LoggerFactory.class.getResource("/").getPath()+"logging.properties";
			InputStream in = new FileInputStream(file);
			LogManager.getLogManager().readConfiguration(in);
		} catch (Exception e) {
			
		}
	}

	public static Logger getLogger(Class<?> clazz) {
		return Logger.getLogger(clazz.getName());
	}
}
