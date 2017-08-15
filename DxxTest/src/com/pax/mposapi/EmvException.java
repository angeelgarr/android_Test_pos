package com.pax.mposapi;

/**
 * <div class="zh">
 * EmvException ���ڹ��� EMV���쳣����
 * </div>
 * <div class="en">
 * EmvException manages the EMV exceptions
 * </div>
 *
 */
public class EmvException extends Exception {

    private static final long serialVersionUID = 1L;
    /**
     * <div class="zh">
     * EMV��������ʼֵ
     * </div>
     * <div class="en">
     * EMV error code start value
     * </div>
     */
    public static final int EMV_ERR_START = -0xa0000;

    /**
     * <div class="zh">IC����λʧ��</div>
     * <div class="en">IC card reset failed</div>
     */
    public static final int EMV_ERR_ICC_RESET     = EMV_ERR_START - 1;         //IC����λʧ��
    /**
     * <div class="zh">IC����ʧ��</div>
     * <div class="en">IC card command failed</div>
     */
    public static final int EMV_ERR_ICC_CMD       = EMV_ERR_START - 2;         //IC����ʧ��
    /**
     * <div class="zh">IC������    </div>
     * <div class="en">IC card blocked</div>
     */
    public static final int EMV_ERR_ICC_BLOCK     = EMV_ERR_START - 3;         //IC������    
       
    /**
     * <div class="zh">IC���������</div>
     * <div class="en">IC card response code error</div>
     */
    public static final int EMV_ERR_RSP      	  = EMV_ERR_START - 4;         //IC���������
    /**
     * <div class="zh">Ӧ������</div>
     * <div class="en">application blocked</div>
     */
    public static final int EMV_ERR_APP_BLOCK     = EMV_ERR_START - 5;         //Ӧ������
    /**
     * <div class="zh">��Ƭ��û��EMVӦ��</div>
     * <div class="en">no EMV application supported</div>
     */
    public static final int EMV_ERR_NO_APP        = EMV_ERR_START - 6;         //��Ƭ��û��EMVӦ��
    /**
     * <div class="zh">�û�ȡ����ǰ��������</div>
     * <div class="en">user cancel</div>
     */
    public static final int EMV_ERR_USER_CANCEL   = EMV_ERR_START - 7;         //�û�ȡ����ǰ��������
    /**
     * <div class="zh">�û�������ʱ</div>
     * <div class="en">timeout</div>
     */
    public static final int EMV_ERR_TIME_OUT      = EMV_ERR_START - 8;         //�û�������ʱ
    /**
     * <div class="zh">��Ƭ���ݴ���</div>
     * <div class="en">card data error</div>
     */
    public static final int EMV_ERR_DATA          = EMV_ERR_START - 9;         //��Ƭ���ݴ���
    /*
     * should not treat them as exception.
     * 
    public static final int EMV_ERR_NOT_ACCEPT    = EMV_ERR_START - 10;        //���ײ�����
    public static final int EMV_ERR_DENIAL        = EMV_ERR_START - 11;        //���ױ��ܾ�
    */
    /**
     * <div class="zh">��Կ����</div>
     * <div class="en">key expired</div>
     */
    public static final int EMV_ERR_KEY_EXP       = EMV_ERR_START - 12;        //��Կ����

    //�ص��������������������붨��
    //public static final int EMV_ERR_NO_PINPAD     = EMV_ERR_START - 13;        //û��������̻���̲�����	//������
    /**
     * <div class="zh">û��������û���������������</div>
     * <div class="en">no pin</div>
     */
    public static final int EMV_ERR_NO_PASSWORD   = EMV_ERR_START - 14;        //û��������û���������������
    /**
     * <div class="zh">��֤������ԿУ��ʹ���</div>
     * <div class="en">capk checksum error</div>
     */
    public static final int EMV_ERR_SUM   		  = EMV_ERR_START - 15;        //��֤������ԿУ��ʹ���
    /**
     * <div class="zh">û���ҵ�ָ�������ݻ�Ԫ��</div>
     * <div class="en">data not found</div>
     */
    public static final int EMV_ERR_NOT_FOUND     = EMV_ERR_START - 16;        //û���ҵ�ָ�������ݻ�Ԫ��
    /**
     * <div class="zh">ָ��������Ԫ��û������</div>
     * <div class="en">no specified data</div>
     */
    public static final int EMV_ERR_NO_DATA       = EMV_ERR_START - 17;        //ָ��������Ԫ��û������
    /**
     * <div class="zh">�ڴ����</div>
     * <div class="en">data overflow</div>
     */
    public static final int EMV_ERR_OVERFLOW      = EMV_ERR_START - 18;        //�ڴ����

