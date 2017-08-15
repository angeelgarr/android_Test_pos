package com.pax.mposapi;

/**
 * <div class="zh">
 * KeyboardException ���ڹ��� keyboard���쳣����
 * </div>
 * <div class="en">
 * KeyboardException manages the keyboard exceptions
 * </div>
 *
 */
public class KeyboardException extends Exception {

    private static final long serialVersionUID = 1L;
    /**
     * <div class="zh">
     * Keyboard��������ʼֵ
     * </div>
     * <div class="en">
     * Keyboard error code start value
     * </div>
     */
    public static final int KEYBOARD_ERR_START = -0x70000;

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
     * ʹ��ָ����exception code�����KeyboardException����
     * </div>
     * <div class="en">
     * Create a KeyboardException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">������</div>
     * <div class="en">exception code</div>
     */
    KeyboardException(int code) {
    	super(searchMessage(code));
        if (code != -0xFFFF) {
        	exceptionCode = KEYBOARD_ERR_START - code;
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
        	messageId = KEYBOARD_ERR_START - messageId;
        }
        switch (messageId) {
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
