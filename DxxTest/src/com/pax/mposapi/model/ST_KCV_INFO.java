package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">用于描述KCV信息的数据,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">describes the KCV information, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class ST_KCV_INFO {
	/**
	 * <div class="zh">
	 * KCV 验证模式 <br/>
	 * 注意当使用{@link com.pax.mposapi.PedManager#pedGetKcv} 接口时,使用的mode 0 实际上 与 {@link com.pax.mposapi.PedManager#pedWriteKey} 接口中使用的 mode 1 功能相同
	 * </div>
	 * <div class="en">
	 * KCV check mode <br/>
	 * Note that in  {@link com.pax.mposapi.PedManager#pedGetKcv}, mode 0's functionality is the same as mode 1's
	 * functionality in {@link com.pax.mposapi.PedManager#pedWriteKey}.
	 * </div>
	 *
	 */
    public byte iCheckMode;
    
	/**
	 * <div class="zh">
	 *  KCV 相关数据<br/>
	 *  见 {@link com.pax.mposapi.PedManager#pedGetKcv}, {@link com.pax.mposapi.PedManager#pedWriteKey}, 
	 *  {@link com.pax.mposapi.PedManager#pedWriteTIK} 相关函数说明<br/>
	 *  总长度最大为128字节.
	 * </div>
	 * <div class="en">
	 *  KCV related data <br/>
	 * see details in {@link com.pax.mposapi.PedManager#pedGetKcv}, {@link com.pax.mposapi.PedManager#pedWriteKey}, 
	 *  {@link com.pax.mposapi.PedManager#pedWriteTIK}.<br/>
	 *  maximum total length is 128 bytes.	 
	 * </div>
	 *
	 */
    public final byte[] aucCheckBuf; // [128];

    /**
     * <div class="zh">
     *  内部使用, 应用程序设置无效, 
     *  在调用 {@link com.pax.mposapi.PedManager#pedGetKcv}时, 必须设置成true, 其他情况设置成false
     * </div>
     * <div class="en">
     *  only for internal use, APP should ignore this flag.
     *  when calling {@link com.pax.mposapi.PedManager#pedGetKcv}, MUST set to true, set to false in other cases.
     * </div>
     */
    public boolean isForGetKcv = false;

    /**
     * <div class="zh"> 创建一个ST_KCV_INFO对象</div>
     * <div class="en"> create an ST_KCV_INFO instance </div>
     */
    public ST_KCV_INFO() {
        aucCheckBuf = new byte[128];
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
        ss.clear();
        ss.order(ByteOrder.LITTLE_ENDIAN);
        ss.put(this.iCheckMode);
        if (iCheckMode == 0 || iCheckMode == 1 || iCheckMode == 2) {
        	if (iCheckMode > 0 || isForGetKcv) {
            	ss.put((byte)(aucCheckBuf[0] + 1));
        		ss.put(aucCheckBuf, 0, aucCheckBuf[0] + 1);
        	}
        } else {	//mode 3
        	int kcvDataLen = aucCheckBuf[0];
        	int kcvLen = aucCheckBuf[1 + kcvDataLen + 1];
        	int checkBufLen = 3 + kcvDataLen + kcvLen;
        	ss.put((byte)checkBufLen);
        	ss.put(aucCheckBuf, 0, checkBufLen);
        }

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
        ss.order(ByteOrder.LITTLE_ENDIAN);
        if (isForGetKcv) {
        	ss.get(aucCheckBuf, 0, bb.length);
        } else {
        	iCheckMode = ss.get();
        	ss.get(aucCheckBuf);
        }
    }
}
