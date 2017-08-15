package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">最终选择应用AID对应的在交易预处理过程中动态设置的内部参数,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">internal flag parameters which are set dynamically during preliminary transaction processing, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class CLSS_PRE_PROC_INTER_INFO {
	
    /**
     * <div class="zh">AID, 5~16字节</div>
     * <div class="en">AID, 5~16bytes</div>
     */		
	public final byte[] aucAID;//[17];       
    /**
     * <div class="zh">AID长度</div>
     * <div class="en">AID length</div>
     */		
	public byte ucAidLen; 

	// payWave
    /**
     * <div class="zh">0-交易金额!=0; 1-交易金额=0</div>
     * <div class="en">0-transaction amount!=0; 1-transaction amount=0</div>
     */		
	public byte   ucZeroAmtFlg;       // 0-交易金额!=0; 1-交易金额=0
    /**
     * <div class="zh">读卡器是否支持状态检查</div>
     * <div class="en"> whether supports status check or not</div>
     */		
	public byte   ucStatusCheckFlg;    // 读卡器是否支持状态检查
    /**
     * <div class="zh">终端交易性能，用于VISA/PBOC中，tag =9F66, 5字节</div>
     * <div class="en">Terminal transaction Qualifiers, used in VISA/PBOC, tag =9F66</div>
     */		
    public final byte[]   aucReaderTTQ;//[5];      // 终端交易性能，用于VISA/PBOC中，tag =9F66
    /**
     * <div class="zh">1-该AID不能进行非接触交易</div>
     * <div class="en">1-the AID doen't support contactless transaction,  0- the AID support contactless transaction </div>
     */		
	public byte   ucCLAppNotAllowed; // 1-该AID不能进行非接触交易
	
	// common
    /**
     * <div class="zh">超过终端最低交易限额标志, 0-否, 1-是</div>
     * <div class="en">Whether the Terminal Floor Limit is exceeded or not, 0-no, 1-yes</div>
     */		
	public byte ucTermFLmtExceed; 
    /**
     * <div class="zh">超过终端非接交易允许的最高限额标志, 0-否, 1-是</div>
     * <div class="en">Whether the Reader Contactless Transaction Limit is exceeded or not, 0-no, 1-yes</div>
     */		
	public byte ucRdCLTxnLmtExceed; 
    /**
     * <div class="zh">超过终端CVM required交易限额标志, 0-否, 1-是</div>
     * <div class="en">Whether the Reader CVM Required Limit is exceeded or not, 0-no, 1-yes</div>
     */		
	public byte ucRdCVMLmtExceed;  
    /**
     * <div class="zh">超过终端非接交易要求在线交易的最低限额标志, 0-否, 1-是</div>
     * <div class="en">Whether the Reader Contactless Floor Limit is exceeded or not, 0-no, 1-yes</div>
     */		
	public byte ucRdCLFLmtExceed;  

    /**
     * <div class="zh">是否使能终端最低交易限额, 0-否, 1-是</div>
     * <div class="en"> Is Terminal Floor Limit present? 0-no, 1-yes</div>
     */		
	public byte ucTermFLmtFlg;
    /**
     * <div class="zh">终端最低交易限额, 4字节</div>
     * <div class="en">Terminal Floor Limit, 4 bytes</div>
     */		
	public final byte[] aucTermFLmt;//[5];
	
    /**
     * <div class="zh">保留, 2字节</div>
     * <div class="en">reserved, 2 bytes</div>
     */		
	public final byte[] aucRFU;//[2];

    /**
     * <div class="zh"> 创建一个CLSS_PRE_PROC_INTER_INFO对象</div>
     * <div class="en"> create an CLSS_PRE_PROC_INTER_INFO instance </div>
     */
    public CLSS_PRE_PROC_INTER_INFO() {
    	aucAID = new byte[17];   
    	aucReaderTTQ = new byte[5];   
    	aucTermFLmt = new byte[5];
    	aucRFU = new byte[2]; 
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

    	ss.put(aucAID);//[17];
    	ss.put(ucAidLen);

    	//payWave
    	ss.put(ucZeroAmtFlg);//0-交易金额!=0);1-交易金额=0
    	ss.put(ucStatusCheckFlg);//读卡器是否支持状态检查
        ss.put(aucReaderTTQ);//[5];//终端交易性能，用于VISA/PBOC中，tag=9F66
    	ss.put(ucCLAppNotAllowed);//1-该AID不能进行非接触交易
    	
    	//common
    	ss.put(ucTermFLmtExceed);
    	ss.put(ucRdCLTxnLmtExceed);
    	ss.put(ucRdCVMLmtExceed);
    	ss.put(ucRdCLFLmtExceed);

    	ss.put(ucTermFLmtFlg);
    	ss.put(aucTermFLmt);//[5];
    	
    	ss.put(aucRFU);//[2];

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
        
    	 ss.get(aucAID);//[17] 
    	ucAidLen = ss.get();

    	//payWave
    	ucZeroAmtFlg = ss.get();//0-交易金额! = 0 = ss.get();1-交易金额 = 0
    	ucStatusCheckFlg = ss.get();//读卡器是否支持状态检查
        ss.get(aucReaderTTQ);//[5] //终端交易性能，用于VISA/PBOC中，tag = 9F66
    	ucCLAppNotAllowed = ss.get();//1-该AID不能进行非接触交易
    	
    	//common
    	ucTermFLmtExceed = ss.get();
    	ucRdCLTxnLmtExceed = ss.get();
    	ucRdCVMLmtExceed = ss.get();
    	ucRdCLFLmtExceed = ss.get();

    	ucTermFLmtFlg = ss.get();
    	ss.get(aucTermFLmt);//[5] = 
    	
    	ss.get(aucRFU);//[2] = 

    }
}
