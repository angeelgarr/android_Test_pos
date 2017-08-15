package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Trans Log, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array
 */
public class TRAN_LOG {
	/**
	 * transaction type
	 */
	public byte	ucTranType;					// 交易类型
	/**
	 * original transaction type
	 */
	public byte	ucOrgTranType;				// 原交易类型
	/**
	 * PAN, maximum 19 bytes
	 */
	public final byte[]	szPan;		//[19+1];				// 卡号
	/**
	 * expiry date, 4 bytes
	 */
	public final byte[]	szExpDate;	//[4+1];				// 有效期
	/**
	 * transaction amount, 12 bytes
	 */
	public final byte[]	szAmount;	//[12+1];				// 交易金额
	/**
	 * tip amount, 12 bytes
	 */
	public final byte[]	szTipAmount;	//[12+1];			// 小费金额
	/**
	 * original transaction amount, 12 bytes
	 */
	public final byte[]	szOrgAmount;	//[12+1];			// 原交易金额
	/**
	 * transaction date time (YYYYMMDDhhmmss), 14 bytes
	 */
	public final byte[]	szDateTime;		//[14+2];			// YYYYMMDDhhmmss
	/**
	 * authorization code, 6 bytes
	 */
	public final byte[]	szAuthCode;		//[6+1];
	/**
	 * response Code, 2 byes
	 */
	public final byte[]	szRspCode;		//[2+1];				// 响应码
	/**
	 * RRN, system ref. no, 13 bytes
	 */
	public final byte[]	szRRN;		//[13+1];				// RRN, system ref. no
	/**
	 * holder name, maximum 26 bytes
	 */
	public final byte[]	szHolderName;		//[26+1];        //
	
	/**
	 * transaction currency configuration
	 */
	public CURRENCY_CONFIG	stTranCurrency;
	
	/**
	 * holder currency configuration
	 */
	public CURRENCY_CONFIG	stHolderCurrency;
	
	/**
	 * transaction status<br/>
	 * 0x0000 - transaction accepted <br/>
	 * 0x0001 - transaction not sent to host <br/>
	 * 0x0002 - transaction adjusted <br/>
	 * 0x0004 - transaction reversed <br/>
	 * 0x0008 - transaction voided <br/>
	 * 0x0010 - reserved <br/>
	 * 0x0020 - transaction amount less than floor limit <br/>
	 * 0x0040 - offline send <br/>
	 * 0x0080 - no need upload <br/>
	 * 0x0100 - need upload TC <br/>
	 */
	public short		uiStatus;				// 交易状态

	/**
	 * entry mode, can be used for iso8583 field 22 <br/>
	 * 0x0000 - no input <br/>
	 * 0x0001 - manual input <br/>
	 * 0x0002 - magnetic swipe card <br/>
	 * 0x0004 - EMV chip card <br/>
	 * 0x0008 - EMV MSD (fallback) <br/>
	 * 0x0010 - online PIN <br/>
	 * 0x0020 - offline PIN (for AMEX) <br/>
	 * 0x0040 - CVV/4DBC entered <br/>
	 * 0x0080 - contactless <br/>
	 * 0x0100 - fallback manual <br/>
	 * 0x0200 - signature <br/>
	 */
	public short	uiEntryMode;			// 输入模式, 可计算出Bit 22
	
	// EMV related data
	/**
	 * application label, 1~16 bytes
	 */
	public final byte[]	szAppLabel;		//[16+1];
	/**
	 * true: PAN Seq. read OK
	 */
	public byte	bPanSeqOK;					// TRUE: PAN Seq read OK
	/**
	 * PAN Seq. No.
	 */
	public byte	ucPanSeqNo;
	/**
	 * AID length
	 */
	public byte	ucAidLen;
	/**
	 * application cryptogram, 8 bytes
	 */
	public final byte[]	sAppCrypto;		//[8];
	/**
	 * TVR
	 */
	public final byte[]	sTVR;			//[6];
	/**
	 * TSI
	 */
	public final byte[]	sTSI;			//[2+1];
	/**
	 * ATC
	 */
	public final byte[]	sATC;			//[2+1];
	/**
	 * ICC data length (field 55)
	 */
	public short	uiIccDataLen;
	/**
	 * AID, 1~16 bytes
	 */
	public final byte[]	sAID;			//[16+1];
	/**
	 * application preferred name, 1~16 bytes
	 */
	public final byte[]	szAppPreferName;	//[16+1];
	/**
	 * ICC data (field 55), maximum 260 bytes
	 */
	public final byte[]	sIccData;		//[LEN_ICC_DATA];

	/**
	 * invoice no.
	 */
	public int	ulInvoiceNo;			// 票据号
	/**
	 * STAN
	 */
	public int	ulSTAN;					// STAN
	/**
	 * original STAN
	 */
	public int	ulOrgSTAN;				// 原交易流水
	
	/**
	 * merchant ID, maximum 16 bytes
	 */
	public final byte[]       uMerchantID;		//[16];
	/**
	 * terminal ID, maximum 9 bytes
	 */
	public final byte[]       uPosTID;			//[9];
	
