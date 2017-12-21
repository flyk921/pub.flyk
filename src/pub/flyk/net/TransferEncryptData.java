package pub.flyk.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import pub.flyk.security.EncryptAndDecrypt;
import pub.flyk.utils.LoggerFactory;

/**
 * @author flyk
 * @date Dec 15, 2017
 * 
 */
public class TransferEncryptData extends Thread {

	private static Logger logger = LoggerFactory.getLogger(TransferEncryptData.class);
	
	private SocketControl socketControl = null;
	private InputStream inputStream = null;
	private OutputStream outputStream = null;
	private EncryptAndDecrypt encryptAndDecrypt;
	public TransferEncryptData(SocketControl socketControl,
			InputStream inputStream, OutputStream outputStream, String password) {
		super();
		this.socketControl = socketControl;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.encryptAndDecrypt = EncryptAndDecrypt.getInstance(password);
	} 
	
	@Override
	public void run() {
		byte[] buffer = new byte[EncryptAndDecrypt.BUFFER_SIZE_MIN];
		byte[] data = null;
		byte[] encryptData = null;
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			socketControl.kill();
		}
		
		
	}
}
