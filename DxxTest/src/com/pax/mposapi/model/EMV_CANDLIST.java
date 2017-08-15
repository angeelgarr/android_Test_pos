package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">EMVӦ���б�,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">EMV application list, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class EMV_CANDLIST {
	/**
	 * <div class="zh">
	 * Ӧ������ѡ������
	 * </div>
	 * <div class="en">
	 * application preferred name
	 * </div>
	 */
	public final byte[] aucAppPreName;//[17];
	
	/**
	 * <div class="zh">
	 * Ӧ�ñ�ǩ
	 * </div>
	 * <div class="en">
	 * application label
	 * </div>
	 */
	public final byte[] aucAppLabel;//[17];
	
	/**
	 * <div class="zh">
	 * tag 'BF0C'����: 1���ֽڵĳ����ֽ�+'BF0C'���222���ֽ�; tag '73'����: 1���ֽڵĳ����ֽ�+'73'���242���ֽ�
	 * </div>
	 * <div class="en">
	 * tag 'BF0C': 1 byte data length + maximum 222 data bytes;  tag '73': 1 byte data length + maximum 242 data bytes
	 * </div>
	 */
	public final byte[] aucIssDiscrData;//[244];
	
	/**
	 * <div class="zh">Ӧ�ñ�־����AppName��Ӧ��Ӧ�ñ�־, EMV�������˱�־������Ӧ�ã�, �16�ֽ�</div>
	 * <div class="en">Application ID, 16 bytes in maximum, (AID is corresponding to  the AppName, The kernel searchs application according to the AID)</div>
	 */
	public final byte[] aucAID;//[17];           
	/**
	 * <div class="zh">Ӧ�ñ�־�ĳ���</div>
	 * <div class="en">AID length</div>
	 */
	public byte ucAidLen;            
         
	/**
	 * <div class="zh">���ȼ���־����Ƭ����, Ӧ�ÿ��Բ�����</div>
	 * <div class="en">priority indicator(It's returned by ICC, so nothing needs to be done by application)</div>
	 */
	public byte ucPriority;          

    /**
     * <div class="zh"> ����һ��EMV_APPLIST����</div>
     * <div class="en"> create an EMV_APPLIST instance </div>
     */
    public EMV_CANDLIST() {
    	aucAppPreName = new byte[17];
    	aucAppLabel = new byte[17];
    	aucIssDiscrData = new byte[244];
    	aucAID = new byte[17];
    }

    /**
     * <div class="zh">
     * ����object�е�����д��byte����
     * </div>
     * <div class="en">
     * get data from this object and write to a byte array,
     * </div>
     *
     * @return
     * <div class="zh">
     * 	�õ��İ�����object���ݵ�byte����,
     * </div>
     * <div class="en">
     * 	a byte array including data of this object,
     * </div>
     */  
    public byte[] serialToBuffer() {
        ByteBuffer ss = ByteBuffer.allocate(1024);
        ss.order(ByteOrder.BIG_ENDIAN);
        ss.clear();

    	ss.put(aucAppPreName);       
    	ss.put(aucAppLabel);
    	ss.put(aucIssDiscrData);            
        ss.put(aucAID);               
    	ss.put(ucAidLen);      
    	ss.put(ucPriority);      
    	
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
     * @param offset
     * <div class="zh">
     *   ƫ����, ����bb�ĵ�offset�ֽڿ�ʼ����
     * </div>
     * <div class="en">
     *   the offset from which the data is read
     * </div>
     * 
     */
    public void serialFromBuffer(byte[] bb, int offset) {
        ByteBuffer ss = ByteBuffer.wrap(bb, offset, bb.length - offset);
        ss.order(ByteOrder.BIG_ENDIAN);
        
    	ss.get(aucAppPreName);
    	ss.get(aucAppLabel);
    	ss.get(aucIssDiscrData);
    	ss.get(aucAID);
    	ucAidLen=ss.get();            
    	ucPriority=ss.get();               
    }
}
