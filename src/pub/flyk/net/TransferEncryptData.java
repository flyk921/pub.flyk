package pub.flyk.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import pub.flyk.security.EncryptAndDecrypt;
import pub.flyk.utils.CommonUtil;
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
			while(true){
				int readSize = inputStream.read(buffer);
				if (readSize == -1) {
					logger.info(this.getName() + "  there is no data, TransferEncryptData will stop !");
					break;
				}
				data = new byte[readSize];
				System.arraycopy(buffer, 0, data, 0, readSize);
				byte[] dataIV = encryptAndDecrypt.getSecureRandom(EncryptAndDecrypt.IV_SIZE);
				byte[] enData = encryptAndDecrypt.encrypt(data, dataIV);
				
				if (enData == null) {
					logger.info(this.getName() + "  data encrypt error !");
					break;
				}
				byte[] noiseData = (enData.length < EncryptAndDecrypt.NOISE_MAX/2) ? encryptAndDecrypt.getSecureRandom(CommonUtil.randomInt(EncryptAndDecrypt.NOISE_MAX)) : new byte[0];
				
				byte[] blockIV = encryptAndDecrypt.getSecureRandom(EncryptAndDecrypt.IV_SIZE);
				byte[] enBlockData = encryptAndDecrypt.encrypt(encryptAndDecrypt.getBlockSizeBytes(enData.length + dataIV.length, noiseData.length), blockIV);
				if (enBlockData == null || enBlockData.length != EncryptAndDecrypt.BLOCK_SIZE) {
					logger.info(this.getName() + "  BlockData encrypt error !");
					break;
				}
				encryptData = new byte[blockIV.length + enBlockData.length + dataIV.length + enData.length + noiseData.length];
				
				System.arraycopy(blockIV, 0, encryptData, 0, blockIV.length);
				System.arraycopy(enBlockData, 0, encryptData, blockIV.length, enBlockData.length);
				System.arraycopy(dataIV, 0, encryptData, blockIV.length + enBlockData.length, dataIV.length);
				System.arraycopy(enData, 0, encryptData, blockIV.length + enBlockData.length + dataIV.length, enData.length);
				System.arraycopy(noiseData, 0, encryptData, blockIV.length + enBlockData.length + dataIV.length + enData.length, noiseData.length);
				
				outputStream.write(encryptData);
				outputStream.flush();
				
				if (readSize == buffer.length && readSize < EncryptAndDecrypt.BUFFER_SIZE_MAX) { 
					buffer = new byte[readSize + EncryptAndDecrypt.BUFFER_SIZE_STEP];
				} else if (readSize < (buffer.length - EncryptAndDecrypt.BUFFER_SIZE_STEP) && (buffer.length - EncryptAndDecrypt.BUFFER_SIZE_STEP) >= EncryptAndDecrypt.BUFFER_SIZE_MIN) {
					buffer = new byte[buffer.length - EncryptAndDecrypt.BUFFER_SIZE_STEP];
				}
			}
			
		} catch (Exception e) {
			logger.warning(this.getName() + "  TransferEncryptData error : " + e.getMessage());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
					inputStream = null;
				} catch (IOException e) {
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
					outputStream = null;
				} catch (IOException e) {
				}
			}
			buffer = null;
			data = null;
			encryptData = null;
			socketControl.setInputOver(true);
			socketControl.kill();
		}
	}
}
