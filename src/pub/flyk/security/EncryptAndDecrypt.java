package pub.flyk.security;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
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
	
	private String key = null;
	
	private static final int ENCRYPT_SIZE = 30;

	private static final int IV_SIZE = 16;

	private static final int NOISE_MAX = 1024 * 4;
	
	private EncryptAndDecrypt(String key) {
		super();
		this.key = key;
		try {
			this.cipher = Cipher.getInstance("AES/CFB/NoPadding");
			this.secureRandom = new SecureRandom();
		} catch (Exception e) {
			logger.warning("init EncryptAndDecrypt failed : " + e.getMessage());
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
			System.out.println(keyBytes.length);
			return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
		} catch (Exception e) {
			logger.warning("getSecretKeyByKey error:" + e.getMessage());
		}
		return null;
	}
	
	
	public byte[] invoke(byte[] data,int mode,byte[] IV){
		if (!checkMode(mode)) {
			logger.info("invalid mode");
			return null;
		}
		if (!checkIV(IV)) {
			logger.info("invalid IV");
			return null;
		}
		if (!checkData(data)) {
			logger.info("data ");
			return null;
		}
		try {
			IvParameterSpec IVSpec = new IvParameterSpec(IV);
//			cipher.init(mode, getSecretKey(this.key), IVSpec);
			cipher.init(mode, getSecretKey(this.key));
//		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
		} catch (InvalidKeyException e) {
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
	
	
	private byte[] getSecureRandom(int size) {

		byte[] bytes = new byte[size];

		secureRandom.nextBytes(bytes);

		return bytes;

	}

	public static EncryptAndDecrypt getInstance(String key){
		return new EncryptAndDecrypt(key);
	}
	
	/**
	 * 
	 * @param key 
	 * @param bytes
	 * @return
	 */
	public byte[] encrypt(byte[] bytes){
		byte[] IV = getSecureRandom(IV_SIZE);
		System.out.println("iv bytes" + Arrays.toString(IV));
		byte[] encrypt_data = invoke(bytes, Cipher.ENCRYPT_MODE, IV);
		return encrypt_data;
	}
	
	/**
	 * 
	 * @param key
	 * @param bytes
	 * @return
	 */
	public byte[] decrypt(byte[] bytes){
		return bytes;
	}


}
