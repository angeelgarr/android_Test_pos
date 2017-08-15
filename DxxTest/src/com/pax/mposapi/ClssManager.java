package com.pax.mposapi;

import java.io.IOException;

import android.content.Context;

import com.pax.mposapi.comm.Cmd;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.comm.RespCode;
import com.pax.mposapi.model.CLSS_MC_AID_PARAM_MC;
import com.pax.mposapi.model.CLSS_PBOC_AID_PARAM;
import com.pax.mposapi.model.CLSS_PRE_PROC_INFO;
import com.pax.mposapi.model.CLSS_PRE_PROC_INTER_INFO;
import com.pax.mposapi.model.CLSS_READER_PARAM;
import com.pax.mposapi.model.CLSS_READER_PARAM_MC;
import com.pax.mposapi.model.CLSS_TERM_CONFIG_MC;
import com.pax.mposapi.model.CLSS_TM_AID_LIST;
import com.pax.mposapi.model.CLSS_TRANS_PARAM;
import com.pax.mposapi.model.CLSS_VISA_AID_PARAM;
import com.pax.mposapi.model.EMV_CAPK;
import com.pax.mposapi.model.EMV_REVOC_LIST;
import com.pax.mposapi.model.POSLOG;
import com.pax.mposapi.model.SYS_PROC_INFO;
import com.pax.mposapi.util.MyLog;
import com.pax.mposapi.util.Utils;

/**
 * <div class="zh">
 * ClssManager 用于处理非接交易
 * </div>
 * <div class="en">
 * ClssManager is used to process contactless transation
 * </div>
 *
 */
public class ClssManager {
	
	//clss kernel type
	public static final int CLSS_KERNTYPE_DEF 					= 0;
	public static final int CLSS_KERNTYPE_JCB 					= 1;	
	public static final int CLSS_KERNTYPE_MC  					= 2;
	public static final int CLSS_KERNTYPE_VIS 					= 3;
	public static final int CLSS_KERNTYPE_PBOC 					= 4;	
	public static final int CLSS_KERNTYPE_AE 					= 5;
	public static final int CLSS_KERNTYPE_ZIP 					= 6;
	public static final int CLSS_KERNTYPE_FLASH 				= 7;
	public static final int CLSS_KERNTYPE_RFU 					= 8;

	//trans path
	public static final int CLSS_VISA_MSD     					= 1;   // scheme_visa_msd_20
	public static final int CLSS_VISA_QVSDC   					= 2;   // scheme_visa_wave3
	public static final int CLSS_VISA_VSDC    					= 3;   // scheme_visa_full_vsdc
	public static final int CLSS_VISA_CONTACT 					= 4;   

	//cvm type
	public static final int RD_CVM_NO							= 0x00;//no CVM
	public static final int RD_CVM_SIG							= 0x10;//signature
	public static final int RD_CVM_ONLINE_PIN					= 0x11;//online PIN
	public static final int RD_CVM_OFFLINE_PIN					= 0x12;//offline PIN
	public static final int RD_CVM_CONSUMER_DEVICE				= 0x1F;//Refer to consumer device
	/*
	 * FIXME: ???
	// 当 amount > contactless cvm limit 时，需要执行何种CVM方式：
	public static final int EMV_CVM_REQ_SIG						= 0x01;
	public static final int EMV_CVM_REQ_ONLINE_PIN				= 0x02;
	*/		
	
    private static final String TAG = "ClssManager";
    private final Proto proto;
    private static ClssManager instance;
    
    //main purpose for this global resp buffer is to ensure 
    //enough receive buffer for ALL callback functions (i.e. passive cmd)
    private static final byte[] respBuffer = new byte[10240];
    
    /**
     * <div class="zh">
     * 用于访问Entry接口的对象
     * </div>
     * <div class="en">
     * an object used to access the Entry interfaces
     * </div>
     */
    public Entry entry;
    /**
     * <div class="zh">
     * 用于访问Pboc接口的对象
     * </div>
     * <div class="en">
     * an object used to access the Pboc interfaces
     * </div>
     */
    public Pboc  pboc;

    /**
     * an object used to access the Integrate interfaces
     */
    public Integrate integrate;
    
    private static ConfigManager cfg;
    
    /**
     * <div class="zh">
     * 使用指定的Context构造出ClssManager对象
     * </div>
     * <div class="en">
     * Create a ClssManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">应用当前的context</div>
     * <div class="en">application context currently</div>
     */    
    private ClssManager(Context context) {
    	proto = Proto.getInstance(context);
    	entry = new Entry();
    	pboc  = new Pboc();
    	integrate = new Integrate();
    	cfg = ConfigManager.getInstance(context);
    }
    
    /**
     * Create a ClssManager instance with a given Context
     * 
     * @param context
     *            application context currently
     */
    public static ClssManager getInstance(Context context) {
        if (instance == null) {
        	instance = new ClssManager(context);
        }
        return instance;
    }
    
