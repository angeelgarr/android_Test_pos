package com.pax.mposapi;

/**
 * <div class="zh">
 * PortException 用于管理 非接读卡器的异常错误
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
     * 端口错误码起始值
     * </div>
     * <div class="en">
     * port error code start value
     * </div>
     */    
    public static final int PORT_ERR_START = -0x40000;
    
    /**
     * <div class="zh">
     * 发送缓冲区未空
     * </div>
     * <div class="en">
     * tx buffer not empty
     * </div>
     */    
    public static final int PORT_ERR_TX_BUFFER_NOT_EMPTY = PORT_ERR_START - 1;
    /**
     * <div class="zh">
     * 非法通道号
     * </div>
     * <div class="en">
     * invalid channel
     * </div>
     */        
    public static final int PORT_ERR_INVALID_CHANNEL = PORT_ERR_START - 2;
    /**
     * <div class="zh">
     * 通道未打开且未与任何物理端口连通
     * </div>
     * <div class="en">
     * port not open
     * </div>
     */            
    public static final int PORT_ERR_CHANNEL_NOT_OPEN = PORT_ERR_START - 3;
    /**
     * <div class="zh">
     * 发送缓冲区错误(持续500ms为满状态)
     * </div>
     * <div class="en">
     * tx buffer error(full state for above 500ms)
     * </div>
     */       
    public static final int PORT_ERR_TX_BUFFER = PORT_ERR_START - 4;
    /**
     * <div class="zh">
     * 无可用的物理端口
     * </div>
     * <div class="en">
     * no available channel
     * </div>
     */       
    public static final int PORT_ERR_NO_AVAILABLE_CHANNEL = PORT_ERR_START - 5;
    
//will never happen
//     * <div class="zh">
//     * 数据接收超时
//     * </div>
//     * <div class="en">
//     * receive timeout
//     * </div>
//     */      
//    public static final int PORT_ERR_DATA_RECV_TIMEOUT = PORT_ERR_START - 0xff;
    /**
     * <div class="zh">
     * 通道正被系统占用
     * </div>
     * <div class="en">
     * channel busy
     * </div>
     */      
    public static final int PORT_ERR_CHANNEL_BUSY = PORT_ERR_START - 0xf0;
    /**
     * <div class="zh">
     * 无效的通讯参数,通讯参数不符合字符串规则或数据超出正常范围
     * </div>
     * <div class="en">
     * invalid parameter
     * </div>
     */          
    public static final int PORT_ERR_INVALID_PARAM = PORT_ERR_START - 0xfe;
    /**
     * <div class="zh">
     * 当前的 exception code
     * </div>
     * <div class="en">
     * current exception code
     * </div>
     */        
    public int exceptionCode = -0xFFFF;
    
    /**
     * <div class="zh">
     * 使用指定的exception code构造出PortException对象
     * </div>
     * <div class="en">
     * Create a PortException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">错误码</div>
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
     * 向System.err输出调用栈信息
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
