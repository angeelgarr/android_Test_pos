package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;
import com.pax.mposapi.model.COMM_PARA;
import com.pax.mposapi.util.Utils;

/**
 * <div class="zh">
 * ModemManager ����modemͨ��,�����ڴ�modem���õĻ���
 * </div>
 * <div class="en">
 * ModemManager is used to do communcation via modem, only for model with modem
 * </div>
 *
 */
public class ModemManager {
	public static final int MODEM_STATUS_OK						=  0x00;
	public static final int MODEM_STATUS_TX_BUFFER_FULL			=  0x01;
	public static final int MODEM_STATUS_SIDE_TEL_OCCUPIED		=  0x02;
	public static final int MODEM_STATUS_NO_LINE_OR_PARALLEL_TEL_OCCUPIED		=  0x03;
	public static final int MODEM_STATUS_NO_LINE				=  0x33;
	public static final int MODEM_STATUS_SIDE_AND_PARALLEL_TEL_IDLE		=  0x83;
	public static final int MODEM_STATUS_NO_CARRIER				=  0x04;
	public static final int MODEM_STATUS_NO_ANSWER				=  0x05;
	public static final int MODEM_STATUS_STARTED_SENDING_NUMBERS		=  0x06;
	public static final int MODEM_STATUS_RX_DATA				=  0x08;
	public static final int MODEM_STATUS_RX_DATA_AND_SENDING	=  0x09;	
	public static final int MODEM_STATUS_DIALING				=  0x0a;
	public static final int MODEM_STATUS_IDLE					=  0x0b;
	public static final int MODEM_STATUS_RX_BUFFER_EMPTY		=  0x0c;
	public static final int MODEM_STATUS_LINE_BUSY				=  0x0d;
	public static final int MODEM_STATUS_NO_PORT_AVAILABLE		=  0xf0;
	public static final int MODEM_STATUS_CANCELLED				=  0xfd;
	
	public static final int PPP_ALG_PAP 						=  0x01;
	public static final int PPP_ALG_CHAP 						=  0x02;
	public static final int PPP_ALG_MSCHAPV1					=  0x04;
	public static final int PPP_ALG_MSCHAPV2					=  0x08;	
	
    private static final String TAG = "ModemManager";
    private final Proto proto;
    private Context context;
    private static ModemManager instance;
    
    private final int MAX_RX_BUFFER_LENGTH = 2048;
    
    /**
     * <div class="zh">
     * ʹ��ָ����Context�����ModemManager����
     * </div>
     * <div class="en">
     * Create a ModemManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">Ӧ�õ�ǰ��context</div>
     * <div class="en">application context currently</div>
     */    
    private ModemManager(Context context) {
    	proto = Proto.getInstance(context);
    	this.context = context;
    }

    /**
     * Create a ModemManager instance with a given Context
     * 
     * @param context
     *            application context currently
     */
    public static ModemManager getInstance(Context context) {
        if (instance == null) {
        	instance = new ModemManager(context);
        }
        return instance;
    }
        
