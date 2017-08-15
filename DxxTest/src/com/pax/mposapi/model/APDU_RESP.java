package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">用于描述从ICC/PICC返回的数据,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">describes the data from ICC/PICC, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class APDU_RESP {
    /**
     * <div class="zh"> 实际从IC卡返回的数据长度 </div>
     * <div class="en"> The actual returned data length </div>
     */
    public short LenOut;/* The actual returned data length */
    /**
     * <div class="zh"> 从IC卡返回的数据, 最长512字节</div>
     * <div class="en"> Returned data from ICC, maximum length is 512 bytes </div>
     */
    public final byte[] DataOut; /* Returned data from ICC */
    /**
     * <div class="zh"> IC卡状态字1 </div>
     * <div class="en"> ICC status 1 </div>
     */
    public byte SWA;/* ICC status 1 */
    /**
     * <div class="zh"> IC卡状态字2 </div>
     * <div class="en"> ICC status2 </div>
     */
    public byte SWB;/* ICC status2 */

    /**
     * <div class="zh"> 创建一个APDU_RESP对象</div>
     * <div class="en"> create an APDU_RESP instance </div>
     */
    public APDU_RESP() {
        DataOut = new byte[512];
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
        // ss.putInt(this.LenOut);
        ss.putShort((short) this.LenOut);
        ss.put(this.DataOut);
        ss.put(this.SWA);
        ss.put(this.SWB);
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
        // this.LenOut=ss.getInt();
        this.LenOut = (short) ss.getShort();
        ss.get(this.DataOut, 0, LenOut);
        this.SWA = ss.get();
        this.SWB = ss.get();
    }
}
