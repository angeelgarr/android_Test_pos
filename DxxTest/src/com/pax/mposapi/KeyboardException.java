package com.pax.mposapi;

/**
 * <div class="zh">
 * KeyboardException 用于管理 keyboard的异常错误
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
     * Keyboard错误码起始值
     * </div>
     * <div class="en">
     * Keyboard error code start value
     * </div>
     */
    public static final int KEYBOARD_ERR_START = -0x70000;

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
     * 使用指定的exception code构造出KeyboardException对象
     * </div>
     * <div class="en">
     * Create a KeyboardException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">错误码</div>
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
