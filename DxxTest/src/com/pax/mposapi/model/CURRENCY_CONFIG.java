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
	public final byte[] szName;				//[3+1];	        // 货币简称，如"HKD","USD"
	/**
	 * ISO-4217 currency code, 2 bytes
	 */
	public final byte[] sCurrencyCode;		//[2];		// ISO-4217   货币代码，如新台币"\x09\x01"
	/**
	 * ISO-3166-1 country code, 2 bytes
	 */
	public final byte[] sCountryCode;		//[2];        // ISO-3166-1 国家或地区代码，如台湾"\x01\x58"
	/**
	 * currency exponent<br/>
	 * For example :  RMB, USD, HKD: 0x02, Korean WON: 0x00
	 */
	public byte ucDecimal;		        // 货币小数点位置。JPY为0，USD，HKD等为2，极少数货币为3
	/**
	 * number of digits to truncate from the right of the transaction amount(ISO8583 field 4) 
	 */
	public byte ucIgnoreDigit;	        // 货币用ISO8583 bit4表示时，转换成普通金额数字串之前先忽略掉的尾数个数

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
     * 将本object中的数据写入byte数组
     * </div>
     * <div class="en">
     * get data from this object and write to a byte array.
     * </div>
     *
     * @return
     * <div class="zh">
     * 	得到的包含本object数据的byte数组.
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
     * 从一个byte数组中读取数据并记录在本object中
     * </div>
     * <div class="en">
     * get data from a byte array to this object
     * </div>
     *
     * @param bb
     * <div class="zh">
     *   从此byte数组中取数据到本object中
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
