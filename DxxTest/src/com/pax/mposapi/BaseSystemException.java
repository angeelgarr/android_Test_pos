package com.pax.mposapi;

/**
 * <div class="zh">
 * BaseSystemException ���ڹ���Base System���쳣����
 * </div>
 * <div class="en">
 * BaseSystemException manages the basic system exceptions
 * </div>
 *
 */
public class BaseSystemException extends Exception {

    private static final long serialVersionUID = 1L;
    
    /**
     * <div class="zh">
     * Base System ��������ʼֵ
     * </div>
     * <div class="en">
     * Base System error code start value
     * </div>
     */
    public static final int BASE_ERR_START = -0x80000;

    /**
     * <div class="zh">
     * ����ʱ���ʽ���� : YY
     * </div>
     * <div class="en">
     * set date time format error: YY
     * </div>
     */
    public static final int BASE_ERR_DATETIME_FORMAT_YY = BASE_ERR_START - 0x01;
    /**
     * <div class="zh">
     * ����ʱ���ʽ���� : MM
     * </div>
     * <div class="en">
     * set date time format error: MM
     * </div>
     */
    public static final int BASE_ERR_DATETIME_FORMAT_MM = BASE_ERR_START - 0x02;
    /**
     * <div class="zh">
     * ����ʱ���ʽ���� : DD
     * </div>
     * <div class="en">
     * set date time format error: DD
     * </div>
     */
    public static final int BASE_ERR_DATETIME_FORMAT_DD = BASE_ERR_START - 0x03;
    /**
     * <div class="zh">
     * ����ʱ���ʽ���� : hh
     * </div>
     * <div class="en">
     * set date time format error: hh
     * </div>
     */
    public static final int BASE_ERR_DATETIME_FORMAT_hh = BASE_ERR_START - 0x04;
    /**
     * <div class="zh">
     * ����ʱ���ʽ���� : mm
     * </div>
     * <div class="en">
     * set date time format error: mm
     * </div>
     */
    public static final int BASE_ERR_DATETIME_FORMAT_mm = BASE_ERR_START - 0x05;
    /**
     * <div class="zh">
     * ����ʱ���ʽ���� : ss
     * </div>
     * <div class="en">
     * set date time format error: ss
     * </div>
     */
    public static final int BASE_ERR_DATETIME_FORMAT_ss = BASE_ERR_START - 0x06;
    /**
     * <div class="zh">
     * ����ʱ��RTC����
     * </div>
     * <div class="en">
     * set date time RTC error
     * </div>
     */
    public static final int BASE_ERR_RTC = BASE_ERR_START - 0xff;
    
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
     * ʹ��ָ����exception code�����BaseSystemException����
     * </div>
     * <div class="en">
     * Create a BaseSystemException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">������</div>
     * <div class="en">exception code</div>
     */
    BaseSystemException(int code) {
    	super(searchMessage(code));
        if (code != -0xFFFF) {
        	exceptionCode = BASE_ERR_START - code;
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
        	messageId = BASE_ERR_START - messageId;
        }
        switch (messageId) {
        	case BASE_ERR_DATETIME_FORMAT_YY:
        		message = "datetime string format YEAR error";
        	break;
        	case BASE_ERR_DATETIME_FORMAT_MM:
        		message = "datetime string format MONTH error";
        	break;
        	case BASE_ERR_DATETIME_FORMAT_DD:
        		message = "datetime string format DAY error";        		
        	break;
        	case BASE_ERR_DATETIME_FORMAT_hh:
        		message = "datetime string format hour error";        		
        	break;
        	case BASE_ERR_DATETIME_FORMAT_mm:
        		message = "datetime string format minute error";        		
        	break;
        	case BASE_ERR_DATETIME_FORMAT_ss:
        		message = "datetime string format second error";        		
        	break;
        	case BASE_ERR_RTC:
        		message = "RTC operation failed";        		
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
