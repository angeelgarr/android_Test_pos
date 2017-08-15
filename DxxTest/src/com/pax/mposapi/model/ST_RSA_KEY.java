package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">用于描述RSA密钥的数据,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">describes the RSA KEY information, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class ST_RSA_KEY {
    /**
     * <div class="zh">
     * 模长, 单位 bits
     * </div>
     * <div class="en">
     * the length of modulus, in bits.
     * </div>
     */
    public int iModulusLen;

    /**
     * <div class="zh">
     *  模, 最长512 字节 
     * </div>
     * <div class="en">
     *  modulus, <= 512 bytes 
     * </div>
     */
    public final byte[] aucModulus; // [512];

    /**
     * <div class="zh">
     *  指数长, 单位是 bits
     * </div>
     * <div class="en">
     *  the length of exponent, in bits
     * </div>
     */
    public int iExponentLen;

    /**
     * <div class="zh">
     *  指数, 单位是 bits, 最长512字节
     * </div>
     * <div class="en">
     *  exponent, in bits, <= 512 bytes
     * </div>
     */
    public final byte[] aucExponent; // [512];

    /**
     * <div class="zh">
     *  密钥信息, 由应用定义, 最长128字节
     * </div>
     * <div class="en">
     *  key info, defined by application, maximum 128 bytes
     * </div>
     */
    public final byte[] aucKeyInfo; // [128];

    /**
     * <div class="zh"> 创建一个ST_RSA_KEY对象</div>
     * <div class="en"> create an ST_RSA_KEY instance </div>
     */
    public ST_RSA_KEY() {
        aucModulus = new byte[512];
        aucExponent = new byte[512];
        aucKeyInfo = new byte[128];
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
        ByteBuffer ss = ByteBuffer.allocate(2048);
        ss.clear();
        ss.order(ByteOrder.BIG_ENDIAN);
        ss.putInt(this.iModulusLen);
        
        ss.put(new byte[aucModulus.length - iModulusLen / 8]);	//left-padded with 0s
        ss.put(aucModulus, 0, iModulusLen / 8);
        
        ss.putInt(this.iExponentLen);
        
        ss.put(new byte[aucExponent.length - iExponentLen / 8]);	//left-padded with 0s
        ss.put(aucExponent, 0, iExponentLen / 8);

        ss.put(this.aucKeyInfo);

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
        this.iModulusLen = ss.getInt();
        byte[] modTmp = new byte[aucModulus.length];
        ss.get(modTmp);
        System.arraycopy(modTmp, aucModulus.length - iModulusLen / 8, aucModulus, 0, iModulusLen / 8);

        this.iExponentLen = ss.getInt();
        byte[] expTmp = new byte[aucExponent.length]; 
        ss.get(expTmp);
        System.arraycopy(expTmp, aucExponent.length - iExponentLen / 8, aucExponent, 0, iExponentLen / 8);

        ss.get(this.aucKeyInfo);
    }
}
