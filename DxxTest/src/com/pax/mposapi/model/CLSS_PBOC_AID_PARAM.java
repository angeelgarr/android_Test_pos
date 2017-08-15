package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">QPBOCӦ����ز���,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">QPBOC application paramter, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class CLSS_PBOC_AID_PARAM {
    /**
     * <div class="zh">����޶� - ͬEMV����޶�</div>
     * <div class="en">Terminal floor limit - the same as Terminal floor limit of contact EMV</div>
     */	
	 public int ulTermFLmt;        //����޶� - ͬEMV����޶�
    /**
     * <div class="zh">����, 4�ֽ�</div>
     * <div class="en">reserved, 4 bytes</div>
     */	
	 public final byte[] aucRFU;//[4];	

    /**
     * <div class="zh"> ����һ��CLSS_PBOC_AID_PARAM����</div>
     * <div class="en"> create an CLSS_PBOC_AID_PARAM instance </div>
     */
    public CLSS_PBOC_AID_PARAM() {
    	aucRFU = new byte[4]; 
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

        ss.putInt(ulTermFLmt);        //����޶� - ͬEMV����޶�
   	 	ss.put(aucRFU);//[4];
   	 	
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
        
        ulTermFLmt = ss.getInt();        //����޶� - ͬEMV����޶�
   	 	ss.get(aucRFU);//[4];
    }
}
