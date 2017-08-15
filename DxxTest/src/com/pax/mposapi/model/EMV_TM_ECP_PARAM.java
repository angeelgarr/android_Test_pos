package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">终端电子现金参数,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">terminal electronic cash paramters, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class EMV_TM_ECP_PARAM {
	/**
	 * <div class="zh">TSI标志是否存在(TSI-Electronic cash terminal support indicator)</div>
	 * <div class="en">TSI flag present or not(TSI-Electronic cash terminal support indicator)</div>
	 */
	public byte ucECTSIFlg;
	/**
	 * <div class="zh">TSI取值(TSI-Electronic cash terminal support indicator)</div>
	 * <div class="en">TSI value(TSI-Electronic cash terminal support indicator)</div>
	 */
	public byte ucECTSIVal;
	/**
	 * <div class="zh">TTL标志是否存在(TTL-Electronic cash terminal transaction limit)</div>
	 * <div class="en">TTL flag present or not(TTL-Electronic cash terminal transaction limit)</div>
	 */
	public byte ucECTTLFlg;
	/**
	 * <div class="zh">TTL取值(TTL-Electronic cash terminal transaction limit)</div>
	 * <div class="en">TTL value(TTL-Electronic cash terminal transaction limit)</div>
	 */
	public int ulECTTLVal;

    /**
     * <div class="zh"> 创建一个EMV_TM_ECP_PARAM对象</div>
     * <div class="en"> create an EMV_TM_ECP_PARAM instance </div>
     */
    public EMV_TM_ECP_PARAM() {

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

    	ss.put(ucECTSIFlg);
    	ss.put(ucECTSIVal);
    	ss.put(ucECTTLFlg);
    	ss.putInt(ulECTTLVal);
    	
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

    	ucECTSIFlg = ss.get();
    	ucECTSIVal = ss.get();
    	ucECTTLFlg = ss.get();
    	ulECTTLVal = ss.getInt();
    }
}