    /**
     * <div class="zh">
     * Entry 实现 entry point功能
     * </div>
     * <div class="en">
     * Entry implements entry point functions
     * </div>
     *
     */
    public class Entry {
        /**
         * <div class="zh">
         * 查询Entry内核版本
         * </div>
         * <div class="en">
         * Query the version of entry kernel.
         * </div>
         * 
    	 * @return
    	 * <div class="zh">
    	 * 		Entry内核版本
    	 * </div>
    	 * <div class="en">
    	 * 		Entry's version number and issue date
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
		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_READ_VER_INFO, new byte[0], rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    			return new String(respBuffer, 1, respBuffer[0]);
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}
    	
        /**
         * <div class="zh">
         * 添加一个AID数据到内核
         * </div>
         * <div class="en">
         * Add an application to application list.
         * </div>
         * 
         * @param aid
    	 * <div class="zh">
    	 * 		[输入]AID
    	 * </div>
    	 * <div class="en">
    	 * 		[input]AID name.
    	 * </div>
    	 * 
         * @param selFlg
    	 * <div class="zh">
    	 * 		AID匹配选项<br/>
    	 * 		{@link EmvManager#EMV_APP_SEL_PARTIAL_MATCH}: 部分匹配 <br/>
    	 * 		{@link EmvManager#EMV_APP_SEL_FULL_MATCH}: 完全匹配 
    	 * </div>
    	 * <div class="en">
    	 * 		AID matching flag<br/>
    	 * 		{@link EmvManager#EMV_APP_SEL_PARTIAL_MATCH}: partial matching<br/>
    	 * 		{@link EmvManager#EMV_APP_SEL_FULL_MATCH}: full matching
    	 * </div>
    	 * 
         * @param kernType
    	 * <div class="zh">
    	 * 		应用内核类型<br/>
    	 * 		<ul>
    	 * 			<li>{@link #CLSS_KERNTYPE_DEF}: 根据RID. 内核进行匹配确认
    	 * 			<li>{@link #CLSS_KERNTYPE_JCB}: JCB
    	 * 			<li>{@link #CLSS_KERNTYPE_MC}: MASTER CARD
    	 * 			<li>{@link #CLSS_KERNTYPE_VIS}: VISA
    	 * 			<li>{@link #CLSS_KERNTYPE_PBOC}: PBOC
    	 * 			<li>{@link #CLSS_KERNTYPE_AE}: American Express
    	 * 		</ul>
    	 * 
    	 * </div>
    	 * <div class="en">
    	 * 		application kernel type<br/>
    	 * 		<ul>
    	 * 			<li>{@link #CLSS_KERNTYPE_DEF}: Kernel will do matching according to its RID.
    	 * 			<li>{@link #CLSS_KERNTYPE_JCB}: JCB
    	 * 			<li>{@link #CLSS_KERNTYPE_MC}: MASTER CARD
    	 * 			<li>{@link #CLSS_KERNTYPE_VIS}: VISA
    	 * 			<li>{@link #CLSS_KERNTYPE_PBOC}: PBOC
    	 * 			<li>{@link #CLSS_KERNTYPE_AE}: American Express
    	 * 		</ul>
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
    	public void addAidList(byte[] aid, int selFlg, int kernType) throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		byte[] req = new byte[1 + aid.length + 1 + 1];
    		req[0] = (byte)aid.length;
    		System.arraycopy(aid, 0, req, 1, aid.length);
    		req[1 + aid.length] = (byte)selFlg;
    		req[1 + aid.length + 1] = (byte)kernType;
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_ADD_AID_LIST, req, rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}

        /**
         * <div class="zh">
         * 从内核中删除一个AID数据
         * </div>
         * <div class="en">
         * Delete an application from the application list
         * </div>
         * 
         * @param aid
    	 * <div class="zh">
    	 * 		[输入]AID
    	 * </div>
    	 * <div class="en">
    	 * 		[input]AID
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
    	public void delAidList(byte[] aid) throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		byte[] req = new byte[1 + aid.length];
    		req[0] = (byte)aid.length;
    		System.arraycopy(aid, 0, req, 1, aid.length);
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_DEL_AID_LIST, req, rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}

        /**
         * <div class="zh">
         * 从内核中删除所有AID数据
         * </div>
         * <div class="en">
         * Delete all applications in the application list
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
    	public void delAllAidList() throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_DEL_ALL_AID_LIST, new byte[0], rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}      
    	
        /**
         * <div class="zh">
         * 设置一个AID对应的交易预处理需使用的参数 <br/>
         * 1.需在交易预处理之前调用该函数;<br/>
         * 2.若已存在该AID对应的参数,则将原参数覆盖;<br/> 
         * 3.设置交易预处理参数前需要先添加其对应的AID应用,否则该函数会抛出EMV_ERR_NOT_FOUND异常 
         * </div>
         * <div class="en">
         * Set AID's corresponding parameters used in Preliminary Transaction Processing.<br/>
         * 1.This function must be called before transaction preprocessing.<br/>
		 * 2.If the AID's corresponding parameters used in Preliminary Transaction Processing already exists, the old one will be replaced by this new one.<br/>
		 * 3.Before set the preliminary parameter, you must add its application into kernel first. If not, this function will return EMV_ERR_NOT_FOUND.<br/>
         * </div>
    	 * 
    	 * @param preProcInfo
         * <div class="zh">
         * 		[输入]AID对应的交易预处理需使用的参数
         * </div>
         * <div class="en">
         * 		[input]Parameters used in transaction preprocessing corresponding to an AID
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
    	public void setPreProcInfo(CLSS_PRE_PROC_INFO preProcInfo) throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		byte[] req = preProcInfo.serialToBuffer();
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_SET_PRE_PROC_INFO, req, rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}

        /**
         * <div class="zh">
         * 删除一个与指定AID对应的交易预处理需使用的参数<br/>
         * 需在交易预处理之前调用该函数, 调用该接口不会删除其对应的应用
         * </div>
         * <div class="en">
         * Delete AID's correspongding parameters used in preliminary transaction processing.<br/>
         * This function must be called before preliminary transaction processing. 
         * </div>
    	 * 
         * @param aid
    	 * <div class="zh">
    	 * 		[输入]AID
    	 * </div>
    	 * <div class="en">
    	 * 		[input]AID
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
    	public void delPreProcInfo(byte[] aid) throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		byte[] req = new byte[1 + aid.length];
    		req[0] = (byte)(aid.length);
    		System.arraycopy(aid, 0, req, 1, aid.length);
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_DEL_PRE_PROC_INFO, req, rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}

        /**
         * <div class="zh">
         * 删除所有交易预处理需使用的参数<br/>
         * 需在交易预处理之前调用该函数,调用该接口不会删除所有应用
         * </div>
         * <div class="en">
         * Delete all parameters used in preliminary transaction processing.<br/>
         * This function must be called before preliminary transaction processing.
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
    	public void delAllPreProcInfo() throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();

    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_DEL_ALL_PRE_PROC_INFO, new byte[0], rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}
    	 
        /**
         * <div class="zh">
         * 交易预处理<br/>
         * 须在轮询非接触卡片之前调用该函数
         * </div>
         * <div class="en">
         * Transaction preprocessing.<br/>
         * This function must be called before detecting card.
         * </div>
    	 * 
         * @param transParam
    	 * <div class="zh">
    	 * 		[输入]交易相关参数结构
    	 * </div>
    	 * <div class="en">
    	 * 		[input]related parameters of transaction.
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
    	public void preTransProc(CLSS_TRANS_PARAM transParam) throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		byte[] req = transParam.serialToBuffer();
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_PRE_TRANS_PROC, req, rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}
   	 
        /**
         * <div class="zh">
         * 应用选择,建立候选列表<br/>
         * 要求在调用该函数前,应用应该通过 {@link PiccManager#piccDetect(byte)} 函数判断出已经有Type A或Type B卡
         * </div>
         * <div class="en">
         * Application selection.<br/>
         * Before calling this function, {@link PiccManager#piccDetect(byte)} must be called to judge whether a type A or type B card is detected or not. 
         * </div>
    	 * 
         * @param slot
    	 * <div class="zh">
    	 * 		该参数暂未使用
    	 * </div>
    	 * <div class="en">
    	 * 		currently not used
    	 * </div>
    	 * 
         * @param readLogFlag
    	 * <div class="zh">
    	 * 		该参数暂未使用
    	 * </div>
    	 * <div class="en">
    	 * 		currently not used
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
    	public void appSlt(int slot, int readLogFlag) throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		byte[] req = new byte[2];
    		req[0] = (byte)slot;
    		req[1] = (byte)readLogFlag;
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_APPSLT, req, rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}

        /**
         * <div class="zh">
         * 最终应用选择, 由内核自动选择最高优先级应用<br/>
         * 接口返回{@link EmvManager#EMV_RSP_ERR}或 {@link EmvManager#EMV_APP_BLOCK}或{@link EmvManager#ICC_BLOCK}或{@link EmvManager#CLSS_RESELECT_APP}时, 
         * 应用层应调用{@link #delCurCandApp()}删除当前应用, 若候选列表中还有其他应用, 继续调用{@link ClssManager.Entry#finalSelect(byte[])}选择下一个应用
         * </div>
         * <div class="en">
         * Final selection, application with the highest priority will be selected automatically by kernel.<br/>
         * if {@link EmvManager#EMV_RSP_ERR},  {@link EmvManager#EMV_APP_BLOCK}, {@link EmvManager#ICC_BLOCK} or {@link EmvManager#CLSS_RESELECT_APP} is returned, 
         * application should call {@link #delCurCandApp()} to delete current application. If there're oehter applications in the candidate list, call this function again to re-select application.
         * </div>
    	 * 
         * @param data
    	 * <div class="zh">
    	 * 		[输出]格式为kernel type(1字节) + AID(1字节长度 + 数据) <br/>
    	 * 		kernel type为:<br/>
    	 * 		<ul>
    	 * 			<li>{@link #CLSS_KERNTYPE_JCB}: JCB
    	 * 			<li>{@link #CLSS_KERNTYPE_MC}: MASTER CARD
    	 * 			<li>{@link #CLSS_KERNTYPE_VIS}: VISA
    	 * 			<li>{@link #CLSS_KERNTYPE_PBOC}: PBOC
    	 * 			<li>{@link #CLSS_KERNTYPE_AE}: American Express
    	 * 		</ul>
    	 * </div>
    	 * <div class="en">
    	 * 		[output]kernel type[1byte] + AID(1byte L + V) <br/>
    	 * 		kernel type can be: <br/>
    	 * 		<ul>
    	 * 			<li>{@link #CLSS_KERNTYPE_JCB}: JCB
    	 * 			<li>{@link #CLSS_KERNTYPE_MC}: MASTER CARD
    	 * 			<li>{@link #CLSS_KERNTYPE_VIS}: VISA
    	 * 			<li>{@link #CLSS_KERNTYPE_PBOC}: PBOC
    	 * 			<li>{@link #CLSS_KERNTYPE_AE}: American Express
    	 * 		</ul>
    	 * </div>
    	 * 
	     * @return
	     * <div class="zh">
	     * <ul>
	     *		<li>{@link EmvManager#EMV_OK}: 成功
	     *		<li>{@link EmvManager#ICC_CMD_ERR}: IC卡命令失败
	     *		<li>{@link EmvManager#EMV_RSP_ERR}: IC卡命令响应码错误
	     *		<li>{@link EmvManager#EMV_NO_APP}: 没有终端支持的EMV应用
	     *		<li>{@link EmvManager#EMV_APP_BLOCK}: 应用已锁
	     *		<li>{@link EmvManager#ICC_BLOCK}: IC卡已锁
	     *		<li>{@link EmvManager#EMV_DATA_ERR}: 数据格式错, 若有需要. 可调用接口{@link #getErrorCode()}获取具体的错误码信息
	     *		<li>{@link EmvManager#CLSS_RESELECT_APP}: 重新进行最终应用选择（仅针对paypss应用）
	     * </ul>
	     * </div>
	     * <div class="en">
	     * <ul>
	     *		<li>{@link EmvManager#EMV_OK}: succeed
	     *		<li>{@link EmvManager#ICC_CMD_ERR}: IC card command failed.
	     *		<li>{@link EmvManager#EMV_RSP_ERR}: IC card response code error.
	     *		<li>{@link EmvManager#EMV_NO_APP}: No EMV application that terminal supported.
	     *		<li>{@link EmvManager#EMV_APP_BLOCK}: application blocked
	     *		<li>{@link EmvManager#ICC_BLOCK}: IC card has been blocked
	     *		<li>{@link EmvManager#EMV_DATA_ERR}: data error, further check the detailed code with{@link #getErrorCode()}
	     *		<li>{@link EmvManager#CLSS_RESELECT_APP}: re-select application(only for paypss application)
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
    	//data : kernel type(1) + AID(L1 + V)
    	public int finalSelect(byte[] data) throws IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_FINAL_SELECT, new byte[0], rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    			data[0] = respBuffer[0];
    			System.arraycopy(respBuffer, 1, data, 1, 1 + respBuffer[1]);
    		}
			return rc.code;
    	}
    	
        /**
         * <div class="zh">
         * 删除候选列表中的当前应用<br/>
         * 若后续GPO命令返回 6985, 则需通过该接口函数删除当前候选应用, 并重新进行最终选择操作
         * </div>
         * <div class="en">
         * Delete the current application from the candidate list<br/>
         * If the Get Processing Options command return status 6985. You need to use this function to delete the first application in the candidate list. Then you can redo the final select.
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
    	public void delCurCandApp() throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_DEL_CUR_CAND_APP, new byte[0], rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}
    	
        /**
         * <div class="zh">
         * 获取与最终选择应用AID对应的在交易预处理过程中动态设置的内部参数<br/>
         * 1.须在最终选择之后,交易继续处理前调用该函数;<br/>
         * 2.调用后,根据当前交易类型,继续调用{@link Pboc#setTransData}接口进行设置 
         * </div>
         * <div class="en">
         * Get the finally selected AID's corresponding parameters, which are dynamically set during preliminary transaction processing.<br/>
         * 1.This function must be called between final selection and transaction processing;<br/>
		 * 2.After calling this function, further call {@link Pboc#setTransData} to set the parameter.  
         * </div>
    	 * 
    	 * @return
         * <div class="zh">
         * 	内核中交易预处理时设置的内部参数, 参见{@link com.pax.mposapi.model.CLSS_PRE_PROC_INTER_INFO}
         * </div>
         * <div class="en">
         * 	the finally selected AID's corresponding parameters, see {@link com.pax.mposapi.model.CLSS_PRE_PROC_INTER_INFO}
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
    	public CLSS_PRE_PROC_INTER_INFO getPreProcInterFlg() throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_GET_PRE_PROC_INTER_FLG, new byte[0], rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    			CLSS_PRE_PROC_INTER_INFO preProcInterInfo = new CLSS_PRE_PROC_INTER_INFO();
    			preProcInterInfo.serialFromBuffer(respBuffer);
    			return preProcInterInfo;
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}

        /**
         * <div class="zh">
         * 获取与最终选择应用AID对应的在交易预处理过程中动态设置的内部参数<br/>
         * 1.须在最终选择之后, 交易继续处理前调用该函数;<br/>
         * 2.调用后,根据当前交易类型,继续调用{@link Pboc#setFinalSelectData}接口进行设置 
         * </div>
         * <div class="en">
         * Get related data returned from card in final selection<br/>
         * 1.This function must be called between final selection and Get Processing Options .<br/>
		 * 2.After calling this function, further call {@link Pboc#setFinalSelectData} to set the parameter. 
         * </div>
    	 * 
    	 * @return
         * <div class="zh">
         * 	最终选择时保存的相关数据
         * </div>
         * <div class="en">
         *  Related data saved during final selection.
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
    	public byte[] getFinalSelectData() throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_GET_FINAL_SELECT_DATA, new byte[0], rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    			int len = Utils.intFromByteArray(respBuffer, 0);
    			byte[] ret = new byte[len];
    			System.arraycopy(respBuffer, 4, ret, 0, len);
    			return ret;
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}

        /**
         * <div class="zh">
         * 选择被锁应用<br/>
         * 1.须在最终选择之后, 交易继续处理前调用该函数;<br/>
         * 2.调用后, 根据当前交易类型, 继续调用  {@link Pboc#setFinalSelectData} 接口进行设置
         * </div>
         * <div class="en">
         * select blocked application<br/>
         * 1.This function must be called between final selection and Get Processing Options .<br/>
		 * 2.After calling this function, further call {@link Pboc#setFinalSelectData} to set the parameter. 
         * </div>
    	 * 
    	 * @param transParam
         * <div class="zh">
         * 		[输入]交易相关参数
         * </div>
         * <div class="en">
         * 		[input]related parameters of transaction.
         * </div>
         * 
    	 * @param termAid
         * <div class="zh">
         * 		[输入]被锁AID相关参数
         * </div>
         * <div class="en">
         * 		[input]AID related paramters of the blocked application 
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
    	public void appSelectUnlockApp(CLSS_TRANS_PARAM transParam, CLSS_TM_AID_LIST termAid) throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		byte[] tp = transParam.serialToBuffer();
    		byte[] ta = termAid.serialToBuffer();
    		byte[] req = new byte[tp.length + ta.length];
    		System.arraycopy(tp, 0, req, 0, tp.length);
    		System.arraycopy(ta, 0, req, tp.length, ta.length);
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_APP_SELECT_UNLOCK_APP, req, rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}
    	
        /**
         * <div class="zh">
         * 获取具体的错误码信息<br/>
         * 目前此函数仅限在{@link ClssManager.Entry#appSlt(int, int)} 抛出{@link EmvException#EMV_ERR_NO_APP_PPSE}或 
         * {@link ClssManager.Entry#finalSelect(byte[])}返回{@link EmvException#EMV_ERR_DATA}时使用.<br/>
         * </div>
         * <div class="en">
         * get detailed error code<br/>
         * currently it's only used when {@link ClssManager.Entry#appSlt(int, int)} throwing {@link EmvException#EMV_ERR_NO_APP_PPSE} or 
         * when {@link ClssManager.Entry#finalSelect(byte[])}returning {@link EmvException#EMV_ERR_DATA}.
         * </div>
    	 * 
    	 * @return
         * <div class="zh">
         * 		错误码<br/>
         * 		1.对{@link ClssManager.Entry#appSlt(int, int)} . errorcode 的值可能为{@link EmvManager#EMV_DATA_ERR}. {@link EmvManager#EMV_RSP_ERR}, {@link EmvManager#EMV_APP_BLOCK}.<br/>
         * 		2.对{@link ClssManager.Entry#finalSelect(byte[])}. errorcode 的值可能为{@link EmvManager#EMV_DATA_ERR}. {@link EmvManager#EMV_NO_DATA}.  
         * </div>
         * <div class="en">
         * 		error code<br/>
         * 		1.for {@link ClssManager.Entry#appSlt(int, int)}, errorcode can be {@link EmvManager#EMV_DATA_ERR}, {@link EmvManager#EMV_RSP_ERR}, {@link EmvManager#EMV_APP_BLOCK}.<br/>
         *		2.for {@link ClssManager.Entry#finalSelect(byte[])}, errorcode can be{@link EmvManager#EMV_DATA_ERR}, {@link EmvManager#EMV_NO_DATA}.
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
    	public int getErrorCode() throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_GET_ERROR_CODE, new byte[0], rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    			return Utils.intFromByteArray(respBuffer, 0);
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}

        /**
         * <div class="zh">
         * 设置Entry库应符合的PayPass规范版本<br/>
         * 1.此函数仅限Entry库用于PayPass应用时调用;<br/>
         * 2.此函数必须在{@link ClssManager.Entry#appSlt(int, int)}之前调用 
         * </div>
         * <div class="en">
         * set the specification version that Entry conforms<br/>
         * 1.this is only used for PayPass application.<br/>
         * 2.must be called before {@link ClssManager.Entry#appSlt(int, int)}.
         * </div>
    	 * 
    	 * @param ver
         * <div class="zh">
         * 		0x03: 支持paypass v3.0
         * 		0x02: 支持paypass v2.1（默认值）
         * </div>
         * <div class="en">
         * 		0x03: support paypass v3.0
         * 		0x02: support paypass v2.1(default)
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
    	public void setMCVersion(int ver) throws EmvException, IOException, ProtoException, CommonException {
    		RespCode rc = new RespCode();
    		byte[] req = new byte[1];
    		req[0] = (byte)ver;
    		
    		proto.sendRecv(Cmd.CmdType.CLSS_ENTRY_SET_MC_VERSION, req, rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}
    }
    
    /**
     * <div class="zh">
     * Pboc 实现  qPBOC 功能
     * </div>
     * <div class="en">
     * Pboc implements qPBOC functions 
     * </div>
     */
    public class Pboc {
        /**
         * <div class="zh">
         * 查询qPBOC内核版本
         * </div>
         * <div class="en">
         * Query the version number of PayWave kernel.
         * </div>
         * 
    	 * @return
    	 * <div class="zh">
    	 * 		qPBOC内核版本
    	 * </div>
    	 * <div class="en">
    	 * 		version of qPBOC kernel
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
		
    		proto.sendRecv(Cmd.CmdType.CLSS_PBOC_READ_VER_INFO, new byte[0], rc, respBuffer);
    		if (rc.code == 0) {
    			//success
    			return new String(respBuffer, 1, respBuffer[0]);
    		} else {
    			throw new EmvException(rc.code);
    		}
    	}

        /**
         * <div class="zh">
         * 读取指定标签的数据值<br/>
         * CLSS库除了能存储所有的EMV定义的标准数据标签的数据, 还能额外存储最多64个发卡方自定义的标签的数据值, 应用程序在读应用数据后可调用该函数读取需要的数据值
         * </div>
         * <div class="en">
         * Get the data element specified by the tag.
         * </div>
         * 
         * @param tag
    	 * <div class="zh">
    	 * 		EMV定义的标准数据元素标签或扩展的标签
    	 * </div>
    	 * <div class="en">
    	 * 		Tag of EMV standard or extended data.
    	 * </div>
    	 *
    	 * @return
    	 * <div class="zh">
    	 * 		null : 无此标签<br/>
    	 * 		非null: 标签值
    	 * </div>
    	 * <div class="en">
    	 * 		null: no specified tag<br/>
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
        //null or data
        public byte[] getTLVData(int tag) throws IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = new byte[2];
        	Utils.short2ByteArray((short)tag, req, 0);

        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_GET_TLV_DATA, req, rc, respBuffer);
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
         * 设置指定标签的数据值
         * </div>
         * <div class="en">
         * Set the data element specified by the tag.
         * </div>
         * 
         * @param tag
    	 * <div class="zh">
    	 * 		EMV定义的标准数据元素标签或扩展的标签
    	 * </div>
    	 * <div class="en">
    	 * 		EMV standard or extended data element Tag.
    	 * </div>
    	 *
         * @param value
    	 * <div class="zh">
    	 * 		[输入]标签Tag指定的数据
    	 * </div>
    	 * <div class="en">
    	 * 		[input]The value of the data element specified by the tag.
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
        public void setTLVData(int tag, byte[] value) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = new byte[2 + 4 + value.length];
        	Utils.short2ByteArray((short)tag, req, 0);
        	Utils.int2ByteArray(value.length, req, 2);
        	System.arraycopy(value, 0, req, 6, value.length);

        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_SET_TLV_DATA, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else {
            	throw new EmvException(rc.code);
        	}
        }    

        /**
         * <div class="zh">
         * <b><font color=red>注意: D180 不支持此功能,参考"MPosApi_Supplementary.docx"获取更多相关信息 </font></b><br/>
         * 设置用户自定义标签数据值
         * </div>
         * <div class="en">
         * <b><font color=red>NOTE: D180 doesn't support this function,see "MPosApi_Supplementary.docx" for more </font></b><br/>
         * Set the data element by user-defined tag
         * </div>
         * 
         * @param tag
    	 * <div class="zh">
    	 * 		用户自定义标签
    	 * </div>
    	 * <div class="en">
    	 * 		User-defined tag.
    	 * </div>
    	 *
         * @param value
    	 * <div class="zh">
    	 * 		[输入]标签Tag指定的数据
    	 * </div>
    	 * <div class="en">
    	 * 		[input]The value of the data element specified by the tag.
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
        public void setEMVUnknownTLVData(int tag, byte[] value) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = new byte[2 + 4 + value.length];
        	Utils.short2ByteArray((short)tag, req, 0);
        	Utils.int2ByteArray(value.length, req, 2);
        	System.arraycopy(value, 0, req, 6, value.length);

        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_SET_EMV_UNKNOWN_TLV_DATA, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else {
            	throw new EmvException(rc.code);
        	}
        }           
        
        /**
         * <div class="zh">
         * 读取读卡器应用相关参数
         * </div>
         * <div class="en">
         * Get related parameters of the reader.
         * </div>
         * 
    	 * @return
    	 * <div class="zh">
    	 * 		读卡器参数, 参见{@link com.pax.mposapi.model#CLSS_READER_PARAM}
    	 * </div>
    	 * <div class="en">
    	 * 		reader's relative parameters, see {@link com.pax.mposapi.model#CLSS_READER_PARAM} 
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
        public CLSS_READER_PARAM getReaderParam() throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_GET_READER_PARAM, new byte[0], rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        		CLSS_READER_PARAM readerParam = new CLSS_READER_PARAM();
        		readerParam.serialFromBuffer(respBuffer);
        		return readerParam;
        	} else {
            	throw new EmvException(rc.code);
        	}
        }
        
        /**
         * <div class="zh">
         * 设置读卡器应用相关参数<br/>
         * 该接口须在{@link ClssManager.Pboc#procTrans(byte[])}函数执行前调用
         * </div>
         * <div class="en">
         * Set related parameter of card reader.<br/>
         * This function must be called before Initiate Application Processing(GPO)  to set the reader's relative parameters.
         * </div>
         * 
    	 * @param readerParam
    	 * <div class="zh">
    	 * 		[输入]读卡器参数, 参见{@link com.pax.mposapi.model#CLSS_READER_PARAM}
    	 * </div>
    	 * <div class="en">
    	 * 		[input]reader's relative parameters, see {@link com.pax.mposapi.model#CLSS_READER_PARAM} 
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
        public void setReaderParam(CLSS_READER_PARAM readerParam) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = readerParam.serialToBuffer();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_SET_READER_PARAM, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else {
            	throw new EmvException(rc.code);
        	}
        }        
        
        /**
         * <div class="zh">
         * 设置QPBOC应用相关参数<br/>
         * 该接口须在{@link ClssManager.Pboc#procTrans(byte[])}函数执行前调用
         * </div>
         * <div class="en">
         * Set QPBOC application related parameters corresponding to the AID.<br/>
         * This function must be called before Initiate Application Processing(GPO) to set application's relative parameters.
         * </div>
         * 
    	 * @param aidParam
    	 * <div class="zh">
    	 * 		[输入]qPBOC应用相关参数, 参见{@link com.pax.mposapi.model#CLSS_PBOC_AID_PARAM}
    	 * </div>
    	 * <div class="en">
    	 * 		[input]qPBOC application related paramters, see{@link com.pax.mposapi.model#CLSS_PBOC_AID_PARAM}
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
        public void setAidParam(CLSS_PBOC_AID_PARAM aidParam) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = aidParam.serialToBuffer();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_SET_AID_PARAM, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else {
            	throw new EmvException(rc.code);
        	}
        }
        
        /**
         * <div class="zh">
         * 添加一个新的认证中心密钥<br/>
         * 如果密钥存在, 则用新的密钥覆盖原来的密钥, 认证中心密钥由收单行提供, 收单行提供的密钥不一定符合结构{@link com.pax.mposapi.model#EMV_CAPK}, 应用可能需要转换后才能添加到EMV库
         * SM算法时, capk需满足条件HashInd = 0x07且ArithInd =0x04
         * </div>
         * <div class="en">
         * Add a new CA public key.<br/>
         * 1.If the public key is already existed, the new key will replace the old one.<br/>
		 * 2.CA public key is provided by acquire. Sometimes, the key is not assigned with the structure of {@link com.pax.mposapi.model#EMV_CAPK}. In this case, the application must convert the public key before it can be added to the EMV library.<br/>
		 * 3.when using SM algorithm. HashInd of CAPK must be 0x07 and ArithInd must be 0x04.
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
        public void addCAPK(EMV_CAPK capk) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = capk.serialToBuffer();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_ADD_CAPK, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else {
            	throw new EmvException(rc.code);
        	}
        } 

        /**
         * <div class="zh">
         * 从内核中删除一个认证中心密钥
         * </div>
         * <div class="en">
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
    	 * 		[输入]应用注册服务商ID 
    	 * </div>
    	 * <div class="en">
    	 * 		[input]Registered Application Provider Identifier.
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
        	System.arraycopy(rid, 0, req, 1, 5);
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_DEL_CAPK, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else {
            	throw new EmvException(rc.code);
        	}
        } 
        
        /**
         * <div class="zh">
         * 从内核中读出一个认证中心公钥
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
    	 * 		the key index
    	 * </div>
    	 * 
    	 * @return
    	 * <div class="zh">
    	 * 		null: 指定的keyId不存在<br/>
    	 * 		非null: 认证中心密钥
    	 * 
    	 * </div>
    	 * <div class="en">
    	 * 		null: no valid for specified keyId<br/>
    	 * 		non-null: the specified CA public key
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
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_GET_CAPK, req, rc, respBuffer);
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
         * 删除所有认证中心公钥<br/>
         * 建议应用开发时, 每次交易前先删除内核中的所有公钥, 根据当前应用所需的CA公钥进行设置, 这样每次仅需设置一个公钥
         * </div>
         * <div class="en">
         * Delete all CA public keys.<br/>
         * Application developer is suggested to call this function to delete all CA Public Keys in kernel at the beginning of each transaction, and set CA Public Key according to current application's requirment. In this way, application only need to set one CA Public Key each time.
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
        public void delAllCAPK() throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_DEL_ALL_CAPK, new byte[0], rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else {
            	throw new EmvException(rc.code);
        	}
        } 
        
        /**
         * <div class="zh">
         * 添加一个待回收的发卡行公钥证书数据到证书回收列表<br/>
         * 如果待添加的证书已存在 , 则直接返回成功
         * </div>
         * <div class="en">
         * Add a revoked issuer public key certification to certification revocation list.<br/>
         * If the revoked issuer public key certification already exists, this function directly returns success. 
         * </div>
         * 
         * @param revocList
    	 * <div class="zh">
    	 * 		[输入]发卡行公钥证书数据结构
    	 * </div>
    	 * <div class="en">
    	 * 		[input] the revoked issuer public key certification
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
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_ADD_REVOC_LIST, req, rc, respBuffer);
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
    	 * 		[input]The corresponding RID of the revoked issuer public key certification (5 byte)
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
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_DEL_REVOC_LIST, req, rc, respBuffer);
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
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_DEL_ALL_REVOC_LIST, new byte[0], rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else {
            	throw new EmvException(rc.code);
        	}
        }

        /**
         * <div class="zh">
         * 设置最终选择相关的数据<br/>
         * 1.设置最终选择数据之前需要通过Entry库提供的接口获取最终选择的数据；<br/>
		 * 2.该接口须在{@link ClssManager.Pboc#procTrans(byte[])}函数执行前调用
         * </div>
         * <div class="en">
         * Set the data returned from card during final selection.<br/>
         * 1.get the data of final selection firstly before setting.<br/>
         * 2.must be called before {@link ClssManager.Pboc#procTrans(byte[])}
         * </div>
         * 
         * @param data
    	 * <div class="zh">
    	 * 		[输入]待设置的数据
    	 * </div>
    	 * <div class="en">
    	 * 		[input]the data to set
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
        public void setFinalSelectData(byte[] data) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = new byte[4 + data.length];
        	Utils.int2ByteArray(data.length, req, 0);
        	System.arraycopy(data, 0, req, 4, data.length);
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_SET_FINAL_SELECT_DATA, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else {
            	throw new EmvException(rc.code);
        	}
        }

        /**
         * <div class="zh">
         * 设置交易相关参数以及预处理结果传递<br/>
		 * 1.该接口须在{@link ClssManager.Pboc#procTrans(byte[])}函数执行前调用<br/>
         * 2.若仅支持非接完整PBOC,可以不进行交易预处理,但在设置交易预处理结果时需设置交易预处理结果数据结构{@link com.pax.mposapi.model.CLSS_PRE_PROC_INTER_INFO#aucReaderTTQ}参数的第一个字节,表明终端支持非接完整PBOC
         * </div>
         * <div class="en">
         * Set related parameters of transaction and transfer the result of preliminary transaction processing.<br/>
         * 1.must be called before {@link ClssManager.Pboc#procTrans(byte[])}<br/>
         * 2.if not support qPBOC, application can ignore pre-processing, however, you should clear qPBOC bit of {@link com.pax.mposapi.model.CLSS_PRE_PROC_INTER_INFO#aucReaderTTQ}. 
         * </div>
         * 
         * @param transParam
    	 * <div class="zh">
    	 * 		[输入]交易相关参数, 其中授权金额及交易类型的设置与EMV L2不同, 参见{@link com.pax.mposapi.model.CLSS_TRANS_PARAM}
    	 * </div>
    	 * <div class="en">
    	 * 		[input]the related parameters of transaction, note that the authorized amount and transaction type settings are different from EMV L2, see{@link com.pax.mposapi.model.CLSS_TRANS_PARAM} 
    	 * </div>
    	 *
         * @param preProcInterInfo
    	 * <div class="zh">
    	 * 		[输入]预处理结果, 参见{@link com.pax.mposapi.model.CLSS_TRANS_PARAM}, 该数据需通过Entry库的{@link ClssManager.Entry#getPreProcInterFlg()}来获取.
    	 * </div>
    	 * <div class="en">
    	 * 		[input]the result of preliminary transaction processing, see {@link com.pax.mposapi.model.CLSS_TRANS_PARAM}, it's archieved with {@link ClssManager.Entry#getPreProcInterFlg()}.
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
        public void setTransData(CLSS_TRANS_PARAM transParam, CLSS_PRE_PROC_INTER_INFO preProcInterInfo) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] tp = transParam.serialToBuffer();
        	byte[] ppii = preProcInterInfo.serialToBuffer();
        	byte[] req = new byte[tp.length + ppii.length];
        	System.arraycopy(tp, 0, req, 0, tp.length);
        	System.arraycopy(ppii, 0, req, tp.length, ppii.length);
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_SET_TRANS_DATA, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else {
            	throw new EmvException(rc.code);
        	}
        }

        /**
         * <div class="zh">
         * PBOC非接触交易处理（包括初始化应用及读记录等处理<br/>
         * 1.该函数执行成功后, 若为TC, 则还需根据具体的交易路径类型判断是否还需进行脱机数据认证/异常文件检查处理;若为AAC或ARQC, 则直接进行脱机拒绝或联机授权处理;<br/>
		 * 2.若返回值为{@link EmvManager#CLSS_RESELECT_APP}, 则需通过Entry库接口{@link ClssManager.Entry#delCurCandApp()}函数删除当前候选应用, 并重新进行最终选择操作;<br/>
		 * 3.如果终端支持SM算法, 必须在调用本函数之前,先调用{@link ClssManager.Pboc#setTLVData(int, byte[])}设置'DF69'的值为1
         * </div>
         * <div class="en">
         * This function includes the processing of GPO and record reading of MSD, qVSDC or  Wave2.<br/>
         * 1.When this function is performed successfully, if AC type is TC,application need to  judge whether to do offline authentication or exception file checking according to the transaction path. While if the AC type is AAC or ARQC, offline rejection or online authorization shall be performed immediately.<br/>
         * 2.if returns {@link EmvManager#CLSS_RESELECT_APP}, use {@link ClssManager.Entry#delCurCandApp()} to delete currently applcation and re-select applcation.<br/>
         * 3.if terminal supports SM algorithm, must call {@link ClssManager.Pboc#setTLVData(int, byte[])} to set 'DF69' to 1 before calling this function.
         * </div>
         * 
         * @param result
         * <div class="zh">
         *		[输出]<br/>
         *		result[0]: 交易路径类型 {@link #CLSS_VISA_QVSDC}: qPBOC 方式  , {@link #CLSS_VISA_VSDC}: 完整非接PBOC方式<br/>
         *		result[1]: AC类型:{@link EmvManager#EMV_AC_AAC}, {@link EmvManager#EMV_AC_TC}, {@link EmvManager#EMV_AC_ARQC}
         * </div>
         * <div class="en">
         * 		[output]<br/>
         * 		result[0]: transaction path  {@link #CLSS_VISA_QVSDC}: qPBOC,  {@link #CLSS_VISA_VSDC}: PBOC<br/>
         * 		result[1]: AC type: {@link EmvManager#EMV_AC_AAC}, {@link EmvManager#EMV_AC_TC}, {@link EmvManager#EMV_AC_ARQC}
         * </div>
         * 
         * @return
         * <div class="zh">
         *		<ul>
         *			<li>{@link EmvManager#EMV_OK}: 成功
         *			<li>{@link EmvManager#CLSS_PARAM_ERR}: 参数错误
         *			<li>{@link EmvManager#CLSS_RESELECT_APP}: 重新执行最终选择. 重新选择应用
         *			<li>{@link EmvManager#CLSS_USE_CONTACT}: 终止非接触交易, 使用非接触界面方式执行交易
         *			<li>{@link EmvManager#CLSS_CARD_EXPIRED}: 卡片已失效
         *			<li>其他: 终止交易,重新询卡
         *		</ul>  
         * </div>
         * <div class="en">
         *		<ul>
         *			<li>{@link EmvManager#EMV_OK}: succeed
         *			<li>{@link EmvManager#CLSS_PARAM_ERR}: parameter error
         *			<li>{@link EmvManager#CLSS_RESELECT_APP}: re-select applcation
         *			<li>{@link EmvManager#CLSS_USE_CONTACT}: Terminate CLSS transaction and use contact interface to process transaction instead.
         *			<li>{@link EmvManager#CLSS_CARD_EXPIRED}: card expired
         *			<li>others: Terminate transaction and redetect card.
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
        //result[0] - transpath, result[1] - ac type
        public int procTrans(byte[] result) throws IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_PROC_TRANS, new byte[0], rc, respBuffer);
    		System.arraycopy(respBuffer, 0, result, 0, 2);
    		return rc.code;
        }

        /**
         * <div class="zh">
         * 脱机数据认证, 并根据认证状态返回最终交易须采用的AC类型<br/>
         * 该函数执行成功后, 应用程序须根据返回的交易AC类型, 进行后续处理（脱机批准, 脱机拒绝, 联机授权）
         * </div>
         * <div class="en">
         * Do offline data authentication and return AC type.<br/>
         * After this function is performed successfully, application shall continue with subsequenced process according to the AC returned in this function parameter.(such as offline approve, offline decline,or online authorization)
         * </div>
         * 
         * @param result
         * <div class="zh">
         *		[输出]<br/>
         *		result[0]: AC类型:{@link EmvManager#EMV_AC_AAC}, {@link EmvManager#EMV_AC_TC}, {@link EmvManager#EMV_AC_ARQC}
         *		result[1]: DDA认证失败标志:1-失败
         * </div>
         * <div class="en">
         * 		[output]<br/>
         *		result[1]: AC type:{@link EmvManager#EMV_AC_AAC}, {@link EmvManager#EMV_AC_TC}, {@link EmvManager#EMV_AC_ARQC}
         *		result[1]: DDA failed falg: 1-failed 
         * </div>
         * 
         * @return
         * <div class="zh">
         *		<ul>
         *			<li>{@link EmvManager#EMV_OK}: 成功
         *			<li>{@link EmvManager#CLSS_USE_CONTACT}: 终止非接触交易, 使用非接触界面方式执行交易
         *			<li>其他: 终止交易,重新询卡
         *		</ul>  
         * </div>
         * <div class="en">
         *		<ul>
         *			<li>{@link EmvManager#EMV_OK}: succeed
         *			<li>{@link EmvManager#CLSS_USE_CONTACT}: Terminate CLSS transaction and use contact interface to process transaction instead.
         *			<li>others: Terminate transaction and redetect card.
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
        //result[0] - ac type, result[1] - dda fail flg
        public int cardAuth(byte[] result) throws IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_CARD_AUTH, new byte[0], rc, respBuffer);
        	System.arraycopy(respBuffer, 0, result, 0, 2);
        	return rc.code;
        }

        /**
         * <div class="zh">
         * 获取持卡人CVM认证方式(签名或联机PIN验证)
         * </div>
         * <div class="en">
         * Get CVM Type (signature or online PIN verification)
         * </div>
         * 
         * @return
         * <div class="zh">
         *		持卡人认证方式<br/>
				{@link #RD_CVM_NO}:  无持卡人认证<br/>
				{@link #RD_CVM_ONLINE_PIN}:  联机PIN<br/>
				{@link #RD_CVM_SIG}:  签名<br/>
         * </div>
         * <div class="en">
         *		CVM type<br/>
				{@link #RD_CVM_NO}:  NO CVM <br/>
				{@link #RD_CVM_ONLINE_PIN}: online PIN<br/>
				{@link #RD_CVM_SIG}:  signature<br/>
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
        public int getCvmType() throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_GET_CVM_TYPE, new byte[0], rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        		return respBuffer[0];
        	} else {
            	throw new EmvException(rc.code);
        	}
        }

        /**
         * <div class="zh">
         * <b><font color=red>注意: D180 不支持此功能,参考"MPosApi_Supplementary.docx"获取更多相关信息 </font></b><br/>
         * 用于MSD 及 qPBOC路径下获取映射填充后的TRACK1数据(ASCII码)<br/>
         * 应用也可直接使用Track 1等价数据根据特定的要求及规则进行填充
         * </div>
         * <div class="en">
         * <b><font color=red>NOTE: D180 doesn't support this function,see "MPosApi_Supplementary.docx" for more </font></b><br/>
         * get mapped track1 data(ASCII) under MSD and qPBOC transaction path<br/>
         * application can use track1 equivalent data to gnereate the data
         * </div>
         * 
         * @return
         * <div class="zh">
         *		映射填充后的TRACK1数据
         * </div>
         * <div class="en">
         * 		track1 data
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
        public String getTrack1MapData() throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_GET_TRACK1_MAP_DATA, new byte[0], rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        		return new String(respBuffer, 1, respBuffer[0]);
        	} else {
            	throw new EmvException(rc.code);
        	}
        }

        /**
         * <div class="zh">
         * <b><font color=red>注意: D180 不支持此功能,参考"MPosApi_Supplementary.docx"获取更多相关信息 </font></b><br/>
         * 用于MSD路径下获取映射填充后的TRACK2数据(ASCII码)<br/>
         * 应用也可直接使用Track 2等价数据根据特定的要求及规则进行填充
         * </div>
         * <div class="en">
         * <b><font color=red>NOTE: D180 doesn't support this function,see "MPosApi_Supplementary.docx" for more </font></b><br/>
         * get mapped track2 data(ASCII) under MSD and qPBOC transaction path<br/>
         * application can use track2 equivalent data to gnereate the data
         * </div>
         * 
         * @return
         * <div class="zh">
         *		映射填充后的TRACK2数据
         * </div>
         * <div class="en">
         * 		track2 data
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
        public String getTrack2MapData() throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_GET_TRACK2_MAP_DATA, new byte[0], rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        		return new String(respBuffer, 1, respBuffer[0]);
        	} else {
            	throw new EmvException(rc.code);
        	}
        }

        /**
         * <div class="zh">
         * 用于获取GPO返回数据<br/>
         * 在交易处理函数{@link ClssManager.Pboc#procTrans(byte[])}返回的交易路径为 {@link #CLSS_VISA_VSDC}时, 需调用该接口来获取GPO返回数据, 并通过完整非接PBOC库中的接口来设置到完整非接库中
         * </div>
         * <div class="en">
         * get the retuned data of GPO<br/>
         * if {@link ClssManager.Pboc#procTrans(byte[])} returns {@link #CLSS_VISA_VSDC}, you should call this function to get returned data of GPO, and then set the data with PBOC interface.
         * </div>
         * 
         * @return
         * <div class="zh">
         *		非null: GPO返回数据
         *		null:  无数据
         * </div>
         * <div class="en">
         * 		non-null: returned data of GPO
         * 		null: no data
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
        public byte[] getGPOData() throws IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();

        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_GET_GPO_DATA, new byte[0], rc, respBuffer);
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
         * <b><font color=red>注意: D180 不支持此功能,参考"MPosApi_Supplementary.docx"获取更多相关信息 </font></b><br/>
         * 重发最后一条读记录命令, 继续交易
         * </div>
         * <div class="en">
         * <b><font color=red>NOTE: D180 doesn't support this function,see "MPosApi_Supplementary.docx" for more </font></b><br/>
         * re-send last read record command and continue transaction
         * </div>
         * 
         * @return
         * <div class="zh">
         *		byte[0]: 交易路径类型 {@link #CLSS_VISA_QVSDC}: qPBOC 方式  , {@link #CLSS_VISA_VSDC}: 完整非接PBOC方式<br/>
         *		byte[1]: AC类型:{@link EmvManager#EMV_AC_AAC}, {@link EmvManager#EMV_AC_TC}, {@link EmvManager#EMV_AC_ARQC}
         * </div>
         * <div class="en">
         * 		byte[0]: transaction path {@link #CLSS_VISA_QVSDC}: qPBOC, {@link #CLSS_VISA_VSDC}: PBOC<br/>
         *		byte[1]: AC type:{@link EmvManager#EMV_AC_AAC}, {@link EmvManager#EMV_AC_TC}, {@link EmvManager#EMV_AC_ARQC}
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
        //byte[0] - transpath, byte[1] - ac type
        public byte[] reSendLastCmd() throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_RESEND_LAST_CMD, new byte[0], rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        		byte[] result = new byte[2];
        		System.arraycopy(respBuffer, 0, result, 0, 2);
        		return result;
        	} else {
            	throw new EmvException(rc.code);
        	}
        }

        /**
         * <div class="zh">
         * <b><font color=red>注意: D180 不支持此功能,参考"MPosApi_Supplementary.docx"获取更多相关信息 </font></b><br/>
         * 发送Get Data命令获取卡片数据<br/>
         * 应用可以在最终选择后调用该接口获取卡片数据.（并不是所有数据都能通过该命令来获取）
         * </div>
         * <div class="en">
         * <b><font color=red>NOTE: D180 doesn't support this function,see "MPosApi_Supplementary.docx" for more </font></b><br/>
         * send Get Data command to get data from card<br/>
         * application can call this function after finall selection.
         * </div>
         * 
         * @param tag
    	 * <div class="zh">
    	 * 		需获取卡片数据TAG:目前只支持以9F开头的TAG
    	 * </div>
    	 * <div class="en">
    	 * 		the tag to get value, currently only support tag start with 9F.
    	 * </div>
    	 *
         * @return
         * <div class="zh">
         *		从卡片中得到的对应TAG数据值
         * </div>
         * <div class="en">
         * 		the value get from the card for the specified tag 
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
        public byte[] getDataCmd(int tag) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = new byte[2];
        	Utils.short2ByteArray((short)tag, req, 0);

        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_GET_DATA_CMD, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        		int len = Utils.intFromByteArray(respBuffer, 0);
        		byte[] dataOut = new byte[len];
        		System.arraycopy(respBuffer, 4, dataOut, 0, len);
        		return dataOut;
        	} else {
        		throw new EmvException(rc.code);
        	}
        }
        
        /**
         * <div class="zh">
         * 对被锁应用进行初始化<br/>
         * 如果选择的路径是非接PBOC, 则返回成功, 选择其他路径视为错误
         * </div>
         * <div class="en">
         * initialize the blocked application<br/>
         * if the transaction path is PBOC, then it's OK, other paths are treated as error. 
         * </div>
         * 
         * @return
         * <div class="zh">
         *		交易路径类型:{@link #CLSS_VISA_QVSDC}: qPBOC 方式  , {@link #CLSS_VISA_VSDC}: 完整非接PBOC方式
         * </div>
         * <div class="en">
         * 		transaction path:{@link #CLSS_VISA_QVSDC}: qPBOC, {@link #CLSS_VISA_VSDC}: PBOC
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
        public int procTransUnlockApp() throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();

        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_PROC_TRANS_UNLOCK_APP, new byte[0], rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        		return respBuffer[0];
        	} else {
        		throw new EmvException(rc.code);
        	}
        }
        
    }
    
    /**
     * This class integrates multiple kernels (currently paypass and paywave)
     */
    public class Integrate {
    	
