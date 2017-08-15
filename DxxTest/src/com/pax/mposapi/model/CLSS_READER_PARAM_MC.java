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
	public final byte[]   aucMerchCatCode;//[2];   // 商户分类码'9F15'(2字节) 
	/**
	 * Merchant Identifier(15Bytes)
	 */
	public final byte[]   aucMerchantID;//[15];    // 商户标识(15字节) 
	
	/**
	 * <div class="zh">收单行标志, 6字节</div>
	 * <div class="en">Acquirer Identifier, 6 bytes</div>
	 */
	public final byte[] AcquierId;//[6];       //收单行标志

	/**
	 * <div class="zh">终端标识(终端号), 8字节 </div>
	 * <div class="en">Terminal Identification(Terminal number), 8 bytes</div>
	 */
    public final byte[]   aucTmID;//[8];           // 终端标识(终端号) 
    /**
     * <div class="zh">终端类型</div>
     * <div class="en">Terminal Type</div>
     */
	public byte   ucTmType;             // 终端类型
	/**
	 * <div class="zh">终端性能, 3字节</div>
	 * <div class="en">Terminal Capabilities, 3 bytes</div>
	 */
	public final byte[]   aucTmCap;//[3];          // 终端性能
	/**
	 * <div class="zh">终端附加性能, 5字节</div>
	 * <div class="en">Additional Terminal Capabilities, 5 bytes</div>
	 */
    public final byte[]   aucTmCapAd;//[5];        // 终端附加性能

	/**
	 * if aucTmCntrCode valid or not. 1 for yes, 0 otherwise
	 */		    
    public byte ucTmCntrCodeFlg;
    
    /**
     * <div class="zh">终端国家代码, 2字节</div>
     * <div class="en">Terminal Country Code, 2 bytes</div>
     */
    public final byte[]   aucTmCntrCode ;//[2];     // 终端国家代码
    
	/**
	 * if aucTmTransCur valid or not. 1 for yes, 0 otherwise
	 */		    
    public byte ucTmTransCurFlg;
    
    /**
     * <div class="zh">终端交易货币代码'5F2A'(2字节)</div>
     * <div class="en">Transaction Currency Code '5F2A'(2Bytes)</div>
     */
	public final byte[]   aucTmTransCur;//[2];      // 终端交易货币代码'5F2A'(2字节) 

	/**
	 * if ucTmTransCurExp valid or not. 1 for yes, 0 otherwise
	 */			
    public byte ucTransCurExpFlg;
    
	/**
	 * <div class="zh">终端交易货币指数'5F36'(1字节)</div>
	 * <div class="en"> Transaction Currency Exponent '5F36'(1Byte)</div>
	 */
	public byte   ucTransCurExp;       // 终端交易货币指数'5F36'(1字节)

	/**
	 * if ucTransCateCode valid or not. 1 for yes, 0 otherwise
	 */	
	public byte ucTransCateCodeFlg;
	
	/**
	 * Transaction Category Code '9F53'
	 */		
	public byte ucTransCateCode;
	
	/**
	 * <div class="zh">保留, 2字节</div>
	 * <div class="en">reserved, 2 bytes</div>
	 */
	public final byte[]   aucRFU;//[2];

    /**
     * <div class="zh"> 创建一个CLSS_READER_PARAM_MC对象</div>
     * <div class="en"> create an CLSS_READER_PARAM_MC instance </div>
     */
    public CLSS_READER_PARAM_MC() {
		aucMerchCatCode = new byte[2];//商户分类码'9F15'(2字节)
		aucMerchantID = new byte[15];//商户标识(15字节)
		
		AcquierId = new byte[6];//收单行标志
		
		aucTmID = new byte[8];//终端标识(终端号)
		aucTmCap = new byte[3];//终端性能
		aucTmCapAd = new byte[5];//终端附加性能
		
		aucTmCntrCode = new byte[2];//终端国家代码
		aucTmTransCur = new byte[2];//终端交易货币代码'5F2A'(2字节)
		
		aucRFU = new byte[2];
    }

    /**
     * <div class="zh">
     * 将本object中的数据写入byte数组.
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

		ss.put(aucMerchCatCode);//[2] //商户分类码'9F15'(2字节)
		ss.put(aucMerchantID);//[15] //商户标识(15字节)
		
		ss.put(AcquierId);//[6] //收单行标志
		
		ss.put(aucTmID);//[8] //终端标识(终端号)
		ss.put(ucTmType);//终端类型
		ss.put(aucTmCap);//[3] //终端性能
		ss.put(aucTmCapAd);//[5] //终端附加性能
		
		ss.put(ucTmCntrCodeFlg);
		ss.put(aucTmCntrCode);//[2] //终端国家代码
		
		ss.put(ucTmTransCurFlg);
		ss.put(aucTmTransCur);//[2] //终端交易货币代码'5F2A'(2字节)
		
		ss.put(ucTransCurExpFlg);
		ss.put(ucTransCurExp);//终端交易货币指数'5F36'(1字节)
		
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
     * 从一个byte数组中读取数据并记录在本object中.
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
        
		ss.get(aucMerchCatCode);  //[2] //商户分类码'9F15'(2字节)
		ss.get(aucMerchantID);  //[15] //商户标识(15字节)
		
		ss.get(AcquierId);  //[6] //收单行标志
		
		ss.get(aucTmID);  //[8] //终端标识(终端号)
		ucTmType = ss.get();  //终端类型
		ss.get(aucTmCap);  //[3] //终端性能
		ss.get(aucTmCapAd);  //[5] //终端附加性能
		
		ucTmCntrCodeFlg = ss.get();
		ss.get(aucTmCntrCode);  //[2] //终端国家代码
		
		ucTmTransCurFlg = ss.get();
		ss.get(aucTmTransCur);  //[2] //终端交易货币代码'5F2A'(2字节)
		
		ucTransCurExpFlg = ss.get();
		ucTransCurExp = ss.get();  //终端交易货币指数'5F36'(1字节)
		
		ucTransCateCodeFlg = ss.get();
		ucTransCateCode = ss.get();
		
		ss.get(aucRFU);
    }
}
