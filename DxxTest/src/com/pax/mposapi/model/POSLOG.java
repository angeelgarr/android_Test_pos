package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array
 */
public class POSLOG {
	/**
	 * authorized amount. should include ulAmntOther if there's cashback. maximum 12 digits 
	 */
	public final byte[] szAmount;	//[12+1];				// 授权金额(unsigned long), 若为返现, 则该金额需包括ulAmntOther的金额
	/**
	 * cashback amount. maximum 12 digits
	 */
    public final byte[] ulAmntOther;	//[12+1];          // 其他金额(unsigned long)
    /**
     * STAN, maximum 12 digits
     */
	public final byte[] ulSTAN;		//[12];				    // STAN
	/**
	 * transaction type (tag '9C') 
	 */
	public byte ucPreTransType;             //for Clss Core
	/**
	 * trans date time, in format "YYYYMMDDhhmmss", 14 bytes
	 */
	public final byte[] szDateTime;		//[14+1];			// YYYYMMDDhhmmss
	/**
	 * terminal id, maximum 12 bytes
	 */
	public final byte[] uPosTID;		//[12];
	/**
	 * merchant id, maximum 16 bytes
	 */
	public final byte[] uMerchantID;		//[16];
	/**
	 * transaction type string to display, maximum 10 bytes
	 */
	public final byte[] ucPosTransType;		//[10];				// 交易类型, for coustmer
	/**
	 * currency configuration
	 */
	public CURRENCY_CONFIG	stTranCurrency;
	/**
	 * timeout value to wait for tapping card, in unit of 100ms. default to 500(i.e. 50s) for 0
	 */
	public int uiTimeOut;
	
    /**
     * <div class="zh"> 创建一个CLSS_PBOC_AID_PARAM对象</div>
     * <div class="en"> create an CLSS_PBOC_AID_PARAM instance </div>
     */
    public POSLOG() {
    	szAmount = new byte[13];
    	ulAmntOther = new byte[13];
    	ulSTAN = new byte[12];
    	szDateTime = new byte[15];
    	uPosTID = new byte[12];
    	uMerchantID = new byte[16];
    	ucPosTransType = new byte[10];
    	
    	stTranCurrency = new CURRENCY_CONFIG();
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

        ss.put(szAmount);
        ss.put(ulAmntOther);
        ss.put(ulSTAN);
        ss.put(ucPreTransType);
        ss.put(szDateTime);
        ss.put(uPosTID);
        ss.put(uMerchantID);
        ss.put(ucPosTransType);
        ss.put(stTranCurrency.serialToBuffer());
        ss.putInt(uiTimeOut);
        
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
        
        ss.get(szAmount);
        ss.get(ulAmntOther);
        ss.get(ulSTAN);
        ucPreTransType = ss.get();
        ss.get(szDateTime);
        ss.get(uPosTID);
        ss.get(uMerchantID);
        ss.get(ucPosTransType);
        
        byte[] bytes = stTranCurrency.serialToBuffer();
        ss.get(bytes);
        stTranCurrency.serialFromBuffer(bytes);
        
        uiTimeOut = ss.getInt();
    }
}
