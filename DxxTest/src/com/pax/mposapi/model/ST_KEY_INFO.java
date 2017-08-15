package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">��������M/S��Կ��Ϣ������,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">describes the M/S KEY information, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class ST_KEY_INFO {
    /**
     * <div class="zh">
     * ��ɢ����Կ��Դ��Կ����Կ����, PED_TLK,PED_TMK,PED_TPK,PED_TAK,PED_TDK, ���õ���ucDstKeyType���ڵ���Կ����
     * </div>
     * <div class="en">
     * the source key type, can be PED_TLK,PED_TMK,PED_TPK,PED_TAK,PED_TDK, it's level MUST not 
     * lower than the level of ucDstKeyType.
     * </div>
     */
    public byte ucSrcKeyType;

    /**
     * <div class="zh">
     * ��ɢ����Կ��Դ��Կ����,����һ���1��ʼ,����ñ���Ϊ0,���ʾ�����Կ��д����������ʽ
     * </div>
     * <div class="en">
     * the source key index used to derive, start from 1. 0 means the key to write is in plain text.
     * </div>
     */
    public byte ucSrcKeyIdx;

    /**
     * <div class="zh">
     * Ŀ����Կ����Կ����,PED_TLK,PED_TMK,PED_TPK,PED_TAK,PED_TDK
     * </div>
     * <div class="en">
     * the type of the key to write, can be PED_TLK,PED_TMK,PED_TPK,PED_TAK,PED_TDK
     * </div>
     */
    public byte ucDstKeyType;

    /**
     * <div class="zh">
     * Ŀ����Կ����
     * </div>
     * <div class="en">
     * the key index of the key to write
     * </div>
     */
    public byte ucDstKeyIdx;

    /**
     * <div class="zh">
     * Ŀ����Կ����, 8/16/24
     * </div>
     * <div class="en">
     * the length of the key to write, 8/16/24
     * </div>
     */
    public byte iDstKeyLen;

    /**
     * <div class="zh">
     * ��Կ����, �Ϊ24�ֽ�
     * </div>
     * <div class="en">
     * key value, maximum length is 24 bytes
     * </div>
     */    
    public final byte[] aucDstKeyValue; // [24];

    /**
     * <div class="zh"> ����һ��ST_KEY_INFO����</div>
     * <div class="en"> create an ST_KEY_INFO instance </div>
     */
    public ST_KEY_INFO() {
        aucDstKeyValue = new byte[24];
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
        ByteBuffer ss = ByteBuffer.allocate(1024);
        ss.clear();
        ss.order(ByteOrder.LITTLE_ENDIAN);
        ss.put(this.ucSrcKeyType);
        ss.put(this.ucSrcKeyIdx);

        ss.put(this.ucDstKeyType);
        ss.put(this.ucDstKeyIdx);

        ss.put(this.iDstKeyLen);

        ss.put(this.aucDstKeyValue, 0, this.iDstKeyLen);

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
        ss.order(ByteOrder.LITTLE_ENDIAN);
        ucSrcKeyType = ss.get();
        ucSrcKeyIdx = ss.get();

        ucDstKeyType = ss.get();
        ucDstKeyIdx = ss.get();

        iDstKeyLen = ss.get();

        ss.get(aucDstKeyValue, 0, iDstKeyLen);
    }
}
