package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.DataModel.DataWithEncryptionMode;
import com.pax.mposapi.DataModel.EncryptionMode;
import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;
import com.pax.mposapi.model.CLSS_TRANS_PARAM;
import com.pax.mposapi.model.EMV_APPLABEL_LIST;
import com.pax.mposapi.model.EMV_APPLIST;
import com.pax.mposapi.model.EMV_CANDLIST;
import com.pax.mposapi.model.EMV_CAPK;
import com.pax.mposapi.model.EMV_ELEMENT_ATTR;
import com.pax.mposapi.model.EMV_MCK_PARAM;
import com.pax.mposapi.model.EMV_PARAM;
import com.pax.mposapi.model.EMV_REVOC_LIST;
import com.pax.mposapi.model.EMV_TM_ECP_PARAM;
import com.pax.mposapi.util.MyLog;
import com.pax.mposapi.util.Utils;

/**
 * <div class="zh">
 * EmvManager 用于处理EMV交易
 * </div>
 * <div class="en">
 * EmvManager is used to process EMV transation
 * </div>
 *
 */
public class EmvManager {
	//return codes
	public static final int EMV_OK								= 0;         //成功

	public static final int ICC_RESET_ERR						= -1;         //IC卡复位失败
	public static final int ICC_CMD_ERR							= -2;         //IC命令失败
	public static final int ICC_BLOCK							= -3;         //IC卡锁卡    

	public static final int EMV_RSP_ERR							= -4;         //IC返回码错误
	public static final int EMV_APP_BLOCK						= -5;         //应用已锁
	public static final int EMV_NO_APP							= -6;         //卡片里没有EMV应用
	public static final int EMV_USER_CANCEL						= -7;         //用户取消当前操作或交易
	public static final int EMV_TIME_OUT						= -8;         //用户操作超时
	public static final int EMV_DATA_ERR						= -9;         //卡片数据错误
	public static final int EMV_NOT_ACCEPT						= -10;        //交易不接受
	public static final int EMV_DENIAL							= -11;        //交易被拒绝
	public static final int EMV_KEY_EXP							= -12;        //密钥过期
	public static final int EMV_NO_PINPAD     					= -13;        //没有密码键盘或键盘不可用 
	public static final int EMV_NO_PASSWORD   					= -14;        //没有密码或用户忽略了密码输入 
	public static final int EMV_SUM_ERR       					= -15;        //认证中心密钥校验和错误 
	public static final int EMV_NOT_FOUND     					= -16;        //没有找到指定的数据或元素 
	public static final int EMV_NO_DATA       					= -17;        //指定的数据元素没有数据 
	public static final int EMV_OVERFLOW      					= -18;        //内存溢出 
	public static final int NO_TRANS_LOG      					= -19; 
	public static final int RECORD_NOTEXIST   					= -20; 
	public static final int LOGITEM_NOTEXIST  					= -21; 
	public static final int ICC_RSP_6985      					= -22;        // GAC中卡片回送6985, 由应用决定是否fallback
	
	public static final int CLSS_USE_CONTACT 					= -23;    // 必须使用其他界面进行交易
	public static final int EMV_FILE_ERR      					= -24;
	public static final int CLSS_TERMINATE    				 	= -25;    // 应终止交易       -25 
	public static final int CLSS_FAILED       					= -26;    // 交易失败 20081217 
	public static final int CLSS_DECLINE      					= -27;   
	public static final int EMV_PARAM_ERR 						= -30;		
	public static final int CLSS_PARAM_ERR            			= -30; // -26 // 因EMV 内核中的参数错误定义为-30
	public static final int CLSS_WAVE2_OVERSEA       			= -31;  // 20090418 for visa wave2 trans
	public static final int CLSS_WAVE2_TERMINATED     			= -32; // 20090421 for wave2 DDA response TLV format error
	public static final int CLSS_WAVE2_US_CARD        			= -33;  // 20090418 for visa wave2 trans
	public static final int CLSS_WAVE3_INS_CARD       			= -34; // 20090427 FOR VISA L3
	public static final int CLSS_RESELECT_APP         			= -35;
	public static final int CLSS_CARD_EXPIRED         			= -36; // liuxl 20091104 for qPBOC spec updated
	public static final int EMV_NO_APP_PPSE_ERR       			= -37;
	public static final int CLSS_USE_VSDC             			= -38; // FOR CLSS PBOC [1/12/2010 yingl]
	public static final int CLSS_CVMDECLINE          			= -39; // CVM result in decline for AE [1/11/2012 zhoujie]
	public static final int CLSS_REFER_CONSUMER_DEVICE  		= -40; //GPO response 6986
	
	//for EMVSetScriptProcMethod
	public static final int EMV_SCRIPT_PROC_NORMAL  	= 0;
	public static final int EMV_SCRIPT_PROC_UNIONPAY  	= 1;
	
	//for EMVAddApp
	public static final int EMV_APP_SEL_PARTIAL_MATCH  	= 0;
	public static final int EMV_APP_SEL_FULL_MATCH  	= 1;
	
	//for EMVProcTrans/EMVStartTrans/EMVCompleteTrans
	public static final int EMV_TRANS_RESULT_OK			= 0;
	public static final int EMV_TRANS_RESULT_NOT_ACCEPT	= -10;
	public static final int EMV_TRANS_RESULT_DENIAL		= -11;	
	public static final int EMV_AC_AAC       			= 0;
	public static final int EMV_AC_TC        			= 1;
	public static final int EMV_AC_ARQC      			= 2;
	public static final int EMV_AC_AAC_HOST  			= 3;
	
	//for EMV_PARAM.TransType
	public static final int EMV_TRANS_TYPE_CASH			= 0x01;
	public static final int EMV_TRANS_TYPE_GOODS		= 0x02;
	public static final int EMV_TRANS_TYPE_SERVICE      = 0x04;
	public static final int EMV_TRANS_TYPE_CASHBACK     = 0x08;
	public static final int EMV_TRANS_TYPE_INQUIRY		= 0x10;
	public static final int EMV_TRANS_TYPE_TRANSFER	  	= 0x20;
	public static final int EMV_TRANS_TYPE_PAYMENT		= 0x40;
	public static final int EMV_TRANS_TYPE_ADMIN		= 0x80;
	public static final int EMV_TRANS_TYPE_CASHDEPOSIT  = 0x90;

	//for set config flag
	public static final int EMV_CONFIG_FLAG_BIT_SUPPORT_ADVICE 				= 0x01;
	public static final int EMV_CONFIG_FLAG_BIT_CONFIRM_AMT_WHEN_NO_PIN 	= 0x02;
	public static final int EMV_CONFIG_FLAG_BIT_SUPPORT_TRANSLOG 			= 0x04;

	
	//for cEMVGetHolderPwd
	public static final int EMV_PIN_FLAG_NO_PIN_REQUIRED		= 0;
	public static final int EMV_PIN_FLAG_ONLINE					= 1;
	public static final int EMV_PIN_FLAG_OFFLINE				= 2;
	public static final int EMV_OFFLINE_PIN_STATUS_PED_TIMEOUT 	= 1;
	public static final int EMV_OFFLINE_PIN_STATUS_PED_WAIT 	= 2;
	public static final int EMV_OFFLINE_PIN_STATUS_PED_FAIL 	= 3;
//	public static final int EMV_NO_PASSWORD						= -14;	//already defined above
	
	//for cEMVReferProc
	public static final int EMV_REFER_APPROVE 					= 1;
	public static final int EMV_REFER_DENIAL  					= 2;
	
	//for cEMVOnlineProc
	public static final int EMV_ONLINE_APPROVE 					= 0;
	public static final int EMV_ONLINE_FAILED 					= 1;
	public static final int EMV_ONLINE_REFER 					= 2;
	public static final int EMV_ONLINE_DENIAL 					= 3;
	public static final int EMV_ONLINE_ABORT 					= 4;

	//for cEMVUnknowTLVData
	public static final int EMV_UNKNOWN_TAG_VALUE_PROVIDED 		= 0;
	public static final int EMV_UNKNOWN_TAG_VALUE_IGNORED 		= -1;
	
	//for cCertVerify
	public static final int EMV_CERT_VERIFY_OK 					= 0;
	public static final int EMV_CERT_VERIFY_ERR 				= 1;

    private static final String TAG = "EmvManager";
    private final Proto proto;
    private final ConfigManager cfg;
    private static EmvManager instance;
    
    //main purpose for this global resp buffer is to ensure 
    //enough receive buffer for ALL callback functions (i.e. passive cmd)
    private static final byte[] respBuffer = new byte[10240];
    
    /**
     * <div class="zh">
     * 转换返回码成字符串
     * </div>
     * <div class="en">
     * transform return code to human-readable string
     * </div>
     * 
     * @param retCode
     * <div class="zh">
     * 		返回码
     * </div>
     * <div class="en">
     * 		return code
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		返回码对应的字符串
     * </div>
     * <div class="en">
     * 		human-readable string for the return code
     * </div>
     */
    public static String retCode2String(int retCode) {
    	String str = "";
    	switch(retCode) {
			case EMV_OK:
			    str = "ok";
			    break;
			case ICC_RESET_ERR:
			    str = "icc reset error";
			    break;
			case ICC_CMD_ERR:
			    str = "icc cmd error";
			    break;
			case ICC_BLOCK:
			    str = "icc blocked";
			    break;
		
			case EMV_RSP_ERR:
			    str = "icc response code error";
			    break;
			case EMV_APP_BLOCK:
			    str = "app blocked";
			    break;
			case EMV_NO_APP:
			    str = "no app";
			    break;
			case EMV_USER_CANCEL:
			    str = "user cancel";
			    break;
			case EMV_TIME_OUT:
			    str = "time out";
			    break;
			case EMV_DATA_ERR:
			    str = "card data error";
			    break;
			case EMV_NOT_ACCEPT:
			    str = "transaction not accepted";
			    break;
			case EMV_DENIAL:
			    str = "transaction denied";
			    break;
			case EMV_KEY_EXP:
			    str = "key expired";
			    break;
			case EMV_NO_PINPAD:
			    str = "no pinpad";
			    break;
			case EMV_NO_PASSWORD:
			    str = "no pin";
			    break;
			case EMV_SUM_ERR:
			    str = "capk checksum error";
			    break;
			case EMV_NOT_FOUND:
			    str = "data not found";
			    break;
			case EMV_NO_DATA:
			    str = "no specified data";
			    break;
			case EMV_OVERFLOW:
			    str = "data overflow";
			    break;
			case NO_TRANS_LOG:
			    str = "no trans log entry";
			    break;
			case RECORD_NOTEXIST:
			    str = "no record";
			    break;
			case LOGITEM_NOTEXIST:
			    str = "no log item";
			    break;
			case ICC_RSP_6985:
			    str = "icc response code 6985";
			    break;
			
			case CLSS_USE_CONTACT:
			    str = "use contact interface";
			    break;
			case EMV_FILE_ERR:
			    str = "emv file error";
			    break;
			case CLSS_TERMINATE:
			    str = "clss transaction terminated";
			    break;
			case CLSS_FAILED:
			    str = "clss transaction failed";
			    break;
			case CLSS_DECLINE:
			    str = "clss transaction declined";
			    break;
			case EMV_PARAM_ERR:
			    str = "param error";
			    break;
			    /*
			case CLSS_PARAM_ERR:
			    str = ;
			    break;
			    */
			case CLSS_WAVE2_OVERSEA:
			    str = "CLSS_ERR_WAVE2_OVERSEA";		//FIXME?
			    break;
			case CLSS_WAVE2_TERMINATED:
			    str = "wave2 DDA response TLV format error";
			    break;
			case CLSS_WAVE2_US_CARD:
			    str = "CLSS_ERR_WAVE2_US_CARD";		//FIXME?
			    break;
			case CLSS_WAVE3_INS_CARD:
			    str = "CLSS_ERR_WAVE3_INS_CARD";		//FIXME?
			    break;
			case CLSS_RESELECT_APP:
			    str = "need reselect app";
			    break;
			case CLSS_CARD_EXPIRED:
			    str = "card expired";
			    break;
			case EMV_NO_APP_PPSE_ERR:
			    str = "no app and PPSE sel error";
			    break;
			case CLSS_USE_VSDC:
			    str = "use VSDC";
			    break;
			case CLSS_CVMDECLINE:
			    str = "CVM result in decline for AE";
			    break;
			case CLSS_REFER_CONSUMER_DEVICE:
			    str = "GPO response 6986";
			    break;
    	}
    	return str;
    }
    
    /**
     * <div class="zh">
     * EMV callback 处理器接口,应用程序应该实现这些接口
     * </div>
     * <div class="en">
     * EMV callback handler interface, APP should implement this interface.
     * </div>
     */
    public static interface EmvCallbackHandler {
    	
