package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">�ǽӽ�����ز���,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">contactless transaction paramter, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class CLSS_TRANS_PARAM {
	
	/**
	 * <div class="zh">��Ȩ���</div>
	 * <div class="en">authorized amount</div>
	 */
    public int  ulAmntAuth;     // ��Ȩ���(public int)
    /**
     * <div class="zh">�������</div>
     * <div class="en">other amount</div>
     */
    public int  ulAmntOther;    // �������(public int) 
    /**
     * <div class="zh">�������м�����</div>
     * <div class="en">transaction number</div>
     */
    public int  ulTransNo;      // �������м�����(4 BYTE)
    /**
     * <div class="zh">��������'9C'</div>
     * <div class="en">transaction type(tag '9C')</div>
     */
	public byte  ucTransType;    // ��������'9C'
	/**
	 * <div class="zh">�������� YYMMDD, 3�ֽ�</div>
	 * <div class="en">transaction date YYMMDD, 3 bytes</div>
	 */
	public final byte[]  aucTransDate;//[4]; // �������� YYMMDD
	/**
	 * <div class="zh">����ʱ�� HHMMSS, 3�ֽ�</div>
	 * <div class="en">transaction time HHMMSS, 3 bytes</div>
	 */
	public final byte[]  aucTransTime;//[4]; // ����ʱ�� HHMMSS

    /**
     * <div class="zh"> ����һ��CLSS_TRANS_PARAM����</div>
     * <div class="en"> create an CLSS_TRANS_PARAM instance </div>
     */
    public CLSS_TRANS_PARAM() {
    	aucTransDate = new byte[4];
    	aucTransTime = new byte[4];
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

        ss.putInt(ulAmntAuth);     // ��Ȩ���(public int)
        ss.putInt(ulAmntOther);    // �������(public int) 
        ss.putInt(ulTransNo);      // �������м�����(4 BYTE)
    	ss.put(ucTransType);    // ��������'9C'
    	ss.put(aucTransDate, 0, 4);//[4]); // �������� YYMMDD
    	ss.put(aucTransTime);//[4]); // ����ʱ�� HHMMSS

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
        
        ulAmntAuth = ss.getInt();     // ��Ȩ���(public int)
        ulAmntOther = ss.getInt();    // �������(public int) 
        ulTransNo = ss.getInt();      // �������м�����(4 BYTE)
        ucTransType = ss.get();    // ��������'9C'
    	ss.get(aucTransDate);//[4]); // �������� YYMMDD
    	ss.get(aucTransTime);//[4]); // ����ʱ�� HHMMSS

    }
}
