package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;
import com.pax.mposapi.util.Utils;

/**
 * <div class="zh">
 * NetManager 用于网络通信, 目前只用于 Modem PPP通信
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
     * 使用指定的Context构造出NetManager对象
     * </div>
     * <div class="en">
     * Create a NetManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">应用当前的context</div>
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
     * 创建网络套接字,相当于创建一个连接句柄
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
     * 创建网络套接字,相当于创建一个连接句柄
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
     *		服务器IP地址
     * </div>
     * <div class="en">
     * 		server IP address
     * </div>
     * 
     * @param port
     * <div class="zh">
     *		服务器端口号
     * </div>
     * <div class="en">
     * 		server port
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
     * 向连接对方发送数据, 只用于{@link #NET_SOCK_STREAM}
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
     *		待发送的数据
     * </div>
     * <div class="en">
     * 		data to send
     * </div>
     * 
     * @param flags
     * <div class="zh">
     *		标志, 目前为0
     * </div>
     * <div class="en">
     * 		flags, currently is 0
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
     * 发送数据, 只用于{@link #NET_SOCK_DGRAM}
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
     *		待发送的数据
     * </div>
     * <div class="en">
     * 		data to send
     * </div>
     * 
     * @param flags
     * <div class="zh">
     *		标志, 目前为0
     * </div>
     * <div class="en">
     * 		flags, currently is 0
     * </div>
     * 
     * @param ip
     * <div class="zh">
     *		目的ip地址
     * </div>
     * <div class="en">
     * 		destination ip address
     * </div>
     * 
     * @param port
     * <div class="zh">
     *		目的端口
     * </div>
     * <div class="en">
     * 		destination port
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
     * 接收数据, 只用于{@link #NET_SOCK_STREAM}
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
     *		期望收到的最大长度
     * </div>
     * <div class="en">
     * 		maximum length expected to receive
     * </div>
     * 
     * @param flags
     * <div class="zh">
     *		标志, 目前为0
     * </div>
     * <div class="en">
     * 		flags, currently is 0
     * </div>
     * 
     * @return
     * <div class="zh">
     *		接收到的数据
     * </div>
     * <div class="en">
     * 		data received
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
     * 接收数据, 只用于{@link #NET_SOCK_DGRAM}
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
     *		期望收到的最大长度
     * </div>
     * <div class="en">
     * 		maximum length expected to receive
     * </div>
     * 
     * @param flags
     * <div class="zh">
     *		标志, 目前为0
     * </div>
     * <div class="en">
     * 		flags, currently is 0
     * </div>
     * 
     * @param ipFrom
     * <div class="zh">
     *		[输出] 数据来源IP地址, 只有ipFrom[0]有效, 如果为null则不输出
     * </div>
     * <div class="en">
     * 		[output] data source IP address, only ipFrom[0] is valid, set to null if not interested in this value
     * </div>
     * 
     * @param portFrom
     * <div class="zh">
     *		[输出] 数据来源端口号, 只有portFrom[0]有效, 如果为null则不输出
     * </div>
     * <div class="en">
     * 		[output] data source port, only portFrom[0] is valid, set to null if not interested in this value
     * </div>
     * 
     * @return
     * <div class="zh">
     *		接收到的数据
     * </div>
     * <div class="en">
     * 		data received
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
     * 关闭socket
     * </div>
     * <div class="en">
     * close socket 
     * </div>
     * 
     * @param socket
     * 		socket
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
     * 设置和获取套接字的信息
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
     * 操作命令,目前支持的有:<br/>
     * 1. {@link #CMD_IO_SET}:设置I/O模式(阻塞模式和异步模式);<br/>
     * 2. {@link #CMD_IO_GET}:获取I/O模式;<br/>
     * 3. {@link #CMD_TO_SET}:设置超时时间,当I/O模式为阻塞时有效,系统缺省的超时时间为20秒;<br/>
     * 4. {@link #CMD_TO_GET}:获取超时时间;<br/>
     * 5. {@link #CMD_IF_SET}:套接字绑定网络接口,当套接字作为服务器时,该命令无效;<br/>
     * 6. {@link #CMD_IF_GET}:获取套接字绑定的网络接口,当套接字作为服务器时,该命令无效;<br/>
     * 7. {@link #CMD_EVENT_GET}:获取套接字事件,该命令只对{@link #NET_SOCK_STREAM}:有效,可能存在的事件有:<br/>
     * 		{@link #SOCK_EVENT_READ}:有数据可读;<br/>
     * 		{@link #SOCK_EVENT_WRITE}:可以发送数据;<br/>
     * 		{@link #SOCK_EVENT_CONN}:连接已成功;<br/>
     * 		{@link #SOCK_EVENT_NETACCEPT}:有新的客户端连接到来;<br/>
     * 		{@link #SOCK_EVENT_ERROR}:连接发生错误，已断开;<br/>
     * 8. {@link #CMD_BUF_GET}:读取缓冲区情况,该命令主要用于获取协议的系统情况,该命令只对{@link #NET_SOCK_STREAM}有效;<br/>
     * 9. {@link #CMD_FD_GET}:获取空闲的句柄数目,应用可获知是否有句柄泄露的情况;<br/>
     * 10. {@link #CMD_KEEPALIVE_SET}:配置sock的KeepAlive功能,该命令只对{@link #NET_SOCK_STREAM}有效;<br/>
     * 11. {@link #CMD_KEEPALIVE_GET} 获取sock的KeepAlive情况,该命令只对{@link #NET_SOCK_STREAM}有效;<br/>
     * </div>
     * <div class="en">
     * Operation command, currentyly support:<br/>
     * 1. {@link #CMD_IO_SET}: Sets I/O mode(Block Mode and asynchronous mode);<br/>
     * 2. {@link #CMD_IO_GET}: Gets I/O mode;<br/>
     * 3. {@link #CMD_TO_SET}: Sets timeout time; if I/O mode is block valid, default system timeout time is 20 seconds.<br/>
     * 4. {@link #CMD_TO_GET}: Gets timeout time;<br/>
     * 5. {@link #CMD_IF_SET}: Binded network interface by Socket; if Socket is working as server, this command is invalid;<br/>
     * 6. {@link #CMD_IF_GET}: Gets binded network interface by Socket; if Socket is working as server, this command is invalid;；<br/>
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
     * 对于不同的命令,该值有不同的含义<br/>
     * 1. cmd={@link #CMD_IO_SET},arg=1表示非阻塞(异步)模式,arg=0表示阻塞模式;<br/>
     * 2. cmd={@link #CMD_IO_GET}, arg无意义;<br/>
     * 3. cmd={@link #CMD_TO_SET}, arg>0表示等待时间(单位毫秒), 如果设置为<=0,则自动为1<br/>
     * 4. cmd={@link #CMD_TO_GET},arg无意义;<br/>
     * 5. cmd={@link #CMD_IF_SET},arg表示网络设备接口索引号,arg=0表示以太网网卡,arg=10表示PPP链路;<br/>
     * 6. cmd={@link #CMD_IF_GET},arg无意义;<br/>
     * 7. cmd={@link #CMD_EVENT_GET},arg=0表示获取事件,arg={@link #SOCK_EVENT_READ}表示获取当前可读数据长度,arg={@link #SOCK_EVENT_WRITE}表示获取当前可发送的数据长度,arg={@link #SOCK_EVENT_ERROR}表示获取错误代码,arg={@link #SOCK_EVENT_NETACCEPT}表示获取当前等待连接的个数;<br/>
     * 8. cmd={@link #CMD_BUF_GET},arg = {@link #TCP_SND_BUF_MAX}表示获取可发送的最大空间,arg= {@link #TCP_RCV_BUF_MAX}表示获取可接收的最大空间,arg= {@link #TCP_SND_BUF_AVAIL}表示获取目前有效的发送空间<br/>
     * 9. cmd={@link #CMD_KEEPALIVE_SET},arg>0表示接收对方报文的等待最大时间(单位毫秒)后开始执行keepalive功能,这时候协议栈自动打开keepalive功能,arg不能<5000,系统缺省值为1000ms;arg<=0表示关闭keepalive功能;<br/>
     * 10. cmd={@link #CMD_KEEPALIVE_GET},arg无意义;
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
     * 9. cmd={@link #CMD_KEEPALIVE_SET}, arg>0 represents that perform keepalive function after maximum waiting time (unit:ms) of receiving sender‘s message, and protocol stack will automatically open keepalive function; arg can not be less than 5000; and the default value of arg is 1000ms,arg<=0 represents closing keepalive function;<br/>
     * 10. cmd={@link #CMD_KEEPALIVE_GET}, arg has no meaning;
     * </div>
     * 
     * @return
     * <div class="zh">
     * 	当成功返回时,对于不同的命令,返回值有不同的含义:<br/>
     * 1. cmd={@link #CMD_IO_SET},成功返回0;<br/>
     * 2. cmd={@link #CMD_IO_GET},返回1表示非阻塞(异步)模式,返回0表示阻塞模式;<br/>
     * 3. cmd={@link #CMD_TO_SET},成功返回0;<br/>
     * 4. cmd={@link #CMD_TO_GET},表示等待的时间(单位毫秒);<br/>
     * 5. cmd={@link #CMD_IF_SET},成功返回0;<br/>
     * 6. cmd={@link #CMD_IF_GET},返回网络设备接口索引;<br/>
     * 7. cmd={@link #CMD_EVENT_GET},根据arg的值,返回值不一样：arg=0返回NetSocket事件;<br/>
     * 8. cmd={@link #CMD_BUF_GET},根据arg值返回不同的值;<br/>
     * 9. cmd={@link #CMD_KEEPALIVE_SET},成功返回0,失败返回<0;<br/>
     * 10. cmd={@link #CMD_KEEPALIVE_GET},返回0表示keepalive关闭,返回>0表示开始执行keepalive时间
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
     * 9. cmd={@link #CMD_KEEPALIVE_SET}, arg>0 represents that perform keepalive function after maximum waiting time (unit:ms) of receiving sender‘s message, and protocol stack will automatically open keepalive function; arg can not be less than 5000; and the default value of arg is 1000ms,arg<=0 represents closing keepalive function;
     * 10. cmd={@link #CMD_KEEPALIVE_GET}, arg has no meansing;
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
	 * 读取网络设备的IP配置信息
	 * </div>
	 * <div class="en">
	 * Get the IP configuration information of network device
	 * </div>
	 * 
	 * @param dev
	 * <div class="zh">
	 * 网络设备编号<br/>
	 * 0:以太网<br/>
	 * 1: PPPoE<br/>
	 * 10: modem PPP<br/>
	 * 11: 无线模块(包括GRPS, CDMA)<br/>
	 * 注意D800只支持 10
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
	 * 	String[0]: ip地址<br/>
	 *  String[1]: 子网掩码<br/>
	 *  String[2]: 网关地址<br/>
	 *  String[3]: DNS地址<br/>
	 * </div>
	 * <div class="en">
	 * 	String[0]: ip address<br/>
	 *  String[1]: subnet mask<br/>
	 *  String[2]: gateway address<br/>
	 *  String[3]: DNS address<br/>
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
	 * 域名解析
	 * </div>
	 * <div class="en">
	 * DNS resolve
	 * </div>
	 * 
	 * @param name
	 * <div class="zh">
	 * 	主机名
	 * </div>
	 * <div class="en">
	 *  host name to resolve
	 * </div>
	 * 
	 * @return
	 * <div class="zh">
	 * 		解析出来的IP地址
	 * </div>
	 * <div class="en">
	 * 		IP adress resolved
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
	 * 	目标IP地址
	 * </div>
	 * <div class="en">
	 *  destination IP address
	 * </div>
	 * 
	 * @param timeout
	 * <div class="zh">
	 * 	超时,单位ms
	 * </div>
	 * <div class="en">
	 *  timeout, in unit ms
	 * </div>
	 * 
	 * @param size
	 * <div class="zh">
	 * 	ping包里的数据长度, <=1024字节
	 * </div>
	 * <div class="en">
	 *  data size in the ping package, <= 1024 bytes
	 * </div>
	 * 
	 * @return
	 * <div class="zh">
	 * 		ping的来回时间, 单位ms
	 * </div>
	 * <div class="en">
	 * 		round-trip-delay of ping, in unit ms 
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
