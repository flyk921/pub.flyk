package pub.flyk.client;

import java.util.Map;
import java.util.logging.Logger;

import pub.flyk.net.LocalServer;
import pub.flyk.utils.CommonUtil;
import pub.flyk.utils.ConfigUtil;
import pub.flyk.utils.LoggerFactory;

/**
 * 
 * ClientService 
 * create and manage LocalServer
 * @author flyk
 * @date Dec 15, 2017
 * 
 */
public class ClientService {
	private static Logger logger = LoggerFactory.getLogger(ClientService.class);
	private String serverHost;
	private int serverPort;
	private String password;
	private int proxyPort;
	private LocalServer localServer = null;

	public ClientService(String serverHost, int serverPort, String password,
			int proxyPort) {
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.password = password;
		this.proxyPort = proxyPort;
	}

	public static void main(String[] args) {
		String serverHost;
		int serverPort;
		String password;
		int proxyPort;
		try {
			if (args.length == 4) {
				serverHost = args[0];
				serverPort = Integer.parseInt(args[1]);
				password = args[2];
				proxyPort = Integer.parseInt(args[3]);
			}else{
				Map<String, String> clientConfig = ConfigUtil.getClientConfig();
				serverHost = CommonUtil.null2String(clientConfig.get("serverHost"));
				serverPort = Integer.parseInt(CommonUtil.null2String(clientConfig.get("serverPort")));
				password = CommonUtil.null2String(clientConfig.get("password"));
				proxyPort = Integer.parseInt(CommonUtil.null2String(clientConfig.get("proxyPort")));
			}
			logger.info("serverHost : " + serverHost + ", serverPort : " + serverPort + ", password : " + password + ", proxyPort : " + proxyPort);
			new LocalServer(proxyPort, serverHost, serverPort, password, false).start();
		} catch (Exception e) {
			logger.warning("ClientService start failed : " + e.getMessage());
		}
	}
	

	public void service() {
		logger.info("serverHost : " + serverHost + ", serverPort : " + serverPort + ", password : " + password + ", proxyPort : " + proxyPort);
		localServer = new LocalServer(proxyPort, serverHost, serverPort, password, false);
		localServer.start();
	}
	
	public void close(){
		if (localServer != null) {
			localServer.close();
			localServer = null;
		}
	}

	public boolean isChanged(String serverHost, int serverPort, String password,int proxyPort) {
		return !(CommonUtil.null2String(this.serverHost).equals(serverHost) && CommonUtil.null2String(this.password).equals(password) && this.serverPort == serverPort && this.proxyPort ==proxyPort);
	}


}
