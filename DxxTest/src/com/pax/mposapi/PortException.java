package com.pax.mposapi;

/**
 * <div class="zh">
 * PortException ���ڹ��� �ǽӶ��������쳣����
 * </div>
 * <div class="en">
 * PortException manages the port exceptions
 * </div>
 *
 */
public class PortException  extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * <div class="zh">
     * �˿ڴ�������ʼֵ
     * </div>
     * <div class="en">
     * port error code start value
     * </div>
     */    
    public static final int PORT_ERR_START = -0x40000;
    
    /**
     * <div class="zh">
     * ���ͻ�����δ��
     * </div>
     * <div class="en">
     * tx buffer not empty
     * </div>
     */    
    public static final int PORT_ERR_TX_BUFFER_NOT_EMPTY = PORT_ERR_START - 1;
    /**
     * <div class="zh">
     * �Ƿ�ͨ����
     * </div>
     * <div class="en">
     * invalid channel
     * </div>
     */        
    public static final int PORT_ERR_INVALID_CHANNEL = PORT_ERR_START - 2;
    /**
     * <div class="zh">
     * ͨ��δ����δ���κ�����˿���ͨ
     * </div>
     * <div class="en">
     * port not open
     * </div>
     */            
    public static final int PORT_ERR_CHANNEL_NOT_OPEN = PORT_ERR_START - 3;
    /**
     * <div class="zh">
     * ���ͻ���������(����500msΪ��״̬)
     * </div>
     * <div class="en">
     * tx buffer error(full state for above 500ms)
     * </div>
     */       
    public static final int PORT_ERR_TX_BUFFER = PORT_ERR_START - 4;
    /**
     * <div class="zh">
     * �޿��õ�����˿�
     * </div>
     * <div class="en">
     * no available channel
     * </div>
     */       
    public static final int PORT_ERR_NO_AVAILABLE_CHANNEL = PORT_ERR_START - 5;
    
//will never happen
//     * <div class="zh">
//     * ���ݽ��ճ�ʱ
//     * </div>
//     * <div class="en">
//     * receive timeout
//     * </div>
//     */      
//    public static final int PORT_ERR_DATA_RECV_TIMEOUT = PORT_ERR_START - 0xff;
    /**
     * <div class="zh">
     * ͨ������ϵͳռ��
     * </div>
     * <div class="en">
     * channel busy
     * </div>
     */      
    public static final int PORT_ERR_CHANNEL_BUSY = PORT_ERR_START - 0xf0;
    /**
     * <div class="zh">
     * ��Ч��ͨѶ����,ͨѶ�����������ַ�����������ݳ���������Χ
     * </div>
     * <div class="en">
     * invalid parameter
     * </div>
     */          
    public static final int PORT_ERR_INVALID_PARAM = PORT_ERR_START - 0xfe;
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
    public PortException(int code) {
    	super(searchMessage(code));
        if (code != -0xFFFF) {
        	exceptionCode = PORT_ERR_START - code;
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
        	messageId = PORT_ERR_START - messageId;
        }
        switch (messageId) {
        case PORT_ERR_TX_BUFFER_NOT_EMPTY:
        	message = "Port Tx buffer not empty";
        	break;
        case PORT_ERR_INVALID_CHANNEL:
        	message = "invalid channel";
        	break;
        case PORT_ERR_CHANNEL_NOT_OPEN:
        	message = "channel not open";
        	break;
        case PORT_ERR_TX_BUFFER:
        	message = "tx buffer error";
        	break;
        case PORT_ERR_NO_AVAILABLE_CHANNEL:
        	message = "No available channel";
        	break;
        	/*
        case PORT_ERR_DATA_RECV_TIMEOUT:
        	message = "data receive timeout";
        	break;
        	*/
        case PORT_ERR_CHANNEL_BUSY:
        	message = "channel busy";
        	break;
        case PORT_ERR_INVALID_PARAM:
        	message = "invalid parameter";
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
