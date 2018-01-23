package pub.flyk.net;

import java.io.IOException;
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
public class TransferDecryptData extends Thread {
	

	private static Logger logger = LoggerFactory.getLogger(TransferDecryptData.class);
	
	private SocketControl socketControl = null;
	private InputStream inputStream = null;
	private OutputStream outputStream = null;
	private EncryptAndDecrypt encryptAndDecrypt;
	public TransferDecryptData(SocketControl socketControl,
			InputStream inputStream, OutputStream outputStream, String password) {
		super();
		this.socketControl = socketControl;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.encryptAndDecrypt = EncryptAndDecrypt.getInstance(password);
	} 
	
	@Override
	public void run() {
		
		byte[] buffer = new byte[EncryptAndDecrypt.ENCRYPT_SIZE];
		byte[] data = null;
		byte[] decryptData = null;
		try {
			while(true){
				int readSize = inputStream.read(buffer);
				if (readSize == -1) {
					logger.info(this.getName() + "  there is no data, TransferDecryptData will stop !");
					break;
				}else if(readSize != EncryptAndDecrypt.ENCRYPT_SIZE){
					logger.info(this.getName() + "  read wrong data, TransferDecryptData will stop !");
					break;
				}
				byte[] blockIV = new byte[EncryptAndDecrypt.IV_SIZE];
				System.arraycopy(buffer, 0, blockIV, 0, blockIV.length);
				byte[] enBlockData = new byte[EncryptAndDecrypt.BLOCK_SIZE];
				System.arraycopy(buffer, blockIV.length, enBlockData, 0, enBlockData.length);
				byte[] deBlockData = encryptAndDecrypt.decrypt(enBlockData, blockIV);
				if (deBlockData == null) {
					logger.info(this.getName() + "  BlockData decrypt error !");
					break;
				}
				int[] blockSize = encryptAndDecrypt.getBlockSize(deBlockData);
				if (blockSize == null || blockSize.length != 2) {
					logger.info(this.getName() + "  get blockSize failed !");
					break;
				}
				//read data 
				int dataSize = blockSize[0] + blockSize[1];
				data = new byte[dataSize];
				//readSize = inputStream.read(data);
				int readCount = 0;
				while (readCount < dataSize) {
					readSize = inputStream.read(data, readCount, dataSize - readCount);
					if (readSize == -1) {
						break;
					}
					readCount += readSize;
				}
				
				if (dataSize != readCount) {
					logger.info(this.getName() + "  read wrong data , readSize : " + readSize + " , dataSize : " + dataSize);
					break;
				}
				byte[] dataIV = new byte[EncryptAndDecrypt.IV_SIZE];
				byte[] enData = new byte[blockSize[0] - EncryptAndDecrypt.IV_SIZE];
				
				System.arraycopy(data, 0, dataIV, 0, dataIV.length);
				System.arraycopy(data, dataIV.length, enData, 0, enData.length);
				decryptData = encryptAndDecrypt.decrypt(enData, dataIV);
				
				if (decryptData == null) {
					logger.info(this.getName() + "  data decrypt error !");
					break;
				}
				
				outputStream.write(decryptData);
				outputStream.flush();
			}
			
		} catch (Exception e) {
			logger.warning(this.getName() + "  TransferDecryptData error : " + e.getMessage());
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
			decryptData = null;
			socketControl.setOutputOver(true);
			socketControl.kill();
		}
	}
}
