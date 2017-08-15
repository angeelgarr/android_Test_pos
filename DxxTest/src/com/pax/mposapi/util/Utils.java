package com.pax.mposapi.util;

import java.util.Random;

public class Utils {
	private static final String TAG = "Utils";
	
	public static void randomBytes(byte[] in) {
		Random random = new Random();
		random.nextBytes(in);
	}

	public static String byte2HexStrUnFormatted(byte[] bytes, int offset, int len) {
		if (offset > bytes.length || offset + len > bytes.length) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i ++) {
			sb.append(Integer.toHexString(bytes[i + offset] | 0xFFFFFF00).substring(6));
			if (((i + 1) % 16) == 0) {
			}
		}
		return sb.toString();
	}

	public static String byte2HexStr(byte[] bytes, int offset, int len) {
		if (offset > bytes.length || offset + len > bytes.length) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		for (int i = 0; i < len; i ++) {
			sb.append(Integer.toHexString(bytes[i + offset] | 0xFFFFFF00).substring(6));
			sb.append(" ");
			if (((i + 1) % 16) == 0) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public static byte[] xor(byte[] a, byte[] b, int len) {
		byte[] result = new byte[len]; 
		for (int i = 0; i < len; i++) {
			result[i] = (byte)(a[i] ^ b[i]);
		}
		return result;
	}
	
	public static String bcd2Str(byte[] bytes){
	    StringBuffer temp=new StringBuffer(bytes.length*2);
	    for(int i=0;i<bytes.length;i++){
	    	byte left = (byte)((bytes[i] & 0xf0)>>>4);
	    	byte right = (byte)(bytes[i] & 0x0f);
	    	if (left >= 0x0a && left <= 0x0f) {
	    		left -= 0x0a;
	    		left += 'A';
	    	} else {
	    		left += '0';
	    	}
	    	
	    	if (right >= 0x0a && right <= 0x0f) {
	    		right -= 0x0a;
	    		right += 'A';
	    	} else {
	    		right += '0';
	    	}
	    	
	    	temp.append(String.format("%c", left));
	    	temp.append(String.format("%c", right));
	    }
	    return temp.toString();
	}

	public static byte[] str2Bcd(String asc) {
	    int len = asc.length();
	    int mod = len % 2;
	    if (mod != 0) {
	     asc = "0" + asc;
	     len = asc.length();
	    }
	    byte abt[] = new byte[len];
	    if (len >= 2) {
	     len = len / 2;
	    }
	    byte bbt[] = new byte[len];
	    abt = asc.getBytes();
	    int j, k;
	    for (int p = 0; p < asc.length()/2; p++) {
	     if ( (abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
	      j = abt[2 * p] - 'a' + 0x0a;
	     } else if ( (abt[2 * p] >= 'A') && (abt[2 * p] <= 'Z')){
	      j = abt[2 * p] - 'A' + 0x0a;
	     } else {
	   	      j = abt[2 * p] - '0';	    	 
	     }

	     if ( (abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
	      k = abt[2 * p + 1] - 'a' + 0x0a;
	     } else if ( (abt[2 * p + 1] >= 'A') && (abt[2 * p + 1] <= 'Z')) {
	      k = abt[2 * p + 1] - 'A' + 0x0a;
	     } else {
	    	 k = abt[2 * p + 1] - '0';
	     }
	     
	     int a = (j << 4) + k;
	     byte b = (byte) a;
	     bbt[p] = b;
	    }
	    return bbt;
	}

	public static boolean cmpByteArray(byte[] a, int aOffset, byte[] b, int bOffset, int len) {
		if ((aOffset + len)> a.length || (bOffset + len) > b.length) {
			return false;
		}
		
		for (int i = 0; i < len; i++) {
			if (a[aOffset + i] != b[bOffset + i]) {
				return false;
			}
		}
		
		return true;
	}
	
	public static void int2ByteArray(int x, byte[] to, int offset) {
		to[offset] 		= (byte)((x >>> 24) & 0xff);
		to[offset + 1] 	= (byte)((x >>> 16) & 0xff);
		to[offset + 2] 	= (byte)((x >>> 8) & 0xff);
		to[offset + 3] 	= (byte)(x & 0xff);
	}

	public static void short2ByteArray(short x, byte[] to, int offset) {
		to[offset] 		= (byte)((x >>> 8) & 0xff);
		to[offset + 1] 	= (byte)(x & 0xff);
	}	

	public static int intFromByteArray(byte[] from, int offset) {
    	return ((from[offset] << 24) & 0xff000000) | ((from[offset + 1] << 16) & 0xff0000) | ((from[offset + 2] << 8) & 0xff00) | (from[offset + 3] & 0xff);
	}

	public static short shortFromByteArray(byte[] from, int offset) {
    	return (short)(((from[offset] << 8) & 0xff00) | (from[offset + 1] & 0xff));
	}
	
	public static int min(int a, int b) {
		return (a < b) ? a : b;
	}
	
	public static class RingBuffer {
		private byte[] buffer;
		private int wp = 0;		//write pointer
		private int rp = 0;		//read pointer
		
		public RingBuffer(int size) {
			buffer = new byte[size];
		}
		
		// 0      			1       		2
		//total bytes/forward bytes/backward bytes
		private synchronized int[] statusForRead() {
			int[] ret = new int[3];
			if (wp >= rp) {
				ret[1] = wp - rp;
				ret[2] = 0;
			} else {
				ret[1] = buffer.length - rp;
				ret[2] = wp;
			}
			
			ret[0] = ret[1] + ret[2];
			return ret;
		}
		
		// 0      			1       		2
		//free bytes/forward bytes/backward bytes
		private synchronized int[] statusForWrite() {
			int[] ret = new int[3];
			if (wp >= rp) {
				ret[1] = buffer.length - wp;
				if (rp == 0) {
					ret[1]--;	//so that the wp won't overlap with rp.
				}
				
				ret[2] = rp;
				if (ret[2] > 0) {
					ret[2]--;
				}
			} else {
				ret[1] = rp - wp - 1;
				ret[2] = 0;
			}
			
			ret[0] = ret[1] + ret[2];	//maximum buffer.length - 1;
			return ret;
		}
		
		public synchronized int read(byte[] out, int offset, int exp) {
			int[] status = statusForRead();
			if (exp > status[0]) {
				exp = status[0];
			}
			
			if (exp <= status[1]) {
				System.arraycopy(buffer, rp, out, offset, exp);
				rp += exp;
				rp %= buffer.length;
				
				return exp;
			} else {
				System.arraycopy(buffer, rp, out, offset, status[1]);
				System.arraycopy(buffer, 0, out, offset + status[1], min(exp - status[1], status[2]));
				rp = min(exp - status[1], status[2]);
				
				return status[1] + rp;
			}
		}
		
		public synchronized int write(byte[] data, int len) {
			int[] status = statusForWrite();
			int realLen = len;
			
			if (realLen > status[0]) {
				MyLog.w(TAG, String.format("len %d too long, free space %d not enough, only %d will be saved!", realLen, status[0], status[0]));
				realLen = status[0];
			}
			
			if (realLen <= status[1]) {
				System.arraycopy(data, 0, buffer, wp, realLen);
				wp += realLen;
				wp %= buffer.length;
			} else {
				System.arraycopy(data, 0, buffer, wp, status[1]);
				System.arraycopy(data, status[1], buffer, 0, realLen - status[1]);
				wp = realLen - status[1];
			}
			return realLen;
		}
		
		//NOTE: don't set buffer to null!
		public synchronized void reset() {
			wp = 0;
			rp = 0;
		}
	}

}
