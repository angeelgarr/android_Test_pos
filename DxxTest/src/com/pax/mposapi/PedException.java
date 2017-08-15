package com.pax.mposapi;

/**
 * <div class="zh">
 * PedException 用于管理 PED的异常错误
 * </div>
 * <div class="en">
 * PedException manages the PED exceptions
 * </div>
 *
 */
public class PedException extends Exception {

    private static final long serialVersionUID = 1L;
	/**
     * <div class="zh">
     * PED错误码起始值
     * </div>
     * <div class="en">
     * PED error code start value
     * </div>
     */
    public static final int PED_ERR_START = -0x10000;
    /**
     * <div class="zh">
     * 密钥不存在
     * </div>
     * <div class="en">
     * Key does not exist.
     * </div>
     */
    public static final int PED_ERR_NO_KEY = PED_ERR_START - 301;
    /**
     * <div class="zh">
     * 密钥索引错,参数索引不在范围内
     * </div>
     * <div class="en">
     * Key index error, parameter index is not in the range.
     * </div>
     */    
    public static final int PED_ERR_KEYIDX = PED_ERR_START - 302;
    /**
     * <div class="zh">
     * 密钥写入时,源密钥的层次比目的密钥低
     * </div>
     * <div class="en">
     * When key is written, the source key level is lower than the destination level.
     * </div>
     */   
    public static final int PED_ERR_DERIVE = PED_ERR_START - 303;
    /**
     * <div class="zh">
     * 密钥验证失败
     * </div>
     * <div class="en">
     * Key verification failed.
     * </div>
     */   
    public static final int PED_ERR_CHECK_KEY_FAIL = PED_ERR_START - 304;
    /**
     * <div class="zh">
     * 没输入PIN
     * </div>
     * <div class="en">
     * No PIN input.
     * </div>
     */       
    public static final int PED_ERR_NO_PIN_INPUT = PED_ERR_START - 305;
    /**
     * <div class="zh">
     * 取消输入PIN
     * </div>
     * <div class="en">
     * Cancel to enter PIN.
     * </div>
     */  
    public static final int PED_ERR_INPUT_CANCEL = PED_ERR_START - 306;
    /**
     * <div class="zh">
     * 函数调用小于最小间隔时间
     * </div>
     * <div class="en">
     * Calling function interval is less than minimum interval time.
     * </div>
     */      
    public static final int PED_ERR_WAIT_INTERVAL = PED_ERR_START - 307;
    /**
     * <div class="zh">
     * KCV模式错,不支持
     * </div>
     * <div class="en">
     * KCV mode error, do not support.
     * </div>
     */          
    public static final int PED_ERR_CHECK_MODE = PED_ERR_START - 308;
    /**
     * <div class="zh">
     * 无权使用该密钥,当出现密钥标签不对,或者写入密钥时,源密钥类型的值大于目的密钥类型,都会返回该密钥
     * </div>
     * <div class="en">
     * Not allowed to use the key. When key label is not correct or source key type is bigger than destination key type, PED will return this code.
     * </div>
     */           
    public static final int PED_ERR_NO_RIGHT_USE = PED_ERR_START - 309;
    /**
     * <div class="zh">
     * 密钥类型错
     * </div>
     * <div class="en">
     * Key type error
     * </div>
     */               
    public static final int PED_ERR_KEY_TYPE = PED_ERR_START - 310;
    /**
     * <div class="zh">
     * 期望PIN的长度字符串错
     * </div>
     * <div class="en">
     * Expected PIN length string error
     * </div>
     */  
    public static final int PED_ERR_EXPLEN = PED_ERR_START - 311;
    /**
     * <div class="zh">
     * 目的密钥索引错,不在范围内
     * </div>
     * <div class="en">
     * Destination key index error
     * </div>
     */  
    public static final int PED_ERR_DSTKEY_IDX = PED_ERR_START - 312;
    /**
     * <div class="zh">
     * 源密钥索引错,不在范围内
     * </div>
     * <div class="en">
     * Source key index error
     * </div>
     */  
    public static final int PED_ERR_SRCKEY_IDX = PED_ERR_START - 313;
    /**
     * <div class="zh">
     * 密钥长度错
     * </div>
     * <div class="en">
     * Key length error
     * </div>
     */  
    public static final int PED_ERR_KEY_LEN = PED_ERR_START - 314;
    /**
     * <div class="zh">
     * 输入PIN超时
     * </div>
     * <div class="en">
     * PIN input timeout
     * </div>
     */     
    public static final int PED_ERR_INPUT_TIMEOUT = PED_ERR_START - 315;
    /**
     * <div class="zh">
     * IC卡不存在
     * </div>
     * <div class="en">
     * IC card does not exist
     * </div>
     */     
    public static final int PED_ERR_NO_ICC = PED_ERR_START - 316;
    /**
     * <div class="zh">
     * IC卡未初始化
     * </div>
     * <div class="en">
     * IC card is not intilized
     * </div>
     */     
    public static final int PED_ERR_ICC_NO_INIT = PED_ERR_START - 317;
    /**
     * <div class="zh">
     * DUKPT组索引号错
     * </div>
     * <div class="en">
     * DUKPT index error
     * </div>
     */         
    public static final int PED_ERR_GROUP_IDX = PED_ERR_START - 318;
    /**
     * <div class="zh">
     * 指针参数非法为空
     * </div>
     * <div class="en">
     * Pointer parameter error
     * </div>
     */       
    public static final int PED_ERR_PARAM_PTR_NULL = PED_ERR_START - 319;
    /**
     * <div class="zh">
     * PED已锁
     * </div>
     * <div class="en">
     * PED locked
     * </div>
     */    
    public static final int PED_ERR_LOCKED = PED_ERR_START - 320;
    /**
     * <div class="zh">
     * PED通用错误
     * </div>
     * <div class="en">
     * PED general error
     * </div>
     */  
    public static final int PED_ERROR = PED_ERR_START - 321;
    /**
     * <div class="zh">
     * 没有空闲的缓冲
     * </div>
     * <div class="en">
     * No free buffer
     * </div>
     */      
    public static final int PED_ERR_NOMORE_BUF = PED_ERR_START - 322;
    /**
     * <div class="zh">
     * 需要取得高级权限
     * </div>
     * <div class="en">
     * Not administration
     * </div>
     */      
    public static final int PED_ERR_NEED_ADMIN = PED_ERR_START - 323;
    /**
     * <div class="zh">
     * DUKPT已经溢出
     * </div>
     * <div class="en">
     * DUKPT overflow
     * </div>
     */        
    public static final int PED_ERR_DUKPT_OVERFLOW = PED_ERR_START - 324;
    /**
     * <div class="zh">
     * KCV 校验失败
     * </div>
     * <div class="en">
     * KCV check error
     * </div>
     */           
    public static final int PED_ERR_KCV_CHECK_FAIL = PED_ERR_START - 325;
    /**
     * <div class="zh">
     * 源密钥类型错
     * </div>
     * <div class="en">
     * When key is written, the ID of source key does not match the type of source key.
     * </div>
     */   
    public static final int PED_ERR_SRCKEY_TYPE = PED_ERR_START - 326;
    /**
     * <div class="zh">
     * 命令不支持
     * </div>
     * <div class="en">
     * Command not supprt
     * </div>
     */       
    public static final int PED_ERR_UNSPT_CMD = PED_ERR_START - 327;
    /**
     * <div class="zh">
     * 通讯错误
     * </div>
     * <div class="en">
     * Communication error
     * </div>
     */        
    public static final int PED_ERR_COMM = PED_ERR_START - 328;
    /**
     * <div class="zh">
     * 没有用户认证公钥
     * </div>
     * <div class="en">
     * No user authentication public key
     * </div>
     */      
    public static final int PED_ERR_NO_UAPUK = PED_ERR_START - 329;
    /**
     * <div class="zh">
     * 取系统敏感服务失败
     * </div>
     * <div class="en">
     * Administration error
     * </div>
     */          
    public static final int PED_ERR_ADMIN = PED_ERR_START - 330;
    /**
     * <div class="zh">
     * PED处于下载非激活状态
     * </div>
     * <div class="en">
     * PED download inactive
     * </div>
     */       
    public static final int PED_ERR_DOWNLOAD_INACTIVE = PED_ERR_START - 331;
    /**
     * <div class="zh">
     * KCV 奇校验失败
     * </div>
     * <div class="en">
     * KCV parity check fail
     * </div>
     */           
    public static final int PED_ERR_KCV_ODD_CHECK_FAIL = PED_ERR_START - 332;
    /**
     * <div class="zh">
     * 读取PED数据失败
     * </div>
     * <div class="en">
     * Read PED data fail
     * </div>
     */      
    public static final int PED_ERR_PED_DATA_RW_FAIL = PED_ERR_START - 333;
    /**
     * <div class="zh">
     * 卡操作错误(脱机明文、密文密码验证)
     * </div>
     * <div class="en">
     * ICC operation fail
     * </div>
     */        
    public static final int PED_ERR_ICC_CMD = PED_ERR_START - 334;
    /**
     * <div class="zh">
     * 用户按CLEAR键退出输入PIN
     * </div>
     * <div class="en">
     * Pressing CLEAR to exit input
     * </div>
     */      
    public static final int PED_ERR_INPUT_CLEAR = PED_ERR_START - 339;
    /**
     * <div class="zh">
     * PED存储空间不足
     * </div>
     * <div class="en">
     * PED No enough space
     * </div>
     */   
    public static final int PED_ERR_NO_FREE_FLASH = PED_ERR_START - 343;
    /**
     * <div class="zh">
     * DUKPT KSN需要先加1
     * </div>
     * <div class="en">
     * DUKPT need inc ksn
     * </div>
     */     
    public static final int PED_ERR_DUKPT_NEED_INC_KSN = PED_ERR_START - 344;
    /**
     * <div class="zh">
     * KCV MODE错误
     * </div>
     * <div class="en">
     * KCV MODE error
     * </div>
     */     
    public static final int PED_ERR_KCV_MODE = PED_ERR_START - 345;
    /**
     * <div class="zh">
     * 无 KCV
     * </div>
     * <div class="en">
     * NO KCV
     * </div>
     */ 
    public static final int PED_ERR_DUKPT_NO_KCV = PED_ERR_START - 346;
    /**
     * <div class="zh">
     * 按FN/ATM4键取消PIN输入
     * </div>
     * <div class="en">
     * press FN/ATM4 KEY for PIN input
     * </div>
     */ 
    public static final int PED_ERR_PIN_BYPASS_BYFUNKEY = PED_ERR_START - 347;
    /**
     * <div class="zh">
     * 数据MAC校验错
     * </div>
     * <div class="en">
     * verify MAC error
     * </div>
     */ 
    public static final int PED_ERR_MAC = PED_ERR_START - 348;
    /**
     * <div class="zh">
     * 数据CRC校验错
     * </div>
     * <div class="en">
     * verify CRC error
     * </div>
     */     
    public static final int PED_ERR_CRC = PED_ERR_START - 349;

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
     * 使用指定的exception code构造出PedException对象
     * </div>
     * <div class="en">
     * Create a PedException instance with a exception code
     * </div>
     * 
     * @param code 
     * <div class="zh">错误码</div>
     * <div class="en">exception code</div>
     */
    PedException(int code) {
    	super(searchMessage(code));
        if (code != -0xFFFF) {
        	exceptionCode = PED_ERR_START + code;
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
        	messageId = PED_ERR_START + messageId;
        }
        switch (messageId) {
        case PED_ERR_NO_KEY:
            message = "Key does not exist.";
            break;
        case PED_ERR_KEYIDX:
            message = "Key index error, parameter index is not in the range.";
            break;
        case PED_ERR_DERIVE:
            message = "When key is written, the source key level is lower than the destination level.";
            break;
        case PED_ERR_CHECK_KEY_FAIL:
            message = "Key verification failed.";
            break;
        case PED_ERR_NO_PIN_INPUT:
            message = "No PIN input";
            break;
        case PED_ERR_INPUT_CANCEL:
            message = "Cancel to enter PIN.";
            break;
        case PED_ERR_WAIT_INTERVAL:
            message = "Calling function interval is less than minimum interval time.";
            break;
        case PED_ERR_CHECK_MODE:
            message = "KCV mode error, do not support.";
            break;
        case PED_ERR_NO_RIGHT_USE:
            message = "Not allowed to use the key. When key label is not correct or source key type is bigger than destination key type, PED will return this code.";
            break;
        case PED_ERR_KEY_TYPE:
            message = "Key type error";
            break;
        case PED_ERR_EXPLEN:
            message = "Expected PIN length string error";
            break;
        case PED_ERR_DSTKEY_IDX:
            message = "Destination key index error";
            break;
        case PED_ERR_SRCKEY_IDX:
            message = "Source key index error";
            break;
        case PED_ERR_KEY_LEN:
            message = "Key length error";
            break;
        case PED_ERR_INPUT_TIMEOUT:
            message = "PIN input timeout";
            break;
        case PED_ERR_NO_ICC:
            message = "IC card does not exist";
            break;
        case PED_ERR_ICC_NO_INIT:
            message = "IC card is not initialized";
            break;
        case PED_ERR_GROUP_IDX:
            message = "DUKPT index error";
            break;
        case PED_ERR_PARAM_PTR_NULL:
            message = "Pointer parameter error";
            break;
        case PED_ERR_LOCKED:
            message = "PED locked";
            break;
        case PED_ERROR:
            message = "PED general error";
            break;
        case PED_ERR_NOMORE_BUF:
            message = "No free buffer";
            break;
        case PED_ERR_NEED_ADMIN:
            message = "Not administration";
            break;
        case PED_ERR_DUKPT_OVERFLOW:
            message = "DUKPT overflow";
            break;
        case PED_ERR_KCV_CHECK_FAIL:
            message = "KCV check error";
            break;
        case PED_ERR_SRCKEY_TYPE:
            message = "When key is written, the ID of source key does not match the type of source key.";
            break;
        case PED_ERR_UNSPT_CMD:
            message = "Command not support";
            break;
        case PED_ERR_COMM:
            message = "Communication error";
            break;
        case PED_ERR_NO_UAPUK:
            message = "No user authentication public key";
            break;
        case PED_ERR_ADMIN:
            message = "Administration error";
            break;
        case PED_ERR_DOWNLOAD_INACTIVE:
            message = "PED download inactive";
            break;
        case PED_ERR_KCV_ODD_CHECK_FAIL:
            message = "KCV parity check fail";
            break;
        case PED_ERR_PED_DATA_RW_FAIL:
            message = "Read PED data fail";
            break;
        case PED_ERR_ICC_CMD:
            message = "ICC operation fail";
            break;
        case PED_ERR_INPUT_CLEAR:
            message = "Pressing CLEAR to exit input";
            break;
        case PED_ERR_NO_FREE_FLASH:
            message = "PED No enough space";
            break;
        case PED_ERR_DUKPT_NEED_INC_KSN:
            message = "DUKPT need inc ksn";
            break;
        case PED_ERR_KCV_MODE:
            message = "KCV MODE error";
            break;
        case PED_ERR_DUKPT_NO_KCV:
            message = "NO KCV";
            break;
        case PED_ERR_PIN_BYPASS_BYFUNKEY:
            message = "press FN/ATM4 KEY for PIN input";
            break;
        case PED_ERR_MAC:
            message = "verify MAC error";
            break;
        case PED_ERR_CRC:
            message = "verify CRC error";
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
