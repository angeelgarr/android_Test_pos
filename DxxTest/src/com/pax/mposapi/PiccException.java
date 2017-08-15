package com.pax.mposapi;

/**
 * <div class="zh">
 * PiccException ���ڹ��� �ǽӶ��������쳣����
 * </div>
 * <div class="en">
 * PiccException manages the PICC reader exceptions
 * </div>
 *
 */
public class PiccException extends Exception {

    private static final long serialVersionUID = 1L;
	/**
     * <div class="zh">
     * PICC��������ʼֵ
     * </div>
     * <div class="en">
     * PICC error code start value
     * </div>
     */
    public static final int PICC_ERR_START = -0x50000;

    // public static final byte SUCCESS = 0x00 ;
    /**
     * <div class="zh">
     * ��������
     * </div>
     * <div class="en">
     * Parameter error
     * </div>
     */    
    public static final int PICC_ERR_PARAMETER = PICC_ERR_START - 0x01;
    /**
     * <div class="zh">
     * ��Ƶģ��δ����
     * </div>
     * <div class="en">
     * RF module close
     * </div>
     */      
    public static final int PICC_ERR_NOT_OPEN = PICC_ERR_START - 0x02;
    /**
     * <div class="zh">
     * δ��Ѱ����Ƭ(��Ӧ������ָ�����͵Ŀ�Ƭ)
     * </div>
     * <div class="en">
     * No specific card in sensing area
     * </div>
     */    
    public static final int PICC_ERR_NOT_SEARCH_CARD = PICC_ERR_START - 0x03;
    /**
     * <div class="zh">
     * ��Ӧ���ڿ�Ƭ����(����ͨѶ��ͻ)
     * </div>
     * <div class="en">
     * Too much card in sensing area(communication conflict)
     * </div>
     */    
    public static final int PICC_ERR_CARD_TOO_MANY = PICC_ERR_START - 0x04;
    /**
     * <div class="zh">
     * Э�����(��ƬӦ���г���Υ��Э��涨������)
     * </div>
     * <div class="en">
     * Protocol error(The data reeponse from card breaches the agreement)
     * </div>
     */    
    public static final int PICC_ERR_PROTOCOL = PICC_ERR_START - 0x05;
    /**
     * <div class="zh">
     * ��Ƭδ����
     * </div>
     * <div class="en">
     * Card not activated
     * </div>
     */    
    //public static final int PICC_ERR_CARD_NOT_REMOVED = PICC_ERR_START - 0x06;
    public static final int PICC_ERR_CARD_NO_ACTIVATION = PICC_ERR_START - 0x13;
    /**
     * <div class="zh">
     * �࿨��ͻ
     * </div>
     * <div class="en">
     * Multi-card conflict
     * </div>
     */    
    public static final int PICC_ERR_MUTI_CARD = PICC_ERR_START - 0x14;
    /**
     * <div class="zh">
     * ��ʱ����Ӧ
     * </div>
     * <div class="en">
     * No response timeout
     * </div>
     */    
    public static final int PICC_ERR_TIMEOUT = PICC_ERR_START - 0x15;
    /**
     * <div class="zh">
     * Э�����
     * </div>
     * <div class="en">
     * Protocol error
     * </div>
     */    
    public static final int PICC_ERR_PROTOCOL2 = PICC_ERR_START - 0x16;
    /**
     * <div class="zh">
     * ͨ�Ŵ������
     * </div>
     * <div class="en">
     * Communication transmission error
     * </div>
     */    
    public static final int PICC_ERR_IO = PICC_ERR_START - 0x17;
    /**
     * <div class="zh">
     * M1����֤ʧ��
     * </div>
     * <div class="en">
     * M1 Card authentication failure.
     * </div>
     */    
    public static final int PICC_ERR_M1_CARD_VERIFY = PICC_ERR_START - 0x18;
    /**
     * <div class="zh">
     * ����δ��֤
     * </div>
     * <div class="en">
     * Sector is not certified
     * </div>
     */    
    public static final int PICC_ERR_FAN_NOT_VERIFY = PICC_ERR_START - 0x19;
    /**
     * <div class="zh">
     * ��ֵ�����ݸ�ʽ����
     * </div>
     * <div class="en">
     * The data format of value block is incorrect.
     * </div>
     */    
    public static final int PICC_ERR_DATA_BLOCK = PICC_ERR_START - 0x1A;
    /**
     * <div class="zh">
     * ��Ƭ���ڸ�Ӧ����
     * </div>
     * <div class="en">
     * Card is still in sensing area.
     * </div>
     */    
    public static final int PICC_ERR_CARD_SENSE = PICC_ERR_START - 0x1B;
    /**
     * <div class="zh">
     * ��Ƭ״̬����(��A/B������M1���ӿ�, ��M1������PiccIsoCommand�ӿ�)
     * </div>
     * <div class="en">
     * Card status error(If A/B card call M1 card interface, or M1 card call PiccIsoCommand interface)
     * </div>
     */    
    public static final int PICC_ERR_CARD_STATUS = PICC_ERR_START - 0x1C;
    /**
     * <div class="zh">
     * �ӿ�оƬ�����ڻ��쳣
     * </div>
     * <div class="en">
     * Interface chip does not exist or abnormal.
     * </div>
     */    
    public static final int PICC_ERR_NOT_CALL = PICC_ERR_START - 0xff;
    
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
     * ʹ��ָ����exception code�����PiccException����
     * </div>
     * <div class="en">
     * Create a PiccException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">������</div>
     * <div class="en">exception code</div>
     */
    PiccException(int code) {
    	super(searchMessage(code));
        if (code != -0xFFFF) {
        	exceptionCode = PICC_ERR_START - code;
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
        	messageId = PICC_ERR_START - messageId;
        }
        switch (messageId) {

        case PICC_ERR_PARAMETER:
            message = "Parameter error";
            break;
        case PICC_ERR_NOT_OPEN:
            message = "RF module close";
            break;
        case PICC_ERR_NOT_SEARCH_CARD:
            message = "No specific card in sensing area";
            break;
        case PICC_ERR_CARD_TOO_MANY:
            message = "Too much card in sensing area(communication conflict)";
            break;
        case PICC_ERR_PROTOCOL:
            message = "Protocol error(The data reeponse from card breaches the agreement)";
            break;
        case PICC_ERR_CARD_NO_ACTIVATION:
            message = "Card not activated";
            break;
        case PICC_ERR_MUTI_CARD:
            message = "Multi-card conflict";
            break;
        case PICC_ERR_TIMEOUT:
            message = "No response timeout";
            break;
        case PICC_ERR_PROTOCOL2:
            message = "Protocol error";
            break;
        case PICC_ERR_IO:
            message = "Communication transmission error";
            break;
        case PICC_ERR_M1_CARD_VERIFY:
            message = "M1 Card authentication failure.";
            break;
        case PICC_ERR_FAN_NOT_VERIFY:
            message = "Sector is not certified";
            break;
        case PICC_ERR_DATA_BLOCK:
            message = "The data format of value block is incorrect.";
            break;
        case PICC_ERR_CARD_SENSE:
            message = "Card is still in sensing area.";
            break;
        case PICC_ERR_CARD_STATUS:
            message = "Card status error(If A/B card call M1 card interface, or M1 card call PiccIsoCommand interface)";
            break;
        case PICC_ERR_NOT_CALL:
            message = "Interface chip does not exist or abnormal.";
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
