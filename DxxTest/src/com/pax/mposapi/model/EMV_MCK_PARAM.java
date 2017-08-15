package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">MCK������صĲ���,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">MCK parameter, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class EMV_MCK_PARAM {

	/**
	 * <div class="zh">0-��֧��, 1��֧��, Ĭ��֧��</div>
	 * <div class="en">0- Not supported, 1-Supported. Default value is 1</div>
	 */
	public byte ucBypassPin;
	/**
	 * <div class="zh">0-ODC, 1-BDC,Ĭ��ΪBDC (ODC: Online Data Capture; BDC: Batch Data Capture)</div>
	 * <div class="en">0-ODC, 1-BDC. Default value is BDC(ODC: Online Data Capture; BDC: Batch Data Capture)</div>
	 */
	public byte ucBatchCapture;
	/**
	 * <div class="zh">0-�ÿ�ƬAIP, 1-���ն�AIP, Ĭ��Ϊ0</div>
	 * <div class="en">0-TRM is based on AIP of card, 1-TRM is based on AIP of Terminal,  the default value is 0.</div>
	 */
	public byte ucUseTermAIPFlg;
	/**
	 * <div class="zh">�ն��Ƿ�ǿ�ƽ��з��չ���, byte1-bit4Ϊ1��ǿ�ƽ��з��չ���byte1-bit4Ϊ0�������з��չ���Ĭ�������ֽ�ȫΪ0, 2�ֽ�</div>
	 * <div class="en">The bit4 of byte1 decide whether force to perform TRM,  "08 00"- Yes; "00 00"- No. Default value is "00 00"</div>
	 */
	public final byte[] aucTermAIP;	//[2]
	/**
	 * <div class="zh">������һ��PIN���Ƿ������������PIN  1-��, 0-��</div>
	 * <div class="en">whether bypass all other pin when one pin has been bypassed 1-Yes, 0-No</div>
	 */
	public byte ucBypassAllFlg;

    /**
     * <div class="zh"> ����һ��EMV_MCK_PARAM����</div>
     * <div class="en"> create an EMV_MCK_PARAM instance </div>
     */
    public EMV_MCK_PARAM() {
    	aucTermAIP = new byte[2];
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

    	ss.put(ucBypassPin);
    	ss.put(ucBatchCapture);
    	ss.put(ucUseTermAIPFlg);
    	ss.put(aucTermAIP);
    	ss.put(ucBypassAllFlg);
    	
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
        
        ucBypassPin = ss.get();
        ucBatchCapture = ss.get();
        ucUseTermAIPFlg = ss.get();
    	ss.get(aucTermAIP);
    	ucBypassAllFlg = ss.get();
    	
    }
}
