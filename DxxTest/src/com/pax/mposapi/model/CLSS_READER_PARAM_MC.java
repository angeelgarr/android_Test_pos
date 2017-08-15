package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Reader parameter for PayPass, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array
 */
public class CLSS_READER_PARAM_MC {

	/**
	 * Merchant category code '9F15'(2Bytes)
	 */
	public final byte[]   aucMerchCatCode;//[2];   // �̻�������'9F15'(2�ֽ�) 
	/**
	 * Merchant Identifier(15Bytes)
	 */
	public final byte[]   aucMerchantID;//[15];    // �̻���ʶ(15�ֽ�) 
	
	/**
	 * <div class="zh">�յ��б�־, 6�ֽ�</div>
	 * <div class="en">Acquirer Identifier, 6 bytes</div>
	 */
	public final byte[] AcquierId;//[6];       //�յ��б�־

	/**
	 * <div class="zh">�ն˱�ʶ(�ն˺�), 8�ֽ� </div>
	 * <div class="en">Terminal Identification(Terminal number), 8 bytes</div>
	 */
    public final byte[]   aucTmID;//[8];           // �ն˱�ʶ(�ն˺�) 
    /**
     * <div class="zh">�ն�����</div>
     * <div class="en">Terminal Type</div>
     */
	public byte   ucTmType;             // �ն�����
	/**
	 * <div class="zh">�ն�����, 3�ֽ�</div>
	 * <div class="en">Terminal Capabilities, 3 bytes</div>
	 */
	public final byte[]   aucTmCap;//[3];          // �ն�����
	/**
	 * <div class="zh">�ն˸�������, 5�ֽ�</div>
	 * <div class="en">Additional Terminal Capabilities, 5 bytes</div>
	 */
    public final byte[]   aucTmCapAd;//[5];        // �ն˸�������

	/**
	 * if aucTmCntrCode valid or not. 1 for yes, 0 otherwise
	 */		    
    public byte ucTmCntrCodeFlg;
    
    /**
     * <div class="zh">�ն˹��Ҵ���, 2�ֽ�</div>
     * <div class="en">Terminal Country Code, 2 bytes</div>
     */
    public final byte[]   aucTmCntrCode ;//[2];     // �ն˹��Ҵ���
    
	/**
	 * if aucTmTransCur valid or not. 1 for yes, 0 otherwise
	 */		    
    public byte ucTmTransCurFlg;
    
    /**
     * <div class="zh">�ն˽��׻��Ҵ���'5F2A'(2�ֽ�)</div>
     * <div class="en">Transaction Currency Code '5F2A'(2Bytes)</div>
     */
	public final byte[]   aucTmTransCur;//[2];      // �ն˽��׻��Ҵ���'5F2A'(2�ֽ�) 

	/**
	 * if ucTmTransCurExp valid or not. 1 for yes, 0 otherwise
	 */			
    public byte ucTransCurExpFlg;
    
	/**
	 * <div class="zh">�ն˽��׻���ָ��'5F36'(1�ֽ�)</div>
	 * <div class="en"> Transaction Currency Exponent '5F36'(1Byte)</div>
	 */
	public byte   ucTransCurExp;       // �ն˽��׻���ָ��'5F36'(1�ֽ�)

	/**
	 * if ucTransCateCode valid or not. 1 for yes, 0 otherwise
	 */	
	public byte ucTransCateCodeFlg;
	
	/**
	 * Transaction Category Code '9F53'
	 */		
	public byte ucTransCateCode;
	
	/**
	 * <div class="zh">����, 2�ֽ�</div>
	 * <div class="en">reserved, 2 bytes</div>
	 */
	public final byte[]   aucRFU;//[2];

    /**
     * <div class="zh"> ����һ��CLSS_READER_PARAM_MC����</div>
     * <div class="en"> create an CLSS_READER_PARAM_MC instance </div>
     */
    public CLSS_READER_PARAM_MC() {
		aucMerchCatCode = new byte[2];//�̻�������'9F15'(2�ֽ�)
		aucMerchantID = new byte[15];//�̻���ʶ(15�ֽ�)
		
		AcquierId = new byte[6];//�յ��б�־
		
		aucTmID = new byte[8];//�ն˱�ʶ(�ն˺�)
		aucTmCap = new byte[3];//�ն�����
		aucTmCapAd = new byte[5];//�ն˸�������
		
		aucTmCntrCode = new byte[2];//�ն˹��Ҵ���
		aucTmTransCur = new byte[2];//�ն˽��׻��Ҵ���'5F2A'(2�ֽ�)
		
		aucRFU = new byte[2];
    }

    /**
     * <div class="zh">
     * ����object�е�����д��byte����.
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

		ss.put(aucMerchCatCode);//[2] //�̻�������'9F15'(2�ֽ�)
		ss.put(aucMerchantID);//[15] //�̻���ʶ(15�ֽ�)
		
		ss.put(AcquierId);//[6] //�յ��б�־
		
		ss.put(aucTmID);//[8] //�ն˱�ʶ(�ն˺�)
		ss.put(ucTmType);//�ն�����
		ss.put(aucTmCap);//[3] //�ն�����
		ss.put(aucTmCapAd);//[5] //�ն˸�������
		
		ss.put(ucTmCntrCodeFlg);
		ss.put(aucTmCntrCode);//[2] //�ն˹��Ҵ���
		
		ss.put(ucTmTransCurFlg);
		ss.put(aucTmTransCur);//[2] //�ն˽��׻��Ҵ���'5F2A'(2�ֽ�)
		
		ss.put(ucTransCurExpFlg);
		ss.put(ucTransCurExp);//�ն˽��׻���ָ��'5F36'(1�ֽ�)
		
		ss.put(ucTransCateCodeFlg);
		ss.put(ucTransCateCode);
		
		ss.put(aucRFU);//[2];
		
        ss.flip();
        byte[] ret = new byte[ss.limit()];
        ss.get(ret);
        return ret;
    }

    /**
     * <div class="zh">
     * ��һ��byte�����ж�ȡ���ݲ���¼�ڱ�object��.
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
        
		ss.get(aucMerchCatCode);  //[2] //�̻�������'9F15'(2�ֽ�)
		ss.get(aucMerchantID);  //[15] //�̻���ʶ(15�ֽ�)
		
		ss.get(AcquierId);  //[6] //�յ��б�־
		
		ss.get(aucTmID);  //[8] //�ն˱�ʶ(�ն˺�)
		ucTmType = ss.get();  //�ն�����
		ss.get(aucTmCap);  //[3] //�ն�����
		ss.get(aucTmCapAd);  //[5] //�ն˸�������
		
		ucTmCntrCodeFlg = ss.get();
		ss.get(aucTmCntrCode);  //[2] //�ն˹��Ҵ���
		
		ucTmTransCurFlg = ss.get();
		ss.get(aucTmTransCur);  //[2] //�ն˽��׻��Ҵ���'5F2A'(2�ֽ�)
		
		ucTransCurExpFlg = ss.get();
		ucTransCurExp = ss.get();  //�ն˽��׻���ָ��'5F36'(1�ֽ�)
		
		ucTransCateCodeFlg = ss.get();
		ucTransCateCode = ss.get();
		
		ss.get(aucRFU);
    }
}
