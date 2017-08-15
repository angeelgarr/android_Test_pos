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
 * EmvManager ���ڴ���EMV����
 * </div>
 * <div class="en">
 * EmvManager is used to process EMV transation
 * </div>
 *
 */
public class EmvManager {
	//return codes
	public static final int EMV_OK								= 0;         //�ɹ�

	public static final int ICC_RESET_ERR						= -1;         //IC����λʧ��
	public static final int ICC_CMD_ERR							= -2;         //IC����ʧ��
	public static final int ICC_BLOCK							= -3;         //IC������    

	public static final int EMV_RSP_ERR							= -4;         //IC���������
	public static final int EMV_APP_BLOCK						= -5;         //Ӧ������
	public static final int EMV_NO_APP							= -6;         //��Ƭ��û��EMVӦ��
	public static final int EMV_USER_CANCEL						= -7;         //�û�ȡ����ǰ��������
	public static final int EMV_TIME_OUT						= -8;         //�û�������ʱ
	public static final int EMV_DATA_ERR						= -9;         //��Ƭ���ݴ���
	public static final int EMV_NOT_ACCEPT						= -10;        //���ײ�����
	public static final int EMV_DENIAL							= -11;        //���ױ��ܾ�
	public static final int EMV_KEY_EXP							= -12;        //��Կ����
	public static final int EMV_NO_PINPAD     					= -13;        //û��������̻���̲����� 
	public static final int EMV_NO_PASSWORD   					= -14;        //û��������û��������������� 
	public static final int EMV_SUM_ERR       					= -15;        //��֤������ԿУ��ʹ��� 
	public static final int EMV_NOT_FOUND     					= -16;        //û���ҵ�ָ�������ݻ�Ԫ�� 
	public static final int EMV_NO_DATA       					= -17;        //ָ��������Ԫ��û������ 
	public static final int EMV_OVERFLOW      					= -18;        //�ڴ���� 
	public static final int NO_TRANS_LOG      					= -19; 
	public static final int RECORD_NOTEXIST   					= -20; 
	public static final int LOGITEM_NOTEXIST  					= -21; 
	public static final int ICC_RSP_6985      					= -22;        // GAC�п�Ƭ����6985, ��Ӧ�þ����Ƿ�fallback
	
	public static final int CLSS_USE_CONTACT 					= -23;    // ����ʹ������������н���
	public static final int EMV_FILE_ERR      					= -24;
	public static final int CLSS_TERMINATE    				 	= -25;    // Ӧ��ֹ����       -25 
	public static final int CLSS_FAILED       					= -26;    // ����ʧ�� 20081217 
	public static final int CLSS_DECLINE      					= -27;   
	public static final int EMV_PARAM_ERR 						= -30;		
	public static final int CLSS_PARAM_ERR            			= -30; // -26 // ��EMV �ں��еĲ���������Ϊ-30
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
     * ת����������ַ���
     * </div>
     * <div class="en">
     * transform return code to human-readable string
     * </div>
     * 
     * @param retCode
     * <div class="zh">
     * 		������
     * </div>
     * <div class="en">
     * 		return code
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		�������Ӧ���ַ���
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
     * EMV callback �������ӿ�,Ӧ�ó���Ӧ��ʵ����Щ�ӿ�
     * </div>
     * <div class="en">
     * EMV callback handler interface, APP should implement this interface.
     * </div>
     */
    public static interface EmvCallbackHandler {
    	
        /**
         * <div class="zh">
	     * <b><font color=red>NOTE: D180�����д˻ص� </font></b><br/>
         * �ȴ��û���Ӧ���б���ѡ��һ��Ӧ��, ���ֻ��һ������ȷ�ϵ�Ӧ��,�򱾺������ᱻ����
         * </div>
         * <div class="en">
	     * <b><font color=red>NOTE: for D180, this function will not be called</font></b><br/>
         * Wait for user to select an application from the application candidate list. If there is only one application in the application list and it doesn't require cardholder confirmation, this function will not be called.
         * </div>
         * 
	     * @param tryCnt
	     * <div class="zh">
	     * 	Ϊ0ʱ, ��һ�ε���, ��֮, �ǵ�һ�ε��ã���EMVҪ��ǵ�һ�ε��õ������Ӧ����ʾ"APP NOT ACCEPT, TRY AGAIN"������.
	     * </div>
	     * <div class="en">
	     * 	TryCnt=0 means it is called for the first time, otherwise, it has been called more than one time. (According to the EMV specification, if this function has been called more than one time, terminal should prompt for 'APP NOT ACCEPT, TRY AGAIN' or some other word like that.).
	     * </div>
	     * 
	     * @param appNum
	     * <div class="zh">
	     * 			app�ĸ���
	     * </div>
	     * <div class="en">
	     * 		The number of the application in the list.
	     * </div>
  		 *
	     * @param apps
	     * <div class="zh">
	     * 			[����]Ӧ���б�
	     * </div>
	     * <div class="en">
	     * 		[input] applications
	     * </div>
  		 *
  		 * @return
  		 * <div class="zh">
  		 * 		>=0: �û�ѡ�е�Ӧ�����(���磺0��ʾѡ��apps[0]) <br/>
  		 * 		{@link #EMV_USER_CANCEL}: �û�ȡ��Ӧ��ѡ��<br/>
  		 * 		{@link #EMV_TIME_OUT}: Ӧ��ѡ��ʱ<br/>
  		 * </div>
  		 * <div class="en">
  		 * 		>=0: The sequence number selected by the user(For example: 0 stands for apps[0] was selected) <br/>
  		 * 		{@link #EMV_USER_CANCEL}: Application selection is canceled by user<br/>
  		 * 		{@link #EMV_TIME_OUT}: Application selection timeout.<br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">ͨ�Ŵ���</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">Э�����</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
	     * <div class="en">common error</div>
         */
    	public int onWaitAppSel(int tryCnt, int appNum, EMV_APPLIST[] apps) throws IOException, ProtoException, CommonException;

