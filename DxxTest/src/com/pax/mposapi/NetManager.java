package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;
import com.pax.mposapi.util.Utils;

/**
 * <div class="zh">
 * NetManager ��������ͨ��, Ŀǰֻ���� Modem PPPͨ��
 * </div>
 * <div class="en">
 * NetManager is used for network communcation, currently only for Modem PPP
 * </div>
 *
 */
public class NetManager {
	public static final int NET_SOCK_STREAM					 	= 0x01;
	public static final int NET_SOCK_DGRAM					 	= 0x02;
	
	public static final int CMD_IO_SET							= 0x01;
	public static final int CMD_IO_GET							= 0x02;
	public static final int CMD_TO_SET							= 0x03;
	public static final int CMD_TO_GET							= 0x04;
	public static final int CMD_IF_SET							= 0x05;
	public static final int CMD_IF_GET							= 0x06;
	public static final int CMD_EVENT_GET						= 0x07;
	public static final int CMD_BUF_GET							= 0x08;
	public static final int CMD_FD_GET							= 0x09;
	public static final int CMD_KEEPALIVE_SET					= 0x0a;
	public static final int CMD_KEEPALIVE_GET					= 0x0b;
	
	public static final int SOCK_EVENT_READ						= (1<<0);
	public static final int SOCK_EVENT_WRITE					= (1<<1);
	public static final int SOCK_EVENT_CONN						= (1<<2);
	public static final int SOCK_EVENT_NETACCEPT				= (1<<3);
	public static final int SOCK_EVENT_ERROR					= (1<<4);
	public static final int SOCK_EVENT_MASK						= 0xff;
	
	//arg for CMD_BUF_GET
	public static final int TCP_SND_BUF_MAX						= 0x01;
	public static final int TCP_RCV_BUF_MAX						= 0x02;
	public static final int TCP_SND_BUF_AVAIL					= 0x04;	
	
    private static final String TAG = "NetManager";
    private final Proto proto;
    private Context context;
    private static NetManager instance;
    private final int MAX_RECV_BUFFER_SIZE = 10240;
    private static int NET_TIMEOUT = 20000; // default is 20000ms in monitor side
    //private static int NET_TIMEOUT = 300000;	//should I give a large enough value
    
    /**
     * <div class="zh">
     * ʹ��ָ����Context�����NetManager����
     * </div>
     * <div class="en">
     * Create a NetManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">Ӧ�õ�ǰ��context</div>
     * <div class="en">application context currently</div>
     */    
    private NetManager(Context context) {
    	proto = Proto.getInstance(context);
    	this.context = context;
    }

    /**
     * Create a NetManager instance with a given Context
     * 
     * @param context
     *            application context currently
     */
    public static NetManager getInstance(Context context) {
        if (instance == null) {
        	instance = new NetManager(context);
        }
        return instance;
    }

