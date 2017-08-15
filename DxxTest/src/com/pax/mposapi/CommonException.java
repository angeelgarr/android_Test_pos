package com.pax.mposapi;

/**
 * <div class="zh">
 * CommonException 用于管理通用异常错误
 * </div>
 * <div class="en">
 * CommonException manages the common exceptions
 * </div>
 *
 */
public class CommonException extends Exception{
	
    private static final long serialVersionUID = 1L;
    
    public static final int COMMON_ERR_START = -0x100000;
    
    /**
     * common data error
     */
    public static final int COMMON_ERR_DATA						= COMMON_ERR_START - 0x1;
    /**
     * data type error
     */
    public static final int COMMON_ERR_DATA_TYPE				= COMMON_ERR_START - 0x2;
    /**
     * data verify error
     */
    public static final int COMMON_ERR_DATA_VERIFY				= COMMON_ERR_START - 0x3;
    /**
     * data decryption error
     */
    public static final int COMMON_ERR_DATA_DECRYPT				= COMMON_ERR_START - 0x4;
    /**
     * data encryption error
     */
    public static final int COMMON_ERR_DATA_ENCRYPT				= COMMON_ERR_START - 0x5;
    /**
     * invalid data source
     */
    public static final int COMMON_ERR_DATA_INVALID_SOURCE		= COMMON_ERR_START - 0x6;
    /**
     * data integrity error
     */
    public static final int COMMON_ERR_DATA_INTEGRITY			= COMMON_ERR_START - 0x7;
    /**
     * data read error
     */
    public static final int COMMON_ERR_DATA_READ				= COMMON_ERR_START - 0x8;
    /**
     * data write error
     */
    public static final int COMMON_ERR_DATA_WRITE				= COMMON_ERR_START - 0x9;
    
    
    
    public static final int COMMON_ERR_END = -0x10FFFF;
    
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
     * 使用指定的exception code构造出CommonException对象
     * </div>
     * <div class="en">
     * Create a CommonException instance with an exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">错误码</div>
     * <div class="en">exception code</div>
     */
    public CommonException(int code) {
    	super(searchMessage(code));
    	exceptionCode = code;
    }

    static boolean isCommonExceptionCode(int code) {
    	return ((code <= COMMON_ERR_START) && (code > COMMON_ERR_END));
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
	        case COMMON_ERR_DATA:
	        {
	        	message = "common data error";
	        	break;
	        }
	        
	        case COMMON_ERR_DATA_TYPE:
	        {
	        	message = "data type error";
	        	break;
	        }
	
	        case COMMON_ERR_DATA_VERIFY:
	        {
	        	message = "data verify error";
	        	break;
	        }
	
	        case COMMON_ERR_DATA_DECRYPT:
	        {
	        	message = "data decryption error";
	        	break;
	        }
	
	        case COMMON_ERR_DATA_ENCRYPT:
	        {
	        	message = "data encryption error";
	        	break;
	        }

	        case COMMON_ERR_DATA_INVALID_SOURCE:
	        {
	        	message = "invalid data source";
	        	break;
	        }
	
	        case COMMON_ERR_DATA_INTEGRITY:
	        {
	        	message = "data integrity error";
	        	break;
	        }
	
	        case COMMON_ERR_DATA_READ:
	        {
	        	message = "data read error";
	        	break;
	        }
	        
	        case COMMON_ERR_DATA_WRITE:
	        {
	        	message = "data write error";
	        	break;
	        }
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
