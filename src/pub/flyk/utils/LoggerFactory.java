package pub.flyk.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import pub.flyk.client.Test;

public class LoggerFactory {
	static{
		String file = Test.class.getResource("/").getPath()+"logging.properties";
		try {
			InputStream in = new FileInputStream(file);
			LogManager.getLogManager().readConfiguration(in);
		} catch (Exception e) {
			
		}
	}

	public static Logger getLogger(Class<?> clazz) {
		return Logger.getLogger(clazz.getName());
	}
}
