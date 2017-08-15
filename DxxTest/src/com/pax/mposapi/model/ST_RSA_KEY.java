package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">��������RSA��Կ������,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">describes the RSA KEY information, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class ST_RSA_KEY {
    /**
     * <div class="zh">
     * ģ��, ��λ bits
     * </div>
     * <div class="en">
     * the length of modulus, in bits.
     * </div>
     */
    public int iModulusLen;

    /**
     * <div class="zh">
     *  ģ, �512 �ֽ� 
     * </div>
     * <div class="en">
     *  modulus, <= 512 bytes 
     * </div>
     */
    public final byte[] aucModulus; // [512];

    /**
     * <div class="zh">
     *  ָ����, ��λ�� bits
     * </div>
     * <div class="en">
     *  the length of exponent, in bits
     * </div>
     */
    public int iExponentLen;

    /**
     * <div class="zh">
     *  ָ��, ��λ�� bits, �512�ֽ�
     * </div>
     * <div class="en">
     *  exponent, in bits, <= 512 bytes
     * </div>
     */
    public final byte[] aucExponent; // [512];

    /**
     * <div class="zh">
     *  ��Կ��Ϣ, ��Ӧ�ö���, �128�ֽ�
     * </div>
     * <div class="en">
     *  key info, defined by application, maximum 128 bytes
     * </div>
     */
    public final byte[] aucKeyInfo; // [128];

    /**
     * <div class="zh"> ����һ��ST_RSA_KEY����</div>
     * <div class="en"> create an ST_RSA_KEY instance </div>
     */
    public ST_RSA_KEY() {
        aucModulus = new byte[512];
        aucExponent = new byte[512];
        aucKeyInfo = new byte[128];
    }

    /**
     * <div class="zh">
     * ����object�е�����д��byte����
     * </div>
     * <div class="en">
     * get data from this object and write to a byte array.
     * </div>
     *
     * @return
     * <div class="zh">
     * 	�õ��İ�����object���ݵ�byte����.
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
     * ��һ��byte�����ж�ȡ���ݲ���¼�ڱ�object��
     * </div>
     * <div class="en">
     * get data from a byte array to this object
     * </div>
     *
     * @param bb
     * <div class="zh">
     *   �Ӵ�byte������ȡ���ݵ���object��
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
