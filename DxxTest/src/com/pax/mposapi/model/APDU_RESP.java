package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">����������ICC/PICC���ص�����,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">describes the data from ICC/PICC, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class APDU_RESP {
    /**
     * <div class="zh"> ʵ�ʴ�IC�����ص����ݳ��� </div>
     * <div class="en"> The actual returned data length </div>
     */
    public short LenOut;/* The actual returned data length */
    /**
     * <div class="zh"> ��IC�����ص�����, �512�ֽ�</div>
     * <div class="en"> Returned data from ICC, maximum length is 512 bytes </div>
     */
    public final byte[] DataOut; /* Returned data from ICC */
    /**
     * <div class="zh"> IC��״̬��1 </div>
     * <div class="en"> ICC status 1 </div>
     */
    public byte SWA;/* ICC status 1 */
    /**
     * <div class="zh"> IC��״̬��2 </div>
     * <div class="en"> ICC status2 </div>
     */
    public byte SWB;/* ICC status2 */

    /**
     * <div class="zh"> ����һ��APDU_RESP����</div>
     * <div class="en"> create an APDU_RESP instance </div>
     */
    public APDU_RESP() {
        DataOut = new byte[512];
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
        // this.LenOut=ss.getInt();
        this.LenOut = (short) ss.getShort();
        ss.get(this.DataOut, 0, LenOut);
        this.SWA = ss.get();
        this.SWB = ss.get();
    }
}
