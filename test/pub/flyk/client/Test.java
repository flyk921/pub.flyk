package pub.flyk.client;

import java.util.logging.Logger;

import pub.flyk.utils.LoggerFactory;
import pub.flyk.utils.StringUtil;


public class Test {
	private static Logger logger = LoggerFactory.getLogger(Test.class);
	public static void main(String[] args) {
		logger.severe("haha");
		logger.info("haha");
		logger.fine("haha");
		logger.finer("haha");
		logger.finest("haha");
		logger.warning("haha");
		logger.config("haha");
		logger.info("com.xyz.foo.level");
		StringUtil.getBytesUtf8("dd");
	}
}