    /**
     * <div class="zh">
     * ����첽ͨѶ���ջ�����������
     * </div>
     * <div class="en">
     * Delete data in the receiving buffer of asynchronous communication.
     * </div>
     * 
     * @throws ModemException
     * <div class="zh">modem����</div>
     * <div class="en">modem error</div>
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
    public void modemReset() throws ModemException, IOException, ProtoException, CommonException {
		RespCode rc = new RespCode();
		
		proto.sendRecv(Cmd.CmdType.MODEM_RESET, new byte[0], rc, new byte[0]);
		if (rc.code == 0) {
			//success
		} else {
			throw new ModemException(rc.code);    		
		}    	
    }
    
    /**
     * <div class="zh">
     * ����modem����,ͬʱ����;�ȴ����Ž��(mode=1)ʱ,���԰�"CANCEL"���˳�
     * </div>
     * <div class="en">
     * Set modem parameters and dial number. While dialing(mode=1), 'CANCEL' can be pressed to exit.
     * </div>
     * 
     * @param commPara  
     * <div class="zh">
	 *			[����] Modem���Ų���,MPara==nullʱ,ʹ��ȱʡ��������,ȱʡ���ŷ�ʽΪ:ͬ����1200��DTMF��CCITT��ʽ("\x00\x00\x14\x0a\x46\x08\x02\x01\x06\x00")
     * </div>
     *  
     * <div class="en">
     *          [input] Modem dialing parameter. If MPara==null, default dialing parameter will be used. Default dialing mode includes: Synchronous, 1200, DTMF and CCITT mode ("\x00\x00\x14\x0a\x46\x08\x02\x01\x06\x00")
     * </div>
     * 
     * @param telNo  
     * <div class="zh">
	 *			[����] �绰����
     * </div>
     *  
     * <div class="en">
     *          [input] Telephone number
     * </div>
     * 
     * @param mode  
     * <div class="zh">
	 *			�Ƿ��������ر�־: 0 ��������(����Ԥ����), 1�ȴ����Ž��
     * </div>
     *  
     * <div class="en">
     *          indicating whether return immediately: 0 Return immediately, 1 Wait for result of dialing
     * </div>
     * 
     * @throws ModemException
     * <div class="zh">modem����</div>
     * <div class="en">modem error</div>
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
    public void modemDial(COMM_PARA commPara, String telNo, byte mode) throws ModemException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] tel = telNo.getBytes();
    	//default cp
    	byte[] cp = new byte[] {0x00, 0x00, 0x14, 0x0a, 0x46, 0x08, 0x02, 0x01, 0x06, 0x00};
    	if (commPara != null) {
    		cp = commPara.serialToBuffer();
    	}
    	
    	byte[] req = new byte[cp.length + 1 + 1 + tel.length];
    	System.arraycopy(cp, 0, req, 0, cp.length);
    	req[cp.length] = mode;
    	req[cp.length + 1] = (byte)tel.length;
    	System.arraycopy(tel, 0, req, cp.length + 2, tel.length);
    	
    	proto.sendRecv(Cmd.CmdType.MODEM_DIAL, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new ModemException(rc.code);	
    	}
    }
    
    /**
     * <div class="zh">
     * ���MODEM���绰��·��״̬,�ú�����������
     * </div>
     * <div class="en">
     * Check modem status as well as telephone line status; function returns immediately.
     * </div>
     * 
     * @return
     * <div class="zh">modem״̬. ע����Щ�쳣������û���г�,��Ҫ���ڵ���<br/>
     * 		{@link #MODEM_STATUS_OK}: �ɹ�<br/>
     * 		{@link #MODEM_STATUS_TX_BUFFER_FULL}: ���ͻ�������<br/>
     * 		{@link #MODEM_STATUS_SIDE_TEL_OCCUPIED}: ���õ绰ռ��<br/>
     * 		{@link #MODEM_STATUS_NO_LINE_OR_PARALLEL_TEL_OCCUPIED}: �绰��δ�Ӻû��ߵ绰ռ��[�ߵ�ѹ��Ϊ0,������]<br/>
     * 		{@link #MODEM_STATUS_NO_LINE}: �绰��δ��[�ߵ�ѹΪ0]<br/>
     * 		{@link #MODEM_STATUS_SIDE_AND_PARALLEL_TEL_IDLE}: ���õ绰�����ߵ绰������(�����ڷ���ת�˹�������ʽ)<br/>
     * 		{@link #MODEM_STATUS_NO_CARRIER}: ��·�ز���ʧ(ͬ������ʧ��)<br/>
     * 		{@link #MODEM_STATUS_NO_ANSWER}: ������Ӧ��<br/>
     * 		{@link #MODEM_STATUS_STARTED_SENDING_NUMBERS}: �ѿ�ʼ����(�����ڷ���ת�˹�������ʽ)<br/>
     * 		{@link #MODEM_STATUS_RX_DATA}: ͬ��ͨ��ʱ, ���ջ������ǿ�(���յ�Զ������)<br/>
     * 		{@link #MODEM_STATUS_RX_DATA_AND_SENDING}: ͬ��ͨ��ʱ, ���ջ������ǿ�(���յ�Զ������)�ҷ��ͻ��������ڷ�������<br/>
     * 		{@link #MODEM_STATUS_DIALING}: ���ڲ���<br/>
     * 		{@link #MODEM_STATUS_IDLE}: �����һ��Ϳ���<br/>
     * 		{@link #MODEM_STATUS_RX_BUFFER_EMPTY}: �����������󱻾ܾ�(���ջ�����Ϊ��)<br/>
     * 		{@link #MODEM_STATUS_LINE_BUSY}: ������·æ<br/>
     * 		{@link #MODEM_STATUS_NO_PORT_AVAILABLE}: (��CPU)���޿��õ�ͨѶ��(������̬����˿���ȫ������ͨѶ��ʹ��)<br/>
     * 		{@link #MODEM_STATUS_CANCELLED}: CANCEL������(Modem����ֹ���Ų���)<br/>
     * </div>
     * <div class="en">modem status. Note that some of the error codes are not listed here, they're mainly for debugging purpose<br/>
     * 		{@link #MODEM_STATUS_OK}: Success<br/>
     * 		{@link #MODEM_STATUS_TX_BUFFER_FULL}: Sending buffer full.<br/>
     * 		{@link #MODEM_STATUS_SIDE_TEL_OCCUPIED}: Side telephtone has been occupied.<br/>
     * 		{@link #MODEM_STATUS_NO_LINE_OR_PARALLEL_TEL_OCCUPIED}: Telephone line is not properly connected, or paralleled line is occupied (Line voltage is not 0, but too low).<br/>
     * 		{@link #MODEM_STATUS_NO_LINE}: Telephone line is not connected (Line voltage is 0).<br/>
     * 		{@link #MODEM_STATUS_SIDE_AND_PARALLEL_TEL_IDLE}: Both side telephone and paralleled telephone are not busy (only for from automatically sending mode to manually receiving mode).<br/>
     * 		{@link #MODEM_STATUS_NO_CARRIER}: no carrier<br/>
     * 		{@link #MODEM_STATUS_NO_ANSWER}: no answer<br/>
     * 		{@link #MODEM_STATUS_STARTED_SENDING_NUMBERS}: Start to send numbers(only for from automatically sending mode to manually receiving mode).<br/>
     * 		{@link #MODEM_STATUS_RX_DATA}: Receive buffer is not empty(received remote data). Sync mode only<br/>
     * 		{@link #MODEM_STATUS_RX_DATA_AND_SENDING}: Receive buffer is not empty(received remote data),and the is sending data. Sync mode only<br/>
     * 		{@link #MODEM_STATUS_DIALING}: Dialing<br/>
     * 		{@link #MODEM_STATUS_IDLE}: Normal hangup and idle.<br/>
     * 		{@link #MODEM_STATUS_RX_BUFFER_EMPTY}: Request of receiving data is denied (receiving buffer is null)<br/>
     * 		{@link #MODEM_STATUS_LINE_BUSY}: Line busy.<br/>
     * 		{@link #MODEM_STATUS_NO_PORT_AVAILABLE}: (The main CPU) There is no more communication port available (Two dynamically allocated ports are being used by other communication ports).)<br/>
     * 		{@link #MODEM_STATUS_CANCELLED}: Press CANCEL key (Modem will stop dialing)<br/>
     * </div>
     * 
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
    public int modemCheck() throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	proto.sendRecv(Cmd.CmdType.MODEM_CHECK, new byte[0], rc, new byte[0]);
    	return rc.code;
    }    
    
    /**
     * <div class="zh">
     * ͨ��MODEM�������ݰ�
     * </div>
     * <div class="en">
     * send data via modem
     * </div>
     * 
     * @param data  
     * <div class="zh">
	 *			[����] Ҫ���͵�����, ÿ�����ɷ���2048�ֽ�
     * </div>
     *  
     * <div class="en">
     *          [input] data to send, maximun 2048 bytes each time
     * </div>
     * 
     * @throws ModemException
     * <div class="zh">modem����</div>
     * <div class="en">modem error</div>
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
    public void modemTxd(byte[] data) throws ModemException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2 + data.length];
    	Utils.short2ByteArray((short)data.length, req, 0);
    	System.arraycopy(data, 0, req, 2, data.length);
    	
    	proto.sendRecv(Cmd.CmdType.MODEM_TXD, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new ModemException(rc.code);	
    	}
    }

    /**
     * <div class="zh">
     * ����modem �������ݰ�<br/>
     * ע��: ����׳���ModemException�쳣exceptionCodeΪMODEM_ERR_RX_BUFFER_EMPTY�����������
     * </div>
     * <div class="en">
     * receive data from modem<br/>
     * NOTE: if a ModemException exception is throwed with exceptionCode MODEM_ERR_RX_BUFFER_EMPTY, 
     * then you should continue to receive
     * </div>
     * 
     * @return  
     * <div class="zh">
	 *			���յ�������, ÿ�����2048�ֽ�
     * </div>
     *  
     * <div class="en">
     *          data received, maximum 2048 bytes each time
     * </div>
     * 
     * @throws ModemException
     * <div class="zh">modem����</div>
     * <div class="en">modem error</div>
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
    public byte[] modemRxd() throws ModemException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] resp = new byte[2 + MAX_RX_BUFFER_LENGTH]; 
    	
    	proto.sendRecv(Cmd.CmdType.MODEM_RXD, new byte[0], rc, resp);
    	if (rc.code == 0) {
    		//success
    		int len = Utils.shortFromByteArray(resp, 0);
    		byte[] recv = new byte[len];
    		System.arraycopy(resp, 2, recv, 0, len);
    		return recv;
    	} else {
        	throw new ModemException(rc.code);	
    	}
    }

    /**
     * <div class="zh">
     * �첽ͨ��ʱ,���շ�������,һ��ֻ�ܽ���һ���ֽ�;�ú�����������
     * </div>
     * <div class="en">
     * Get one byte of return data in asynchronous communication. Function returns immediately.
     * </div>
     * 
     * @return  
     * <div class="zh">
	 *			���յ���1�ֽ�����
     * </div>
     *  
     * <div class="en">
     *          data received
     * </div>
     * 
     * @throws ModemException
     * <div class="zh">modem����</div>
     * <div class="en">modem error</div>
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
    public byte modemAsyncGet() throws ModemException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] resp = new byte[1]; 
    	
    	proto.sendRecv(Cmd.CmdType.MODEM_ASYNC_GET, new byte[0], rc, resp);
    	if (rc.code == 0) {
    		//success
    		return resp[0];
    	} else {
        	throw new ModemException(rc.code);	
    	}
    }
            
	/**
	* <div class="zh">
	* MODEM�һ�����ֹMODEM�Ĳ��Ų���<br/>
	* �һ������������²���,�����������Զ��ȵȺ�3���ſ�ʼ�ٲ���,�Ա㽻������ɹ��߶������������Ͳ�����
	* </div>
	* <div class="en">
	* Hang up MODEM or terminate MODEM dialing<br/>
	* If dialing again right after hanging up, driver will wait and start redial after 3 seconds, in order to allow PABX finishing hangup and transmitting dialing tone
	* </div>
	* 
     * @throws ModemException
     * <div class="zh">modem����</div>
     * <div class="en">modem error</div>
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
    public void modemOnHook() throws ModemException, IOException, ProtoException, CommonException {
		RespCode rc = new RespCode();
		
		proto.sendRecv(Cmd.CmdType.MODEM_ONHOOK, new byte[0], rc, new byte[0]);
		if (rc.code == 0) {
			//success
		} else {
			throw new ModemException(rc.code);    		
		}    	
    }

	/**
	* <div class="zh">
	* MODEM�һ�����ֹMODEM�Ĳ��Ų���<br/>
	* �һ������������²���,�����������Զ��ȵȺ�3���ſ�ʼ�ٲ���,�Ա㽻������ɹ��߶������������Ͳ�����
	* </div>
	* <div class="en">
	* Hang up MODEM or terminate MODEM dialing<br/>
	* If dialing again right after hanging up, driver will wait and start redial after 3 seconds, in order to allow PABX finishing hangup and transmitting dialing tone
	* </div>
	* 
	* @throws ModemException
	* <div class="zh">modem����</div>
	* <div class="en">modem error</div>
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
    public void modemHangOff() throws ModemException, IOException, ProtoException, CommonException {
		RespCode rc = new RespCode();
		
		proto.sendRecv(Cmd.CmdType.MODEM_HANGOFF, new byte[0], rc, new byte[0]);
		if (rc.code == 0) {
			//success
		} else {
			throw new ModemException(rc.code);    		
		}    	
    }
    
	/**
	* <div class="zh">
	* ���������µ�AT��������,������MODEM �Ĳ�����Ϊ
	* </div>
	* <div class="en">
	* Insert new AT control command to control MODEM dialing
	* </div>
	* 
	* @param cmd  
	* <div class="zh">
	*		AT����
	* </div>
	* <div class="en">
	*       AT command
	* </div>
	* 
	* @param timeout10Ms  
	* <div class="zh">
	*		�ȴ���Ӧʱ��, ��λΪ10ms
	* </div>
	* <div class="en">
	*       Waiting time for response, in units 10ms
	* </div>
	* 
	* @return
	* <div class="zh">
	*		��Ӧ����
	* </div>
	* <div class="en">
	*       data responded
	* </div>
	* 
	* @throws ModemException
	* <div class="zh">modem����</div>
	* <div class="en">modem error</div>
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
    public byte[] modemExCommand(String cmd, int timeout10Ms) throws ModemException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] cmdBytes = cmd.getBytes(); 
    	byte[] req = new byte[1 + cmdBytes.length + 2];
    	req[0] = (byte)cmdBytes.length;
    	System.arraycopy(cmdBytes, 0, req, 1, cmdBytes.length);
    	Utils.short2ByteArray((short)timeout10Ms, req, 1 + cmdBytes.length);
    	
    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += timeout10Ms * 10;
    	
    	byte[] resp = new byte[2048];
    	try {
	    	proto.sendRecv(Cmd.CmdType.MODEM_EX_COMMAND, req, rc, resp);
	    	if (rc.code == 0) {
	    		//success
	    		int len = Utils.shortFromByteArray(resp, 0);
	    		byte[] recv = new byte[len];
	    		System.arraycopy(resp, 2, recv, 0, len);
	    		return recv;
	    	} else {
	        	throw new ModemException(rc.code);	
	    	} 
    	}finally {
    		cfg.receiveTimeout = savedRecvTimeout;    		
    	}
    }
    
	/**
	* <div class="zh">
	* ����PPP��·
	* </div>
	* <div class="en">
	* Activate PPP Link
	* </div>
	* 
	* @param name  
	* <div class="zh">
	*		��֤ʱ������û���,���Ȳ��ܳ���99���ַ�
	* </div>
	* <div class="en">
	*       User name, which cannot exceed 99 characters
	* </div>
	* 
	* @param passwd  
	* <div class="zh">
	*		��֤ʱ���������,���Ȳ��ܳ���99���ַ�
	* </div>
	* <div class="en">
	*       Password, which cannot exceed 99 characters
	* </div>
	* 
	* @param auth  
	* <div class="zh">
	*		��֤ʱ���õ��㷨,Ŀǰ֧�ֵ��㷨��:<br/>
	*		{@link #PPP_ALG_PAP} PAP��֤�㷨<br/>
	*		{@link #PPP_ALG_CHAP} CHAP��֤�㷨<br/>
	*		{@link #PPP_ALG_MSCHAPV1} MSCHAPV1��֤�㷨<br/>
	*		{@link #PPP_ALG_MSCHAPV2} MSCHAPV2��֤�㷨��֤�㷨<br/>
	*		����Ҫ����һ��,Ҳ���Բ��ö���;���ö�����֤�㷨ʱ,ÿ���㷨���(+)�����(|)����,��{@link #PPP_ALG_PAP} | {@link #PPP_ALG_CHAP}
	* </div>
	* <div class="en">
	*       Authentication algorithms supporting: <br/>
	*		{@link #PPP_ALG_PAP} PAP authentication algorithm<br/>
	*		{@link #PPP_ALG_CHAP} CHAP authentication algorithm<br/>
	*		{@link #PPP_ALG_MSCHAPV1} MSCHAPV1 authentication algorithm<br/>
	*		{@link #PPP_ALG_MSCHAPV2} MSCHAPV2 authentication algorithm<br/>
	*       At least one type of authentication algorithm has to be chose; more than one authentication algorithm will also be allowed by using (+) or (|), for example, {@link #PPP_ALG_PAP} | {@link #PPP_ALG_CHAP}
	* </div>
	* 
	* @param timeout  
	* <div class="zh">
	*		�ȴ���ʱʱ��, ��λΪ����; <br/>
	*		timeout<0����ʾһֱ�ȴ�������PPP��·�ɹ���ʧ��<br/>
	*		timeout=0����ʾ���ȴ�,���̷���<br/>
	*		timeout>0����ʾ�ȴ�����ʱ��
	* </div>
	* <div class="en">
	*       Timeout value, in ms;<br/>
	*       timeout<0 represents waiting until activating PPP link successful or failed <br/>
	*       timeout=0 represents no waiting; return immediately <br/>
	*       timeout>0 represents waiting for specified period of time
	* </div>
	* 
	* @throws NetException
	* <div class="zh">�������</div>
	* <div class="en">network error</div>
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
    public void pppLogin(String name, String passwd, int auth, int timeout) throws NetException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] nameBytes = name.getBytes();
    	byte[] passwdBytes = passwd.getBytes();
    	byte[] req = new byte[1 + nameBytes.length + 1 + passwdBytes.length + 4 + 4];
    	req[0] = (byte)nameBytes.length;
    	System.arraycopy(nameBytes, 0, req, 1, nameBytes.length);
    	req[1 + nameBytes.length] = (byte)passwdBytes.length;
    	System.arraycopy(passwdBytes, 0, req, 1 + nameBytes.length + 1, passwdBytes.length);
    	Utils.int2ByteArray(auth, req, 1 + nameBytes.length + 1 + passwdBytes.length);
    	Utils.int2ByteArray(timeout, req, 1 + nameBytes.length + 1 + passwdBytes.length + 4);
    	
    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += timeout;
    	
    	try {
	    	proto.sendRecv(Cmd.CmdType.MODEM_PPP_LOGIN, req, rc, new byte[0]);
	    	if (rc.code == 0) {
	    		//success
	    	} else {
	        	throw new NetException(rc.code);	
	    	} 
    	}finally {
    		cfg.receiveTimeout = savedRecvTimeout;    		
    	}
    }
     
    /**
     * <div class="zh">
     * �ر�PPP��·
     * </div>
     * <div class="en">
     * Close PPP link
     * </div>
     * 
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
    public void pppLogout() throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	proto.sendRecv(Cmd.CmdType.MODEM_PPP_LOGOUT, new byte[0], rc, new byte[0]);
    }
    
    /**
     * <div class="zh">
     * ���PPP��·���
     * </div>
     * <div class="en">
     * Check PPP link status
     * </div>
     * 
     * @return
     * <div class="zh">PPP��·״̬<br/>
     * 	0: ��·�ѳɹ�����<br/>
     *  1: ������<br/>
     *  <0: ��·����ʧ�ܻ���·û������
     * </div>
     * <div class="en">PPP link status<br/>
     * 	0: link is successfully established<br/>
     *  1: link is being established<br/>
     *  <0: link failed or link is not activated
     * </div>
     * 
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
    public int pppCheck() throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	proto.sendRecv(Cmd.CmdType.MODEM_PPP_CHECK, new byte[0], rc, new byte[0]);
    	return rc.code;
    }
}
