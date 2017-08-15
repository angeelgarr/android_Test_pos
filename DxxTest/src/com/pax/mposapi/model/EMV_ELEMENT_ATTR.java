package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">��Ƭ��ǩԪ������,�ṩ�ӿڽ����ݴ��л�������,��������ж�������</div>
 * <div class="en">EMV data element attributes, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class EMV_ELEMENT_ATTR {
	
	//EMV_ELEMENT_ATTR
	public static final int  EMV_ELEMENT_ATTR_N 				= 0x04;/* ������ */
	public static final int  EMV_ELEMENT_ATTR_B    				= 0x08;/* �������� */
	public static final int  EMV_ELEMENT_ATTR_CN   				= 0x10;/* ѹ�������� */
	public static final int  EMV_ELEMENT_ATTR_AN   				= 0x20;/* ��ĸ������ */
	public static final int  EMV_ELEMENT_ATTR_ANS  				= 0x40;/* ������ĸ������ */
	
	public static final int  EMV_SRC_TM   						= 1;/* �ն� */
	public static final int  EMV_SRC_ICC   						= 0;/* IC�� */
	public static final int  EMV_SRC_ISS  						= 2;/* ������ */
	
	/**
	 * <div class="zh">��ǩ����󳤶�</div>
	 * <div class="en">The maximum length for this tag.</div>
	 */	
	public int MaxLen;
	/**
	 * <div class="zh">��ǩֵ</div>
	 * <div class="en">Tag</div>
	 */	
	public short Tag;
	/**
	 * <div class="zh">��ǩ���ݸ�ʽ���<br/>
		{@link #EMV_ELEMENT_ATTR_N}: 0x04 ������ <br/>
		{@link #EMV_ELEMENT_ATTR_B}: 0x08  ��������<br/> 
		{@link #EMV_ELEMENT_ATTR_CN}: 0x10  ѹ�������� <br/>
		{@link #EMV_ELEMENT_ATTR_AN}: 0x20  ��ĸ������ <br/>
		{@link #EMV_ELEMENT_ATTR_ANS}: 0x40 ������ĸ������ <br/>
		</div>
	 * <div class="en">The format of this data<br/>
		{@link #EMV_ELEMENT_ATTR_N}: 0x04 numeric <br/>
		{@link #EMV_ELEMENT_ATTR_B}: 0x08 binary<br/> 
		{@link #EMV_ELEMENT_ATTR_CN}: 0x10 compressed numeric <br/>
		{@link #EMV_ELEMENT_ATTR_AN}: 0x20 alphabet and numric<br/>
		{@link #EMV_ELEMENT_ATTR_ANS}: 0x40 alphabet, numeric and special character <br/>
	 * </div>
	 */	
	public short Attr;
	/**
	 * <div class="zh">������ģ�棬û����Ϊ0, ���������ģ��ֵ</div>
	 * <div class="en">The template which this tag belongs, 0 if none, at most 2 templates</div>
	 */	
	public final short[] usTemplate;//[2];
	/**
	 * <div class="zh">����Ԫ��Դ<br/>
		{@link #EMV_SRC_TM}: 1 �ն� <br/>
		{@link #EMV_SRC_ICC}: 0 IC��<br/> 
		{@link #EMV_SRC_ISS}: 2 ������ <br/>
	 * </div>
	 * <div class="en">The source of data element.<br/>
		{@link #EMV_SRC_TM}: 1 terminal<br/>
		{@link #EMV_SRC_ICC}: 0 ICC<br/> 
		{@link #EMV_SRC_ISS}: 2 Issuer <br/>
	 * </div>
	 */	
	public byte ucSource;

    /**
     * <div class="zh"> ����һ��EMV_ELEMENT_ATTR����</div>
     * <div class="en"> create an EMV_ELEMENT_ATTR instance </div>
     */
    public EMV_ELEMENT_ATTR() {
    	usTemplate = new short[2];
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

    	ss.putInt(MaxLen);
    	ss.putShort(Tag);
    	ss.putShort(Attr);
    	ss.putShort(0, usTemplate[0]);
    	ss.putShort(1, usTemplate[1]);
    	ss.put(ucSource);
    	
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
        
    	MaxLen = ss.getInt();
    	Tag = ss.getShort();
    	Attr = ss.getShort();
    	usTemplate[0] = ss.getShort();
    	usTemplate[1] = ss.getShort();
    	ucSource = ss.get();

    }
}
