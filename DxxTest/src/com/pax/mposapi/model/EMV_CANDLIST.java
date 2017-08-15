package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">EMV应用列表,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">EMV application list, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class EMV_CANDLIST {
	/**
	 * <div class="zh">
	 * 应用优先选择名称
	 * </div>
	 * <div class="en">
	 * application preferred name
	 * </div>
	 */
	public final byte[] aucAppPreName;//[17];
	
	/**
	 * <div class="zh">
	 * 应用标签
	 * </div>
	 * <div class="en">
	 * application label
	 * </div>
	 */
	public final byte[] aucAppLabel;//[17];
	
	/**
	 * <div class="zh">
	 * tag 'BF0C'数据: 1个字节的长度字节+'BF0C'最大222个字节; tag '73'数据: 1个字节的长度字节+'73'最大242个字节
	 * </div>
	 * <div class="en">
	 * tag 'BF0C': 1 byte data length + maximum 222 data bytes;  tag '73': 1 byte data length + maximum 242 data bytes
	 * </div>
	 */
	public final byte[] aucIssDiscrData;//[244];
	
	/**
	 * <div class="zh">应用标志（和AppName对应的应用标志, EMV库依靠此标志来搜索应用）, 最长16字节</div>
	 * <div class="en">Application ID, 16 bytes in maximum, (AID is corresponding to  the AppName, The kernel searchs application according to the AID)</div>
	 */
	public final byte[] aucAID;//[17];           
	/**
	 * <div class="zh">应用标志的长度</div>
	 * <div class="en">AID length</div>
	 */
	public byte ucAidLen;            
         
	/**
	 * <div class="zh">优先级标志（卡片返回, 应用可以不处理）</div>
	 * <div class="en">priority indicator(It's returned by ICC, so nothing needs to be done by application)</div>
	 */
	public byte ucPriority;          

    /**
     * <div class="zh"> 创建一个EMV_APPLIST对象</div>
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
     * 将本object中的数据写入byte数组
     * </div>
     * <div class="en">
     * get data from this object and write to a byte array,
     * </div>
     *
     * @return
     * <div class="zh">
     * 	得到的包含本object数据的byte数组,
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
     * @param offset
     * <div class="zh">
     *   偏移量, 将从bb的第offset字节开始解析
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