    /**
     * <div class="zh"> 创建一个TRAN_LOG对象</div>
     * <div class="en"> create an TRAN_LOG instance </div>
     */
    public TRAN_LOG() {
    	szPan = new byte[19+1];				// 卡号
    	szExpDate = new byte[4+1];				// 有效期
    	szAmount = new byte[12+1];				// 交易金额
    	szTipAmount = new byte[12+1];			// 小费金额
    	szOrgAmount = new byte[12+1];			// 原交易金额
    	szDateTime = new byte[14+2];			// YYYYMMDDhhmmss
    	szAuthCode = new byte[6+1];
    	szRspCode = new byte[2+1];				// 响应码
    	szRRN = new byte[13+1];				// RRN, system ref. no
    	szHolderName = new byte[26+1];        //

    	stTranCurrency = new CURRENCY_CONFIG();
    	stHolderCurrency = new CURRENCY_CONFIG();

    	// EMV related data
    	szAppLabel = new byte[16+1];
    	sAppCrypto = new byte[8];
    	sTVR = new byte[6];
    	sTSI = new byte[2+1];
    	sATC = new byte[2+1];
    	sAID = new byte[16+1];
    	szAppPreferName = new byte[16+1];
    	sIccData = new byte[260];	//LEN_ICC_DATA

    	uMerchantID = new byte[16];
    	uPosTID = new byte[9];
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

    	ss.put(ucTranType);					// 交易类型
    	ss.put(ucOrgTranType);				// 原交易类型
    	ss.put(szPan);		//[19+1];				// 卡号
    	ss.put(szExpDate);	//[4+1];				// 有效期
    	ss.put(szAmount);	//[12+1];				// 交易金额
    	ss.put(szTipAmount);	//[12+1];			// 小费金额
    	ss.put(szOrgAmount);	//[12+1];			// 原交易金额
    	ss.put(szDateTime);		//[14+2];			// YYYYMMDDhhmmss
    	ss.put(szAuthCode);		//[6+1];
    	ss.put(szRspCode);		//[2+1];				// 响应码
    	ss.put(szRRN);		//[13+1];				// RRN, system ref. no
    	ss.put(szHolderName);		//[26+1];        //
    	
    	ss.put(stTranCurrency.serialToBuffer());
    	ss.put(stHolderCurrency.serialToBuffer());
    	
    	ss.putShort(uiStatus);				// 交易状态

    	ss.putShort(uiEntryMode);			// 输入模式, 可计算出Bit 22
    	
    	// EMV related data
    	ss.put(szAppLabel);		//[16+1];
    	ss.put(bPanSeqOK);					// TRUE: PAN Seq read OK
    	ss.put(ucPanSeqNo);
    	ss.put(ucAidLen);
    	ss.put(sAppCrypto);		//[8];
    	ss.put(sTVR);			//[6];
    	ss.put(sTSI);			//[2+1];
    	ss.put(sATC);			//[2+1];
    	ss.putShort(uiIccDataLen);
    	ss.put(sAID);			//[16+1];
    	ss.put(szAppPreferName);	//[16+1];
    	ss.put(sIccData);		//[LEN_ICC_DATA];

    	ss.putInt(ulInvoiceNo);			// 票据号
    	ss.putInt(ulSTAN);					// STAN
    	ss.putInt(ulOrgSTAN);				// 原交易流水
    	
    	
    	ss.put(uMerchantID);		//[16];
    	ss.put(uPosTID);			//[9];

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
        
        ucTranType = ss.get();					// 交易类型
        ucOrgTranType = ss.get();				// 原交易类型
    	ss.get(szPan);		//[19+1];				// 卡号
    	ss.get(szExpDate);	//[4+1];				// 有效期
    	ss.get(szAmount);	//[12+1];				// 交易金额
    	ss.get(szTipAmount);	//[12+1];			// 小费金额
    	ss.get(szOrgAmount);	//[12+1];			// 原交易金额
    	ss.get(szDateTime);		//[14+2];			// YYYYMMDDhhmmss
    	ss.get(szAuthCode);		//[6+1];
    	ss.get(szRspCode);		//[2+1];				// 响应码
    	ss.get(szRRN);		//[13+1];				// RRN, system ref. no
    	ss.get(szHolderName);		//[26+1];        //
    	
    	byte[] tmp = stTranCurrency.serialToBuffer();
    	ss.get(tmp);
    	stTranCurrency.serialFromBuffer(tmp);
    	
    	tmp = stHolderCurrency.serialToBuffer();
    	ss.get(tmp);
    	stHolderCurrency.serialFromBuffer(tmp);
    	
    	uiStatus = ss.getShort();				// 交易状态

    	uiEntryMode = ss.getShort();			// 输入模式, 可计算出Bit 22
    	
    	// EMV related data
    	ss.get(szAppLabel);		//[16+1];
    	bPanSeqOK = ss.get();					// TRUE: PAN Seq read OK
    	ucPanSeqNo = ss.get();
    	ucAidLen = ss.get();
    	ss.get(sAppCrypto);		//[8];
    	ss.get(sTVR);			//[6];
    	ss.get(sTSI);			//[2+1];
    	ss.get(sATC);			//[2+1];
    	uiIccDataLen = ss.getShort();
    	ss.get(sAID);			//[16+1];
    	ss.get(szAppPreferName);	//[16+1];
    	ss.get(sIccData);		//[LEN_ICC_DATA];

    	ulInvoiceNo = ss.getInt();			// 票据号
    	ulSTAN = ss.getInt();					// STAN
    	ulOrgSTAN = ss.getInt();				// 原交易流水
    	
    	ss.get(uMerchantID);		//[16];
    	ss.get(uPosTID);			//[9];
    }
}
