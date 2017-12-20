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
				serverHost = CommonUtil.null2String(args[0]);
				serverPort = Integer.parseInt(args[1]);
				password = CommonUtil.null2String(args[2]);
				proxyPort = Integer.parseInt(args[3]);
			}else{
				Map<String, String> clientConfig = ConfigUtil.getClientConfig();
				serverHost = CommonUtil.null2String(clientConfig.get("serverHost"));
				serverPort = Integer.parseInt(CommonUtil.null2String(clientConfig.get("serverPort")));
				password = CommonUtil.null2String(clientConfig.get("password"));
				proxyPort = Integer.parseInt(CommonUtil.null2String(clientConfig.get("proxyPort")));
			}
			ClientService clientService = new ClientService(serverHost,serverPort,password,proxyPort);
			clientService.service();
		} catch (Exception e) {
			logger.warning("ClientService start failed : " + e.getMessage());
		}
	}
	

	public void service() {
		localServer = new LocalServer(proxyPort, serverHost, serverPort, password);
		localServer.run();
	}
	
	public void close(){
		if (localServer != null) {
			localServer.close();
		}
	}

	public boolean isChanged(String serverHost, int serverPort, String password,int proxyPort) {
		return !(CommonUtil.null2String(this.serverHost).equals(serverHost) && CommonUtil.null2String(this.password).equals(password) && this.serverPort == serverPort && this.proxyPort ==proxyPort);
	}


}
