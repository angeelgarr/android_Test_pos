package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;
import com.pax.mposapi.model.RSA_PINKEY;
import com.pax.mposapi.model.ST_KCV_INFO;
import com.pax.mposapi.model.ST_KEY_INFO;
import com.pax.mposapi.model.ST_RSA_KEY;
import com.pax.mposapi.util.Utils;
/**
 * <div class="zh">
 * PedManager 用于管理密钥,数据加解密<br/>
 * </div>
 * <div class="en">
 * PedManager is used to manage the keys, and to perform data encryption/decryption<br/>
 * </div>
 *
 */
public class PedManager {
    public static final byte PED_DECRYPT = 0x00;
    public static final byte PED_ENCRYPT = 0x01;
    
    public static final int PED_TLK = 0x01;
    public static final int PED_TMK = 0x02;
    public static final int PED_TPK = 0x03;
    public static final int PED_TAK = 0x04;
    public static final int PED_TDK = 0x05;
    public static final int PED_TIK = 0x06;

    public static final byte PED_MAC_MODE_ANSIX9_9 = 0x00;
    public static final byte PED_MAC_MODE_1 = 0x01;			//TBD: formal name?
    public static final byte PED_MAC_MODE_ANSIX9_19 = 0x02;
    
    public static final byte PED_PINBLOCK_ISO9564_0 = 0x00;
    public static final byte PED_PINBLOCK_ISO9564_1 = 0x01;
    public static final byte PED_PINBLOCK_ISO9564_3 = 0x02;
    public static final byte PED_PINBLOCK_HK_EPS = 0x03;
    
    public static final byte PED_DUKPT_DES_WITH_MAC_KEY = 0x00;
    public static final byte PED_DUKPT_DES_WITH_DES_KEY = 0x01;
    public static final byte PED_DUKPT_DES_WITH_PIN_KEY = 0x02;
    
    public static final byte PED_DUKPT_DES_EBC_DECRYPTION = 0x00;
    public static final byte PED_DUKPT_DES_EBC_ENCRYPTION = 0x01;
    public static final byte PED_DUKPT_DES_CBC_DECRYPTION = 0x02;
    public static final byte PED_DUKPT_DES_CBC_ENCRYPTION = 0x03;
	
    /*
    private static final int MAX_RSA_MODULUS_BITS = 2048;
    private static final int MAX_RSA_MODULUS_LEN = ((MAX_RSA_MODULUS_BITS + 7) / 8);
    private static final int MAX_RSA_PRIME_BITS = ((MAX_RSA_MODULUS_BITS + 1) / 2);
    private static final int MAX_RSA_PRIME_LEN = ((MAX_RSA_PRIME_BITS + 7) / 8);
    */
    
	private static final String TAG = "PedManager";
    private final Proto proto;
    Context context;
    private static PedManager instance;

    /**
     * 
     * Output of {@link #pedGetMacDukpt}
     * 
     */
    public class MacDukptOutput {
        /**
         * the MAC result, 8 bytes.
         */
        public byte[] macOut = null;
        /**
         * the current KSN, 10 bytes.
         */
        public byte[] ksnOut = null;

        /**
         * Create a MacDukptOutput instance.
         * 
         */
        MacDukptOutput() {
            macOut = new byte[8];
            ksnOut = new byte[10];
        }

    }

    /**
     * 
     * Output of {@link #pedGetPinDukpt}
     * 
     */
    public class PinDukptOutput {
        /**
         * the generated PIN Block result, 8 bytes.
         */
        public byte[] pinBlockOut = null;
        /**
         * the current KSN, 10 bytes.
         */
        public byte[] ksnOut = null;

        /**
         * Create a PinDukptOutput instance.
         * 
         */
        PinDukptOutput() {
            pinBlockOut = new byte[8];
            ksnOut = new byte[10];
        }
    }

    /**
     * 
     * Output of {@link #pedDukptDes}
     * 
     */
    public class DukptDesOutput {
        /**
         * the result data.
         */
        public byte[] dataOut = null;
        /**
         * the Current KSN, 10 bytes.
         */
        public byte[] ksnOut = null;

        /**
         * Create an DukptDesOutput instance with a given input data length.
         * 
         * @param dataInLen
         */
        DukptDesOutput(int dataInLen) {
            dataOut = new byte[dataInLen];
            ksnOut = new byte[10];
        }
    }

    /**
     * 
     * Output of {@link #pedRsaRecover}
     * 
     */
    public class RsaRecoverOutput {
        /**
         * The encrypted/decrypted data.
         */
        public byte[] pucData = null;
        /**
         * Key information.
         */
        public byte[] pucKeyInfo = null;

        /**
         * Create an RsaRecoverOutput instance with a given input data length.
         * 
         * @param dataLen
         */
        RsaRecoverOutput(int dataLen) {
            pucData = new byte[dataLen];
            pucKeyInfo = new byte[128];
        }
    }    
    
    /**
     * <div class="zh">
     * 使用指定的Context构造出PedManager对象
     * </div>
     * <div class="en">
     * Create a PedManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">应用当前的context</div>
     * <div class="en">application context currently</div>
     */
    private PedManager(Context context) {
    	proto = Proto.getInstance(context);
    	this.context = context;
    }

    /**
     * Create a PedManager instance with a given Context
     * 
     * @param context
     *            application context currently
     */
    public static PedManager getInstance(Context context) {
        if (instance == null) {
        	instance = new PedManager(context);
        }
        return instance;
    }
    
