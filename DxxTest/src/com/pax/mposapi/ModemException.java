package com.pax.mposapi;

/**
 * <div class="zh">
 * ModemException ���ڹ��� MODEM�쳣����
 * </div>
 * <div class="en">
 * ModemException manages the modem exceptions
 * </div>
 *
 */
public class ModemException  extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * <div class="zh">
     * modem��������ʼֵ
     * </div>
     * <div class="en">
     * modem error code start value
     * </div>
     */    
    public static final int MODEM_ERR_START = -0xb0000;
    
    /**
     * <div class="zh">
     * ���ͻ�������
     * </div>
     * <div class="en">
     * tx buffer full
     * </div>
     */       
    public static final int MODEM_ERR_TX_BUFFER_FULL = MODEM_ERR_START - 0x01;
    /**
     * <div class="zh">
     * ���õ绰ռ��
     * </div>
     * <div class="en">
     * Side telephtone has been occupied
     * </div>
     */           
    public static final int MODEM_ERR_SIDE_TEL_OCCUPIED = MODEM_ERR_START - 0x02;
    /**
     * <div class="zh">
     * �绰��δ�Ӻû��ߵ绰ռ��[�ߵ�ѹ��Ϊ0,������]
     * </div>
     * <div class="en">
     * Telephone line is not properly connected, or paralleled line is occupied (Line voltage is not 0, but too low)
     * </div>
     */           
    public static final int MODEM_ERR_NO_LINE_OR_PARALLEL_TEL_OCCUPIED = MODEM_ERR_START - 0x03;
    /**
     * <div class="zh">
     * �绰��δ��[�ߵ�ѹΪ0]
     * </div>
     * <div class="en">
     * Telephone line is not connected (Line voltage is 0)
     * </div>
     */       
    public static final int MODEM_ERR_NO_LINE = MODEM_ERR_START - 0x33;
    /**
     * <div class="zh">
     * ���õ绰�����ߵ绰������(�����ڷ���ת�˹�������ʽ)
     * </div>
     * <div class="en">
     * Both side telephone and paralleled telephone are not busy (only for from automatically sending mode to manually receiving mode).
     * </div>
     */           
    public static final int MODEM_ERR_SIDE_AND_PARALLEL_TEL_IDLE = MODEM_ERR_START - 0x83;
    /**
     * <div class="zh">
     * ��·�ز���ʧ(ͬ������ʧ��)
     * </div>
     * <div class="en">
     * NO CARRIER
     * </div>
     */       
    public static final int MODEM_ERR_NO_CARRIER = MODEM_ERR_START - 0x04;
    /**
     * <div class="zh">
     * ������Ӧ��
     * </div>
     * <div class="en">
     * NO ANSWER
     * </div>
     */     
    public static final int MODEM_ERR_NO_ANSWER = MODEM_ERR_START - 0X05;
//    public static final int MODEM_ERR_STARTED_SENDING_NUMBERS = MODEM_ERR_START - 0x06;
//    public static final int MODEM_ERR_DIALING = MODEM_ERR_START - 0x0a;
//    public static final int MODEM_ERR_IDLE = MODEM_ERR_START - 0x0b;
    /**
     * <div class="zh">
     * �����������󱻾ܾ�(���ջ�����Ϊ��)
     * </div>
     * <div class="en">
     * rx buffer empty
     * </div>
     */      
    public static final int MODEM_ERR_RX_BUFFER_EMPTY = MODEM_ERR_START - 0x0c;
    /**
     * <div class="zh">
     * ������·æ
     * </div>
     * <div class="en">
     * line busy
     * </div>
     */      
    public static final int MODEM_ERR_LINE_BUSY = MODEM_ERR_START - 0x0d;
    /**
     * <div class="zh">
     * (��CPU)���޿��õ�ͨѶ��(������̬����˿���ȫ������ͨѶ��ʹ��)
     * </div>
     * <div class="en">
     * (The main CPU) There is no more communication port available (Two dynamically allocated ports are being used by other communication ports).
     * </div>
     */         
    public static final int MODEM_ERR_NO_PORT_AVAILABLE = MODEM_ERR_START - 0xf0;
    /**
     * <div class="zh">
     * CANCEL������(Modem����ֹ���Ų���)
     * </div>
     * <div class="en">
     * cancelled
     * </div>
     */          
    public static final int MODEM_ERR_CANCELLED = MODEM_ERR_START - 0xfd;
    /**
     * <div class="zh">
     * ��Ч�����ݳ���(len=0 ��len>2048)���ᷢ������
     * </div>
     * <div class="en">
     * Invalid data length (len=0 or len>2048) do not send data.
     * </div>
     */              
    public static final int MODEM_ERR_DATA_LEN = MODEM_ERR_START - 0xfe;
    
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
     * ʹ��ָ����exception code�����PortException����
     * </div>
     * <div class="en">
     * Create a PortException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">������</div>
     * <div class="en">exception code</div>
     */    
    public ModemException(int code) {
    	super(searchMessage(code));
        if (code != -0xFFFF) {
        	exceptionCode = MODEM_ERR_START - code;
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
        	messageId = MODEM_ERR_START - messageId;
        }
        switch (messageId) {
        case MODEM_ERR_TX_BUFFER_FULL:
        	message = "tx buffer full";
        	break;
        case MODEM_ERR_SIDE_TEL_OCCUPIED:
        	message = "Side telephtone has been occupied";
        	break;
        case MODEM_ERR_NO_LINE_OR_PARALLEL_TEL_OCCUPIED:
        	message = "Telephone line is not properly connected, or paralleled line is occupied (Line voltage is not 0, but too low)";
        	break;
        case MODEM_ERR_NO_LINE:
        	message = "Telephone line is not connected (Line voltage is 0)";
        	break;
        case MODEM_ERR_SIDE_AND_PARALLEL_TEL_IDLE:
        	message = "Both side telephone and paralleled telephone are not busy (only for from automatically sending mode to manually receiving mode).";
        	break;
        case MODEM_ERR_NO_CARRIER:
        	message = "NO CARRIER";
        	break;
        case MODEM_ERR_NO_ANSWER:
        	message = "NO ANSWER";
        	break;
//        MODEM_ERR_STARTED_SENDING_NUMBERS
//        MODEM_ERR_DIALING
//        MODEM_ERR_IDLE
        case MODEM_ERR_RX_BUFFER_EMPTY:
        	message = "rx buffer empty";
        	break;
        case MODEM_ERR_LINE_BUSY:
        	message = "line busy";
        	break;
        case MODEM_ERR_NO_PORT_AVAILABLE:
        	message = "(The main CPU) There is no more communication port available (Two dynamically allocated ports are being used by other communication ports).";
        	break;
        case MODEM_ERR_CANCELLED:
        	message = "cancelled";
        	break;
        case MODEM_ERR_DATA_LEN:
        	message = "Invalid data length (len=0 or len>2048) do not send data.";
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
