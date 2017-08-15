package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;
import com.pax.mposapi.model.APDU_RESP;
import com.pax.mposapi.model.APDU_SEND;
import com.pax.mposapi.model.PICC_PARA;
import com.pax.mposapi.util.Utils;

/**
 * <div class="zh">
 * PiccManager 用于与非接触式IC卡片(Proximity IC Card)交互信息
 * </div>
 * <div class="en">
 * PiccManager is used to interact with Proximity IC Card. 
 * </div>
 *
 */
public class PiccManager {
    public static final byte PICC_LIGHT_RED = 0x01;
    public static final byte PICC_LIGHT_GREEN = 0x02;
    public static final byte PICC_LIGHT_YELLOW = 0x04;
    public static final byte PICC_LIGHT_BLUE = 0x08;    
    
    public static final byte PICC_DETECT_MODE_ISO14443 = 0x00;
    public static final byte PICC_DETECT_MODE_EMV = 0x01;
    public static final byte PICC_DETECT_MODE_TYPE_A_ONLY = 'A';
    public static final byte PICC_DETECT_MODE_TYPE_B_ONLY = 'B';
    public static final byte PICC_DETECT_MODE_TYPE_M1_ONLY = 'M';

    public static final byte PICC_CARD_TYPE_A = 'A';
    public static final byte PICC_CARD_TYPE_B = 'B';
    public static final byte PICC_CARD_TYPE_M = 'M';

    public static final byte PICC_REMOVE_MODE_HALT = 'H';
    public static final byte PICC_REMOVE_MODE_REMOVE = 'R';
    public static final byte PICC_REMOVE_MODE_EMV = 'E';
    
    public static final byte PICC_M1_OPERATE_INC_VALUE = '+';
    public static final byte PICC_M1_OPERATE_DEC_VALUE = '-';
    public static final byte PICC_M1_OPERATE_BACKUP    = '>';
    	
    private static final String TAG = "PiccManager";
    private final Proto proto;
    private static PiccManager instance;

    /**
     * 
     * Output of {@link #piccDetect} method.
     * 
     */
    public class PiccCardInfo {
        /**
		* <div class="zh">
		* 		1字节数据表示卡类型 <br/>
		*      <ul>
		*            <li>'A'{@link #PICC_CARD_TYPE_A}: type A 卡<br/>
		*            <li>'B'{@link #PICC_CARD_TYPE_B}: type B 卡<br/>
		*            <li>'M'{@link #PICC_CARD_TYPE_M}: M1 卡<br/>
		*      </ul>
		* </div>
		* 
		* <div class="en">
		*      <ul>
		*            <li>'A'{@link #PICC_CARD_TYPE_A}:Found type A card<br/>
		*            <li>'B'{@link #PICC_CARD_TYPE_B}:Found type B card<br/>
		*            <li>'M'{@link #PICC_CARD_TYPE_M}:Found M1 card<br/>
		*      </ul>
		* </div>
        */
        public byte CardType;
        /**
         * <div class="zh">
         *          序列号信息。一般序列号为4字节、7字节或10字节。
         * </div>
         * 
         * <div class="en">
         *          serial no value. usually 4, 7 or 10 bytes
         * </div>
         */
        public byte[] SerialInfo = null;
        /**
	 	 * <div class="zh">
	 	 *          卡片逻辑通道号, 该通道号由驱动内部分配和指定,取值范围为0~14
		 * </div>
		 * 
		 * <div class="en">
		 *          card logical channel number. The channel number is allocated internally by driver, and
		 *            range is 0~14
		 * </div>
		 * 
         */
        public byte CID;
        /**
	     * <div class="zh">
	     *         详细错误代码、卡片响应信息等内容 <br/>
	     *         格式: 错误号（2字节）+卡片相关信息（L+V）+保留字节<br/>
					Other [2...]: <br/>
					<ul>
					 	<li>对于A型卡，返回卡片的ATS(Answer To Select)信息，其长度不超过62字节 
					 	<li>对于B型卡，返回卡片的ATQB(Answer To Request B)信息，其长度为12字节
					 	<li>对于M1卡，返回卡片的ATQA(Answer To Request A)信息，其长度为2字节
					 	<li>有关ATS、ATQB、ATQA的详细信息请查阅ISO14443-3、ISO14443-4的相关部分。 
					</ul>
					Other[…299]：保留字节，用于未来扩展；目前全输出0x00 若需要输出该信息，Other缓冲区大小至少应为300字节。<br/>
	     * </div>
	     * 
	     * <div class="en">
	     *         detailed error code and card response information<br/>
	     *         Format: error code (2 bytes) + card response data (L + V) + reserved <br/>  
					Other[0-1]: return detailed error code(low bytes ahead); As card searching process is complicated, use this return value to locate exception error accurately 
					Other [2...]: <br/>
					<ul>
						<li>For type A card, ATS (Answer To Select) is returned, and its length should be less than 62 bytes
						<li>For type B card, ATQB (Answer To Request B) is returned, and its length should be 12 bytes
						<li>For M1 card, ATQA (Answer To Request A) is returned, and its length should be 2 bytes. 
						<li>Please refer to relevant sections in ISO14443-3 and ISO14443-4 for detailed information of ATS, ATQB and ATQA.
					</ul>
					Other[...299]: content at the end are reserved bytes for future use; output is 0x00 at present If the message needs outputting, the size of the buffer should be at lease 300 bytes.            
	     * </div>
         */
        public byte[] Other = null;

