package com.pax.mposapi;

/**
 * <div class="zh">
 * UIException ���ڹ��� UI���쳣����
 * </div>
 * <div class="en">
 * UIException manages the UI exceptions
 * </div>
 *
 */
public class UIException extends Exception {

    private static final long serialVersionUID = 1L;
	/**
     * <div class="zh">
     * UI��������ʼֵ
     * </div>
     * <div class="en">
     * UI error code start value
     * </div>
     */
    public static final int UI_ERR_START = -0x60000;
    /**
     * <div class="zh">
     * UI��������
     * </div>
     * <div class="en">
     * UI param error
     * </div>
     */
    public static final int UI_ERR_PARAM = UI_ERR_START - 1;
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
     * ʹ��ָ����exception code�����UIException����
     * </div>
     * <div class="en">
     * Create a UIException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">������</div>
     * <div class="en">exception code</div>
     */
    UIException(int code) {
    	super(searchMessage(code));
        if (code != -0xFFFF) {
        	exceptionCode = UI_ERR_START - code;
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
        	messageId = UI_ERR_START - messageId;
        }
        switch (messageId) {
        	case UI_ERR_PARAM:
        		message = "param error";
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
