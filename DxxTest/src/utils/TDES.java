package utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class TDES {

	private static final String Algorithm = "DESede"; //���� �����㷨,���� DES,DESede,Blowfish
	    
	    //keybyteΪ������Կ������Ϊ24�ֽ�
	    //srcΪ�����ܵ����ݻ�������Դ��
	    public static byte[] encryptMode(byte[] keybyte, byte[] src) {
	       try {
	            //������Կ
	            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

	            //����
	            Cipher c1 = Cipher.getInstance("desede" + "/ECB/NoPadding");
	            c1.init(Cipher.ENCRYPT_MODE, deskey);
	            return c1.doFinal(src);
	        } catch (java.security.NoSuchAlgorithmException e1) {
	            e1.printStackTrace();
	        } catch (javax.crypto.NoSuchPaddingException e2) {
	            e2.printStackTrace();
	        } catch (java.lang.Exception e3) {
	            e3.printStackTrace();
	        }
	        return null;
	    }

	    //keybyteΪ������Կ������Ϊ24�ֽ�
	    //srcΪ���ܺ�Ļ�����
	    public static byte[] decryptMode(byte[] keybyte, byte[] src) {      
	    try {
	            //������Կ
	            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

	            //����
	            Cipher c1 = Cipher.getInstance("desede" + "/ECB/NoPadding");
	            c1.init(Cipher.DECRYPT_MODE, deskey);
	            return c1.doFinal(src);
	        } catch (java.security.NoSuchAlgorithmException e1) {
	            e1.printStackTrace();
	        } catch (javax.crypto.NoSuchPaddingException e2) {
	            e2.printStackTrace();
	        } catch (java.lang.Exception e3) {
	            e3.printStackTrace();
	        }
	        return null;
	    }

	    //ת����ʮ�������ַ���
	    public static String byte2hex(byte[] b) {
	        String hs="";
	        String stmp="";

	        for (int n=0;n<b.length;n++) {
	            stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
	            if (stmp.length()==1) hs=hs+"0"+stmp;
	            else hs=hs+stmp;
	            if (n<b.length-1)  hs=hs+":";
	        }
	        return hs.toUpperCase();
	    }
	    
	    //16�ֽ���Կת����24�ֽ�
	    public static byte[] key24(byte[] key16) {
	        byte[] key = new byte[24];
	        
	        System.arraycopy(key16, 0, key, 0, key16.length); 
	        System.arraycopy(key16, 0, key, 16, 8); 
	    	
	        return key;
	    }
	    
	    //�����Զ����
	    public static byte[] DataPadding(byte[] Data) {
	    	int len = Data.length;
	    	byte[] DataPadding = new byte[len + (8 - len%8)]; 
	    	System.arraycopy(Data, 0, DataPadding, 0, len); 
	    	int lenPadding = DataPadding.length;
	    	
	    	if(len%8 == 0){
	    		return Data;
	    	}
	    	else if(len%8 == 7){
	    		DataPadding[lenPadding - 1] = (byte)0x80;
	    		return DataPadding;
	    	}
	    	else{
	    		DataPadding[len] = (byte)0x80;
	    		for(int i = 1; i < (8 - len%8); i++){
	    			DataPadding[len + i] = (byte)0x00;
	    		}
	    		return DataPadding;
	    	}
	    }
}