    	// NOTE: in ms
    	private int cardPollingTimeout; 
    	
    	/**
    	 * Download CAPK
    	 * 
    	 * @param capk
    	 * 		[input] CA public key
    	 * 
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */
    	public void downloadCAPK(EMV_CAPK capk) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = capk.serialToBuffer();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_DOWNLOAD_CAPK, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    	
    	/**
    	 * Download application
    	 * 
    	 * @param selFlag
    	 * 		Application selection flag (partial matching{@link com.pax.mposapi.EmvManager#EMV_APP_SEL_PARTIAL_MATCH} or full matching{@link com.pax.mposapi.EmvManager#EMV_APP_SEL_FULL_MATCH})
    	 * @param preProcInfo
    	 * 		[input] Parameters used in transaction preprocessing corresponding to an AID
    	 * 
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */    	
    	public void downloadApp(int selFlag, CLSS_PRE_PROC_INFO preProcInfo) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] ppi = preProcInfo.serialToBuffer();
        	byte[] req = new byte[1 + ppi.length];
        	req[0] = (byte)selFlag;
        	System.arraycopy(ppi, 0, req, 1, ppi.length);
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_DOWNLOAD_APP, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    	
    	/**
    	 * Download reader param for PayWave
    	 * 
    	 * @param readerParam
    	 * 		[input] reader parameters
    	 * 
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */      	
    	public void downloadVisaReaderParam(CLSS_READER_PARAM readerParam) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = readerParam.serialToBuffer();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_DOWNLOAD_VISA_READER_PARAM, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    	
    	/**
    	 * Download reader param for PayPass
    	 * 
    	 * @param readerParamMC
    	 * 		[input] reader parameters
    	 * 
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */    	
    	public void downloadMCReaderParam(CLSS_READER_PARAM_MC readerParamMC) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = readerParamMC.serialToBuffer();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_DOWNLOAD_MC_READER_PARAM, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    	    	
    	/**
    	 * Download AID param for Maste PayPass
    	 * 
    	 * @param mcAidParam
    	 * 		[input] reader parameters
    	 * 
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */    	    	
    	public void downloadMCAidParam(CLSS_MC_AID_PARAM_MC mcAidParam) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = mcAidParam.serialToBuffer();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_DOWNLOAD_MC_AID_PARAM, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}

