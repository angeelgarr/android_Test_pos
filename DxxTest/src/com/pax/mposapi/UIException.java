package com.pax.mposapi;

/**
 * <div class="zh">
 * UIException 用于管理 UI的异常错误
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
     * UI错误码起始值
     * </div>
     * <div class="en">
     * UI error code start value
     * </div>
     */
    public static final int UI_ERR_START = -0x60000;
    /**
     * <div class="zh">
     * UI参数错误
     * </div>
     * <div class="en">
     * UI param error
     * </div>
     */
    public static final int UI_ERR_PARAM = UI_ERR_START - 1;
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
     * 使用指定的exception code构造出UIException对象
     * </div>
     * <div class="en">
     * Create a UIException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">错误码</div>
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
