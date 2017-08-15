package com.pax.mposapi;

/**
 * <div class="zh">
 * PrinterException ���ڹ��� ��ӡ�����쳣����
 * </div>
 * <div class="en">
 * PrinterException manages the printer exceptions
 * </div>
 *
 */
public class PrinterException extends Exception {

    private static final long serialVersionUID = 1L;
    /**
     * <div class="zh">
     * ��ӡ����������ʼֵ
     * </div>
     * <div class="en">
     * printer error code start value
     * </div>
     */
    public static final int PRN_ERR_START = -0x90000;

    /**
     * <div class="zh">
     * ��ӡ��æ
     * </div>
     * <div class="en">
     * busy
     * </div>
     */    
    public static final int PRN_ERR_BUSY = PRN_ERR_START - 0x01;
    /**
     * <div class="zh">
     * ��ӡ��ȱֽ
     * </div>
     * <div class="en">
     * out of paper
     * </div>
     */        
    public static final int PRN_ERR_NO_PAPER = PRN_ERR_START - 0x02;
    /**
     * <div class="zh">
     * ��ӡ���ݰ���ʽ��
     * </div>
     * <div class="en">
     * data format error
     * </div>
     */        
    public static final int PRN_ERR_DATA_FORMAT = PRN_ERR_START - 0x03;
    /**
     * <div class="zh">
     * ��ӡ������
     * </div>
     * <div class="en">
     * printer fault
     * </div>
     */        
    public static final int PRN_ERR_FAULT = PRN_ERR_START - 0x04;
    /**
     * <div class="zh">
     * ��ӡ������
     * </div>
     * <div class="en">
     * overheated
     * </div>
     */        
    public static final int PRN_ERR_OVERHEATED = PRN_ERR_START - 0x08;
    /**
     * <div class="zh">
     * ��ӡδ���
     * </div>
     * <div class="en">
     * print unfinished
     * </div>
     */        
    public static final int PRN_ERR_UNFINISHED = PRN_ERR_START - 0xf0;
    /**
     * <div class="zh">
     * ��ӡ��δװ�ֿ�
     * </div>
     * <div class="en">
     * no such font
     * </div>
     */        
    public static final int PRN_ERR_NO_SUCH_FONT = PRN_ERR_START - 0xfc;
    /**
     * <div class="zh">
     * ���ݰ�����
     * </div>
     * <div class="en">
     * data package too long
     * </div>
     */        
    public static final int PRN_ERR_DATA_TOO_LONG = PRN_ERR_START - 0xfe;
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
     * ʹ��ָ����exception code�����PrinterException����
     * </div>
     * <div class="en">
     * Create a PrinterException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">������</div>
     * <div class="en">exception code</div>
     */
    PrinterException(int code) {
    	super(searchMessage(code));
        if (code != -0xFFFF) {
        	exceptionCode = PRN_ERR_START - code;
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
        	messageId = PRN_ERR_START - messageId;
        }
        switch (messageId) {
        case PRN_ERR_BUSY:
        	message = "busy";
        	break;
        case PRN_ERR_NO_PAPER:
        	message = "out of paper";
        	break;
        case PRN_ERR_DATA_FORMAT:
        	message = "data format error";
        	break;
        case PRN_ERR_FAULT:
        	message = "printer fault";
        	break;
        case PRN_ERR_OVERHEATED:
        	message = "overheated";
        	break;
        case PRN_ERR_UNFINISHED:
        	message = "print unfinished";
        	break;
        case PRN_ERR_NO_SUCH_FONT:
        	message = "no such font";
        	break;
        case PRN_ERR_DATA_TOO_LONG:
        	message = "data package too long";
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
