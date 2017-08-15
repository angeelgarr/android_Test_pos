package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">�ն˵����ֽ����,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">terminal electronic cash paramters, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class EMV_TM_ECP_PARAM {
	/**
	 * <div class="zh">TSI��־�Ƿ����(TSI-Electronic cash terminal support indicator)</div>
	 * <div class="en">TSI flag present or not(TSI-Electronic cash terminal support indicator)</div>
	 */
	public byte ucECTSIFlg;
	/**
	 * <div class="zh">TSIȡֵ(TSI-Electronic cash terminal support indicator)</div>
	 * <div class="en">TSI value(TSI-Electronic cash terminal support indicator)</div>
	 */
	public byte ucECTSIVal;
	/**
	 * <div class="zh">TTL��־�Ƿ����(TTL-Electronic cash terminal transaction limit)</div>
	 * <div class="en">TTL flag present or not(TTL-Electronic cash terminal transaction limit)</div>
	 */
	public byte ucECTTLFlg;
	/**
	 * <div class="zh">TTLȡֵ(TTL-Electronic cash terminal transaction limit)</div>
	 * <div class="en">TTL value(TTL-Electronic cash terminal transaction limit)</div>
	 */
	public int ulECTTLVal;

    /**
     * <div class="zh"> ����һ��EMV_TM_ECP_PARAM����</div>
     * <div class="en"> create an EMV_TM_ECP_PARAM instance </div>
     */
    public EMV_TM_ECP_PARAM() {

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

    	ss.put(ucECTSIFlg);
    	ss.put(ucECTSIVal);
    	ss.put(ucECTTLFlg);
    	ss.putInt(ulECTTLVal);
    	
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

    	ucECTSIFlg = ss.get();
    	ucECTSIVal = ss.get();
    	ucECTTLFlg = ss.get();
    	ulECTTLVal = ss.getInt();
    }
}