    //��������־
    /**
     * <div class="zh">�޽�����־</div>
     * <div class="en">no trans log entry</div>
     */
    public static final int EMV_ERR_NO_TRANS_LOG      = EMV_ERR_START - 19;
    /**
     * <div class="zh">��¼������</div>
     * <div class="en">no record</div>
     */
    public static final int EMV_ERR_RECORD_NOTEXIST   = EMV_ERR_START - 20;
    /**
     * <div class="zh">������־�����</div>
     * <div class="en">no log item</div>
     */
    public static final int EMV_ERR_LOGITEM_NOTEXIST  = EMV_ERR_START - 21;

    /**
     * <div class="zh">GAC�п�Ƭ����6985, ��Ӧ�þ����Ƿ�fallback</div>
     * <div class="en">icc responded code 6985</div>
     */
    public static final int EMV_ERR_ICC_RSP_6985      = EMV_ERR_START - 22;        // GAC�п�Ƭ����6985, ��Ӧ�þ����Ƿ�fallback

    //for clss
    /**
     * <div class="zh">����ʹ������������н���</div>
     * <div class="en">use contact interface</div>
     */
    public static final int CLSS_ERR_USE_CONTACT    	= EMV_ERR_START - 23;         // ����ʹ������������н���
    /**
     * <div class="zh">�ļ�����ʧ��</div>
     * <div class="en">file error</div>
     */
    public static final int EMV_ERR_FILE				= EMV_ERR_START - 24;
    /**
     * <div class="zh">Ӧ��ֹ����</div>
     * <div class="en">clss transaction terminated</div>
     */
    public static final int CLSS_ERR_TERMINATE      	= EMV_ERR_START - 25;         // Ӧ��ֹ����
    /**
     * <div class="zh">����ʧ��</div>
     * <div class="en">clss transaction failed</div>
     */
    public static final int CLSS_ERR_FAILED				= EMV_ERR_START - 26;         // ����ʧ��
    /**
     * <div class="zh">���׾ܾ�</div>
     * <div class="en">clss transaction declined</div>
     */
    public static final int CLSS_ERR_DECLINE			= EMV_ERR_START - 27;

    /**
     * <div class="zh">��������</div>
     * <div class="en">parameter error</div>
     */
    public static final int EMV_ERR_PARAM			  = EMV_ERR_START - 30;    
    //public static final int CLSS_ERR_PARAM_ERR			= EMV_ERR_START - 30;	//it's EMV_ERR_PARAM
    
    /**
     * <div class="zh">CLSS_ERR_WAVE2_OVERSEA</div>
     * <div class="en">CLSS_ERR_WAVE2_OVERSEA</div>
     */
    public static final int CLSS_ERR_WAVE2_OVERSEA      = EMV_ERR_START - 31;	//comment FIXME?
    /**
     * <div class="zh">wave2 DDA ���ص�TLV��ʽ����</div>
     * <div class="en">wave2 DDA response TLV format error</div>
     */
    public static final int CLSS_ERR_WAVE2_TERMINATED   = EMV_ERR_START - 32;
    /**
     * <div class="zh">CLSS_ERR_WAVE2_US_CARD</div>
     * <div class="en">CLSS_ERR_WAVE2_US_CARD</div>
     */
    public static final int CLSS_ERR_WAVE2_US_CARD      = EMV_ERR_START - 33;	//comment FIXME?
    /**
     * <div class="zh">CLSS_ERR_WAVE3_INS_CARD</div>
     * <div class="en">CLSS_ERR_WAVE3_INS_CARD</div>
     */
    public static final int CLSS_ERR_WAVE3_INS_CARD   	= EMV_ERR_START - 34;	//comment FIXME?
    
    /**
     * <div class="zh">������ѡ��Ӧ��</div>
     * <div class="en">need reselect app</div>
     */
    public static final int CLSS_ERR_RESELECT_APP      	= EMV_ERR_START - 35;    
    /**
     * <div class="zh">��Ƭ����</div>
     * <div class="en">card expired</div>
     */
    public static final int CLSS_ERR_CARD_EXPIRED      	= EMV_ERR_START - 36;
    /**
     * <div class="zh">û���ն�֧�ֵ�Ӧ����ѡ��PPSEʱ���������</div>
     * <div class="en">no app and PPSE sel error</div>
     */
    public static final int EMV_ERR_NO_APP_PPSE			= EMV_ERR_START - 37;
    