    /**
     * <div class="zh">
     * 写入一个密钥,包括TLK,TMK和TWK的写入,发散,并可以选择使用KCV验证密钥正确性<br/>
	 * 将一个密钥的密文或者明文写入到指定的密钥类型区域的指定索引的位置, 该函数的用法有以下几个要点:<br/>
	 * 1 - 当usScrKeyIdx =0时,系统认为stKey中的aucDstKeyValue是密钥的明文,则不去判断ucSrcKeyType,ucSrcKeyIdx,直接将aucDstKeyValue写到ucDstKeyType区域的ucDstKeyIdx位置,只有当PED_TLK不存在时,可以允许明文写入或者下载任何密钥;<br/>
	 * 2 - PED_TLK存在时,不允许明文写入或者下载密钥,PED_TLK只允许16或24字节,并且不允许注入8字节密钥,PED_TLK只能是16字节或者24字节;<br/>
	 * 3 - 当写入PED_TLK时,PED首先格式化,清除所有已经下载的密钥,再写入PED_TLK;<br/>
	 * 4 - 当ucSrcKeyIdx为参数说明中的合法值时,系统认为KeyInfoIn中的aucDstKeyValue是密钥的密文,则通过ucSrcKeyType类型密钥区的ucSrcKeyIdx号密钥对aucDstKeyValue进行解密,并写入到ucDstKeyType区域的ucDstKeyIdx位置, 其中ucDstKeyType >= ucSrcKeyType;<br/>
	 * 5 - ucDstKeyLen只能为8或16,24,当ucDstKeyLen为8时,这个密钥只能用于DES计算,ucDstKeyLen为16或24时,可以用于TDES计算;<br/>
	 * 6 - ucDstKeyType指定了密钥类型,当ucDstKeyType=PED_TPK时,这个密钥只能用于计算PIN Block, 当ucDstKeyType = PED_TAK时,这密钥只能用于计算MAC, 当ucDstKeyType=PED_TDK时,这个密钥只能用于DES/TDES的加解密运算, 从而限制了工作密钥的用途,保证工作密钥功能的唯一性;
     * </div>
     * <div class="en">
     * To write or derive one key to PED, including BKLK, TMK, TWK, and use KCV to check the key correction.<br/>
		Writing the cryptograph and plaintext of a key to the specific index position of the specific key type area. Using this function have following key points:
		1. When usScrKeyIdx=0, system considering that the aucDstKeyValue of stKey is the plaintext of key and do not judge usSrcKeyType and ucSrcKeyIdx. Write the aucDstKeyValue to ucDstKeyIdx in ucDstKeyType area directly. Only when PED_TLK does not exist, it allows plaintext to type in or download any key;<br/>
		2. When PED_TLK exist,it is not allowed plaintext to write or download key. PED_TLK can be 16 or 24 byte.8 byte key is not allowed;<br/>
		3. When type in PED_TLK, PED can be formatting firstly. Clear all downloaded key and write again;<br/>
		4. If ucSrcKeyIdx is valid, PED consider the aucDstKeyValue of KeyInfoIn as key cryptograph, thus decrypt it by key of ucSrcKeyIdx and write the key to ucDstKeyIdx. ucDstKeyType >= ucSrcKeyType;<br/>
		5. ucDstKeyLen only could be 8 or 16 or 24. If ucDstKeyLen = 8, the key could only be used for DES. If ucDstKeyLen = 16 OR 24, the key could be used for TDES;<br/>
		6. If ucDstKeyType=PED_TPK, the key only be used to encrypt PIN Block. If ucDstKeyType=PED_TAK, the key can only be used for MAC encryption. If ucDstKeyType=PED_TDK, the key can only be used for DES/TDES;     
     * </div>
     * 
     * @param keyInfoIn
     * <div class="zh">
     * 			[输入]
     * 			<ul>
     * 				<li>ucSrcKeyType: PED_TLK, PED_TMK, PED_TPK, PED_TAK, PED_TDK
     * 				<li>ucSrcKeyIdx: 
     * 					<ul>	
     * 						<li>当ucSrcKeyType = PED_TLK时 ucSrcKeyIdx = 1; 
     * 						<li>当ucSrcKeyType = PED_TMK时, ucSrcKeyIdx = [1~100] (D180 [1~20]); 
     * 						<li>当ucSrcKeyType = PED_TPK或PED_TAK或PED_TDK时ucSrcKeyIdx = [1~100] (D180 [1~20]);
     * 					</ul>
     * 				<li>ucDstKeyType: PED_TLK, PED_TMK, PED_TPK, PED_TAK, PED_TDK <br/>
     * 				<li>ucDstKeyIdx:
     * 					<ul>
     * 						<li>当ucDstKeyType = PED_TLK时 ucDstKeyIdx = 1; 
     * 						<li>当ucDstKeyType = PED_TMK时, ucDstKeyIdx = [1~100] (D180 [1~20]); 
     * 						<li>当ucDstKeyType = PED_TPK或PED_TAK或PED_TDK时ucDstKeyIdx = [1~100] (D180 [1~20]);
     * 					</ul>
     * 				<li>iDstKeyLen: 8/16/24
     * 				<li>aucDstKeyValue: 密钥明文或密文
     *           </ul>
     * </div>
     * <div class="en">
     * 			[input] <br/>
     * 			<ul>
     * 				<li>ucSrcKeyType: PED_TLK, PED_TMK, PED_TPK, PED_TAK, PED_TDK
     * 				<li>ucSrcKeyIdx: 
     * 					<ul>
     * 						<li>If ucSrcKeyType = PED_TLK,UcSrcKeyIdx = 1;
     * 						<li>If ucSrcKeyType = PED_TMK,ucSrcKeyIdx = [1~100] (D180 [1~20]);
     * 						<li>If ucSrcKeyType = PED_TPK or PED_TAK or PED_TDK, ucDstKeyIdx = [1~100] (D180 [1~20])
     * 					</ul>
     * 				<li>ucDstKeyType: PED_TLK, PED_TMK, PED_TPK, PED_TAK, PED_TDK
     * 				<li>ucDstKeyIdx: 
     * 					<ul> 
     * 						<li>If ucDstKeyType = PED_TLK, ucDstKeyIdx = 1;
     * 						<li>If ucDstKeyType = PED_TMK, ucDstKeyIdx = [1~100] (D180 [1~20]);
     * 						<li>If ucDstKeyType = PED_TPK or PED_TAK or PED_TDK, ucDstKeyIdx = [1~100] (D180 [1~20]);
     * 					</ul>
     * 				<li>iDstKeyLen: 8/16/24
     * 				<li>aucDstKeyValue: Key plaintext or ciphertext.
     *            </ul>
     * </div>
     * @param kcvInfoIn
     * <div class="zh">
     * 		
     * 			[输入] <br/>
     * 			iCheckMode: 验证模式 
     * 			<ul>
     * 				<li> 0x00: 无验证
     * 				<li> 0x01: 对8个字节的0x00计算DES/TDES加密,得到 的密文的前4个字节即为KCV
     * 				<li> 0x02: 首先对密钥明文进行奇校验,再对"\x12\x34\x56\x78\x90\x12\x34\x56" 进行DES/TDES 加密运算,得到密文的前4个字节即为KCV
     * 				<li> 0x03: 传入一串数据KcvData,使用源密钥对 [aucDstKeyValue(密文) + KcvData]进行 进行指定模式的MAC运算,得到8个字节的 MAC即为KCV
     * 			</ul>
     * 			<p>aucCheckBuf: <br/>
     * 			<ul>
     * 				<li> iCheckMode 为 0: aucCheckBuf的值无效,系统认为不验证KCV,所 以aucCheckBuf可以为无效数据
     * 				<li> iCheckMode 为 1 或 2 时: aucCheckBuf[0]=KCV的长度(4) aucCheckBuf[1]开始为KCV的值
     * 				<li> iCheckMode 为 3: aucCheckBuf[0]= KcvData长度(KcvDataLen), aucCheckBuf+1: KcvData, 
     * 						aucCheckBuf[1+KcvDataLen]=MAC运算模式值[其取值参考 {@link #pedGetMac}],
     * 						aucCheckBuf[2+KcvDataLen]=KCV长度 aucCheckBuf + 3+KcvDataLen指向KCV的值
     * 			</ul>
     * 			</p>
     * </div>
     * <div class="en">
     * 			[input] <br/>
     * 
     * 			iCheckMode: Check mode
     * 			<ul>
     *	 			<li> 0x00: No KCV check
     * 				<li> 0x01: Perform DES/TDES encryption on 8 bytes 0x00, and use first 4 bytes as KCV.
     * 				<li> 0x02: Perform parity check 1st, then perform DES/TDES encryption on 8 bytes "\x12\x34\x56\x78\x90\x12\x34\x56", and use first 4 bytes as KCV
     * 				<li> 0x03: Send in data KcvData, use source key to perform specified mode of MAC on [aucDesKeyValue + KcvData], and use the result as KCV.
     * 			</ul>
     * 			<p>aucCheckBuf:
     * 			<ul>
     * 				<li> iCheckMode is  0: PED wont check KCV, this data is no meaning.
     * 				<li> iCheckMode is  1 or 2 : aucCheckBuf[0]=KCV length(4) aucCheckBuf[1]~[4] 4bytes KCV
     * 				<li> iCheckMode is  3: aucCheckBuf[0]= KcvData Length, aucCheckBuf+1: KcvData, 
     * 						aucCheckBuf[1+KcvDataLen]=MAC mode[see {@link #pedGetMac}],
     * 						aucCheckBuf[2+KcvDataLen]=KCV length, 
     *	 					aucCheckBuf[3+KcvDataLen]=KCV value
     *			</ul>
     *			</p>
     * </div>
     *            
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */
    public void pedWriteKey(ST_KEY_INFO keyInfoIn, ST_KCV_INFO kcvInfoIn) throws PedException, IOException, ProtoException, CommonException{
    	RespCode rc = new RespCode();
    	
    	kcvInfoIn.isForGetKcv = false;
    	byte[] keyinfo = keyInfoIn.serialToBuffer();
    	byte[] kcvinfo = kcvInfoIn.serialToBuffer();
    	
    	byte[] req = new byte[keyinfo.length + kcvinfo.length];
    	System.arraycopy(keyinfo, 0, req, 0, keyinfo.length);
    	System.arraycopy(kcvinfo, 0, req, keyinfo.length, kcvinfo.length);
    	
    	proto.sendRecv(Cmd.CmdType.PED_WRITE_KEY, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PedException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 用KeyIdx指定的MAC密钥对DataIn进行Mode指定的算法进行MAC运算,输出8字节的MAC结果。
     * </div>
     * <div class="en">
     * To use usKeyIdx key calculate the MAC following the Mode algorithm,
     * output the MAC result
     * </div>
     * 
     * @param KeyIdx
     * <div class="zh">
     *            1~100 (D180 1~20) TAK 索引
     * </div>
     * <div class="en">
     *            1~100 (D180 1~20) TAK index
     * </div>
     * @param DataIn
     * <div class="zh">
     * 			[输入]
     *            需进行MAC运算的数据包
     * </div>
     * <div class="en">
     * 			[input]
     *            The data to calculate MAC.
     * </div>
     *
     * @param DataInLen
     * <div class="zh">
     *            需进行MAC运算的数据包长度
     * </div>
     * <div class="en">
     *            The length data to calculate MAC.
     * </div>
     *          
     * @param Mode
     * <div class="zh">
     * 		<ul>
	 * 			<li>0x00{@link #PED_MAC_MODE_ANSIX9_9}：ANSIX9.9. 将BLOCK1用MAC密钥做DES/TDES加密,加密结果与BLOCK2进行逐位异或后再用TAK做DES/TDES加密,依次进行得到8字节的加密结果 
	 * 			<li>0x01{@link #PED_MAC_MODE_1}：将BLOCK1和BLOCK2进行逐位异或,异或结果与BLOCK3进行逐位异或,依次进行,最后得到8字节的异或结果,将该结果用TAK进行DES/TDES加密运算 
	 * 			<li>0x02{@link #PED_MAC_MODE_ANSIX9_19}： ANSIX9.19. 将BLOCK1用TAK做DES加密(只取前8个字节的key),加密结果与BLOCK2进行逐位异或后再用TAK做DES加密,依次进行得到8字节的加密结果,直到最后一次采用DES/TDES加密
	 * 		</ul>     
     * </div>
     * <div class="en">
     * 		<ul>
	 *			<li>0x00{@link #PED_MAC_MODE_ANSIX9_9}: ANSIX9.9. Doing DES/TDES encryption for BLOCK1 by using MAC key. Doing DES/TDES encryption again by using TAK when and after bitwise XOR the previous encryption result with BLOCK2. Processing in turn to get the 8 bytes encryption result. 
	 *			<li>0x01{@link #PED_MAC_MODE_1}: Doing bitwise XOR for BLOCK1 and BLOCK2; Do bitwise XOR again by using previous XOR result with BLOCK3. Do it in turn and finally get the 8 bytes XOR result. Using TAK to process DES/TDES encryption for the result. 
	 *			<li>0x02{@link #PED_MAC_MODE_ANSIX9_19}: ANSIX9.19. Do DES encryption for BLOCK1 by using TAK (only take the first 8 bytes of key). The encryption result wills bitwise XOR with BLOCK2, and then doing DES encryption by using TAK again. Do it in turn and get the 8 bytes encryption result. Using DES/TDES to encrypt in the last time.
	 *		</ul>
     * </div>
     *
     * @return
     * <div class="zh">
     * 			8字节MAC值
     * </div>
     * <div class="en">
     * 			8 bytes MAC result
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */
    public byte[] pedGetMac(byte KeyIdx, byte[] DataIn, int DataInLen, byte Mode) throws PedException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[4 + DataInLen];
    	req[0] = KeyIdx;
    	req[1] = (byte)(DataInLen / 256);
    	req[2] = (byte)(DataInLen % 256);
    	System.arraycopy(DataIn, 0, req, 3, DataInLen);
    	req[3 + DataInLen] = Mode;
    	
    	byte[] mac = new byte[8]; 
    	
    	proto.sendRecv(Cmd.CmdType.PED_MS_GET_MAC, req, rc, mac);
    	if (rc.code == 0) {
    		return mac;
    	} else {
        	throw new PedException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 使用TDK对DataInLen长度的数据进行DES/TDES运算,使用DES或TDES根据密钥的长度而定。
     * </div>
     * <div class="en">
     * To use TDK encrypt or decrypt data by DES/TDES. Using DES or TDES depends
     * on the key length.
     * </div>
     * 
     * @param KeyIdx
     * <div class="zh">
     *            1~100 (D180 1~20) TDK 索引
     * </div>
     * <div class="en">
     *            1~100 (D180 1~20) TDK index
     * </div>
     * @param DataIn
     * <div class="zh">
     * 			[输入]
     *            需进行DES运算的数据包
     * </div>
     * <div class="en">
     * 			[input]
     *            The data to calculate DES.
     * </div>
     *
     * @param DataInLen
     * <div class="zh">
     *            数据长度 <= 1024, 能被8整除, 如果不能, 则自动右补 0x00
     * </div>
     * <div class="en">
     *             Data length <=1024, should be multiple of 8, right padded with 0x00s if NOT
     * </div>
     *          
     * @param Mode
     * <div class="zh">
	 * 			1: 加密{@link #PED_ENCRYPT}, 0: 解密{@link #PED_DECRYPT}     
     * </div>
     * <div class="en">
	 * 			1: encryption{@link #PED_ENCRYPT}, 0: decryption{@link #PED_DECRYPT}     
     * </div>
     *
     * @return
     * <div class="zh">
     * 			加密/解密 结果
     * </div>
     * <div class="en">
     * 			encryption/decryption result
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */    
    public byte[] pedCalcDES(byte KeyIdx, byte[] DataIn, int DataInLen, byte Mode) throws PedException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	int realDataLen = (DataInLen + 7) / 8 * 8;
    	byte[] req = new byte[4 + realDataLen];
    	req[0] = KeyIdx;
    	req[1] = (byte)(realDataLen / 256);
    	req[2] = (byte)(realDataLen % 256);
    	System.arraycopy(DataIn, 0, req, 3, DataInLen);	//NOTE: if DataInLen < realDataLen, they're already 0s.    	
    	req[3 + realDataLen] = Mode;
    	
    	byte[] resp = new byte[2 + realDataLen];
    	proto.sendRecv(Cmd.CmdType.PED_MS_CALC_DES, req, rc, resp);
    	if (rc.code == 0) {
    		//success
    		if ((Utils.shortFromByteArray(resp, 0)) != realDataLen) {
    			throw new RuntimeException("Invalid resp len for des calculation");
    		} else {
    			byte[] dataOut = new byte[realDataLen];
    			System.arraycopy(resp, 2, dataOut, 0, realDataLen);
    			return dataOut;
    		}
    	} else {
        	throw new PedException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 实现脱机明文PIN校验功能。获取明文PIN,然后按照应用提供的卡片命令与卡片通道号,将明文PIN BLOCK直接发送给卡片(PIN BLOCK格式在用法部分描述)。
     * </div>
     * <div class="en">
     * Verify plaintext offline PIN Get plaintext PIN. Send plaintext PIN BLOCK
     * to card, according to card command and card slot number, which are
     * provided by application.
     * </div>
     * 
     * @param IccSlot
     * <div class="zh">
     *            0x00 卡片所在的卡座号
     * </div>
     * <div class="en">
     *            0x00 ICC slot number
     * </div>
     * @param ExpPinLenIn
     * <div class="zh">
     *            参考 {@link #pedGetPinBlock}
     * </div>
     * <div class="en">
     *            see {@link #pedGetPinBlock}
     * </div>
     *
     * @param Mode
     * <div class="zh">
     *            0x00 IC卡命令模式,现支持符合EMV2000的IC卡命令
     * </div>
     * <div class="en">
     *            0x00 Currently only support EMV2000
     * </div>
     *          
     * @param TimeoutMs
     * <div class="zh">
	 * 			输入PIN的超时时间,单位：毫秒 最大值为300000ms
     * </div>
     * <div class="en">
	 * 			The timeout of PIN entry [ms] Maximum is 300000ms.
     * </div>
     *
     * @return
     * <div class="zh">
     * 			卡片响应的状态码(2字节：SW1+SW2)
     * </div>
     * <div class="en">
     * 			2 bytes Card response code (2 bytes: SW1++SW2)
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */        
    public byte[] pedVerifyPlainPin(byte IccSlot, String ExpPinLenIn, byte Mode, int TimeoutMs) throws PedException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	byte[] expPinLenBytes = ExpPinLenIn.getBytes(); 
    	byte[] req = new byte[7 + expPinLenBytes.length];
    	req[0] = IccSlot;
    	req[1] = (byte)expPinLenBytes.length;
    	System.arraycopy(expPinLenBytes, 0, req, 2, expPinLenBytes.length);
    	req[2 + expPinLenBytes.length] = Mode;
    	req[2 + expPinLenBytes.length + 1] = (byte)(TimeoutMs >> 24);
    	req[2 + expPinLenBytes.length + 2] = (byte)(TimeoutMs >> 16);
    	req[2 + expPinLenBytes.length + 3] = (byte)(TimeoutMs >> 8);
    	req[2 + expPinLenBytes.length + 4] = (byte)(TimeoutMs);

    	
    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += TimeoutMs;

    	byte[] SW = new byte[2]; 
    	try {
	    	proto.sendRecv(Cmd.CmdType.PED_ICC_VERIFY_PLAIN_PIN, req, rc, SW);
	    	if (rc.code == 0) {
	    		//success
	    	} else {
	        	throw new PedException(rc.code);
	    	}
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    	
    	return SW;
    }

    /**
     * <div class="zh">
     * 获取密钥的KCV值,以供对话双方进行密钥验证,用指定的密钥及算法对一段数据进行加密,并返回部分数据密文。
     * </div>
     * <div class="en">
     * Getting value of KCV for key verification of to side,using specific key
     * and algorithm to encrypt data, and then return part of cryptograph.
     * </div>
     * 
     * @param KeyType
     * <div class="zh">
     *            PED_TLK, PED_TMK, PED_TAK, PED_TPK, PED_TDK
     * </div>
     * <div class="en">
     *            PED_TLK, PED_TMK, PED_TAK, PED_TPK, PED_TDK
     * </div>
     * @param KeyIdx
     * <div class="zh">
	 *				密钥的索引号,如： TLK,只能为1。 TMK可取值1~100(D180 1~20)。 TWK可取值1~100(D180 1~20)。 TIK可取值为1~10(D180 1~5)。
     * </div>
     * <div class="en">
     *            Index number of the key,for example:TLK,only 1 <br/>
     *            TMK can select from 1~100 (D180 1~20).<br/>
     *            TWK can select from 1~100 (D180 1~20).<br/>
     *            TIK can select from 1~10 (D180 1~5).
     * </div>
     *
     * @param KcvInfoInOut
     * <div class="zh">
     * 		[输入/输出] <br/>
     * 		<ul>
     *            <li>[输入] iCheckMode = 0x00：使用该密钥对一段数据进行DES/TDES加密运算,生成的密文的前4个字节即为KCV
     *            <li>aucCheckBuf
     *            <ul>
     *            	<li>[输入] 当iCheckMode =0时,aucCheckBuf[0]为需运算数据的长度。 aucCheckBuf+1指向需运算的数据
     *            	<li>[输出] 当函数正确返回时,aucCheckBuf指向4个字节长度的KCV.
     *            	<li>进行运算的数据必须是8的倍数。
     *            	<li>当KeyType为PED_TIK时，返回的KCV值为pedWriteTIK接口写入时的KCV值。如果PedWriteTIK注入密钥时不带KCV校验值，将无法返回KCV校验值.
     *            </ul>
	 *		</ul>
     * 
     * </div>
     * <div class="en">
     * 		[input/output]
     * 		<ul>
     *            <li>[input] iCheckMode iCheckMode = 0x00:using this key to process
     *            	DES/TDES encryption operation for data,the first 4 byte of
     *            	cryptograph are KCV
     *            <li>aucCheckBuf
     *            <ul>
     *            	<li>[input] If iCheckMode =0,aucCheckBuf[0] is the data length of required
     *            		operation. aucCheckBuf+1 point to required operation data
     *            	<li>[output] When returned correctly,aucCheckBuf point to 4 byte length of KCV.
     *            	<li>The length of aucCheckBuf should be multiple of 8.<br/>
     *            	<li>If KeyType is PED_TIK, KCV value is the same as which when writing TIK with pedWriteTIK, none if not provided when writing key.
	 *			  </ul>
	 *		</ul>
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */  
    public void pedGetKcv(byte KeyType, byte KeyIdx, ST_KCV_INFO KcvInfoInOut) throws PedException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	KcvInfoInOut.isForGetKcv = true;
    	byte[] kcvInfo = KcvInfoInOut.serialToBuffer();    	
    	byte[] req = new byte[2 + kcvInfo.length];
    	req[0] = KeyType;
    	req[1] = KeyIdx;
    	System.arraycopy(kcvInfo, 0, req, 2, kcvInfo.length);
    	
    	byte[] resp = new byte[4];
    	proto.sendRecv(Cmd.CmdType.PED_GET_KCV, req, rc, resp);
    	if (rc.code == 0) {
    		//success
    		KcvInfoInOut.serialFromBuffer(resp);
    	} else {
        	throw new PedException(rc.code);
    	}
    }
    
    /**
     * <div class="zh">
     * 获取PED版本信息
     * </div>
     * <div class="en">
     * Get the PED version information.
     * </div>
     * 
     * @return
     * <div class="zh">
     * PED版本信息.
     * </div>
     * <div class="en">
     * PED version information.
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */  
    public String pedGetVer() throws PedException, IOException, ProtoException, CommonException {
        byte[] ver = new byte[17];
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.PED_GET_VER, new byte[0], rc, ver);
    	if (rc.code == 0) {
    		//success
            return new String(ver, 1, ver[0]);
    	} else {
        	throw new PedException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 清除PED里的所有密钥信息
     * </div>
     * <div class="en">
     * Clear all key information of PED.
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */     
    public void pedErase() throws PedException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.PED_ERASE, new byte[0], rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PedException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 设定密钥标签
     * </div>
     * <div class="en">
     * set key tag value
     * </div>
     * 
     * @param KeyTagIn
     * <div class="zh">
     * [输入] 用户指定的密钥标签, 8 bytes.
     * </div>
     * <div class="en">
     * [input] key tag value, 8 bytes
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */     
    public void pedSetKeyTag(byte[] KeyTagIn) throws PedException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();

    	proto.sendRecv(Cmd.CmdType.PED_SET_KEY_TAG, KeyTagIn, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PedException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 1.注入RSA密钥到PED <br/>
     * 2.PED最多支持10组RSA密钥,目前最长只支持256字节长的RSA密钥。 <br/>
     * 3.存储的RSA密钥是公钥还是私钥由密钥的指数长度决定,当密钥指数和模等长时,是私钥。 <br/>
     * 4.PED通过PedWriteRsaKey注入RSA密钥。 <br/>
     * 5.通过PedRSARecover使用已注入的密钥进行RSA运算。 <br/>
     * 6.任何时候,RSA密钥可以进行重写。   
     * </div>
     * <div class="en">
     *  Write in RSA to the PED, PED can maximum support 10 set of RSA Key,and the
     * maximum supported length of RSA key is 256 byte. The stored RSA is the
     * pulic key or private key will be determined by the exponent length. It is
     * private key if the length of key exponent is equal to modulus. PED write
     * in RSA key through PedWriteRsaKey; processing RSA operation by using
     * written key through PedRsaRecover. RSA key can be rewritten at any time.
     * </div>
     * 
     * @param RSAKeyIndex
     * <div class="zh">
     * 		密钥索引the index of RSAKEY, [1~10];
     * </div>
     * <div class="en">
     * 		the index of RSAKEY, [1~10];
     * </div>
     * 
     * @param pstRsakeyIn
     * <div class="zh">
     * 		[输入] RSA 密钥. 参考  {@link ST_RSA_KEY}
     * </div>
     * <div class="en">
     * 		[input] RSA key. see {@link ST_RSA_KEY} 
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */       
    public void pedWriteRsaKey(byte RSAKeyIndex, ST_RSA_KEY pstRsakeyIn) throws PedException, IOException, ProtoException, CommonException{
    	RespCode rc = new RespCode();
    	
    	byte[] rsaKeyInfo = pstRsakeyIn.serialToBuffer();    	
    	byte[] req = new byte[1 + rsaKeyInfo.length];
    	req[0] = RSAKeyIndex;
    	System.arraycopy(rsaKeyInfo, 0, req, 1, rsaKeyInfo.length);
    	
    	proto.sendRecv(Cmd.CmdType.PED_WRITE_RSA_KEY, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PedException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 用存储在PED的RSA密钥进行数据RSA数据运算
     * </div>
     * <div class="en">
     *  Using the RSA key which stored in PED to process RSA data operation.
     * </div>
     * 
     * @param RSAKeyIndex
     * <div class="zh">
     * 		密钥索引the index of RSAKEY, [1~10];
     * </div>
     * <div class="en">
     * 		the index of RSAKEY, [1~10];
     * </div>
     * 
     * @param pucDataIn
     * <div class="zh">
     * 		[输入] 被加解密的数据,和模等长。
     * </div>
     * <div class="en">
     * 		[input] The data to be encrypted/decrypted, it's length MUST equals modulus length
     * </div>
     *
     * @return
     * 		the result
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */       
    public RsaRecoverOutput pedRsaRecover(byte RSAKeyIndex, byte[] pucDataIn) throws PedException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	byte[] req = new byte[1 + 4 + pucDataIn.length];
    	req[0] = RSAKeyIndex;
    	req[1] = (byte)(pucDataIn.length >>> 24);
    	req[2] = (byte)(pucDataIn.length >>> 16);
    	req[3] = (byte)(pucDataIn.length >>> 8);
    	req[4] = (byte)(pucDataIn.length & 0xff);
    	System.arraycopy(pucDataIn, 0, req, 5, pucDataIn.length);
    	
    	byte[] resp = new byte[512 + 128];
    	RsaRecoverOutput result = new RsaRecoverOutput(pucDataIn.length);
    	int len = proto.sendRecv(Cmd.CmdType.PED_RSA_RECOVER, req, rc, resp);
    	if (rc.code == 0 && (len - 128) == pucDataIn.length) {
    		//success
    		System.arraycopy(resp, 0, result.pucData, 0, pucDataIn.length);
   			System.arraycopy(resp, pucDataIn.length, result.pucKeyInfo, 0, 128);
   	    	return result;
    	} else {
        	throw new PedException(rc.code);    		
    	}
    }

    /**
     * <div class="zh">
     * 设定某些功能键的功能。 PED上电后,CLEAR键的默认功能为,持卡人输入PIN时,按CLEAR键,清除已输入的PIN。 可以通过该函数来设置CLEAR键的不同功能。
     * </div>
     * <div class="en">
     *  Setting some function of function key. When PED power on,the default
     * function of CLEAR button: card holder can clear inputted PIN by pressing
     * CLEAR button. Can set different functions for CLEAR button by using this
     * function.
     * </div>
     * 
     * @param ucKey
     * <div class="zh">
     * 	<ul>
     * 		<li>0x00: 表示在已输入的PIN已经清空或者没有输入PIN时按CLEAR键的功能,PED退出输入密码状态,并返回PED_RET_ERR_INPUT_CLEAR 
     * 		<li>0x01: 表示调用该函数后,密码输入的接口(PedGetPinBlock、PedGetPinDukpt、PedVerifyPlainPin、PedVerifyCipherPin)在输入PIN过程中,按下CLEAR键,逐个清除最后输入的PIN, 当清空所有已输入的PIN时,不退出输入PIN函数。
     * 	</ul> 
     * </div>
     * <div class="en">
     * 	<ul>
     * 		<li>0x00: if no PIN code currently (either cleared or not input yet), pressing CLEAR key will quit PIN
     *            input process and return PED_RET_ERR_INPUT_CLEAR
     * 		<li>0x01: pressing CLEAR key only clears PIN code one by one, and won't quit if there's no PIN code.
     * 	</ul>   
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */       
    public void pedSetFunctionKey(byte ucKey) throws PedException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	byte[] req = new byte[1];
    	req[0] = ucKey;

    	proto.sendRecv(Cmd.CmdType.PED_SET_FUNCKEY, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PedException(rc.code);
    	}
    }

	/*
	 * FIXME! unsupported currently
	 */
    /*
    public void pedInjectKey(ST_KEYBLOCK_INFO pstKeyBlockInfoIn) throws PedException, IOException, ProtoException {
    	RespCode rc = new RespCode();
    	
    	byte[] req = pstKeyBlockInfoIn.serialToBuffer();

    	proto.sendRecv(Cmd.CmdType.PED_INJECT_KEY, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PedException(rc.code);
    	}
    }
    */

    /**
     * <div class="zh">
     * 读取下一次计算的KSN。
     * </div>
     * <div class="en">
     *  Reading the KSN which will be computed at next time.
     * </div>
     * 
     * @param GroupIdx
     * <div class="zh">
     * 		1~10(D180 1~5) DUKPT group ID
     * </div>
     * <div class="en">
     * 		1~10(D180 1~5) DUKPT group ID
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		10字节的KSN
     * </div>
     * <div class="en">
     * 		10 bytes KSN
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */           
    public byte[] pedGetDukptKSN(byte GroupIdx) throws PedException, IOException, ProtoException, CommonException {
        byte[] ksnOut = new byte[10];
    	RespCode rc = new RespCode();
    	
    	byte[] req = new byte[1];
    	req[0] = GroupIdx;

    	proto.sendRecv(Cmd.CmdType.PED_DUKPT_GET_KSN, req, rc, ksnOut);
    	if (rc.code == 0) {
    		//success
        	return ksnOut;
    	} else {
        	throw new PedException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * KSN 加1
     * </div>
     * <div class="en">
     *  Increase KSN by 1
     * </div>
     * 
     * @param GroupIdx
     * <div class="zh">
     * 		1~10(D180 1~5) DUKPT group ID
     * </div>
     * <div class="en">
     * 		1~10(D180 1~5) DUKPT group ID
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */
    public void pedDukptIncreaseKsn(byte GroupIdx) throws PedException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	byte[] req = new byte[1];
    	req[0] = GroupIdx;

    	proto.sendRecv(Cmd.CmdType.PED_DUKPT_INCREASE_KSN, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PedException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 写入TIK,并可以选择使用KCV验证密钥正确性。
     * </div>
     * <div class="en">
     *  Write TIK into PED and optionaly check KCV.
     * </div>
     * 
     * @param GroupIdx
     * <div class="zh">
     * 		1~10(D180 1~5) DUKPT group ID
     * </div>
     * <div class="en">
     * 		1~10(D180 1~5) DUKPT group ID
     * </div>
     * @param SrcKeyIdx
     * <div class="zh">
     * 		[0~1] 用于分散的密钥的密钥索引
     * </div>
     * <div class="en">
     * 		[0~1] The key index which is used for TIK Derivation .
     * </div>
     * @param KeyLen
     * <div class="zh">
     * 		8 or 16, TIK的长度,现DUKPT算法支持8/16字节长 度的密钥
     * </div>
     * <div class="en">
     * 		8 or 16, TIK Length, Currently supports 8 and 16 bytes.
     * </div>
     * @param KeyValueIn
     * <div class="zh">
     * 		[输入] TIK 明文 (SrcKeyIdx == 0)或  密文(SrcKeyIdx == 1)
     * </div>
     * <div class="en">
     * 		[input] TIK plain text(SrcKeyIdx == 0) or cryptograph(SrcKeyIdx == 1).
     * </div>
     * @param KsnIn
     * <div class="zh">
     * 		[输入] 初始KSN
     * </div>
     * <div class="en">
     * 		[input] Initial KSN.
     * </div>
     * @param KcvInfoIn
     * <div class="zh">
     * 		[输入] 参考  {@link #pedWriteKey}
     * </div>
     * <div class="en">
     * 		[input] see {@link #pedWriteKey}
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */
    public void pedWriteTIK(byte GroupIdx, byte SrcKeyIdx, byte KeyLen, byte[] KeyValueIn, byte[] KsnIn, ST_KCV_INFO KcvInfoIn) throws IOException, PedException, ProtoException, CommonException {
    	RespCode rc = new RespCode();

    	KcvInfoIn.isForGetKcv = false;
    	byte[] kcvInfo = KcvInfoIn.serialToBuffer();
    	byte[] req = new byte[3 + KeyLen + 10 + kcvInfo.length];
    	req[0] = GroupIdx;
    	req[1] = SrcKeyIdx;
    	req[2] = KeyLen;
    	System.arraycopy(KeyValueIn, 0, req, 3, KeyLen);
    	System.arraycopy(KsnIn, 0, req, 3 + KeyLen, 10);
    	System.arraycopy(kcvInfo, 0, req, 3 + KeyLen + 10, kcvInfo.length);

    	proto.sendRecv(Cmd.CmdType.PED_WRITE_TIK, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PedException(rc.code);
    	}
    }
/*
    private boolean extractPan(byte[] in, byte[] out) {
    	if (in.length > 19 || in.length < 13) {
    		return false;
    	}
    	
    	out[0] = out[1] = out[2] = out[3] = '0';
    	System.arraycopy(in, in.length - 13, out, 4, 12);
    	
    	return true;
    }
*/    
    
    /**
     * <div class="zh">
     * 指定的时限内,扫描键盘上输入的PIN并输出PIN BLOCK加密数据块。 输入ExpPinLenIn指定长度的PIN,输出由Mode指定算法加密的PIN BLOCK。
     * </div>
     * <div class="en">
     * Scan the keyboard PIN entry and output the PIN BLOCK using TPK.
     * </div>
     * 
     * @param KeyIdx
      * <div class="zh">
     *  		[1~100] (D180 [1~20]) TPK index
     * </div>
     * <div class="en">
     * 			 [1~100] (D180 [1~20]) TPK index
     * </div>
     *           
     * @param ExpPinLenIn
     * <div class="zh">
	 *		0~12的枚举集合 可输入的合法密码长度字符串,应用程序把允许的密码长度全部枚举出来,
	 *		并且用","号隔开每个长度,如允许输入4,6位密码并且允许无密码直接按确认,则该字符串应该设置为"0,4,6"。若枚举0长度则示意可以不输任何数字而直接按确认键返回。
     * </div>
     * <div class="en">
     *            Enumeration of 0-12 <br/>
     *            Application enumerates of all possible lengths of PIN. ','
     *            will be used to separate each number of length. If no PIN, or
     *            4 or 6 digits of PIN are allowed, the string will be set as
     *            '0,4,6'. 0 means that no PIN is required, and pressing 'Enter'
     *            will return.
     * </div>

     * @param DataIn
     * <div class="zh">
     * 		[输入]
     * 		<ul>
	 *			<li>当Mode={@link #PED_PINBLOCK_ISO9564_0}时,DataIn指向卡号移位后生成的16位主帐号。 
	 *			<li>当Mode={@link #PED_PINBLOCK_ISO9564_1}时,输入参数为参与PinBlock的格式化,8字节数据(根据ISO9564的规范,该数据可以是随机数、交易流水号或时间戳等)。 
	 *			<li>当Mode={@link #PED_PINBLOCK_ISO9564_3}时,DataIn指向卡号移位后生成的16位主帐号,DataIn+16指向参与PinBlock格式化的8字节数据(根据ISO9564的规范,该数据可以是随机数、交易流水号或时间戳等,但是每个字节的高4位和低4位,均必须在0xA~0xF之间,所以当Mode为0x02时,应用需要将该8字节的数据做此检查,如果不满足要求将返回错误)。 
	 *			<li>当Mode={@link #PED_PINBLOCK_HK_EPS}时,为交易流水号ISN [6 Bytes,ASCII码]]
	 *		</ul>
     * </div>
     * <div class="en">
     * 		[input]
     * 		<ul>
     *            <li>If Mode={@link #PED_PINBLOCK_ISO9564_0}, DataIn is the 16 bytes PAN after shifting.
     *            <li>If Mode={@link #PED_PINBLOCK_ISO9564_1}, refer to ISO9564.
     *            <li>If Mode={@link #PED_PINBLOCK_ISO9564_3}, refer to ISO9564.
     *            <li>If Mode={@link #PED_PINBLOCK_HK_EPS}, datain is ISN [6 Bytes, ASCII code]
     *      </ul>
     * </div>     
     * @param Mode
     * <div class="zh">
     *            PIN BLOCK 格式 <br/>
     *            ISO9564 format 0 {@link #PED_PINBLOCK_ISO9564_0}<br/>
     *            ISO9564 format 1 {@link #PED_PINBLOCK_ISO9564_1}<br/>
     *            ISO9564 format 3 {@link #PED_PINBLOCK_ISO9564_3}<br/>
     *            HK EPS format {@link #PED_PINBLOCK_HK_EPS}
     * </div>
     * <div class="en">
     *            PIN BLOCK format <br/>
     *            ISO9564 format 0 {@link #PED_PINBLOCK_ISO9564_0}<br/>
     *            ISO9564 format 1 {@link #PED_PINBLOCK_ISO9564_1}<br/>
     *            ISO9564 format 3 {@link #PED_PINBLOCK_ISO9564_3}<br/>
     *            HK EPS format {@link #PED_PINBLOCK_HK_EPS}
     * </div>     
     * 
     * @param TimeOutMs
     * <div class="zh">
	 *			     输入PIN的超时时间,单位：毫秒 最大值为300000ms
     * </div>
     * <div class="en">
     *            The timeout of PIN entry [ms, Input] <br/>
     *            Maximum is 300000Ms.
     * </div>
     * 
     * @return
     * <div class="zh">
	 * 			  8字节 PINBlock
     * </div>
     * <div class="en">
     *            8bytes PINBlock
     * </div>     
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */    
    public byte[] pedGetPinBlock(byte KeyIdx, String ExpPinLenIn, byte[] DataIn, byte Mode, int TimeOutMs) throws IOException, PedException, ProtoException, CommonException {
    	RespCode rc = new RespCode();

    	byte[] expPinLenBytes = ExpPinLenIn.getBytes(); 
    	byte[] req = new byte[8 + expPinLenBytes.length + DataIn.length];
    	req[0] = KeyIdx;
    	req[1] = (byte)expPinLenBytes.length;
    	System.arraycopy(expPinLenBytes, 0, req, 2, expPinLenBytes.length);
    	req[2 + expPinLenBytes.length] = (byte)DataIn.length;
    	System.arraycopy(DataIn, 0, req, 2 + expPinLenBytes.length + 1, DataIn.length);
    	req[2 + expPinLenBytes.length + 1 + DataIn.length] = Mode;
    	req[2 + expPinLenBytes.length + 1 + DataIn.length + 1] = (byte)(TimeOutMs >> 24);
    	req[2 + expPinLenBytes.length + 1 + DataIn.length + 2] = (byte)(TimeOutMs >> 16);
    	req[2 + expPinLenBytes.length + 1 + DataIn.length + 3] = (byte)(TimeOutMs >> 8);
    	req[2 + expPinLenBytes.length + 1 + DataIn.length + 4] = (byte)(TimeOutMs);

    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += TimeOutMs;

    	byte[] PinBlockOut = new byte[8];
    	try {
	    	proto.sendRecv(Cmd.CmdType.PED_MS_GET_PIN, req, rc, PinBlockOut);
	    	if (rc.code == 0) {
	    		//success
	    	} else {
	        	throw new PedException(rc.code);
	    	}
    	}finally{
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    	return PinBlockOut;
    }

    /**
     * <div class="zh">
     * 在PED上输入PIN,并使DUKPT的PIN密钥计算PINBlock。
     * </div>
     * <div class="en">
     * Scan the keyboard PIN entry and output the PIN BLOCK using TIK.
     * </div>
     * 
     * @param GroupIdx
      * <div class="zh">
     *  		1~10(D180 1~5) DUKPT 组ID
     * </div>
     * <div class="en">
     * 			1~10(D180 1~5) DUKPT group ID
     * </div>
     *           
     * @param ExpPinLenIn
     * <div class="zh">
	 *		0~12的枚举集合 可输入的合法密码长度字符串,应用程序把允许的密码长度全部枚举出来,
	 *		并且用","号隔开每个长度,如允许输入4,6位密码并且允许无密码直接按确认,则该字符串应该设置为"0,4,6"。若枚举0长度则示意可以不输任何数字而直接按确认键返回。
     * </div>
     * <div class="en">
     *            Enumeration of 0-12 <br/>
     *            Application enumerates of all possible lengths of PIN. ','
     *            will be used to separate each number of length. If no PIN, or
     *            4 or 6 digits of PIN are allowed, the string will be set as
     *            '0,4,6'. 0 means that no PIN is required, and pressing 'Enter'
     *            will return.
     * </div>

     * @param DataIn
     * <div class="zh">
     * 		[输入]
     * 		<ul>
	 *			<li>当Mode={@link #PED_PINBLOCK_ISO9564_0}时,DataIn指向卡号移位后生成的16位主帐号。 
	 *			<li>当Mode={@link #PED_PINBLOCK_ISO9564_1}时,输入参数为参与PinBlock的格式化,8字节数据(根据ISO9564的规范,该数据可以是随机数、交易流水号或时间戳等)。 
	 *			<li>当Mode={@link #PED_PINBLOCK_ISO9564_3}时,DataIn指向卡号移位后生成的16位主帐号,DataIn+16指向参与PinBlock格式化的8字节数据(根据ISO9564的规范,该数据可以是随机数、交易流水号或时间戳等,但是每个字节的高4位和低4位,均必须在0xA~0xF之间,所以当Mode为0x02时,应用需要将该8字节的数据做此检查,如果不满足要求将返回错误)。 
	 *			<li>当Mode={@link #PED_PINBLOCK_HK_EPS}时,为交易流水号ISN [6 Bytes,ASCII码]]
	 *		</ul>
     * </div>
     * <div class="en">
     * 		[input]
     * 		<ul>
     *            <li>If Mode={@link #PED_PINBLOCK_ISO9564_0}, DataIn is the 16 bytes PAN after shifting.
     *            <li>If Mode={@link #PED_PINBLOCK_ISO9564_1}, refer to ISO9564.
     *            <li>If Mode={@link #PED_PINBLOCK_ISO9564_3}, refer to ISO9564.
     *            <li>If Mode={@link #PED_PINBLOCK_HK_EPS}, datain is ISN [6 Bytes, ASCII code]
     *      </ul>
     * </div>     
     * 
     * @param Mode
     * <div class="zh">
     *            PIN BLOCK 格式 <br/>
     *            ISO9564 format 0 {@link #PED_PINBLOCK_ISO9564_0}<br/>
     *            ISO9564 format 1 {@link #PED_PINBLOCK_ISO9564_1}<br/>
     *            ISO9564 format 3 {@link #PED_PINBLOCK_ISO9564_3}<br/>
     *            HK EPS format {@link #PED_PINBLOCK_HK_EPS}
     * </div>
     * <div class="en">
     *            PIN BLOCK format <br/>
     *            ISO9564 format 0 {@link #PED_PINBLOCK_ISO9564_0}<br/>
     *            ISO9564 format 1 {@link #PED_PINBLOCK_ISO9564_1}<br/>
     *            ISO9564 format 3 {@link #PED_PINBLOCK_ISO9564_3}<br/>
     *            HK EPS format {@link #PED_PINBLOCK_HK_EPS}
     * </div>     
     * 
     * @param TimeoutMs
     * <div class="zh">
	 *			     输入PIN的超时时间,单位：毫秒 最大值为300000ms
     * </div>
     * <div class="en">
     *            The timeout of PIN entry [ms, Input] <br/>
     *            Maximum is 300000Ms.
     * </div>
     * 
     * @return
     * 		the result
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */
    public PinDukptOutput pedGetPinDukpt(byte GroupIdx, String ExpPinLenIn, byte[] DataIn, byte Mode, int TimeoutMs)
            throws IOException, PedException, ProtoException, CommonException {
    	RespCode rc = new RespCode();

    	byte[] expPinLenBytes = ExpPinLenIn.getBytes(); 
    	byte[] req = new byte[8 + expPinLenBytes.length + DataIn.length];
    	req[0] = GroupIdx;
    	req[1] = (byte)expPinLenBytes.length;
    	System.arraycopy(expPinLenBytes, 0, req, 2, expPinLenBytes.length);
    	req[2 + expPinLenBytes.length] = (byte)DataIn.length;
    	System.arraycopy(DataIn, 0, req, 2 + expPinLenBytes.length + 1, DataIn.length);
    	req[2 + expPinLenBytes.length + 1 + DataIn.length] = Mode;
    	req[2 + expPinLenBytes.length + 1 + DataIn.length + 1] = (byte)(TimeoutMs >> 24);
    	req[2 + expPinLenBytes.length + 1 + DataIn.length + 2] = (byte)(TimeoutMs >> 16);
    	req[2 + expPinLenBytes.length + 1 + DataIn.length + 3] = (byte)(TimeoutMs >> 8);
    	req[2 + expPinLenBytes.length + 1 + DataIn.length + 4] = (byte)(TimeoutMs);

    	byte[] resp = new byte[18];
    	
    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += TimeoutMs;

    	PinDukptOutput result = new PinDukptOutput();
    	try {
    		proto.sendRecv(Cmd.CmdType.PED_DUKPT_GET_PIN, req, rc, resp);
	    	if (rc.code == 0) {
	    		//success
	    		System.arraycopy(resp, 0, result.pinBlockOut, 0, 8);
	    		System.arraycopy(resp, 8, result.ksnOut, 0, 10);
	        	return result;
	    	} else {
	        	throw new PedException(rc.code);
	    	}
    	}finally{
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    }

    /**
     * <div class="zh">
     * 使用DUKPT计算MAC
     * </div>
     * <div class="en">
     * Calculate MAC using DUKPT.
     * </div>
     * 
     * @param GroupIdx
      * <div class="zh">
     *  		1~10(D180 1~5) DUKPT 组ID
     * </div>
     * <div class="en">
     * 			1~10(D180 1~5) DUKPT group ID
     * </div>
     *           
     * @param DataIn
     * <div class="zh">
	 *			[输入] 需要计算MAC的数据内容, 长度<= 1024, 如果长度不为8字节整除,则自动补0x00
     * </div>
     * <div class="en">
     *          [input] data to be calculated MAC, <= 1024bytes, right padded with 0x00s if it's length is not multiple of 8
     * </div>
     *  
     * @param Mode
     * <div class="zh">
	 *			  参考  {@link #pedGetMac}
     * </div>
     * <div class="en">
	 *			see {@link #pedGetMac}
     * </div>     
     * 
     * @return
     * 		the result
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */    
    public MacDukptOutput pedGetMacDukpt(byte GroupIdx, byte[] DataIn, byte Mode) throws IOException, PedException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	int DataInLen = DataIn.length;
    	byte[] req = new byte[4 + DataInLen];
    	req[0] = GroupIdx;
    	req[1] = (byte)(DataInLen / 256);
    	req[2] = (byte)(DataInLen % 256);
    	System.arraycopy(DataIn, 0, req, 3, DataInLen);
    	req[3 + DataInLen] = Mode;

    	byte[] resp = new byte[18];
    	MacDukptOutput result = new MacDukptOutput();
    	proto.sendRecv(Cmd.CmdType.PED_DUKPT_GET_MAC, req, rc, resp);
    	if (rc.code == 0) {
    		//success
    		System.arraycopy(resp, 0, result.macOut, 0, 8);
    		System.arraycopy(resp, 8, result.ksnOut, 0, 10);
        	return result;
    	} else {
        	throw new PedException(rc.code);
    	}
    }


    /**
     * <div class="zh">
     * 实现脱机密文PIN校验功能。先获取明文PIN,再用应用提供的RsaPinKey对明文PIN按照EMV规范进行加密,然后用应用提供的卡片命令与卡片通道号,将密文PIN直接发送给卡片。
     * </div>
     * <div class="en">
     * Verify offline enciphered PIN. <br/>
     * Get plaintext PIN; Use RsaPinKey, which is provided by application, to
     * encrypt plaintext PIN according to EMV standard <br/>
     * Send enciphered PIN to card, according to card command and card slot
     * number, which are provided by application.
     * </div>
     * 
     * @param IccSlot
     * <div class="zh">
     *            0x00 卡片所在的卡座号
     * </div>
     * <div class="en">
     *            0x00 ICC slot number
     * </div>
     * @param ExpPinLenIn
     * <div class="zh">
     *            参考 {@link #pedGetPinBlock}
     * </div>
     * <div class="en">
     *            see {@link #pedGetPinBlock}
     * </div>
     *
     * @param RsaPinKeyIn
     * <div class="zh">
     *            [输入] 参考 {@link com.pax.mposapi.model.RSA_PINKEY}
     * </div>
     * <div class="en">
     *            [input] see {@link com.pax.mposapi.model.RSA_PINKEY}
     * </div>
     *
     * @param Mode
     * <div class="zh">
     *            0x00 IC卡命令模式,现支持符合EMV2000的IC卡命令
     * </div>
     * <div class="en">
     *            0x00 Currently only support EMV2000
     * </div>
     *          
     * @param TimeoutMs
     * <div class="zh">
	 * 			输入PIN的超时时间,单位：毫秒 最大值为300000ms
     * </div>
     * <div class="en">
	 * 			The timeout of PIN entry [ms] Maximum is 300000ms.
     * </div>
     *
     * @return
     * <div class="zh">
     * 			卡片响应的状态码(2字节：SW1+SW2)
     * </div>
     * <div class="en">
     * 			2 bytes Card response code (2 bytes: SW1+SW2)
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */        
    public byte[] pedVerifyCipherPin(byte IccSlot, String ExpPinLenIn, RSA_PINKEY RsaPinKeyIn, byte Mode, int TimeoutMs) throws IOException,
            PedException, ProtoException, CommonException {
    	RespCode rc = new RespCode();

    	byte[] expPinLenBytes = ExpPinLenIn.getBytes();     	
    	byte[] req = new byte[284 + expPinLenBytes.length];
    	req[0] = IccSlot;
    	req[1] = (byte)expPinLenBytes.length;
    	System.arraycopy(expPinLenBytes, 0, req, 2, expPinLenBytes.length);
    	
    	byte []rsaPinKey = RsaPinKeyIn.serialToBuffer();
    	System.arraycopy(rsaPinKey, 0, req, 2 + expPinLenBytes.length, rsaPinKey.length);

    	req[2 + expPinLenBytes.length + rsaPinKey.length] = Mode;
    	req[2 + expPinLenBytes.length + rsaPinKey.length + 1] = (byte)(TimeoutMs >> 24);
    	req[2 + expPinLenBytes.length + rsaPinKey.length + 2] = (byte)(TimeoutMs >> 16);
    	req[2 + expPinLenBytes.length + rsaPinKey.length + 3] = (byte)(TimeoutMs >> 8);
    	req[2 + expPinLenBytes.length + rsaPinKey.length + 4] = (byte)(TimeoutMs);
    	
    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += TimeoutMs;

    	byte[] SW = new byte[2];
    	try {
	    	proto.sendRecv(Cmd.CmdType.PED_ICC_VERIFY_CIPHER_PIN, req, rc, SW);
	    	if (rc.code == 0) {
	    		//success
	    	} else {
	        	throw new PedException(rc.code);
	    	}
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    	
    	return SW;
    }
    /**
     * <div class="zh">
     * 使用DUKPT的MAC密钥或DES密钥，对输入缓存内数据进行加密或解密。
     * </div>
     * <div class="en">
     * Use MAC key or DES key of DUKPT to encrypt or decrypt the data which have
     * been input into the buffer.
     * </div>
     * 
     * @param GroupIdx
      * <div class="zh">
     *  		1~10(D180 1~5) DUKPT 组ID
     * </div>
     * <div class="en">
     * 			1~10(D180 1~5) DUKPT group ID
     * </div>
     * 
     * @param KeyVarType
      * <div class="zh">
      * 	<ul>
     *  		<li>{@link #PED_DUKPT_DES_WITH_MAC_KEY}， 用请求和应答MAC密钥 
     *  		<li>{@link #PED_DUKPT_DES_WITH_DES_KEY}， 用DUKPT DES密钥运算 
     *  		<li>{@link #PED_DUKPT_DES_WITH_PIN_KEY}. 用DUKPT PIN密钥做EBC加密 (不能做解密)
     *  	</ul>
     * </div>
     * <div class="en">
     * 		<ul>
     *          <li>{@link #PED_DUKPT_DES_WITH_MAC_KEY}, Use request and response MAC key.
     *          <li>{@link #PED_DUKPT_DES_WITH_DES_KEY}, Use DUKPT DES key operation.
     *          <li>{@link #PED_DUKPT_DES_WITH_PIN_KEY}, Use DUKPT PIN key to do the EBC encryption. (It can not
     *            do decryption.)
     *      </ul>
     * </div>
     * 
     * @param pucIV
     * <div class="zh">
     *  		[输入] 8字节初始向量，CBC加解密时需要
     * </div>
     * <div class="en">
     * 			[input] 8bytes IV used for CBC encryption/decryption
     * </div>
     * 
     * @param DataIn
     * <div class="zh">
	 *			[输入]需要计算DES的数据内容, 长度<=8192字节并且能被8整除
     * </div>
     * <div class="en">
     *          [input] data to be calculated DES, length <=8192 bytes and must be multiple of 8. 
     * </div>
     *  
     * @param Mode
     * <div class="zh">
	 *			{@link #PED_DUKPT_DES_EBC_DECRYPTION}:EBC解密<br/> 
	 *			{@link #PED_DUKPT_DES_EBC_ENCRYPTION}:EBC加密<br/>
	 *		  	{@link #PED_DUKPT_DES_CBC_DECRYPTION}:CBC解密<br/>
	 *  		{@link #PED_DUKPT_DES_CBC_ENCRYPTION}:CBC加密
     * </div>
     * <div class="en">
	 *			{@link #PED_DUKPT_DES_EBC_DECRYPTION}:EBC decryption<br/> 
	 *			{@link #PED_DUKPT_DES_EBC_ENCRYPTION}:EBC encryption<br/>
	 *		  	{@link #PED_DUKPT_DES_CBC_DECRYPTION}:CBC decryption<br/>
	 *  		{@link #PED_DUKPT_DES_CBC_ENCRYPTION}:CBC encryption
     * </div>     
     * 
     * @return
     * 		the result
     * 
     * @throws PedException
     * <div class="zh">PED 错误</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */    
    public DukptDesOutput pedDukptDes(byte GroupIdx, byte KeyVarType, byte[] pucIV, byte[] DataIn, byte Mode)
            throws IOException, PedException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	int DataInLen = DataIn.length;
    	byte[] req = new byte[13 + DataInLen];
    	req[0] = GroupIdx;
    	req[1] = KeyVarType;
    	System.arraycopy(pucIV, 0, req, 2, 8);
    	req[10] = (byte)(DataInLen / 256);
    	req[11] = (byte)(DataInLen % 256);
    	System.arraycopy(DataIn, 0, req, 12, DataInLen);
    	req[12 + DataInLen] = Mode;

    	byte[] resp = new byte[2 + (((DataInLen + 7) / 8) * 8) + 10];
		DukptDesOutput result = new DukptDesOutput((((DataInLen + 7) / 8) * 8));
    	proto.sendRecv(Cmd.CmdType.PED_DUKPT_DES, req, rc, resp);
    	if (rc.code == 0) {
    		//success
    		int dataLen = Utils.shortFromByteArray(resp, 0);
    		System.arraycopy(resp, 2, result.dataOut, 0, dataLen);
    		System.arraycopy(resp, 2 + dataLen, result.ksnOut, 0, 10);
        	return result;
    	} else {
        	throw new PedException(rc.code);
    	}
    }

}
