package pub.flyk.net;

import java.net.Socket;
import java.util.logging.Logger;

import pub.flyk.utils.LoggerFactory;

/**
 * @author flyk
 * @date Dec 15, 2017
 * 
 */
public class SocketControl extends Thread {
	
	private static Logger logger = LoggerFactory.getLogger(SocketControl.class);
	
	private Socket clientSocket = null;

	private Socket proxySocket = null;
	
	private String password; 
	
	private boolean isInputOver = false;
	
	private boolean isOutputOver = false;

	public SocketControl(Socket clientSocket, String proxyHost, int proxyPort, String password) {
		try {
			this.clientSocket = clientSocket;
			this.password = password;
			this.proxySocket = new Socket(proxyHost, proxyPort);
		} catch (Exception e) {
			logger.warning("SocketControl create failed : " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public synchronized void setInputOver(boolean isInputOver) {
		this.isInputOver = isInputOver;
	}
	
	public synchronized void setOutputOver(boolean isOutputOver) {
		this.isOutputOver = isOutputOver;
	}
	
	@Override
	public void run() {
		try {
			proxySocket.setSoTimeout(180000);
			clientSocket.setSoTimeout(180000);
			
			proxySocket.setKeepAlive(true);
			clientSocket.setKeepAlive(true);
			
			new TransferEncryptData(this, clientSocket.getInputStream(), proxySocket.getOutputStream(), password).start();
			new TransferDecryptData(this, proxySocket.getInputStream(), clientSocket.getOutputStream(), password).start();
		} catch (Exception e) {
			logger.warning("TransferData failed : " + e.getMessage());
			isInputOver = true;
			isOutputOver = true;
			kill();
		}
	}
	
	public void kill(){
		if (isInputOver && isOutputOver) {
			try {
				proxySocket.close();
				logger.info("proxySocket closed");
			} catch (Exception e) {
			}
			try {
				clientSocket.close();
				logger.info("clientSocket closed");
			} catch (Exception e) {
			}
		}
	}

}
