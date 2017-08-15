package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;
import com.pax.mposapi.util.Utils;

/**
 * <div class="zh">
 * PortManager 用于管理通信端口, 目前只支持 PINPAD串口, 仅用于带相应端口配置的机型
 * </div>
 * <div class="en">
 * PortManager is used to manage the communication ports, currently only support PINPAD RS232 port, only for models with related port
 *  
 * </div>
 *
 */
public class PortManager {
    private static final String TAG = "PortManager";
    private final Proto proto;
    private Context context;
    private static PortManager instance;
    
    /**
     * <div class="zh">
     * 使用指定的Context构造出PortManager对象
     * </div>
     * <div class="en">
     * Create a PortManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">应用当前的context</div>
     * <div class="en">application context currently</div>
     */    
    private PortManager(Context context) {
    	proto = Proto.getInstance(context);
    	this.context = context;
    }

    /**
     * Create a PortManager instance with a given Context
     * 
     * @param context
     *            application context currently
     */
    public static PortManager getInstance(Context context) {
        if (instance == null) {
        	instance = new PortManager(context);
        }
        return instance;
    }
            
    /**
     * <div class="zh">
     * 打开指定的通讯口,并设置通讯参数
     * </div>
     * <div class="en">
     * open specified port, and set related parameters
     * </div>
     * 
     * @param channel
     * <div class="zh">
     *            端口号,目前只支持PINPAD口即channel 0. 
     * </div>
     * <div class="en">
     *            port number, currently only support PINPAD port(i.e. channel 0)
     * </div>
     * 
     * @param attr
     * <div class="zh">
     *            串口属性,通讯速率和格式串,如:"9600,8,n,1"表示波特率 9600bps,8个数据位,无校验,1个停止位; 字符串中间用','隔开<br/>
     *            		波特率:600,1200,2400,4800,9600,14400,19200,28800,38400,57600,115200,230400之一;<br/>
     *            		数据位:7或 8<br/>
     *            		校验方式:o-奇校验,e-偶校验,n-无校验<br/>
     *            		停止位:1或2
     * </div>
     * <div class="en">
     *            port attributes, in format "rate,data bits,parity,stop bit". e.g. "9600,8,n,1"<br/>
     *            		rate:one of 600,1200,2400,4800,9600,14400,19200,28800,38400,57600,115200,230400;<br/>
     *            		data bits:7or8;<br/>
     *            		parity:o-Odd,e-Even,n-No;<br/>
     *            		stop bits:1or2
     * </div>
     * 
     * @throws PortException
     * <div class="zh">端口错误</div>
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
    public void portOpen(byte channel, String attr) throws PortException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1 + 1 + attr.length()];
    	req[0] = (byte)channel;
    	req[1] = (byte)attr.length();
    	System.arraycopy(attr.getBytes(), 0, req, 2, attr.length());
    	
    	proto.sendRecv(Cmd.CmdType.PORT_OPEN, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PortException(rc.code);    		
    	}
    }
    
    /**
     * <div class="zh">
     * 关闭指定的通讯口
     * </div>
     * <div class="en">
     * close specified port
     * </div>
     * 
     * @param channel
     * <div class="zh">
     *            端口号, 目前只支持PINPAD口即channel 0. 
     * </div>
     * <div class="en">
     *            port number, currently only support PINPAD port(i.e. channel 0)
     * </div>
     * 
     * @throws PortException
     * <div class="zh">端口错误</div>
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
    public void portClose(byte channel) throws PortException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = (byte)channel;
    	
    	proto.sendRecv(Cmd.CmdType.PORT_CLOSE, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PortException(rc.code);    		
    	}    	
    }
    
    /**
     * <div class="zh">
     * 复位通讯口,该函数将清除串口接收缓冲区中的所有数据
     * </div>
     * <div class="en">
     * reset port, this will clear tx buffer.
     * </div>
     * 
     * @param channel
     * <div class="zh">
     *            端口号, 目前只支持PINPAD口即channel 0. 
     * </div>
     * <div class="en">
     *            port number, currently only support PINPAD port(i.e. channel 0)
     * </div>
     * 
     * @throws PortException
     * <div class="zh">端口错误</div>
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
    public void portReset(byte channel) throws PortException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = (byte)channel;
    	
    	proto.sendRecv(Cmd.CmdType.PORT_RESET, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PortException(rc.code);    		
    	}    	
    }
    
    /**
     * <div class="zh">
     * 使用指定的通讯口发送若干字节的数据
     * </div>
     * <div class="en">
     * send data via specified port
     * </div>
     * 
     * @param channel
     * <div class="zh">
     *            端口号, 目前只支持PINPAD口即channel 0. 
     * </div>
     * <div class="en">
     *            port number, currently only support PINPAD port(i.e. channel 0)
     * </div>
     * 
     * @param buffer
     * <div class="zh">
     *            [输入]待发送的数据 
     * </div>
     * <div class="en">
     *            [input]data to send
     * </div>
     * 
     * @throws PortException
     * <div class="zh">端口错误</div>
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
    public void portSends(byte channel, byte[] buffer) throws PortException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1 + 4 + buffer.length];
    	req[0] = (byte)channel;
    	Utils.int2ByteArray(buffer.length, req, 1);
    	System.arraycopy(buffer, 0, req, 5, buffer.length);

    	proto.sendRecv(Cmd.CmdType.PORT_SEND, req, rc, new byte[0]);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new PortException(rc.code);    		
    	}    	
    }
    
    /**
     * <div class="zh">
     * 在给定的时限内,最多接收期望长度的数据 <br/>
     * 注意接收超时不会抛出异常,而是返回数据长度为0
     * </div>
     * <div class="en">
     * receive data via specified port within timeout, maximum expLen bytes. 
     * Note that receive timeout will not throw exception, instead, a byte array with 0 byte length is returned. 
     * </div>
     * 
     * @param channel
     * <div class="zh">
     *            端口号, 目前只支持PINPAD口即channel 0. 
     * </div>
     * <div class="en">
     *            port number, currently only support PINPAD port(i.e. channel 0)
     * </div>
     * 
     * @param expLen
     * <div class="zh">
     *            期望接收的数据字节数 
     * </div>
     * <div class="en">
     *            data length expected
     * </div>
     * 
     * @param timeout
     * <div class="zh">
     *            接收超时时长(单位毫秒). 为0时,则有数据便接收后立即退出,无数据也立即退出，均返回收到的字节数 
     * </div>
     * <div class="en">
     *            timeout to receive(in ms). if equals 0, read available data and return immediately.
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		接收到的数据
     * </div>
     * <div class="en">
     * 		data received
     * </div>
     * 
     * @throws PortException
     * <div class="zh">端口错误</div>
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
    public byte[] portRecvs(byte channel, int expLen, int timeout) throws PortException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[7];
    	req[0] = (byte)channel;
    	Utils.int2ByteArray(expLen, req, 1);
    	Utils.short2ByteArray((short)timeout, req, 5);

    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += timeout;

    	byte[] recv = new byte[4 + expLen];
    	try {
	    	proto.sendRecv(Cmd.CmdType.PORT_RECV, req, rc, recv);
	    	if (rc.code == 0) {
	    		//success
	    		int retLen = Utils.intFromByteArray(recv, 0);
	    		byte[] ret = new byte[retLen];
	    		System.arraycopy(recv, 4, ret, 0, retLen);
	    		return ret;
	    	} else {
	        	throw new PortException(rc.code);    		
	    	}
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    }
}
