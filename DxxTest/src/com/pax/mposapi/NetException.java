package com.pax.mposapi;

/**
 * <div class="zh">
 * NetException ���ڹ��� �����쳣����
 * </div>
 * <div class="en">
 * NetException manages the network exceptions
 * </div>
 *
 */
public class NetException  extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * <div class="zh">
     * �����������ʼֵ
     * </div>
     * <div class="en">
     * net error code start value
     * </div>
     */    
    public static final int NET_ERR_START = -0xc0000;
    /**
     * <div class="zh">
     * �ڴ治��
     * </div>
     * <div class="en">
     * Memory space is not enough
     * </div>
     */    
    public static final int NET_ERR_MEM = NET_ERR_START - 0x01;
    /**
     * <div class="zh">
     * ����������
     * </div>
     * <div class="en">
     * Buffer error
     * </div>
     */        
    public static final int NET_ERR_BUF = NET_ERR_START - 0x02;
    /**
     * <div class="zh">
     * ��ͼ��������ʧ��
     * </div>
     * <div class="en">
     * Establish connection failed.
     * </div>
     */        
    public static final int NET_ERR_ABRT = NET_ERR_START - 0x03;
    /**
     * <div class="zh">
     * ���ӱ��Է���λ(�յ��Է���Reset)
     * </div>
     * <div class="en">
     * Reset the connection by peer (receive the Reset from peer)
     * </div>
     */        
    public static final int NET_ERR_RST = NET_ERR_START - 0x04;
    /**
     * <div class="zh">
     * �����ѹر�
     * </div>
     * <div class="en">
     * Connection closed
     * </div>
     */        
    public static final int NET_ERR_CLSD = NET_ERR_START - 0x05;
    /**
     * <div class="zh">
     * ����δ�ɹ�����
     * </div>
     * <div class="en">
     * Connection not successfully established
     * </div>
     */        
    public static final int NET_ERR_CONN = NET_ERR_START - 0x06;
    /**
     * <div class="zh">
     * �������
     * </div>
     * <div class="en">
     * Variable invalid
     * </div>
     */        
    public static final int NET_ERR_VAL = NET_ERR_START - 0x07;
    /**
     * <div class="zh">
     * �������
     * </div>
     * <div class="en">
     * Parameter invalid
     * </div>
     */        
    public static final int NET_ERR_ARG = NET_ERR_START - 0x08;
    /**
     * <div class="zh">
     * ����·��(route)
     * </div>
     * <div class="en">
     * Route invalid
     * </div>
     */        
    public static final int NET_ERR_RTE = NET_ERR_START - 0x09;
    /**
     * <div class="zh">
     * ��ַ���˿�ʹ����
     * </div>
     * <div class="en">
     * Address or port in use
     * </div>
     */        
    public static final int NET_ERR_USE = NET_ERR_START - 0x0a;
    /**
     * <div class="zh">
     * �ײ�Ӳ������
     * </div>
     * <div class="en">
     * Hardware error
     * </div>
     */        
    public static final int NET_ERR_IF = NET_ERR_START - 0x0b;
    /**
     * <div class="zh">
     * �����ѽ���
     * </div>
     * <div class="en">
     * Connected
     * </div>
     */        
    public static final int NET_ERR_ISCONN = NET_ERR_START - 0x0c;
    /**
     * <div class="zh">
     * ��ʱ
     * </div>
     * <div class="en">
     * Timeout
     * </div>
     */        
    public static final int NET_ERR_TIMEOUT = NET_ERR_START - 0x0d;
    /**
     * <div class="zh">
     * ������Դ������,������
     * </div>
     * <div class="en">
     * Requested sources not exist, retrial required
     * </div>
     */        
    public static final int NET_ERR_AGAIN = NET_ERR_START - 0x0e;
    /**
     * <div class="zh">
     * �Ѵ���
     * </div>
     * <div class="en">
     * Exist
     * </div>
     */        
    public static final int NET_ERR_EXIST = NET_ERR_START - 0x0f;
    /**
     * <div class="zh">
     * ϵͳ��֧��
     * </div>
     * <div class="en">
     * System does not support
     * </div>
     */        
    public static final int NET_ERR_SYS = NET_ERR_START - 0x10;
    /**
     * <div class="zh">
     * ��������
     * </div>
     * <div class="en">
     * Password wrong
     * </div>
     */        
    public static final int NET_ERR_PASSWD = NET_ERR_START - 0x11;
    /**
     * <div class="zh">
     * ����ʧ��
     * </div>
     * <div class="en">
     * Modem dialing failed
     * </div>
     */        
    public static final int NET_ERR_MODEM = NET_ERR_START - 0x12;
    /**
     * <div class="zh">
     * ������·�ѶϿ�,�����²���
     * </div>
     * <div class="en">
     * PPP link is breakdown; redial is required.
     * </div>
     */        
    public static final int NET_ERR_LINKDOWN = NET_ERR_START - 0x13;
    /**
     * <div class="zh">
     * Logout
     * </div>
     * <div class="en">
     * Logout
     * </div>
     */        
    public static final int NET_ERR_LOGOUT = NET_ERR_START - 0x14;
    /**
     * <div class="zh">
     * PPP�Ͽ�
     * </div>
     * <div class="en">
     * PPP disconnected
     * </div>
     */        
    public static final int NET_ERR_PPP = NET_ERR_START - 0x15;
    /**
     * <div class="zh">
     * �ַ���̫��
     * </div>
     * <div class="en">
     * String too long
     * </div>
     */        
    public static final int NET_ERR_STR = NET_ERR_START - 0x16;
    /**
     * <div class="zh">
     * ������������
     * </div>
     * <div class="en">
     * Resolving error of domain name.
     * </div>
     */        
    public static final int NET_ERR_DNS = NET_ERR_START - 0x17;
    /**
     * <div class="zh">
     * ��Ӧ�Ĺ���ϵͳû�г�ʼ��
     * </div>
     * <div class="en">
     * Corresponding functional system are not initialized.
     * </div>
     */        
    public static final int NET_ERR_INIT = NET_ERR_START - 0x18;
    /**
     * <div class="zh">
     * û���ҵ�PPPoE������
     * </div>
     * <div class="en">
     * Do not find PPPoE server.
     * </div>
     */        
    public static final int NET_ERR_SERV = NET_ERR_START - 0x1e;
    
    /**
     * <div class="zh">
     * ��ǰ�� exception code
     * </div>
     * <div class="en">
     * current exception code
     * </div>
     */        
    public int exceptionCode = -0xFFFF;
    
    /**
     * <div class="zh">
     * ʹ��ָ����exception code�����PortException����
     * </div>
     * <div class="en">
     * Create a PortException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">������</div>
     * <div class="en">exception code</div>
     */    
    public NetException(int code) {
    	super(searchMessage(code));
        if (code != -0xFFFF) {
        	exceptionCode = NET_ERR_START + code;
        }
    }
    
    /**
     * search Message According to error No
     * 
     * @param messageId
     *            error No
     * @return error message
     */
    private static String searchMessage(int messageId) {
        String message = "";
        if (messageId != -0xFFFF) {
        	messageId = NET_ERR_START + messageId;
        }
        switch (messageId) {
        case NET_ERR_MEM:
        	message = "Memory space is not enough.";
        	break;
        case NET_ERR_BUF:
        	message = "Buffer error";
        	break;
        case NET_ERR_ABRT:
        	message = "Establish connection failed.";
        	break;
        case NET_ERR_RST:
        	message = "Reset the connection by peer (receive the Reset from peer)";
        	break;
        case NET_ERR_CLSD:
        	message = "Connection closed";
        	break;
        case NET_ERR_CONN:
        	message = "Connection not successfully established";
        	break;
        case NET_ERR_VAL:
        	message = "Variable invalid";
        	break;
        case NET_ERR_ARG:
        	message = "Parameter invalid";
        	break;
        case NET_ERR_RTE:
        	message = "Route invalid";
        	break;
        case NET_ERR_USE:
        	message = "Address or port in use";
        	break;
        case NET_ERR_IF:
        	message = "Hardware error";
        	break;
        case NET_ERR_ISCONN:
        	message = "Connected";
        	break;
        case NET_ERR_TIMEOUT:
        	message = "Timeout";
        	break;
        case NET_ERR_AGAIN:
        	message = "Requested sources not exist, retrial required";
        	break;
        case NET_ERR_EXIST:
        	message = "Exist";
        	break;
        case NET_ERR_SYS:
        	message = "System does not support";
        	break;
        case NET_ERR_PASSWD:
        	message = "Password wrong";
        	break;
        case NET_ERR_MODEM:
        	message = "Modem dialing failed";
        	break;
        case NET_ERR_LINKDOWN:
        	message = "PPP link is breakdown; redial is required.";
        	break;
        case NET_ERR_LOGOUT:
        	message = "Logout";
        	break;
        case NET_ERR_PPP:
        	message = "PPP disconnected";
        	break;
        case NET_ERR_STR:
        	message = "String too long";
        	break;
        case NET_ERR_DNS:
        	message = "Resolving error of domain name.";
        	break;
        case NET_ERR_INIT:
        	message = "Corresponding functional system are not initialized.";
        	break;
        case NET_ERR_SERV:
        	message = "Do not find PPPoE server.";
        	break;
            
		case -0xFFFF:
			message = "Unsupported function";
			break;            
        }
        
        message += String.format("(%d, -0x%x)", messageId, -messageId);
        return message;
    }
    
    /**
     * <div class="zh">
     * ��System.err�������ջ��Ϣ
     * </div>
     * <div class="en">
     * Writes a printable representation of this Throwable's stack trace to the
     * System.err stream.
     * </div>
     *
     */
    @Override
    public void printStackTrace() {
        System.err.println("Exception Code : " + exceptionCode);
        super.printStackTrace();
    }
}
