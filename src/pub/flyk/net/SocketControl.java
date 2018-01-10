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
	
	private Socket normalSocket = null;

	private Socket encryptSocket = null;
	
	private String password; 
	
	private boolean isInputOver = false;
	
	private boolean isOutputOver = false;

	public SocketControl(Socket normalSocket, Socket encryptSocket, String password) {
		this.normalSocket = normalSocket;
		this.password = password;
		this.encryptSocket = encryptSocket;
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
			encryptSocket.setSoTimeout(180000);
			normalSocket.setSoTimeout(180000);
			
			encryptSocket.setKeepAlive(true);
			normalSocket.setKeepAlive(true);
			
			new TransferEncryptData(this, normalSocket.getInputStream(), encryptSocket.getOutputStream(), password).start();
			new TransferDecryptData(this, encryptSocket.getInputStream(), normalSocket.getOutputStream(), password).start();
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
				encryptSocket.close();
				logger.info("proxySocket closed");
			} catch (Exception e) {
			}
			try {
				normalSocket.close();
				logger.info("clientSocket closed");
			} catch (Exception e) {
			}
		}
	}

}
