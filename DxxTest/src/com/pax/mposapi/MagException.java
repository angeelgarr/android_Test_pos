package com.pax.mposapi;
/**
 * <div class="zh">
 * MagException ���ڹ��� �ſ��Ķ������쳣����
 * </div>
 * <div class="en">
 * MagException manages the magnetic stripe card reader exceptions
 * </div>
 *
 */
public class MagException extends Exception {

    private static final long serialVersionUID = 1L;
	/**
     * <div class="zh">
     * �ſ��Ķ��� ��������ʼֵ
     * </div>
     * <div class="en">
     * magnetic stripe card reader error code start value
     * </div>
     */
    public static final int MAG_ERR_START = -0x20000;
    
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
     * ʹ��ָ����exception code�����MagException����
     * </div>
     * <div class="en">
     * Create a MagException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">������</div>
     * <div class="en">exception code</div>
     */
    MagException(int code) {
    	super(searchMessage(code));
        if (code != -0xFFFF) {
        	exceptionCode = MAG_ERR_START - code;
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
        	messageId = MAG_ERR_START - messageId;
        }
        switch(messageId) {
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