    /**
     * <div class="zh">CLSS_ERR_USE_VSDC</div>
     * <div class="en">clss use VSDC</div>
     */
    public static final int CLSS_ERR_USE_VSDC			= EMV_ERR_START - 38;// FOR CLSS PBOC [1/12/2010 yingl]	//comment FIXME?
    /**
     * <div class="zh">CVM ���½��׾ܾ� (for AE)</div>
     * <div class="en">CVM result in decline for AE</div>
     */
    public static final int CLSS_ERR_CVMDECLINE			= EMV_ERR_START - 39;// CVM result in decline for AE [1/11/2012 zhoujie] comment FIXME?
    /**
     * <div class="zh">GPO ����  6986</div>
     * <div class="en">GPO response 6986</div>
     */
    public static final int CLSS_REFER_CONSUMER_DEVICE			= EMV_ERR_START - 40;//GPO response 6986

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
     * ʹ��ָ����exception code�����EmvException����
     * </div>
     * <div class="en">
     * Create a EmvException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">������</div>
     * <div class="en">exception code</div>
     */
    public EmvException(int code) {
    	super(searchMessage(code));
        if (code != -0xFFFF) {
        	exceptionCode = EMV_ERR_START + code;
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
        	messageId = EMV_ERR_START + messageId;
        }
        switch (messageId) {
	        case EMV_ERR_ICC_RESET:
	        	message = "icc reset error";
	        	break;
	        case EMV_ERR_ICC_CMD:
	        	message = "icc cmd error";
	        	break;
	        case EMV_ERR_ICC_BLOCK:
	        	message = "icc blocked";
	        	break;	
	        case EMV_ERR_RSP:
	        	message = "icc response code error";
	        	break;
	        case EMV_ERR_APP_BLOCK:
	        	message = "app blocked";
	        	break;
	        case EMV_ERR_NO_APP:
	        	message = "no app";
	        	break;
	        case EMV_ERR_USER_CANCEL:
	        	message = "user cancel";
	        	break;
	        case EMV_ERR_TIME_OUT:
	        	message = "time out";
	        	break;
	        case EMV_ERR_DATA:
	        	message = "card data error";
	        	break;
	        /*
	        case EMV_ERR_NOT_ACCEPT:
	        	message = "transaction not accepted";
	        	break;
	        case EMV_ERR_DENIAL:
	        	message = "transaction denied";
	        	break;
	        */
	        case EMV_ERR_KEY_EXP:
	        	message = "key expired";
	        	break;		
	        /*
	        case EMV_ERR_NO_PINPAD:
	        	message = "no pinpad";
	        	break;
	        */
	        case EMV_ERR_NO_PASSWORD:
	        	message = "no pin";
	        	break;
	        case EMV_ERR_SUM:
	        	message = "capk checksum error";
	        	break;
	        case EMV_ERR_NOT_FOUND:
	        	message = "data not found";
	        	break;
	        case EMV_ERR_NO_DATA:
	        	message = "no specified data";
	        	break;
	        case EMV_ERR_OVERFLOW:
	        	message = "data overflow";
	        	break;	
	        case EMV_ERR_NO_TRANS_LOG:
	        	message = "no trans log entry";
	        	break;
	        case EMV_ERR_RECORD_NOTEXIST:
	        	message = "no record";
	        	break;
	        case EMV_ERR_LOGITEM_NOTEXIST:
	        	message = "no log item";
	        	break;	
	        case EMV_ERR_ICC_RSP_6985:
	        	message = "icc response code 6985";
	        	break;
	        	
	        case CLSS_ERR_USE_CONTACT:
	        	message = "use contact interface";
	        	break;
	        case EMV_ERR_FILE:
	        	message = "emv file error";
	        	break;	        
	        case CLSS_ERR_TERMINATE:
	        	message = "clss transaction terminated";
		        break;
	        case CLSS_ERR_FAILED:
	        	message = "clss transaction failed";
		        break;
	        case CLSS_ERR_DECLINE:
	        	message = "clss transaction declined";
	        	break;
	        case EMV_ERR_PARAM:
	        	message = "param error";
	        	break;
	        	
	        /*
	         * case CLSS_ERR_PARAM_ERR:

	        	message = "param error";
		        break;
	         */

	        case CLSS_ERR_WAVE2_OVERSEA:
	        	message = "CLSS_ERR_WAVE2_OVERSEA";		//FIXME?
		        break;
	        case CLSS_ERR_WAVE2_TERMINATED:
	        	message = "wave2 DDA response TLV format error";
	        	break;
	        case CLSS_ERR_WAVE2_US_CARD:
	        	message = "CLSS_ERR_WAVE2_US_CARD";		//FIXME?
		        break;
	        case CLSS_ERR_WAVE3_INS_CARD:
	        	message = "CLSS_ERR_WAVE3_INS_CARD";		//FIXME?
		        break;

	        case CLSS_ERR_RESELECT_APP:
	        	message = "need reselect app";
		        break;
	        case CLSS_ERR_CARD_EXPIRED:
	        	message = "card expired";
	        	break;
	        
	        case EMV_ERR_NO_APP_PPSE:
	        	message = "no app and PPSE sel error";
	        	break;
	        	
	        case CLSS_ERR_USE_VSDC:
	        	message = "use VSDC";
	        	break;
	        	
	        case CLSS_ERR_CVMDECLINE:
	        	message = "CVM result in decline for AE";
	        	break;
	        	
	        case CLSS_REFER_CONSUMER_DEVICE:
	        	message = "GPO response 6986";
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
