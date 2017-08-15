package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">被锁AID相关参数,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">blocked AID parameter, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class CLSS_TM_AID_LIST {
    /**
     * <div class="zh">AID长度</div>
     * <div class="en">AID length</div>
     */			
	public byte ucAidLen;     
    /**
     * <div class="zh">AID, 5~16字节</div>
     * <div class="en">AID, 5~16bytes</div>
     */		
	public final byte[] aucAID;	//[17];   // 20090609 liuxl 增加一字节 
    /**
     * <div class="zh">部分选择标识(1-部分匹配，0-完全匹配)</div>
     * <div class="en">partial matching flag(1-partial matching. 0-full matching)</div>
     */		
	public byte ucSelFlg;	 // 部分选择标识(1-部分匹配，0-完全匹配)
    /**
     * <div class="zh">内核类型</div>
     * <div class="en">kernel type</div>
     */		
	public byte ucKernType; // 20090609 liuxl 增加

    /**
     * <div class="zh"> 创建一个CLSS_TM_AID_LIST对象</div>
     * <div class="en"> create an CLSS_TM_AID_LIST instance </div>
     */
    public CLSS_TM_AID_LIST() {
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

    	ss.put(ucAidLen);     
    	ss.put(aucAID);	//[17];   // 20090609 liuxl 增加一字节 
    	ss.put(ucSelFlg);	 // 部分选择标识(1-部分匹配，0-完全匹配)
    	ss.put(ucKernType); // 20090609 liuxl 增加

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
        
    	ucAidLen = ss.get();     
    	ss.get(aucAID);	//[17];   // 20090609 liuxl 增加一字节 
    	ucSelFlg = ss.get();	 // 部分选择标识(1-部分匹配，0-完全匹配)
    	ucKernType = ss.get(); // 20090609 liuxl 增加

    }
}
