package pub.flyk.net;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import pub.flyk.utils.LoggerFactory;

/**
 * 
 * @author flyk
 * @date Dec 15, 2017
 * 
 */
public class LocalServer extends Thread {
	private static Logger logger = LoggerFactory.getLogger(LocalServer.class);
	
	private int listenPort;
	private String serverHost;
	private int serverPort;
	private String password;
	
	private ServerSocket serverSocket = null;
	private boolean kill = false;
	private boolean isServer = false;
	
	public LocalServer(int listenPort, String serverHost, int serverPort,
			String password,boolean isServer) {
		super();
		this.listenPort = listenPort;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
		this.password = password;
		this.isServer = isServer;
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(listenPort,100);//backlog set 100
		} catch (Exception e) {
			logger.warning("listenPort : " + listenPort + ", create ServerSocket failed " + e.getMessage());
			kill = true;
			throw new RuntimeException(e.getMessage());
		}
		while (!kill) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
			} catch (Exception e) {
				logger.warning("accept socket failed " + e.getMessage());
				if (serverSocket != null && !serverSocket.isClosed()) {
					logger.info("ServerSocket status is normal ! listenPort :" + listenPort);
					pause(5000l);
					continue;
				}else{
					logger.info("ServerSocket status is invalid ! listenPort :" + listenPort);
					kill = true;
					break;
				}
			}
			try {
				Socket serverSocket = new Socket(serverHost, serverPort);
				if (isServer) {
					new SocketControl(serverSocket,socket, password).start();
				}else{
					new SocketControl(socket,serverSocket, password).start();
				}
			} catch (Exception e) {
				logger.warning("SocketControl cteate or run failed !" + e.getMessage());
			}
		}
		close();
	}
	
	private void pause(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
		}
	}
	public synchronized void close(){
		kill = true;
		if (serverSocket != null) {
			try {
				serverSocket.close();
				serverSocket = null;
			} catch (Exception e) {
				logger.info("ServerSocket close failed !" + e.getMessage());
			}
		}
	}
}
