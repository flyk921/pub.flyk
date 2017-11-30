package pub.flyk.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class ConfigUtil {
	private static final File clientFile = new File("client.conf");

	private static final File serverFile = new File("server.conf");

	private static final File userFile = new File("user.conf");
	
	private static Map<String, String> serverConfig;
	private static Map<String, String> clientConfig;
	private static Hashtable<String, String> userList;
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getServerConfig(){
		if (serverConfig == null) {
			serverConfig = parse(serverFile,HashMap.class);
		}
		return serverConfig;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, String> getClientConfig(){
		if (clientConfig == null) {
			clientConfig = parse(clientFile,HashMap.class);
			
		}
		return clientConfig;
	}
	@SuppressWarnings("unchecked")
	public static Hashtable<String, String> getUserList(){
		if (userList == null) {
			userList = parse(userFile,Hashtable.class);
		}
		return userList;
	}
	private static <T> T parse(File file, Class<T> clazz) {
		String data = CommonUtil.null2String(FileUtil.readFile(file));
		if ("".equals(data)) {
			return null;
		}
		try {
			return JSON.parseObject(data, clazz);
		} catch (Exception e) {
			return null;
		}
	}
}
