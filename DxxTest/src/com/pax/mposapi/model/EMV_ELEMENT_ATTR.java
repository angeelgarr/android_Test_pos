package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">卡片标签元素数据,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">EMV data element attributes, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class EMV_ELEMENT_ATTR {
	
	//EMV_ELEMENT_ATTR
	public static final int  EMV_ELEMENT_ATTR_N 				= 0x04;/* 数字型 */
	public static final int  EMV_ELEMENT_ATTR_B    				= 0x08;/* 二进制型 */
	public static final int  EMV_ELEMENT_ATTR_CN   				= 0x10;/* 压缩数字型 */
	public static final int  EMV_ELEMENT_ATTR_AN   				= 0x20;/* 字母数字型 */
	public static final int  EMV_ELEMENT_ATTR_ANS  				= 0x40;/* 特殊字母数字型 */
	
	public static final int  EMV_SRC_TM   						= 1;/* 终端 */
	public static final int  EMV_SRC_ICC   						= 0;/* IC卡 */
	public static final int  EMV_SRC_ISS  						= 2;/* 发卡人 */
	
	/**
	 * <div class="zh">标签的最大长度</div>
	 * <div class="en">The maximum length for this tag.</div>
	 */	
	public int MaxLen;
	/**
	 * <div class="zh">标签值</div>
	 * <div class="en">Tag</div>
	 */	
	public short Tag;
	/**
	 * <div class="zh">标签数据格式标记<br/>
		{@link #EMV_ELEMENT_ATTR_N}: 0x04 数字型 <br/>
		{@link #EMV_ELEMENT_ATTR_B}: 0x08  二进制型<br/> 
		{@link #EMV_ELEMENT_ATTR_CN}: 0x10  压缩数字型 <br/>
		{@link #EMV_ELEMENT_ATTR_AN}: 0x20  字母数字型 <br/>
		{@link #EMV_ELEMENT_ATTR_ANS}: 0x40 特殊字母数字型 <br/>
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
	 * <div class="zh">所属的模版，没有则为0, 最多有两个模板值</div>
	 * <div class="en">The template which this tag belongs, 0 if none, at most 2 templates</div>
	 */	
	public final short[] usTemplate;//[2];
	/**
	 * <div class="zh">数据元来源<br/>
		{@link #EMV_SRC_TM}: 1 终端 <br/>
		{@link #EMV_SRC_ICC}: 0 IC卡<br/> 
		{@link #EMV_SRC_ISS}: 2 发卡人 <br/>
	 * </div>
	 * <div class="en">The source of data element.<br/>
		{@link #EMV_SRC_TM}: 1 terminal<br/>
		{@link #EMV_SRC_ICC}: 0 ICC<br/> 
		{@link #EMV_SRC_ISS}: 2 Issuer <br/>
	 * </div>
	 */	
	public byte ucSource;

    /**
     * <div class="zh"> 创建一个EMV_ELEMENT_ATTR对象</div>
     * <div class="en"> create an EMV_ELEMENT_ATTR instance </div>
     */
    public EMV_ELEMENT_ATTR() {
    	usTemplate = new short[2];
    }

    /**
     * <div class="zh">
     * 将本object中的数据写入byte数组
     * </div>
     * <div class="en">
     * get data from this object and write to a byte array.
     * </div>
     *
     * @return
     * <div class="zh">
     * 	得到的包含本object数据的byte数组.
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
     * 从一个byte数组中读取数据并记录在本object中
     * </div>
     * <div class="en">
     * get data from a byte array to this object
     * </div>
     *
     * @param bb
     * <div class="zh">
     *   从此byte数组中取数据到本object中
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
