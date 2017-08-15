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
 * ClssManager ���ڴ���ǽӽ���
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
	// �� amount > contactless cvm limit ʱ����Ҫִ�к���CVM��ʽ��
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
     * ���ڷ���Entry�ӿڵĶ���
     * </div>
     * <div class="en">
     * an object used to access the Entry interfaces
     * </div>
     */
    public Entry entry;
    /**
     * <div class="zh">
     * ���ڷ���Pboc�ӿڵĶ���
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
     * ʹ��ָ����Context�����ClssManager����
     * </div>
     * <div class="en">
     * Create a ClssManager instance with a given Context
     * </div>
     * 
     * @param context 
     * <div class="zh">Ӧ�õ�ǰ��context</div>
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
     * Entry ʵ�� entry point����
     * </div>
     * <div class="en">
     * Entry implements entry point functions
     * </div>
     *
     */
    public class Entry {
        /**
         * <div class="zh">
         * ��ѯEntry�ں˰汾
         * </div>
         * <div class="en">
         * Query the version of entry kernel.
         * </div>
         * 
    	 * @return
    	 * <div class="zh">
    	 * 		Entry�ں˰汾
    	 * </div>
    	 * <div class="en">
    	 * 		Entry's version number and issue date
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
         * ���һ��AID���ݵ��ں�
         * </div>
         * <div class="en">
         * Add an application to application list.
         * </div>
         * 
         * @param aid
    	 * <div class="zh">
    	 * 		[����]AID
    	 * </div>
    	 * <div class="en">
    	 * 		[input]AID name.
    	 * </div>
    	 * 
         * @param selFlg
    	 * <div class="zh">
    	 * 		AIDƥ��ѡ��<br/>
    	 * 		{@link EmvManager#EMV_APP_SEL_PARTIAL_MATCH}: ����ƥ�� <br/>
    	 * 		{@link EmvManager#EMV_APP_SEL_FULL_MATCH}: ��ȫƥ�� 
    	 * </div>
    	 * <div class="en">
    	 * 		AID matching flag<br/>
    	 * 		{@link EmvManager#EMV_APP_SEL_PARTIAL_MATCH}: partial matching<br/>
    	 * 		{@link EmvManager#EMV_APP_SEL_FULL_MATCH}: full matching
    	 * </div>
    	 * 
         * @param kernType
    	 * <div class="zh">
    	 * 		Ӧ���ں�����<br/>
    	 * 		<ul>
    	 * 			<li>{@link #CLSS_KERNTYPE_DEF}: ����RID. �ں˽���ƥ��ȷ��
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
         * ���ں���ɾ��һ��AID����
         * </div>
         * <div class="en">
         * Delete an application from the application list
         * </div>
         * 
         * @param aid
    	 * <div class="zh">
    	 * 		[����]AID
    	 * </div>
    	 * <div class="en">
    	 * 		[input]AID
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
         * ���ں���ɾ������AID����
         * </div>
         * <div class="en">
         * Delete all applications in the application list
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
         * ����һ��AID��Ӧ�Ľ���Ԥ������ʹ�õĲ��� <br/>
         * 1.���ڽ���Ԥ����֮ǰ���øú���;<br/>
         * 2.���Ѵ��ڸ�AID��Ӧ�Ĳ���,��ԭ��������;<br/> 
         * 3.���ý���Ԥ�������ǰ��Ҫ��������Ӧ��AIDӦ��,����ú������׳�EMV_ERR_NOT_FOUND�쳣 
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
         * 		[����]AID��Ӧ�Ľ���Ԥ������ʹ�õĲ���
         * </div>
         * <div class="en">
         * 		[input]Parameters used in transaction preprocessing corresponding to an AID
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
         * ɾ��һ����ָ��AID��Ӧ�Ľ���Ԥ������ʹ�õĲ���<br/>
         * ���ڽ���Ԥ����֮ǰ���øú���, ���øýӿڲ���ɾ�����Ӧ��Ӧ��
         * </div>
         * <div class="en">
         * Delete AID's correspongding parameters used in preliminary transaction processing.<br/>
         * This function must be called before preliminary transaction processing. 
         * </div>
    	 * 
         * @param aid
    	 * <div class="zh">
    	 * 		[����]AID
    	 * </div>
    	 * <div class="en">
    	 * 		[input]AID
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
         * ɾ�����н���Ԥ������ʹ�õĲ���<br/>
         * ���ڽ���Ԥ����֮ǰ���øú���,���øýӿڲ���ɾ������Ӧ��
         * </div>
         * <div class="en">
         * Delete all parameters used in preliminary transaction processing.<br/>
         * This function must be called before preliminary transaction processing.
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
         * ����Ԥ����<br/>
         * ������ѯ�ǽӴ���Ƭ֮ǰ���øú���
         * </div>
         * <div class="en">
         * Transaction preprocessing.<br/>
         * This function must be called before detecting card.
         * </div>
    	 * 
         * @param transParam
    	 * <div class="zh">
    	 * 		[����]������ز����ṹ
    	 * </div>
    	 * <div class="en">
    	 * 		[input]related parameters of transaction.
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
         * Ӧ��ѡ��,������ѡ�б�<br/>
         * Ҫ���ڵ��øú���ǰ,Ӧ��Ӧ��ͨ�� {@link PiccManager#piccDetect(byte)} �����жϳ��Ѿ���Type A��Type B��
         * </div>
         * <div class="en">
         * Application selection.<br/>
         * Before calling this function, {@link PiccManager#piccDetect(byte)} must be called to judge whether a type A or type B card is detected or not. 
         * </div>
    	 * 
         * @param slot
    	 * <div class="zh">
    	 * 		�ò�����δʹ��
    	 * </div>
    	 * <div class="en">
    	 * 		currently not used
    	 * </div>
    	 * 
         * @param readLogFlag
    	 * <div class="zh">
    	 * 		�ò�����δʹ��
    	 * </div>
    	 * <div class="en">
    	 * 		currently not used
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
         * ����Ӧ��ѡ��, ���ں��Զ�ѡ��������ȼ�Ӧ��<br/>
         * �ӿڷ���{@link EmvManager#EMV_RSP_ERR}�� {@link EmvManager#EMV_APP_BLOCK}��{@link EmvManager#ICC_BLOCK}��{@link EmvManager#CLSS_RESELECT_APP}ʱ, 
         * Ӧ�ò�Ӧ����{@link #delCurCandApp()}ɾ����ǰӦ��, ����ѡ�б��л�������Ӧ��, ��������{@link ClssManager.Entry#finalSelect(byte[])}ѡ����һ��Ӧ��
         * </div>
         * <div class="en">
         * Final selection, application with the highest priority will be selected automatically by kernel.<br/>
         * if {@link EmvManager#EMV_RSP_ERR},  {@link EmvManager#EMV_APP_BLOCK}, {@link EmvManager#ICC_BLOCK} or {@link EmvManager#CLSS_RESELECT_APP} is returned, 
         * application should call {@link #delCurCandApp()} to delete current application. If there're oehter applications in the candidate list, call this function again to re-select application.
         * </div>
    	 * 
         * @param data
    	 * <div class="zh">
    	 * 		[���]��ʽΪkernel type(1�ֽ�) + AID(1�ֽڳ��� + ����) <br/>
    	 * 		kernel typeΪ:<br/>
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
	     *		<li>{@link EmvManager#EMV_OK}: �ɹ�
	     *		<li>{@link EmvManager#ICC_CMD_ERR}: IC������ʧ��
	     *		<li>{@link EmvManager#EMV_RSP_ERR}: IC��������Ӧ�����
	     *		<li>{@link EmvManager#EMV_NO_APP}: û���ն�֧�ֵ�EMVӦ��
	     *		<li>{@link EmvManager#EMV_APP_BLOCK}: Ӧ������
	     *		<li>{@link EmvManager#ICC_BLOCK}: IC������
	     *		<li>{@link EmvManager#EMV_DATA_ERR}: ���ݸ�ʽ��, ������Ҫ. �ɵ��ýӿ�{@link #getErrorCode()}��ȡ����Ĵ�������Ϣ
	     *		<li>{@link EmvManager#CLSS_RESELECT_APP}: ���½�������Ӧ��ѡ�񣨽����paypssӦ�ã�
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
         * <div class="zh">ͨ�Ŵ���</div>
         * <div class="en">communication error</div>
         * @throws ProtoException
         * <div class="zh">Э�����</div>
         * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
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
         * ɾ����ѡ�б��еĵ�ǰӦ��<br/>
         * ������GPO����� 6985, ����ͨ���ýӿں���ɾ����ǰ��ѡӦ��, �����½�������ѡ�����
         * </div>
         * <div class="en">
         * Delete the current application from the candidate list<br/>
         * If the Get Processing Options command return status 6985. You need to use this function to delete the first application in the candidate list. Then you can redo the final select.
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
         * ��ȡ������ѡ��Ӧ��AID��Ӧ���ڽ���Ԥ��������ж�̬���õ��ڲ�����<br/>
         * 1.��������ѡ��֮��,���׼�������ǰ���øú���;<br/>
         * 2.���ú�,���ݵ�ǰ��������,��������{@link Pboc#setTransData}�ӿڽ������� 
         * </div>
         * <div class="en">
         * Get the finally selected AID's corresponding parameters, which are dynamically set during preliminary transaction processing.<br/>
         * 1.This function must be called between final selection and transaction processing;<br/>
		 * 2.After calling this function, further call {@link Pboc#setTransData} to set the parameter.  
         * </div>
    	 * 
    	 * @return
         * <div class="zh">
         * 	�ں��н���Ԥ����ʱ���õ��ڲ�����, �μ�{@link com.pax.mposapi.model.CLSS_PRE_PROC_INTER_INFO}
         * </div>
         * <div class="en">
         * 	the finally selected AID's corresponding parameters, see {@link com.pax.mposapi.model.CLSS_PRE_PROC_INTER_INFO}
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
         * ��ȡ������ѡ��Ӧ��AID��Ӧ���ڽ���Ԥ��������ж�̬���õ��ڲ�����<br/>
         * 1.��������ѡ��֮��, ���׼�������ǰ���øú���;<br/>
         * 2.���ú�,���ݵ�ǰ��������,��������{@link Pboc#setFinalSelectData}�ӿڽ������� 
         * </div>
         * <div class="en">
         * Get related data returned from card in final selection<br/>
         * 1.This function must be called between final selection and Get Processing Options .<br/>
		 * 2.After calling this function, further call {@link Pboc#setFinalSelectData} to set the parameter. 
         * </div>
    	 * 
    	 * @return
         * <div class="zh">
         * 	����ѡ��ʱ������������
         * </div>
         * <div class="en">
         *  Related data saved during final selection.
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
         * ѡ����Ӧ��<br/>
         * 1.��������ѡ��֮��, ���׼�������ǰ���øú���;<br/>
         * 2.���ú�, ���ݵ�ǰ��������, ��������  {@link Pboc#setFinalSelectData} �ӿڽ�������
         * </div>
         * <div class="en">
         * select blocked application<br/>
         * 1.This function must be called between final selection and Get Processing Options .<br/>
		 * 2.After calling this function, further call {@link Pboc#setFinalSelectData} to set the parameter. 
         * </div>
    	 * 
    	 * @param transParam
         * <div class="zh">
         * 		[����]������ز���
         * </div>
         * <div class="en">
         * 		[input]related parameters of transaction.
         * </div>
         * 
    	 * @param termAid
         * <div class="zh">
         * 		[����]����AID��ز���
         * </div>
         * <div class="en">
         * 		[input]AID related paramters of the blocked application 
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
         * ��ȡ����Ĵ�������Ϣ<br/>
         * Ŀǰ�˺���������{@link ClssManager.Entry#appSlt(int, int)} �׳�{@link EmvException#EMV_ERR_NO_APP_PPSE}�� 
         * {@link ClssManager.Entry#finalSelect(byte[])}����{@link EmvException#EMV_ERR_DATA}ʱʹ��.<br/>
         * </div>
         * <div class="en">
         * get detailed error code<br/>
         * currently it's only used when {@link ClssManager.Entry#appSlt(int, int)} throwing {@link EmvException#EMV_ERR_NO_APP_PPSE} or 
         * when {@link ClssManager.Entry#finalSelect(byte[])}returning {@link EmvException#EMV_ERR_DATA}.
         * </div>
    	 * 
    	 * @return
         * <div class="zh">
         * 		������<br/>
         * 		1.��{@link ClssManager.Entry#appSlt(int, int)} . errorcode ��ֵ����Ϊ{@link EmvManager#EMV_DATA_ERR}. {@link EmvManager#EMV_RSP_ERR}, {@link EmvManager#EMV_APP_BLOCK}.<br/>
         * 		2.��{@link ClssManager.Entry#finalSelect(byte[])}. errorcode ��ֵ����Ϊ{@link EmvManager#EMV_DATA_ERR}. {@link EmvManager#EMV_NO_DATA}.  
         * </div>
         * <div class="en">
         * 		error code<br/>
         * 		1.for {@link ClssManager.Entry#appSlt(int, int)}, errorcode can be {@link EmvManager#EMV_DATA_ERR}, {@link EmvManager#EMV_RSP_ERR}, {@link EmvManager#EMV_APP_BLOCK}.<br/>
         *		2.for {@link ClssManager.Entry#finalSelect(byte[])}, errorcode can be{@link EmvManager#EMV_DATA_ERR}, {@link EmvManager#EMV_NO_DATA}.
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
         * ����Entry��Ӧ���ϵ�PayPass�淶�汾<br/>
         * 1.�˺�������Entry������PayPassӦ��ʱ����;<br/>
         * 2.�˺���������{@link ClssManager.Entry#appSlt(int, int)}֮ǰ���� 
         * </div>
         * <div class="en">
         * set the specification version that Entry conforms<br/>
         * 1.this is only used for PayPass application.<br/>
         * 2.must be called before {@link ClssManager.Entry#appSlt(int, int)}.
         * </div>
    	 * 
    	 * @param ver
         * <div class="zh">
         * 		0x03: ֧��paypass v3.0
         * 		0x02: ֧��paypass v2.1��Ĭ��ֵ��
         * </div>
         * <div class="en">
         * 		0x03: support paypass v3.0
         * 		0x02: support paypass v2.1(default)
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
     * Pboc ʵ��  qPBOC ����
     * </div>
     * <div class="en">
     * Pboc implements qPBOC functions 
     * </div>
     */
    public class Pboc {
        /**
         * <div class="zh">
         * ��ѯqPBOC�ں˰汾
         * </div>
         * <div class="en">
         * Query the version number of PayWave kernel.
         * </div>
         * 
    	 * @return
    	 * <div class="zh">
    	 * 		qPBOC�ں˰汾
    	 * </div>
    	 * <div class="en">
    	 * 		version of qPBOC kernel
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
         * ��ȡָ����ǩ������ֵ<br/>
         * CLSS������ܴ洢���е�EMV����ı�׼���ݱ�ǩ������, ���ܶ���洢���64���������Զ���ı�ǩ������ֵ, Ӧ�ó����ڶ�Ӧ�����ݺ�ɵ��øú�����ȡ��Ҫ������ֵ
         * </div>
         * <div class="en">
         * Get the data element specified by the tag.
         * </div>
         * 
         * @param tag
    	 * <div class="zh">
    	 * 		EMV����ı�׼����Ԫ�ر�ǩ����չ�ı�ǩ
    	 * </div>
    	 * <div class="en">
    	 * 		Tag of EMV standard or extended data.
    	 * </div>
    	 *
    	 * @return
    	 * <div class="zh">
    	 * 		null : �޴˱�ǩ<br/>
    	 * 		��null: ��ǩֵ
    	 * </div>
    	 * <div class="en">
    	 * 		null: no specified tag<br/>
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
         * ����ָ����ǩ������ֵ
         * </div>
         * <div class="en">
         * Set the data element specified by the tag.
         * </div>
         * 
         * @param tag
    	 * <div class="zh">
    	 * 		EMV����ı�׼����Ԫ�ر�ǩ����չ�ı�ǩ
    	 * </div>
    	 * <div class="en">
    	 * 		EMV standard or extended data element Tag.
    	 * </div>
    	 *
         * @param value
    	 * <div class="zh">
    	 * 		[����]��ǩTagָ��������
    	 * </div>
    	 * <div class="en">
    	 * 		[input]The value of the data element specified by the tag.
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
         * <b><font color=red>ע��: D180 ��֧�ִ˹���,�ο�"MPosApi_Supplementary.docx"��ȡ���������Ϣ </font></b><br/>
         * �����û��Զ����ǩ����ֵ
         * </div>
         * <div class="en">
         * <b><font color=red>NOTE: D180 doesn't support this function,see "MPosApi_Supplementary.docx" for more </font></b><br/>
         * Set the data element by user-defined tag
         * </div>
         * 
         * @param tag
    	 * <div class="zh">
    	 * 		�û��Զ����ǩ
    	 * </div>
    	 * <div class="en">
    	 * 		User-defined tag.
    	 * </div>
    	 *
         * @param value
    	 * <div class="zh">
    	 * 		[����]��ǩTagָ��������
    	 * </div>
    	 * <div class="en">
    	 * 		[input]The value of the data element specified by the tag.
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
         * ��ȡ������Ӧ����ز���
         * </div>
         * <div class="en">
         * Get related parameters of the reader.
         * </div>
         * 
    	 * @return
    	 * <div class="zh">
    	 * 		����������, �μ�{@link com.pax.mposapi.model#CLSS_READER_PARAM}
    	 * </div>
    	 * <div class="en">
    	 * 		reader's relative parameters, see {@link com.pax.mposapi.model#CLSS_READER_PARAM} 
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
         * ���ö�����Ӧ����ز���<br/>
         * �ýӿ�����{@link ClssManager.Pboc#procTrans(byte[])}����ִ��ǰ����
         * </div>
         * <div class="en">
         * Set related parameter of card reader.<br/>
         * This function must be called before Initiate Application Processing(GPO)  to set the reader's relative parameters.
         * </div>
         * 
    	 * @param readerParam
    	 * <div class="zh">
    	 * 		[����]����������, �μ�{@link com.pax.mposapi.model#CLSS_READER_PARAM}
    	 * </div>
    	 * <div class="en">
    	 * 		[input]reader's relative parameters, see {@link com.pax.mposapi.model#CLSS_READER_PARAM} 
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
         * ����QPBOCӦ����ز���<br/>
         * �ýӿ�����{@link ClssManager.Pboc#procTrans(byte[])}����ִ��ǰ����
         * </div>
         * <div class="en">
         * Set QPBOC application related parameters corresponding to the AID.<br/>
         * This function must be called before Initiate Application Processing(GPO) to set application's relative parameters.
         * </div>
         * 
    	 * @param aidParam
    	 * <div class="zh">
    	 * 		[����]qPBOCӦ����ز���, �μ�{@link com.pax.mposapi.model#CLSS_PBOC_AID_PARAM}
    	 * </div>
    	 * <div class="en">
    	 * 		[input]qPBOC application related paramters, see{@link com.pax.mposapi.model#CLSS_PBOC_AID_PARAM}
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
         * ���һ���µ���֤������Կ<br/>
         * �����Կ����, �����µ���Կ����ԭ������Կ, ��֤������Կ���յ����ṩ, �յ����ṩ����Կ��һ�����Ͻṹ{@link com.pax.mposapi.model#EMV_CAPK}, Ӧ�ÿ�����Ҫת���������ӵ�EMV��
         * SM�㷨ʱ, capk����������HashInd = 0x07��ArithInd =0x04
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
         * ���ں���ɾ��һ����֤������Կ
         * </div>
         * <div class="en">
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
    	 * 		[����]Ӧ��ע�������ID 
    	 * </div>
    	 * <div class="en">
    	 * 		[input]Registered Application Provider Identifier.
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
         * ���ں��ж���һ����֤���Ĺ�Կ
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
    	 * 		the key index
    	 * </div>
    	 * 
    	 * @return
    	 * <div class="zh">
    	 * 		null: ָ����keyId������<br/>
    	 * 		��null: ��֤������Կ
    	 * 
    	 * </div>
    	 * <div class="en">
    	 * 		null: no valid for specified keyId<br/>
    	 * 		non-null: the specified CA public key
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
         * ɾ��������֤���Ĺ�Կ<br/>
         * ����Ӧ�ÿ���ʱ, ÿ�ν���ǰ��ɾ���ں��е����й�Կ, ���ݵ�ǰӦ�������CA��Կ��������, ����ÿ�ν�������һ����Կ
         * </div>
         * <div class="en">
         * Delete all CA public keys.<br/>
         * Application developer is suggested to call this function to delete all CA Public Keys in kernel at the beginning of each transaction, and set CA Public Key according to current application's requirment. In this way, application only need to set one CA Public Key each time.
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
         * ���һ�������յķ����й�Կ֤�����ݵ�֤������б�<br/>
         * �������ӵ�֤���Ѵ��� , ��ֱ�ӷ��سɹ�
         * </div>
         * <div class="en">
         * Add a revoked issuer public key certification to certification revocation list.<br/>
         * If the revoked issuer public key certification already exists, this function directly returns success. 
         * </div>
         * 
         * @param revocList
    	 * <div class="zh">
    	 * 		[����]�����й�Կ֤�����ݽṹ
    	 * </div>
    	 * <div class="en">
    	 * 		[input] the revoked issuer public key certification
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
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_ADD_REVOC_LIST, req, rc, respBuffer);
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
    	 * 		[input]The corresponding RID of the revoked issuer public key certification (5 byte)
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
        	
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_DEL_REVOC_LIST, req, rc, respBuffer);
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
        	proto.sendRecv(Cmd.CmdType.CLSS_PBOC_DEL_ALL_REVOC_LIST, new byte[0], rc, respBuffer);
        	if (rc.code == 0) {
        		//success
        	} else {
            	throw new EmvException(rc.code);
        	}
        }

        /**
         * <div class="zh">
         * ��������ѡ����ص�����<br/>
         * 1.��������ѡ������֮ǰ��Ҫͨ��Entry���ṩ�Ľӿڻ�ȡ����ѡ������ݣ�<br/>
		 * 2.�ýӿ�����{@link ClssManager.Pboc#procTrans(byte[])}����ִ��ǰ����
         * </div>
         * <div class="en">
         * Set the data returned from card during final selection.<br/>
         * 1.get the data of final selection firstly before setting.<br/>
         * 2.must be called before {@link ClssManager.Pboc#procTrans(byte[])}
         * </div>
         * 
         * @param data
    	 * <div class="zh">
    	 * 		[����]�����õ�����
    	 * </div>
    	 * <div class="en">
    	 * 		[input]the data to set
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
         * ���ý�����ز����Լ�Ԥ����������<br/>
		 * 1.�ýӿ�����{@link ClssManager.Pboc#procTrans(byte[])}����ִ��ǰ����<br/>
         * 2.����֧�ַǽ�����PBOC,���Բ����н���Ԥ����,�������ý���Ԥ������ʱ�����ý���Ԥ���������ݽṹ{@link com.pax.mposapi.model.CLSS_PRE_PROC_INTER_INFO#aucReaderTTQ}�����ĵ�һ���ֽ�,�����ն�֧�ַǽ�����PBOC
         * </div>
         * <div class="en">
         * Set related parameters of transaction and transfer the result of preliminary transaction processing.<br/>
         * 1.must be called before {@link ClssManager.Pboc#procTrans(byte[])}<br/>
         * 2.if not support qPBOC, application can ignore pre-processing, however, you should clear qPBOC bit of {@link com.pax.mposapi.model.CLSS_PRE_PROC_INTER_INFO#aucReaderTTQ}. 
         * </div>
         * 
         * @param transParam
    	 * <div class="zh">
    	 * 		[����]������ز���, ������Ȩ���������͵�������EMV L2��ͬ, �μ�{@link com.pax.mposapi.model.CLSS_TRANS_PARAM}
    	 * </div>
    	 * <div class="en">
    	 * 		[input]the related parameters of transaction, note that the authorized amount and transaction type settings are different from EMV L2, see{@link com.pax.mposapi.model.CLSS_TRANS_PARAM} 
    	 * </div>
    	 *
         * @param preProcInterInfo
    	 * <div class="zh">
    	 * 		[����]Ԥ������, �μ�{@link com.pax.mposapi.model.CLSS_TRANS_PARAM}, ��������ͨ��Entry���{@link ClssManager.Entry#getPreProcInterFlg()}����ȡ.
    	 * </div>
    	 * <div class="en">
    	 * 		[input]the result of preliminary transaction processing, see {@link com.pax.mposapi.model.CLSS_TRANS_PARAM}, it's archieved with {@link ClssManager.Entry#getPreProcInterFlg()}.
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
         * PBOC�ǽӴ����״���������ʼ��Ӧ�ü�����¼�ȴ���<br/>
         * 1.�ú���ִ�гɹ���, ��ΪTC, ������ݾ���Ľ���·�������ж��Ƿ�������ѻ�������֤/�쳣�ļ���鴦��;��ΪAAC��ARQC, ��ֱ�ӽ����ѻ��ܾ���������Ȩ����;<br/>
		 * 2.������ֵΪ{@link EmvManager#CLSS_RESELECT_APP}, ����ͨ��Entry��ӿ�{@link ClssManager.Entry#delCurCandApp()}����ɾ����ǰ��ѡӦ��, �����½�������ѡ�����;<br/>
		 * 3.����ն�֧��SM�㷨, �����ڵ��ñ�����֮ǰ,�ȵ���{@link ClssManager.Pboc#setTLVData(int, byte[])}����'DF69'��ֵΪ1
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
         *		[���]<br/>
         *		result[0]: ����·������ {@link #CLSS_VISA_QVSDC}: qPBOC ��ʽ  , {@link #CLSS_VISA_VSDC}: �����ǽ�PBOC��ʽ<br/>
         *		result[1]: AC����:{@link EmvManager#EMV_AC_AAC}, {@link EmvManager#EMV_AC_TC}, {@link EmvManager#EMV_AC_ARQC}
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
         *			<li>{@link EmvManager#EMV_OK}: �ɹ�
         *			<li>{@link EmvManager#CLSS_PARAM_ERR}: ��������
         *			<li>{@link EmvManager#CLSS_RESELECT_APP}: ����ִ������ѡ��. ����ѡ��Ӧ��
         *			<li>{@link EmvManager#CLSS_USE_CONTACT}: ��ֹ�ǽӴ�����, ʹ�÷ǽӴ����淽ʽִ�н���
         *			<li>{@link EmvManager#CLSS_CARD_EXPIRED}: ��Ƭ��ʧЧ
         *			<li>����: ��ֹ����,����ѯ��
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
         * <div class="zh">ͨ�Ŵ���</div>
         * <div class="en">communication error</div>
         * @throws ProtoException
         * <div class="zh">Э�����</div>
         * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
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
         * �ѻ�������֤, ��������֤״̬�������ս�������õ�AC����<br/>
         * �ú���ִ�гɹ���, Ӧ�ó�������ݷ��صĽ���AC����, ���к��������ѻ���׼, �ѻ��ܾ�, ������Ȩ��
         * </div>
         * <div class="en">
         * Do offline data authentication and return AC type.<br/>
         * After this function is performed successfully, application shall continue with subsequenced process according to the AC returned in this function parameter.(such as offline approve, offline decline,or online authorization)
         * </div>
         * 
         * @param result
         * <div class="zh">
         *		[���]<br/>
         *		result[0]: AC����:{@link EmvManager#EMV_AC_AAC}, {@link EmvManager#EMV_AC_TC}, {@link EmvManager#EMV_AC_ARQC}
         *		result[1]: DDA��֤ʧ�ܱ�־:1-ʧ��
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
         *			<li>{@link EmvManager#EMV_OK}: �ɹ�
         *			<li>{@link EmvManager#CLSS_USE_CONTACT}: ��ֹ�ǽӴ�����, ʹ�÷ǽӴ����淽ʽִ�н���
         *			<li>����: ��ֹ����,����ѯ��
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
         * <div class="zh">ͨ�Ŵ���</div>
         * <div class="en">communication error</div>
         * @throws ProtoException
         * <div class="zh">Э�����</div>
         * <div class="en">protocol error</div>
	     * @throws CommonException
	     * <div class="zh">ͨ�ô���</div>
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
         * ��ȡ�ֿ���CVM��֤��ʽ(ǩ��������PIN��֤)
         * </div>
         * <div class="en">
         * Get CVM Type (signature or online PIN verification)
         * </div>
         * 
         * @return
         * <div class="zh">
         *		�ֿ�����֤��ʽ<br/>
				{@link #RD_CVM_NO}:  �޳ֿ�����֤<br/>
				{@link #RD_CVM_ONLINE_PIN}:  ����PIN<br/>
				{@link #RD_CVM_SIG}:  ǩ��<br/>
         * </div>
         * <div class="en">
         *		CVM type<br/>
				{@link #RD_CVM_NO}:  NO CVM <br/>
				{@link #RD_CVM_ONLINE_PIN}: online PIN<br/>
				{@link #RD_CVM_SIG}:  signature<br/>
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
         * <b><font color=red>ע��: D180 ��֧�ִ˹���,�ο�"MPosApi_Supplementary.docx"��ȡ���������Ϣ </font></b><br/>
         * ����MSD �� qPBOC·���»�ȡӳ�������TRACK1����(ASCII��)<br/>
         * Ӧ��Ҳ��ֱ��ʹ��Track 1�ȼ����ݸ����ض���Ҫ�󼰹���������
         * </div>
         * <div class="en">
         * <b><font color=red>NOTE: D180 doesn't support this function,see "MPosApi_Supplementary.docx" for more </font></b><br/>
         * get mapped track1 data(ASCII) under MSD and qPBOC transaction path<br/>
         * application can use track1 equivalent data to gnereate the data
         * </div>
         * 
         * @return
         * <div class="zh">
         *		ӳ�������TRACK1����
         * </div>
         * <div class="en">
         * 		track1 data
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
         * <b><font color=red>ע��: D180 ��֧�ִ˹���,�ο�"MPosApi_Supplementary.docx"��ȡ���������Ϣ </font></b><br/>
         * ����MSD·���»�ȡӳ�������TRACK2����(ASCII��)<br/>
         * Ӧ��Ҳ��ֱ��ʹ��Track 2�ȼ����ݸ����ض���Ҫ�󼰹���������
         * </div>
         * <div class="en">
         * <b><font color=red>NOTE: D180 doesn't support this function,see "MPosApi_Supplementary.docx" for more </font></b><br/>
         * get mapped track2 data(ASCII) under MSD and qPBOC transaction path<br/>
         * application can use track2 equivalent data to gnereate the data
         * </div>
         * 
         * @return
         * <div class="zh">
         *		ӳ�������TRACK2����
         * </div>
         * <div class="en">
         * 		track2 data
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
         * ���ڻ�ȡGPO��������<br/>
         * �ڽ��״�����{@link ClssManager.Pboc#procTrans(byte[])}���صĽ���·��Ϊ {@link #CLSS_VISA_VSDC}ʱ, ����øýӿ�����ȡGPO��������, ��ͨ�������ǽ�PBOC���еĽӿ������õ������ǽӿ���
         * </div>
         * <div class="en">
         * get the retuned data of GPO<br/>
         * if {@link ClssManager.Pboc#procTrans(byte[])} returns {@link #CLSS_VISA_VSDC}, you should call this function to get returned data of GPO, and then set the data with PBOC interface.
         * </div>
         * 
         * @return
         * <div class="zh">
         *		��null: GPO��������
         *		null:  ������
         * </div>
         * <div class="en">
         * 		non-null: returned data of GPO
         * 		null: no data
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
         * <b><font color=red>ע��: D180 ��֧�ִ˹���,�ο�"MPosApi_Supplementary.docx"��ȡ���������Ϣ </font></b><br/>
         * �ط����һ������¼����, ��������
         * </div>
         * <div class="en">
         * <b><font color=red>NOTE: D180 doesn't support this function,see "MPosApi_Supplementary.docx" for more </font></b><br/>
         * re-send last read record command and continue transaction
         * </div>
         * 
         * @return
         * <div class="zh">
         *		byte[0]: ����·������ {@link #CLSS_VISA_QVSDC}: qPBOC ��ʽ  , {@link #CLSS_VISA_VSDC}: �����ǽ�PBOC��ʽ<br/>
         *		byte[1]: AC����:{@link EmvManager#EMV_AC_AAC}, {@link EmvManager#EMV_AC_TC}, {@link EmvManager#EMV_AC_ARQC}
         * </div>
         * <div class="en">
         * 		byte[0]: transaction path {@link #CLSS_VISA_QVSDC}: qPBOC, {@link #CLSS_VISA_VSDC}: PBOC<br/>
         *		byte[1]: AC type:{@link EmvManager#EMV_AC_AAC}, {@link EmvManager#EMV_AC_TC}, {@link EmvManager#EMV_AC_ARQC}
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
         * <b><font color=red>ע��: D180 ��֧�ִ˹���,�ο�"MPosApi_Supplementary.docx"��ȡ���������Ϣ </font></b><br/>
         * ����Get Data�����ȡ��Ƭ����<br/>
         * Ӧ�ÿ���������ѡ�����øýӿڻ�ȡ��Ƭ����.���������������ݶ���ͨ������������ȡ��
         * </div>
         * <div class="en">
         * <b><font color=red>NOTE: D180 doesn't support this function,see "MPosApi_Supplementary.docx" for more </font></b><br/>
         * send Get Data command to get data from card<br/>
         * application can call this function after finall selection.
         * </div>
         * 
         * @param tag
    	 * <div class="zh">
    	 * 		���ȡ��Ƭ����TAG:Ŀǰֻ֧����9F��ͷ��TAG
    	 * </div>
    	 * <div class="en">
    	 * 		the tag to get value, currently only support tag start with 9F.
    	 * </div>
    	 *
         * @return
         * <div class="zh">
         *		�ӿ�Ƭ�еõ��Ķ�ӦTAG����ֵ
         * </div>
         * <div class="en">
         * 		the value get from the card for the specified tag 
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
         * �Ա���Ӧ�ý��г�ʼ��<br/>
         * ���ѡ���·���Ƿǽ�PBOC, �򷵻سɹ�, ѡ������·����Ϊ����
         * </div>
         * <div class="en">
         * initialize the blocked application<br/>
         * if the transaction path is PBOC, then it's OK, other paths are treated as error. 
         * </div>
         * 
         * @return
         * <div class="zh">
         *		����·������:{@link #CLSS_VISA_QVSDC}: qPBOC ��ʽ  , {@link #CLSS_VISA_VSDC}: �����ǽ�PBOC��ʽ
         * </div>
         * <div class="en">
         * 		transaction path:{@link #CLSS_VISA_QVSDC}: qPBOC, {@link #CLSS_VISA_VSDC}: PBOC
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
