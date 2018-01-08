package pub.flyk.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggerFactory {
	static{
		try {
			File file = new File("config/logger.properties");
			if (file.exists()) {
				InputStream in = new FileInputStream(file);
				LogManager.getLogManager().readConfiguration(in);
			}
		} catch (Exception e) {
			
		}
	}

	public static Logger getLogger(Class<?> clazz) {
		return Logger.getLogger(clazz.getName());
	}
}