        /**
         * <div class="zh">
	     * <b><font color=red>NOTE: D180不会有此回调 </font></b><br/>
         * 等待用户从应用列表中选择一个应用, 如果只有一个无需确认的应用,则本函数不会被调用
         * </div>
         * <div class="en">
	     * <b><font color=red>NOTE: for D180, this function will not be called</font></b><br/>
         * Wait for user to select an application from the application candidate list. If there is only one application in the application list and it doesn't require cardholder confirmation, this function will not be called.
         * </div>
         * 
	     * @param tryCnt
	     * <div class="zh">
	     * 	为0时, 第一次调用, 反之, 非第一次调用（按EMV要求非第一次调用的情况下应该显示"APP NOT ACCEPT, TRY AGAIN"等字样.
	     * </div>
	     * <div class="en">
	     * 	TryCnt=0 means it is called for the first time, otherwise, it has been called more than one time. (According to the EMV specification, if this function has been called more than one time, terminal should prompt for 'APP NOT ACCEPT, TRY AGAIN' or some other word like that.).
	     * </div>
	     * 
	     * @param appNum
	     * <div class="zh">
	     * 			app的个数
	     * </div>
	     * <div class="en">
	     * 		The number of the application in the list.
	     * </div>
  		 *
	     * @param apps
	     * <div class="zh">
	     * 			[输入]应用列表
	     * </div>
	     * <div class="en">
	     * 		[input] applications
	     * </div>
  		 *
  		 * @return
  		 * <div class="zh">
  		 * 		>=0: 用户选中的应用序号(比如：0表示选中apps[0]) <br/>
  		 * 		{@link #EMV_USER_CANCEL}: 用户取消应用选择<br/>
  		 * 		{@link #EMV_TIME_OUT}: 应用选择超时<br/>
  		 * </div>
  		 * <div class="en">
  		 * 		>=0: The sequence number selected by the user(For example: 0 stands for apps[0] was selected) <br/>
  		 * 		{@link #EMV_USER_CANCEL}: Application selection is canceled by user<br/>
  		 * 		{@link #EMV_TIME_OUT}: Application selection timeout.<br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">通信错误</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">协议错误</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">通用错误</div>
	     * <div class="en">common error</div>
         */
    	public int onWaitAppSel(int tryCnt, int appNum, EMV_APPLIST[] apps) throws IOException, ProtoException, CommonException;