        /**
         * Create an PiccCardInfo instance.
         * 
         */
        PiccCardInfo(byte[] CardType, byte[] SerialNo, byte[] CID, byte[] Other) {
            this.CardType = CardType[0];
            this.SerialInfo = SerialNo;
            this.CID = CID[0];
            this.Other = Other;
        }

        /**
         * Create an PiccCardInfo instance.
         * 
         */
        PiccCardInfo() {
        }

    }
    
    /**
     * <div class="zh">
     * 使用指定的Context构造出PiccManager对象
     * </div>
     * <div class="en">
     * Create a PiccManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">应用当前的context</div>
     * <div class="en">application context currently</div>
     */
    private PiccManager(Context context) {
    	proto = Proto.getInstance(context);
    }

    /**
     * Create a PiccManager instance with a given Context
     * 
     * @param context
     *            application context currently
     */
    public static PiccManager getInstance(Context context) {
        if (instance == null) {
        	instance = new PiccManager(context);
        }
        return instance;
    }
        
    /**
     * <div class="zh">
     * 对非接触卡模块上电并复位,检查复位后模块初始状态是否正常。
     * </div>
     * <div class="en">
     * Power on and reset contactless module, and check whether initial staus of
     * the module is normal.
     * </div>
     * 
     * @throws PiccException
     * <div class="zh">非接卡阅读器错误</div>
     * <div class="en">proximity IC card reader error</div>
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
    public void piccOpen() throws PiccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.PICC_OPEN, new byte[0], rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PiccException(rc.code);    		
    	}
		//return (rc.code == 0);
    }

    /**
     * <div class="zh">
     * 写入指定的参数设置，用于适应较特殊的应用环境；或者读取当前的参数设置。
     * </div>
     * <div class="en">
     * Write specific parameter settings to suit special application
     * environment; or read the current parameter settings.
     * </div>
     * 
     * @param mode
     * <div class="zh">
     *            ‘r’或‘R’;<br/>
     *            ‘w’或‘W’用于指定参数设置模 式： <br/>
     *            ‘r’或‘R’,表示读取; <br/>
     *            ‘w’或‘W’,表示写入.
     * </div>
     * 
     * <div class="en">
     *            'r'or 'R';<br/>
     *            'w'or'W' are Used to specify parameter setting mode: <br/>
     *            'r'or 'R' means reading; <br/>
     *            'w' or 'W' means writing.
     * </div>
     * 
     * @param picc_para
     * <div class="zh">
     *            [输入] 参数设置, 参见  {@link com.pax.mposapi.model.PICC_PARA}. 如果是读操作,可以为null
     * </div>
     * <div class="en">
     *            [input] parameter settings, see {@link com.pax.mposapi.model.PICC_PARA}. For reading, can be null
     * </div>
     * 
     * @return
     * 		the returned result, only valid for reading operation
     * 
     * @throws PiccException
     * <div class="zh">非接卡阅读器错误</div>
     * <div class="en">proximity IC card reader error</div>
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
    public PICC_PARA piccSetup(byte mode, PICC_PARA picc_para) throws PiccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	if (picc_para == null) {
    		picc_para = new PICC_PARA();
    	}
    	byte[] pp = picc_para.serialToBuffer(); 
    	byte[] req;
    	byte[] resp;
    	
    	if (mode == 'w' || mode == 'W') {
	     	req = new byte[3 + pp.length];

	     	req[1] = (byte)(pp.length / 256);
	    	req[2] = (byte)(pp.length % 256);
	    	System.arraycopy(pp, 0, req, 3, pp.length);
	    	
	    	resp = new byte[0];
    	} else {
    		req = new byte[1];
	    	resp = new byte[2 + pp.length];
    	}
    	
     	req[0] = mode;
    	
    	proto.sendRecv(Cmd.CmdType.PICC_SETUP, req, rc, resp);
    	if (rc.code == 0) {
    		//success
    		if (mode == 'r' || mode == 'R') {
    			byte[] para = new byte[pp.length];
    			System.arraycopy(resp, 2, para, 0, pp.length);
    			picc_para.serialFromBuffer(para);
    			return picc_para;
    		} else {
    			return picc_para;
    		}
    	} else {
        	throw new PiccException(rc.code);    		
    	}
    }

    /**
     * <div class="zh">
     * 按指定的模式搜寻PICC卡片;搜到卡片后,将其选中并激活。感应区内不允许多卡存在。
     * </div>
     * <div class="en">
     * Detect PICC card according to appointed mode; when find the card, chooses
     * and activates it.
     * </div>
     * 
     * @param Mode <br/>
     * <div class="zh">
     * 		<ul>
     *            <li>0{@link #PICC_DETECT_MODE_ISO14443} : 搜寻A型卡和B型卡一次, 此模式适用于需要增强多卡检测功能的场合。 该模式是符合ISO14443规范的寻卡模式<br/>
     *            <li>1{@link #PICC_DETECT_MODE_EMV} : 搜寻A型卡和B型卡一次；此模式为EMV寻卡模式，通常使用该模式<br/>
     *            <li>'a'or'A'{@link #PICC_DETECT_MODE_TYPE_A_ONLY}: 只搜寻A型卡一次<br/>
     *            <li>'b'or'B'{@link #PICC_DETECT_MODE_TYPE_B_ONLY}: 只搜寻B型卡一次<br/>
     *            <li>'m'or'M'{@link #PICC_DETECT_MODE_TYPE_M1_ONLY}: 只搜寻M1型卡一次<br/>
     *            <li>其他值 : 保留
     *      </ul>
     * </div>
     * 
     * <div class="en">
     * 		<ul>
     *            <li>0{@link #PICC_DETECT_MODE_ISO14443}:Detect type A card once, and detect type B card once; this conforms to ISO14443 <br/>
     *            <li>1{@link #PICC_DETECT_MODE_EMV} :Detect type A card once, and detect type B card once; this conforms to EMV, generally you should use this mode <br/>            
     *            <li>'a'or'A'{@link #PICC_DETECT_MODE_TYPE_A_ONLY}: Only detect type A card once;<br/>
     *            <li>'b'or'B'{@link #PICC_DETECT_MODE_TYPE_B_ONLY}: Only detect type B card once;<br/>
     *            <li>'m'or'M'{@link #PICC_DETECT_MODE_TYPE_M1_ONLY}: Only detect type M1 card once;<br/>
     *            <li>Others:Reserved
     *      </ul>
     * </div>
     * 
     * @return
     * <div class="zh">
     *            非null: 检测到卡片 <br/>
     *            null: 未检测到卡片<br/> 
     * </div>
     * 
     * <div class="en">
     *            non null: card detected<br/>
     *            null: no card detected<br/> 
     * </div>
     * 
     * @throws PiccException
     * <div class="zh">非接卡阅读器错误</div>
     * <div class="en">proximity IC card reader error</div>
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
    public PiccCardInfo piccDetect(byte Mode) throws PiccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = Mode;
    	
    	byte[] resp = new byte[1024];
    	PiccCardInfo result = new PiccCardInfo();
    	int len = proto.sendRecv(Cmd.CmdType.PICC_DETECT, req, rc, resp);
    	if (rc.code == 0) {
    		//success
   			result.CardType = resp[0];
   			
   			result.SerialInfo = new byte[resp[1]];
   			System.arraycopy(resp, 2, result.SerialInfo, 0, resp[1]);
   			
   			result.CID = resp[2 + resp[1]];
   			
   			result.Other = new byte[len - (2 + resp[1] + 1)];
   			System.arraycopy(resp, 2 + resp[1] + 1, result.Other, 0, len - (2 + resp[1] + 1));
   			
    		return result;
    	} else if (rc.code == 3) {
    		return null;
    	} else {
        	throw new PiccException(rc.code);    		
    	}
    }

    /**
     * <div class="zh">
     * 在指定的通道上,向卡片发送APDU格式的数据,并接收响应。
     * </div>
     * <div class="en">
     * In specific channel, send data of APDU format to card and receive
     * response
     * </div>
     * 
     * @param cid
     * <div class="zh">
     *            用于指定卡片逻辑通道号;该通道号由 PiccDetect( )的CID参数项输出,其取值范围为 0~14,目前取值均为0
     * </div>
     * 
     * <div class="en">
     *            card logical channel number. The channel number is allocated internally by driver, and
     *            range is 0~14, currently it's always 0
     * </div>
     * 
     * @param ApduSend  
     * <div class="zh">
	 *			[输入] 发送到PICC卡的数据, 参考 {@link com.pax.mposapi.model.APDU_SEND}
     * </div>
     *  
     * <div class="en">
     *          [input] Data structure send to PICC card. see {@link com.pax.mposapi.model.APDU_SEND}
     * </div>
     * 
     * @return  
     * <div class="zh">
	 *			从PICC卡接收到的数据, 参考 {@link com.pax.mposapi.model.APDU_RESP}
     * </div>
     *  
     * <div class="en">
     *           Data structure received from PICC card. see {@link com.pax.mposapi.model.APDU_RESP}
     * </div>
     * 
     * @throws PiccException
     * <div class="zh">非接卡阅读器错误</div>
     * <div class="en">proximity IC card reader error</div>
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
    public APDU_RESP piccIsoCommand(byte cid, APDU_SEND ApduSend) throws PiccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
        byte[] apduSend = ApduSend.serialToBuffer();
    	byte[] req = new byte[1 + apduSend.length];
    	req[0] = cid;
    	System.arraycopy(apduSend, 0, req, 1, apduSend.length);
    	
    	APDU_RESP apduResp = new APDU_RESP();
    	byte[] resp = apduResp.serialToBuffer();
    	proto.sendRecv(Cmd.CmdType.PICC_ISOCOMMAND, req, rc, resp);
    	if (rc.code == 0) {
    		//success
    		apduResp.serialFromBuffer(resp);
    	} else {
        	throw new PiccException(rc.code);    		
    	}
    	return apduResp;
    }

    /**
     * <div class="zh">
     * 依据指定的模式,向卡片发送停机指令；或者发送停活指令；或者复位载波，并判断卡片是否已经移开感应区。
     * </div>
     * <div class="en">
     * Send stop command to card according to specific mode, or send halt
     * command, judge whether card is removed from inductive area in addition.
     * </div>
     * 
     * @param mode <br/>
     * <div class="zh">
     * 		<ul>
     *            <li>'h'or'H'{@link #PICC_REMOVE_MODE_HALT}:意为HALT，仅向卡片发送停活指令后就退出；该过 程不执行卡移开检测. <br/>
     *            <li>'r'or'R'{@link #PICC_REMOVE_MODE_REMOVE}:REMOVE， 向卡片发送停活指令，并执行卡移开检测.<br/>
     *            <li>'e'or'E'{@link #PICC_REMOVE_MODE_EMV}:符合EMV非接规范的移卡模式 复位载波，并执行卡移开检测.<br/>
     *      </ul>
     * </div>
     * 
     * <div class="en">
     * 		<ul>
     *            <li>'h'or'H'{@link #PICC_REMOVE_MODE_HALT}:HALT, quit after sending halt command to card;no card
     *            removed check during this process. <br/>
     *            <li>'r'or'R'{@link #PICC_REMOVE_MODE_REMOVE}:REMOVE, sending halt command to card, and check card removal<br/>
     *            <li>'e'or'E'{@link #PICC_REMOVE_MODE_EMV}:card removal conforms to EMV.<br/>
     *      </ul>
     * </div>
     * 
     * @param cid
     * <div class="zh">
     *            用于指定卡片逻辑通道号;该通道号由 PiccDetect( )的CID参数项输出,其取值范围为 0~14,目前取值均为0
     * </div>
     * 
     * <div class="en">
     *            card logical channel number. The channel number is allocated internally by driver, and
     *            range is 0~14, currently it's always 0
     * </div>
     * 
     * @return 
     * <div class="zh">
     * 			true : 卡已移开 <br/>
     * 			false : 卡未移开<br/>
     * </div>
     *  
     * <div class="en">
     * 			true : card removed<br/>
     * 			false : card not removed<br/>
     * </div>
     * 
     * @throws PiccException
     * <div class="zh">非接卡阅读器错误</div>
     * <div class="en">proximity IC card reader error</div>
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
    public boolean piccRemove(byte mode, byte cid) throws PiccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	req[0] = mode;
    	req[1] = cid;
    	proto.sendRecv(Cmd.CmdType.PICC_REMOVE, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    		return true;
    	} else if (rc.code == 0x06) { //CARD_NOT_REMOVED
    		return false;
    	} else {
        	throw new PiccException(rc.code);    		
    	}    	
    }

    /**
     * <div class="zh">
     * 关闭PICC模块
     * </div>
     * <div class="en">
     * Close PICC module
     * </div>
     * 
     * @throws PiccException
     * <div class="zh">非接卡阅读器错误</div>
     * <div class="en">proximity IC card reader error</div>
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
    public void piccClose() throws PiccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.PICC_CLOSE, new byte[0], rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PiccException(rc.code);    		
    	}
    }

    /**
     * <div class="zh">
     * 验证M1卡访问时读写相应块需要提交的A密码或B密码。
     * </div>
     * <div class="en">
     * Verify password A or B that should be submitted when reading/writing
     * correspoding block of M1 card
     * </div>
     * 
     * @param type
     * <div class="zh">
     *            'A'or'a':提交A密码.<br/>
     *            'B'or'b':提交B密码.<br/>
     * </div>
     * <div class="en">
     *            'A'or'a':Password A is submitted.<br/>
     *            'B'or'b':Password B is submitted.<br/>
     * </div>
     * @param blkNo
     * <div class="zh">
     *            指定要访问的 block号.
     * </div>
     * <div class="en">
     *            Specifys visiting block number.
     * </div>
     *            
     * @param pwd
     * <div class="zh">
     *            [输入] 密码
     * </div>
     * <div class="en">
     *            [input] password
     * </div>
     *            
     * @param serialNo
     * <div class="zh">
     * 			  [输入] 系列号, 通过接口 piccDetect() 获得
     * </div>
     * <div class="en">
     *            [input] serial number, get via piccDetect() interface.
     * </div>
     *            
     * @throws PiccException
     * <div class="zh">非接卡阅读器错误</div>
     * <div class="en">proximity IC card reader error</div>
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
    public void m1Authority(byte type, byte blkNo, byte[] pwd, byte[] serialNo) throws PiccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[4 + pwd.length + serialNo.length];
    	req[0] = type;
    	req[1] = blkNo;
    	req[2] = (byte)pwd.length;
    	System.arraycopy(pwd, 0, req, 3, pwd.length);
    	req[3 + pwd.length] = (byte)serialNo.length;
    	System.arraycopy(serialNo, 0, req, 3 + pwd.length + 1, serialNo.length);    	
    	
    	proto.sendRecv(Cmd.CmdType.PICC_M1_AUTH, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PiccException(rc.code);    		
    	}
    }

    /**
     * <div class="zh">
     * 读取M1卡指定块的内容(共16字节)。
     * </div>
     * <div class="en">
     * Read content of block specified by M1 card (totally 16 bytes).
     * </div>
     * 
     * @param blkNo
     * <div class="zh">
     *            指定要访问的 block号.
     * </div>
     * <div class="en">
     *            Specifys visiting block number.
     * </div>
     *
     * @return
     * <div class="zh">
     *            16字节的block内容
     * </div>
     * <div class="en">
     *            16 bytes block content.
     * </div>
     * 
     * @throws PiccException
     * <div class="zh">非接卡阅读器错误</div>
     * <div class="en">proximity IC card reader error</div>
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
    public byte[] m1ReadBlock(byte blkNo) throws PiccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
        byte[] blkValue = new byte[16];
    	byte[] req = new byte[1];
    	req[0] = blkNo;
    	
    	byte[] resp = new byte[1 + 16];	//FIXME, do we need the length byte? since it's length is fixed 16 bytes.
    	int len = proto.sendRecv(Cmd.CmdType.PICC_M1_READ_BLOCK, req, rc, resp);
    	if (rc.code == 0) {
    		//success
    		System.arraycopy(resp, 1, blkValue, 0, resp[0]);
    	} else {
        	throw new PiccException(rc.code);    		
    	}
    	
        return blkValue;
    }

    /**
     * <div class="zh">
     * 向M1卡指定块写入指定的内容(共16字节)。
     * </div>
     * <div class="en">
     * Write specified content to specified block of M1 card (totally 16 bytes).
     * </div>
     * 
     * @param blkNo
     * <div class="zh">
     *            指定要访问的 block号.
     * </div>
     * <div class="en">
     *            Specifys visiting block number.
     * </div>
     *
     * @param blkValue
     * <div class="zh">
     *            [输入] 要写入的16字节的block内容
     * </div>
     * <div class="en">
     *             [input] 16 bytes block content to write
     * </div>
     *
     * @throws PiccException
     * <div class="zh">非接卡阅读器错误</div>
     * <div class="en">proximity IC card reader error</div>
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
    public void m1WriteBlock(byte blkNo, byte[] blkValue) throws PiccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2 + blkValue.length];
    	req[0] = blkNo;
    	req[1] = (byte)blkValue.length;
    	System.arraycopy(blkValue, 0, req, 2, blkValue.length);

    	proto.sendRecv(Cmd.CmdType.PICC_M1_WRITE_BLOCK, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PiccException(rc.code);    		
    	}
    }

    /**
     * <div class="zh">
     * 对M1卡的指定数据块BlkNo进行充/减值/备份操作，将操作后的值更新到另一个指定的数据块UpdateBlkNo。
     * </div>
     * <div class="en">
     * Increase or decrease value of M1 card purse, and updates main purse or
     * backup purse finally.
     * </div>
     * 
     * @param type
     * <div class="zh">
     *            '+':充值 , '-':减值, '>':转存/备份操作
     * </div>
     * <div class="en">
     *            '+':Increase value.  '-':Decrease value. '>':Save as/ backup operation.
     * </div>
     *
     * @param blkNo
     * <div class="zh">
     *            指定要访问的 block号.
     * </div>
     * <div class="en">
     *            Specifys visiting block number.
     * </div>
     *
     * @param amount
     * <div class="zh">
     *            [输入] 要写入的待充值/减值的金额数
     * </div>
     * <div class="en">
     *             [input] amount value
     * </div>
     *
     * @param updateBlkNo
     * <div class="zh">
     *            指定操作结果最终写入到的块号
     * </div>
     * <div class="en">
     *             Specifies number of block to which operation result will
     *            write
     * </div>
     *
     * @throws PiccException
     * <div class="zh">非接卡阅读器错误</div>
     * <div class="en">proximity IC card reader error</div>
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
    public void m1Operate(byte type, byte blkNo, int amount, byte updateBlkNo) throws PiccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[7];
    	req[0] = type;
    	req[1] = blkNo;
    	Utils.int2ByteArray(amount, req, 2);
    	req[6] = updateBlkNo;

    	proto.sendRecv(Cmd.CmdType.PICC_M1_OPERATE, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PiccException(rc.code);    		
    	}
    }

    /**
     * <div class="zh">
     * 控制RF模块的4个LED灯的点亮和熄灭状态。
     * </div>
     * <div class="en">
     * Control status of 4 LED lights of RF module.
     * </div>
     * 
     * @param ucLedIndex
     * <div class="zh">
     *           灯索引，每位代表一种颜色的灯. 使用{@link #PICC_LIGHT_RED}/{@link #PICC_LIGHT_YELLOW}
     *           /{@link #PICC_LIGHT_GREEN}/{@link #PICC_LIGHT_BLUE}中的一个,或者多个的or值.
     * </div>
     * <div class="en">
     *           led index. use one of the {@link #PICC_LIGHT_RED}/{@link #PICC_LIGHT_YELLOW}
     *           /{@link #PICC_LIGHT_GREEN}/{@link #PICC_LIGHT_BLUE} or use bit-or result of 2 or more of them. 
     * </div>
     *
     * @param ucOnOff
     * <div class="zh">
     *            0: 关闭; <br/>
     *            none 0: 打开<br/>
     * </div>
     * <div class="en">
     *            0: turn off; <br/>
     *            none 0: turn on<br/>
     * </div>
     * 
     * @throws PiccException
     * <div class="zh">非接卡阅读器错误</div>
     * <div class="en">proximity IC card reader error</div>
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
    public void piccLight(byte ucLedIndex, byte ucOnOff) throws PiccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	req[0] = ucLedIndex;
    	req[1] = ucOnOff;

    	proto.sendRecv(Cmd.CmdType.PICC_LIGHT, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PiccException(rc.code);    		
    	}
    }

    /**
     * <div class="zh">
     * 初始化射频卡接口芯片为FeliCa调制编码方式。
     * </div>
     * <div class="en">
     * Initializing mifare interface chip as FeliCa modulate coding function.
     * </div>
     * 
     * @param ucRate
     * <div class="zh">
     *          设置与卡片交互的传输速率。 0-212Kbps，1-424Kbps
     * </div>
     * <div class="en">
     *          Setting transmission speed with card. 0-212Kbps,1-424Kbps. 
     * </div>
     *
     * @param ucPol
     * <div class="zh">
     *            设置FeliCa调制方式。. <br/>
     *            0 正向调制输出. <br/>
     *            1 反向调制输出.<br/>
     * </div>
     * <div class="en">
     *            Setting FeliCa modulate function. <br/>
     *            0 forward modulate output. <br/>
     *            1 reverse modulate output.<br/>
     * </div>
     * 
     * @throws PiccException
     * <div class="zh">非接卡阅读器错误</div>
     * <div class="en">proximity IC card reader error</div>
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
    public void piccInitFelica(byte ucRate, byte ucPol) throws PiccException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	req[0] = ucRate;
    	req[1] = ucPol;

    	proto.sendRecv(Cmd.CmdType.PICC_INIT_FELICA, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PiccException(rc.code);    		
    	}
    }
}