    	/**
    	 * Download AID param for Visa PayWave
    	 * 
    	 * @param visaAidParam
    	 * 		[input] reader parameters
    	 * 
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */    	    	
    	public void downloadVisaAidParam(CLSS_VISA_AID_PARAM visaAidParam) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = visaAidParam.serialToBuffer();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_DOWNLOAD_VISA_AID_PARAM, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    	
    	/**
    	 * Download terminal configuration for PayPass
    	 * 
    	 * @param termConfigMC
    	 * 		[input] terminal configuration
    	 * 
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */       	
    	public void downloadMCTermConfig(CLSS_TERM_CONFIG_MC termConfigMC) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = termConfigMC.serialToBuffer();
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_DOWNLOAD_MC_TERM_CONFIG, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    	
    	/**
    	 * Delete all CAPK
    	 *  
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */      	
    	public void deleteAllCAPK() throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = new byte[0];
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_DELETE_ALL_CAPK, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    	
    	/**
    	 * Delete one CAPK specified by keyId and rid
    	 *  
    	 * @param keyId
    	 * 		The index of the key.
    	 * 
    	 * @param rid
    	 * 		[input] Registered Application Provider Identifier.
    	 *  
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */      	
    	public void deleteOneCAPK(int keyId, byte[] rid) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = new byte[1 + 5];
        	req[0] = (byte)keyId;
        	System.arraycopy(rid, 0, req, 1, 5);
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_DELETE_ONE_CAPK, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    	
    	/**
    	 * Delete all AID
    	 *  
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */      	
    	public void deleteAllAID() throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = new byte[0];
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_DELETE_ALL_AID, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    	
    	/**
    	 * Delete one specified AID
    	 *  
    	 * @param aid
    	 * 		[input] AID to delete
    	 *  
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */      	    	
    	public void deleteOneAID(byte[] aid) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = new byte[1 + aid.length];
        	req[0] = (byte)aid.length;
        	System.arraycopy(aid, 0, req, 1, aid.length);
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_DELETE_ONE_AID, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    	
    	/**
    	 * Init transaction <br/>
    	 * After successfully init the transaction, the terminal should polling the card(including Magnetic Swipe Card, IC Card and Contactless Card),
    	 * The polling timeout is specified by {@link POSLOG#uiTimeOut}.
    	 * 
    	 * @param poslog
    	 * 		[input] Transaction parameters, such as amount, date, merchantID and so on
    	 *  
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */    	
    	public void transInit(POSLOG poslog) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();