        /**
         * <div class="zh">
	     * <b><font color=red>NOTE: 仅适用于D180 </font></b><br/>
         * 等待用户从应用列表中选择一个应用, 如果只有一个无需确认的应用,则本函数不会被调用
         * </div>
         * <div class="en">
	     * <b><font color=red>NOTE: only for D180 </font></b><br/>
         * Wait for user to select an application from the application candidate list. If there is only one application in the application list and it doesn't require cardholder confirmation, this function will not be called.
         * </div>
         * 
	     * @param tryCnt
	     * <div class="zh">
	     * 	为0时, 第一次调用, 反之, 非第一次调用（按EMV要求非第一次调用的情况下应该显示"APP NOT ACCEPT, TRY AGAIN"等字样.
	     * </div>
	     * <div class="en">
	     * 	TryCnt=0 means it is called for the first time, otherwise, it has been called more than one time. (According to the EMV specification, if this function has been called more than one time, terminal should prompt for 'APP NOT ACCEPT, TRY AGAIN' or some other word like that.).
	     * </div>
	     * 
	     * @param appNum
	     * <div class="zh">
	     * 			app的个数
	     * </div>
	     * <div class="en">
	     * 		The number of the application in the list.
	     * </div>
  		 *
	     * @param apps
	     * <div class="zh">
	     * 			[输入]应用列表
	     * </div>
	     * <div class="en">
	     * 		[input] applications
	     * </div>
  		 *
  		 * @return
  		 * <div class="zh">
  		 * 		>=0: 用户选中的应用序号(比如：0表示选中apps[0]) <br/>
  		 * 		{@link #EMV_USER_CANCEL}: 用户取消应用选择<br/>
  		 * 		{@link #EMV_TIME_OUT}: 应用选择超时<br/>
  		 * </div>
  		 * <div class="en">
  		 * 		>=0: The sequence number selected by the user(For example: 0 stands for apps[0] was selected) <br/>
  		 * 		{@link #EMV_USER_CANCEL}: Application selection is canceled by user<br/>
  		 * 		{@link #EMV_TIME_OUT}: Application selection timeout.<br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">通信错误</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">协议错误</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">通用错误</div>
	     * <div class="en">common error</div>
         */    	
    	public int onCandAppSel(int tryCnt, int appNum, EMV_CANDLIST[] apps) throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
         * 输入交易金额
         * </div>
         * <div class="en">
         * Input transaction amount.
         * </div>
         * 
	     * @param amts
	     * <div class="zh">
	     * 		<br/>
	     * 		amts[0] - [输出]: 交易金额. <br/> 
	     * 		amts[1] - [输入/输出] : 返现金额. 作为输入时如果其值为null则表示不必输出返现金额,否则应输出返现金额 <br/>
	     * 		<b>注意,金额必须以货币的最小单位表示,如人民币用分表示</b>
	     * </div>
	     * <div class="en">
	     * 		<br/>
	     * 		amts[0] - [output]: transaction amount. <br/> 
	     * 		amts[1] - [input/output] : cashback amount. As an input, if it's null, then no need input cashback amount.<br/>
	     * 		<b>Note, the amount MUST be presented with the smallest unit</b>
	     * </div>
	     * 
  		 * @return
  		 * <div class="zh">
  		 * 		{@link #EMV_OK}: 输入成功<br/>
  		 * 		{@link #EMV_USER_CANCEL}: 用户取消金额输入<br/>
  		 * 		{@link #EMV_TIME_OUT}: 应用输入金额超时<br/>
  		 * </div>
  		 * <div class="en">
  		 * 		{@link #EMV_OK}: Input succeeds<br/>
  		 * 		{@link #EMV_USER_CANCEL}: Amount input is canceled by user.<br/>
  		 * 		{@link #EMV_TIME_OUT}: Amount input timeout<br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">通信错误</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">协议错误</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">通用错误</div>
	     * <div class="en">common error</div>
         */
    	//if cashbackAmt is null, then no need cashback
    	public int onInputAmount(String[] amts) throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
         * 等待用户输入持卡人密码
         * </div>
         * <div class="en">
         * Wait for cardholder to input PIN.
         * </div>
         * 
	     * @param pinFlag
	     * <div class="zh">
	     * 		{@link #EMV_PIN_FLAG_NO_PIN_REQUIRED}: 不需要输入PIN, 但要显示金额给持卡人确认. <br/>
	     * 		{@link #EMV_PIN_FLAG_ONLINE}: online PIN. <br/>
	     * 		{@link #EMV_PIN_FLAG_OFFLINE}: offline PIN. <br/>
	     * </div>
	     * <div class="en">
	     * 		{@link #EMV_PIN_FLAG_NO_PIN_REQUIRED}: no PIN required, but need to show amount to card holder. <br/>
	     * 		{@link #EMV_PIN_FLAG_ONLINE}: online PIN. <br/>
	     * 		{@link #EMV_PIN_FLAG_OFFLINE}: offline PIN. <br/>
	     * </div>
	     * 
	     * @param tryFlag
	     * <div class="zh">
	     * 		仅当pinFlag为2时有意义<br/>
	     * 		0 : 表示本次交易中, 第一次调用该函数获取用户密码 <br/>
	     * 		1 : 非第一次调用该函数获取用户密码(密码验证错, 且仅脱机验证密码的情况下才会出现). <br/>
	     * </div>
	     * <div class="en">
	     * 		valid only when pinFlag is 2<br/>
	     * 		0 : It's the first time calling this function to get the cardholder's PIN in this transaction.<br/>
	     * 		1 : It's not the first time calling this function to get the cardholder's PIN in this transaction. (It appears only when verifying the offline PIN and failing). <br/>	     
	     * </div>
  		 *
	     * @param remainCnt
	     * <div class="zh">
	     * 		仅当pinFlag为2时有意义<br/>
		 *		表示还有几次重试机会, 如果RemainCnt为1, 则表示只有最后一次机会了, 如果密码再错, 则卡密码会被锁定
	     * </div>
	     * <div class="en">
	     * 		valid only when pinFlag is 2<br/>
	     * 		The chance remained to verify the PIN. If RemainCnt equals 1, it means only one chance remained to verify the PIN, and if the following PIN verification is still failed, the PIN will be blocked
	     * </div>
	     * 
	     * @param pinStatus
	     * <div class="zh">
	     * 		仅当pinFlag为2时有意义<br/>
	     * 		{@link #EMV_OFFLINE_PIN_STATUS_PED_WAIT}:  PIN输入间隔时间不够<br/>
	     * 		{@link #EMV_OFFLINE_PIN_STATUS_PED_TIMEOUT}: PIN输入超时<br/>
	     * 		{@link #EMV_OFFLINE_PIN_STATUS_PED_FAIL}: PED设备锁定或其他系统错误<br/>
	     * </div>
	     * <div class="en">
	     * 		valid on when pinFlag is 2<br/>
	     * 		{@link #EMV_OFFLINE_PIN_STATUS_PED_WAIT}:  PIN input interval not enough<br/>
	     * 		{@link #EMV_OFFLINE_PIN_STATUS_PED_TIMEOUT}: PIN input timeout<br/>
	     * 		{@link #EMV_OFFLINE_PIN_STATUS_PED_FAIL}: PED locked or other failure<br/>
	     * </div>
  		 *
  		 * @return
  		 * <div class="zh">
  		 * 		{@link #EMV_OK}: 输入密码成功<br/>
  		 * 		{@link #EMV_NO_PASSWORD}: 无密码或用户不希望输入密码<br/>
  		 * 		{@link #EMV_USER_CANCEL}: 用户取消交易<br/>
  		 * 		{@link #EMV_TIME_OUT}: 输入密码超时<br/>
  		 * </div>
  		 * <div class="en">
  		 * 		{@link #EMV_OK}: Succeed<br/>
  		 * 		{@link #EMV_NO_PASSWORD}: No PIN or cardholder doesn't want to input PIN<br/>
  		 * 		{@link #EMV_USER_CANCEL}: PIN input is canceled by user<br/>
  		 * 		{@link #EMV_TIME_OUT}: PIN input timeout<br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">通信错误</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">协议错误</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">通用错误</div>
	     * <div class="en">common error</div>
         */
    	public int onGetHolderPwd(int pinFlag, int tryFlag, int remainCnt, int pinStatus) throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
	     * <b><font color=red>NOTE: D180不会有此回调 </font></b><br/> 
         * 处理发卡行发起的参考
         * </div>
         * <div class="en">
	     * <b><font color=red>NOTE: for D180, this function will not be called</font></b><br/>
         * Process referral activated by the issuer
         * </div>
  		 *
  		 * @return
  		 * <div class="zh">
  		 * 		{@link #EMV_REFER_APPROVE}: 接受交易<br/>
  		 * 		{@link #EMV_REFER_DENIAL}: 拒绝交易<br/>
  		 * </div>
  		 * <div class="en">
  		 * 		{@link #EMV_REFER_APPROVE}: Referral approved.<br/>
  		 * 		{@link #EMV_REFER_DENIAL}: Referral denied.<br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">通信错误</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">协议错误</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">通用错误</div>
	     * <div class="en">common error</div>
         */
    	public int onReferProc() throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
	     * <b><font color=red>NOTE: D180不会有此回调 </font></b><br/>
         * 联机交易
         * </div>
         * <div class="en">
	     * <b><font color=red>NOTE: for D180, this function will not be called</font></b><br/>
         * Online transaction.
         * </div>
         * 
	     * @param respCode
	     * <div class="zh">
	     * 		[输出] 授权响应代码, 2字节, 如果没有, 则RspCode[0]等于0(比如, 联机失败的情况)
	     * </div>
	     * <div class="en">
	     * 		[output] Authorization response code, 2 bytes. Set RspCode[0] as 0 in case of online failed.
	     * </div>
	     * 
	     * @param authCode
	     * <div class="zh">
	     * 		[输出] 1字节长度 + 授权码, 如果没有授权码, 则长度为0
	     * </div>
	     * <div class="en">
	     * 		[output] 1 byte length + Authorization code, length is 0 if there's no Authorization code.
	     * </div>
  		 *
	     * @param authData
	     * <div class="zh">
	     * 		[输出] 4字节长度 + 主机返回的外部认证数据, 如果没有主机返回的外部认证数据, 则长度为0
	     * </div>
	     * <div class="en">
	     * 		[output] 4 bytes length + Issuer authentication data returned from host, length is 0 if there's no Issuer authentication data.
	     * </div>
	     * 
	     * @param script
	     * <div class="zh">
	     * 		[输出] 4字节长度 + 发卡行脚本, 如果主机返回的脚本不是在同一个8583域中下传的, 则把所有的脚本拼接在一起后通过该参数返回, 如果没有, 则长度为0
	     * </div>
	     * <div class="en">
	     * 		[output] 4 bytes length + Issuer script. If the scripts are not sent in one 8583 field, then put all the scripts together and return by this parameter. length is 0 if there's no issuer script.
	     * </div>
  		 *
  		 * @return
  		 * <div class="zh">
  		 * 		{@link #EMV_ONLINE_APPROVE}: 联机批准（主机批准交易）<br/>
  		 * 		{@link #EMV_ONLINE_DENIAL}: 联机拒绝（主机没有批准交易）<br/>
  		 * 		{@link #EMV_ONLINE_REFER}: 联机参考（发卡行参考）<br/>
  		 * 		{@link #EMV_ONLINE_FAILED}: 联机失败<br/>
  		 * 		{@link #EMV_ONLINE_ABORT}: 终止交易（PBOC要求）<br/>
  		 * </div>
  		 * <div class="en">
  		 * 		{@link #EMV_ONLINE_APPROVE}: Online transaction approved (host approve the transaction)<br/>
  		 * 		{@link #EMV_ONLINE_DENIAL}: Online transaction denied (host denied the transaction).<br/>
  		 * 		{@link #EMV_ONLINE_REFER}: Online transaction referral (Issuer referral).<br/>
  		 * 		{@link #EMV_ONLINE_FAILED}: Online transaction failed.<br/>
  		 * 		{@link #EMV_ONLINE_ABORT}: Online transaction aborted (PBOC requirement).<br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">通信错误</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">协议错误</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">通用错误</div>
	     * <div class="en">common error</div>
         */    	
    	//authCode L1 + V,  authData L4 + V, script L4 + V 
    	public int onOnlineProc(byte[] respCode, byte[] authCode, byte[] authData, byte[] script) throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
	     * <b><font color=red>NOTE: D180不会有此回调 </font></b><br/>
         * 联机或脱机通知处理
         * </div>
         * <div class="en">
	     * <b><font color=red>NOTE: for D180, this function will not be called</font></b><br/>
         * Online or offline advice processing
         * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">通信错误</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">协议错误</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">通用错误</div>
	     * <div class="en">common error</div>
         */    	    	
    	public void onAdviceProc() throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
         * 验证密码成功通知
         * </div>
         * <div class="en">
         * Prompt for "PIN OK"
         * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">通信错误</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">协议错误</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">通用错误</div>
	     * <div class="en">common error</div>
         */    	    	    	
    	public void onVerifyPinOk() throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
	     * <b><font color=red>NOTE: D180不会有此回调 </font></b><br/>
         * 获取EMV库不识别TAG的数据值
         * </div>
         * <div class="en">
	     * <b><font color=red>NOTE: for D180, this function will not be called</font></b><br/>
         * Get the data of the unknown tag.
         * </div>
         * 
	     * @param tag
	     * <div class="zh">
	     * 		标签, 可能EMV没定义的, 也可能定义了, 但IC卡片中没有的.
	     * </div>
	     * <div class="en">
	     * 		Tag. It may be not defined by EMV, or defined by EMV but can't be found in the IC card.
	     * </div>
	     * 
	     * @param len
	     * <div class="zh">
	     * 		DOL要求的标签值的长度
	     * </div>
	     * <div class="en">
	     * 		The length of the tag according to the DOL requirement.
	     * </div>
  		 *
	     * @param value
	     * <div class="zh">
	     * 		[输出] 标签值, 应用程序填写
	     * </div>
	     * <div class="en">
	     * 		[output] The value of the tag, filled by the application.
	     * </div>
	     * 
  		 * @return
  		 * <div class="zh">
  		 * 		{@link #EMV_UNKNOWN_TAG_VALUE_PROVIDED}: 应用程序已提供相应的标签值<br/>
  		 * 		{@link #EMV_UNKNOWN_TAG_VALUE_IGNORED}: 应用程序没有处理 <br/>
  		 * </div>
  		 * <div class="en">
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">通信错误</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">协议错误</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">通用错误</div>
	     * <div class="en">common error</div>
         */    	
    	public int onUnknownTLVData(short tag, int len, byte[] value) throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
         * 持卡人证件认证（PBOC2.0）, PBOC2.0新增加了持卡人证件认证的CVM, 在交易过程中, EMV内核发现当前使用的PBOC卡需要持卡人认证时, 
         * 会调用该函数, 应用程序应该在该函数中调用   {@link EmvManager#getTLVData} 函数读取证件号码及证件类型, 并把相关信息提供给操作人员核对
         * </div>
         * <div class="en">
         * Cardholder credential verify(PBOC2.0)
         * PBOC2.0 adds the CVM, which is for cardholder credential verify. 
         * During the transaction, when the EMV kernel finds that the current used PBOC card need to do the cardholder verify, it will call this function. 
         * The application should call the function {@link EmvManager#getTLVData} to read the credential number and credential type, and provide the relevant information to the operator.<br/>
		 * This function is only used for PBOC.
         * </div>
	     * 
  		 * @return
  		 * <div class="zh">
  		 * 		{@link #EMV_CERT_VERIFY_OK}: 认证成功<br/>
  		 * 		{@link #EMV_CERT_VERIFY_ERR}: 认证失败 <br/>
  		 * </div>
  		 * <div class="en">
  		 * 		{@link #EMV_CERT_VERIFY_OK}: Verify succeeded<br/>
  		 * 		{@link #EMV_CERT_VERIFY_ERR}: Verify failed <br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">通信错误</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">协议错误</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">通用错误</div>
	     * <div class="en">common error</div>
         */    	    	
    	public int onCertVerify() throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
         * 参数设置 <br/>
         * 1.该接口用于在最终选择执行之后, 取处理选项命令发送前, 根据最终选择的AID名称或具体的应用需要, 设置与该AID或与当前交易对应的参数 <br/>
         * 2.应用程序在该接口中调用 {@link EmvManager#setTLVData} 接口设置所需的参数<br/>
         * 3.当该函数返回值不为{@link #EMV_OK}时, 内核将立即退出交易处理过程<br/>
         * 4.支持SM算法的终端需在该回调函数中设置数据元0xDF69的值为1, 长度为1<br/>
         *    
         * </div>
         * <div class="en">
         * Set parameter after application selection.<br/>
         * 1.This function is used to set some AID specific parameter after performing application selection and before GPO. <br/>
         * 2. Application can call  {@link EmvManager#setTLVData} in this function to set these parameters.<br/>
		 * 3.When the return value of this function is not {@link #EMV_OK}, kernel will abort the current transaction.<br/>
		 * 4.if terminal supports SM algorithm, should set value of tag 0xDF69 to 1

         * </div>
	     * 
  		 * @return
  		 * <div class="zh">
  		 * 		{@link #EMV_OK}: 成功,可继续EMV交易<br/>
  		 * 		其他: 取消/终止EMV交易<br/>
  		 * </div>
  		 * <div class="en">
  		 * 		{@link #EMV_OK}: Succeed.<br/>
  		 * 		others: Abort/Terminate current transaction.<br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">通信错误</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">协议错误</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">通用错误</div>
	     * <div class="en">common error</div>
         */
    	public int onSetParam() throws IOException, ProtoException, CommonException;
    }
    
    /**
     * <div class="zh">
     * 使用指定的Context构造出EmvManager对象
     * </div>
     * <div class="en">
     * Create a EmvManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">应用当前的context</div>
     * <div class="en">application context currently</div>
     */ 
    //注意：之前是private（2014.08.21）
    public EmvManager(Context context) {
    	proto = Proto.getInstance(context);
    	cfg = ConfigManager.getInstance(context);
    }

    /**
     * Create an EmvManager instance with a given Context
     * 
     * @param context
     *            application context currently
     */
    public static EmvManager getInstance(Context context) {
        if (instance == null) {
        	instance = new EmvManager(context);
        }
        return instance;
    }
        
    /**
     * <div class="zh">
     * 设置callback 处理器
     * </div>
     * <div class="en">
     * set EMV callback handler
     * </div>
     * 
     * @param handler 
     * <div class="zh">callback 处理器</div>
     * <div class="en">callback handler</div>
     */      
    public void setCallbackHandler(EmvCallbackHandler handler) {
    	proto.setEmvCallbackHandler(handler);
    }

    /**
     * <div class="zh">
     * 读终端参数
     * </div>
     * <div class="en">
     * Get terminal parameter.
     * </div>
     * 
	 * @return
	 * <div class="zh">
	 * 		终端参数
	 * </div>
	 * <div class="en">
	 * 		terminal parameter
	 * </div>
	 *
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */    
    public EMV_PARAM getParameter() throws EmvException, IOException, ProtoException, CommonException, BaseSystemException {

        String s="getEMVParameter|123|D180EMDK";
        int i=0;
        RespCode rc = new RespCode();
    	EMV_PARAM emvParam = new EMV_PARAM();
    	byte[] send= s.getBytes();
    	for(i=0;i<10240;i++)
    		respBuffer[i]=0x00;
    	proto.sendRecv(Cmd.CmdType.MTLA_GET_EMV_PARAMETER, send, rc, respBuffer);
    	if (rc.code == 0) {
    		String sTemp=new String(respBuffer);
    		//"success|123|<EMV_PARAM>"
    		if(sTemp.contains("success|123|") == true)
    		{
    			byte[] bTemp=new byte[respBuffer.length-12];
    			for(i=12;i<respBuffer.length;i++)
    			{
    				bTemp[i-12]=respBuffer[i];
    			}
    			emvParam.serialFromBuffer(bTemp);
            	return emvParam;
    		}
    		//success
    		return null;
    	} else {
        	throw new EmvException(rc.code);
    	}  
    }
    
    /**
     * <div class="zh">
     * 设置终端参数
     * </div>
     * <div class="en">
     * set terminal parameter
     * </div>
     * 
     * @param param
	 * <div class="zh">
	 * 		终端参数, 参见{@link EMV_PARAM}
	 * </div>
	 * <div class="en">
	 * 		terminal parameter, see {@link EMV_PARAM}
	 * </div>
	 *
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */        
    public byte[] setParameter(EMV_PARAM param) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	int i,j;
    	String s="downloadParam|123|D180EMDK|";
    	byte[] req1=s.getBytes();
    	byte[] req = param.serialToBuffer();
    	byte[] send=new byte[req.length+req1.length];
    	for(i=0;i<req1.length;i++)
    		send[i]=req1[i];
    	for(j=0;j<req.length;j++)
    		send[i++]=req[j];
    	for(i=0;i<10240;i++)
    		respBuffer[i]=0x00;
    	proto.sendRecv(Cmd.CmdType.MTLA_DOWNLOAD_EMV_PARAM, send, rc, respBuffer);
    	if (rc.code == 0) { 
    		//success
    		return respBuffer;
    	} else {
        	throw new EmvException(rc.code);
    	}
    }
    
    /**
     * <div class="zh">
     * 读取指定标签的数据值
     * </div>
     * <div class="en">
     * Get the value of the data element by specifying the tag.
     * </div>
     * 
     * @param tag
	 * <div class="zh">
	 * 		EMV定义的标准数据元素标签或扩展的标签
	 * </div>
	 * <div class="en">
	 * 		Tag of EMV standard or extended data element.
	 * </div>
	 *
	 * @return
	 * <div class="zh">
	 * 		null : 无此标签<br/>
	 * 		非null: 标签值
	 * </div>
	 * <div class="en">
	 *		null: no specified tag<br/>
	 *		non-null: value of the specified tag 
	 * </div>
	 * 
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */            
    //null or data
    public DataWithEncryptionMode getTLVData(int tag) throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	Utils.short2ByteArray((short)tag, req, 0);

    	proto.sendRecv(Cmd.CmdType.EMV_GET_TLV_DATA, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success - cleartext
    		int len = Utils.intFromByteArray(respBuffer, 0);
    		byte[] dataOut = new byte[len];
    		System.arraycopy(respBuffer, 4, dataOut, 0, len);
    		DataWithEncryptionMode dem = new DataWithEncryptionMode(EncryptionMode.CLEAR, dataOut);
    		return dem;
    	} else if (rc.code == DataModel.MPOS_RET_SENSITIVE_CIPHER_DUKPTDES){
    		//success - ciphertext
    		int len = Utils.intFromByteArray(respBuffer, 0);
    		byte[] dataOut = new byte[len];
    		System.arraycopy(respBuffer, 4, dataOut, 0, len);
    		DataWithEncryptionMode dem = new DataWithEncryptionMode(EncryptionMode.SENSITIVE_CIPHER_DUKPTDES, dataOut);
    		return dem;
    	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        	throw new CommonException(rc.code);
    	}
    	return null;
    }
    
    /**
     * <div class="zh">
     * 设置指定标签的数据值
     * </div>
     * <div class="en">
     * Set the value of the data element by specifying the tag.
     * </div>
     * 
     * @param tag
	 * <div class="zh">
	 * 		EMV定义的标准数据元素标签或扩展的标签
	 * </div>
	 * <div class="en">
	 * 		Tag of EMV standard or extended data element.
	 * </div>
	 *
     * @param value
	 * <div class="zh">
	 * 		[输入]标签Tag指定的数据
	 * </div>
	 * <div class="en">
	 * 		[input] The value of the data element specified by the tag.
	 * </div>
	 * 
	 * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */                
    public void setTLVData(int tag, byte[] value) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2 + 4 + value.length];
    	Utils.short2ByteArray((short)tag, req, 0);
    	Utils.int2ByteArray(value.length, req, 2);
    	System.arraycopy(value, 0, req, 6, value.length);

    	proto.sendRecv(Cmd.CmdType.EMV_SET_TLV_DATA, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }    
    
    /**
     * <div class="zh">
     * 读取发卡行脚本结果
     * </div>
     * <div class="en">
     * Get the issuer script processing result.
     * </div>
     * 
	 * @return
	 * <div class="zh">
	 * 		null : 无脚本<br/>
	 * 		非null: 脚本结果
	 * </div>
	 * <div class="en">
	 * 		null: no issuer script processing result<br/>
	 * 		non-null: issuer script processing result
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */                
    //null or data
    public byte[] getScriptResult() throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();

    	proto.sendRecv(Cmd.CmdType.EMV_GET_SCRIPT_RESULT, new byte[0], rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		int len = Utils.intFromByteArray(respBuffer, 0);
    		byte[] dataOut = new byte[len];
    		System.arraycopy(respBuffer, 4, dataOut, 0, len);
    		return dataOut;
    	} else {
        	return null;
    	}
    }

    /**
     * <div class="zh">
     * <b><font color=red>注意: D180 不支持此功能</font></b><br/>
     * 设置PCI脱机PIN验证参数
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function </font></b><br/>
     * add proprietary data elements<br/>
     * Set PCI offline PIN verification parameter
     * </div>
     * 
     * @param timeout
	 * <div class="zh">
	 * 		输入PIN的超时时间, 单位：毫秒, 最大值为300,000Ms
	 * </div>
	 * <div class="en">
	 *		Timeout of input PIN, unit: ms, maximum 300,000 ms. 		
	 * </div>
	 *
     * @param expPinLen
	 * <div class="zh">
	 * 		该参数指明可输入的合法密码长度字符串. 0～12的枚举集合,用","号隔开每个长度, 如允许输入4、6位密码并且允许无密码直接按确认, 则该字符串应该设置为"0,4,6".
	 * </div>
	 * <div class="en">
	 * 		 the allowed length of PIN. It is a string of the enumeration of 0-12 and separated by ','. For example, '0,4,6' means it is allowed to input 4 or 6 digits for PIN, and to directly press Enter without input PIN.
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */        
    public void setPCIModeParam(int timeout, String expPinLen) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] pinLen = expPinLen.getBytes(); 
    	byte[] req = new byte[4 + 1 + pinLen.length];
    	Utils.int2ByteArray(timeout, req, 0);
    	req[4] = (byte)expPinLen.length();
    	System.arraycopy(pinLen, 0, req, 5, pinLen.length);

    	proto.sendRecv(Cmd.CmdType.EMV_SET_PCI_MODE_PARAM, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }    

    /**
     * <div class="zh">
     * 获取内核发布版本号及日期信息
     * </div>
     * <div class="en">
     * Get the version of EMV kernel and the release date.
     * </div>
     * 
	 * @return
	 * <div class="zh">
	 * 		内核发布版本号及日期信息,  示例: "v26 2008.10.09"
	 * </div>
	 * <div class="en">
	 * 		Version and release date of kernel with the maximum length of 20 bytes. For example, "v26 2008.10.09".
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */              
    public String readVerInfo() throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();

    	proto.sendRecv(Cmd.CmdType.EMV_READ_VER_INFO, new byte[0], rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		return new String(respBuffer, 1, respBuffer[0]);
    	} else {
        	throw new EmvException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * <b><font color=red>注意: D180 不支持此功能 </font></b><br/> 
     * 清除内核中的交易流水记录 <br/>
	 * 1.内核会循环记录8笔交易金额数据, 用于终端风险管理时, 判断当前交易金额与有记录的该卡号最后一次交易记录金额的累加值是否超出最低限额<br/>
	 * 2.若应用程序不希望累加最后一笔交易金额, 或在批结算等操作后, 需要同步删除内核交易记录时, 可通过该接口清除内核中的交易流水记录
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function </font></b><br/>
     * Clear the transaction log of kernel.<br/>
     * 1.Kernel will record the amount of the last 8 transactions. When performing terminal risk management, kernel will add the amount of current transaction with the amount of the last transaction found in the log giving that the PAN is same and the result will be used for floor limit check.<br/>
     * 2.This function is provided for application to erase this log after settlement or other cases when necessary.
     * </div>
     * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */                   
    public void clearTransLog() throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();

    	proto.sendRecv(Cmd.CmdType.EMV_CLEAR_TRANS_LOG, new byte[0], rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * <b><font color=red>注意: D180 不支持此功能</font></b><br/>
     * 添加卡片自定义标签<br/>
	 * 1.内核只支持EMV定义的标准数据标签以及发卡行数据标签, 需要为应用程序提供专用函数来添加卡片数据元素自定义标签, 卡片自定义标签列表由应用来定义及初始化, 并将卡片自定义标签列表通过该函数传递给内核;<br/>
	 * 2.卡片自定义标签列表的数据结构参看{@link EMV_ELEMENT_ATTR}, 标签列表内的标签个数与nAddNum必需一致, 若nAddNum个标签数据中出现MaxLen或者Tag为0, 则会返回参数设置错误;<br/>
	 * 3.每次调用该函数, 内核都会清空内核对应的卡片自定义标签列表;<br/>
	 * 4.如果数据元素标签重复出现, 选择最新数据存储
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function</font></b><br/>
     * add proprietary data elements<br/>
     * 1.Kernel only supports EMV defined standard data element. This function is used to add the list of any proprietary data elements to kernel so it will save the corresponding ones. Thus the application can access the data through {@link EmvManager#getTLVData}.<br/>
	 * 2.Refer to appendix E for structure {@link EMV_ELEMENT_ATTR}. The number in the list must equal nAddNum. MaxLen and Tag must not be zero. Otherwise, parameter error will be returned.<br/>
	 * 3.Each time calling this function, kernel will replace the original list.<br/>
	 * 4.If data duplicate happens when reading ICC, the latest data will be used.<br/>
     * </div>
     * 
     * @param attr
	 * <div class="zh">
	 * 		[输入]卡片自定义数据标签列表
	 * </div>
	 * <div class="en">
	 * 		[input]List of issuer proprietary data elements
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */                       
    public void addIccTag(EMV_ELEMENT_ATTR[] attr) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	int num = attr.length;
    	int len = num > 0 ? attr[0].serialToBuffer().length : 0;
    	byte[] req = new byte[1 + num * len];
    	req[0] = (byte)(num);
    	
    	for (int i = 0; i < num; i++) {
    		byte[] a = attr[i].serialToBuffer();
    		System.arraycopy(a, 0, req, 1 + i * len, a.length);
    	}

    	proto.sendRecv(Cmd.CmdType.EMV_ADD_ICC_TAG, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }   
    
    /**
     * <div class="zh">
     * 设置脚本处理方式
     * </div>
     * <div class="en">
     * add proprietary data elements<br/>
     * set issuer script processing method
     * </div>
     * 
     * @param method
	 * <div class="zh">
	 * 	<ul>
	 * 		<li>{@link #EMV_SCRIPT_PROC_UNIONPAY}: 银联入网认证要求脚本命令无响应时脚本处理结果为脚本处理未执行. 银联入网认证测试需求）
	 * 		<li>{@link #EMV_SCRIPT_PROC_NORMAL}:EMV正常处理方式（默认值）
	 * 	</ul>
	 * </div>
	 * <div class="en">
	 * 	<ul>
	 * 		<li>{@link #EMV_SCRIPT_PROC_UNIONPAY}: CUP processing method
	 * 		<li>{@link #EMV_SCRIPT_PROC_NORMAL}:EMV normal processing method (default)
	 * 	</ul>
	 * </div>
     *  
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */        
    public void setScriptProcMethod(int method) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = (byte)(method);
    	
    	proto.sendRecv(Cmd.CmdType.EMV_SET_SCRIPT_PROC_METHOD, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    } 

    /**
     * <div class="zh">
     * 添加一个新的认证中心密钥<br/>
	 * 如果密钥存在, 则用新的密钥覆盖原来的密钥.认证中心密钥由收单行提供.收单行提供的密钥不一定符合结构{@link com.pax.mposapi.model#EMV_CAPK}, 应用可能需要转换后才能添加到EMV库
     * </div>
     * <div class="en">
     * Add a new CA public key.
     * Overwrites if already exsists. CAPK is provided by the acquirer, if it does not conform to {@link com.pax.mposapi.model#EMV_CAPK}, please convert it first. 
     * </div>
     * 
     * @param capk
	 * <div class="zh">
	 * 		密钥数据
	 * </div>
	 * <div class="en">
	 * 		CA public key
	 * </div>
     *  
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */            
    public byte[] addCAPK(EMV_CAPK capk) throws EmvException, IOException, ProtoException, CommonException {
     	RespCode rc = new RespCode();
    	int i,j;
    	String s="downloadCAPK|123|D180EMDK|";
    	byte[] req1=s.getBytes();
    	byte[] req = capk.serialToBuffer();
    	byte[] send=new byte[req.length+req1.length];
    	for(i=0;i<req1.length;i++)
    		send[i]=req1[i];
    	for(j=0;j<req.length;j++)
    		send[i++]=req[j];
    	for(i=0;i<10240;i++)
    		respBuffer[i]=0x00;
    	proto.sendRecv(Cmd.CmdType.MTLA_DOWNLOAD_CAPK, send, rc, respBuffer);
    	if (rc.code == 0) { 
    		//success
    		return respBuffer;
    	} else {
        	throw new EmvException(rc.code);
    	}
    } 

    /**
     * <div class="zh">
     * <b><font color=red>注意: D180 不支持此功能</font></b><br/>
     * 从EMV库中删除一个认证中心密钥
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function</font></b><br/>
     * Delete a CA public key.
     * </div>
     * 
     * @param keyId
	 * <div class="zh">
	 * 		密钥索引
	 * </div>
	 * <div class="en">
	 * 		The index of the key.
	 * </div>
	 * 
     * @param rid
	 * <div class="zh">
	 * 		[输入]应用注册服务商ID, 对于D180,忽略此参数 
	 * </div>
	 * <div class="en">
	 * 		[input]Registered Application Provider Identifier. This is ignored for D180
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */                
    public void delCAPK(int keyId, byte[] rid) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[6];
    	req[0] = (byte)keyId;
    	if (rid != null && rid.length == 5) {
    		System.arraycopy(rid, 0, req, 1, 5);
    	}
    	
    	proto.sendRecv(Cmd.CmdType.EMV_DEL_CAPK, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    } 
    
    /**
     * <div class="zh">
     * 从EMV库中删除所有认证中心密钥
     * </div>
     * <div class="en">
     * Delete all CA public key.
     * </div>
     * @return 
     * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     * @throws BaseSystemException 
     */      
    public byte[] deleteAllCAPK(String sequenceID,String sessionCode) throws EmvException, IOException, ProtoException, CommonException, BaseSystemException {
    	byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
		/*String s="abort|123456|1234567890";*/
	    String s="deleteAllCAPK |"+sequenceID+"|"+sessionCode;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_DELETE_ALL_CAPK,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
    }
    
    /**
     * <div class="zh">
     * 从EMV库中读出一个认证中心密钥
     * </div>
     * <div class="en">
     * Get a CA public key.
     * </div>
     * 
     * @param keyId
	 * <div class="zh">
	 * 		密钥索引
	 * </div>
	 * <div class="en">
	 * 		The key storage index.
	 * </div>
	 * 
	 * @return
	 * <div class="zh">
	 * 		null: 指定的keyId不存在<br/>
	 * 		非null: 认证中心密钥
	 * </div>
	 * <div class="en">
	 * 		null: no CAPK for specified index<br/>
	 * 		non-null: CAPK
	 * </div>
	 * 
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */                    
    public EMV_CAPK getCAPK(int keyId) throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = (byte)keyId;
    	EMV_CAPK capk = new EMV_CAPK();
    	proto.sendRecv(Cmd.CmdType.EMV_GET_CAPK, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		capk.serialFromBuffer(respBuffer);
    		return capk;
    	} else {
    		return null;
    	}
    } 

    /**
     * <div class="zh">
     * <b><font color=red>注意: D180 不支持此功能</font></b><br/>
     * 检查EMV库中密钥的有效性 , 每次检测只返回一个过期密钥, 应用程序对过期密钥处理完后, 应该再次检测, 直到返回true为止
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function</font></b><br/>
     * Check the validity of the public keys. Only one expired key will be returned when calling this function. When the function returns false, the application should handle it and then continually call this function until it returns true.
     * </div>
     * 
     * @param keyId
	 * <div class="zh">
	 * 		[输入/输出]密钥索引, 仅当返回false时有效<br/>
	 * 		作为输入时, 可以为null,表示不输出keyId<br/>
	 * 		如果keyId不为null, 则输出keyId[0]表示过期密钥的keyId.
	 * </div>
	 * <div class="en">
	 * 		[input/output]The index of the expired key. valid only when function returns false<br/>
	 * 		As an input, no output keyId if keyId is null.
	 * </div>
	 * 
     * @param rid
	 * <div class="zh">
	 * 		[输入/输出]应用注册服务商ID, 仅当返回false时有效<br/>
	 * 		作为输入时, 可以为null,表示不输出rid, 否则输出rid<br/>
	 * </div>
	 * <div class="en">
	 * 		[input/output]The RID of the expired key. valid only when function returns false<br/>
	 * 		As an input, no output rid if rid is null.
	 * </div>
	 * 
	 * @return
	 * <div class="zh">
	 * 		true: 密钥有效<br/>
	 * 		false: 密钥过期, 可以检查keyId[0], rid返回值<br/>
	 * </div>
	 * <div class="en">
	 * 		true: key is valid<br/>
	 * 		false: key is expired, can further check keyId[0] and rid if needed<br/>
	 * </div>
	 * 
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */            
    public boolean checkCAPK(byte[] keyId, byte[] rid) throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.EMV_CHECK_CAPK, new byte[0], rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		return true;
    	} else {
    		if (keyId != null) {
    			keyId[0] = respBuffer[0];
    		}
    		
    		if (rid != null) {
    			System.arraycopy(respBuffer, 1, rid, 0, 5);
    		}
    		
    		return false;
    	}
    } 

    /**
     * <div class="zh">
     * 添加一个EMV应用到应用列表
     * </div>
     * <div class="en">
     * Add an EMV application to terminal application list.
     * </div>
     * 
     * @param app
	 * <div class="zh">
	 * 		[输入]应用列表数据
	 * 
	 * </div>
	 * <div class="en">
	 * 		[input]application data
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */ 
    public byte[] addApp(EMV_APPLIST app) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	int i,j;
    	String s="downloadAPP|123|D180EMDK|";
    	byte[] req1=s.getBytes();
    	byte[] req = app.serialToBuffer();
    	byte[] send=new byte[req.length+req1.length];
    	for(i=0;i<req1.length;i++)
    		send[i]=req1[i];
    	for(j=0;j<req.length;j++)
    		send[i++]=req[j];
    	for(i=0;i<10240;i++)
    		respBuffer[i]=0x00;
    	proto.sendRecv(Cmd.CmdType.MTLA_DOWNLOAD_APP, send, rc, respBuffer);
    	if (rc.code == 0) { 
    		//success
    		return respBuffer;
    	} else {
        	throw new EmvException(rc.code);
    	}
    }


    /**
     * <div class="zh">
     * 读取一个应用数据
     * </div>
     * <div class="en">
     * Get an application from the terminal application list.
     * </div>
     * 
     * @param index
	 * <div class="zh">
	 * 		应用存储索引范围
	 * </div>
	 * <div class="en">
	 * 		the application index
	 * </div>
	 * 
     * @return
	 * <div class="zh">
	 * 		null: 无指定应用
	 * 		非null: 应用数据
	 * </div>
	 * <div class="en">
	 * 		null: no specified application
	 * 		non-null: application data
	 * </div>
	 * 
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */ 
    public EMV_APPLIST getApp(int index) throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = (byte)index;
    	EMV_APPLIST app = new EMV_APPLIST();
    	proto.sendRecv(Cmd.CmdType.EMV_GET_APP, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		app.serialFromBuffer(respBuffer, 0);
    		return app;
    	} else {
        	return null;
    	}
    } 
    
    /**
     * <div class="zh">
     * <b><font color=red>注意: D180 不支持此功能 </font></b><br/>
     * 从EMV应用列表中删除一个应用
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function</font></b><br/>
     * Delete an application from the application list.
     * </div>
     * 
     * @param aid
	 * <div class="zh">
	 * 		[输入]应用标志, 压缩BCD码, 不大于16字节.
	 * </div>
	 * <div class="en">
	 * 		[input]Application ID, compressed BCD, no more than 16 bytes.
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */ 
    public void delApp(byte[] aid) throws EmvException, IOException, ProtoException, CommonException {
    	int aidLen = 0;
    	if (aid != null) {
    		aidLen = aid.length;
    	}
    	
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1 + aidLen];
    	req[0] = (byte)aidLen;
    	if (aidLen > 0) { 
    		System.arraycopy(aid, 0, req, 1, aidLen);
    	}
    	proto.sendRecv(Cmd.CmdType.EMV_DEL_APP, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 删除应用列表中的所有应用
     * </div>
     * <div class="en">
     * Delete all applications from the application list.
     * </div>
     * @return 
     * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     * @throws BaseSystemException 
     */ 
    public byte[] deleteAllAPP(String sequenceID,String sessionCode) throws ProtoException, IOException, CommonException, BaseSystemException{
    	byte[] resp = new byte[128];

	    RespCode rc = new RespCode();
		/*String s="abort|123456|1234567890";*/
	    String s="deleteAllAPP|"+sequenceID+"|"+sessionCode;
		byte[] req = s.getBytes();
		
	    proto.sendRecv(Cmd.CmdType.MTLA_DELETE_ALL_APP,req, rc, resp);
	    if (rc.code == 0) {
	    	//success
	    } else {
	    	throw new BaseSystemException(rc.code);    		
	    }
	    return resp;
    }
    
    /**
     * <div class="zh">
     * 获取当前最终选择应用的参数
     * <b><font color=red>注意: D180 不支持此功能</font></b><br/>
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function</font></b><br/>
     * Get the parameter of current finally selected application.
     * </div>
     * 
     * @return
	 * <div class="zh">
	 * 		null: 当前没有最终选择的应用
	 * 		非null: 应用参数
	 * </div>
	 * <div class="en">
	 * 		null: no finally selected application
	 * 		non-null: the parameter of current finally selected application
	 * </div>
	 * 
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */     
    public EMV_APPLIST getFinalAppPara() throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	EMV_APPLIST app = new EMV_APPLIST();
    	proto.sendRecv(Cmd.CmdType.EMV_GET_FINAL_APP_PARA, new byte[0], rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		app.serialFromBuffer(respBuffer, 0);
    		return app;
    	} else {
    		return null;
    	}
    }

    /**
     * <div class="zh">
     * 修改当前最终选择应用的参数
     * <b><font color=red>注意: 对于D180,由于内核没有保存添加应用时附带的应用参数,所以必须在app select后,获取选中的终端AID(9F06),并重新设置该应用相关参数到内核</font></b><br/>
     * </div>
     * <div class="en">
     * Modify the parameter of current finally selected application.
     * <b><font color=red>NOTE: for D180, since the parameters are not saved when adding application, so after app selection, please get the selected AID(9F06) and 
     * set the parameters with this method </font></b><br/>
     * </div>
     * 
     * @param app
	 * <div class="zh">
	 * 		[输入]APP数据
	 * </div>
	 * <div class="en">
	 * 		[input]application data
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */     
    public void modFinalAppPara(EMV_APPLIST app) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = app.serialToBuffer();
    	proto.sendRecv(Cmd.CmdType.EMV_MOD_FINAL_APP_PARA, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 获取与当前候选列表对应的标签列表数据
     * </div>
     * <div class="en">
     * Get the label list of the application candidate list.
     * </div>
     * 
     * @return
	 * <div class="zh">
	 * 		非null: 获得的标签列表数组 <br/> 
	 * 		null: 无标签列表数据
	 * </div>
	 * <div class="en">
	 * 		non-null: the array of the label list<br/>
	 * 		null: no label list
	 * </div>
	 * 
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */         
    public EMV_APPLABEL_LIST[] getLabelList() throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.EMV_GET_LABEL_LIST, new byte[0], rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		int appNum = respBuffer[0]; 
    		EMV_APPLABEL_LIST[] appLabelLists = new EMV_APPLABEL_LIST[appNum];
    		int len = new EMV_APPLABEL_LIST().serialToBuffer().length;
    		for (int i = 0; i < appNum; i++) {
    			appLabelLists[i] = new EMV_APPLABEL_LIST();    			
    			appLabelLists[i].serialFromBuffer(respBuffer, 1 + i * len);
    		}
    		return appLabelLists;
    	} else {
        	return null;
    	}
    }

    /**
     * <div class="zh">
     * 添加一个待回收的发卡行公钥证书数据到证书回收列表<br/>
     * 如果待添加的证书已存在, 则直接返回成功
     * </div>
     * <div class="en">
     * Add a revoked issuer public key certification to revoked certification list.
     * </div>
     * 
     * @param revocList
	 * <div class="zh">
	 * 		[输入]发卡行公钥证书数据结构
	 * </div>
	 * <div class="en">
	 * 		[input]the revoked issuer public key certification.
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */    
    public void addRevocList(EMV_REVOC_LIST revocList) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = revocList.serialToBuffer();
    	
    	proto.sendRecv(Cmd.CmdType.EMV_ADD_REVOC_LIST, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }
    
    /**
     * <div class="zh">
     * 从EMV库中删除一个发卡行公钥证书回收数据
     * </div>
     * <div class="en">
     * Delete a revoked issuer public key certification.
     * </div>
     * 
     * @param index
	 * <div class="zh">
	 * 		待删除的回收公钥证书数据对应的CA公钥索引
	 * </div>
	 * <div class="en">
	 * 		The corresponding CA public key index of the revoked issuer public key certification.
	 * </div>
	 *
     * @param rid
	 * <div class="zh">
	 * 		[输入]待删除的回收公钥证书数据对应的RID数据(5字节)
	 * </div>
	 * <div class="en">
	 * 		[input]The corresponding RID of the revoked issuer public key certification.
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */    
    public void delRevocList(int index, byte[] rid) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[6];
    	req[0] = (byte)index;
    	System.arraycopy(rid, 0, req, 1, rid.length);
    	
    	proto.sendRecv(Cmd.CmdType.EMV_DEL_REVOC_LIST, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }
    
    /**
     * <div class="zh">
     * 从EMV库中删除所有发卡行公钥证书回收数据列表
     * </div>
     * <div class="en">
     * Delete all revoked issuer public key certifications.
     * </div>
     * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */    
    public void delAllRevocList() throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.EMV_DEL_ALL_REVOC_LIST, new byte[0], rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 初始化EMV内核数据元存储结构<br/>
     * 此函数需要在每次交易开始前（如在应用选择之前）调用, 用于初始化EMV内核数据元存储结构
     * </div>
     * <div class="en">
     * Initialize the EMV kernel data element storage structure.<br/>
     * The function need to be called before the start of every transaction, for instance, before the application selection, to initialize the EMV kernel data element storage structure.
     * </div>
     * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */       
    public void initTLVData() throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.EMV_INIT_TLV_DATA, new byte[0], rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 选择EMV应用 <br/>
     * 1.要求在调用该函数前,应用应该通过{@link IccManager#iccDetect} 函数判断出slot指定的卡座中已经有EMV IC卡片
     * EMV要求如果返回码是：{@link #EMV_NO_APP}和{@link #EMV_DATA_ERR}, 就应该提示用户刷磁卡交易(应用可根据实际要求决定怎么做);<br/>
     * {@link #EMV_USER_CANCEL} 和 {@link #EMV_TIME_OUT} 返回码是由回调函数{@link EmvManager.EmvCallbackHandler#onWaitAppSel}返回的, 
     * 如果应用程序在{@link EmvManager.EmvCallbackHandler#onWaitAppSel}函数中并没有返回这两个返回码, 则本函数也不会返回这两个返回码<br/>
     * 2.ICC_RSP_6985 是GPO命令在应用只有一个或只剩一个的情况时, 卡片返回"6985"时返回的, 由应用决定终止交易还是FALLBACK<br/>
     * </div>
     * <div class="en">
     * EMV application selection.<br/>
     * 1.Before calling this function, the EMV IC card must be in the specified card slot which can be detected by the function {@link IccManager#iccDetect}.<br/>
	 * 2.According to the EMV specification, if it returns {@link #EMV_NO_APP} and {@link #EMV_DATA_ERR}, application must prompt for 'Swipe card'.(The application can decide how to do according to the actual requirement)<br/>
	 * 3.{@link EmvException#EMV_ERR_USER_CANCEL} and {@link EmvException#EMV_ERR_TIME_OUT} are returned by callback function {@link EmvManager.EmvCallbackHandler#onWaitAppSel}. If the function  {@link EmvManager.EmvCallbackHandler#onWaitAppSel} doesn't return these two values, this function does not return them either.<br/>
	 * 4.ICC_RSP_6985 is returned when GPO responses 6985 and there's no application left in the application candidate list, the terminal application should then decide whether to terminate the transaction or to fallback.<br/>
     * </div>
     * 
     * @param slot
     * <div class="zh">
     * 		卡座号 
     * </div>
     * <div class="en">
     * 		Card slot number.
     * </div>
     * 
     * @param transNo
     * <div class="zh">
     * 		本次交易的序号 
     * </div>
     * <div class="en">
     * 		The sequence number of the transaction.
     * </div>
     *  
     * @return
     * <div class="zh">
     * <ul>
     *		<li>{@link #EMV_OK}: 成功
     *		<li>{@link #ICC_RESET_ERR}: IC卡复位失败
     *		<li>{@link #ICC_CMD_ERR}: IC卡命令失败
     *		<li>{@link #ICC_BLOCK}: IC卡已锁
     *		<li>{@link #EMV_NO_APP}: 没有终端支持的EMV应用
     *		<li>{@link #EMV_APP_BLOCK}: 应用已锁
     *		<li>{@link #EMV_DATA_ERR}: 卡片数据格式错误
     *		<li>{@link #EMV_TIME_OUT}: 应用选择超时
     *		<li>{@link #EMV_USER_CANCEL}: 用户取消应用选择
     *		<li>{@link #ICC_RSP_6985}: GPO命令中, 卡片返回'6985'
     * </ul>
     * </div>
     * <div class="en">
     * <ul>
     *		<li>{@link #EMV_OK}: Succeed.
     *		<li>{@link #ICC_RESET_ERR}: IC card reset failed.
     *		<li>{@link #ICC_CMD_ERR}: IC card command failed.
     *		<li>{@link #ICC_BLOCK}: IC card has been blocked.
     *		<li>{@link #EMV_NO_APP}: There is no IC card application supported by terminal.
     *		<li>{@link #EMV_APP_BLOCK}: The EMV application has been blocked.
     *		<li>{@link #EMV_DATA_ERR}: IC card data format error.
     *		<li>{@link #EMV_TIME_OUT}: Application selection timeout.
     *		<li>{@link #EMV_USER_CANCEL}: Application selection is canceled by user.
     *		<li>{@link #ICC_RSP_6985}: IC card responses with 6985 when GPO.
     * </ul>
     * </div>
     *  
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */           
    public int appSelect(int slot, int transNo) throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[5];
    	req[0] = (byte)slot;
    	Utils.int2ByteArray(transNo, req, 1);
    	
    	proto.sendRecv(Cmd.CmdType.EMV_APP_SELECT, req, rc, respBuffer);
    	return rc.code;
    }

    /**
     * <div class="zh">
     * 读当前选择的应用的数据和交易金额等数据 <br/>
     * {@link EmvException#EMV_ERR_USER_CANCEL} 和{@link EmvException#EMV_ERR_TIME_OUT} 异常是由于回调函数{@link EmvCallbackHandler#onInputAmount}返回相应的值引起的, 
     * 如果应用程序在{@link EmvCallbackHandler#onInputAmount}函数中并没有返回这两个返回码, 则本函数也不会抛出这两个异常.<br/>
     * </div>
     * <div class="en">
     * Read the selected application's data, transaction amount, etc.<br/>
     * {@link EmvException#EMV_ERR_USER_CANCEL} and {@link EmvException#EMV_ERR_TIME_OUT} are returned by callback function {@link EmvCallbackHandler#onInputAmount}. If the function  {@link EmvCallbackHandler#onInputAmount} doesn't return these two values, this function does not return them either.<br/>
     * </div>
     *  
     * @throws EMVException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */           
    public void readAppData() throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	proto.sendRecv(Cmd.CmdType.EMV_READ_APP_DATA, new byte[0], rc, respBuffer);
    	if (rc.code == 0 ) {
    		//success
    	} else {
    		throw new EmvException(rc.code);
    	}
    }


    /**
     * <div class="zh">
     * 卡片数据认证 <br/>
     * 因为EMV要求认证失败不一定要终止交易, 所以该函数返回EMV_OK, 并不代表数据认证成功, 
     * 应用程序如果需要了解认证的结果, 可通过函数{@link EmvManager#getTLVData}查询TVR的值判断认证结果, 如果是CDA认证, 该函数只恢复IC卡密钥, 认证工作在后边的GAC命令时才进行
     * </div>
     * <div class="en">
     * IC card data authentication.<br/>
     * This function returning EMV_OK does not stand for data authentication succeeded. The application can get the result of the authentication through function {@link EmvManager#getTLVData} and query the value of TVR. If the authentication method is CDA , this function just recovers the IC card private key, the authentication will not be done until performing Generate AC command.
     * </div>
     * 
     * @return
     * <div class="zh">
     * <ul>
     *		<li>{@link #EMV_OK}: 成功
     *		<li>{@link #ICC_CMD_ERR}: IC卡命令失败
     *		<li>{@link #EMV_RSP_ERR}: IC卡返回码错误
     *		<li>{@link #EMV_DENIAL}: 交易被拒绝
     *</ul>
     * </div>
     * <div class="en">
     * <ul>
     *		<li>{@link #EMV_OK}: Complete.
     *		<li>{@link #ICC_CMD_ERR}: IC card command failed.
     *		<li>{@link #EMV_RSP_ERR}: IC card response code error.
     *		<li>{@link #EMV_DENIAL}: Transaction denied.
     *</ul>
     * </div>
     *  
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */         
    public int cardAuth() throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	proto.sendRecv(Cmd.CmdType.EMV_CARD_AUTH, new byte[0], rc, respBuffer);
    	return rc.code;
    }

    /**
     * <div class="zh">
     * <b><font color=red>注意: D180 不支持此功能</font></b><br/>
     * 处理EMV交易 <br/>
     * 1.{@link #EMV_USER_CANCEL}和{@link #EMV_TIME_OUT} 返回码是由回调函数{@link EmvCallbackHandler#onGetHolderPwd}返回的, 
     * 如果应用程序在{@link EmvCallbackHandler#onGetHolderPwd}函数中并没有返回这两个返回码, 则本函数也不会返回这两个返回码;<br/>
	 * 2.{@link #EMV_NOT_ACCEPT}和{@link #EMV_DENIAL}都是交易失败, 只是EMV要求：在服务不允许等情况下要返回{@link #EMV_NOT_ACCEPT}, 别的情况下返回{@link #EMV_DENIAL};<br/>
	 * 3.ICC_RSP_6985返回码是在执行Generate AC命令中, 卡片回送状态码为'6985'时返回的, 由应用决定是终止交易还是FALLBACK 
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function,</font></b><br/>
     * Process EMV transaction.<br/>
     * 1.{@link #EMV_USER_CANCEL} and {@link #EMV_TIME_OUT} are returned by callback function {@link EmvCallbackHandler#onGetHolderPwd}. If the function  {@link EmvCallbackHandler#onGetHolderPwd} doesn't return these two values, this function does not return them  either.<br/>
	 * 2.{@link #EMV_NOT_ACCEPT} and {@link #EMV_DENIAL} stand for transaction failed. According to the EMV specification, {@link #EMV_NOT_ACCEPT} will be returned on condition that the service does not accepted, and {@link #EMV_DENIAL} will be returned on other conditions.<br/>
	 * 3.ICC_RSP_6985 is returned when GAC responses 6985, the terminal application should then decide whether to terminate the transaction or to fallback.

     * </div>
     * 
     * @return
     * <div class="zh">
     *		<ul>
     *			<li>{@link #EMV_OK}: 成功
     *			<li>{@link #ICC_CMD_ERR}: IC卡命令失败
     *			<li>{@link #EMV_RSP_ERR}: IC卡返回码错误
     *			<li>{@link #EMV_DATA_ERR}: 卡片数据格式错误
     *			<li>{@link #EMV_NOT_ACCEPT}: 交易不接受
     *			<li>{@link #EMV_DENIAL}: 交易拒绝
     *			<li>{@link #EMV_TIME_OUT}: 应用选择超时
     *			<li>{@link #EMV_USER_CANCEL}: 用户取消应用选择
     *			<li>{@link #ICC_RSP_6985}: GAC命令中,卡片回送'6985'
     *		</ul>  
     * </div>
     * <div class="en">
     *		<ul>
     *			<li>{@link #EMV_OK}: Succeed.
     *			<li>{@link #ICC_CMD_ERR}: IC card command failed.
     *			<li>{@link #EMV_RSP_ERR}: IC card response code error.
     *			<li>{@link #EMV_DATA_ERR}: IC card data format error.
     *			<li>{@link #EMV_NOT_ACCEPT}: Transaction cannot be accepted.
     *			<li>{@link #EMV_DENIAL}: Transaction denied.
     *			<li>{@link #EMV_TIME_OUT}: Input PIN timeout.
     *			<li>{@link #EMV_USER_CANCEL}: Transaction is canceled by user.
     *			<li>{@link #ICC_RSP_6985}: ICC response 6985 in GAC.
     *		</ul>  
     * </div>
     * 
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */               
    public int procTrans() throws IOException, ProtoException, CommonException{
    	RespCode rc = new RespCode();
    	
		//since we may change the receive timeout in onGetHolderPwd
    	int savedRecvTimeout = cfg.receiveTimeout;
    	try {
    		proto.sendRecv(Cmd.CmdType.EMV_PROC_TRANS, new byte[0], rc, respBuffer);
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    		MyLog.i(TAG, "restore receive timeout to: " + savedRecvTimeout);
    	}
    	return rc.code;
    }
    
     /**
      * <div class="zh">
      * 为读取交易日志的应用选择（PBOC2.0兼容EMV2）<br/>
      * 该函数和{@link EmvManager#appSelect}几乎一样, 不同的是在此应用选择中, 根据不同的参数设置, 可将被锁定的应用添加到应用列表中
      * </div>
      * <div class="en">
      * Read the card's transaction log.<br/>
      * This function is similar with the function {@link EmvManager#appSelect}. The difference between them is that this function can add the block application to candidate list base on the different parameter setting when the application selection is done.
      * </div>
      * 
      * @param slot
      * <div class="zh">
      * 	卡座号 
      * </div>
      * <div class="en">
      * 	Card slot number.
      * </div>
      * 
      * @param flag
      * <div class="zh">
      * 	锁定的应用是否加入候选列表, 0-加入, 1-不加入 (默认为0)
      * </div>
      * <div class="en">
      * 	Decide whether the block application will be added to candidate list or not.  0-Add, 1-Not add (The default is 0)
      * </div>
      *
      * @return
      * <div class="zh">
      *		<ul>
      *			<li>{@link #EMV_OK}: 成功
      *			<li>{@link #ICC_RESET_ERR}: IC卡复位失败
      *			<li>{@link #ICC_CMD_ERR}: IC卡命令失败
      *			<li>{@link #ICC_BLOCK}: IC卡已锁
      *			<li>{@link #EMV_NO_APP}: 没有终端支持的EMV应用
      *			<li>{@link #EMV_DATA_ERR}: 卡片数据格式错误
      *			<li>{@link #EMV_TIME_OUT}: 应用选择超时
      *			<li>{@link #EMV_USER_CANCEL}: 用户取消应用选择
      *		</ul>  
      * </div>
      * <div class="en">
      *		<ul>
      *			<li>{@link #EMV_OK}: Succeed.
      *			<li>{@link #ICC_RESET_ERR}: IC card reset failed.
      *			<li>{@link #ICC_CMD_ERR}: IC card command failed.
      *			<li>{@link #ICC_BLOCK}: IC card has been blocked.
      *			<li>{@link #EMV_NO_APP}: No EMV application that terminal supported.
      *			<li>{@link #EMV_DATA_ERR}: Card data format error.
      *			<li>{@link #EMV_TIME_OUT}: Application selection timeout.
      *			<li>{@link #EMV_USER_CANCEL}: Application selection is canceled by user.
      *		</ul>  
      * </div>
      * 
      * @throws IOException
      * <div class="zh">通信错误</div>
      * <div class="en">communication error</div>
      * @throws ProtoException
      * <div class="zh">协议错误</div>
      * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
      */       
    public int appSelectForLog(int slot, int flag) throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	req[0] = (byte)slot;
    	req[1] = (byte)flag;
    	
    	proto.sendRecv(Cmd.CmdType.EMV_APP_SELECT_FOR_LOG, req, rc, respBuffer);
    	return rc.code;
    }

    /**
     * <div class="zh">
     * 读交易日志（PBOC2.0兼容EMV2）<br/>
     * 应用程序在用 {@link EmvManager#appSelectForLog} 完成应用选择之后, 调用该函数读取已选定的应用的交易日志.RecordNo记录号从1开始
     * 该函数只是把交易日志读到EMV内核的缓冲中, 应用程序可以通过{@link EmvManager#getLogItem}读取具体的日志内容, 比如交易金额、时间等等.
     * </div>
     * <div class="en">
     * Read transaction log<br/>
     * After the application finished the application selection with function {@link EmvManager#appSelectForLog}, 
     * this function can be called to read the transaction log of the selected application.
     * RecordNo, Record number begins at 1.
     * This function can only read the transaction log to the EMV kernel buffer. Application can read 
     * the specific log by the function {@link EmvManager#getLogItem}, such as transaction amount, transaction time and so on.
     * </div>
     * 
     * @param recordNo
     * <div class="zh">
     * 		记录号
     * </div>
     * <div class="en">
     * 		Record number
     * </div>
     * 
     * @return
     * <div class="zh">
     *		true: 成功 <br/>
     *  	false: 记录不存在或数据错误
     * </div>
     * <div class="en">
     * </div>
     * 
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */ 
    public boolean readLogRecord(int recordNo) throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[4];
    	Utils.int2ByteArray(recordNo, req, 0);
    	
    	proto.sendRecv(Cmd.CmdType.EMV_READ_LOG_RECORD, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		return true;
    	} else {
        	return false;
    	}
    }

    /**
     * <div class="zh">
     * 读交易日志的数据项（PBOC2.0兼容EMV2）<br/>
     * 对于通过{@link EmvManager#readLogRecord} 函数读出的每一个交易日志记录, 应用程序都可以通过本函数去读取具体的日志信息
     * </div>
     * <div class="en">
     * Read the data items of transaction log(PBOC2.0 compatible EMV2) <br/>
     * For each transaction log record that was read by function {@link EmvManager#readLogRecord}, 
     * application can read the specific log information by this function. 
     * </div>
     * 
     * @param tag
     * <div class="zh">
     * 		需要读取的数据项的标签
     * </div>
     * <div class="en">
     *		The tag of data items that need to read. 
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		null: 指定的日志项不存在或数据错误 <br/>
     * 		非null: 数据项的值
     * </div>
     * <div class="en">
     * 		null: log record doesn't exist or card data error
     * 		non-null: the log item read
     * </div>
     *  
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */
    public byte[] getLogItem(int tag) throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	Utils.short2ByteArray((short)tag, req, 0);

    	proto.sendRecv(Cmd.CmdType.EMV_GET_LOG_ITEM, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		int len = Utils.intFromByteArray(respBuffer, 0);
    		byte[] dataOut = new byte[len];
    		System.arraycopy(respBuffer, 4, dataOut, 0, len);
    		return dataOut;
    	} else {
        	return null;
    	}
    }

    /**
     * <div class="zh">
     * 获取内核中的MCK参数
     * </div>
     * <div class="en">
     * Get the kernel MCK parameter.
     * </div>
     * 
     * @return
     * <div class="zh">
     *		输出的MCK参数
     * </div>
     * <div class="en">
     * 		the MCK paramter
     * </div>
     *  
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */       
    public EMV_MCK_PARAM getMCKParam() throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	EMV_MCK_PARAM mckParam = new EMV_MCK_PARAM();
    	proto.sendRecv(Cmd.CmdType.EMV_GET_MCK_PARAM, new byte[0], rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		mckParam.serialFromBuffer(respBuffer);
    		return mckParam;
    	} else {
        	throw new EmvException(rc.code);
    	}
    }
    
    /**
     * <div class="zh">
     * 设置MCK配置相关的部分参数
     * </div>
     * <div class="en">
     * Set MCK parameter.
     * </div>
     * 
     * @param mckParam
     * <div class="zh">
     *		待设置MCK参数
     *</div>
     *<div class="en">
     *		MCK param to set
     *</div>
     *  
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */       
    public void setMCKParam(EMV_MCK_PARAM mckParam) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = mckParam.serialToBuffer(); 
    	proto.sendRecv(Cmd.CmdType.EMV_SET_MCK_PARAM, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }
    
    /**
     * <div class="zh">
     * 设置终端电子现金相关参数
     * </div>
     * <div class="en">
     * Set the terminal electronic cash related parameters.
     * </div>
     * 
     * @param tmEcpParam
     * <div class="zh">
     *		待设置电子现金参数
     *</div>
     *<div class="en">
     *		The electronic cash parameters which need to be set.
     *</div>
     *  
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */           
    public void setTmECPParam(EMV_TM_ECP_PARAM tmEcpParam) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = tmEcpParam.serialToBuffer(); 
    	proto.sendRecv(Cmd.CmdType.EMV_SET_TM_ECP_PARAM, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }    

    /**
     * <div class="zh">
     * 从卡片中读取电子现金余额
     * </div>
     * <div class="en">
     * Read the electronic cash balance from card.
     * </div>
     * 
     * @return
     * <div class="zh">
     *		读取到的电子现金余额
     *</div>
     *<div class="en">
     *		the electronic cash balance read
     *</div>
     *  
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */           
    public int getCardEcbBalance() throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	proto.sendRecv(Cmd.CmdType.EMV_GET_CARD_ECB_BALANCE, new byte[0], rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		int balance = Utils.intFromByteArray(respBuffer, 0);
    		return balance;
    	} else {
        	throw new EmvException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 实现处理限制、持卡人验证、终端风险管理及第一次GAC操作 <br/>
     * 对于"交易脱机拒绝, 要求发送通知"的情况由应用根据CID的取值判断是否需进行通知消息处理
     * </div>
     * <div class="en">
     * Performing process restrict, cardholder verification, terminal risk management & 1st GAC.<br/>
     * For the transaction result in sending advice message when declined, developer should get the CID from kernel, and check if the advice message is needed.
     * </div>
     * 
     * @param authAmt
     * <div class="zh">
     *		该金额将覆盖在{@link EmvManager.EmvCallbackHandler#onInputAmount}函数中auth amount (amts[0])的值
     * </div>
     * <div class="en">
     * 		This authorization amount will overwrite the auth amount(amts[0]) in {@link EmvManager.EmvCallbackHandler#onInputAmount}.
     * </div>
     * 
     * @param cashBackAmt
     * <div class="zh">
     *		该金额将覆盖在{@link EmvManager.EmvCallbackHandler#onInputAmount}函数中callback amount (amts[1])的值
     * </div>
     * <div class="en">
     * 		This authorization amount will overwrite the cashback amount(amts[1]) in {@link EmvManager.EmvCallbackHandler#onInputAmount}.
     * </div>
     * 
     * @return
     * <div class="zh">
     *		int[0]: 交易结果 <br/>
     *		<ul>
     *			<li>{@link #EMV_OK}: 交易处理成功
     *			<li>{@link #EMV_DENIAL}: 交易拒绝
     *			<li>{@link #EMV_DATA_ERR}: 卡片数据格式错误
     *			<li>{@link #EMV_NOT_ACCEPT}: 交易不接受
     *			<li>{@link #ICC_CMD_ERR}: IC卡命令失败
     *			<li>{@link #ICC_RSP_6985}: GAC中卡片回送6985
     *			<li>{@link #EMV_RSP_ERR}: GAC应答错误
     *			<li>{@link #EMV_PARAM_ERR}: 参数错误
     *		</ul>  
     *
     *		int[1]: AC type<br/>
     *		<ul>
     *			<li>{@link #EMV_AC_TC}
     *			<li>{@link #EMV_AC_AAC}
     *			<li>{@link #EMV_AC_ARQC}
     *		</ul>
     *
     * </div>
     * <div class="en">
     *		int[0]: transaction result <br/>
     *		<ul>
     *			<li>{@link #EMV_OK}: Succeed.
     *			<li>{@link #EMV_DENIAL}: Transaction denied.
     *			<li>{@link #EMV_DATA_ERR}: IC card data format error.
     *			<li>{@link #EMV_NOT_ACCEPT}: Transaction is not accepted.
     *			<li>{@link #ICC_CMD_ERR}: IC card command failed.
     *			<li>{@link #ICC_RSP_6985}: ICC response 6985 in 1st GAC.
     *			<li>{@link #EMV_RSP_ERR}: IC card response code error.
     *			<li>{@link #EMV_PARAM_ERR}: Parameter error.
     *		</ul>  
     *
     *		int[1]: AC type<br/>
     *		<ul>
     *			<li>{@link #EMV_AC_TC}
     *			<li>{@link #EMV_AC_AAC}
     *			<li>{@link #EMV_AC_ARQC}
     *		</ul>
     *     * </div>
     *  
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */               
    //returns int[0] -- ok/denial/not_accept...,   int[1] -- ac type
    public int[] startTrans(int authAmt, int cashBackAmt) throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[8];
		Utils.int2ByteArray(authAmt, req, 0);    	
		Utils.int2ByteArray(cashBackAmt, req, 4);    	

		//since we may change the receive timeout in onGetHolderPwd
    	int savedRecvTimeout = cfg.receiveTimeout;		
		try {
    		proto.sendRecv(Cmd.CmdType.EMV_START_TRANS, req, rc, respBuffer);
    	} finally {
    		cfg.receiveTimeout = savedRecvTimeout;
    		MyLog.i(TAG, "restore receive timeout to: " + savedRecvTimeout);    		
    	}
    	int[] ret = new int[2];
		ret[0] = rc.code;
		ret[1] = respBuffer[0];
		return ret;
    }

    /**
     * <div class="zh">
     * 实现对联机应答数据的处理（外部认证、脚本处理）及第二次GAC操作 <br/>
     * 1.联机批准或拒绝时, 若有以下数据, 则需由应用通过SetTLV方式进行设置：ARC-8A, AC-89, IAD-91 <br/>
     * 2.对于"交易脱机拒绝, 要求发送通知"的情况由应用根据CID的取值判断是否需进行通知消息处理
     * </div>
     * <div class="en">
     * Performing online data processing (issuer authentication, script processing, etc) & 2nd GAC.<br/>
     * 1.The following data element, if exists, must be sent to kernel through EMVSetTLVData before calling this function: ARC-8A, AC-89, IAD-91<br/>
	 * 2.For the transaction result in sending advice message when declined, developer should get the CID from kernel, and check if the advice message is needed.
     * </div>
     * 
     * @param commuStatus
     * <div class="zh">
     * 	<ul>
     *		<li>{@link #EMV_ONLINE_APPROVE}: 联机批准, 或者 host 返回参考时, 操作员选择批准
     *		<li>{@link #EMV_ONLINE_FAILED}: 无法联机
     *		<li>{@link #EMV_ONLINE_DENIAL}: 联机拒绝, 或者 host 返回参考时, 操作员选择拒绝
     *	</ul>
     *
     * </div>
     * <div class="en">
     * 	<ul>
     *		<li>{@link #EMV_ONLINE_APPROVE}: Online approved or online referral approved
     *		<li>{@link #EMV_ONLINE_FAILED}: Online failed
     *		<li>{@link #EMV_ONLINE_DENIAL}: Online rejected or online referral rejected
     *	</ul>
     * </div>
     * 
     * @param script
     * <div class="zh">
     *		[输入]脚本数据（TLV格式）
     * </div>
     * <div class="en">
     * 		[input]Issuer script data in TLV format
     * </div>
     * 
     * @return
     * <div class="zh">
     *		int[0]: 交易结果 <br/>
     *		<ul>
     *			<li>{@link #EMV_OK}: 交易处理成功
     *			<li>{@link #EMV_DENIAL}: 交易拒绝
     *			<li>{@link #EMV_DATA_ERR}: 卡片数据格式错误
     *			<li>{@link #EMV_NOT_ACCEPT}: 交易不接受
     *			<li>{@link #ICC_CMD_ERR}: IC卡命令失败
     *			<li>{@link #ICC_RSP_6985}: GAC中卡片回送6985
     *			<li>{@link #EMV_RSP_ERR}: GAC应答错误
     *			<li>{@link #EMV_PARAM_ERR}: 参数错误
     *		</ul>  
     *
     *		int[1]: AC type<br/>
     *		<ul>
     *			<li>{@link #EMV_AC_TC}: 交易批准
     *			<li>{@link #EMV_AC_AAC}: 终端请求TC, 而卡片CID返回拒绝
     *			<li>{@link #EMV_AC_AAC_HOST}: 因联机时返回EMV_ONLINE_DENIAL{@link #EMV_ONLINE_DENIAL}而拒绝
     *		</ul>
     *
     * </div>
     * <div class="en">
     *		int[0]: transaction result <br/>
     *		<ul>
     *			<li>{@link #EMV_OK}: Succeed
     *			<li>{@link #EMV_DENIAL}: transaction denied
     *			<li>{@link #EMV_DATA_ERR}: IC card data format error.
     *			<li>{@link #EMV_NOT_ACCEPT}: transaction not accept
     *			<li>{@link #ICC_CMD_ERR}: IC card command failed.
     *			<li>{@link #ICC_RSP_6985}: IC card responses with 6985 when GPO.
     *			<li>{@link #EMV_RSP_ERR}: IC card response code error.
     *			<li>{@link #EMV_PARAM_ERR}: parameter error
     *		</ul>  
     *
     *		int[1]: AC type<br/>
     *		<ul>
     *			<li>{@link #EMV_AC_TC}: transaction approved
     *			<li>{@link #EMV_AC_AAC}: terminal request TC, but rejected by Card CID
     *			<li>{@link #EMV_AC_AAC_HOST}: rejected because of online transaction returns {@link #EMV_ONLINE_DENIAL}而拒绝
     *		</ul>
     *     * </div>
     *  
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */                   
    //returns int[0] -- ok/denial/not_accept...,   int[1] -- ac type
    public int[] completeTrans(int commuStatus, byte[] script) throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[5 + script.length];
    	req[0] = (byte)commuStatus;
    	Utils.int2ByteArray(script.length, req, 1);
    	System.arraycopy(script, 0, req, 5, script.length);
    	
    	proto.sendRecv(Cmd.CmdType.EMV_COMPLETE_TRANS, req, rc, respBuffer);
    	int[] ret = new int[2];
		ret[0] = rc.code;
		ret[1] = respBuffer[0];
		return ret;
    }

    /**
     * <div class="zh">
     * 设置配置参数
     * </div>
     * <div class="en">
     * Set configuration.
     * </div>
     * 
     * @param flag
     * <div class="zh">
     * 		目前仅bit 1 - bit 3有效, 其他位留待扩展. <br/>
     * 		bit 1： <br/>
     *		1 -支持advice；{@link #EMV_CONFIG_FLAG_BIT_SUPPORT_ADVICE} <br/>
     *		0 -不支持advice（默认为0）；<br/>
     *		bit 2： <br/>
     *		1 -无PIN输入也要持卡人确认金额； {@link #EMV_CONFIG_FLAG_BIT_CONFIRM_AMT_WHEN_NO_PIN}<br/>
     *		0 -无PIN输入不需要持卡人确认金额（默认为0） <br/>
     *		bit 3： <br/>
     *		1 -支持交易日志； .{@link #EMV_CONFIG_FLAG_BIT_SUPPORT_TRANSLOG} <br/>
     *		0 -不支持交易日志<br/>
     * </div>
     * <div class="en">
	 *	Only bit 1(the lowest one) & bit 2 are valid at moment.<br/>
	 *	bit 1:	1 = support advice<br/>
	 *		0 = not support (default)<br/>
	 *	bit 2:	1= always asking  user to confirm amount even no PIN input is required<br/>
	 *		0 = does not ask user to confirm when no PIN input is required (default)<br/>
	 *	bit 3:   1 = support transaction log<br/>
	 *	       0 = not support transaction log<br/>
     * </div>
     *   
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */                      
    public void setConfigFlag(int flag) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[1];
    	req[0] = (byte)flag;
    	
    	proto.sendRecv(Cmd.CmdType.EMV_SET_CONFIG_FLAG, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 设置最终选择以及GPO命令中卡片返回的数据 <br/>
     * <b>此函数仅用于标准非接PBOC应用</b>
     * </div>
     * <div class="en">
     * Set response data of final selection and GPO command into EMV kernel.<br/>
     * <b>This function is only used for Clss PBOC application</b>
     * </div>
     * 
     * @param transParam
     * <div class="zh">
     * 		[输入]交易相关参数, 参见 {@link model.CLSS_TRANS_PARAM}
     * </div>
     * <div class="en">
     * 		[input]Transaction related parameters
     * </div>
     *   
     * @param selData
     * <div class="zh">
     * 		[输入]最终选择命令返回的数据, 必须通过 {@link ClssManager.Entry#getFinalSelectData}来获取
     * </div>
     * <div class="en">
     * 		[input]Response data of final selection command.,which need to be got by  {@link ClssManager.Entry#getFinalSelectData} in Entry library
     * </div>
     *   
     * @param GPOData
     * <div class="zh">
     * 		[输入]最终选择命令返回的数据, 必须通过 {@link ClssManager.Pboc#getGPOData}来获取
     * </div>
     * <div class="en">
     * 		[input]The data of GPO command and response, which need to be got by  {@link ClssManager.Pboc#getGPOData} in qPBOC library.
     * </div>
     * 
     * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */       
    public void switchClss(CLSS_TRANS_PARAM transParam, byte[] selData, byte[] GPOData) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] tp = transParam.serialToBuffer();
    	byte[] req = new byte[tp.length + 2 + selData.length + 2 + GPOData.length];
    	System.arraycopy(tp, 0, req, 0, tp.length);
    	
    	Utils.short2ByteArray((short)selData.length, req, tp.length);
    	System.arraycopy(selData, 0, req, tp.length + 2, selData.length);
    	
    	Utils.short2ByteArray((short)GPOData.length, req, tp.length + 2 + selData.length);
    	System.arraycopy(GPOData, 0, req, tp.length + 2 + selData.length + 2, GPOData.length);
    	
    	proto.sendRecv(Cmd.CmdType.EMV_SWITCH_CLSS, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }
    
    /**
     * <div class="zh">
     * 设置交易金额和返现金额 <br/>
     * 超过0xFFFFFFFF的金额可用此接口进行传送金额到内核, 请在{@link EmvManager#procTrans}或{@link EmvManager#startTrans}之前调用此接口,
     * 若调用此接口, {@link EmvManager.EmvCallbackHandler#onInputAmount}中请将authAmt和callBackAmt均设置为0并返回{@link #EMV_OK}
     * </div>
     * <div class="en">
     * Set transaction amount and cashback amount.<br/>
     * If the amount exceeds 0xffffffff, please use this function to set the amount to kernel.
	 * Please call this function before {@link EmvManager#procTrans} or {@link EmvManager#startTrans}.
	 * If use this function to set amount, please make {@link EmvManager.EmvCallbackHandler#onInputAmount} return two zero amount.
     * </div>
     * 
     * @param authAmt
     * <div class="zh">
     * 		交易金额, 请提供可见字符串(如"112345678900"). 函数内部会转换成 9F02数据元的格式(即"\x11\x23\x45\x67\x89\x00")
     * </div>
     * <div class="en">
     * 		Authorised Amount, please provide with human-readable string.
     * Inside this method, the string will be translated to be conform to the format of tag '9F02'(i.e."\x11\x23\x45\x67\x89\x00").

     * </div>
     *   
     * @param cashBackAmt
     * <div class="zh">
     * 		返现金额, 请提供可见字符串(如"112345678900"). 函数内部会转换成 9F03数据元的格式(即"\x11\x23\x45\x67\x89\x00")
     * </div>
     * <div class="en">
     * 		cashback Amount, please provide with human-readable string(say "112345678900").
     * Inside this method, the string will be translated to be conform to the format of tag '9F03'(i.e."\x11\x23\x45\x67\x89\x00").
     * </div>
     * 
	 * @throws EmvException
     * <div class="zh">EMV错误</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */      
    public void setAmount(String authAmt, String cashBackAmt) throws EmvException, IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] aa = Utils.str2Bcd(authAmt);
    	byte[] ca = Utils.str2Bcd(cashBackAmt);
    	if (aa.length > 6) {
    		MyLog.e(TAG, "authAmt length is too long! max length is 12!");
    		throw new EmvException(EMV_PARAM_ERR);
    	}
    	if (ca.length > 6) {
    		MyLog.e(TAG, "cashBackAmt length is too long! max length is 12!");
    		throw new EmvException(EMV_PARAM_ERR);
    	}
    	
    	byte[] req = new byte[12];
    	System.arraycopy(aa, 0, req, 6 - aa.length, aa.length);
    	System.arraycopy(ca, 0, req, 12 - ca.length, ca.length);
    	
    	proto.sendRecv(Cmd.CmdType.EMV_SET_AMOUNT, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    	} else {
        	throw new EmvException(rc.code);
    	}
    }

    /**
     * <div class="zh">
     * 读圈存日志（PBOC3.0） <br/>
     * 应用程序在用 {@link EmvManager#appSelectForLog} 完成应用选择之后, 调用该函数读取已选定的应用的交易日志, recordNo记录号从1开始;<br/>
     * 该函数只是把交易日志读到EMV内核的缓冲中, 应用程序可以通过{@link EmvManager#getSingleLoadLogItem}读取具体的日志内容, 比如交易金额、时间等等
     * </div>
     * <div class="en">
     * read load log of selected application(PBOC3.0)<br/>
     * after application selection with  {@link EmvManager#appSelectForLog}, calling this function to read the transaction log of the selected application.
     * the recordNo is from 1. <br/>
     * This function is to read the logs into EMV kernel buffer, further calling {@link EmvManager#getSingleLoadLogItem} to read the contents of the log.
     * </div>
     * 
     * @param recordNo
     * <div class="zh">
     * 		记录号, 从1开始
     * </div>
     * <div class="en">
     * 		record no. starting from 1
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		true: 读取圈存日志成功 <br/>
     * 		false: 数据不存在或数据错误
     * </div>
     * <div class="en">
     * 		true: read load log succeeded<br/>
     * 		false: no specified log data.
     * </div>
     * 
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */     
    public boolean readSingleLoadLog(int recordNo) throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[4];
    	Utils.int2ByteArray(recordNo, req, 0);
    	
    	proto.sendRecv(Cmd.CmdType.EMV_READ_SINGLE_LOAD_LOG, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		return true;
    	} else {
        	return false;
    	}
    }

    /**
     * <div class="zh">
     * 读圈存日志的数据项（PBOC3.0） <br/>
     * 对于通过{@link EmvManager#readSingleLoadLog}函数读出的每一个圈存日志记录, 应用程序都可以通过本函数去读取具体的日志信息
     * </div>
     * <div class="en">
     * read load log item (PBOC3.0) <br/>
     * read contents of the log which is loaded with {@link EmvManager#readSingleLoadLog} previously
     * </div>
     * 
     * @param tag
     * <div class="zh">
     * 		需要读取的数据项的标签
     * </div>
     * <div class="en">
     * 		the tag to read
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		null: 数据不存在或数据错误<br/>
     * 		非null: 数据项的值
     * </div>
     * <div class="en">
     * 		null: no data for specified tag<br/>
     * 		non-null: the value of the specified tag
     * </div>
     * 
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */    
    public byte[] getSingleLoadLogItem(int tag) throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	byte[] req = new byte[2];
    	Utils.short2ByteArray((short)tag, req, 0);

    	proto.sendRecv(Cmd.CmdType.EMV_GET_SINGLE_LOAD_LOG_ITEM, req, rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		int len = Utils.intFromByteArray(respBuffer, 0);
    		byte[] dataOut = new byte[len];
    		System.arraycopy(respBuffer, 4, dataOut, 0, len);
    		return dataOut;
    	} else {
        	return null;
    	}
    }

    /**
     * <div class="zh">
     * 一次性读取所有圈存日志（PBOC3.0） <br/>
     * 读取出来的圈存日志格式请参考PBOC 3.0第13册表12及表13
     * </div>
     * <div class="en">
     * read all load log (PBOC3.0)<br/>
     * please refer to PBOC3.0 book13, table 12 and table 13 for the format of the load log.
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		null: 数据不存在或数据错误<br/>
     * 		非null: 圈存日志数据
     * </div>
     * <div class="en">
     * 		null: no load log data<br/>
     * 		non-null: the load log data
     * </div>
     * 
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */    
    public byte[] readAllLoadLogs() throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();

    	proto.sendRecv(Cmd.CmdType.EMV_READ_ALL_LOAD_LOGS, new byte[0], rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		int len = Utils.intFromByteArray(respBuffer, 0);
    		byte[] dataOut = new byte[len];
    		System.arraycopy(respBuffer, 4, dataOut, 0, len);
    		return dataOut;
    	} else {
        	return null;
    	}
    }

    /**
	 * Get the log data which is read from card. <br/>
	 * 1. If the application read loading log before calling this function, the log data which is got by this function is loading log data. Please refer to PBOC 3.0 Book 13 for the format of the loading log data.<br/>
	 * 2. If the application read transaction log before calling this function, the log data which is got by this function is transaction log data. Please refer to PBOC 3.0 Book 13 for the format of the transaction log data.
	 * 
     * @return
     * 		null: no log data<br/>
     * 		non-null: the log data
     * 
     * @throws IOException
     * <div class="zh">通信错误</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">协议错误</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">通用错误</div>
     * <div class="en">common error</div>
     */    
    public byte[] getLogData() throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();

    	proto.sendRecv(Cmd.CmdType.EMV_GET_LOG_DATA, new byte[0], rc, respBuffer);
    	if (rc.code == 0) {
    		//success
    		int len = Utils.intFromByteArray(respBuffer, 0);
    		byte[] dataOut = new byte[len];
    		System.arraycopy(respBuffer, 4, dataOut, 0, len);
    		return dataOut;
    	} else {
        	return null;
    	}
    }    
}
