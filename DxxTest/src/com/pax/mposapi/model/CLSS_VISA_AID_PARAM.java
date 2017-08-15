package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * PayWave application parameter, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array
 */
public class CLSS_VISA_AID_PARAM {
	
	/**
	 * Terminal floor limits - the same as floor limits of contact EMV
	 */
	public int ulTermFLmt;

	/**
	 * 01(default):only supports domestic ctless transaction
	 * 00 or not present: supports international ctless transaction
	*/
	public byte ucDomesticOnly;
	
	/**
	 * cardholder veryfy method request number
	 */
	public byte ucCvmReqNum;
	
	/**
	 * whether a CVM is required when the amount is higher than the Contactless CVM Required Limit. 01-Signature 02-Online PIN
	 * */
	public final byte [] aucCvmReq;//[5] 
	
	/**default is 0, support offline transaction for all version of DDA
	 *01 - only support  offline transaction for version 01 of DDA
	 */
	public byte ucEnDDAVerNo; //默认=0, 读卡器支持所有版本的DDA卡片的脱机交易, 01-仅支持dda版本为'01'的卡片进行脱机交易	 
	
    /**
     * create an CLSS_VISA_AID_PARAM instance
     */
    public CLSS_VISA_AID_PARAM() {
    	
    	aucCvmReq = new byte[5];
    }

    /**
     * get data from this object and write to a byte array.
     *
     * @return
     * 	a byte array including data of this object.
     */
    public byte[] serialToBuffer() {
        ByteBuffer ss = ByteBuffer.allocate(1024);
        ss.order(ByteOrder.BIG_ENDIAN);
        ss.clear();

        ss.putInt(ulTermFLmt);
    	ss.put(ucDomesticOnly); 
    	ss.put(ucCvmReqNum);           
    	ss.put(aucCvmReq);//[5]  
    	ss.put(ucEnDDAVerNo);

        ss.flip();
        byte[] ret = new byte[ss.limit()];
        ss.get(ret);
        return ret;
    }

    /**
     * get data from a byte array to this object
     *
     * @param bb
     *   a byte array from which data should be read
     * 
     */
    public void serialFromBuffer(byte[] bb) {
        ByteBuffer ss = ByteBuffer.wrap(bb);
        ss.order(ByteOrder.BIG_ENDIAN);
        
        ulTermFLmt = ss.getInt();			
        ucDomesticOnly = ss.get();         
        ucCvmReqNum = ss.get();
        ss.get(aucCvmReq);
        ucEnDDAVerNo = ss.get();
    }
}
