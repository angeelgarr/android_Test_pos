package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">�����й�Կ����֤���б�,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">revoked CA public key, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class EMV_REVOC_LIST {
	/**
	 * <div class="zh">RID����(5�ֽ�)</div>
	 * <div class="en">RID(5 bytes)</div>
	 */	
	public final byte[] ucRid;//[5]
	/**
	 * <div class="zh">CA��Կ����</div>
	 * <div class="en">Index of CA public key</div>
	 */	
	public byte ucIndex;
	/**
	 * <div class="zh">�����й�Կ֤�����к�(3�ֽ�)</div>
	 * <div class="en">Serial number of the issuer public key certification(3 bytes)</div>
	 */	
	public final byte[] ucCertSn;//[3] 


    /**
     * <div class="zh"> ����һ��EMV_REVOC_LIST����</div>
     * <div class="en"> create an EMV_REVOC_LIST instance </div>
     */
    public EMV_REVOC_LIST() {
    	ucRid = new byte[5];   
    	ucCertSn = new byte[3];
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

    	ss.put(ucRid);
    	ss.put(ucIndex);   
    	ss.put(ucCertSn); 
    	
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
        
    	ss.get(ucRid);
    	ucIndex = ss.get();   
    	ss.get(ucCertSn); 
    }
}
