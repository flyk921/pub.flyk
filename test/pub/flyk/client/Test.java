package pub.flyk.client;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import pub.flyk.utils.LoggerFactory;
import pub.flyk.utils.StringUtil;


public class Test {
	private static Logger logger = LoggerFactory.getLogger(Test.class);
	public static void main(String[] args) {
		/*logger.severe("haha");
		logger.info("haha");
		logger.fine("haha");
		logger.finer("haha");
		logger.finest("haha");
		logger.warning("haha");
		logger.config("haha");
		logger.info("com.xyz.foo.level");
		StringUtil.getBytesUtf8("dd");
		String abc =  "5e3e18254255a4cd2b96b328798ff944";
		
		byte[] a = Base64.getDecoder().decode(abc.getBytes(StandardCharsets.UTF_8));
		System.out.println(Arrays.toString(a));*/
		
//		System.out.println(String.format("%05d", 2321));
//		System.out.println(Arrays.toString(String.format("%05d", 2321).getBytes()));
//		System.out.println(String.format("%08d", 38));
//		System.out.println(Arrays.toString(String.format("%08d", 38).getBytes()));
//		
//		try {
//			new Scanner(Paths.get(""));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		/*Map<String, String> clientConfig = new HashMap<>();
		clientConfig.put("serverHost","127.0.0.1");
		clientConfig.put("serverPort","");
		clientConfig.put("proxyPort","3128");
		clientConfig.put("password","");
		
		System.out.println(clientConfig.toString());
		System.out.println(JSON.toJSON(clientConfig));
		
		File clientFile = new File("config/client.conf");
		System.out.println(FileUtil.saveFile(clientConfig.toString(), clientFile));*/
		
//		EncryptAndDecrypt en = EncryptAndDecrypt.getInstance("22kejhkafnxzcjk");
//		byte[] en_data = en.encrypt(String.format("%08d,%05d",12398,2983).getBytes());
//		System.out.println(Arrays.toString(en_data));
		logger.info("hehe");
		
	}
	
	
	@org.junit.Test
	public void testAes() throws Exception {
		String key = "11111";
		SecureRandom secureRandom = new SecureRandom();
		Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
		
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] result = messageDigest.digest(StringUtil.getBytesUtf8(key));
		byte[] keyBytes = Base64.getEncoder().encode(result);
		SecretKeySpec secretKey =  new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
		
		byte[] IV = new byte[16];
		secureRandom.nextBytes(IV);
		IvParameterSpec IVSpec = new IvParameterSpec(IV );
		cipher.init(Cipher.ENCRYPT_MODE, secretKey,IVSpec);
		String str = "hhhhhhhhhhhh";
		byte[] r = cipher.doFinal(StringUtil.getBytesUtf8(str));
		
		System.out.println(Arrays.toString(r));
		System.out.println(r.length);
		System.out.println(StringUtil.toHexString(r));
		
		System.out.println(Base64.getEncoder().encode(r).length);
	}
}
