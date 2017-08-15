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
 * ModemManager 用于modem通信,仅用于带modem配置的机型
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
     * 使用指定的Context构造出ModemManager对象
     * </div>
     * <div class="en">
     * Create a ModemManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">应用当前的context</div>
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
     * 清除异步通讯接收缓冲区的数据
     * </div>
     * <div class="en">
     * Delete data in the receiving buffer of asynchronous communication.
     * </div>
     * 
     * @throws ModemException
     * <div class="zh">modem错误</div>
     * <div class="en">modem error</div>
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
     * 设置modem参数,同时拨号;等待拨号结果(mode=1)时,可以按"CANCEL"键退出
     * </div>
     * <div class="en">
     * Set modem parameters and dial number. While dialing(mode=1), 'CANCEL' can be pressed to exit.
     * </div>
     * 
     * @param commPara  
     * <div class="zh">
	 *			[输入] Modem拨号参数,MPara==null时,使用缺省参数拨号,缺省拨号方式为:同步、1200、DTMF、CCITT方式("\x00\x00\x14\x0a\x46\x08\x02\x01\x06\x00")
     * </div>
     *  
     * <div class="en">
     *          [input] Modem dialing parameter. If MPara==null, default dialing parameter will be used. Default dialing mode includes: Synchronous, 1200, DTMF and CCITT mode ("\x00\x00\x14\x0a\x46\x08\x02\x01\x06\x00")
     * </div>
     * 
     * @param telNo  
     * <div class="zh">
	 *			[输入] 电话号码
     * </div>
     *  
     * <div class="en">
     *          [input] Telephone number
     * </div>
     * 
     * @param mode  
     * <div class="zh">
	 *			是否立即返回标志: 0 立即返回(用于预拨号), 1等待拨号结果
     * </div>
     *  
     * <div class="en">
     *          indicating whether return immediately: 0 Return immediately, 1 Wait for result of dialing
     * </div>
     * 
     * @throws ModemException
     * <div class="zh">modem错误</div>
     * <div class="en">modem error</div>
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
     * 检测MODEM及电话线路的状态,该函数立即返回
     * </div>
     * <div class="en">
     * Check modem status as well as telephone line status; function returns immediately.
     * </div>
     * 
     * @return
     * <div class="zh">modem状态. 注意有些异常错误码没有列出,主要用于调试<br/>
     * 		{@link #MODEM_STATUS_OK}: 成功<br/>
     * 		{@link #MODEM_STATUS_TX_BUFFER_FULL}: 发送缓冲区满<br/>
     * 		{@link #MODEM_STATUS_SIDE_TEL_OCCUPIED}: 旁置电话占用<br/>
     * 		{@link #MODEM_STATUS_NO_LINE_OR_PARALLEL_TEL_OCCUPIED}: 电话线未接好或并线电话占用[线电压不为0,但过低]<br/>
     * 		{@link #MODEM_STATUS_NO_LINE}: 电话线未接[线电压为0]<br/>
     * 		{@link #MODEM_STATUS_SIDE_AND_PARALLEL_TEL_IDLE}: 旁置电话、并线电话均空闲(仅用于发号转人工接听方式)<br/>
     * 		{@link #MODEM_STATUS_NO_CARRIER}: 线路载波丢失(同步建链失败)<br/>
     * 		{@link #MODEM_STATUS_NO_ANSWER}: 拨号无应答<br/>
     * 		{@link #MODEM_STATUS_STARTED_SENDING_NUMBERS}: 已开始发号(仅用于发号转人工接听方式)<br/>
     * 		{@link #MODEM_STATUS_RX_DATA}: 同步通信时, 接收缓冲区非空(接收到远端数据)<br/>
     * 		{@link #MODEM_STATUS_RX_DATA_AND_SENDING}: 同步通信时, 接收缓冲区非空(接收到远端数据)且发送缓冲区正在发送数据<br/>
     * 		{@link #MODEM_STATUS_DIALING}: 正在拨号<br/>
     * 		{@link #MODEM_STATUS_IDLE}: 正常挂机和空闲<br/>
     * 		{@link #MODEM_STATUS_RX_BUFFER_EMPTY}: 接收数据请求被拒绝(接收缓冲区为空)<br/>
     * 		{@link #MODEM_STATUS_LINE_BUSY}: 被叫线路忙<br/>
     * 		{@link #MODEM_STATUS_NO_PORT_AVAILABLE}: (主CPU)已无可用的通讯口(两个动态分配端口正全被其它通讯口使用)<br/>
     * 		{@link #MODEM_STATUS_CANCELLED}: CANCEL键按下(Modem将终止拨号操作)<br/>
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
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */        
    public int modemCheck() throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	proto.sendRecv(Cmd.CmdType.MODEM_CHECK, new byte[0], rc, new byte[0]);
    	return rc.code;
    }    
    
    /**
     * <div class="zh">
     * 通过MODEM发送数据包
     * </div>
     * <div class="en">
     * send data via modem
     * </div>
     * 
     * @param data  
     * <div class="zh">
	 *			[输入] 要发送的数据, 每次最大可发送2048字节
     * </div>
     *  
     * <div class="en">
     *          [input] data to send, maximun 2048 bytes each time
     * </div>
     * 
     * @throws ModemException
     * <div class="zh">modem错误</div>
     * <div class="en">modem error</div>
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
     * 接收modem 返回数据包<br/>
     * 注意: 如果抛出的ModemException异常exceptionCode为MODEM_ERR_RX_BUFFER_EMPTY则请继续接收
     * </div>
     * <div class="en">
     * receive data from modem<br/>
     * NOTE: if a ModemException exception is throwed with exceptionCode MODEM_ERR_RX_BUFFER_EMPTY, 
     * then you should continue to receive
     * </div>
     * 
     * @return  
     * <div class="zh">
	 *			接收到的数据, 每次最大2048字节
     * </div>
     *  
     * <div class="en">
     *          data received, maximum 2048 bytes each time
     * </div>
     * 
     * @throws ModemException
     * <div class="zh">modem错误</div>
     * <div class="en">modem error</div>
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
     * 异步通信时,接收返回数据,一次只能接收一个字节;该函数立即返回
     * </div>
     * <div class="en">
     * Get one byte of return data in asynchronous communication. Function returns immediately.
     * </div>
     * 
     * @return  
     * <div class="zh">
	 *			接收到的1字节数据
     * </div>
     *  
     * <div class="en">
     *          data received
     * </div>
     * 
     * @throws ModemException
     * <div class="zh">modem错误</div>
     * <div class="en">modem error</div>
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
	* MODEM挂机或终止MODEM的拨号操作<br/>
	* 挂机后若马上重新拨号,则驱动程序自动先等候3秒后才开始再拨号,以便交换机完成挂线动作和重新输送拨号音
	* </div>
	* <div class="en">
	* Hang up MODEM or terminate MODEM dialing<br/>
	* If dialing again right after hanging up, driver will wait and start redial after 3 seconds, in order to allow PABX finishing hangup and transmitting dialing tone
	* </div>
	* 
     * @throws ModemException
     * <div class="zh">modem错误</div>
     * <div class="en">modem error</div>
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
	* MODEM挂机或终止MODEM的拨号操作<br/>
	* 挂机后若马上重新拨号,则驱动程序自动先等候3秒后才开始再拨号,以便交换机完成挂线动作和重新输送拨号音
	* </div>
	* <div class="en">
	* Hang up MODEM or terminate MODEM dialing<br/>
	* If dialing again right after hanging up, driver will wait and start redial after 3 seconds, in order to allow PABX finishing hangup and transmitting dialing tone
	* </div>
	* 
	* @throws ModemException
	* <div class="zh">modem错误</div>
	* <div class="en">modem error</div>
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
	* 用于输入新的AT控制命令,来控制MODEM 的拨号行为
	* </div>
	* <div class="en">
	* Insert new AT control command to control MODEM dialing
	* </div>
	* 
	* @param cmd  
	* <div class="zh">
	*		AT命令
	* </div>
	* <div class="en">
	*       AT command
	* </div>
	* 
	* @param timeout10Ms  
	* <div class="zh">
	*		等待响应时间, 单位为10ms
	* </div>
	* <div class="en">
	*       Waiting time for response, in units 10ms
	* </div>
	* 
	* @return
	* <div class="zh">
	*		响应数据
	* </div>
	* <div class="en">
	*       data responded
	* </div>
	* 
	* @throws ModemException
	* <div class="zh">modem错误</div>
	* <div class="en">modem error</div>
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
	* 启用PPP链路
	* </div>
	* <div class="en">
	* Activate PPP Link
	* </div>
	* 
	* @param name  
	* <div class="zh">
	*		认证时所需的用户名,长度不能超过99个字符
	* </div>
	* <div class="en">
	*       User name, which cannot exceed 99 characters
	* </div>
	* 
	* @param passwd  
	* <div class="zh">
	*		认证时所需的密码,长度不能超过99个字符
	* </div>
	* <div class="en">
	*       Password, which cannot exceed 99 characters
	* </div>
	* 
	* @param auth  
	* <div class="zh">
	*		认证时采用的算法,目前支持的算法有:<br/>
	*		{@link #PPP_ALG_PAP} PAP认证算法<br/>
	*		{@link #PPP_ALG_CHAP} CHAP认证算法<br/>
	*		{@link #PPP_ALG_MSCHAPV1} MSCHAPV1认证算法<br/>
	*		{@link #PPP_ALG_MSCHAPV2} MSCHAPV2认证算法认证算法<br/>
	*		至少要采用一种,也可以采用多种;采用多种认证算法时,每个算法相加(+)或相或(|)即可,如{@link #PPP_ALG_PAP} | {@link #PPP_ALG_CHAP}
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
	*		等待超时时间, 单位为毫秒; <br/>
	*		timeout<0：表示一直等待到启用PPP链路成功或失败<br/>
	*		timeout=0：表示不等待,立刻返回<br/>
	*		timeout>0：表示等待有限时间
	* </div>
	* <div class="en">
	*       Timeout value, in ms;<br/>
	*       timeout<0 represents waiting until activating PPP link successful or failed <br/>
	*       timeout=0 represents no waiting; return immediately <br/>
	*       timeout>0 represents waiting for specified period of time
	* </div>
	* 
	* @throws NetException
	* <div class="zh">网络错误</div>
	* <div class="en">network error</div>
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
     * 关闭PPP链路
     * </div>
     * <div class="en">
     * Close PPP link
     * </div>
     * 
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
    public void pppLogout() throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	proto.sendRecv(Cmd.CmdType.MODEM_PPP_LOGOUT, new byte[0], rc, new byte[0]);
    }
    
    /**
     * <div class="zh">
     * 检查PPP链路情况
     * </div>
     * <div class="en">
     * Check PPP link status
     * </div>
     * 
     * @return
     * <div class="zh">PPP链路状态<br/>
     * 	0: 链路已成功建立<br/>
     *  1: 处理当中<br/>
     *  <0: 链路建立失败或链路没有启用
     * </div>
     * <div class="en">PPP link status<br/>
     * 	0: link is successfully established<br/>
     *  1: link is being established<br/>
     *  <0: link failed or link is not activated
     * </div>
     * 
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
    public int pppCheck() throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	proto.sendRecv(Cmd.CmdType.MODEM_PPP_CHECK, new byte[0], rc, new byte[0]);
    	return rc.code;
    }
}
