package com.pax.mposapi;

/**
 * <div class="zh">
 * IccException ���ڹ���ICC���쳣����
 * </div>
 * <div class="en">
 * IccException manages the ICC exceptions
 * </div>
 *
 */
public class IccException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * <div class="zh">
     * ICC ��������ʼֵ
     * </div>
     * <div class="en">
     * ICC error code start value
     * </div>
     */
    public static final int ICC_ERR_START = -0x30000;

    /**
     * <div class="zh">
     * IC��ͨ�ų�ʱ
     * </div>
     * <div class="en">
     * ICC communication timeout
     * </div>
     */
    public static final int ICC_ERR_TIMEOUT = ICC_ERR_START - 0x01;
    /**
     * <div class="zh">
     * IC���γ�
     * </div>
     * <div class="en">
     * IC Card pulled out 
     * </div>
     */    
    public static final int ICC_ERR_PULLOUT_CARD = ICC_ERR_START - 0x02;
    /**
     * <div class="zh">
     * ��ż����
     * </div>
     * <div class="en">
     * parity error
     * </div>
     */    
    public static final int ICC_ERR_PARITY = ICC_ERR_START - 0x03;
    /**
     * <div class="zh">
     * ѡ��ͨ������
     * </div>
     * <div class="en">
     * channel error
     * </div>
     */    
    public static final int ICC_ERR_CHANNEL = ICC_ERR_START - 0x04;
    /**
     * <div class="zh">
     * ICC ���ݳ��ȴ���
     * </div>
     * <div class="en">
     * ICC data length error
     * </div>
     */    
    public static final int ICC_ERR_DATA_LEN_OUT = ICC_ERR_START - 0x05;
    /**
     * <div class="zh">
     * ��ƬЭ�����(��ΪT��0��T��1)
     * </div>
     * <div class="en">
     * protocol error (neither T=0 nor T=1)
     * </div>
     */    
    public static final int ICC_ERR_PROTOCOL = ICC_ERR_START - 0x06;
    /**
     * <div class="zh">
     * û�и�λ��Ƭ
     * </div>
     * <div class="en">
     * card not reset
     * </div>
     */    
    public static final int ICC_ERR_NO_RESET_CARD = ICC_ERR_START - 0x07;
    /**
     * <div class="zh">
     * ����ͨ�Ż�û�ϵ�
     * </div>
     * <div class="en">
     * cannot communicate or not power on
     * </div>
     */    
    public static final int ICC_ERR_NOT_CALL = ICC_ERR_START - 0xff;
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
     * ʹ��ָ����exception code�����IccException����
     * </div>
     * <div class="en">
     * Create a IccException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">������</div>
     * <div class="en">exception code</div>
     */
    IccException(int code) {
    	super(searchMessage(code));
        if (code != -0xFFFF) {
        	exceptionCode = ICC_ERR_START - code;
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
        	messageId = ICC_ERR_START - messageId;
        }
        switch (messageId) {
        case ICC_ERR_TIMEOUT:
            message = "timeout";
            break;
        case ICC_ERR_PULLOUT_CARD:
            message = "card pull out";
            break;
        case ICC_ERR_PARITY:
            message = "parity error";
            break;
        case ICC_ERR_CHANNEL:
            message = "choose channel error";
            break;
        case ICC_ERR_DATA_LEN_OUT:
            message = "data len out";
            break;
        case ICC_ERR_PROTOCOL:
            message = "card protocol err";
            break;
        case ICC_ERR_NO_RESET_CARD:
            message = "no reset card";
            break;
        case ICC_ERR_NOT_CALL:
            message = "NOT call/NO battery/NO card plug in";
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
