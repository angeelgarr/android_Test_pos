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
	public final byte[] szAmount;	//[12+1];				// ��Ȩ���(unsigned long), ��Ϊ����, ��ý�������ulAmntOther�Ľ��
	/**
	 * cashback amount. maximum 12 digits
	 */
    public final byte[] ulAmntOther;	//[12+1];          // �������(unsigned long)
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
	public final byte[] ucPosTransType;		//[10];				// ��������, for coustmer
	/**
	 * currency configuration
	 */
	public CURRENCY_CONFIG	stTranCurrency;
	/**
	 * timeout value to wait for tapping card, in unit of 100ms. default to 500(i.e. 50s) for 0
	 */
	public int uiTimeOut;
	
    /**
     * <div class="zh"> ����һ��CLSS_PBOC_AID_PARAM����</div>
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