        	// the wait card timeout default to 500(unit 100ms, i.e. 50s) for 0
        	if (poslog.uiTimeOut == 0) {
        		poslog.uiTimeOut = 500;
        	}

        	byte[] req = poslog.serialToBuffer();

        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_TRANS_INIT, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        		cardPollingTimeout = poslog.uiTimeOut * 100;
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    	
    	/**
    	 * Cancel card polling <br/>
    	 * This method should be called right after {@link #transInit(POSLOG)} if you want to cancel card polling.
    	 * 
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */        	
    	public void transCancel() throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = new byte[0];
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_TRANS_CANCEL, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    	
    	/**
    	 * Get card polling status<br/>
    	 * If Contactless Card detected, please further call other methods in this class; <br/>
    	 * If Magnetic Swipe Card detected, please call methods in {@link MagManager}; <br/>
    	 * if IC Card detected, please call methods in #EmvManager or {@link IccManager};<br/>
    	 * 
    	 * @return
    	 * 		0 - Magnetic Swipe Card detected<br/>
    	 * 		1 - IC Card detected<br/>
    	 * 		2 - Contactless Card detected<br/>
    	 * 
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */        	
    	public int waitCardStatus() throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = new byte[0];
        	
        	int savedRecvTimeout = cfg.receiveTimeout;
        	
