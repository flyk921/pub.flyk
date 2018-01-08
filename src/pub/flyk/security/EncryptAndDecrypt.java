package pub.flyk.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import pub.flyk.utils.LoggerFactory;
import pub.flyk.utils.StringUtil;

public class EncryptAndDecrypt {
	
	private static Logger logger = LoggerFactory.getLogger(EncryptAndDecrypt.class);
	
	private SecureRandom secureRandom = null;

	private Cipher cipher = null;
	
	private SecretKey key = null;
	
	public static final int ENCRYPT_SIZE = 31;
	
	public static final int BLOCK_SIZE = 15;

	public static final int IV_SIZE = 16;

	public static final int NOISE_MAX = 1024 * 4;
	
	public static final int BUFFER_SIZE_MIN = 1024 * 128; 

	public static final int BUFFER_SIZE_MAX = 1024 * 512; 

	public static final int BUFFER_SIZE_STEP = 1024 * 128; 
	
	private EncryptAndDecrypt(String key) {
		super();
		try {
			this.cipher = Cipher.getInstance("AES/CFB/NoPadding");
			this.secureRandom = new SecureRandom();
			this.key = getSecretKey(key);
		} catch (Exception e) {
			logger.warning("init EncryptAndDecrypt failed : " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}
	
	
	private SecretKey getSecretKey(String key) {
		if (StringUtil.isBlank(key)) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] result = messageDigest.digest(StringUtil.getBytesUtf8(key));
			byte[] keyBytes = Base64.getEncoder().encode(result);
			return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
		} catch (Exception e) {
			logger.warning("getSecretKeyByKey error:" + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}
	
	
	private byte[] invoke(byte[] data,int mode,byte[] IV){
		if (!checkMode(mode)) {
			logger.info("invalid mode");
			return null;
		}
		if (!checkIV(IV)) {
			logger.info("invalid IV");
			return null;
		}
		if (!checkData(data)) {
			logger.info("data is null");
			return null;
		}
		try {
			IvParameterSpec IVSpec = new IvParameterSpec(IV);
			cipher.init(mode, key, IVSpec);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			logger.warning("init cipher failed : " + e.getMessage());
			return null;
		}
		
		try {
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			logger.warning("dofinal failed : " + e.getMessage());
		}
		
		return null;
	}
	
	private boolean checkData(byte[] data) {
		if (data != null ) {
			return true;
		}
		return false;
	}

	private boolean checkIV(byte[] IV) {
		if (IV != null && IV.length == IV_SIZE) {
			return true;
		}
		return false;
	}

	/**
	 * if mode is 1 or 2 return true;
	 * @param mode
	 * @return
	 */
	private boolean checkMode(int mode) {
		if (mode == Cipher.DECRYPT_MODE || mode == Cipher.ENCRYPT_MODE) {
			return true;
		}
		return false;
	}
	

	public static EncryptAndDecrypt getInstance(String key){
		return new EncryptAndDecrypt(key);
	}
	
	
	public byte[] getSecureRandom(int size) {
		
		byte[] bytes = new byte[size];
		
		secureRandom.nextBytes(bytes);
		
		return bytes;
		
	}
	/**
	 * 
	 * @param bytes
	 * @param IV
	 * @return
	 */
	public byte[] encrypt(byte[] bytes,byte[] IV){
		return invoke(bytes, Cipher.ENCRYPT_MODE, IV);
	}
	
	/**
	 * 
	 * @param bytes
	 * @param IV
	 * @return
	 */
	public byte[] decrypt(byte[] bytes,byte[] IV){
		return invoke(bytes, Cipher.DECRYPT_MODE, IV);
	}

	public byte[] getBlockSizeBytes(int dataSize, int noiseSize) {
		try {
			return StringUtil.getBytesUtf8(String.format("%010d:%04d", dataSize, noiseSize));
		} catch (Exception e) {
			logger.warning("getBlockSizeBytes error : " + e.getMessage());
			return null;
		}

	}
	public int[] getBlockSize(byte[] size) {
		if (size == null || size.length != BLOCK_SIZE) {
			return null;
		}
		try {
			String sizeStr = StringUtil.newStringUtf8(size);

			if (!sizeStr.matches("\\d+:\\d+")) {
				return null;
			}
			String[] sizeArr = sizeStr.split(":");
			return new int[] { Integer.valueOf(sizeArr[0]), Integer.valueOf(sizeArr[1]) };
		} catch (Exception e) {
			logger.warning("getBlockSize error : " + e.getMessage());
			return null;
		}
	}

}
