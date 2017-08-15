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
 * PedManager ���ڹ�����Կ,���ݼӽ���<br/>
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
     * ʹ��ָ����Context�����PedManager����
     * </div>
     * <div class="en">
     * Create a PedManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">Ӧ�õ�ǰ��context</div>
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
     * д��һ����Կ,����TLK,TMK��TWK��д��,��ɢ,������ѡ��ʹ��KCV��֤��Կ��ȷ��<br/>
	 * ��һ����Կ�����Ļ�������д�뵽ָ������Կ���������ָ��������λ��, �ú������÷������¼���Ҫ��:<br/>
	 * 1 - ��usScrKeyIdx =0ʱ,ϵͳ��ΪstKey�е�aucDstKeyValue����Կ������,��ȥ�ж�ucSrcKeyType,ucSrcKeyIdx,ֱ�ӽ�aucDstKeyValueд��ucDstKeyType�����ucDstKeyIdxλ��,ֻ�е�PED_TLK������ʱ,������������д����������κ���Կ;<br/>
	 * 2 - PED_TLK����ʱ,����������д�����������Կ,PED_TLKֻ����16��24�ֽ�,���Ҳ�����ע��8�ֽ���Կ,PED_TLKֻ����16�ֽڻ���24�ֽ�;<br/>
	 * 3 - ��д��PED_TLKʱ,PED���ȸ�ʽ��,��������Ѿ����ص���Կ,��д��PED_TLK;<br/>
	 * 4 - ��ucSrcKeyIdxΪ����˵���еĺϷ�ֵʱ,ϵͳ��ΪKeyInfoIn�е�aucDstKeyValue����Կ������,��ͨ��ucSrcKeyType������Կ����ucSrcKeyIdx����Կ��aucDstKeyValue���н���,��д�뵽ucDstKeyType�����ucDstKeyIdxλ��, ����ucDstKeyType >= ucSrcKeyType;<br/>
	 * 5 - ucDstKeyLenֻ��Ϊ8��16,24,��ucDstKeyLenΪ8ʱ,�����Կֻ������DES����,ucDstKeyLenΪ16��24ʱ,��������TDES����;<br/>
	 * 6 - ucDstKeyTypeָ������Կ����,��ucDstKeyType=PED_TPKʱ,�����Կֻ�����ڼ���PIN Block, ��ucDstKeyType = PED_TAKʱ,����Կֻ�����ڼ���MAC, ��ucDstKeyType=PED_TDKʱ,�����Կֻ������DES/TDES�ļӽ�������, �Ӷ������˹�����Կ����;,��֤������Կ���ܵ�Ψһ��;
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
     * 			[����]
     * 			<ul>
     * 				<li>ucSrcKeyType: PED_TLK, PED_TMK, PED_TPK, PED_TAK, PED_TDK
     * 				<li>ucSrcKeyIdx: 
     * 					<ul>	
     * 						<li>��ucSrcKeyType = PED_TLKʱ ucSrcKeyIdx = 1; 
     * 						<li>��ucSrcKeyType = PED_TMKʱ, ucSrcKeyIdx = [1~100] (D180 [1~20]); 
     * 						<li>��ucSrcKeyType = PED_TPK��PED_TAK��PED_TDKʱucSrcKeyIdx = [1~100] (D180 [1~20]);
     * 					</ul>
     * 				<li>ucDstKeyType: PED_TLK, PED_TMK, PED_TPK, PED_TAK, PED_TDK <br/>
     * 				<li>ucDstKeyIdx:
     * 					<ul>
     * 						<li>��ucDstKeyType = PED_TLKʱ ucDstKeyIdx = 1; 
     * 						<li>��ucDstKeyType = PED_TMKʱ, ucDstKeyIdx = [1~100] (D180 [1~20]); 
     * 						<li>��ucDstKeyType = PED_TPK��PED_TAK��PED_TDKʱucDstKeyIdx = [1~100] (D180 [1~20]);
     * 					</ul>
     * 				<li>iDstKeyLen: 8/16/24
     * 				<li>aucDstKeyValue: ��Կ���Ļ�����
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
     * 			[����] <br/>
     * 			iCheckMode: ��֤ģʽ 
     * 			<ul>
     * 				<li> 0x00: ����֤
     * 				<li> 0x01: ��8���ֽڵ�0x00����DES/TDES����,�õ� �����ĵ�ǰ4���ֽڼ�ΪKCV
     * 				<li> 0x02: ���ȶ���Կ���Ľ�����У��,�ٶ�"\x12\x34\x56\x78\x90\x12\x34\x56" ����DES/TDES ��������,�õ����ĵ�ǰ4���ֽڼ�ΪKCV
     * 				<li> 0x03: ����һ������KcvData,ʹ��Դ��Կ�� [aucDstKeyValue(����) + KcvData]���� ����ָ��ģʽ��MAC����,�õ�8���ֽڵ� MAC��ΪKCV
     * 			</ul>
     * 			<p>aucCheckBuf: <br/>
     * 			<ul>
     * 				<li> iCheckMode Ϊ 0: aucCheckBuf��ֵ��Ч,ϵͳ��Ϊ����֤KCV,�� ��aucCheckBuf����Ϊ��Ч����
     * 				<li> iCheckMode Ϊ 1 �� 2 ʱ: aucCheckBuf[0]=KCV�ĳ���(4) aucCheckBuf[1]��ʼΪKCV��ֵ
     * 				<li> iCheckMode Ϊ 3: aucCheckBuf[0]= KcvData����(KcvDataLen), aucCheckBuf+1: KcvData, 
     * 						aucCheckBuf[1+KcvDataLen]=MAC����ģʽֵ[��ȡֵ�ο� {@link #pedGetMac}],
     * 						aucCheckBuf[2+KcvDataLen]=KCV���� aucCheckBuf + 3+KcvDataLenָ��KCV��ֵ
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
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��KeyIdxָ����MAC��Կ��DataIn����Modeָ�����㷨����MAC����,���8�ֽڵ�MAC�����
     * </div>
     * <div class="en">
     * To use usKeyIdx key calculate the MAC following the Mode algorithm,
     * output the MAC result
     * </div>
     * 
     * @param KeyIdx
     * <div class="zh">
     *            1~100 (D180 1~20) TAK ����
     * </div>
     * <div class="en">
     *            1~100 (D180 1~20) TAK index
     * </div>
     * @param DataIn
     * <div class="zh">
     * 			[����]
     *            �����MAC��������ݰ�
     * </div>
     * <div class="en">
     * 			[input]
     *            The data to calculate MAC.
     * </div>
     *
     * @param DataInLen
     * <div class="zh">
     *            �����MAC��������ݰ�����
     * </div>
     * <div class="en">
     *            The length data to calculate MAC.
     * </div>
     *          
     * @param Mode
     * <div class="zh">
     * 		<ul>
	 * 			<li>0x00{@link #PED_MAC_MODE_ANSIX9_9}��ANSIX9.9. ��BLOCK1��MAC��Կ��DES/TDES����,���ܽ����BLOCK2������λ��������TAK��DES/TDES����,���ν��еõ�8�ֽڵļ��ܽ�� 
	 * 			<li>0x01{@link #PED_MAC_MODE_1}����BLOCK1��BLOCK2������λ���,�������BLOCK3������λ���,���ν���,���õ�8�ֽڵ������,���ý����TAK����DES/TDES�������� 
	 * 			<li>0x02{@link #PED_MAC_MODE_ANSIX9_19}�� ANSIX9.19. ��BLOCK1��TAK��DES����(ֻȡǰ8���ֽڵ�key),���ܽ����BLOCK2������λ��������TAK��DES����,���ν��еõ�8�ֽڵļ��ܽ��,ֱ�����һ�β���DES/TDES����
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
     * 			8�ֽ�MACֵ
     * </div>
     * <div class="en">
     * 			8 bytes MAC result
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ʹ��TDK��DataInLen���ȵ����ݽ���DES/TDES����,ʹ��DES��TDES������Կ�ĳ��ȶ�����
     * </div>
     * <div class="en">
     * To use TDK encrypt or decrypt data by DES/TDES. Using DES or TDES depends
     * on the key length.
     * </div>
     * 
     * @param KeyIdx
     * <div class="zh">
     *            1~100 (D180 1~20) TDK ����
     * </div>
     * <div class="en">
     *            1~100 (D180 1~20) TDK index
     * </div>
     * @param DataIn
     * <div class="zh">
     * 			[����]
     *            �����DES��������ݰ�
     * </div>
     * <div class="en">
     * 			[input]
     *            The data to calculate DES.
     * </div>
     *
     * @param DataInLen
     * <div class="zh">
     *            ���ݳ��� <= 1024, �ܱ�8����, �������, ���Զ��Ҳ� 0x00
     * </div>
     * <div class="en">
     *             Data length <=1024, should be multiple of 8, right padded with 0x00s if NOT
     * </div>
     *          
     * @param Mode
     * <div class="zh">
	 * 			1: ����{@link #PED_ENCRYPT}, 0: ����{@link #PED_DECRYPT}     
     * </div>
     * <div class="en">
	 * 			1: encryption{@link #PED_ENCRYPT}, 0: decryption{@link #PED_DECRYPT}     
     * </div>
     *
     * @return
     * <div class="zh">
     * 			����/���� ���
     * </div>
     * <div class="en">
     * 			encryption/decryption result
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ʵ���ѻ�����PINУ�鹦�ܡ���ȡ����PIN,Ȼ����Ӧ���ṩ�Ŀ�Ƭ�����뿨Ƭͨ����,������PIN BLOCKֱ�ӷ��͸���Ƭ(PIN BLOCK��ʽ���÷���������)��
     * </div>
     * <div class="en">
     * Verify plaintext offline PIN Get plaintext PIN. Send plaintext PIN BLOCK
     * to card, according to card command and card slot number, which are
     * provided by application.
     * </div>
     * 
     * @param IccSlot
     * <div class="zh">
     *            0x00 ��Ƭ���ڵĿ�����
     * </div>
     * <div class="en">
     *            0x00 ICC slot number
     * </div>
     * @param ExpPinLenIn
     * <div class="zh">
     *            �ο� {@link #pedGetPinBlock}
     * </div>
     * <div class="en">
     *            see {@link #pedGetPinBlock}
     * </div>
     *
     * @param Mode
     * <div class="zh">
     *            0x00 IC������ģʽ,��֧�ַ���EMV2000��IC������
     * </div>
     * <div class="en">
     *            0x00 Currently only support EMV2000
     * </div>
     *          
     * @param TimeoutMs
     * <div class="zh">
	 * 			����PIN�ĳ�ʱʱ��,��λ������ ���ֵΪ300000ms
     * </div>
     * <div class="en">
	 * 			The timeout of PIN entry [ms] Maximum is 300000ms.
     * </div>
     *
     * @return
     * <div class="zh">
     * 			��Ƭ��Ӧ��״̬��(2�ֽڣ�SW1+SW2)
     * </div>
     * <div class="en">
     * 			2 bytes Card response code (2 bytes: SW1++SW2)
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��ȡ��Կ��KCVֵ,�Թ��Ի�˫��������Կ��֤,��ָ������Կ���㷨��һ�����ݽ��м���,�����ز����������ġ�
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
	 *				��Կ��������,�磺 TLK,ֻ��Ϊ1�� TMK��ȡֵ1~100(D180 1~20)�� TWK��ȡֵ1~100(D180 1~20)�� TIK��ȡֵΪ1~10(D180 1~5)��
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
     * 		[����/���] <br/>
     * 		<ul>
     *            <li>[����] iCheckMode = 0x00��ʹ�ø���Կ��һ�����ݽ���DES/TDES��������,���ɵ����ĵ�ǰ4���ֽڼ�ΪKCV
     *            <li>aucCheckBuf
     *            <ul>
     *            	<li>[����] ��iCheckMode =0ʱ,aucCheckBuf[0]Ϊ���������ݵĳ��ȡ� aucCheckBuf+1ָ�������������
     *            	<li>[���] ��������ȷ����ʱ,aucCheckBufָ��4���ֽڳ��ȵ�KCV.
     *            	<li>������������ݱ�����8�ı�����
     *            	<li>��KeyTypeΪPED_TIKʱ�����ص�KCVֵΪpedWriteTIK�ӿ�д��ʱ��KCVֵ�����PedWriteTIKע����Կʱ����KCVУ��ֵ�����޷�����KCVУ��ֵ.
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
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��ȡPED�汾��Ϣ
     * </div>
     * <div class="en">
     * Get the PED version information.
     * </div>
     * 
     * @return
     * <div class="zh">
     * PED�汾��Ϣ.
     * </div>
     * <div class="en">
     * PED version information.
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ���PED���������Կ��Ϣ
     * </div>
     * <div class="en">
     * Clear all key information of PED.
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * �趨��Կ��ǩ
     * </div>
     * <div class="en">
     * set key tag value
     * </div>
     * 
     * @param KeyTagIn
     * <div class="zh">
     * [����] �û�ָ������Կ��ǩ, 8 bytes.
     * </div>
     * <div class="en">
     * [input] key tag value, 8 bytes
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * 1.ע��RSA��Կ��PED <br/>
     * 2.PED���֧��10��RSA��Կ,Ŀǰ�ֻ֧��256�ֽڳ���RSA��Կ�� <br/>
     * 3.�洢��RSA��Կ�ǹ�Կ����˽Կ����Կ��ָ�����Ⱦ���,����Կָ����ģ�ȳ�ʱ,��˽Կ�� <br/>
     * 4.PEDͨ��PedWriteRsaKeyע��RSA��Կ�� <br/>
     * 5.ͨ��PedRSARecoverʹ����ע�����Կ����RSA���㡣 <br/>
     * 6.�κ�ʱ��,RSA��Կ���Խ�����д��   
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
     * 		��Կ����the index of RSAKEY, [1~10];
     * </div>
     * <div class="en">
     * 		the index of RSAKEY, [1~10];
     * </div>
     * 
     * @param pstRsakeyIn
     * <div class="zh">
     * 		[����] RSA ��Կ. �ο�  {@link ST_RSA_KEY}
     * </div>
     * <div class="en">
     * 		[input] RSA key. see {@link ST_RSA_KEY} 
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * �ô洢��PED��RSA��Կ��������RSA��������
     * </div>
     * <div class="en">
     *  Using the RSA key which stored in PED to process RSA data operation.
     * </div>
     * 
     * @param RSAKeyIndex
     * <div class="zh">
     * 		��Կ����the index of RSAKEY, [1~10];
     * </div>
     * <div class="en">
     * 		the index of RSAKEY, [1~10];
     * </div>
     * 
     * @param pucDataIn
     * <div class="zh">
     * 		[����] ���ӽ��ܵ�����,��ģ�ȳ���
     * </div>
     * <div class="en">
     * 		[input] The data to be encrypted/decrypted, it's length MUST equals modulus length
     * </div>
     *
     * @return
     * 		the result
     * 
     * @throws PedException
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * �趨ĳЩ���ܼ��Ĺ��ܡ� PED�ϵ��,CLEAR����Ĭ�Ϲ���Ϊ,�ֿ�������PINʱ,��CLEAR��,����������PIN�� ����ͨ���ú���������CLEAR���Ĳ�ͬ���ܡ�
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
     * 		<li>0x00: ��ʾ���������PIN�Ѿ���ջ���û������PINʱ��CLEAR���Ĺ���,PED�˳���������״̬,������PED_RET_ERR_INPUT_CLEAR 
     * 		<li>0x01: ��ʾ���øú�����,��������Ľӿ�(PedGetPinBlock��PedGetPinDukpt��PedVerifyPlainPin��PedVerifyCipherPin)������PIN������,����CLEAR��,��������������PIN, ����������������PINʱ,���˳�����PIN������
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
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��ȡ��һ�μ����KSN��
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
     * 		10�ֽڵ�KSN
     * </div>
     * <div class="en">
     * 		10 bytes KSN
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * KSN ��1
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
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * д��TIK,������ѡ��ʹ��KCV��֤��Կ��ȷ�ԡ�
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
     * 		[0~1] ���ڷ�ɢ����Կ����Կ����
     * </div>
     * <div class="en">
     * 		[0~1] The key index which is used for TIK Derivation .
     * </div>
     * @param KeyLen
     * <div class="zh">
     * 		8 or 16, TIK�ĳ���,��DUKPT�㷨֧��8/16�ֽڳ� �ȵ���Կ
     * </div>
     * <div class="en">
     * 		8 or 16, TIK Length, Currently supports 8 and 16 bytes.
     * </div>
     * @param KeyValueIn
     * <div class="zh">
     * 		[����] TIK ���� (SrcKeyIdx == 0)��  ����(SrcKeyIdx == 1)
     * </div>
     * <div class="en">
     * 		[input] TIK plain text(SrcKeyIdx == 0) or cryptograph(SrcKeyIdx == 1).
     * </div>
     * @param KsnIn
     * <div class="zh">
     * 		[����] ��ʼKSN
     * </div>
     * <div class="en">
     * 		[input] Initial KSN.
     * </div>
     * @param KcvInfoIn
     * <div class="zh">
     * 		[����] �ο�  {@link #pedWriteKey}
     * </div>
     * <div class="en">
     * 		[input] see {@link #pedWriteKey}
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ָ����ʱ����,ɨ������������PIN�����PIN BLOCK�������ݿ顣 ����ExpPinLenInָ�����ȵ�PIN,�����Modeָ���㷨���ܵ�PIN BLOCK��
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
	 *		0~12��ö�ټ��� ������ĺϷ����볤���ַ���,Ӧ�ó������������볤��ȫ��ö�ٳ���,
	 *		������","�Ÿ���ÿ������,����������4,6λ���벢������������ֱ�Ӱ�ȷ��,����ַ���Ӧ������Ϊ"0,4,6"����ö��0������ʾ����Բ����κ����ֶ�ֱ�Ӱ�ȷ�ϼ����ء�
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
     * 		[����]
     * 		<ul>
	 *			<li>��Mode={@link #PED_PINBLOCK_ISO9564_0}ʱ,DataInָ�򿨺���λ�����ɵ�16λ���ʺš� 
	 *			<li>��Mode={@link #PED_PINBLOCK_ISO9564_1}ʱ,�������Ϊ����PinBlock�ĸ�ʽ��,8�ֽ�����(����ISO9564�Ĺ淶,�����ݿ������������������ˮ�Ż�ʱ�����)�� 
	 *			<li>��Mode={@link #PED_PINBLOCK_ISO9564_3}ʱ,DataInָ�򿨺���λ�����ɵ�16λ���ʺ�,DataIn+16ָ�����PinBlock��ʽ����8�ֽ�����(����ISO9564�Ĺ淶,�����ݿ������������������ˮ�Ż�ʱ�����,����ÿ���ֽڵĸ�4λ�͵�4λ,��������0xA~0xF֮��,���Ե�ModeΪ0x02ʱ,Ӧ����Ҫ����8�ֽڵ��������˼��,���������Ҫ�󽫷��ش���)�� 
	 *			<li>��Mode={@link #PED_PINBLOCK_HK_EPS}ʱ,Ϊ������ˮ��ISN [6 Bytes,ASCII��]]
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
     *            PIN BLOCK ��ʽ <br/>
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
	 *			     ����PIN�ĳ�ʱʱ��,��λ������ ���ֵΪ300000ms
     * </div>
     * <div class="en">
     *            The timeout of PIN entry [ms, Input] <br/>
     *            Maximum is 300000Ms.
     * </div>
     * 
     * @return
     * <div class="zh">
	 * 			  8�ֽ� PINBlock
     * </div>
     * <div class="en">
     *            8bytes PINBlock
     * </div>     
     * 
     * @throws PedException
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��PED������PIN,��ʹDUKPT��PIN��Կ����PINBlock��
     * </div>
     * <div class="en">
     * Scan the keyboard PIN entry and output the PIN BLOCK using TIK.
     * </div>
     * 
     * @param GroupIdx
      * <div class="zh">
     *  		1~10(D180 1~5) DUKPT ��ID
     * </div>
     * <div class="en">
     * 			1~10(D180 1~5) DUKPT group ID
     * </div>
     *           
     * @param ExpPinLenIn
     * <div class="zh">
	 *		0~12��ö�ټ��� ������ĺϷ����볤���ַ���,Ӧ�ó������������볤��ȫ��ö�ٳ���,
	 *		������","�Ÿ���ÿ������,����������4,6λ���벢������������ֱ�Ӱ�ȷ��,����ַ���Ӧ������Ϊ"0,4,6"����ö��0������ʾ����Բ����κ����ֶ�ֱ�Ӱ�ȷ�ϼ����ء�
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
     * 		[����]
     * 		<ul>
	 *			<li>��Mode={@link #PED_PINBLOCK_ISO9564_0}ʱ,DataInָ�򿨺���λ�����ɵ�16λ���ʺš� 
	 *			<li>��Mode={@link #PED_PINBLOCK_ISO9564_1}ʱ,�������Ϊ����PinBlock�ĸ�ʽ��,8�ֽ�����(����ISO9564�Ĺ淶,�����ݿ������������������ˮ�Ż�ʱ�����)�� 
	 *			<li>��Mode={@link #PED_PINBLOCK_ISO9564_3}ʱ,DataInָ�򿨺���λ�����ɵ�16λ���ʺ�,DataIn+16ָ�����PinBlock��ʽ����8�ֽ�����(����ISO9564�Ĺ淶,�����ݿ������������������ˮ�Ż�ʱ�����,����ÿ���ֽڵĸ�4λ�͵�4λ,��������0xA~0xF֮��,���Ե�ModeΪ0x02ʱ,Ӧ����Ҫ����8�ֽڵ��������˼��,���������Ҫ�󽫷��ش���)�� 
	 *			<li>��Mode={@link #PED_PINBLOCK_HK_EPS}ʱ,Ϊ������ˮ��ISN [6 Bytes,ASCII��]]
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
     *            PIN BLOCK ��ʽ <br/>
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
	 *			     ����PIN�ĳ�ʱʱ��,��λ������ ���ֵΪ300000ms
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
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ʹ��DUKPT����MAC
     * </div>
     * <div class="en">
     * Calculate MAC using DUKPT.
     * </div>
     * 
     * @param GroupIdx
      * <div class="zh">
     *  		1~10(D180 1~5) DUKPT ��ID
     * </div>
     * <div class="en">
     * 			1~10(D180 1~5) DUKPT group ID
     * </div>
     *           
     * @param DataIn
     * <div class="zh">
	 *			[����] ��Ҫ����MAC����������, ����<= 1024, ������Ȳ�Ϊ8�ֽ�����,���Զ���0x00
     * </div>
     * <div class="en">
     *          [input] data to be calculated MAC, <= 1024bytes, right padded with 0x00s if it's length is not multiple of 8
     * </div>
     *  
     * @param Mode
     * <div class="zh">
	 *			  �ο�  {@link #pedGetMac}
     * </div>
     * <div class="en">
	 *			see {@link #pedGetMac}
     * </div>     
     * 
     * @return
     * 		the result
     * 
     * @throws PedException
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ʵ���ѻ�����PINУ�鹦�ܡ��Ȼ�ȡ����PIN,����Ӧ���ṩ��RsaPinKey������PIN����EMV�淶���м���,Ȼ����Ӧ���ṩ�Ŀ�Ƭ�����뿨Ƭͨ����,������PINֱ�ӷ��͸���Ƭ��
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
     *            0x00 ��Ƭ���ڵĿ�����
     * </div>
     * <div class="en">
     *            0x00 ICC slot number
     * </div>
     * @param ExpPinLenIn
     * <div class="zh">
     *            �ο� {@link #pedGetPinBlock}
     * </div>
     * <div class="en">
     *            see {@link #pedGetPinBlock}
     * </div>
     *
     * @param RsaPinKeyIn
     * <div class="zh">
     *            [����] �ο� {@link com.pax.mposapi.model.RSA_PINKEY}
     * </div>
     * <div class="en">
     *            [input] see {@link com.pax.mposapi.model.RSA_PINKEY}
     * </div>
     *
     * @param Mode
     * <div class="zh">
     *            0x00 IC������ģʽ,��֧�ַ���EMV2000��IC������
     * </div>
     * <div class="en">
     *            0x00 Currently only support EMV2000
     * </div>
     *          
     * @param TimeoutMs
     * <div class="zh">
	 * 			����PIN�ĳ�ʱʱ��,��λ������ ���ֵΪ300000ms
     * </div>
     * <div class="en">
	 * 			The timeout of PIN entry [ms] Maximum is 300000ms.
     * </div>
     *
     * @return
     * <div class="zh">
     * 			��Ƭ��Ӧ��״̬��(2�ֽڣ�SW1+SW2)
     * </div>
     * <div class="en">
     * 			2 bytes Card response code (2 bytes: SW1+SW2)
     * </div>
     * 
     * @throws PedException
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ʹ��DUKPT��MAC��Կ��DES��Կ�������뻺�������ݽ��м��ܻ���ܡ�
     * </div>
     * <div class="en">
     * Use MAC key or DES key of DUKPT to encrypt or decrypt the data which have
     * been input into the buffer.
     * </div>
     * 
     * @param GroupIdx
      * <div class="zh">
     *  		1~10(D180 1~5) DUKPT ��ID
     * </div>
     * <div class="en">
     * 			1~10(D180 1~5) DUKPT group ID
     * </div>
     * 
     * @param KeyVarType
      * <div class="zh">
      * 	<ul>
     *  		<li>{@link #PED_DUKPT_DES_WITH_MAC_KEY}�� �������Ӧ��MAC��Կ 
     *  		<li>{@link #PED_DUKPT_DES_WITH_DES_KEY}�� ��DUKPT DES��Կ���� 
     *  		<li>{@link #PED_DUKPT_DES_WITH_PIN_KEY}. ��DUKPT PIN��Կ��EBC���� (����������)
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
     *  		[����] 8�ֽڳ�ʼ������CBC�ӽ���ʱ��Ҫ
     * </div>
     * <div class="en">
     * 			[input] 8bytes IV used for CBC encryption/decryption
     * </div>
     * 
     * @param DataIn
     * <div class="zh">
	 *			[����]��Ҫ����DES����������, ����<=8192�ֽڲ����ܱ�8����
     * </div>
     * <div class="en">
     *          [input] data to be calculated DES, length <=8192 bytes and must be multiple of 8. 
     * </div>
     *  
     * @param Mode
     * <div class="zh">
	 *			{@link #PED_DUKPT_DES_EBC_DECRYPTION}:EBC����<br/> 
	 *			{@link #PED_DUKPT_DES_EBC_ENCRYPTION}:EBC����<br/>
	 *		  	{@link #PED_DUKPT_DES_CBC_DECRYPTION}:CBC����<br/>
	 *  		{@link #PED_DUKPT_DES_CBC_ENCRYPTION}:CBC����
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
     * <div class="zh">PED ����</div>
     * <div class="en">PED error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
