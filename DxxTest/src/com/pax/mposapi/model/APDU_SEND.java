package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">用于描述发往ICC/PICC数据,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">describes the data sent to ICC/PICC, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class APDU_SEND {
    /**
     * CLA, INS, P1, P2
     */	
    /**
     * <div class="zh"> 命令码 (CLA, INS, P1, P2), 4字节</div>
     * <div class="en"> command(CLA, INS, P1, P2), 4 bytes </div>
     */    
    public final byte[] Command; // [4]
    /**
     * <div class="zh"> 发送到IC卡的数据长度 </div>
     * <div class="en"> length of data to send </div>
     */    
    public short Lc;
    /**
     * <div class="zh"> 发送到IC卡的数据 , 最长512字节</div>
     * <div class="en"> data to send, maximum length is 512 bytes </div>
     */  
    public final byte[] DataIn; // [512];
    /**
     * <div class="zh"> 期望返回数据的长度 </div>
     * <div class="en"> expeted data length to be return </div>
     */      
    public short Le;
    
    /**
     * <div class="zh"> 创建一个APDU_SEND对象</div>
     * <div class="en"> create an APDU_SEND instance </div>
     */
    public APDU_SEND() {
        Command = new byte[4];
        DataIn = new byte[512];
    }

    /**
     * <div class="zh"> 用指定参数创建一个APDU_SEND对象</div>
     * <div class="en"> create an APDU_SEND instance with specified arguments</div>
     * 
     * @param cmd
     * <div class="zh"> 参考  {@link #Command} </div>
     * <div class="en"> see {@link #Command} </div>
     * @param data
     * <div class="zh"> 参考  {@link #DataIn} </div>
     * <div class="en"> see {@link #DataIn} </div>
     * @param lc
     * <div class="zh"> 参考  {@link #Lc} </div>
     * <div class="en"> see {@link #Lc} </div>
     * @param le
     * <div class="zh"> 参考  {@link #Le} </div>
     * <div class="en"> see {@link #Le} </div>
     */
    public APDU_SEND(byte[] cmd, byte[] data, short lc, short le) {
        Command = cmd;
        DataIn = data;
        Lc = lc;
        Le = le;
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
        ss.order(ByteOrder.BIG_ENDIAN);
        ss.put(this.Command);
        // ss.putInt(this.Lc);
        ss.putShort((short) (this.Lc));
        ss.put(this.DataIn, 0, Lc);
        // ss.putInt(this.Le);
        ss.putShort((short) this.Le);

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
        ss.get(this.Command);
        // this.Lc=ss.getInt();
        this.Lc = ss.getShort();
        ss.get(this.DataIn, 0, Lc);
        // this.Le=ss.getInt();
        this.Le = ss.getShort();
    }
}