        /**
         * <div class="zh">
	     * <b><font color=red>NOTE: ��������D180 </font></b><br/>
         * �ȴ��û���Ӧ���б���ѡ��һ��Ӧ��, ���ֻ��һ������ȷ�ϵ�Ӧ��,�򱾺������ᱻ����
         * </div>
         * <div class="en">
	     * <b><font color=red>NOTE: only for D180 </font></b><br/>
         * Wait for user to select an application from the application candidate list. If there is only one application in the application list and it doesn't require cardholder confirmation, this function will not be called.
         * </div>
         * 
	     * @param tryCnt
	     * <div class="zh">
	     * 	Ϊ0ʱ, ��һ�ε���, ��֮, �ǵ�һ�ε��ã���EMVҪ��ǵ�һ�ε��õ������Ӧ����ʾ"APP NOT ACCEPT, TRY AGAIN"������.
	     * </div>
	     * <div class="en">
	     * 	TryCnt=0 means it is called for the first time, otherwise, it has been called more than one time. (According to the EMV specification, if this function has been called more than one time, terminal should prompt for 'APP NOT ACCEPT, TRY AGAIN' or some other word like that.).
	     * </div>
	     * 
	     * @param appNum
	     * <div class="zh">
	     * 			app�ĸ���
	     * </div>
	     * <div class="en">
	     * 		The number of the application in the list.
	     * </div>
  		 *
	     * @param apps
	     * <div class="zh">
	     * 			[����]Ӧ���б�
	     * </div>
	     * <div class="en">
	     * 		[input] applications
	     * </div>
  		 *
  		 * @return
  		 * <div class="zh">
  		 * 		>=0: �û�ѡ�е�Ӧ�����(���磺0��ʾѡ��apps[0]) <br/>
  		 * 		{@link #EMV_USER_CANCEL}: �û�ȡ��Ӧ��ѡ��<br/>
  		 * 		{@link #EMV_TIME_OUT}: Ӧ��ѡ��ʱ<br/>
  		 * </div>
  		 * <div class="en">
  		 * 		>=0: The sequence number selected by the user(For example: 0 stands for apps[0] was selected) <br/>
  		 * 		{@link #EMV_USER_CANCEL}: Application selection is canceled by user<br/>
  		 * 		{@link #EMV_TIME_OUT}: Application selection timeout.<br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">ͨ�Ŵ���</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">Э�����</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
	     * <div class="en">common error</div>
         */    	
    	public int onCandAppSel(int tryCnt, int appNum, EMV_CANDLIST[] apps) throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
         * ���뽻�׽��
         * </div>
         * <div class="en">
         * Input transaction amount.
         * </div>
         * 
	     * @param amts
	     * <div class="zh">
	     * 		<br/>
	     * 		amts[0] - [���]: ���׽��. <br/> 
	     * 		amts[1] - [����/���] : ���ֽ��. ��Ϊ����ʱ�����ֵΪnull���ʾ����������ֽ��,����Ӧ������ֽ�� <br/>
	     * 		<b>ע��,�������Ի��ҵ���С��λ��ʾ,��������÷ֱ�ʾ</b>
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
  		 * 		{@link #EMV_OK}: ����ɹ�<br/>
  		 * 		{@link #EMV_USER_CANCEL}: �û�ȡ���������<br/>
  		 * 		{@link #EMV_TIME_OUT}: Ӧ�������ʱ<br/>
  		 * </div>
  		 * <div class="en">
  		 * 		{@link #EMV_OK}: Input succeeds<br/>
  		 * 		{@link #EMV_USER_CANCEL}: Amount input is canceled by user.<br/>
  		 * 		{@link #EMV_TIME_OUT}: Amount input timeout<br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">ͨ�Ŵ���</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">Э�����</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
	     * <div class="en">common error</div>
         */
    	//if cashbackAmt is null, then no need cashback
    	public int onInputAmount(String[] amts) throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
         * �ȴ��û�����ֿ�������
         * </div>
         * <div class="en">
         * Wait for cardholder to input PIN.
         * </div>
         * 
	     * @param pinFlag
	     * <div class="zh">
	     * 		{@link #EMV_PIN_FLAG_NO_PIN_REQUIRED}: ����Ҫ����PIN, ��Ҫ��ʾ�����ֿ���ȷ��. <br/>
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
	     * 		����pinFlagΪ2ʱ������<br/>
	     * 		0 : ��ʾ���ν�����, ��һ�ε��øú�����ȡ�û����� <br/>
	     * 		1 : �ǵ�һ�ε��øú�����ȡ�û�����(������֤��, �ҽ��ѻ���֤���������²Ż����). <br/>
	     * </div>
	     * <div class="en">
	     * 		valid only when pinFlag is 2<br/>
	     * 		0 : It's the first time calling this function to get the cardholder's PIN in this transaction.<br/>
	     * 		1 : It's not the first time calling this function to get the cardholder's PIN in this transaction. (It appears only when verifying the offline PIN and failing). <br/>	     
	     * </div>
  		 *
	     * @param remainCnt
	     * <div class="zh">
	     * 		����pinFlagΪ2ʱ������<br/>
		 *		��ʾ���м������Ի���, ���RemainCntΪ1, ���ʾֻ�����һ�λ�����, ��������ٴ�, ������ᱻ����
	     * </div>
	     * <div class="en">
	     * 		valid only when pinFlag is 2<br/>
	     * 		The chance remained to verify the PIN. If RemainCnt equals 1, it means only one chance remained to verify the PIN, and if the following PIN verification is still failed, the PIN will be blocked
	     * </div>
	     * 
	     * @param pinStatus
	     * <div class="zh">
	     * 		����pinFlagΪ2ʱ������<br/>
	     * 		{@link #EMV_OFFLINE_PIN_STATUS_PED_WAIT}:  PIN������ʱ�䲻��<br/>
	     * 		{@link #EMV_OFFLINE_PIN_STATUS_PED_TIMEOUT}: PIN���볬ʱ<br/>
	     * 		{@link #EMV_OFFLINE_PIN_STATUS_PED_FAIL}: PED�豸����������ϵͳ����<br/>
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
  		 * 		{@link #EMV_OK}: ��������ɹ�<br/>
  		 * 		{@link #EMV_NO_PASSWORD}: ��������û���ϣ����������<br/>
  		 * 		{@link #EMV_USER_CANCEL}: �û�ȡ������<br/>
  		 * 		{@link #EMV_TIME_OUT}: �������볬ʱ<br/>
  		 * </div>
  		 * <div class="en">
  		 * 		{@link #EMV_OK}: Succeed<br/>
  		 * 		{@link #EMV_NO_PASSWORD}: No PIN or cardholder doesn't want to input PIN<br/>
  		 * 		{@link #EMV_USER_CANCEL}: PIN input is canceled by user<br/>
  		 * 		{@link #EMV_TIME_OUT}: PIN input timeout<br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">ͨ�Ŵ���</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">Э�����</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
	     * <div class="en">common error</div>
         */
    	public int onGetHolderPwd(int pinFlag, int tryFlag, int remainCnt, int pinStatus) throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
	     * <b><font color=red>NOTE: D180�����д˻ص� </font></b><br/> 
         * �������з���Ĳο�
         * </div>
         * <div class="en">
	     * <b><font color=red>NOTE: for D180, this function will not be called</font></b><br/>
         * Process referral activated by the issuer
         * </div>
  		 *
  		 * @return
  		 * <div class="zh">
  		 * 		{@link #EMV_REFER_APPROVE}: ���ܽ���<br/>
  		 * 		{@link #EMV_REFER_DENIAL}: �ܾ�����<br/>
  		 * </div>
  		 * <div class="en">
  		 * 		{@link #EMV_REFER_APPROVE}: Referral approved.<br/>
  		 * 		{@link #EMV_REFER_DENIAL}: Referral denied.<br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">ͨ�Ŵ���</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">Э�����</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
	     * <div class="en">common error</div>
         */
    	public int onReferProc() throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
	     * <b><font color=red>NOTE: D180�����д˻ص� </font></b><br/>
         * ��������
         * </div>
         * <div class="en">
	     * <b><font color=red>NOTE: for D180, this function will not be called</font></b><br/>
         * Online transaction.
         * </div>
         * 
	     * @param respCode
	     * <div class="zh">
	     * 		[���] ��Ȩ��Ӧ����, 2�ֽ�, ���û��, ��RspCode[0]����0(����, ����ʧ�ܵ����)
	     * </div>
	     * <div class="en">
	     * 		[output] Authorization response code, 2 bytes. Set RspCode[0] as 0 in case of online failed.
	     * </div>
	     * 
	     * @param authCode
	     * <div class="zh">
	     * 		[���] 1�ֽڳ��� + ��Ȩ��, ���û����Ȩ��, �򳤶�Ϊ0
	     * </div>
	     * <div class="en">
	     * 		[output] 1 byte length + Authorization code, length is 0 if there's no Authorization code.
	     * </div>
  		 *
	     * @param authData
	     * <div class="zh">
	     * 		[���] 4�ֽڳ��� + �������ص��ⲿ��֤����, ���û���������ص��ⲿ��֤����, �򳤶�Ϊ0
	     * </div>
	     * <div class="en">
	     * 		[output] 4 bytes length + Issuer authentication data returned from host, length is 0 if there's no Issuer authentication data.
	     * </div>
	     * 
	     * @param script
	     * <div class="zh">
	     * 		[���] 4�ֽڳ��� + �����нű�, ����������صĽű�������ͬһ��8583�����´���, ������еĽű�ƴ����һ���ͨ���ò�������, ���û��, �򳤶�Ϊ0
	     * </div>
	     * <div class="en">
	     * 		[output] 4 bytes length + Issuer script. If the scripts are not sent in one 8583 field, then put all the scripts together and return by this parameter. length is 0 if there's no issuer script.
	     * </div>
  		 *
  		 * @return
  		 * <div class="zh">
  		 * 		{@link #EMV_ONLINE_APPROVE}: ������׼��������׼���ף�<br/>
  		 * 		{@link #EMV_ONLINE_DENIAL}: �����ܾ�������û����׼���ף�<br/>
  		 * 		{@link #EMV_ONLINE_REFER}: �����ο��������вο���<br/>
  		 * 		{@link #EMV_ONLINE_FAILED}: ����ʧ��<br/>
  		 * 		{@link #EMV_ONLINE_ABORT}: ��ֹ���ף�PBOCҪ��<br/>
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
	     * <div class="zh">ͨ�Ŵ���</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">Э�����</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
	     * <div class="en">common error</div>
         */    	
    	//authCode L1 + V,  authData L4 + V, script L4 + V 
    	public int onOnlineProc(byte[] respCode, byte[] authCode, byte[] authData, byte[] script) throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
	     * <b><font color=red>NOTE: D180�����д˻ص� </font></b><br/>
         * �������ѻ�֪ͨ����
         * </div>
         * <div class="en">
	     * <b><font color=red>NOTE: for D180, this function will not be called</font></b><br/>
         * Online or offline advice processing
         * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">ͨ�Ŵ���</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">Э�����</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
	     * <div class="en">common error</div>
         */    	    	
    	public void onAdviceProc() throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
         * ��֤����ɹ�֪ͨ
         * </div>
         * <div class="en">
         * Prompt for "PIN OK"
         * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">ͨ�Ŵ���</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">Э�����</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
	     * <div class="en">common error</div>
         */    	    	    	
    	public void onVerifyPinOk() throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
	     * <b><font color=red>NOTE: D180�����д˻ص� </font></b><br/>
         * ��ȡEMV�ⲻʶ��TAG������ֵ
         * </div>
         * <div class="en">
	     * <b><font color=red>NOTE: for D180, this function will not be called</font></b><br/>
         * Get the data of the unknown tag.
         * </div>
         * 
	     * @param tag
	     * <div class="zh">
	     * 		��ǩ, ����EMVû�����, Ҳ���ܶ�����, ��IC��Ƭ��û�е�.
	     * </div>
	     * <div class="en">
	     * 		Tag. It may be not defined by EMV, or defined by EMV but can't be found in the IC card.
	     * </div>
	     * 
	     * @param len
	     * <div class="zh">
	     * 		DOLҪ��ı�ǩֵ�ĳ���
	     * </div>
	     * <div class="en">
	     * 		The length of the tag according to the DOL requirement.
	     * </div>
  		 *
	     * @param value
	     * <div class="zh">
	     * 		[���] ��ǩֵ, Ӧ�ó�����д
	     * </div>
	     * <div class="en">
	     * 		[output] The value of the tag, filled by the application.
	     * </div>
	     * 
  		 * @return
  		 * <div class="zh">
  		 * 		{@link #EMV_UNKNOWN_TAG_VALUE_PROVIDED}: Ӧ�ó������ṩ��Ӧ�ı�ǩֵ<br/>
  		 * 		{@link #EMV_UNKNOWN_TAG_VALUE_IGNORED}: Ӧ�ó���û�д��� <br/>
  		 * </div>
  		 * <div class="en">
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">ͨ�Ŵ���</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">Э�����</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
	     * <div class="en">common error</div>
         */    	
    	public int onUnknownTLVData(short tag, int len, byte[] value) throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
         * �ֿ���֤����֤��PBOC2.0��, PBOC2.0�������˳ֿ���֤����֤��CVM, �ڽ��׹�����, EMV�ں˷��ֵ�ǰʹ�õ�PBOC����Ҫ�ֿ�����֤ʱ, 
         * ����øú���, Ӧ�ó���Ӧ���ڸú����е���   {@link EmvManager#getTLVData} ������ȡ֤�����뼰֤������, ���������Ϣ�ṩ��������Ա�˶�
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
  		 * 		{@link #EMV_CERT_VERIFY_OK}: ��֤�ɹ�<br/>
  		 * 		{@link #EMV_CERT_VERIFY_ERR}: ��֤ʧ�� <br/>
  		 * </div>
  		 * <div class="en">
  		 * 		{@link #EMV_CERT_VERIFY_OK}: Verify succeeded<br/>
  		 * 		{@link #EMV_CERT_VERIFY_ERR}: Verify failed <br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">ͨ�Ŵ���</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">Э�����</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
	     * <div class="en">common error</div>
         */    	    	
    	public int onCertVerify() throws IOException, ProtoException, CommonException;
    	
        /**
         * <div class="zh">
         * �������� <br/>
         * 1.�ýӿ�����������ѡ��ִ��֮��, ȡ����ѡ�������ǰ, ��������ѡ���AID���ƻ�����Ӧ����Ҫ, �������AID���뵱ǰ���׶�Ӧ�Ĳ��� <br/>
         * 2.Ӧ�ó����ڸýӿ��е��� {@link EmvManager#setTLVData} �ӿ���������Ĳ���<br/>
         * 3.���ú�������ֵ��Ϊ{@link #EMV_OK}ʱ, �ں˽������˳����״������<br/>
         * 4.֧��SM�㷨���ն����ڸûص���������������Ԫ0xDF69��ֵΪ1, ����Ϊ1<br/>
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
  		 * 		{@link #EMV_OK}: �ɹ�,�ɼ���EMV����<br/>
  		 * 		����: ȡ��/��ֹEMV����<br/>
  		 * </div>
  		 * <div class="en">
  		 * 		{@link #EMV_OK}: Succeed.<br/>
  		 * 		others: Abort/Terminate current transaction.<br/>
  		 * </div>
  		 *
	     * @throws IOException
	     * <div class="zh">ͨ�Ŵ���</div>
	     * <div class="en">communication error</div>
	     * @throws ProtoException
	     * <div class="zh">Э�����</div>
	     * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
	     * <div class="en">common error</div>
         */
    	public int onSetParam() throws IOException, ProtoException, CommonException;
    }
    
    /**
     * <div class="zh">
     * ʹ��ָ����Context�����EmvManager����
     * </div>
     * <div class="en">
     * Create a EmvManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">Ӧ�õ�ǰ��context</div>
     * <div class="en">application context currently</div>
     */ 
    //ע�⣺֮ǰ��private��2014.08.21��
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
     * ����callback ������
     * </div>
     * <div class="en">
     * set EMV callback handler
     * </div>
     * 
     * @param handler 
     * <div class="zh">callback ������</div>
     * <div class="en">callback handler</div>
     */      
    public void setCallbackHandler(EmvCallbackHandler handler) {
    	proto.setEmvCallbackHandler(handler);
    }

    /**
     * <div class="zh">
     * ���ն˲���
     * </div>
     * <div class="en">
     * Get terminal parameter.
     * </div>
     * 
	 * @return
	 * <div class="zh">
	 * 		�ն˲���
	 * </div>
	 * <div class="en">
	 * 		terminal parameter
	 * </div>
	 *
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * �����ն˲���
     * </div>
     * <div class="en">
     * set terminal parameter
     * </div>
     * 
     * @param param
	 * <div class="zh">
	 * 		�ն˲���, �μ�{@link EMV_PARAM}
	 * </div>
	 * <div class="en">
	 * 		terminal parameter, see {@link EMV_PARAM}
	 * </div>
	 *
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��ȡָ����ǩ������ֵ
     * </div>
     * <div class="en">
     * Get the value of the data element by specifying the tag.
     * </div>
     * 
     * @param tag
	 * <div class="zh">
	 * 		EMV����ı�׼����Ԫ�ر�ǩ����չ�ı�ǩ
	 * </div>
	 * <div class="en">
	 * 		Tag of EMV standard or extended data element.
	 * </div>
	 *
	 * @return
	 * <div class="zh">
	 * 		null : �޴˱�ǩ<br/>
	 * 		��null: ��ǩֵ
	 * </div>
	 * <div class="en">
	 *		null: no specified tag<br/>
	 *		non-null: value of the specified tag 
	 * </div>
	 * 
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ����ָ����ǩ������ֵ
     * </div>
     * <div class="en">
     * Set the value of the data element by specifying the tag.
     * </div>
     * 
     * @param tag
	 * <div class="zh">
	 * 		EMV����ı�׼����Ԫ�ر�ǩ����չ�ı�ǩ
	 * </div>
	 * <div class="en">
	 * 		Tag of EMV standard or extended data element.
	 * </div>
	 *
     * @param value
	 * <div class="zh">
	 * 		[����]��ǩTagָ��������
	 * </div>
	 * <div class="en">
	 * 		[input] The value of the data element specified by the tag.
	 * </div>
	 * 
	 * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��ȡ�����нű����
     * </div>
     * <div class="en">
     * Get the issuer script processing result.
     * </div>
     * 
	 * @return
	 * <div class="zh">
	 * 		null : �޽ű�<br/>
	 * 		��null: �ű����
	 * </div>
	 * <div class="en">
	 * 		null: no issuer script processing result<br/>
	 * 		non-null: issuer script processing result
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * <b><font color=red>ע��: D180 ��֧�ִ˹���</font></b><br/>
     * ����PCI�ѻ�PIN��֤����
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function </font></b><br/>
     * add proprietary data elements<br/>
     * Set PCI offline PIN verification parameter
     * </div>
     * 
     * @param timeout
	 * <div class="zh">
	 * 		����PIN�ĳ�ʱʱ��, ��λ������, ���ֵΪ300,000Ms
	 * </div>
	 * <div class="en">
	 *		Timeout of input PIN, unit: ms, maximum 300,000 ms. 		
	 * </div>
	 *
     * @param expPinLen
	 * <div class="zh">
	 * 		�ò���ָ��������ĺϷ����볤���ַ���. 0��12��ö�ټ���,��","�Ÿ���ÿ������, ����������4��6λ���벢������������ֱ�Ӱ�ȷ��, ����ַ���Ӧ������Ϊ"0,4,6".
	 * </div>
	 * <div class="en">
	 * 		 the allowed length of PIN. It is a string of the enumeration of 0-12 and separated by ','. For example, '0,4,6' means it is allowed to input 4 or 6 digits for PIN, and to directly press Enter without input PIN.
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��ȡ�ں˷����汾�ż�������Ϣ
     * </div>
     * <div class="en">
     * Get the version of EMV kernel and the release date.
     * </div>
     * 
	 * @return
	 * <div class="zh">
	 * 		�ں˷����汾�ż�������Ϣ,  ʾ��: "v26 2008.10.09"
	 * </div>
	 * <div class="en">
	 * 		Version and release date of kernel with the maximum length of 20 bytes. For example, "v26 2008.10.09".
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * <b><font color=red>ע��: D180 ��֧�ִ˹��� </font></b><br/> 
     * ����ں��еĽ�����ˮ��¼ <br/>
	 * 1.�ں˻�ѭ����¼8�ʽ��׽������, �����ն˷��չ���ʱ, �жϵ�ǰ���׽�����м�¼�ĸÿ������һ�ν��׼�¼�����ۼ�ֵ�Ƿ񳬳�����޶�<br/>
	 * 2.��Ӧ�ó���ϣ���ۼ����һ�ʽ��׽��, ����������Ȳ�����, ��Ҫͬ��ɾ���ں˽��׼�¼ʱ, ��ͨ���ýӿ�����ں��еĽ�����ˮ��¼
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function </font></b><br/>
     * Clear the transaction log of kernel.<br/>
     * 1.Kernel will record the amount of the last 8 transactions. When performing terminal risk management, kernel will add the amount of current transaction with the amount of the last transaction found in the log giving that the PAN is same and the result will be used for floor limit check.<br/>
     * 2.This function is provided for application to erase this log after settlement or other cases when necessary.
     * </div>
     * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * <b><font color=red>ע��: D180 ��֧�ִ˹���</font></b><br/>
     * ��ӿ�Ƭ�Զ����ǩ<br/>
	 * 1.�ں�ֻ֧��EMV����ı�׼���ݱ�ǩ�Լ����������ݱ�ǩ, ��ҪΪӦ�ó����ṩר�ú�������ӿ�Ƭ����Ԫ���Զ����ǩ, ��Ƭ�Զ����ǩ�б���Ӧ�������弰��ʼ��, ������Ƭ�Զ����ǩ�б�ͨ���ú������ݸ��ں�;<br/>
	 * 2.��Ƭ�Զ����ǩ�б�����ݽṹ�ο�{@link EMV_ELEMENT_ATTR}, ��ǩ�б��ڵı�ǩ������nAddNum����һ��, ��nAddNum����ǩ�����г���MaxLen����TagΪ0, ��᷵�ز������ô���;<br/>
	 * 3.ÿ�ε��øú���, �ں˶�������ں˶�Ӧ�Ŀ�Ƭ�Զ����ǩ�б�;<br/>
	 * 4.�������Ԫ�ر�ǩ�ظ�����, ѡ���������ݴ洢
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
	 * 		[����]��Ƭ�Զ������ݱ�ǩ�б�
	 * </div>
	 * <div class="en">
	 * 		[input]List of issuer proprietary data elements
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ���ýű�����ʽ
     * </div>
     * <div class="en">
     * add proprietary data elements<br/>
     * set issuer script processing method
     * </div>
     * 
     * @param method
	 * <div class="zh">
	 * 	<ul>
	 * 		<li>{@link #EMV_SCRIPT_PROC_UNIONPAY}: ����������֤Ҫ��ű���������Ӧʱ�ű�������Ϊ�ű�����δִ��. ����������֤��������
	 * 		<li>{@link #EMV_SCRIPT_PROC_NORMAL}:EMV��������ʽ��Ĭ��ֵ��
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
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ���һ���µ���֤������Կ<br/>
	 * �����Կ����, �����µ���Կ����ԭ������Կ.��֤������Կ���յ����ṩ.�յ����ṩ����Կ��һ�����Ͻṹ{@link com.pax.mposapi.model#EMV_CAPK}, Ӧ�ÿ�����Ҫת���������ӵ�EMV��
     * </div>
     * <div class="en">
     * Add a new CA public key.
     * Overwrites if already exsists. CAPK is provided by the acquirer, if it does not conform to {@link com.pax.mposapi.model#EMV_CAPK}, please convert it first. 
     * </div>
     * 
     * @param capk
	 * <div class="zh">
	 * 		��Կ����
	 * </div>
	 * <div class="en">
	 * 		CA public key
	 * </div>
     *  
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * <b><font color=red>ע��: D180 ��֧�ִ˹���</font></b><br/>
     * ��EMV����ɾ��һ����֤������Կ
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function</font></b><br/>
     * Delete a CA public key.
     * </div>
     * 
     * @param keyId
	 * <div class="zh">
	 * 		��Կ����
	 * </div>
	 * <div class="en">
	 * 		The index of the key.
	 * </div>
	 * 
     * @param rid
	 * <div class="zh">
	 * 		[����]Ӧ��ע�������ID, ����D180,���Դ˲��� 
	 * </div>
	 * <div class="en">
	 * 		[input]Registered Application Provider Identifier. This is ignored for D180
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��EMV����ɾ��������֤������Կ
     * </div>
     * <div class="en">
     * Delete all CA public key.
     * </div>
     * @return 
     * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��EMV���ж���һ����֤������Կ
     * </div>
     * <div class="en">
     * Get a CA public key.
     * </div>
     * 
     * @param keyId
	 * <div class="zh">
	 * 		��Կ����
	 * </div>
	 * <div class="en">
	 * 		The key storage index.
	 * </div>
	 * 
	 * @return
	 * <div class="zh">
	 * 		null: ָ����keyId������<br/>
	 * 		��null: ��֤������Կ
	 * </div>
	 * <div class="en">
	 * 		null: no CAPK for specified index<br/>
	 * 		non-null: CAPK
	 * </div>
	 * 
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * <b><font color=red>ע��: D180 ��֧�ִ˹���</font></b><br/>
     * ���EMV������Կ����Ч�� , ÿ�μ��ֻ����һ��������Կ, Ӧ�ó���Թ�����Կ�������, Ӧ���ٴμ��, ֱ������trueΪֹ
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function</font></b><br/>
     * Check the validity of the public keys. Only one expired key will be returned when calling this function. When the function returns false, the application should handle it and then continually call this function until it returns true.
     * </div>
     * 
     * @param keyId
	 * <div class="zh">
	 * 		[����/���]��Կ����, ��������falseʱ��Ч<br/>
	 * 		��Ϊ����ʱ, ����Ϊnull,��ʾ�����keyId<br/>
	 * 		���keyId��Ϊnull, �����keyId[0]��ʾ������Կ��keyId.
	 * </div>
	 * <div class="en">
	 * 		[input/output]The index of the expired key. valid only when function returns false<br/>
	 * 		As an input, no output keyId if keyId is null.
	 * </div>
	 * 
     * @param rid
	 * <div class="zh">
	 * 		[����/���]Ӧ��ע�������ID, ��������falseʱ��Ч<br/>
	 * 		��Ϊ����ʱ, ����Ϊnull,��ʾ�����rid, �������rid<br/>
	 * </div>
	 * <div class="en">
	 * 		[input/output]The RID of the expired key. valid only when function returns false<br/>
	 * 		As an input, no output rid if rid is null.
	 * </div>
	 * 
	 * @return
	 * <div class="zh">
	 * 		true: ��Կ��Ч<br/>
	 * 		false: ��Կ����, ���Լ��keyId[0], rid����ֵ<br/>
	 * </div>
	 * <div class="en">
	 * 		true: key is valid<br/>
	 * 		false: key is expired, can further check keyId[0] and rid if needed<br/>
	 * </div>
	 * 
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ���һ��EMVӦ�õ�Ӧ���б�
     * </div>
     * <div class="en">
     * Add an EMV application to terminal application list.
     * </div>
     * 
     * @param app
	 * <div class="zh">
	 * 		[����]Ӧ���б�����
	 * 
	 * </div>
	 * <div class="en">
	 * 		[input]application data
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��ȡһ��Ӧ������
     * </div>
     * <div class="en">
     * Get an application from the terminal application list.
     * </div>
     * 
     * @param index
	 * <div class="zh">
	 * 		Ӧ�ô洢������Χ
	 * </div>
	 * <div class="en">
	 * 		the application index
	 * </div>
	 * 
     * @return
	 * <div class="zh">
	 * 		null: ��ָ��Ӧ��
	 * 		��null: Ӧ������
	 * </div>
	 * <div class="en">
	 * 		null: no specified application
	 * 		non-null: application data
	 * </div>
	 * 
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * <b><font color=red>ע��: D180 ��֧�ִ˹��� </font></b><br/>
     * ��EMVӦ���б���ɾ��һ��Ӧ��
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function</font></b><br/>
     * Delete an application from the application list.
     * </div>
     * 
     * @param aid
	 * <div class="zh">
	 * 		[����]Ӧ�ñ�־, ѹ��BCD��, ������16�ֽ�.
	 * </div>
	 * <div class="en">
	 * 		[input]Application ID, compressed BCD, no more than 16 bytes.
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ɾ��Ӧ���б��е�����Ӧ��
     * </div>
     * <div class="en">
     * Delete all applications from the application list.
     * </div>
     * @return 
     * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��ȡ��ǰ����ѡ��Ӧ�õĲ���
     * <b><font color=red>ע��: D180 ��֧�ִ˹���</font></b><br/>
     * </div>
     * <div class="en">
     * <b><font color=red>NOTE: D180 doesn't support this function</font></b><br/>
     * Get the parameter of current finally selected application.
     * </div>
     * 
     * @return
	 * <div class="zh">
	 * 		null: ��ǰû������ѡ���Ӧ��
	 * 		��null: Ӧ�ò���
	 * </div>
	 * <div class="en">
	 * 		null: no finally selected application
	 * 		non-null: the parameter of current finally selected application
	 * </div>
	 * 
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * �޸ĵ�ǰ����ѡ��Ӧ�õĲ���
     * <b><font color=red>ע��: ����D180,�����ں�û�б������Ӧ��ʱ������Ӧ�ò���,���Ա�����app select��,��ȡѡ�е��ն�AID(9F06),���������ø�Ӧ����ز������ں�</font></b><br/>
     * </div>
     * <div class="en">
     * Modify the parameter of current finally selected application.
     * <b><font color=red>NOTE: for D180, since the parameters are not saved when adding application, so after app selection, please get the selected AID(9F06) and 
     * set the parameters with this method </font></b><br/>
     * </div>
     * 
     * @param app
	 * <div class="zh">
	 * 		[����]APP����
	 * </div>
	 * <div class="en">
	 * 		[input]application data
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��ȡ�뵱ǰ��ѡ�б��Ӧ�ı�ǩ�б�����
     * </div>
     * <div class="en">
     * Get the label list of the application candidate list.
     * </div>
     * 
     * @return
	 * <div class="zh">
	 * 		��null: ��õı�ǩ�б����� <br/> 
	 * 		null: �ޱ�ǩ�б�����
	 * </div>
	 * <div class="en">
	 * 		non-null: the array of the label list<br/>
	 * 		null: no label list
	 * </div>
	 * 
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ���һ�������յķ����й�Կ֤�����ݵ�֤������б�<br/>
     * �������ӵ�֤���Ѵ���, ��ֱ�ӷ��سɹ�
     * </div>
     * <div class="en">
     * Add a revoked issuer public key certification to revoked certification list.
     * </div>
     * 
     * @param revocList
	 * <div class="zh">
	 * 		[����]�����й�Կ֤�����ݽṹ
	 * </div>
	 * <div class="en">
	 * 		[input]the revoked issuer public key certification.
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��EMV����ɾ��һ�������й�Կ֤���������
     * </div>
     * <div class="en">
     * Delete a revoked issuer public key certification.
     * </div>
     * 
     * @param index
	 * <div class="zh">
	 * 		��ɾ���Ļ��չ�Կ֤�����ݶ�Ӧ��CA��Կ����
	 * </div>
	 * <div class="en">
	 * 		The corresponding CA public key index of the revoked issuer public key certification.
	 * </div>
	 *
     * @param rid
	 * <div class="zh">
	 * 		[����]��ɾ���Ļ��չ�Կ֤�����ݶ�Ӧ��RID����(5�ֽ�)
	 * </div>
	 * <div class="en">
	 * 		[input]The corresponding RID of the revoked issuer public key certification.
	 * </div>
	 * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��EMV����ɾ�����з����й�Կ֤����������б�
     * </div>
     * <div class="en">
     * Delete all revoked issuer public key certifications.
     * </div>
     * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��ʼ��EMV�ں�����Ԫ�洢�ṹ<br/>
     * �˺�����Ҫ��ÿ�ν��׿�ʼǰ������Ӧ��ѡ��֮ǰ������, ���ڳ�ʼ��EMV�ں�����Ԫ�洢�ṹ
     * </div>
     * <div class="en">
     * Initialize the EMV kernel data element storage structure.<br/>
     * The function need to be called before the start of every transaction, for instance, before the application selection, to initialize the EMV kernel data element storage structure.
     * </div>
     * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ѡ��EMVӦ�� <br/>
     * 1.Ҫ���ڵ��øú���ǰ,Ӧ��Ӧ��ͨ��{@link IccManager#iccDetect} �����жϳ�slotָ���Ŀ������Ѿ���EMV IC��Ƭ
     * EMVҪ������������ǣ�{@link #EMV_NO_APP}��{@link #EMV_DATA_ERR}, ��Ӧ����ʾ�û�ˢ�ſ�����(Ӧ�ÿɸ���ʵ��Ҫ�������ô��);<br/>
     * {@link #EMV_USER_CANCEL} �� {@link #EMV_TIME_OUT} ���������ɻص�����{@link EmvManager.EmvCallbackHandler#onWaitAppSel}���ص�, 
     * ���Ӧ�ó�����{@link EmvManager.EmvCallbackHandler#onWaitAppSel}�����в�û�з���������������, �򱾺���Ҳ���᷵��������������<br/>
     * 2.ICC_RSP_6985 ��GPO������Ӧ��ֻ��һ����ֻʣһ�������ʱ, ��Ƭ����"6985"ʱ���ص�, ��Ӧ�þ�����ֹ���׻���FALLBACK<br/>
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
     * 		������ 
     * </div>
     * <div class="en">
     * 		Card slot number.
     * </div>
     * 
     * @param transNo
     * <div class="zh">
     * 		���ν��׵���� 
     * </div>
     * <div class="en">
     * 		The sequence number of the transaction.
     * </div>
     *  
     * @return
     * <div class="zh">
     * <ul>
     *		<li>{@link #EMV_OK}: �ɹ�
     *		<li>{@link #ICC_RESET_ERR}: IC����λʧ��
     *		<li>{@link #ICC_CMD_ERR}: IC������ʧ��
     *		<li>{@link #ICC_BLOCK}: IC������
     *		<li>{@link #EMV_NO_APP}: û���ն�֧�ֵ�EMVӦ��
     *		<li>{@link #EMV_APP_BLOCK}: Ӧ������
     *		<li>{@link #EMV_DATA_ERR}: ��Ƭ���ݸ�ʽ����
     *		<li>{@link #EMV_TIME_OUT}: Ӧ��ѡ��ʱ
     *		<li>{@link #EMV_USER_CANCEL}: �û�ȡ��Ӧ��ѡ��
     *		<li>{@link #ICC_RSP_6985}: GPO������, ��Ƭ����'6985'
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
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ����ǰѡ���Ӧ�õ����ݺͽ��׽������� <br/>
     * {@link EmvException#EMV_ERR_USER_CANCEL} ��{@link EmvException#EMV_ERR_TIME_OUT} �쳣�����ڻص�����{@link EmvCallbackHandler#onInputAmount}������Ӧ��ֵ�����, 
     * ���Ӧ�ó�����{@link EmvCallbackHandler#onInputAmount}�����в�û�з���������������, �򱾺���Ҳ�����׳��������쳣.<br/>
     * </div>
     * <div class="en">
     * Read the selected application's data, transaction amount, etc.<br/>
     * {@link EmvException#EMV_ERR_USER_CANCEL} and {@link EmvException#EMV_ERR_TIME_OUT} are returned by callback function {@link EmvCallbackHandler#onInputAmount}. If the function  {@link EmvCallbackHandler#onInputAmount} doesn't return these two values, this function does not return them either.<br/>
     * </div>
     *  
     * @throws EMVException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��Ƭ������֤ <br/>
     * ��ΪEMVҪ����֤ʧ�ܲ�һ��Ҫ��ֹ����, ���Ըú�������EMV_OK, ��������������֤�ɹ�, 
     * Ӧ�ó��������Ҫ�˽���֤�Ľ��, ��ͨ������{@link EmvManager#getTLVData}��ѯTVR��ֵ�ж���֤���, �����CDA��֤, �ú���ֻ�ָ�IC����Կ, ��֤�����ں�ߵ�GAC����ʱ�Ž���
     * </div>
     * <div class="en">
     * IC card data authentication.<br/>
     * This function returning EMV_OK does not stand for data authentication succeeded. The application can get the result of the authentication through function {@link EmvManager#getTLVData} and query the value of TVR. If the authentication method is CDA , this function just recovers the IC card private key, the authentication will not be done until performing Generate AC command.
     * </div>
     * 
     * @return
     * <div class="zh">
     * <ul>
     *		<li>{@link #EMV_OK}: �ɹ�
     *		<li>{@link #ICC_CMD_ERR}: IC������ʧ��
     *		<li>{@link #EMV_RSP_ERR}: IC�����������
     *		<li>{@link #EMV_DENIAL}: ���ױ��ܾ�
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
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
     * <div class="en">common error</div>
     */         
    public int cardAuth() throws IOException, ProtoException, CommonException {
    	RespCode rc = new RespCode();
    	
    	proto.sendRecv(Cmd.CmdType.EMV_CARD_AUTH, new byte[0], rc, respBuffer);
    	return rc.code;
    }

    /**
     * <div class="zh">
     * <b><font color=red>ע��: D180 ��֧�ִ˹���</font></b><br/>
     * ����EMV���� <br/>
     * 1.{@link #EMV_USER_CANCEL}��{@link #EMV_TIME_OUT} ���������ɻص�����{@link EmvCallbackHandler#onGetHolderPwd}���ص�, 
     * ���Ӧ�ó�����{@link EmvCallbackHandler#onGetHolderPwd}�����в�û�з���������������, �򱾺���Ҳ���᷵��������������;<br/>
	 * 2.{@link #EMV_NOT_ACCEPT}��{@link #EMV_DENIAL}���ǽ���ʧ��, ֻ��EMVҪ���ڷ�������������Ҫ����{@link #EMV_NOT_ACCEPT}, �������·���{@link #EMV_DENIAL};<br/>
	 * 3.ICC_RSP_6985����������ִ��Generate AC������, ��Ƭ����״̬��Ϊ'6985'ʱ���ص�, ��Ӧ�þ�������ֹ���׻���FALLBACK 
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
     *			<li>{@link #EMV_OK}: �ɹ�
     *			<li>{@link #ICC_CMD_ERR}: IC������ʧ��
     *			<li>{@link #EMV_RSP_ERR}: IC�����������
     *			<li>{@link #EMV_DATA_ERR}: ��Ƭ���ݸ�ʽ����
     *			<li>{@link #EMV_NOT_ACCEPT}: ���ײ�����
     *			<li>{@link #EMV_DENIAL}: ���׾ܾ�
     *			<li>{@link #EMV_TIME_OUT}: Ӧ��ѡ��ʱ
     *			<li>{@link #EMV_USER_CANCEL}: �û�ȡ��Ӧ��ѡ��
     *			<li>{@link #ICC_RSP_6985}: GAC������,��Ƭ����'6985'
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
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
      * Ϊ��ȡ������־��Ӧ��ѡ��PBOC2.0����EMV2��<br/>
      * �ú�����{@link EmvManager#appSelect}����һ��, ��ͬ�����ڴ�Ӧ��ѡ����, ���ݲ�ͬ�Ĳ�������, �ɽ���������Ӧ����ӵ�Ӧ���б���
      * </div>
      * <div class="en">
      * Read the card's transaction log.<br/>
      * This function is similar with the function {@link EmvManager#appSelect}. The difference between them is that this function can add the block application to candidate list base on the different parameter setting when the application selection is done.
      * </div>
      * 
      * @param slot
      * <div class="zh">
      * 	������ 
      * </div>
      * <div class="en">
      * 	Card slot number.
      * </div>
      * 
      * @param flag
      * <div class="zh">
      * 	������Ӧ���Ƿ�����ѡ�б�, 0-����, 1-������ (Ĭ��Ϊ0)
      * </div>
      * <div class="en">
      * 	Decide whether the block application will be added to candidate list or not.  0-Add, 1-Not add (The default is 0)
      * </div>
      *
      * @return
      * <div class="zh">
      *		<ul>
      *			<li>{@link #EMV_OK}: �ɹ�
      *			<li>{@link #ICC_RESET_ERR}: IC����λʧ��
      *			<li>{@link #ICC_CMD_ERR}: IC������ʧ��
      *			<li>{@link #ICC_BLOCK}: IC������
      *			<li>{@link #EMV_NO_APP}: û���ն�֧�ֵ�EMVӦ��
      *			<li>{@link #EMV_DATA_ERR}: ��Ƭ���ݸ�ʽ����
      *			<li>{@link #EMV_TIME_OUT}: Ӧ��ѡ��ʱ
      *			<li>{@link #EMV_USER_CANCEL}: �û�ȡ��Ӧ��ѡ��
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
      * <div class="zh">ͨ�Ŵ���</div>
      * <div class="en">communication error</div>
      * @throws ProtoException
      * <div class="zh">Э�����</div>
      * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��������־��PBOC2.0����EMV2��<br/>
     * Ӧ�ó������� {@link EmvManager#appSelectForLog} ���Ӧ��ѡ��֮��, ���øú�����ȡ��ѡ����Ӧ�õĽ�����־.RecordNo��¼�Ŵ�1��ʼ
     * �ú���ֻ�ǰѽ�����־����EMV�ں˵Ļ�����, Ӧ�ó������ͨ��{@link EmvManager#getLogItem}��ȡ�������־����, ���罻�׽�ʱ��ȵ�.
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
     * 		��¼��
     * </div>
     * <div class="en">
     * 		Record number
     * </div>
     * 
     * @return
     * <div class="zh">
     *		true: �ɹ� <br/>
     *  	false: ��¼�����ڻ����ݴ���
     * </div>
     * <div class="en">
     * </div>
     * 
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��������־�������PBOC2.0����EMV2��<br/>
     * ����ͨ��{@link EmvManager#readLogRecord} ����������ÿһ��������־��¼, Ӧ�ó��򶼿���ͨ��������ȥ��ȡ�������־��Ϣ
     * </div>
     * <div class="en">
     * Read the data items of transaction log(PBOC2.0 compatible EMV2) <br/>
     * For each transaction log record that was read by function {@link EmvManager#readLogRecord}, 
     * application can read the specific log information by this function. 
     * </div>
     * 
     * @param tag
     * <div class="zh">
     * 		��Ҫ��ȡ��������ı�ǩ
     * </div>
     * <div class="en">
     *		The tag of data items that need to read. 
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		null: ָ������־����ڻ����ݴ��� <br/>
     * 		��null: �������ֵ
     * </div>
     * <div class="en">
     * 		null: log record doesn't exist or card data error
     * 		non-null: the log item read
     * </div>
     *  
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��ȡ�ں��е�MCK����
     * </div>
     * <div class="en">
     * Get the kernel MCK parameter.
     * </div>
     * 
     * @return
     * <div class="zh">
     *		�����MCK����
     * </div>
     * <div class="en">
     * 		the MCK paramter
     * </div>
     *  
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ����MCK������صĲ��ֲ���
     * </div>
     * <div class="en">
     * Set MCK parameter.
     * </div>
     * 
     * @param mckParam
     * <div class="zh">
     *		������MCK����
     *</div>
     *<div class="en">
     *		MCK param to set
     *</div>
     *  
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * �����ն˵����ֽ���ز���
     * </div>
     * <div class="en">
     * Set the terminal electronic cash related parameters.
     * </div>
     * 
     * @param tmEcpParam
     * <div class="zh">
     *		�����õ����ֽ����
     *</div>
     *<div class="en">
     *		The electronic cash parameters which need to be set.
     *</div>
     *  
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * �ӿ�Ƭ�ж�ȡ�����ֽ����
     * </div>
     * <div class="en">
     * Read the electronic cash balance from card.
     * </div>
     * 
     * @return
     * <div class="zh">
     *		��ȡ���ĵ����ֽ����
     *</div>
     *<div class="en">
     *		the electronic cash balance read
     *</div>
     *  
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ʵ�ִ������ơ��ֿ�����֤���ն˷��չ�����һ��GAC���� <br/>
     * ����"�����ѻ��ܾ�, Ҫ����֪ͨ"�������Ӧ�ø���CID��ȡֵ�ж��Ƿ������֪ͨ��Ϣ����
     * </div>
     * <div class="en">
     * Performing process restrict, cardholder verification, terminal risk management & 1st GAC.<br/>
     * For the transaction result in sending advice message when declined, developer should get the CID from kernel, and check if the advice message is needed.
     * </div>
     * 
     * @param authAmt
     * <div class="zh">
     *		�ý�������{@link EmvManager.EmvCallbackHandler#onInputAmount}������auth amount (amts[0])��ֵ
     * </div>
     * <div class="en">
     * 		This authorization amount will overwrite the auth amount(amts[0]) in {@link EmvManager.EmvCallbackHandler#onInputAmount}.
     * </div>
     * 
     * @param cashBackAmt
     * <div class="zh">
     *		�ý�������{@link EmvManager.EmvCallbackHandler#onInputAmount}������callback amount (amts[1])��ֵ
     * </div>
     * <div class="en">
     * 		This authorization amount will overwrite the cashback amount(amts[1]) in {@link EmvManager.EmvCallbackHandler#onInputAmount}.
     * </div>
     * 
     * @return
     * <div class="zh">
     *		int[0]: ���׽�� <br/>
     *		<ul>
     *			<li>{@link #EMV_OK}: ���״���ɹ�
     *			<li>{@link #EMV_DENIAL}: ���׾ܾ�
     *			<li>{@link #EMV_DATA_ERR}: ��Ƭ���ݸ�ʽ����
     *			<li>{@link #EMV_NOT_ACCEPT}: ���ײ�����
     *			<li>{@link #ICC_CMD_ERR}: IC������ʧ��
     *			<li>{@link #ICC_RSP_6985}: GAC�п�Ƭ����6985
     *			<li>{@link #EMV_RSP_ERR}: GACӦ�����
     *			<li>{@link #EMV_PARAM_ERR}: ��������
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
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ʵ�ֶ�����Ӧ�����ݵĴ����ⲿ��֤���ű��������ڶ���GAC���� <br/>
     * 1.������׼��ܾ�ʱ, ������������, ������Ӧ��ͨ��SetTLV��ʽ�������ã�ARC-8A, AC-89, IAD-91 <br/>
     * 2.����"�����ѻ��ܾ�, Ҫ����֪ͨ"�������Ӧ�ø���CID��ȡֵ�ж��Ƿ������֪ͨ��Ϣ����
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
     *		<li>{@link #EMV_ONLINE_APPROVE}: ������׼, ���� host ���زο�ʱ, ����Աѡ����׼
     *		<li>{@link #EMV_ONLINE_FAILED}: �޷�����
     *		<li>{@link #EMV_ONLINE_DENIAL}: �����ܾ�, ���� host ���زο�ʱ, ����Աѡ��ܾ�
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
     *		[����]�ű����ݣ�TLV��ʽ��
     * </div>
     * <div class="en">
     * 		[input]Issuer script data in TLV format
     * </div>
     * 
     * @return
     * <div class="zh">
     *		int[0]: ���׽�� <br/>
     *		<ul>
     *			<li>{@link #EMV_OK}: ���״���ɹ�
     *			<li>{@link #EMV_DENIAL}: ���׾ܾ�
     *			<li>{@link #EMV_DATA_ERR}: ��Ƭ���ݸ�ʽ����
     *			<li>{@link #EMV_NOT_ACCEPT}: ���ײ�����
     *			<li>{@link #ICC_CMD_ERR}: IC������ʧ��
     *			<li>{@link #ICC_RSP_6985}: GAC�п�Ƭ����6985
     *			<li>{@link #EMV_RSP_ERR}: GACӦ�����
     *			<li>{@link #EMV_PARAM_ERR}: ��������
     *		</ul>  
     *
     *		int[1]: AC type<br/>
     *		<ul>
     *			<li>{@link #EMV_AC_TC}: ������׼
     *			<li>{@link #EMV_AC_AAC}: �ն�����TC, ����ƬCID���ؾܾ�
     *			<li>{@link #EMV_AC_AAC_HOST}: ������ʱ����EMV_ONLINE_DENIAL{@link #EMV_ONLINE_DENIAL}���ܾ�
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
     *			<li>{@link #EMV_AC_AAC_HOST}: rejected because of online transaction returns {@link #EMV_ONLINE_DENIAL}���ܾ�
     *		</ul>
     *     * </div>
     *  
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * �������ò���
     * </div>
     * <div class="en">
     * Set configuration.
     * </div>
     * 
     * @param flag
     * <div class="zh">
     * 		Ŀǰ��bit 1 - bit 3��Ч, ����λ������չ. <br/>
     * 		bit 1�� <br/>
     *		1 -֧��advice��{@link #EMV_CONFIG_FLAG_BIT_SUPPORT_ADVICE} <br/>
     *		0 -��֧��advice��Ĭ��Ϊ0����<br/>
     *		bit 2�� <br/>
     *		1 -��PIN����ҲҪ�ֿ���ȷ�Ͻ� {@link #EMV_CONFIG_FLAG_BIT_CONFIRM_AMT_WHEN_NO_PIN}<br/>
     *		0 -��PIN���벻��Ҫ�ֿ���ȷ�Ͻ�Ĭ��Ϊ0�� <br/>
     *		bit 3�� <br/>
     *		1 -֧�ֽ�����־�� .{@link #EMV_CONFIG_FLAG_BIT_SUPPORT_TRANSLOG} <br/>
     *		0 -��֧�ֽ�����־<br/>
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
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��������ѡ���Լ�GPO�����п�Ƭ���ص����� <br/>
     * <b>�˺��������ڱ�׼�ǽ�PBOCӦ��</b>
     * </div>
     * <div class="en">
     * Set response data of final selection and GPO command into EMV kernel.<br/>
     * <b>This function is only used for Clss PBOC application</b>
     * </div>
     * 
     * @param transParam
     * <div class="zh">
     * 		[����]������ز���, �μ� {@link model.CLSS_TRANS_PARAM}
     * </div>
     * <div class="en">
     * 		[input]Transaction related parameters
     * </div>
     *   
     * @param selData
     * <div class="zh">
     * 		[����]����ѡ������ص�����, ����ͨ�� {@link ClssManager.Entry#getFinalSelectData}����ȡ
     * </div>
     * <div class="en">
     * 		[input]Response data of final selection command.,which need to be got by  {@link ClssManager.Entry#getFinalSelectData} in Entry library
     * </div>
     *   
     * @param GPOData
     * <div class="zh">
     * 		[����]����ѡ������ص�����, ����ͨ�� {@link ClssManager.Pboc#getGPOData}����ȡ
     * </div>
     * <div class="en">
     * 		[input]The data of GPO command and response, which need to be got by  {@link ClssManager.Pboc#getGPOData} in qPBOC library.
     * </div>
     * 
     * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ���ý��׽��ͷ��ֽ�� <br/>
     * ����0xFFFFFFFF�Ľ����ô˽ӿڽ��д��ͽ��ں�, ����{@link EmvManager#procTrans}��{@link EmvManager#startTrans}֮ǰ���ô˽ӿ�,
     * �����ô˽ӿ�, {@link EmvManager.EmvCallbackHandler#onInputAmount}���뽫authAmt��callBackAmt������Ϊ0������{@link #EMV_OK}
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
     * 		���׽��, ���ṩ�ɼ��ַ���(��"112345678900"). �����ڲ���ת���� 9F02����Ԫ�ĸ�ʽ(��"\x11\x23\x45\x67\x89\x00")
     * </div>
     * <div class="en">
     * 		Authorised Amount, please provide with human-readable string.
     * Inside this method, the string will be translated to be conform to the format of tag '9F02'(i.e."\x11\x23\x45\x67\x89\x00").

     * </div>
     *   
     * @param cashBackAmt
     * <div class="zh">
     * 		���ֽ��, ���ṩ�ɼ��ַ���(��"112345678900"). �����ڲ���ת���� 9F03����Ԫ�ĸ�ʽ(��"\x11\x23\x45\x67\x89\x00")
     * </div>
     * <div class="en">
     * 		cashback Amount, please provide with human-readable string(say "112345678900").
     * Inside this method, the string will be translated to be conform to the format of tag '9F03'(i.e."\x11\x23\x45\x67\x89\x00").
     * </div>
     * 
	 * @throws EmvException
     * <div class="zh">EMV����</div>
     * <div class="en">EMV error</div>
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��Ȧ����־��PBOC3.0�� <br/>
     * Ӧ�ó������� {@link EmvManager#appSelectForLog} ���Ӧ��ѡ��֮��, ���øú�����ȡ��ѡ����Ӧ�õĽ�����־, recordNo��¼�Ŵ�1��ʼ;<br/>
     * �ú���ֻ�ǰѽ�����־����EMV�ں˵Ļ�����, Ӧ�ó������ͨ��{@link EmvManager#getSingleLoadLogItem}��ȡ�������־����, ���罻�׽�ʱ��ȵ�
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
     * 		��¼��, ��1��ʼ
     * </div>
     * <div class="en">
     * 		record no. starting from 1
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		true: ��ȡȦ����־�ɹ� <br/>
     * 		false: ���ݲ����ڻ����ݴ���
     * </div>
     * <div class="en">
     * 		true: read load log succeeded<br/>
     * 		false: no specified log data.
     * </div>
     * 
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * ��Ȧ����־�������PBOC3.0�� <br/>
     * ����ͨ��{@link EmvManager#readSingleLoadLog}����������ÿһ��Ȧ����־��¼, Ӧ�ó��򶼿���ͨ��������ȥ��ȡ�������־��Ϣ
     * </div>
     * <div class="en">
     * read load log item (PBOC3.0) <br/>
     * read contents of the log which is loaded with {@link EmvManager#readSingleLoadLog} previously
     * </div>
     * 
     * @param tag
     * <div class="zh">
     * 		��Ҫ��ȡ��������ı�ǩ
     * </div>
     * <div class="en">
     * 		the tag to read
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		null: ���ݲ����ڻ����ݴ���<br/>
     * 		��null: �������ֵ
     * </div>
     * <div class="en">
     * 		null: no data for specified tag<br/>
     * 		non-null: the value of the specified tag
     * </div>
     * 
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * һ���Զ�ȡ����Ȧ����־��PBOC3.0�� <br/>
     * ��ȡ������Ȧ����־��ʽ��ο�PBOC 3.0��13���12����13
     * </div>
     * <div class="en">
     * read all load log (PBOC3.0)<br/>
     * please refer to PBOC3.0 book13, table 12 and table 13 for the format of the load log.
     * </div>
     * 
     * @return
     * <div class="zh">
     * 		null: ���ݲ����ڻ����ݴ���<br/>
     * 		��null: Ȧ����־����
     * </div>
     * <div class="en">
     * 		null: no load log data<br/>
     * 		non-null: the load log data
     * </div>
     * 
     * @throws IOException
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
     * <div class="zh">ͨ�Ŵ���</div>
     * <div class="en">communication error</div>
     * @throws ProtoException
     * <div class="zh">Э�����</div>
     * <div class="en">protocol error</div>
     * @throws CommonException
     * <div class="zh">ͨ�ô���</div>
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
