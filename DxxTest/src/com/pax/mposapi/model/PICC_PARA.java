package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">��������PICC�Ĳ���,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">describes the PICC parameters, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class PICC_PARA {
	
	/**
	 * <div class="zh">
	 * ��������İ汾��Ϣ,��:"1.01A";ֻ�ܶ�ȡ,д����Ч, �5�ֽ�
	 * </div>
	 * <div class="en">
	 * The version information of Driver's, for example: "1.01A" can only be read
	 * </div>
	 */
    public final byte[] drv_ver; // [5]; //e.g. "1.01A", read only
	/**
	 * <div class="zh">
	 * ���������������Ϣ,��:"2006.08.25"; ֻ�ܶ�ȡ, �12�ֽ�
	 * </div>
	 * <div class="en">
	 * The date informaion of driver,such as:"2006.08.25"; Read only
	 * </div>
	 */
    public final byte[] drv_date; // [12]; //e.g. "2006.08.25",read only
	/**
	 * <div class="zh">
	 * A������絼д������:1--����,������������
	 * </div>
	 * <div class="en">
	 * The output conductance of A card is write enable:1_alowed,Others��not allowed
	 * </div>
	 */
    public byte a_conduct_w; // Type A conduct write enable: 1--enable,else
                             // disable
	/**
	 * <div class="zh">
	 * A������絼���Ʊ���,��Ч��Χ0~63,����ʱ��Ϊ63
	 * </div>
	 * <div class="en">
	 * The A card control variable of output conductance,invalid 0~63,over can be seen as 63,The defaut value is 63;
	 * </div>
	 */    
    public byte a_conduct_val;// Type A output conduct value,0~63
	/**
	 * <div class="zh">
	 * M1������絼д������
	 * </div>
	 * <div class="en">
	 * The output conductance of M1 card is write enable
	 * </div>
	 */
    public byte m_conduct_w; // M1 conduct write enable: 1--enable,else disable
	/**
	 * <div class="zh">
	 * M1������絼���Ʊ���,��Ч��Χ0~63,����ʱ��Ϊ63
	 * </div>
	 * <div class="en">
	 * The M1 card control variable of output conductance, invalid 0~63,over can be seen as 63,The defaut value is 1;
	 * </div>
	 */
    public byte m_conduct_val;// M1 output conduct value,0~63
	/**
	 * <div class="zh">
	 * B������ָ��д������
	 * </div>
	 * <div class="en">
	 * The modulation index of B card is written enable
	 * </div>
	 */
    public byte b_modulate_w; // Type B modulate index write
                              // enable,1--enable,else disable
	/**
	 * <div class="zh">
	 * B������ָ�����Ʊ���,��Ч��Χ0~63,����ʱ��Ϊ63
	 * </div>
	 * <div class="en">
	 * The B card control variable of modulation index, invalid 0~63,over can be seen as 63,The defaut value is 4
	 * </div>
	 */
    public byte b_modulate_val;// Type B modulate index value
	/**
	 * <div class="zh">
	 * ��Ƭ���ջ�������Сд������
	 * </div>
	 * <div class="en">
	 * Receiving buffer of card is written enable
	 * </div>
	 */
    public byte card_buffer_w;// added in V1.00C,20061204
	/**
	 * <div class="zh">
	 * ��Ƭ���ջ�������С����(��λ:�ֽ�),��Чֵ1~256.����256ʱ,����256д��;��Ϊ0ʱ,������д��
	 * </div>
	 * <div class="en">
	 * Receiving buffer parameter of card (Unit:byte), valid: 1~256. when over 256,it will use 256;if it is 0,it will not be written in
	 * </div>
	 */    
    public short card_buffer_val;// max_frame_size of PICC
	/**
	 * <div class="zh">
	 * S(WTX)��Ӧ���ʹ���д�������ݲ����ã�
	 * </div>
	 * <div class="en">
	 * Written enable for S(WTX) response to sending times
	 * </div>
	 */
    public byte wait_retry_limit_w;// added in V1.00F,20071212
	/**
	 * <div class="zh">
	 * S(WTX)��Ӧ������Դ������ݲ����ã�
	 * </div>
	 * <div class="en">
	 * The most repeat times of S(WTX) response, default is 3
	 * </div>
	 */
    public short wait_retry_limit_val;// max retry count for WTX block
                                    // requests,default 3

    // 20080617
	/**
	 * <div class="zh">
	 * ��Ƭ���ͼ��д������
	 * </div>
	 * <div class="en">
	 * Card type check is allow to write
	 * </div>
	 */
    public byte card_type_check_w;
	/**
	 * <div class="zh">
	 * 0-��鿨Ƭ����,����-����鿨Ƭ����(Ĭ��Ϊ��鿨Ƭ����)
	 * </div>
	 * <div class="en">
	 * 0-Check card type,other-do not check the card type(default to check the card type )
	 * </div>
	 */
    public byte card_type_check_val;

    // 2009-10-30
	/**
	 * <div class="zh">
	 * B��Ƭ����������д������:1--���� ����ֵ--��������ֵ���ɶ�
	 * </div>
	 * <div class="en">
	 * Receiver sensitivity of type B card is written enable: 1--allowed, others--disallowed
	 * </div>
	 */
    public byte card_RxThreshold_w;
	/**
	 * <div class="zh">
	 * B��Ƭ����������
	 * </div>
	 * <div class="en">
	 * Receiver sensitivity of type B card
	 * </div>
	 */
    public byte card_RxThreshold_val;

    // 2009-11-20
	/**
	 * <div class="zh">
	 * felica����ָ��д������
	 * </div>
	 * <div class="en">
	 * felica modulation depth allow to write: 1--allowed, others-- not allowed
	 * </div>
	 */
    public byte f_modulate_w;
	/**
	 * <div class="zh">
	 * felica����ָ��
	 * </div>
	 * <div class="en">
	 * felica modulation depth
	 * </div>
	 */
    public byte f_modulate_val;

    //add by wls 2011.05.17
	/**
	 * <div class="zh">
	 * A������ָ��д������:1--��������ֵ��������
	 * </div>
	 * <div class="en">
	 * type A card modulation depth allow to write: 1--allowed, others--disallowed
	 * </div>
	 */
    public byte a_modulate_w; // A������ָ��д������:1--��������ֵ��������
	/**
	 * <div class="zh">
	 * A������ָ�����Ʊ�������Ч��Χ0~63,����ʱ��Ϊ63
	 * </div>
	 * <div class="en">
	 * type A card modulation depth, 0~63.
	 * </div>
	 */
    public byte a_modulate_val; // A������ָ�����Ʊ�������Ч��Χ0~63,����ʱ��Ϊ63
    
             //add by wls 2011.05.17
	/**
	 * <div class="zh">
	 * A�����������ȼ��д������:1--��������ֵ--������
	 * </div>
	 * <div class="en">
	 * Receiver sensitivity of type A card allow to write: 1--allowed, others--disallowed
	 * </div>
	 */
    public byte a_card_RxThreshold_w; //���������ȼ��д������:1--��������ֵ--������
	/**
	 * <div class="zh">
	 * A������������
	 * </div>
	 * <div class="en">
	 * Receiver sensitivity of type A card
	 * </div>
	 */
    public byte a_card_RxThreshold_val;//A������������
    
    //add by liubo 2011.10.25, ���A,B��C������������
	/**
	 * <div class="zh">
	 * A����������д������:1--��������ֵ--������
	 * </div>
	 * <div class="en">
	 * Antenna gain of type A card allow to write: 1--allowed, others--disallowed
	 * </div>
	 */
    public byte a_card_antenna_gain_w;
	/**
	 * <div class="zh">
	 * A����������
	 * </div>
	 * <div class="en">
	 * Antenna gain of type A card
	 * </div>
	 */
    public byte a_card_antenna_gain_val;
	/**
	 * <div class="zh">
	 * B����������д������:1--��������ֵ--������
	 * </div>
	 * <div class="en">
	 * Antenna gain of type B card allow to write: 1--allowed, others--disallowed
	 * </div>
	 */    
    public byte b_card_antenna_gain_w;
	/**
	 * <div class="zh">
	 * B����������
	 * </div>
	 * <div class="en">
	 * Antenna gain of type B card
	 * </div>
	 */
    public byte b_card_antenna_gain_val;
	/**
	 * <div class="zh">
	 * Felia��������д������:1--��������ֵ--������
	 * </div>
	 * <div class="en">
	 * Antenna gain of type Felica allow to write: 1--allowed, others--disallowed
	 * </div>
	 */    
    public byte f_card_antenna_gain_w;
	/**
	 * <div class="zh">
	 * Felia��������
	 * </div>
	 * <div class="en">
	 * Antenna gain of type Felica
	 * </div>
	 */
    public byte f_card_antenna_gain_val;
    
    //added by liubo 2011.10.25�����Felica�Ľ���������
	/**
	 * <div class="zh">
	 * Felia����������д������:1--��������ֵ--������
	 * </div>
	 * <div class="en">
	 * The version information of Driver's, for example: "1.01A" can only be read
	 * </div>
	 */
    public byte f_card_RxThreshold_w;
	/**
	 * <div class="zh">
	 * Felia����������
	 * </div>
	 * <div class="en">
	 * Receiver sensitivity of Felica
	 * </div>
	 */
    public byte f_card_RxThreshold_val;
	/**
	 * <div class="zh">
	 * ����, 76�ֽ�
	 * </div>
	 * <div class="en">
	 * reserved, 76 bytes
	 * </div>
	 */
    public final byte[] reserved; // [76];

    /**
     * <div class="zh"> ����һ��PICC_PARA����</div>
     * <div class="en"> create an PICC_PARA instance </div>
     */
    public PICC_PARA() {
        drv_ver = new byte[5];
        drv_date = new byte[12];
        reserved = new byte[76];
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
        
        ss.clear();
        ss.order(ByteOrder.LITTLE_ENDIAN);
        ss.put(this.drv_ver);
        ss.put(this.drv_date);
        ss.put(this.a_conduct_w);
        ss.put(this.a_conduct_val);

        ss.put(this.m_conduct_w);
        ss.put(this.m_conduct_val);

        ss.put(this.b_modulate_w);
        ss.put(this.b_modulate_val);

        ss.put(this.card_buffer_w);
        ss.putShort(this.card_buffer_val);

        ss.put(this.wait_retry_limit_w);
        ss.putShort(this.wait_retry_limit_val);
        // 20080617
        ss.put(this.card_type_check_w);
        ss.put(this.card_type_check_val);

        // 2009-10-30
        ss.put(this.card_RxThreshold_w);
        ss.put(this.card_RxThreshold_val);

        // 2009-11-20
        ss.put(this.f_modulate_w);
        ss.put(this.f_modulate_val);

        ss.put(this.a_modulate_w);
        ss.put(this.a_modulate_val);
        ss.put(this.a_card_RxThreshold_w);
        ss.put(this.a_card_RxThreshold_val);
        ss.put(this.a_card_antenna_gain_w);
        ss.put(this.a_card_antenna_gain_val);
        ss.put(this.b_card_antenna_gain_w);
        ss.put(this.b_card_antenna_gain_val);
        ss.put(this.f_card_antenna_gain_w);
        ss.put(this.f_card_antenna_gain_val);
        ss.put(this.f_card_RxThreshold_w);
        ss.put(this.f_card_RxThreshold_val);
        
        ss.put(this.reserved);

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
        ss.order(ByteOrder.LITTLE_ENDIAN);
        ss.get(this.drv_ver);
        ss.get(this.drv_date);
        this.a_conduct_w = ss.get();
        this.a_conduct_val = ss.get();

        this.m_conduct_w = ss.get();
        this.m_conduct_val = ss.get();

        this.b_modulate_w = ss.get();
        this.b_modulate_val = ss.get();

        this.card_buffer_w = ss.get();
        this.card_buffer_val = ss.getShort();

        this.wait_retry_limit_w = ss.get();
        this.wait_retry_limit_val = ss.getShort();
        // 20080617
        this.card_type_check_w = ss.get();
        this.card_type_check_val = ss.get();

        // 2009-10-30
        this.card_RxThreshold_w = ss.get();
        this.card_RxThreshold_val = ss.get();

        // 2009-11-20
        this.f_modulate_w = ss.get();
        this.f_modulate_val = ss.get();

        this.a_modulate_w = ss.get();
        this.a_modulate_val = ss.get();
        this.a_card_RxThreshold_w = ss.get();
        this.a_card_RxThreshold_val = ss.get();
        this.a_card_antenna_gain_w = ss.get();
        this.a_card_antenna_gain_val = ss.get();
        this.b_card_antenna_gain_w = ss.get();
        this.b_card_antenna_gain_val = ss.get();
        this.f_card_antenna_gain_w = ss.get();
        this.f_card_antenna_gain_val = ss.get();
        this.f_card_RxThreshold_w = ss.get();
        this.f_card_RxThreshold_val = ss.get();

        ss.get(this.reserved);
    }
}
