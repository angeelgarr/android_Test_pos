package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array
 */
public class SYS_PROC_INFO {
	
	/**
	 * track1 data, maximum 79 bytes
	 */
	public final byte[]	szTrack1;	//[79+1];
	/**
	 * track2 data, maximum 40 bytes
	 */
	public final byte[]	szTrack2;	//[40+4];
	/**
	 * track1 data, maximum 107 bytes
	 */
	public final byte[]	szTrack3;	//[107+1];
	
	/**
	 * contactless transaction status<br/>
	 * 0x01 - offline approved<br/>
	 * 0x02 - offline declined<br/>
	 * 0x03 - need online<br/>
	 * 0x04 - need PIN<br/>
	 * 0x05 - need sign<br/>
	 */
	public int iClssStatus;
	/**
	 * transaction log
	 */
	public TRAN_LOG	stTranLog;

	/**
	 * pin block(RFU), 8 bytes
	 */
	public final byte[]	sPinBlock;	//[8];			// PIN block (RFU)
	
    /**
     * <div class="zh"> 创建一个SYS_PROC_INFO对象</div>
     * <div class="en"> create an SYS_PROC_INFO instance </div>
     */
    public SYS_PROC_INFO() {
    	szTrack1 = new byte[80];
    	szTrack2 = new byte[44];
    	szTrack3 = new byte[108];
    	stTranLog = new TRAN_LOG();
    	sPinBlock = new byte[8];
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

        ss.put(szTrack1);
        ss.put(szTrack2);
        ss.put(szTrack3);
        
        ss.putInt(iClssStatus);
        ss.put(stTranLog.serialToBuffer());
        
        ss.put(sPinBlock);
        
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
        
        ss.get(szTrack1);
        ss.get(szTrack2);
        ss.get(szTrack3);
        
        iClssStatus = ss.getInt();
        
        byte[] tmp = stTranLog.serialToBuffer(); 
        ss.get(tmp);
        stTranLog.serialFromBuffer(tmp);
        
        ss.get(sPinBlock);
    }
}