        	try {
        		cfg.receiveTimeout += cardPollingTimeout;
	        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_WAIT_CARD_STATUS, req, rc, respBuffer);
	        	if (rc.code == 0) {
	        		//success
	        		return respBuffer[0];		// card type
	        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
	        		throw new CommonException(rc.code);
	        	} else {
	            	throw new EmvException(rc.code);
	        	}
        	} finally {
        		cfg.receiveTimeout = savedRecvTimeout;
        		MyLog.i(TAG, "restore receive timeout to: " + savedRecvTimeout);        		
        	}
    	}
    	
    	/**
    	 * Transaction processing<br/>
    	 * Before calling this method, you should call {@link ClssManager.Integrate#waitCardStatus} to judge whether the card is tapped or not
    	 * 
    	 * @return
    	 * 		transaction result
    	 * 
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */      	
    	public SYS_PROC_INFO transStart() throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = new byte[0];
        	
        	SYS_PROC_INFO spi = new SYS_PROC_INFO();
        	byte[] spiBytes = spi.serialToBuffer();
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_TRANS_START, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        		System.arraycopy(respBuffer, 0, spiBytes, 0, spiBytes.length);
        		spi.serialFromBuffer(spiBytes);
        		return spi;
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    	
    	/**
    	 * Transaction Process to finish PayWave transaction<br/>
    	 * Only when issuer authentication data was received,and card type is visa paywave.
    	 * Calling this function, and ask card holder tap card again to finish online authorization  
    	 * 
    	 * @param script
    	 * 		[input] script data
    	 * 
    	 * @return
    	 * 		transaction result
    	 * 
    	 * @throws EmvException
         * 		EMV error
         * @throws IOException
         * 		communication error
         * @throws ProtoException
         * 		protocol error
	     * @throws CommonException
	     * 		common error 
    	 */      	
    	public SYS_PROC_INFO transWaveFinish(byte[] script) throws EmvException, IOException, ProtoException, CommonException {
        	RespCode rc = new RespCode();
        	byte[] req = new byte[2 + script.length];
        	Utils.short2ByteArray((short)script.length, req, 0);
        	System.arraycopy(script, 0, req, 2, script.length);
        	
        	SYS_PROC_INFO spi = new SYS_PROC_INFO();
        	byte[] spiBytes = spi.serialToBuffer();
        	proto.sendRecv(Cmd.CmdType.CLSS_INTEGRATE_TRASN_WAVE_FINISH, req, rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        		System.arraycopy(respBuffer, 0, spiBytes, 0, spiBytes.length);
        		spi.serialFromBuffer(spiBytes);
        		return spi;
        	} else if (CommonException.isCommonExceptionCode(rc.code)) {
        		throw new CommonException(rc.code);
        	} else {
            	throw new EmvException(rc.code);
        	}
    	}
    }
    
}
