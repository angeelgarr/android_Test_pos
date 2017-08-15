package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">����AID��ز���,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">blocked AID parameter, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class CLSS_TM_AID_LIST {
    /**
     * <div class="zh">AID����</div>
     * <div class="en">AID length</div>
     */			
	public byte ucAidLen;     
    /**
     * <div class="zh">AID, 5~16�ֽ�</div>
     * <div class="en">AID, 5~16bytes</div>
     */		
	public final byte[] aucAID;	//[17];   // 20090609 liuxl ����һ�ֽ� 
    /**
     * <div class="zh">����ѡ���ʶ(1-����ƥ�䣬0-��ȫƥ��)</div>
     * <div class="en">partial matching flag(1-partial matching. 0-full matching)</div>
     */		
	public byte ucSelFlg;	 // ����ѡ���ʶ(1-����ƥ�䣬0-��ȫƥ��)
    /**
     * <div class="zh">�ں�����</div>
     * <div class="en">kernel type</div>
     */		
	public byte ucKernType; // 20090609 liuxl ����

    /**
     * <div class="zh"> ����һ��CLSS_TM_AID_LIST����</div>
     * <div class="en"> create an CLSS_TM_AID_LIST instance </div>
     */
    public CLSS_TM_AID_LIST() {
    	aucAID = new byte[17];   
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

    	ss.put(ucAidLen);     
    	ss.put(aucAID);	//[17];   // 20090609 liuxl ����һ�ֽ� 
    	ss.put(ucSelFlg);	 // ����ѡ���ʶ(1-����ƥ�䣬0-��ȫƥ��)
    	ss.put(ucKernType); // 20090609 liuxl ����

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
        
    	ucAidLen = ss.get();     
    	ss.get(aucAID);	//[17];   // 20090609 liuxl ����һ�ֽ� 
    	ucSelFlg = ss.get();	 // ����ѡ���ʶ(1-����ƥ�䣬0-��ȫƥ��)
    	ucKernType = ss.get(); // 20090609 liuxl ����

    }
}
