package com.pax.mposapi;

/**
 * <div class="zh">
 * ProtoException ���ڹ��� ͨ��Э����쳣����
 * </div>
 * <div class="en">
 * ProtoException manages the communication protocol exceptions
 * </div>
 *
 */
public class ProtoException extends Exception{
	
    private static final long serialVersionUID = 1L;
    /**
     * <div class="zh">
     * ������ݱ�terminal NAK
     * </div>
     * <div class="en">
     * received NAK from the terminal
     * </div>
     */ 
    public static final int PROTO_ERR_NAKED = -2;    
    /**
     * <div class="zh">
     * ���յ���ݰ�����
     * </div>
     * <div class="en">
     * received incomplete data package
     * </div>
     */    
    public static final int PROTO_ERR_NO_ENOUGH_DATA1 = -3;
    /**
     * <div class="zh">
     * ���յ���ݰ��ʽ����ȷ
     * </div>
     * <div class="en">
     * received data package format error
     * </div>
     */    
    public static final int PROTO_ERR_DATA_FORMAT1 = -4;
    /**
     * <div class="zh">
     * ���յ���ݰ�У�����
     * </div>
     * <div class="en">
     * received data package checksum error
     * </div>
     */    
    public static final int PROTO_ERR_DATA_FORMAT2 = -10;
    /**
     * <div class="zh">
     * ���յ���ݰ�У�����
     * </div>
     * <div class="en">
     * received data package checksum error
     * </div>
     */    
    public static final int PROTO_ERR_DATA_FORMAT3 = -11;
    /**
     * <div class="zh">
     * ���յ���ݰ�У�����
     * </div>
     * <div class="en">
     * received data package checksum error
     * </div>
     */    
    public static final int PROTO_ERR_DATA_FORMAT4 = -12;
    /**
     * <div class="zh">
     * ���յ���ݰ�У�����
     * </div>
     * <div class="en">
     * received data package checksum error
     * </div>
     */    
    public static final int PROTO_ERR_DATA_FORMAT5 = -13;
    /**
     * <div class="zh">
     * ���յ���ݰ�У�����
     * </div>
     * <div class="en">
     * received data package checksum error
     * </div>
     */    
    public static final int PROTO_ERR_NO_ENOUGH_DATA2 = -6;
    /**
     * <div class="zh">
     * ���յ���ݰ�У�����
     * </div>
     * <div class="en">
     * received data package checksum error
     * </div>
     */    
    public static final int PROTO_ERR_NO_ENOUGH_DATA4 = -7;
    /**
     * <div class="zh">
     * ���յ���ݰ�У�����
     * </div>
     * <div class="en">
     * received data package checksum error
     * </div>
     */    
    public static final int PROTO_ERR_NO_ENOUGH_DATA5 = -8;
    /**
     * <div class="zh">
     * ���յ���ݰ�У�����
     * </div>
     * <div class="en">
     * received data package checksum error
     * </div>
     */    
    public static final int PROTO_ERR_NO_ENOUGH_DATA6 = -9;
    /**
     * <div class="zh">
     * ���յ���ݰ�У�����
     * </div>
     * <div class="en">
     * received data package checksum error
     * </div>
     */    
    public static final int PROTO_ERR_CHKSUM = -5;
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
     * ʹ��ָ����exception code�����ProtoException����
     * </div>
     * <div class="en">
     * Create a ProtoException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">������</div>
     * <div class="en">exception code</div>
     */
    public ProtoException(int code) {
    	super(searchMessage(code));
    	exceptionCode = code;
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
        switch (messageId) {
        case PROTO_ERR_NAKED:
        	message = "naked by peer";
        	break;
        case PROTO_ERR_NO_ENOUGH_DATA1:
        	message = "no enough data1";
        	break;
        case PROTO_ERR_NO_ENOUGH_DATA2:
        	message = "no enough data2";
        	break;
        case PROTO_ERR_NO_ENOUGH_DATA4:
        	message = "no enough data4";
        	break;
        case PROTO_ERR_NO_ENOUGH_DATA5:
        	message = "no enough data5";
        	break;
        case PROTO_ERR_NO_ENOUGH_DATA6:
        	message = "no enough data6";
        	break;
        case PROTO_ERR_DATA_FORMAT1:
        	message = "data format error1";
        	break;
        case PROTO_ERR_DATA_FORMAT2:
        	message = "data format error2";
        	break;
        case PROTO_ERR_DATA_FORMAT3:
        	message = "data format error3";
        	break;
        case PROTO_ERR_DATA_FORMAT4:
        	message = "data format error4";
        	break;
        case PROTO_ERR_DATA_FORMAT5:
        	message = "data format error5";
        	break;
        case PROTO_ERR_CHKSUM:
        	message = "checksum error";
        	break;     	
        }
        message += String.format("(%d, -0x%x)", messageId, -messageId);
        return message;
    }
    /**
     * <div class="zh">
     * ��System.err������ջ��Ϣ
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
