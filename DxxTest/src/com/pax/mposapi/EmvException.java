package com.pax.mposapi;

/**
 * <div class="zh">
 * EmvException 用于管理 EMV的异常错误
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
     * EMV错误码起始值
     * </div>
     * <div class="en">
     * EMV error code start value
     * </div>
     */
    public static final int EMV_ERR_START = -0xa0000;

    /**
     * <div class="zh">IC卡复位失败</div>
     * <div class="en">IC card reset failed</div>
     */
    public static final int EMV_ERR_ICC_RESET     = EMV_ERR_START - 1;         //IC卡复位失败
    /**
     * <div class="zh">IC命令失败</div>
     * <div class="en">IC card command failed</div>
     */
    public static final int EMV_ERR_ICC_CMD       = EMV_ERR_START - 2;         //IC命令失败
    /**
     * <div class="zh">IC卡锁卡    </div>
     * <div class="en">IC card blocked</div>
     */
    public static final int EMV_ERR_ICC_BLOCK     = EMV_ERR_START - 3;         //IC卡锁卡    
       
    /**
     * <div class="zh">IC返回码错误</div>
     * <div class="en">IC card response code error</div>
     */
    public static final int EMV_ERR_RSP      	  = EMV_ERR_START - 4;         //IC返回码错误
    /**
     * <div class="zh">应用已锁</div>
     * <div class="en">application blocked</div>
     */
    public static final int EMV_ERR_APP_BLOCK     = EMV_ERR_START - 5;         //应用已锁
    /**
     * <div class="zh">卡片里没有EMV应用</div>
     * <div class="en">no EMV application supported</div>
     */
    public static final int EMV_ERR_NO_APP        = EMV_ERR_START - 6;         //卡片里没有EMV应用
    /**
     * <div class="zh">用户取消当前操作或交易</div>
     * <div class="en">user cancel</div>
     */
    public static final int EMV_ERR_USER_CANCEL   = EMV_ERR_START - 7;         //用户取消当前操作或交易
    /**
     * <div class="zh">用户操作超时</div>
     * <div class="en">timeout</div>
     */
    public static final int EMV_ERR_TIME_OUT      = EMV_ERR_START - 8;         //用户操作超时
    /**
     * <div class="zh">卡片数据错误</div>
     * <div class="en">card data error</div>
     */
    public static final int EMV_ERR_DATA          = EMV_ERR_START - 9;         //卡片数据错误
    /*
     * should not treat them as exception.
     * 
    public static final int EMV_ERR_NOT_ACCEPT    = EMV_ERR_START - 10;        //交易不接受
    public static final int EMV_ERR_DENIAL        = EMV_ERR_START - 11;        //交易被拒绝
    */
    /**
     * <div class="zh">密钥过期</div>
     * <div class="en">key expired</div>
     */
    public static final int EMV_ERR_KEY_EXP       = EMV_ERR_START - 12;        //密钥过期

    //回调函数或其他函数返回码定义
    //public static final int EMV_ERR_NO_PINPAD     = EMV_ERR_START - 13;        //没有密码键盘或键盘不可用	//不适用
    /**
     * <div class="zh">没有密码或用户忽略了密码输入</div>
     * <div class="en">no pin</div>
     */
    public static final int EMV_ERR_NO_PASSWORD   = EMV_ERR_START - 14;        //没有密码或用户忽略了密码输入
    /**
     * <div class="zh">认证中心密钥校验和错误</div>
     * <div class="en">capk checksum error</div>
     */
    public static final int EMV_ERR_SUM   		  = EMV_ERR_START - 15;        //认证中心密钥校验和错误
    /**
     * <div class="zh">没有找到指定的数据或元素</div>
     * <div class="en">data not found</div>
     */
    public static final int EMV_ERR_NOT_FOUND     = EMV_ERR_START - 16;        //没有找到指定的数据或元素
    /**
     * <div class="zh">指定的数据元素没有数据</div>
     * <div class="en">no specified data</div>
     */
    public static final int EMV_ERR_NO_DATA       = EMV_ERR_START - 17;        //指定的数据元素没有数据
    /**
     * <div class="zh">内存溢出</div>
     * <div class="en">data overflow</div>
     */
    public static final int EMV_ERR_OVERFLOW      = EMV_ERR_START - 18;        //内存溢出

    //读交易日志
    /**
     * <div class="zh">无交易日志</div>
     * <div class="en">no trans log entry</div>
     */
    public static final int EMV_ERR_NO_TRANS_LOG      = EMV_ERR_START - 19;
    /**
     * <div class="zh">记录不存在</div>
     * <div class="en">no record</div>
     */
    public static final int EMV_ERR_RECORD_NOTEXIST   = EMV_ERR_START - 20;
    /**
     * <div class="zh">交易日志项不存在</div>
     * <div class="en">no log item</div>
     */
    public static final int EMV_ERR_LOGITEM_NOTEXIST  = EMV_ERR_START - 21;

    /**
     * <div class="zh">GAC中卡片回送6985, 由应用决定是否fallback</div>
     * <div class="en">icc responded code 6985</div>
     */
    public static final int EMV_ERR_ICC_RSP_6985      = EMV_ERR_START - 22;        // GAC中卡片回送6985, 由应用决定是否fallback

    //for clss
    /**
     * <div class="zh">必须使用其他界面进行交易</div>
     * <div class="en">use contact interface</div>
     */
    public static final int CLSS_ERR_USE_CONTACT    	= EMV_ERR_START - 23;         // 必须使用其他界面进行交易
    /**
     * <div class="zh">文件操作失败</div>
     * <div class="en">file error</div>
     */
    public static final int EMV_ERR_FILE				= EMV_ERR_START - 24;
    /**
     * <div class="zh">应终止交易</div>
     * <div class="en">clss transaction terminated</div>
     */
    public static final int CLSS_ERR_TERMINATE      	= EMV_ERR_START - 25;         // 应终止交易
    /**
     * <div class="zh">交易失败</div>
     * <div class="en">clss transaction failed</div>
     */
    public static final int CLSS_ERR_FAILED				= EMV_ERR_START - 26;         // 交易失败
    /**
     * <div class="zh">交易拒绝</div>
     * <div class="en">clss transaction declined</div>
     */
    public static final int CLSS_ERR_DECLINE			= EMV_ERR_START - 27;

    /**
     * <div class="zh">参数错误</div>
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
     * <div class="zh">wave2 DDA 返回的TLV格式错误</div>
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
     * <div class="zh">需重新选择应用</div>
     * <div class="en">need reselect app</div>
     */
    public static final int CLSS_ERR_RESELECT_APP      	= EMV_ERR_START - 35;    
    /**
     * <div class="zh">卡片过期</div>
     * <div class="en">card expired</div>
     */
    public static final int CLSS_ERR_CARD_EXPIRED      	= EMV_ERR_START - 36;
    /**
     * <div class="zh">没有终端支持的应用且选择PPSE时返回码错误</div>
     * <div class="en">no app and PPSE sel error</div>
     */
    public static final int EMV_ERR_NO_APP_PPSE			= EMV_ERR_START - 37;
    
    /**
     * <div class="zh">CLSS_ERR_USE_VSDC</div>
     * <div class="en">clss use VSDC</div>
     */
    public static final int CLSS_ERR_USE_VSDC			= EMV_ERR_START - 38;// FOR CLSS PBOC [1/12/2010 yingl]	//comment FIXME?
    /**
     * <div class="zh">CVM 导致交易拒绝 (for AE)</div>
     * <div class="en">CVM result in decline for AE</div>
     */
    public static final int CLSS_ERR_CVMDECLINE			= EMV_ERR_START - 39;// CVM result in decline for AE [1/11/2012 zhoujie] comment FIXME?
    /**
     * <div class="zh">GPO 返回  6986</div>
     * <div class="en">GPO response 6986</div>
     */
    public static final int CLSS_REFER_CONSUMER_DEVICE			= EMV_ERR_START - 40;//GPO response 6986

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
     * 使用指定的exception code构造出EmvException对象
     * </div>
     * <div class="en">
     * Create a EmvException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">错误码</div>
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
