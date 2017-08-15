package com.pax.dxxtest;

public class util {
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte1(hexChars[pos]) << 4 | charToByte1(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
     private static byte charToByte1(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
 	
 	public static void ConvIntA2ByteA(int[] iA, byte[] bA)
 	{
 		for(int i = 0; i < iA.length; i++)
 		{
 			bA[i] = (byte) (iA[i] & 0xFF);
 		}
 	}
}
