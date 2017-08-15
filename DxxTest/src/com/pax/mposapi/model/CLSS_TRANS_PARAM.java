package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">非接交易相关参数,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">contactless transaction paramter, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class CLSS_TRANS_PARAM {
	
	/**
	 * <div class="zh">授权金额</div>
	 * <div class="en">authorized amount</div>
	 */
    public int  ulAmntAuth;     // 授权金额(public int)
    /**
     * <div class="zh">其他金额</div>
     * <div class="en">other amount</div>
     */
    public int  ulAmntOther;    // 其他金额(public int) 
    /**
     * <div class="zh">交易序列计数器</div>
     * <div class="en">transaction number</div>
     */
    public int  ulTransNo;      // 交易序列计数器(4 BYTE)
    /**
     * <div class="zh">交易类型'9C'</div>
     * <div class="en">transaction type(tag '9C')</div>
     */
	public byte  ucTransType;    // 交易类型'9C'
	/**
	 * <div class="zh">交易日期 YYMMDD, 3字节</div>
	 * <div class="en">transaction date YYMMDD, 3 bytes</div>
	 */
	public final byte[]  aucTransDate;//[4]; // 交易日期 YYMMDD
	/**
	 * <div class="zh">交易时间 HHMMSS, 3字节</div>
	 * <div class="en">transaction time HHMMSS, 3 bytes</div>
	 */
	public final byte[]  aucTransTime;//[4]; // 交易时间 HHMMSS

    /**
     * <div class="zh"> 创建一个CLSS_TRANS_PARAM对象</div>
     * <div class="en"> create an CLSS_TRANS_PARAM instance </div>
     */
    public CLSS_TRANS_PARAM() {
    	aucTransDate = new byte[4];
    	aucTransTime = new byte[4];
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

        ss.putInt(ulAmntAuth);     // 授权金额(public int)
        ss.putInt(ulAmntOther);    // 其他金额(public int) 
        ss.putInt(ulTransNo);      // 交易序列计数器(4 BYTE)
    	ss.put(ucTransType);    // 交易类型'9C'
    	ss.put(aucTransDate, 0, 4);//[4]); // 交易日期 YYMMDD
    	ss.put(aucTransTime);//[4]); // 交易时间 HHMMSS

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
        
        ulAmntAuth = ss.getInt();     // 授权金额(public int)
        ulAmntOther = ss.getInt();    // 其他金额(public int) 
        ulTransNo = ss.getInt();      // 交易序列计数器(4 BYTE)
        ucTransType = ss.get();    // 交易类型'9C'
    	ss.get(aucTransDate);//[4]); // 交易日期 YYMMDD
    	ss.get(aucTransTime);//[4]); // 交易时间 HHMMSS

    }
}
