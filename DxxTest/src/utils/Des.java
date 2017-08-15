package utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Des {

	/**
	 * 加密 
	 * @param datasource:需要加密的数据 
	 * @param key：密钥
	 */
	public static byte[] desCrypto(byte[] datasource, byte[] key) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(key); // 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES" + "/ECB/NoPadding"); // 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random); // 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(datasource);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密 
	 * @param src 密文 
	 * @param key:密钥
	 */
	public static byte[] decrypt(byte[] src, byte[] key) {
		try {
			SecureRandom random = new SecureRandom(); // DES算法要求有一个可信任的随机数源
			DESKeySpec desKey = new DESKeySpec(key); // 从原始密匙数据创建DESKeySpec对象
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			// 一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作,NoPadding为填充方式 默认为PKCS5Padding
			Cipher cipher = Cipher.getInstance("DES" + "/ECB/NoPadding");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, securekey, random);
			// 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(src);
		} catch (NoSuchAlgorithmException e) { // 算数错误
			e.printStackTrace();
		} catch (InvalidKeyException e) {// 无效key错误
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {// 无效key错误
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {// 填充错误
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {// 非法数据块
			e.printStackTrace();
		} catch (BadPaddingException e) {// 错误的填充
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] sessionkey(byte[] Distributedpara, byte[] keysource) {
		byte[] sessionkey;
		byte[] Distributedparaback = new byte[Distributedpara.length];		
		int keylen = keysource.length;
		byte[] keysourceleft = new byte[keylen/2];
		System.arraycopy(keysource, 0, keysourceleft, 0, keylen/2);
		byte[] keysourceright = new byte[keylen/2];
		System.arraycopy(keysource, keylen/2, keysourceright, 0, keylen/2);
		
		byte[] Templeft = Des.desCrypto(Distributedpara, keysourceleft);
		Templeft = Des.decrypt(Templeft, keysourceright);
		Templeft = Des.desCrypto(Templeft, keysourceleft);
		byte[] sessionkeyleft = new byte[Templeft.length];
		System.arraycopy(Templeft, 0, sessionkeyleft, 0, Templeft.length);

//		System.out.println("sessionkeyleft:");
//		for(int j = 0; j < sessionkeyleft.length; j++){
//			System.out.println(" "+sessionkeyleft[j]);			
//		}		
		
		for(int i = 0; i < Distributedpara.length; i++){
			Distributedparaback[i] = (byte)(~(int)Distributedpara[i]);
		}
		
		byte[] Tempright = Des.desCrypto(Distributedparaback, keysourceleft);
		Tempright = Des.decrypt(Tempright, keysourceright);
		Tempright = Des.desCrypto(Tempright, keysourceleft);
		byte[] sessionkeyright = new byte[Tempright.length];
		System.arraycopy(Tempright, 0, sessionkeyright, 0, Tempright.length);

//		System.out.println("sessionkeyright:");
//		for(int j = 0; j < sessionkeyright.length; j++){
//			System.out.println(" "+sessionkeyright[j]);			
//		}		

		sessionkey = new byte[sessionkeyleft.length + sessionkeyright.length];
		System.arraycopy(sessionkeyleft, 0, sessionkey, 0, sessionkeyleft.length);
		System.arraycopy(sessionkeyright, 0, sessionkey, sessionkeyleft.length, sessionkeyright.length);
		
		String sessionkeystr = "";
 		for(int i = 0; i<sessionkey.length; i++){
 			sessionkeystr += Integer.toHexString((sessionkey[i]& 0x000000FF) | 0xFFFFFF00).substring(6);
		}
 		System.out.println("sessionkey:" + sessionkeystr);
 		
		return sessionkey;
	}

}
