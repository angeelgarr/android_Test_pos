package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Currency Configuration, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array
 */
public class CURRENCY_CONFIG {
	
	/**
	 * Currency name, 3 bytes (e.g. "HKD", "USD")
	 */
	public final byte[] szName;				//[3+1];	        // ���Ҽ�ƣ���"HKD","USD"
	/**
	 * ISO-4217 currency code, 2 bytes
	 */
	public final byte[] sCurrencyCode;		//[2];		// ISO-4217   ���Ҵ��룬����̨��"\x09\x01"
	/**
	 * ISO-3166-1 country code, 2 bytes
	 */
	public final byte[] sCountryCode;		//[2];        // ISO-3166-1 ���һ�������룬��̨��"\x01\x58"
	/**
	 * currency exponent<br/>
	 * For example :  RMB, USD, HKD: 0x02, Korean WON: 0x00
	 */
	public byte ucDecimal;		        // ����С����λ�á�JPYΪ0��USD��HKD��Ϊ2������������Ϊ3
	/**
	 * number of digits to truncate from the right of the transaction amount(ISO8583 field 4) 
	 */
	public byte ucIgnoreDigit;	        // ������ISO8583 bit4��ʾʱ��ת������ͨ������ִ�֮ǰ�Ⱥ��Ե���β������

    /**
     * create an CURRENCY_CONFIG instance
     */
    public CURRENCY_CONFIG() {
    	szName = new byte[4];
    	sCurrencyCode = new byte[2];
    	sCountryCode = new byte[2];
    }

    /**
     * <div class="zh">
     * ����object�е�����д��byte����
     * </div>
     * <div class="en">
     * get data from this object and write to a byte array.
     * </div>
     *
     * @return
     * <div class="zh">
     * 	�õ��İ�����object���ݵ�byte����.
     * </div>
     * <div class="en">
     * 	a byte array including data of this object.
     * </div>
     */  
    public byte[] serialToBuffer() {
        ByteBuffer ss = ByteBuffer.allocate(1024);
        ss.order(ByteOrder.BIG_ENDIAN);
        ss.clear();

        ss.put(szName);
        ss.put(sCurrencyCode);
        ss.put(sCountryCode);
        ss.put(ucDecimal);
        ss.put(ucIgnoreDigit);
        
        ss.flip();
        byte[] ret = new byte[ss.limit()];
        ss.get(ret);
        return ret;
    }

    /**
     * <div class="zh">
     * ��һ��byte�����ж�ȡ���ݲ���¼�ڱ�object��
     * </div>
     * <div class="en">
     * get data from a byte array to this object
     * </div>
     *
     * @param bb
     * <div class="zh">
     *   �Ӵ�byte������ȡ���ݵ���object��
     * </div>
     * <div class="en">
     *   a byte array from which data should be read
     * </div>
     * 
     */
    public void serialFromBuffer(byte[] bb) {
        ByteBuffer ss = ByteBuffer.wrap(bb);
        ss.order(ByteOrder.BIG_ENDIAN);
        
        ss.get(szName);
        ss.get(sCurrencyCode);
        ss.get(sCountryCode);
        ucDecimal = ss.get();
        ucIgnoreDigit = ss.get();
    }
}
