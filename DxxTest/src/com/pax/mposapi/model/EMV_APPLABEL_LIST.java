package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">应用标签列表,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">application label list, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class EMV_APPLABEL_LIST {
	/**
	 * <div class="zh">应用preferred name, 1~16字节</div>
	 * <div class="en">application preferred name, 1~16 bytes</div>
	 */
	public final byte[] aucAppPreName;//[17]   
	/**
	 * <div class="zh">应用标签, 1~16字节</div>
	 * <div class="en">application label, 1~16 bytes</div>
	 */
	public final byte[] aucAppLabel;//[17]   
	/**
	 * <div class="zh">Issuer Discretionary Data, tag 'BF0C'数据: 1个字节的长度字节+'BF0C'最大222个字节(最大共223字节), tag '73'数据:1个字节的长度字节+'73'最大242个字节(最大共243字节)</div>
	 * <div class="en">Data in template "BF0C" or "73", in the format of length+value, where 1 byte for length and other bytes for value</div>
	 */
	public final byte[] aucIssDiscrData;//[244] 
	/**
	 * <div class="zh">AID, 1~16字节</div>
	 * <div class="en">AID, 1~16bytes</div>
	 */
	public final byte[] aucAID;//[17]  
	/**
	 * <div class="zh">AID长度</div>
	 * <div class="en">AID length</div>
	 */
	public byte ucAidLen;  


    /**
     * <div class="zh"> 创建一个EMV_APPLABEL_LIST对象</div>
     * <div class="en"> create an EMV_APPLABEL_LIST instance </div>
     */
    public EMV_APPLABEL_LIST() {
    	aucAppPreName = new byte[17];   
    	aucAppLabel = new byte[17];   
    	aucIssDiscrData = new byte[244]; 
    	aucAID = new byte[17];
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

    	ss.put(aucAppPreName);//[17]   
    	ss.put(aucAppLabel);//[17]   
    	ss.put(aucIssDiscrData);//[244] 
    	ss.put(aucAID);//[17]  
    	ss.put(ucAidLen);
    	
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
     * @param offset
     * <div class="zh">
     *   偏移量, 将从bb的第offset字节开始解析
     * </div>
     * <div class="en">
     *   the offset from which the data is read
     * </div>
     * 
     */
    public void serialFromBuffer(byte[] bb, int offset) {
        ByteBuffer ss = ByteBuffer.wrap(bb, offset, bb.length - offset);
        ss.order(ByteOrder.BIG_ENDIAN);
        
    	ss.get(aucAppPreName);//[17]   
    	ss.get(aucAppLabel);//[17]   
    	ss.get(aucIssDiscrData);//[244] 
    	ss.get(aucAID);//[17]
    	ucAidLen = ss.get();
    }
}
