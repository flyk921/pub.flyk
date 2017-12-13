package pub.flyk.client;

/**
 * client
 * @author flyk
 * @date 2017年12月13日
 */
public class Client extends Thread {
	
	private String serverHost = "";
	
	private String serverPort = "";
	
	private String password = "";
	
	private String proxyPort = "";

	public Client(String serverHost, String serverPort, String password, String proxyPort) {
		super();
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.password = password;
		this.proxyPort = proxyPort;
	}
	
	




}
