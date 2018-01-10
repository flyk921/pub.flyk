package pub.flyk.server;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import pub.flyk.net.LocalServer;
import pub.flyk.utils.CommonUtil;
import pub.flyk.utils.ConfigUtil;
import pub.flyk.utils.LoggerFactory;
import pub.flyk.utils.StringUtil;

/**
 * 
 * 
 * @author flyk
 * @date Dec 15, 2017
 * 
 */
public class ServerService {

	private static Logger logger = LoggerFactory.getLogger(ServerService.class);
	
	private File lockFile = null;
	
	private String proxyHost = null;

	private int proxyPort;
	

	public ServerService() {
		try {
			lockFile = new File("server.lock");
			Map<String, String> serverConfig = ConfigUtil.getServerConfig();
			proxyHost = CommonUtil.null2String(serverConfig.get("proxyHost"));
			proxyPort = Integer.parseInt(CommonUtil.null2String(serverConfig.get("proxyPort")));
		} catch (Exception e) {
			logger.warning("ServerService start failed : " + e.getMessage());
			throw new RuntimeException("ServerService start failed : " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		ServerService serverService = new ServerService();
		serverService.service();
	}
	
	public void service() {
		
		if (lockFile.exists()) {
			logger.info("server already started!");
			logger.info("if it do not work ,please delete " + lockFile.getAbsolutePath() + " file and restart");
			return;
		}
		
		try {
			lockFile.createNewFile();
			lockFile.deleteOnExit();
		} catch (IOException e) {
			logger.warning("lockFile create failed : " + e.getMessage());
		}
		
		logger.info("server is starting !");
		logger.info("proxyHost : " + proxyHost);
		logger.info("proxyPort : " + proxyPort);
		
		Hashtable<String, String> users = null; // users
		Hashtable<String, LocalServer> localServers = new Hashtable<String, LocalServer>(); // LocalServers
		
		while (true) {
			users = ConfigUtil.getUserList();
			if (users == null || users.size() == 0) {
				pause(5000l);
				continue;
			}
			Enumeration<String> localServersKeys = localServers.keys();
			while (localServersKeys.hasMoreElements()) {
				String port = (String) localServersKeys.nextElement();
				try {
					String key = CommonUtil.null2String(users.remove(port));
					if (StringUtil.isBlank(key)) {
						localServers.remove(port).close();
						logger.info("delete LocalServer port : "+ port);
					}
					//ignore key that modifed key 
				} catch (Exception e) {
					logger.warning("delete LocalServer failed ! port : "+ port + " : " + e.getMessage());
				}
			}
			Enumeration<String> usersKeys = users.keys();
			while (usersKeys.hasMoreElements()) {
				String port = (String) usersKeys.nextElement();
				try {
					String key = CommonUtil.null2String(users.remove(port));
					if (StringUtil.isBlank(key)) {
						logger.info("add LocalServer failed ! port : "+ port + ", key is blank!!");
					}else{
						LocalServer localServer = new LocalServer(Integer.parseInt(port), proxyHost, proxyPort, key, true);
						localServer.start();
						localServers.put(port, localServer);
						logger.info("add LocalServer port : " + port + " , key : " + key);
					}
				} catch (Exception e) {
					logger.warning("add LocalServer failed ! port : "+ port + " : " + e.getMessage());
				}
			}
		}
		
	}

	private void pause(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	
	

}
