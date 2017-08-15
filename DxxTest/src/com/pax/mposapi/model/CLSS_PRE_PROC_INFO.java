package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">AID��Ӧ�Ľ���Ԥ�������,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">pre-processing parameter, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class CLSS_PRE_PROC_INFO {
    /**
     * <div class="zh">�ն�����޶�(ͬEMV L2)</div>
     * <div class="en">Terminal floor limit of terminal (the same as terminal floor limit of EMV L2)</div>
     */		
	public int ulTermFLmt;
    /**
     * <div class="zh">�ǽӽ����޶�(�����Ȩ�����ڸ� ֵ�����ʹ�������ӿڽ��н���)</div>
     * <div class="en">Contactless transaction limit of reader(other transaction interface must be used if authorized amount is greater than this value)</div>
     */		
	public int ulRdClssTxnLmt;
    /**
     * <div class="zh">Ҫ�� ִ��CVM������޶�</div>
     * <div class="en">CVM required limit of reader</div>
     */		
	public int ulRdCVMLmt;
    /**
     * <div class="zh">�ǽ�����޶�(���ڴ�ֵ������������)</div>
     * <div class="en">Contactless floor limit of reader</div>
     */		
	public int ulRdClssFLmt;

    /**
     * <div class="zh">AID, 5~16�ֽ�</div>
     * <div class="en">AID, 5~16 bytes</div>
     */		
	public final byte[] aucAID;//[17];       
    /**
     * <div class="zh">AID����</div>
     * <div class="en">AID length</div>
     */		
	public byte ucAidLen; 
	
    /**
     * <div class="zh">��AID��Ӧ���ں�����(���� VISA, MasterCard, PBOC)</div>
     * <div class="en">Kernel type corresponding to the AID(e.g. VISA, MasterCard, PBOC)</div>
     */		
	public byte ucKernType; // ���������AID ����ѭ���ں�����

	// payWave
    /**
     * <div class="zh">���İ汾17�Ƿ�֧��  1-��, 0-��</div>
     * <div class="en">Is cryptogram version 17 supported? 1-yes, 0-no</div>
     */		
	public byte  ucCrypto17Flg;
    /**
     * <div class="zh">
     * 0-����Ȩ���=0,��TTQҪ����������, <br/>
     * 1-����Ȩ���=0, ���ڲ�qVSDC��֧�ֱ�־��λ
     * </div>
     * <div class="en">
     * 0-TTQ requires online cryptogram when authorized amount is equal to 0<br/>
     * 1-Internal qVSDC does not support flag bit setting when authorized amount is equal to 0
     * </div>
     */		
	public byte   ucZeroAmtNoAllowed;       // 0-����Ȩ���=0,��TTQҪ����������, 1-����Ȩ���=0, ���ڲ�qVSDC��֧�ֱ�־��λ
    /**
     * <div class="zh">�������Ƿ�֧��״̬���, 1-��, 0-��</div>
     * <div class="en">Does card reader support status check? 1-yes, 0-no</div>
     */		
	public byte   ucStatusCheckFlg;    // �������Ƿ�֧��״̬���
    /**
     * <div class="zh">�ն˽�������, ����VISA/PBOC��, tag =9F66, 5 �ֽ�</div>
     * <div class="en">Reader's terminal transaction qualifiers, used in VISA/PBOC, tag =9F66, 5 bytes</div>
     */		
    public final byte[]   aucReaderTTQ;//[5];      // �ն˽�������, ����VISA/PBOC��, tag =9F66
	
	// common
    /**
     * <div class="zh">�ն�����޶�(ͬEMV L2)�Ƿ���Ч��־</div>
     * <div class="en">whether the Terminal Floor Limit is present (the same as Terminal Floor Limit of EMV L2)</div>
     */		
	public byte ucTermFLmtFlg; 
    /**
     * <div class="zh">�ǽӽ�������޶��Ƿ���Ч��־</div>
     * <div class="en">whether reader Contactless Transaction Limit is present</div>
     */		
	public byte ucRdClssTxnLmtFlg; 
    /**
     * <div class="zh">Ҫ�� ִ��CVM������޶��Ƿ���Ч��־</div>
     * <div class="en">whether reader CVM Required Limit is present</div>
     */		
	public byte ucRdCVMLmtFlg;   
    /**
     * <div class="zh">�ǽ�����޶�(���ڴ�ֵ������������)�Ƿ���Ч��־</div>
     * <div class="en">wheterh reader Contactless Floor Limit is present</div>
     */		
	public byte ucRdClssFLmtFlg; 	 
	
    /**
     * <div class="zh">����, 2�ֽ�</div>
     * <div class="en">reserved, 2 bytes</div>
     */		
	public final byte[] aucRFU;//[2];

    /**
     * <div class="zh"> ����һ��CLSS_PRE_PROC_INFO����</div>
     * <div class="en"> create an CLSS_PRE_PROC_INFO instance </div>
     */
    public CLSS_PRE_PROC_INFO() {
    	aucAID = new byte[17];   
    	aucReaderTTQ = new byte[5];   
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

    	ss.putInt(ulTermFLmt);
    	ss.putInt(ulRdClssTxnLmt);
    	ss.putInt(ulRdCVMLmt);
    	ss.putInt(ulRdClssFLmt);

    	ss.put(aucAID);//[17]);
    	ss.put(ucAidLen);
    	
    	ss.put(ucKernType);//���������AID����ѭ���ں�����

    	//payWave
    	ss.put(ucCrypto17Flg);
    	ss.put(ucZeroAmtNoAllowed);//0-����Ȩ���=0,��TTQҪ����������,1-����Ȩ���=0,���ڲ�qVSDC��֧�ֱ�־��λ
    	ss.put(ucStatusCheckFlg);//�������Ƿ�֧��״̬���
        ss.put(aucReaderTTQ);//[5];//�ն˽�������, ����VISA/PBOC��, tag=9F66
    	
    	//common
    	ss.put(ucTermFLmtFlg);
    	ss.put(ucRdClssTxnLmtFlg);
    	ss.put(ucRdCVMLmtFlg);
    	ss.put(ucRdClssFLmtFlg);	
    	
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
        
        ulTermFLmt = ss.getInt();
        ulRdClssTxnLmt = ss.getInt();
        ulRdCVMLmt = ss.getInt();
        ulRdClssFLmt = ss.getInt();

    	ss.get(aucAID);//[17]);
    	ucAidLen = ss.get();
    	
    	ucKernType = ss.get();//���������AID����ѭ���ں�����

    	//payWave
    	ucCrypto17Flg = ss.get();
    	ucZeroAmtNoAllowed = ss.get();//0-����Ȩ���=0,��TTQҪ����������,1-����Ȩ���=0,���ڲ�qVSDC��֧�ֱ�־��λ
    	ucStatusCheckFlg = ss.get();//�������Ƿ�֧��״̬���
        ss.get(aucReaderTTQ);//[5]);//�ն˽�������, ����VISA/PBOC��, tag=9F66
    	
    	//common
        ucTermFLmtFlg = ss.get();
        ucRdClssTxnLmtFlg = ss.get();
        ucRdCVMLmtFlg = ss.get();
        ucRdClssFLmtFlg = ss.get();	
    	
    	ss.get(aucRFU);//[2]);

    }
}
