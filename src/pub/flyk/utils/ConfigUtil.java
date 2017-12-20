package pub.flyk.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSON;

public class ConfigUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
	
	private static final File clientFile = new File("config/client.conf");

	private static final File serverFile = new File("config/server.conf");

	private static final File userFile = new File("config/user.conf");
	
	private static Map<String, String> serverConfig;
	private static Map<String, String> clientConfig;
	private static Hashtable<String, String> userList;
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getServerConfig(){
		if (serverConfig == null) {
			logger.info("init serverConfig !");
			serverConfig = parse(serverFile,HashMap.class);
		}
		return serverConfig;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, String> getClientConfig(){
		if (clientConfig == null) {
			logger.info("init clientConfig !");
			clientConfig = parse(clientFile,HashMap.class);
			
		}
		return clientConfig;
	}
	public static boolean saveClientConfig(Map<String, String> clientConfig){
		if (clientConfig == null || clientConfig.isEmpty()) {
			logger.info("clientConfig is null or clientConfig is empty ,so it can't be saved !");
			return false;
		}
		try {
			String data = CommonUtil.null2String(JSON.toJSON(clientConfig));
			return FileUtil.saveFile(data, clientFile);
		} catch (Exception e) {
			logger.warning("save file failed ! " + e.getMessage());
			return false;
		}
	}
	public static boolean saveClientConfig(){
		return saveClientConfig(clientConfig);
	}
	public static Hashtable<String, String> getUserList(){
		if (userList == null) {
			logger.info("init userList !");
			userList = parse2Hashtable(userFile);
		}
		return userList;
	}
	private static Hashtable<String, String> parse2Hashtable(File file) {
		List<?> list = parse(userFile,List.class);
		Hashtable<String, String> table = (list == null || list.size() == 0) ? null : new Hashtable<String, String>(list.size());
		for (Object obj : list) {
			@SuppressWarnings("unchecked")
			Map<String, String> map = JSON.parseObject(CommonUtil.null2String(obj), HashMap.class);
			table.put(map.get("port"), map.get("key"));
		}
		return table;
	}
	private static <T> T parse(File file, Class<T> clazz) {
		if (file == null || clazz == null) {
			logger.info("parse file failed ! file is null or target class is null !");
			return null;
		}
		String data = CommonUtil.null2String(FileUtil.readFile(file));
		if (StringUtil.isBlank(data)) {
			logger.info("no content in the file");
			return null;
		}
		try {
			return JSON.parseObject(data, clazz);
		} catch (Exception e) {
			logger.warning(data + " parse into " + clazz.getName() + " failed");
			return null;
		}
	}
}