    /**
     * <div class="zh">
     * ���������׽���,�൱�ڴ���һ�����Ӿ��
     * </div>
     * <div class="en">
     * Create network Socket, which is equal to create a connection handle
     * </div>
     * 
     * @param type
	 *		{@link #NET_SOCK_STREAM}: TCP socket<br/>
	 *		{@link #NET_SOCK_DGRAM}: UDP socket<br/>
     * 
     * @return  
     * 		socket
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
    public int NetSocket(int type) throws NetException, IOException, ProtoException, CommonException {
		RespCode rc = new RespCode();
		byte[] req = new byte[1];
		byte[] resp = new byte[4];
		
		proto.sendRecv(Cmd.CmdType.NET_SOCKET, req, rc, resp);
		if (rc.code == 0) {
			//success
			int socket = Utils.intFromByteArray(resp, 0);
			return socket;
		} else {
			throw new NetException(rc.code);    		
		}    	
    }
    

    /**
     * <div class="zh">
     * ���������׽���,�൱�ڴ���һ�����Ӿ��
     * </div>
     * <div class="en">
     * Create network Socket, which is equal to create a connection handle
     * </div>
     * 
     * @param socket
     * 		socket
     * 
     * @param ip
     * <div class="zh">
     *		������IP��ַ
     * </div>
     * <div class="en">
     * 		server IP address
     * </div>
     * 
     * @param port
     * <div class="zh">
     *		�������˿ں�
     * </div>
     * <div class="en">
     * 		server port
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
    public void NetConnect(int socket, String ip, short port) throws NetException, IOException, ProtoException, CommonException {
		RespCode rc = new RespCode();
		byte[] ipBytes = ip.getBytes();
		byte[] req = new byte[4 + 2 + 1 + ipBytes.length];
		Utils.int2ByteArray(socket, req, 0);
		Utils.short2ByteArray(port, req, 4);
		req[6] = (byte)ipBytes.length;
		System.arraycopy(ipBytes, 0, req, 7, ipBytes.length);

    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += NET_TIMEOUT;
		
    	try {
			proto.sendRecv(Cmd.CmdType.NET_CONNECT, req, rc, new byte[0]);
			if (rc.code == 0) {
				//success
			} else {
				throw new NetException(rc.code);    		
			}
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    }
    
    /**
     * <div class="zh">
     * �����ӶԷ���������, ֻ����{@link #NET_SOCK_STREAM}
     * </div>
     * <div class="en">
     * send data to connected peer, only for {@link #NET_SOCK_STREAM} 
     * </div>
     * 
     * @param socket
     * 		socket
     * 
     * @param buf
     * <div class="zh">
     *		�����͵�����
     * </div>
     * <div class="en">
     * 		data to send
     * </div>
     * 
     * @param flags
     * <div class="zh">
     *		��־, ĿǰΪ0
     * </div>
     * <div class="en">
     * 		flags, currently is 0
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
    public void NetSend(int socket, byte[] buf, int flags) throws NetException, IOException, ProtoException, CommonException {
		RespCode rc = new RespCode();
		byte[] req = new byte[4 + 4 + 2 + buf.length];
		Utils.int2ByteArray(socket, req, 0);
		Utils.int2ByteArray(flags, req, 4);
		Utils.short2ByteArray((short)buf.length, req, 8);
		System.arraycopy(buf, 0, req, 10, buf.length);

    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += NET_TIMEOUT;
		
    	try {
			proto.sendRecv(Cmd.CmdType.NET_SEND, req, rc, new byte[0]);
			if (rc.code == 0) {
				//success
			} else {
				throw new NetException(rc.code);    		
			}
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    }
    
    /**
     * <div class="zh">
     * ��������, ֻ����{@link #NET_SOCK_DGRAM}
     * </div>
     * <div class="en">
     * send data, only for {@link #NET_SOCK_DGRAM} 
     * </div>
     * 
     * @param socket
     * 		socket
     * 
     * @param buf
     * <div class="zh">
     *		�����͵�����
     * </div>
     * <div class="en">
     * 		data to send
     * </div>
     * 
     * @param flags
     * <div class="zh">
     *		��־, ĿǰΪ0
     * </div>
     * <div class="en">
     * 		flags, currently is 0
     * </div>
     * 
     * @param ip
     * <div class="zh">
     *		Ŀ��ip��ַ
     * </div>
     * <div class="en">
     * 		destination ip address
     * </div>
     * 
     * @param port
     * <div class="zh">
     *		Ŀ�Ķ˿�
     * </div>
     * <div class="en">
     * 		destination port
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
    public void NetSendTo(int socket, byte[] buf, int flags, String ip, short port) throws NetException, IOException, ProtoException, CommonException {
		RespCode rc = new RespCode();
		byte[] ipBytes = ip.getBytes();
		byte[] req = new byte[4 + 4 + 2 + 2 + buf.length + 1 + ipBytes.length];
		Utils.int2ByteArray(socket, req, 0);
		Utils.int2ByteArray(flags, req, 4);
		Utils.short2ByteArray(port, req, 8);
		Utils.short2ByteArray((short)buf.length, req, 10);
		System.arraycopy(buf, 0, req, 12, buf.length);
		req[12 + buf.length] = (byte)ipBytes.length;
		System.arraycopy(ipBytes, 0, req, 12 + buf.length + 1, ipBytes.length);
		
    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += NET_TIMEOUT;
		
    	try {		
			proto.sendRecv(Cmd.CmdType.NET_SENDTO, req, rc, new byte[0]);
			if (rc.code == 0) {
				//success
			} else {
				throw new NetException(rc.code);    		
			}
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    }
    
    /**
     * <div class="zh">
     * ��������, ֻ����{@link #NET_SOCK_STREAM}
     * </div>
     * <div class="en">
     * receive data, only for {@link #NET_SOCK_STREAM} 
     * </div>
     * 
     * @param socket
     * 		socket
     * 
     * @param expLen
     * <div class="zh">
     *		�����յ�����󳤶�
     * </div>
     * <div class="en">
     * 		maximum length expected to receive
     * </div>
     * 
     * @param flags
     * <div class="zh">
     *		��־, ĿǰΪ0
     * </div>
     * <div class="en">
     * 		flags, currently is 0
     * </div>
     * 
     * @return
     * <div class="zh">
     *		���յ�������
     * </div>
     * <div class="en">
     * 		data received
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
    public byte[] NetRecv(int socket, int expLen, int flags) throws NetException, IOException, ProtoException, CommonException {
		RespCode rc = new RespCode();
		byte[] req = new byte[4 + 2 + 4];
		Utils.int2ByteArray(socket, req, 0);
		Utils.short2ByteArray((short)expLen, req, 4);
		Utils.int2ByteArray(flags, req, 6);
		
    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += NET_TIMEOUT;
		
    	try {
			byte[] resp = new byte[MAX_RECV_BUFFER_SIZE];
			proto.sendRecv(Cmd.CmdType.NET_RECV, req, rc, resp);
			if (rc.code == 0) {
				//success
	    		int len = Utils.shortFromByteArray(resp, 0);
	    		byte[] recv = new byte[len];
	    		System.arraycopy(resp, 2, recv, 0, len);
	    		return recv;
			} else {
				throw new NetException(rc.code);    		
			}
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    }
    
    /**
     * <div class="zh">
     * ��������, ֻ����{@link #NET_SOCK_DGRAM}
     * </div>
     * <div class="en">
     * receive data, only for {@link #NET_SOCK_DGRAM} 
     * </div>
     * 
     * @param socket
     * 		socket
     * 
     * @param expLen
     * <div class="zh">
     *		�����յ�����󳤶�
     * </div>
     * <div class="en">
     * 		maximum length expected to receive
     * </div>
     * 
     * @param flags
     * <div class="zh">
     *		��־, ĿǰΪ0
     * </div>
     * <div class="en">
     * 		flags, currently is 0
     * </div>
     * 
     * @param ipFrom
     * <div class="zh">
     *		[���] ������ԴIP��ַ, ֻ��ipFrom[0]��Ч, ���Ϊnull�����
     * </div>
     * <div class="en">
     * 		[output] data source IP address, only ipFrom[0] is valid, set to null if not interested in this value
     * </div>
     * 
     * @param portFrom
     * <div class="zh">
     *		[���] ������Դ�˿ں�, ֻ��portFrom[0]��Ч, ���Ϊnull�����
     * </div>
     * <div class="en">
     * 		[output] data source port, only portFrom[0] is valid, set to null if not interested in this value
     * </div>
     * 
     * @return
     * <div class="zh">
     *		���յ�������
     * </div>
     * <div class="en">
     * 		data received
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
    public byte[] NetRecvFrom(int socket, int expLen, int flags, String[] ipFrom, short[] portFrom) throws NetException, IOException, ProtoException, CommonException {
		RespCode rc = new RespCode();
		byte[] req = new byte[4 + 2 + 4];
		Utils.int2ByteArray(socket, req, 0);
		Utils.short2ByteArray((short)expLen, req, 4);
		Utils.int2ByteArray(flags, req, 6);
		
    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += NET_TIMEOUT;
		
    	try {		
			
			byte[] resp = new byte[MAX_RECV_BUFFER_SIZE];
			proto.sendRecv(Cmd.CmdType.NET_RECVFROM, req, rc, resp);
			if (rc.code == 0) {
				//success
	    		int dataLen = Utils.shortFromByteArray(resp, 0);
	    		byte[] recv = new byte[dataLen];
	    		System.arraycopy(resp, 2, recv, 0, dataLen);
	    		
				int ipLen = resp[2 + dataLen];
	    		if (ipFrom != null) {
	    			byte[] ip = new byte[ipLen];
	    			System.arraycopy(resp, 2 + dataLen + 1, ip, 0, ipLen);
	    			ipFrom[0] = new String(ip);
	    		}
	    		
	    		if (portFrom != null) {
	    			short port = Utils.shortFromByteArray(resp, 2 + dataLen + 1 + ipLen);
	    			portFrom[0] = new Short(port);
	    		}
	    		
	    		return recv;
			} else {
				throw new NetException(rc.code);    		
			}
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    	}
    }
        
    /**
     * <div class="zh">
     * �ر�socket
     * </div>
     * <div class="en">
     * close socket 
     * </div>
     * 
     * @param socket
     * 		socket
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
    public void NetCloseSocket(int socket) throws NetException, IOException, ProtoException, CommonException {
		RespCode rc = new RespCode();
		byte[] req = new byte[4];
		Utils.int2ByteArray(socket, req, 0);
		
		proto.sendRecv(Cmd.CmdType.NET_CLOSE_SOCKET, req, rc, new byte[0]);
		if (rc.code == 0) {
			//success
		} else {
			throw new NetException(rc.code);    		
		}    	
    }
    
    /**
     * <div class="zh">
     * ���úͻ�ȡ�׽��ֵ���Ϣ
     * </div>
     * <div class="en">
     * Set and get socket information
     * </div>
     * 
     * @param socket
     * <div class="zh">socket</div>
     * <div class="en">socket</div>
     * 
     * @param cmd
     * <div class="zh">
     * ��������,Ŀǰ֧�ֵ���:<br/>
     * 1. {@link #CMD_IO_SET}:����I/Oģʽ(����ģʽ���첽ģʽ);<br/>
     * 2. {@link #CMD_IO_GET}:��ȡI/Oģʽ;<br/>
     * 3. {@link #CMD_TO_SET}:���ó�ʱʱ��,��I/OģʽΪ����ʱ��Ч,ϵͳȱʡ�ĳ�ʱʱ��Ϊ20��;<br/>
     * 4. {@link #CMD_TO_GET}:��ȡ��ʱʱ��;<br/>
     * 5. {@link #CMD_IF_SET}:�׽��ְ�����ӿ�,���׽�����Ϊ������ʱ,��������Ч;<br/>
     * 6. {@link #CMD_IF_GET}:��ȡ�׽��ְ󶨵�����ӿ�,���׽�����Ϊ������ʱ,��������Ч;<br/>
     * 7. {@link #CMD_EVENT_GET}:��ȡ�׽����¼�,������ֻ��{@link #NET_SOCK_STREAM}:��Ч,���ܴ��ڵ��¼���:<br/>
     * 		{@link #SOCK_EVENT_READ}:�����ݿɶ�;<br/>
     * 		{@link #SOCK_EVENT_WRITE}:���Է�������;<br/>
     * 		{@link #SOCK_EVENT_CONN}:�����ѳɹ�;<br/>
     * 		{@link #SOCK_EVENT_NETACCEPT}:���µĿͻ������ӵ���;<br/>
     * 		{@link #SOCK_EVENT_ERROR}:���ӷ��������ѶϿ�;<br/>
     * 8. {@link #CMD_BUF_GET}:��ȡ���������,��������Ҫ���ڻ�ȡЭ���ϵͳ���,������ֻ��{@link #NET_SOCK_STREAM}��Ч;<br/>
     * 9. {@link #CMD_FD_GET}:��ȡ���еľ����Ŀ,Ӧ�ÿɻ�֪�Ƿ��о��й¶�����;<br/>
     * 10. {@link #CMD_KEEPALIVE_SET}:����sock��KeepAlive����,������ֻ��{@link #NET_SOCK_STREAM}��Ч;<br/>
     * 11. {@link #CMD_KEEPALIVE_GET} ��ȡsock��KeepAlive���,������ֻ��{@link #NET_SOCK_STREAM}��Ч;<br/>
     * </div>
     * <div class="en">
     * Operation command, currentyly support:<br/>
     * 1. {@link #CMD_IO_SET}: Sets I/O mode(Block Mode and asynchronous mode);<br/>
     * 2. {@link #CMD_IO_GET}: Gets I/O mode;<br/>
     * 3. {@link #CMD_TO_SET}: Sets timeout time; if I/O mode is block valid, default system timeout time is 20 seconds.<br/>
     * 4. {@link #CMD_TO_GET}: Gets timeout time;<br/>
     * 5. {@link #CMD_IF_SET}: Binded network interface by Socket; if Socket is working as server, this command is invalid;<br/>
     * 6. {@link #CMD_IF_GET}: Gets binded network interface by Socket; if Socket is working as server, this command is invalid;��<br/>
     * 7. {@link #CMD_EVENT_GET}: Gets Socket events, this command is only valid for {@link #NET_SOCK_STREAM}, existed events would be:<br/>
     *  	{@link #SOCK_EVENT_READ} reading data required<br/>
     *  	{@link #SOCK_EVENT_WRITE} ready to send data<br/>
     *  	{@link #SOCK_EVENT_CONN} sent successfully<br/>
     *  	{@link #SOCK_EVENT_NETACCEPT} accepted new client connection<br/>
     *  	{@link #SOCK_EVENT_ERROR} Connection error, has been disconnected.<br/>
     * 8. {@link #CMD_BUF_GET} Reads buffer information; this command is mainly to get protocol system information and only valid for {@link #NET_SOCK_STREAM};<br/>
     * 9. {@link #CMD_FD_GET} Gets idle number of file handle; application will know that whether file handle is revealed or not.<br/>
     * 10. {@link #CMD_KEEPALIVE_SET} Sets KeepAlive function of socket; this command is only valid for {@link #NET_SOCK_STREAM};<br/>
     * 11. {@link #CMD_KEEPALIVE_GET} Gets KeepAlive function of socket; this command is only valid for {@link #NET_SOCK_STREAM};
     * </div>
     * 
     * @param arg
     * <div class="zh">
     * ���ڲ�ͬ������,��ֵ�в�ͬ�ĺ���<br/>
     * 1. cmd={@link #CMD_IO_SET},arg=1��ʾ������(�첽)ģʽ,arg=0��ʾ����ģʽ;<br/>
     * 2. cmd={@link #CMD_IO_GET}, arg������;<br/>
     * 3. cmd={@link #CMD_TO_SET}, arg>0��ʾ�ȴ�ʱ��(��λ����), �������Ϊ<=0,���Զ�Ϊ1<br/>
     * 4. cmd={@link #CMD_TO_GET},arg������;<br/>
     * 5. cmd={@link #CMD_IF_SET},arg��ʾ�����豸�ӿ�������,arg=0��ʾ��̫������,arg=10��ʾPPP��·;<br/>
     * 6. cmd={@link #CMD_IF_GET},arg������;<br/>
     * 7. cmd={@link #CMD_EVENT_GET},arg=0��ʾ��ȡ�¼�,arg={@link #SOCK_EVENT_READ}��ʾ��ȡ��ǰ�ɶ����ݳ���,arg={@link #SOCK_EVENT_WRITE}��ʾ��ȡ��ǰ�ɷ��͵����ݳ���,arg={@link #SOCK_EVENT_ERROR}��ʾ��ȡ�������,arg={@link #SOCK_EVENT_NETACCEPT}��ʾ��ȡ��ǰ�ȴ����ӵĸ���;<br/>
     * 8. cmd={@link #CMD_BUF_GET},arg = {@link #TCP_SND_BUF_MAX}��ʾ��ȡ�ɷ��͵����ռ�,arg= {@link #TCP_RCV_BUF_MAX}��ʾ��ȡ�ɽ��յ����ռ�,arg= {@link #TCP_SND_BUF_AVAIL}��ʾ��ȡĿǰ��Ч�ķ��Ϳռ�<br/>
     * 9. cmd={@link #CMD_KEEPALIVE_SET},arg>0��ʾ���նԷ����ĵĵȴ����ʱ��(��λ����)��ʼִ��keepalive����,��ʱ��Э��ջ�Զ���keepalive����,arg����<5000,ϵͳȱʡֵΪ1000ms;arg<=0��ʾ�ر�keepalive����;<br/>
     * 10. cmd={@link #CMD_KEEPALIVE_GET},arg������;
     * </div>
     * 
     * <div class="en">
     * For different command, this value has different meanings:<br/>
     * 1. cmd={@link #CMD_IO_SET}, arg=1 represents unblock (asynchronous) mode; arg=0 represents block mode;<br/>
     * 2. cmd={@link #CMD_IO_GET}, arg has no meaning;<br/>
     * 3. cmd={@link #CMD_TO_SET}, arg>0 represents waiting time (Unit:ms); if set to a value<=0, will be automatically set to 1;<br/>
     * 4. cmd={@link #CMD_TO_GET},arg has no meaning;<br/>
     * 5. cmd={@link #CMD_IF_SET},arg represents interface index of network device; arg=0 represents Ethernet card; arg=10 represents PPP link;<br/>
     * 6. cmd={@link #CMD_IF_GET},arg has no meaning;<br/>
     * 7. cmd={@link #CMD_EVENT_GET},arg represents getting events; arg={@link #SOCK_EVENT_READ} represents getting length of data that currently can be read; arg={@link #SOCK_EVENT_WRITE} represents getting length of data that can be sent; arg={@link #SOCK_EVENT_ERROR} represents getting error message; arg={@link #SOCK_EVENT_NETACCEPT} represents getting number of links that are currently waiting;<br/>
     * 8. cmd={@link #CMD_BUF_GET}, arg = {@link #TCP_SND_BUF_MAX} represents getting maximum space that can be sent; arg= {@link #TCP_RCV_BUF_MAX} represents getting maximum space that can be received; arg= {@link #TCP_SND_BUF_AVAIL} represents getting space that are currently valid;<br/>
     * 9. cmd={@link #CMD_KEEPALIVE_SET}, arg>0 represents that perform keepalive function after maximum waiting time (unit:ms) of receiving sender��s message, and protocol stack will automatically open keepalive function; arg can not be less than 5000; and the default value of arg is 1000ms,arg<=0 represents closing keepalive function;<br/>
     * 10. cmd={@link #CMD_KEEPALIVE_GET}, arg has no meaning;
     * </div>
     * 
     * @return
     * <div class="zh">
     * 	���ɹ�����ʱ,���ڲ�ͬ������,����ֵ�в�ͬ�ĺ���:<br/>
     * 1. cmd={@link #CMD_IO_SET},�ɹ�����0;<br/>
     * 2. cmd={@link #CMD_IO_GET},����1��ʾ������(�첽)ģʽ,����0��ʾ����ģʽ;<br/>
     * 3. cmd={@link #CMD_TO_SET},�ɹ�����0;<br/>
     * 4. cmd={@link #CMD_TO_GET},��ʾ�ȴ���ʱ��(��λ����);<br/>
     * 5. cmd={@link #CMD_IF_SET},�ɹ�����0;<br/>
     * 6. cmd={@link #CMD_IF_GET},���������豸�ӿ�����;<br/>
     * 7. cmd={@link #CMD_EVENT_GET},����arg��ֵ,����ֵ��һ����arg=0����NetSocket�¼�;<br/>
     * 8. cmd={@link #CMD_BUF_GET},����argֵ���ز�ͬ��ֵ;<br/>
     * 9. cmd={@link #CMD_KEEPALIVE_SET},�ɹ�����0,ʧ�ܷ���<0;<br/>
     * 10. cmd={@link #CMD_KEEPALIVE_GET},����0��ʾkeepalive�ر�,����>0��ʾ��ʼִ��keepaliveʱ��
     * </div>
     * <div class="en">
     * 	For return of successful, different returned values have different meanings, in terms of different commands:<br/>
     * 1. cmd={@link #CMD_IO_SET}, Successfully return 0;<br/>
     * 2. cmd={@link #CMD_IO_GET}, Returning 1 represents unblock (asynchronous) mode; returning 0 represents block mode;<br/>
     * 3. cmd={@link #CMD_TO_SET}, Successfully return 0;<br/>
     * 4. cmd={@link #CMD_TO_GET}, the waiting time (unit:ms);
     * 5. cmd={@link #CMD_IF_SET},arg represents interface index of network device; arg=0 represents Ethernet card; arg=10 represents PPP link;
     * 6. cmd={@link #CMD_IF_GET},arg has no meanings;
     * 7. cmd={@link #CMD_EVENT_GET},arg represents getting events; arg={@link #SOCK_EVENT_READ} represents getting length of data that currently can be read; arg={@link #SOCK_EVENT_WRITE} represents getting length of data that can be sent; arg={@link #SOCK_EVENT_ERROR} represents getting error message; arg={@link #SOCK_EVENT_NETACCEPT} represents getting number of links that are currently waiting;
     * 8. cmd={@link #CMD_BUF_GET}, arg = {@link #TCP_SND_BUF_MAX} represents getting maximum space that can be sent; arg= {@link #TCP_RCV_BUF_MAX} represents getting maximum space that can be received; arg= {@link #TCP_SND_BUF_AVAIL} represents getting space that are currently valid;
     * 9. cmd={@link #CMD_KEEPALIVE_SET}, arg>0 represents that perform keepalive function after maximum waiting time (unit:ms) of receiving sender��s message, and protocol stack will automatically open keepalive function; arg can not be less than 5000; and the default value of arg is 1000ms,arg<=0 represents closing keepalive function;
     * 10. cmd={@link #CMD_KEEPALIVE_GET}, arg has no meansing;
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
    public int Netioctl(int socket, int cmd, int arg) throws NetException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
		byte[] req = new byte[4 + 4 + 4];
		Utils.int2ByteArray(socket, req, 0);
		Utils.int2ByteArray(cmd, req, 4);
		Utils.int2ByteArray(arg, req, 8);
		
		byte[] resp = new byte[4];
		proto.sendRecv(Cmd.CmdType.NET_IOCTL, req, rc, resp);
		if (rc.code == 0) {
			//success
			int val = Utils.intFromByteArray(resp, 0);
			
			// update NET_TIMEOUT
			if (cmd == CMD_TO_SET) {
				// set to <= 0 is NOT allowed
				if (arg <= 0) {
					arg = 1;
				}
				NET_TIMEOUT = arg;
			}
			
			return val;
		} else {
			throw new NetException(rc.code);    		
		}
    }
     
	/**
	 * <div class="zh">
	 * ��ȡ�����豸��IP������Ϣ
	 * </div>
	 * <div class="en">
	 * Get the IP configuration information of network device
	 * </div>
	 * 
	 * @param dev
	 * <div class="zh">
	 * �����豸���<br/>
	 * 0:��̫��<br/>
	 * 1: PPPoE<br/>
	 * 10: modem PPP<br/>
	 * 11: ����ģ��(����GRPS, CDMA)<br/>
	 * ע��D800ֻ֧�� 10
	 * </div>
	 * <div class="en">
	 * Network device number(decimal).<br/>
	 * 0 - Ethernet<br/>
	 * 1- PPPoE<br/>
	 * 10 - Modem PPP<br/>
	 * 11 - Wireless module(GPRS or CDMA)<br/>
	 * Note: D800 only supports 10
	 * </div>
	 * 
	 * @return
	 * <div class="zh">
	 * 	String[0]: ip��ַ<br/>
	 *  String[1]: ��������<br/>
	 *  String[2]: ���ص�ַ<br/>
	 *  String[3]: DNS��ַ<br/>
	 * </div>
	 * <div class="en">
	 * 	String[0]: ip address<br/>
	 *  String[1]: subnet mask<br/>
	 *  String[2]: gateway address<br/>
	 *  String[3]: DNS address<br/>
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
    public String[] NetDevGet(int dev) throws NetException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
		byte[] req = new byte[4];
		Utils.int2ByteArray(dev, req, 0);
		
		byte[] resp = new byte[64];
		proto.sendRecv(Cmd.CmdType.NET_DEVGET, req, rc, resp);
		if (rc.code == 0) {
			//success
			String[] ret = new String[4]; 
			
			int ipLen = resp[0];
			byte[] ip = new byte[ipLen];
			System.arraycopy(resp, 1, ip, 0, ipLen);
			ret[0] = new String(ip);
			
			int maskLen = resp[1 + ipLen];
			byte[] mask = new byte[maskLen];
			System.arraycopy(resp, 1 + ipLen + 1, mask, 0, maskLen);
			ret[1] = new String(mask);

			int gwLen = resp[1 + ipLen + 1 + maskLen];
			byte[] gw = new byte[gwLen];
			System.arraycopy(resp, 1 + ipLen + 1 + maskLen + 1, gw, 0, gwLen);
			ret[2] = new String(gw);

			int dnsLen = resp[1 + ipLen + 1 + maskLen + 1 + gwLen];
			byte[] dns = new byte[dnsLen];
			System.arraycopy(resp, 1 + ipLen + 1 + maskLen + 1 + gwLen + 1, dns, 0, dnsLen);
			ret[3] = new String(dns);
			
			return ret;
		} else {
			throw new NetException(rc.code);    		
		}
    }
    
	/**
	 * <div class="zh">
	 * ��������
	 * </div>
	 * <div class="en">
	 * DNS resolve
	 * </div>
	 * 
	 * @param name
	 * <div class="zh">
	 * 	������
	 * </div>
	 * <div class="en">
	 *  host name to resolve
	 * </div>
	 * 
	 * @return
	 * <div class="zh">
	 * 		����������IP��ַ
	 * </div>
	 * <div class="en">
	 * 		IP adress resolved
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
    public String DnsResolve(String name) throws NetException, IOException, ProtoException, CommonException {
       	RespCode rc = new RespCode();
       	byte[] nameBytes = name.getBytes();
		byte[] req = new byte[1 + nameBytes.length];
		req[0] = (byte)nameBytes.length;
		System.arraycopy(nameBytes, 0, req, 1, nameBytes.length);
		
		byte[] resp = new byte[16];
		proto.sendRecv(Cmd.CmdType.NET_DNS_RESOLVE, req, rc, resp);
		if (rc.code == 0) {
			//success
			int ipLen = resp[0];
			byte[] ip = new byte[ipLen];
			System.arraycopy(resp, 1, ip, 0, ipLen);
			
			return new String(ip);
		} else {
			throw new NetException(rc.code);    		
		}    	
    }
    
    
	/**
	 * <div class="zh">
	 * ping
	 * </div>
	 * <div class="en">
	 * ping
	 * </div>
	 * 
	 * @param ip
	 * <div class="zh">
	 * 	Ŀ��IP��ַ
	 * </div>
	 * <div class="en">
	 *  destination IP address
	 * </div>
	 * 
	 * @param timeout
	 * <div class="zh">
	 * 	��ʱ,��λms
	 * </div>
	 * <div class="en">
	 *  timeout, in unit ms
	 * </div>
	 * 
	 * @param size
	 * <div class="zh">
	 * 	ping��������ݳ���, <=1024�ֽ�
	 * </div>
	 * <div class="en">
	 *  data size in the ping package, <= 1024 bytes
	 * </div>
	 * 
	 * @return
	 * <div class="zh">
	 * 		ping������ʱ��, ��λms
	 * </div>
	 * <div class="en">
	 * 		round-trip-delay of ping, in unit ms 
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
    public int NetPing(String ip, int timeout, int size) throws NetException, IOException, ProtoException, CommonException {
       	RespCode rc = new RespCode();
       	byte[] ipBytes = ip.getBytes();
		byte[] req = new byte[4 + 2 + 1 + ipBytes.length];
		Utils.int2ByteArray(timeout, req, 0);
		Utils.short2ByteArray((short)size, req, 4);
		req[6] = (byte)ipBytes.length;
		System.arraycopy(ipBytes, 0, req, 7, ipBytes.length);
		
    	ConfigManager cfg = ConfigManager.getInstance(context);
    	int savedRecvTimeout = cfg.receiveTimeout;
    	cfg.receiveTimeout += timeout;
		
		byte[] resp = new byte[4];
		try {
			proto.sendRecv(Cmd.CmdType.NET_PING, req, rc, resp);
			if (rc.code == 0) {
				//success
				return Utils.intFromByteArray(resp, 0);
			} else {
				throw new NetException(rc.code);    		
			}
		} finally {
    		cfg.receiveTimeout = savedRecvTimeout;			
		}
    }
}
