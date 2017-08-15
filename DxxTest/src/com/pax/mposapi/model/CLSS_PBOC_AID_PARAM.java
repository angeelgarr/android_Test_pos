package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">QPBOC应用相关参数,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">QPBOC application paramter, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class CLSS_PBOC_AID_PARAM {
    /**
     * <div class="zh">最低限额 - 同EMV最低限额</div>
     * <div class="en">Terminal floor limit - the same as Terminal floor limit of contact EMV</div>
     */	
	 public int ulTermFLmt;        //最低限额 - 同EMV最低限额
    /**
     * <div class="zh">保留, 4字节</div>
     * <div class="en">reserved, 4 bytes</div>
     */	
	 public final byte[] aucRFU;//[4];	

    /**
     * <div class="zh"> 创建一个CLSS_PBOC_AID_PARAM对象</div>
     * <div class="en"> create an CLSS_PBOC_AID_PARAM instance </div>
     */
    public CLSS_PBOC_AID_PARAM() {
    	aucRFU = new byte[4]; 
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

        ss.putInt(ulTermFLmt);        //最低限额 - 同EMV最低限额
   	 	ss.put(aucRFU);//[4];
   	 	
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
        
        ulTermFLmt = ss.getInt();        //最低限额 - 同EMV最低限额
   	 	ss.get(aucRFU);//[4];
    }
}
