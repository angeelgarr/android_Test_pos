package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;
import com.pax.mposapi.util.Utils;

/**
 * <div class="zh">
 * PortManager ���ڹ���ͨ�Ŷ˿�, Ŀǰֻ֧�� PINPAD����, �����ڴ���Ӧ�˿����õĻ���
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
     * ʹ��ָ����Context�����PortManager����
     * </div>
     * <div class="en">
     * Create a PortManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">Ӧ�õ�ǰ��context</div>
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
     * ��ָ����ͨѶ��,������ͨѶ����
     * </div>
     * <div class="en">
     * open specified port, and set related parameters
     * </div>
     * 
     * @param channel
     * <div class="zh">
     *            �˿ں�,Ŀǰֻ֧��PINPAD�ڼ�channel 0. 
     * </div>
     * <div class="en">
     *            port number, currently only support PINPAD port(i.e. channel 0)
     * </div>
     * 
     * @param attr
     * <div class="zh">
     *            ��������,ͨѶ���ʺ͸�ʽ��,��:"9600,8,n,1"��ʾ������ 9600bps,8������λ,��У��,1��ֹͣλ; �ַ����м���','����<br/>
     *            		������:600,1200,2400,4800,9600,14400,19200,28800,38400,57600,115200,230400֮һ;<br/>
     *            		����λ:7�� 8<br/>
     *            		У�鷽ʽ:o-��У��,e-żУ��,n-��У��<br/>
     *            		ֹͣλ:1��2
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
     * <div class="zh">�˿ڴ���</div>
     * <div class="en">proximity IC card reader error</div>
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
     * �ر�ָ����ͨѶ��
     * </div>
     * <div class="en">
     * close specified port
     * </div>
     * 
     * @param channel
     * <div class="zh">
     *            �˿ں�, Ŀǰֻ֧��PINPAD�ڼ�channel 0. 
     * </div>
     * <div class="en">
     *            port number, currently only support PINPAD port(i.e. channel 0)
     * </div>
     * 
     * @throws PortException
     * <div class="zh">�˿ڴ���</div>
     * <div class="en">proximity IC card reader error</div>
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
     * ��λͨѶ��,�ú�����������ڽ��ջ������е���������
     * </div>
     * <div class="en">
     * reset port, this will clear tx buffer.
     * </div>
     * 
     * @param channel
     * <div class="zh">
     *            �˿ں�, Ŀǰֻ֧��PINPAD�ڼ�channel 0. 
     * </div>
     * <div class="en">
     *            port number, currently only support PINPAD port(i.e. channel 0)
     * </div>
     * 
     * @throws PortException
     * <div class="zh">�˿ڴ���</div>
     * <div class="en">proximity IC card reader error</div>
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
     * ʹ��ָ����ͨѶ�ڷ��������ֽڵ�����
     * </div>
     * <div class="en">
     * send data via specified port
     * </div>
     * 
     * @param channel
     * <div class="zh">
     *            �˿ں�, Ŀǰֻ֧��PINPAD�ڼ�channel 0. 
     * </div>
     * <div class="en">
     *            port number, currently only support PINPAD port(i.e. channel 0)
     * </div>
     * 
     * @param buffer
     * <div class="zh">
     *            [����]�����͵����� 
     * </div>
     * <div class="en">
     *            [input]data to send
     * </div>
     * 
     * @throws PortException
     * <div class="zh">�˿ڴ���</div>
     * <div class="en">proximity IC card reader error</div>
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
     * �ڸ�����ʱ����,�������������ȵ����� <br/>
     * ע����ճ�ʱ�����׳��쳣,���Ƿ������ݳ���Ϊ0
     * </div>
     * <div class="en">
     * receive data via specified port within timeout, maximum expLen bytes. 
     * Note that receive timeout will not throw exception, instead, a byte array with 0 byte length is returned. 
     * </div>
     * 
     * @param channel
     * <div class="zh">
     *            �˿ں�, Ŀǰֻ֧��PINPAD�ڼ�channel 0. 
     * </div>
     * <div class="en">
     *            port number, currently only support PINPAD port(i.e. channel 0)
     * </div>
     * 
     * @param expLen
     * <div class="zh">
     *            �������յ������ֽ��� 
     * </div>
     * <div class="en">
     *            data length expected
     * </div>
     * 
     * @param timeout
     * <div class="zh">
     *            ���ճ�ʱʱ��(��λ����). Ϊ0ʱ,�������ݱ���պ������˳�,������Ҳ�����˳����������յ����ֽ��� 
     * </div>
     * <div class="en">
     *            timeout to receive(in ms). if equals 0, read available data and return immediately.
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		���յ�������
     * </div>
     * <div class="en">
     * 		data received
     * </div>
     * 
     * @throws PortException
     * <div class="zh">�˿ڴ���</div>
     * <div class="en">proximity IC card reader error</div>
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
