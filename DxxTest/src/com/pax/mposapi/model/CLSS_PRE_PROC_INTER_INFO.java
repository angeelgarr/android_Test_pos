package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">����ѡ��Ӧ��AID��Ӧ���ڽ���Ԥ��������ж�̬���õ��ڲ�����,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">internal flag parameters which are set dynamically during preliminary transaction processing, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class CLSS_PRE_PROC_INTER_INFO {
	
    /**
     * <div class="zh">AID, 5~16�ֽ�</div>
     * <div class="en">AID, 5~16bytes</div>
     */		
	public final byte[] aucAID;//[17];       
    /**
     * <div class="zh">AID����</div>
     * <div class="en">AID length</div>
     */		
	public byte ucAidLen; 

	// payWave
    /**
     * <div class="zh">0-���׽��!=0; 1-���׽��=0</div>
     * <div class="en">0-transaction amount!=0; 1-transaction amount=0</div>
     */		
	public byte   ucZeroAmtFlg;       // 0-���׽��!=0; 1-���׽��=0
    /**
     * <div class="zh">�������Ƿ�֧��״̬���</div>
     * <div class="en"> whether supports status check or not</div>
     */		
	public byte   ucStatusCheckFlg;    // �������Ƿ�֧��״̬���
    /**
     * <div class="zh">�ն˽������ܣ�����VISA/PBOC�У�tag =9F66, 5�ֽ�</div>
     * <div class="en">Terminal transaction Qualifiers, used in VISA/PBOC, tag =9F66</div>
     */		
    public final byte[]   aucReaderTTQ;//[5];      // �ն˽������ܣ�����VISA/PBOC�У�tag =9F66
    /**
     * <div class="zh">1-��AID���ܽ��зǽӴ�����</div>
     * <div class="en">1-the AID doen't support contactless transaction,  0- the AID support contactless transaction </div>
     */		
	public byte   ucCLAppNotAllowed; // 1-��AID���ܽ��зǽӴ�����
	
	// common
    /**
     * <div class="zh">�����ն���ͽ����޶��־, 0-��, 1-��</div>
     * <div class="en">Whether the Terminal Floor Limit is exceeded or not, 0-no, 1-yes</div>
     */		
	public byte ucTermFLmtExceed; 
    /**
     * <div class="zh">�����ն˷ǽӽ������������޶��־, 0-��, 1-��</div>
     * <div class="en">Whether the Reader Contactless Transaction Limit is exceeded or not, 0-no, 1-yes</div>
     */		
	public byte ucRdCLTxnLmtExceed; 
    /**
     * <div class="zh">�����ն�CVM required�����޶��־, 0-��, 1-��</div>
     * <div class="en">Whether the Reader CVM Required Limit is exceeded or not, 0-no, 1-yes</div>
     */		
	public byte ucRdCVMLmtExceed;  
    /**
     * <div class="zh">�����ն˷ǽӽ���Ҫ�����߽��׵�����޶��־, 0-��, 1-��</div>
     * <div class="en">Whether the Reader Contactless Floor Limit is exceeded or not, 0-no, 1-yes</div>
     */		
	public byte ucRdCLFLmtExceed;  

    /**
     * <div class="zh">�Ƿ�ʹ���ն���ͽ����޶�, 0-��, 1-��</div>
     * <div class="en"> Is Terminal Floor Limit present? 0-no, 1-yes</div>
     */		
	public byte ucTermFLmtFlg;
    /**
     * <div class="zh">�ն���ͽ����޶�, 4�ֽ�</div>
     * <div class="en">Terminal Floor Limit, 4 bytes</div>
     */		
	public final byte[] aucTermFLmt;//[5];
	
    /**
     * <div class="zh">����, 2�ֽ�</div>
     * <div class="en">reserved, 2 bytes</div>
     */		
	public final byte[] aucRFU;//[2];

    /**
     * <div class="zh"> ����һ��CLSS_PRE_PROC_INTER_INFO����</div>
     * <div class="en"> create an CLSS_PRE_PROC_INTER_INFO instance </div>
     */
    public CLSS_PRE_PROC_INTER_INFO() {
    	aucAID = new byte[17];   
    	aucReaderTTQ = new byte[5];   
    	aucTermFLmt = new byte[5];
    	aucRFU = new byte[2]; 
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

    	ss.put(aucAID);//[17];
    	ss.put(ucAidLen);

    	//payWave
    	ss.put(ucZeroAmtFlg);//0-���׽��!=0);1-���׽��=0
    	ss.put(ucStatusCheckFlg);//�������Ƿ�֧��״̬���
        ss.put(aucReaderTTQ);//[5];//�ն˽������ܣ�����VISA/PBOC�У�tag=9F66
    	ss.put(ucCLAppNotAllowed);//1-��AID���ܽ��зǽӴ�����
    	
    	//common
    	ss.put(ucTermFLmtExceed);
    	ss.put(ucRdCLTxnLmtExceed);
    	ss.put(ucRdCVMLmtExceed);
    	ss.put(ucRdCLFLmtExceed);

    	ss.put(ucTermFLmtFlg);
    	ss.put(aucTermFLmt);//[5];
    	
    	ss.put(aucRFU);//[2];

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
        
    	 ss.get(aucAID);//[17] 
    	ucAidLen = ss.get();

    	//payWave
    	ucZeroAmtFlg = ss.get();//0-���׽��! = 0 = ss.get();1-���׽�� = 0
    	ucStatusCheckFlg = ss.get();//�������Ƿ�֧��״̬���
        ss.get(aucReaderTTQ);//[5] //�ն˽������ܣ�����VISA/PBOC�У�tag = 9F66
    	ucCLAppNotAllowed = ss.get();//1-��AID���ܽ��зǽӴ�����
    	
    	//common
    	ucTermFLmtExceed = ss.get();
    	ucRdCLTxnLmtExceed = ss.get();
    	ucRdCVMLmtExceed = ss.get();
    	ucRdCLFLmtExceed = ss.get();

    	ucTermFLmtFlg = ss.get();
    	ss.get(aucTermFLmt);//[5] = 
    	
    	ss.get(aucRFU);//[2] = 

    }
}
