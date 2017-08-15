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
	 * ���� 
	 * @param datasource:��Ҫ���ܵ����� 
	 * @param key����Կ
	 */
	public static byte[] desCrypto(byte[] datasource, byte[] key) {
		try {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(key); // ����һ���ܳ׹�����Ȼ��������DESKeySpecת����
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);// Cipher����ʵ����ɼ��ܲ���
			Cipher cipher = Cipher.getInstance("DES" + "/ECB/NoPadding"); // ���ܳ׳�ʼ��Cipher����
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random); // ���ڣ���ȡ���ݲ�����
			// ��ʽִ�м��ܲ���
			return cipher.doFinal(datasource);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ���� 
	 * @param src ���� 
	 * @param key:��Կ
	 */
	public static byte[] decrypt(byte[] src, byte[] key) {
		try {
			SecureRandom random = new SecureRandom(); // DES�㷨Ҫ����һ�������ε������Դ
			DESKeySpec desKey = new DESKeySpec(key); // ��ԭʼ�ܳ����ݴ���DESKeySpec����
			// ����һ���ܳ׹�����Ȼ��������DESKeySpecת����
			// һ��SecretKey����
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher����ʵ����ɼ��ܲ���,NoPaddingΪ��䷽ʽ Ĭ��ΪPKCS5Padding
			Cipher cipher = Cipher.getInstance("DES" + "/ECB/NoPadding");
			// ���ܳ׳�ʼ��Cipher����
			cipher.init(Cipher.DECRYPT_MODE, securekey, random);
			// ���ڣ���ȡ���ݲ�����
			// ��ʽִ�м��ܲ���
			return cipher.doFinal(src);
		} catch (NoSuchAlgorithmException e) { // ��������
			e.printStackTrace();
		} catch (InvalidKeyException e) {// ��Чkey����
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {// ��Чkey����
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {// ������
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {// �Ƿ����ݿ�
			e.printStackTrace();
		} catch (BadPaddingException e) {// ��������
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
