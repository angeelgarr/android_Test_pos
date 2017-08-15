package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">用于描述RSA PIN KEY的数据,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">describes the RSA PIN KEY, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class RSA_PINKEY {		
    /**
     * <div class="zh"> 公钥模长, 单位bit</div>
     * <div class="en"> modulus length, in bits </div>
     */
    public int modlen;
    /**
     * <div class="zh"> 公钥模, 最长2048 bits(256字节)</div>
     * <div class="en"> modulus, 2048 bits(256 bytes) maximum </div>
     */
    public final byte[] mod; // [256];
    /**
     * <div class="zh"> 公钥指数长, 单位bit</div>
     * <div class="en"> exponent length, in bits </div>
     */
    public int expLen;
    /**
     * <div class="zh"> 公钥指数, 1或3字节</div>
     * <div class="en"> exponent, 1 or 3 bytes </div>
     */
    public final byte[] exp; // [4];
    /**
     * <div class="zh"> 从IC卡获得的随机数长 </div>
     * <div class="en"> length of random number from ICC  </div>
     */
    public byte iccrandomlen;
    /**
     * <div class="zh"> 从IC卡获得的随机数长, 8字节</div>
     * <div class="en"> random number from ICC, 8 bytes  </div>
     */
    public final byte[] iccrandom; // [8];

    /**
     * <div class="zh"> 创建一个RSA_PINKEY对象</div>
     * <div class="en"> create an RSA_PINKEY instance </div>
     */
    public RSA_PINKEY() {
        mod = new byte[256];
        exp = new byte[4];
        iccrandom = new byte[8];
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
        
        ss.putInt(this.modlen);
        ss.put(new byte[mod.length - modlen / 8]);	//left-padded with 0s
        ss.put(mod, 0, modlen / 8);
        
        ss.putInt(this.expLen);
        ss.put(new byte[exp.length - expLen / 8]);	//left-padded with 0s
        ss.put(this.exp, 0, expLen / 8);
        
        ss.put(this.iccrandomlen);
        ss.put(this.iccrandom);

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
        this.modlen = ss.getInt();
        byte[] modTmp = new byte[mod.length]; 
        ss.get(modTmp);
        System.arraycopy(modTmp, mod.length - modlen / 8, mod, 0, modlen / 8);
        
        expLen = ss.getInt();
        byte[] expTmp = new byte[exp.length]; 
        ss.get(expTmp);
        System.arraycopy(expTmp, exp.length - expLen / 8, exp, 0, expLen / 8);
        
        this.iccrandomlen = ss.get();
        ss.get(this.iccrandom);
    }
}
