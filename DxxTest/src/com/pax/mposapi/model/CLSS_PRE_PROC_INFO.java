package com.pax.mposapi.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <div class="zh">AID对应的交易预处理参数,提供接口将数据串行化到数组,或从数组中读出数据</div>
 * <div class="en">pre-processing parameter, and provides interfaces to 
 * serialize data into a byte array or to read data from a byte array</div>
 */
public class CLSS_PRE_PROC_INFO {
    /**
     * <div class="zh">终端最低限额(同EMV L2)</div>
     * <div class="en">Terminal floor limit of terminal (the same as terminal floor limit of EMV L2)</div>
     */		
	public int ulTermFLmt;
    /**
     * <div class="zh">非接交易限额(如果授权金额大于该 值则必须使用其他接口进行交易)</div>
     * <div class="en">Contactless transaction limit of reader(other transaction interface must be used if authorized amount is greater than this value)</div>
     */		
	public int ulRdClssTxnLmt;
    /**
     * <div class="zh">要求 执行CVM的最低限额</div>
     * <div class="en">CVM required limit of reader</div>
     */		
	public int ulRdCVMLmt;
    /**
     * <div class="zh">非接最低限额(高于此值必须联机交易)</div>
     * <div class="en">Contactless floor limit of reader</div>
     */		
	public int ulRdClssFLmt;

    /**
     * <div class="zh">AID, 5~16字节</div>
     * <div class="en">AID, 5~16 bytes</div>
     */		
	public final byte[] aucAID;//[17];       
    /**
     * <div class="zh">AID长度</div>
     * <div class="en">AID length</div>
     */		
	public byte ucAidLen; 
	
    /**
     * <div class="zh">与AID对应的内核类型(比如 VISA, MasterCard, PBOC)</div>
     * <div class="en">Kernel type corresponding to the AID(e.g. VISA, MasterCard, PBOC)</div>
     */		
	public byte ucKernType; // 定义后续该AID 所遵循的内核类型

	// payWave
    /**
     * <div class="zh">密文版本17是否支持  1-是, 0-否</div>
     * <div class="en">Is cryptogram version 17 supported? 1-yes, 0-no</div>
     */		
	public byte  ucCrypto17Flg;
    /**
     * <div class="zh">
     * 0-若授权金额=0,则TTQ要求联机密文, <br/>
     * 1-若授权金额=0, 则内部qVSDC不支持标志置位
     * </div>
     * <div class="en">
     * 0-TTQ requires online cryptogram when authorized amount is equal to 0<br/>
     * 1-Internal qVSDC does not support flag bit setting when authorized amount is equal to 0
     * </div>
     */		
	public byte   ucZeroAmtNoAllowed;       // 0-若授权金额=0,则TTQ要求联机密文, 1-若授权金额=0, 则内部qVSDC不支持标志置位
    /**
     * <div class="zh">读卡器是否支持状态检查, 1-是, 0-否</div>
     * <div class="en">Does card reader support status check? 1-yes, 0-no</div>
     */		
	public byte   ucStatusCheckFlg;    // 读卡器是否支持状态检查
    /**
     * <div class="zh">终端交易性能, 用于VISA/PBOC中, tag =9F66, 5 字节</div>
     * <div class="en">Reader's terminal transaction qualifiers, used in VISA/PBOC, tag =9F66, 5 bytes</div>
     */		
    public final byte[]   aucReaderTTQ;//[5];      // 终端交易性能, 用于VISA/PBOC中, tag =9F66
	
	// common
    /**
     * <div class="zh">终端最低限额(同EMV L2)是否有效标志</div>
     * <div class="en">whether the Terminal Floor Limit is present (the same as Terminal Floor Limit of EMV L2)</div>
     */		
	public byte ucTermFLmtFlg; 
    /**
     * <div class="zh">非接交易最高限额是否有效标志</div>
     * <div class="en">whether reader Contactless Transaction Limit is present</div>
     */		
	public byte ucRdClssTxnLmtFlg; 
    /**
     * <div class="zh">要求 执行CVM的最低限额是否有效标志</div>
     * <div class="en">whether reader CVM Required Limit is present</div>
     */		
	public byte ucRdCVMLmtFlg;   
    /**
     * <div class="zh">非接最低限额(高于此值必须联机交易)是否有效标志</div>
     * <div class="en">wheterh reader Contactless Floor Limit is present</div>
     */		
	public byte ucRdClssFLmtFlg; 	 
	
    /**
     * <div class="zh">保留, 2字节</div>
     * <div class="en">reserved, 2 bytes</div>
     */		
	public final byte[] aucRFU;//[2];

    /**
     * <div class="zh"> 创建一个CLSS_PRE_PROC_INFO对象</div>
     * <div class="en"> create an CLSS_PRE_PROC_INFO instance </div>
     */
    public CLSS_PRE_PROC_INFO() {
    	aucAID = new byte[17];   
    	aucReaderTTQ = new byte[5];   
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

    	ss.putInt(ulTermFLmt);
    	ss.putInt(ulRdClssTxnLmt);
    	ss.putInt(ulRdCVMLmt);
    	ss.putInt(ulRdClssFLmt);

    	ss.put(aucAID);//[17]);
    	ss.put(ucAidLen);
    	
    	ss.put(ucKernType);//定义后续该AID所遵循的内核类型

    	//payWave
    	ss.put(ucCrypto17Flg);
    	ss.put(ucZeroAmtNoAllowed);//0-若授权金额=0,则TTQ要求联机密文,1-若授权金额=0,则内部qVSDC不支持标志置位
    	ss.put(ucStatusCheckFlg);//读卡器是否支持状态检查
        ss.put(aucReaderTTQ);//[5];//终端交易性能, 用于VISA/PBOC中, tag=9F66
    	
    	//common
    	ss.put(ucTermFLmtFlg);
    	ss.put(ucRdClssTxnLmtFlg);
    	ss.put(ucRdCVMLmtFlg);
    	ss.put(ucRdClssFLmtFlg);	
    	
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
        
        ulTermFLmt = ss.getInt();
        ulRdClssTxnLmt = ss.getInt();
        ulRdCVMLmt = ss.getInt();
        ulRdClssFLmt = ss.getInt();

    	ss.get(aucAID);//[17]);
    	ucAidLen = ss.get();
    	
    	ucKernType = ss.get();//定义后续该AID所遵循的内核类型

    	//payWave
    	ucCrypto17Flg = ss.get();
    	ucZeroAmtNoAllowed = ss.get();//0-若授权金额=0,则TTQ要求联机密文,1-若授权金额=0,则内部qVSDC不支持标志置位
    	ucStatusCheckFlg = ss.get();//读卡器是否支持状态检查
        ss.get(aucReaderTTQ);//[5]);//终端交易性能, 用于VISA/PBOC中, tag=9F66
    	
    	//common
        ucTermFLmtFlg = ss.get();
        ucRdClssTxnLmtFlg = ss.get();
        ucRdCVMLmtFlg = ss.get();
        ucRdClssFLmtFlg = ss.get();	
    	
    	ss.get(aucRFU);//[2]);

    }
}
