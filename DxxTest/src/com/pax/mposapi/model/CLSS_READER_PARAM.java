package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">��������ز���,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">reader parameter, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class CLSS_READER_PARAM {
	
	/**
	 * <div class="zh">�ο����Ҵ���ͽ��״����ת��ϵ��(���׻��ҶԲο����ҵĻ���*1000)</div>
	 * <div class="en">Transform modulus of referenced currency code and transaction currency code (exchange rate from transaction currency to referenced currency *1000)</div>
	 */
	public int   ulReferCurrCon;       // �ο����Ҵ���ͽ��״����ת��ϵ��(���׻��ҶԲο����ҵĻ���*1000)

	/**
	 * <div class="zh">�̻����Ƽ�λ��������ĳ���</div>
	 * <div class="en">The length of Merchant Name and Location</div>
	 */
	public short  usMchLocLen;          // �̻����Ƽ�λ��������ĳ���
	/**
	 * <div class="zh">�̻����Ƽ�λ��(1-256 �ֽ�)</div>
	 * <div class="en"> Merchant Name and Location(1-256 Byte)</div>
	 */
	public final byte[]   aucMchNameLoc;//[257];   // �̻����Ƽ�λ��(1-256 �ֽ�)
	/**
	 * <div class="zh"> �̻�������'9F15'(2�ֽ�)</div>
	 * <div class="en">Merchant category code '9F15'(2Bytes)</div>
	 */
	public final byte[]   aucMerchCatCode;//[2];   // �̻�������'9F15'(2�ֽ�) 
	/**
	 * <div class="zh">�̻���ʶ(15�ֽ�) </div>
	 * <div class="en"> Merchant Identifier(15Bytes)</div>
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
     * <div class="zh">�ն˹��Ҵ���, 2�ֽ�</div>
     * <div class="en">Terminal Country Code, 2 bytes</div>
     */
    public final byte[]   aucTmCntrCode ;//[2];     // �ն˹��Ҵ���
    /**
     * <div class="zh">�ն˽��׻��Ҵ���'5F2A'(2�ֽ�)</div>
     * <div class="en">Transaction Currency Code '5F2A'(2Bytes)</div>
     */
	public final byte[]   aucTmTransCur;//[2];      // �ն˽��׻��Ҵ���'5F2A'(2�ֽ�) 
	/**
	 * <div class="zh">�ն˽��׻���ָ��'5F36'(1�ֽ�)</div>
	 * <div class="en"> Transaction Currency Exponent '5F36'(1Byte)</div>
	 */
	public byte   ucTmTransCurExp;       // �ն˽��׻���ָ��'5F36'(1�ֽ�)

	/**
	 * <div class="zh">�ն˽��ײο����Ҵ���'9F3C'(2�ֽ�)</div>
	 * <div class="en">Transaction Reference Currency Code '9F3C'(2Bytes)</div>
	 */
	public final byte[]   aucTmRefCurCode;//[2];    // �ն˽��ײο����Ҵ���'9F3C'(2�ֽ�)
	/**
	 * <div class="zh">�ն˽��ײο�����ָ��'9F3D'(1�ֽ�)</div>
	 * <div class="en">Transaction Reference Currency Exponent '9F3D'(1Byte)</div>
	 */
	public byte   ucTmRefCurExp;	       // �ն˽��ײο�����ָ��'9F3D'(1�ֽ�)

	/**
	 * <div class="zh">����, 3�ֽ�</div>
	 * <div class="en">reserved, 3 bytes</div>
	 */
	public final byte[]   aucRFU;//[3];

    /**
     * <div class="zh"> ����һ��CLSS_READER_PARAM����</div>
     * <div class="en"> create an CLSS_READER_PARAM instance </div>
     */
    public CLSS_READER_PARAM() {
		aucMchNameLoc = new byte[257];//�̻����Ƽ�λ��(1-256�ֽ�)
		aucMerchCatCode = new byte[2];//�̻�������'9F15'(2�ֽ�)
		aucMerchantID = new byte[15];//�̻���ʶ(15�ֽ�)
		
		AcquierId = new byte[6];//�յ��б�־
		
		aucTmID = new byte[8];//�ն˱�ʶ(�ն˺�)
		aucTmCap = new byte[3];//�ն�����
		aucTmCapAd = new byte[5];//�ն˸�������
		
		aucTmCntrCode = new byte[2];//�ն˹��Ҵ���
		aucTmTransCur = new byte[2];//�ն˽��׻��Ҵ���'5F2A'(2�ֽ�)
		
		aucTmRefCurCode = new byte[2];//�ն˽��ײο����Ҵ���'9F3C'(2�ֽ�)
		
		aucRFU = new byte[3];
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

		ss.putInt(ulReferCurrCon);//�ο����Ҵ���ͽ��״����ת��ϵ��(���׻��ҶԲο����ҵĻ���*1000)
		
		ss.putShort(usMchLocLen);//�̻����Ƽ�λ��������ĳ���
		ss.put(aucMchNameLoc);//[257] //�̻����Ƽ�λ��(1-256�ֽ�)
		ss.put(aucMerchCatCode);//[2] //�̻�������'9F15'(2�ֽ�)
		ss.put(aucMerchantID);//[15] //�̻���ʶ(15�ֽ�)
		
		ss.put(AcquierId);//[6] //�յ��б�־
		
		ss.put(aucTmID);//[8] //�ն˱�ʶ(�ն˺�)
		ss.put(ucTmType);//�ն�����
		ss.put(aucTmCap);//[3] //�ն�����
		ss.put(aucTmCapAd);//[5] //�ն˸�������
		
		ss.put(aucTmCntrCode);//[2] //�ն˹��Ҵ���
		ss.put(aucTmTransCur);//[2] //�ն˽��׻��Ҵ���'5F2A'(2�ֽ�)
		ss.put(ucTmTransCurExp);//�ն˽��׻���ָ��'5F36'(1�ֽ�)
		
		ss.put(aucTmRefCurCode);//[2] //�ն˽��ײο����Ҵ���'9F3C'(2�ֽ�)
		ss.put(ucTmRefCurExp);	//�ն˽��ײο�����ָ��'9F3D'(1�ֽ�)
		
		ss.put(aucRFU);//[3];
		
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
        
        ulReferCurrCon = ss.getInt();  //�ο����Ҵ���ͽ��״����ת��ϵ��(���׻��ҶԲο����ҵĻ���*1000)
		
        usMchLocLen = ss.getShort();  //�̻����Ƽ�λ��������ĳ���
		ss.get(aucMchNameLoc);  //[257] //�̻����Ƽ�λ��(1-256�ֽ�)
		ss.get(aucMerchCatCode);  //[2] //�̻�������'9F15'(2�ֽ�)
		ss.get(aucMerchantID);  //[15] //�̻���ʶ(15�ֽ�)
		
		ss.get(AcquierId);  //[6] //�յ��б�־
		
		ss.get(aucTmID);  //[8] //�ն˱�ʶ(�ն˺�)
		ucTmType = ss.get();  //�ն�����
		ss.get(aucTmCap);  //[3] //�ն�����
		ss.get(aucTmCapAd);  //[5] //�ն˸�������
		
		ss.get(aucTmCntrCode);  //[2] //�ն˹��Ҵ���
		ss.get(aucTmTransCur);  //[2] //�ն˽��׻��Ҵ���'5F2A'(2�ֽ�)
		ucTmTransCurExp = ss.get();  //�ն˽��׻���ָ��'5F36'(1�ֽ�)
		
		ss.get(aucTmRefCurCode);  //[2] //�ն˽��ײο����Ҵ���'9F3C'(2�ֽ�)
		ucTmRefCurExp = ss.get();	//�ն˽��ײο�����ָ��'9F3D'(1�ֽ�)
		
		ss.get(aucRFU);
    }
}
