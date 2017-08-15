package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">MCK配置相关的参数,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">MCK parameter, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class EMV_MCK_PARAM {

	/**
	 * <div class="zh">0-不支持, 1－支持, 默认支持</div>
	 * <div class="en">0- Not supported, 1-Supported. Default value is 1</div>
	 */
	public byte ucBypassPin;
	/**
	 * <div class="zh">0-ODC, 1-BDC,默认为BDC (ODC: Online Data Capture; BDC: Batch Data Capture)</div>
	 * <div class="en">0-ODC, 1-BDC. Default value is BDC(ODC: Online Data Capture; BDC: Batch Data Capture)</div>
	 */
	public byte ucBatchCapture;
	/**
	 * <div class="zh">0-用卡片AIP, 1-用终端AIP, 默认为0</div>
	 * <div class="en">0-TRM is based on AIP of card, 1-TRM is based on AIP of Terminal,  the default value is 0.</div>
	 */
	public byte ucUseTermAIPFlg;
	/**
	 * <div class="zh">终端是否强制进行风险管理, byte1-bit4为1：强制进行风险管理；byte1-bit4为0：不进行风险管理。默认两个字节全为0, 2字节</div>
	 * <div class="en">The bit4 of byte1 decide whether force to perform TRM,  "08 00"- Yes; "00 00"- No. Default value is "00 00"</div>
	 */
	public final byte[] aucTermAIP;	//[2]
	/**
	 * <div class="zh">当忽略一个PIN后是否忽略所有其他PIN  1-是, 0-否</div>
	 * <div class="en">whether bypass all other pin when one pin has been bypassed 1-Yes, 0-No</div>
	 */
	public byte ucBypassAllFlg;

    /**
     * <div class="zh"> 创建一个EMV_MCK_PARAM对象</div>
     * <div class="en"> create an EMV_MCK_PARAM instance </div>
     */
    public EMV_MCK_PARAM() {
    	aucTermAIP = new byte[2];
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

    	ss.put(ucBypassPin);
    	ss.put(ucBatchCapture);
    	ss.put(ucUseTermAIPFlg);
    	ss.put(aucTermAIP);
    	ss.put(ucBypassAllFlg);
    	
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
        
        ucBypassPin = ss.get();
        ucBatchCapture = ss.get();
        ucUseTermAIPFlg = ss.get();
    	ss.get(aucTermAIP);
    	ucBypassAllFlg = ss.get();
    	
    }
}
