package com.pax.dxxtest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.widget.TextView;

import com.pax.dxxtest.R;
import com.pax.mposapi.ClssManager;
import com.pax.mposapi.KeyboardManager;
import com.pax.mposapi.PortManager;
import com.pax.mposapi.UIManager;
import com.pax.mposapi.model.CLSS_MC_AID_PARAM_MC;
import com.pax.mposapi.model.CLSS_PRE_PROC_INFO;
import com.pax.mposapi.model.CLSS_READER_PARAM;
import com.pax.mposapi.model.CLSS_READER_PARAM_MC;
import com.pax.mposapi.model.CLSS_TERM_CONFIG_MC;
import com.pax.mposapi.model.CLSS_VISA_AID_PARAM;
import com.pax.mposapi.model.EMV_CAPK;
import com.pax.mposapi.model.POSLOG;
import com.pax.mposapi.model.SYS_PROC_INFO;
import com.pax.mposapi.util.Utils;

public class ClssActivity extends Activity {
	
	private String mInterface;
	private String mMethod;
	private String mNo;
	
	private UIManager ui;
	private KeyboardManager kbd;
	private ClssManager clss;
	private PortManager portmg;
	private TextView text;
	private ProgressDialog progressDialog;
	
	private final int NORMALRESULT = 1;
	private final int EXCEPTRESULT = 2;


	// EMV_LIB 
	private final int MAX_REVOCLIST_NUM   = 30;      // EMVCOÒªÇóÃ¿¸öRID±ØÐëÖ§³Ö30¸ö»ØÊÕÁÐ±í  
	private final int MAX_APP_NUM      = 32;         //EMV¿âÓ¦ÓÃÁÐ±í×î¶à¿É´æ´¢µÄÓ¦ÓÃÊý 
	private final int MAX_CAPK_NUM     = 45;         //EMV¿âCAPK×î´óÖµ  

	private final int PART_MATCH       = 0x00;       //Ó¦ÓÃÑ¡ÔñÆ¥Åä±êÖ¾(²¿·ÖÆ¥Åä)  
	private final int FULL_MATCH       = 0x01;       //Ó¦ÓÃÑ¡ÔñÆ¥Åä±êÖ¾(ÍêÈ«Æ¥Åä) 

	private final int EMV_CASH         = 0x01;       //½»Ò×ÀàÐÍ(ÏÖ½ð) 
	private final int EMV_GOODS	       = 0x02;       //½»Ò×ÀàÐÍ(¹ºÎï) 
	private final int EMV_SERVICE      = 0x04;       //½»Ò×ÀàÐÍ(·þÎñ) 
	private final int EMV_CASHBACK     = 0x08;       //½»Ò×ÀàÐÍ(·´ÏÖ) 
	private final int EMV_INQUIRY      = 0x10;		 //½»Ò×ÀàÐÍ(²éÑ¯) 
	private final int EMV_TRANSFER     = 0x20;		 //½»Ò×ÀàÐÍ(×ªÕË) 
	private final int EMV_PAYMENT	   = 0x40;		 //½»Ò×ÀàÐÍ(Ö§¸¶) 
	private final int EMV_ADMIN		   = 0x80;		 //½»Ò×ÀàÐÍ(¹ÜÀí) 
	private final int EMV_CASHDEPOSIT  = 0x90;       //½»Ò×ÀàÐÍ(´æ¿î)

	private final int CLSSTRANSALE = 0x01;
	private final int DOWNLOADCAPK = 0x02;
	private final int DOWNLOADAID  = 0x03;
	private final int DOWNVISAREADER = 0x04;
	private final int DOWNVISAAIDPARAM = 0x05;
	private final int DOWNMCREADER = 0x06;
	private final int DOWNMCAIDPARAM = 0x07;
	// Revised by mauhui, 2013-12-04
	// delete paypass3.0 	Clss_TransParam_MC ClssTransParaMC
	// there is no need to download this parameter file
	//there is no need to download 
	private final int DOWNMCTERMCONFIG  = 0x08;
	private final int FINISHTRANS = 0x09;
	private final int CLSSGETIPN = 0x0A;
	private final int DELETECAPK = 0x0B;
	private final int DELETEALLCAPK = 0x0C;
	private final int DELETEAID     = 0x0D;
	private final int DELETEALLAID  = 0x0E;
	private final int CLSSTRANSALECANCEL = 0x0F;
	private final int CLSSTRANSALEINIT = 0x10;
	private final int CLSSWAITCARD = 0x11;
	private final int CLSSSTARTSIGN = 0x12;
	private final int CLSSCANCELSIGN = 0x13;
	private final int CLSSCHECKSIGN = 0x14;
	private final int CLSSSGETSIG = 0x15;
	private final int CLSSSHOWSIG = 0x16;
	
	private final int COMM_BUFF_LEN  = (8*1024);
	private final int POS_COMMAND_OK			= 0x00;	// ok
	private final int POS_COMMAND_UNSUPPORT	= 0xFE;	// pos not support the command
	private final int POS_COMMAND_NOTCONNECT	= 0xFF;	// not connect to pos yet
	
	
	private final int EC_OK			= 0x00;
	private final int EC_FAIL			= 0xA1;
	private final int EC_IC			= 0xA2; //using contact 
	private final int EC_NO_APP		= 0xA3;
	private final int EC_BLOCK        = 0xA4;
	private final int EC_APP_BLOCK    = 0xA5;
	private final int EC_ERR			= 0xA6;
	private final int NO_ENOUGH_AMT	= 0xA7;
	private final int EC_OUT_AMT      = 0xA8;
	private final int EC_CANCEL		= 0xA9;
	private final int NO_SUPPORT		= 0xB1;
	private final int EC_CA_ERR		= 0xB2;
	private final int EMV_NO_AID		= 0xB3;
	private final int EC_NO_CARD		= 0xB4;
	private final int EC_ERR_PARAM	= 0xB5;
	private final int EC_OVERFLOW 	= 0xB5;
	private final int EC_CMD_ERR   	= 0xB7;
	private final int EC_RSP_ERR  	= 0xB8;
	private final int EC_FILE_NOTFOUND = 0xB9;
	private final int EC_NO_APP_PPSE_ERR  = 0xC0;
	private final int EC_DATA_ERR		= 0xC1;
	private final int EC_NO_DATA      = 0xC2;
	private final int EC_CAPK_ERR		= 0xC3;
	private final int EC_DECLINE_TRANS = 0xC4;
	private final int EC_AMOUNT_ZERO  = 0xC5;
	private final int EC_TIMEOUT      = 0xC6;
	private final int EC_AMOUNT_TOO_BIG  = 0xC7;
	private final int EC_USER_CANCEL     = 0xC8;
	
	private final int EC_TS_NOT_OPNE     = 0xD0;  //´¥ÃþÆÁÃ»´ò¿ª
	private final int EC_TS_NOTSUPPORT     = 0xD1; //ÏµÍ³²»Ö§³Ö´¥ÃþÆÁ
	private final int EC_TS_OCCUPIED       = 0xD2; //´¥ÃþÆÁÒÑ±»Õ¼ÓÃ
	private final int EC_TS_INVALID_AREA   = 0xD3;
	private final int EC_TS_SIN_SAVE_FAIL  = 0xD4;
	
	private final int EC_NO_VISAREADER_FILE     = 0xE0;  // NO VisaReader FILE
	private final int EC_NO_VISAAID_FILE     = 0xE1; //NO VisaAidParam FILE
	private final int EC_NO_MCTERM_CONFIG       = 0xE2; //NO MCTermConfig
	private final int EC_NO_MCREADER_FILE   = 0xE3; // NO MCReader FILE
	private final int EC_NO_MCAAID_FILE  = 0xE4; //NO MCAidParam FILE
	private final int EC_FILE_NAME_TOO_LONG = 0xE5;

	//Define retrun value
	private final int IFD_OK	= 0;	//Ö´ÐÐ³É¹¦
	private final int IFD_ICC_TypeError	= 0x01;	//¿¨Æ¬ÀàÐÍ²»¶Ô
	private final int IFD_ICC_NoExist	= 0x02;	//ÎÞ¿¨
	private final int IFD_ICC_NoPower = 0x03;	//ÓÐ¿¨Î´ÉÏµç
	private final int IFD_ICC_NoResponse	= 0x04;	//¿¨Æ¬ÎÞÓ¦´ð
	private final int IFD_ConnectError	= 0x11;	//¶Á¿¨Æ÷Á¬½Ó´í
	private final int IFD_UnConnected	= 0x12;	//Î´½¨Á¢Á¬½Ó(Ã»ÓÐÖ´ÐÐ´ò¿ªÉè±¸º¯Êý)
	private final int IFD_BadCommand	= 0x13;	//(¶¯Ì¬¿â)²»Ö§³Ö¸ÃÃüÁî
	private final int IFD_ParameterError	= 0x14;	//(·¢¸ø¶¯Ì¬¿âµÄ)ÃüÁî²ÎÊý´í
	private final int IFD_CheckSumError	= 0x15; //ÐÅÏ¢Ð£ÑéºÍ³ö´í
	
	private final int KERNTYPE_DEF  = 0;
	private final int KERNTYPE_JCB  = 1;
	private final int KERNTYPE_MC   = 2;
	private final int KERNTYPE_VIS  = 3;
	private final int KERNTYPE_PBOC = 4;
	private final int KERNTYPE_AE   = 5;
	private final int KERNTYPE_RFU  = 6;
	
	private final int CT_OFFLINE_APPV		= 0x01;
	private final int CT_OFFLINE_DECLINE	= 0x02;
	private final int CT_NEED_ONLINE		= 0x03;
	private final int CT_NEED_PIN 			= 0x04;
	private final int CT_NEED_SIGN        	= 0x05;
		
	private final int MAX_BUF_LEN  = 256;
	private final int STX          = 0x02;       //Start of Text
	
	private final int BMP1_TYPE  = 1;
	private final int BMP24_TYPE = 24;
	
	private final int LINE_THIN    = 0;
	private final int LINE_MEDIUM  = 1;
	private final int LINE_THICK   = 2;
	
	private final int COLOR_BLACK	= 0;
	private final int COLOR_WHITE	= 0xffffff;
	
	private final int PT_CHAR   = 0;
	private final int PT_UINT   = 1;
	private final int PT_INT    = 2;
	private final int PT_USHORT = 3;
	
	//Define function types
	private final int IC_FUN       = 0x01;
	private final int MAG_FUN      = 0x02;
	private final int COMM_FUN     = 0x03;
	private final int PRINT_FUN    = 0x04;
	private final int PINPAD_FUN   = 0x05;
	private final int FILE_FUN     = 0x06;
	private final int MULAPP_FUN   = 0x07;
	private final int SIMUL_END    = 0x08;
	private final int RFCARD_FUN   = 0x09;
	private final int WNET_FUN     = 0x0A;
	private final int BEEP_FUN     = 0x0B;

	//private final int PICC_FUN	 = 0x0C;
	private final int PPP_FUN		 = 0x0D;
	private final int PED_FUN		 = 0x0E;
	private final int MISC_FUN	 = 0x0F;
	private final int INNERPED_FUN = 0x10;
	private final int PHONE_FUN	 = 0x11;
	private final int IP_FUN       = 0x12;
	private final int FATFS_FUN    = 0x13;
	private final int USB_FUN		 = 0x14;
	private final int SYS_SET_FUN  = 0x15;
	private final int WL_NET_FUN   = 0x16;
	private final int SYS_BASE_FUN = 0x17;
	private final int SSL_FUN		 = 0x18;	
	private final int TILTSENSOR_FUN = 0x19;

	//Mandy add for CMB
	private final int SCR_FUN		= 0x1A;
	private final int KEY_FUN		= 0x1B;

	//mahui add for Clss
	private final int CLSS_FUN    = 0x1C;
	
	private final int PED_WRITE_KEY 			= 0x1E;
	private final int PED_WRITE_TIK 			= 0x1F;
	private final int PED_GET_PIN_BLOCK 		= 0x20;
	private final int PED_GET_MAC 				= 0x21;
	private final int PED_CALC_DES 				= 0x22;
	private final int PED_GET_PIN_DUKPT 		= 0x23;
	private final int PED_GET_MAC_DUKPT 		= 0x24;
	private final int PED_RE_GEN_PIN_BLOCK 		= 0x25;
	private final int PED_VERIFY_PLAIN_PIN 		= 0x26;
	private final int PED_VERIFY_CIPHER_PIN 	= 0x27;
	private final int PED_GET_KCV 				= 0x28;
	private final int PED_WRITE_KEY_VAR 		= 0x29;
	private final int PED_GET_VER 				= 0x2A;
	private final int PED_ERASE 				= 0x2B;
	private final int PED_SET_INTERVAL_TIME 	= 0x2C;
	private final int PED_SET_KEY_TAG 			= 0x2D;
	private final int PED_SET_FUNCTION_KEY		= 0x30;
	private final int PED_DUKPT_DES				= 0x31;
	private final int PED_DUKPT_INCREASE_KSN	= 0x32;
	private final int PED_GET_DUPKT_KSN			= 0x33;
	private final int PED_ERASE_KEY				= 0x34;
	private final int PED_WRITE_KEYEX			= 0x35;
	private final int PED_WRITE_RSAKEY			= 0x36;
	private final int PED_RSA_RECOVER			= 0x37;
	private final int PED_RSAKEY_PAIRGEN		= 0x38;
	
	
	private final int PED_RET_OK = 0;

	private final int TIME_OUT = 3;
	
	private byte glPort = 3;

	private SYS_PROC_INFO	glProcInfo;		// ½»Ò×´¦ÀíÐÅÏ¢
	private EMV_CAPK gCapklist[];
	private CLSS_PRE_PROC_INFO gClss_PreProcInfo;
	
	private byte    com232_revbuf[];
	private byte    com232_sendbuf[];

	private EMV_CAPK capk01;
	private EMV_CAPK capk02;
	private EMV_CAPK capk03;
	private EMV_CAPK capk04;
	private EMV_CAPK capk05;
	private EMV_CAPK capk06;
	private EMV_CAPK capk07;
	private EMV_CAPK capk08;
	private EMV_CAPK capk09;
	private EMV_CAPK capk10;
	private EMV_CAPK capk11;
	private EMV_CAPK capk12;
	private EMV_CAPK capk13;
	private EMV_CAPK capk14;
	private EMV_CAPK capk15;
	private EMV_CAPK capk16;
	private EMV_CAPK capk17;
	private EMV_CAPK capk18;
	private EMV_CAPK capk19;
	private EMV_CAPK capk20;

	
	private CLSS_PRE_PROC_INFO VISA_VSDC_APP;
	private CLSS_PRE_PROC_INFO VISA_ELECTRON_APP;
	private CLSS_PRE_PROC_INFO MASTER_MCHIP; 
	private CLSS_PRE_PROC_INFO MASTER_MAESTRO_APP;
	private CLSS_PRE_PROC_INFO MASTER_CIRRUS_APP;
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        
        Intent intent = getIntent();
        mInterface = intent.getStringExtra("Interface");
        mMethod = intent.getStringExtra("Method");
        mNo = intent.getStringExtra("No");
        
        TextView tv = (TextView)findViewById(R.id.base_title);
        tv.setText(mInterface + "_" + mMethod + mNo);
        
	    ui = UIManager.getInstance(this);
	    kbd = KeyboardManager.getInstance(this);
	    clss = ClssManager.getInstance(this);
	    portmg = PortManager.getInstance(this);
	    
	    test();
    }

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NORMALRESULT:
			case EXCEPTRESULT:
				Bundle bundle = msg.getData();
				String result = bundle.getString("result");
				text.setText(result);
				break;
			}
	    }

	};

	private void test() {
		text = (TextView)findViewById(R.id.textViewBase);		
		progressDialog = new ProgressDialog(ClssActivity.this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Processing");
		progressDialog.setIndeterminate(true);
		progressDialog.show();
				
		new Thread(new Runnable(){
			
			public void run(){
				Looper.prepare();				
				if(mInterface.equals("DownloadCAPK"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestDownloadCAPKFUN1();
						}
					}
				}
				else if(mInterface.equals("DownloadAPP"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestDownloadAPPFUN1();
						}
					}
				}
				else if(mInterface.equals("DownloadParam"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestDownloadParamFUN1();
						}
					}
				}
				else if(mInterface.equals("ClssTrans"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestClssTransFUN1();
						}
					}
				}
			}
		}).start();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		  if (keyCode == KeyEvent.KEYCODE_BACK) {
			  finish();
		  }
		  return false;
	}	
	
	private void IntArray2ByteArray(int[] in, int iOffset, byte[] by, int bOffset, int len)
	{
		for(int i = 0; i < len; i++)
		{
			by[bOffset + i] = (byte) (in[iOffset + i] & 0xff);
		}
	}
	
	private void InitCapk()
	{
		//����CAPK��д��
		int tmp[];
		//init capk01
		capk01 = new EMV_CAPK();
		tmp = new int[]{0xA0, 0x00, 0x00, 0x03, 0x33};
		IntArray2ByteArray(tmp, 0, capk01.RID, 0, tmp.length);
		capk01.KeyID = 0x01;
		capk01.HashInd = 0x01;
		capk01.ArithInd = 0x01;
		capk01.ModulLen = (byte) 128;
		tmp = new int[]{0xD2, 0x84, 0x53, 0x44, 0x12, 0x32, 0x27, 0xD8, 0xD0, 0x23, 0xE8, 0x28, 0x38, 0x39, 0xED, 0x83, 0xAE, 0x5B,
	            0x01, 0x1F, 0x31, 0x63, 0x7E, 0x9B, 0x49, 0x22, 0xF8, 0xE5, 0xD9, 0x03, 0xBE, 0xCB, 0xD5, 0xA6, 0x78, 0x93,
	            0xC4, 0xBE, 0x68, 0x58, 0x25, 0x40, 0x86, 0xED, 0xF7, 0x80, 0xDB, 0x30, 0x12, 0xB8, 0x31, 0x4D, 0xF5, 0x21,
	            0xF2, 0xBD, 0xD5, 0xF9, 0xAF, 0xCC, 0x7C, 0xEC, 0xCD, 0xEC, 0x3E, 0xB7, 0x4C, 0x27, 0x3F, 0xC1, 0x66, 0x72,
	            0xED, 0x9E, 0x04, 0x76, 0x24, 0x74, 0x7F, 0xC9, 0xE9, 0xDD, 0x59, 0x33, 0xFB, 0x78, 0x4D, 0xE9, 0x4D, 0xE0,
	            0xFB, 0x18, 0x76, 0x24, 0xD0, 0xCC, 0x57, 0x93, 0x09, 0xA1, 0x65, 0x1C, 0xEA, 0x4B, 0xAD, 0x30, 0x5C, 0x5B,
	            0x83, 0xF1, 0x98, 0x1F, 0xEC, 0x34, 0x05, 0x0F, 0x0F, 0x5D, 0xBF, 0x72, 0xBF, 0x13, 0x78, 0x37, 0x6D, 0x4C,
	            0x8F, 0xD3};
		IntArray2ByteArray(tmp, 0, capk01.Modul, 0, tmp.length);
		capk01.ExpLen = 3;
		tmp = new int[]{0x01, 0x00, 0x01};
		IntArray2ByteArray(tmp, 0, capk01.Exp, 0, tmp.length);
		tmp = new int[]{0x06, 0x10, 0x31};
		IntArray2ByteArray(tmp, 0, capk01.ExpDate, 0, tmp.length);
		tmp = new int[]{0x22, 0xA1, 0xD6, 0x6F, 0x53, 0xF7, 0x6D, 0x47, 0xF9, 0x96, 0x0C, 0xF3, 0xD5, 0x96, 0xD1, 0xEC, 0x4E, 0xBF,
	            0xC2, 0x47};
		IntArray2ByteArray(tmp, 0, capk01.CheckSum, 0, tmp.length);
		
		
		//init capk02
		capk02 = new EMV_CAPK();
		tmp = new int[]{0xA0, 0x00, 0x00, 0x03, 0x33};
		IntArray2ByteArray(tmp, 0, capk02.RID, 0, tmp.length);
		capk02.KeyID = 0x08;
		capk02.HashInd = 0x01;
		capk02.ArithInd = 0x01;
		capk02.ModulLen = (byte) 144;
		tmp = new int[]{0xB6, 0x16, 0x45, 0xED, 0xFD, 0x54, 0x98, 0xFB, 0x24, 0x64, 0x44, 0x03, 0x7A, 0x0F, 0xA1, 0x8C, 0x0F, 0x10,
	            0x1E, 0xBD, 0x8E, 0xFA, 0x54, 0x57, 0x3C, 0xE6, 0xE6, 0xA7, 0xFB, 0xF6, 0x3E, 0xD2, 0x1D, 0x66, 0x34, 0x08,
	            0x52, 0xB0, 0x21, 0x1C, 0xF5, 0xEE, 0xF6, 0xA1, 0xCD, 0x98, 0x9F, 0x66, 0xAF, 0x21, 0xA8, 0xEB, 0x19, 0xDB,
	            0xD8, 0xDB, 0xC3, 0x70, 0x6D, 0x13, 0x53, 0x63, 0xA0, 0xD6, 0x83, 0xD0, 0x46, 0x30, 0x4F, 0x5A, 0x83, 0x6B,
	            0xC1, 0xBC, 0x63, 0x28, 0x21, 0xAF, 0xE7, 0xA2, 0xF7, 0x5D, 0xA3, 0xC5, 0x0A, 0xC7, 0x4C, 0x54, 0x5A, 0x75,
	            0x45, 0x62, 0x20, 0x41, 0x37, 0x16, 0x96, 0x63, 0xCF, 0xCC, 0x0B, 0x06, 0xE6, 0x7E, 0x21, 0x09, 0xEB, 0xA4,
	            0x1B, 0xC6, 0x7F, 0xF2, 0x0C, 0xC8, 0xAC, 0x80, 0xD7, 0xB6, 0xEE, 0x1A, 0x95, 0x46, 0x5B, 0x3B, 0x26, 0x57,
	            0x53, 0x3E, 0xA5, 0x6D, 0x92, 0xD5, 0x39, 0xE5, 0x06, 0x43, 0x60, 0xEA, 0x48, 0x50, 0xFE, 0xD2, 0xD1, 0xBF};
		IntArray2ByteArray(tmp, 0, capk02.Modul, 0, tmp.length);
		capk02.ExpLen = 1;
		tmp = new int[]{0x03};
		IntArray2ByteArray(tmp, 0, capk02.Exp, 0, tmp.length);
		tmp = new int[]{0x30, 0x12, 0x30};
		IntArray2ByteArray(tmp, 0, capk02.ExpDate, 0, tmp.length);
		tmp = new int[]{0xEE, 0x23, 0xB6, 0x16, 0xC9, 0x5C, 0x02, 0x65, 0x2A, 0xD1, 0x88, 0x60, 0xE4, 0x87, 0x87, 0xC0, 0x79, 0xE8,
	            0xE8, 0x5A};
		IntArray2ByteArray(tmp, 0, capk02.CheckSum, 0, tmp.length);
		
		
		//init capk03
		capk03 = new EMV_CAPK();
		tmp = new int[]{0xA0, 0x00, 0x00, 0x03, 0x33};
		IntArray2ByteArray(tmp, 0, capk03.RID, 0, tmp.length);
		capk03.KeyID = 0x09;
		capk03.HashInd = 0x01;
		capk03.ArithInd = 0x01;
		capk03.ModulLen = (byte) 176;
		tmp = new int[]{ 0xEB, 0x37, 0x4D, 0xFC, 0x5A, 0x96, 0xB7, 0x1D, 0x28, 0x63, 0x87, 0x5E, 0xDA, 0x2E, 0xAF, 0xB9,
	            0x6B, 0x1B, 0x43, 0x9D, 0x3E, 0xCE, 0x0B, 0x18, 0x26, 0xA2, 0x67, 0x2E, 0xEE, 0xFA, 0x79, 0x90,
	            0x28, 0x67, 0x76, 0xF8, 0xBD, 0x98, 0x9A, 0x15, 0x14, 0x1A, 0x75, 0xC3, 0x84, 0xDF, 0xC1, 0x4F,
	            0xEF, 0x92, 0x43, 0xAA, 0xB3, 0x27, 0x07, 0x65, 0x9B, 0xE9, 0xE4, 0x79, 0x7A, 0x24, 0x7C, 0x2F,
	            0x0B, 0x6D, 0x99, 0x37, 0x2F, 0x38, 0x4A, 0xF6, 0x2F, 0xE2, 0x3B, 0xC5, 0x4B, 0xCD, 0xC5, 0x7A,
	            0x9A, 0xCD, 0x1D, 0x55, 0x85, 0xC3, 0x03, 0xF2, 0x01, 0xEF, 0x4E, 0x8B, 0x80, 0x6A, 0xFB, 0x80,
	            0x9D, 0xB1, 0xA3, 0xDB, 0x1C, 0xD1, 0x12, 0xAC, 0x88, 0x4F, 0x16, 0x4A, 0x67, 0xB9, 0x9C, 0x7D,
	            0x6E, 0x5A, 0x8A, 0x6D, 0xF1, 0xD3, 0xCA, 0xE6, 0xD7, 0xED, 0x3D, 0x5B, 0xE7, 0x25, 0xB2, 0xDE,
	            0x4A, 0xDE, 0x23, 0xFA, 0x67, 0x9B, 0xF4, 0xEB, 0x15, 0xA9, 0x3D, 0x8A, 0x6E, 0x29, 0xC7, 0xFF,
	            0xA1, 0xA7, 0x0D, 0xE2, 0xE5, 0x4F, 0x59, 0x3D, 0x90, 0x8A, 0x3B, 0xF9, 0xEB, 0xBD, 0x76, 0x0B,
	            0xBF, 0xDC, 0x8D, 0xB8, 0xB5, 0x44, 0x97, 0xE6, 0xC5, 0xBE, 0x0E, 0x4A, 0x4D, 0xAC, 0x29, 0xE5};
		IntArray2ByteArray(tmp, 0, capk03.Modul, 0, tmp.length);
		capk03.ExpLen = 1;
		tmp = new int[]{0x03};
		IntArray2ByteArray(tmp, 0, capk03.Exp, 0, tmp.length);
		tmp = new int[]{0x30, 0x12, 0x30};
		IntArray2ByteArray(tmp, 0, capk03.ExpDate, 0, tmp.length);
		tmp = new int[]{0xA0, 0x75, 0x30, 0x6E, 0xAB, 0x00, 0x45, 0xBA, 0xF7, 0x2C, 0xDD, 0x33, 0xB3, 0xB6, 0x78, 0x77, 0x9D, 0xE1,
				0xF5, 0x27};
		IntArray2ByteArray(tmp, 0, capk03.CheckSum, 0, tmp.length);
	
		//init capk04
		capk04 = new EMV_CAPK();
		tmp = new int[]{0xA0, 0x00, 0x00, 0x03, 0x33};
		IntArray2ByteArray(tmp, 0, capk04.RID, 0, tmp.length);
		capk04.KeyID = 0x0b;
		capk04.HashInd = 0x01;
		capk04.ArithInd = 0x01;
		capk04.ModulLen = (byte) 248;
		tmp = new int[]{ 0xCF, 0x9F, 0xDF, 0x46, 0xB3, 0x56, 0x37, 0x8E, 0x9A, 0xF3, 0x11, 0xB0, 0xF9, 0x81, 0xB2, 0x1A,
	            0x1F, 0x22, 0xF2, 0x50, 0xFB, 0x11, 0xF5, 0x5C, 0x95, 0x87, 0x09, 0xE3, 0xC7, 0x24, 0x19, 0x18,
	            0x29, 0x34, 0x83, 0x28, 0x9E, 0xAE, 0x68, 0x8A, 0x09, 0x4C, 0x02, 0xC3, 0x44, 0xE2, 0x99, 0x9F,
	            0x31, 0x5A, 0x72, 0x84, 0x1F, 0x48, 0x9E, 0x24, 0xB1, 0xBA, 0x00, 0x56, 0xCF, 0xAB, 0x3B, 0x47,
	            0x9D, 0x0E, 0x82, 0x64, 0x52, 0x37, 0x5D, 0xCD, 0xBB, 0x67, 0xE9, 0x7E, 0xC2, 0xAA, 0x66, 0xF4,
	            0x60, 0x1D, 0x77, 0x4F, 0xEA, 0xEF, 0x77, 0x5A, 0xCC, 0xC6, 0x21, 0xBF, 0xEB, 0x65, 0xFB, 0x00,
	            0x53, 0xFC, 0x5F, 0x39, 0x2A, 0xA5, 0xE1, 0xD4, 0xC4, 0x1A, 0x4D, 0xE9, 0xFF, 0xDF, 0xDF, 0x13,
	            0x27, 0xC4, 0xBB, 0x87, 0x4F, 0x1F, 0x63, 0xA5, 0x99, 0xEE, 0x39, 0x02, 0xFE, 0x95, 0xE7, 0x29,
	            0xFD, 0x78, 0xD4, 0x23, 0x4D, 0xC7, 0xE6, 0xCF, 0x1A, 0xBA, 0xBA, 0xA3, 0xF6, 0xDB, 0x29, 0xB7,
	            0xF0, 0x5D, 0x1D, 0x90, 0x1D, 0x2E, 0x76, 0xA6, 0x06, 0xA8, 0xCB, 0xFF, 0xFF, 0xEC, 0xBD, 0x91,
	            0x8F, 0xA2, 0xD2, 0x78, 0xBD, 0xB4, 0x3B, 0x04, 0x34, 0xF5, 0xD4, 0x51, 0x34, 0xBE, 0x1C, 0x27,
	            0x81, 0xD1, 0x57, 0xD5, 0x01, 0xFF, 0x43, 0xE5, 0xF1, 0xC4, 0x70, 0x96, 0x7C, 0xD5, 0x7C, 0xE5,
	            0x3B, 0x64, 0xD8, 0x29, 0x74, 0xC8, 0x27, 0x59, 0x37, 0xC5, 0xD8, 0x50, 0x2A, 0x12, 0x52, 0xA8,
	            0xA5, 0xD6, 0x08, 0x8A, 0x25, 0x9B, 0x69, 0x4F, 0x98, 0x64, 0x8D, 0x9A, 0xF2, 0xCB, 0x0E, 0xFD,
	            0x9D, 0x94, 0x3C, 0x69, 0xF8, 0x96, 0xD4, 0x9F, 0xA3, 0x97, 0x02, 0x16, 0x2A, 0xCB, 0x5A, 0xF2,
	            0x9B, 0x90, 0xBA, 0xDE, 0x00, 0x5B, 0xC1, 0x57};
		IntArray2ByteArray(tmp, 0, capk04.Modul, 0, tmp.length);
		capk04.ExpLen = 1;
		tmp = new int[]{0x03};
		IntArray2ByteArray(tmp, 0, capk04.Exp, 0, tmp.length);
		tmp = new int[]{0x30, 0x12, 0x30};
		IntArray2ByteArray(tmp, 0, capk04.ExpDate, 0, tmp.length);
		tmp = new int[]{0xBD, 0x33, 0x1F, 0x99, 0x96, 0xA4, 0x90, 0xB3, 0x3C, 0x13, 0x44, 0x10, 0x66, 0xA0, 0x9A, 0xD3, 0xFE, 0xB5,
	            0xF6, 0x6C};
		IntArray2ByteArray(tmp, 0, capk04.CheckSum, 0, tmp.length);
	
		
		//init capk05
		capk05 = new EMV_CAPK();
		tmp = new int[]{0xA0, 0x00, 0x00, 0x03, 0x33};
		IntArray2ByteArray(tmp, 0, capk05.RID, 0, tmp.length);
		capk05.KeyID = 0x0a;
		capk05.HashInd = 0x01;
		capk05.ArithInd = 0x01;
		capk05.ModulLen = (byte) 128;
		tmp = new int[]{0xB2, 0xAB, 0x1B, 0x6E, 0x9A, 0xC5, 0x5A, 0x75, 0xAD, 0xFD, 0x5B, 0xBC, 0x34, 0x49, 0x0E, 0x53,
	            0xC4, 0xC3, 0x38, 0x1F, 0x34, 0xE6, 0x0E, 0x7F, 0xAC, 0x21, 0xCC, 0x2B, 0x26, 0xDD, 0x34, 0x46,
	            0x2B, 0x64, 0xA6, 0xFA, 0xE2, 0x49, 0x5E, 0xD1, 0xDD, 0x38, 0x3B, 0x81, 0x38, 0xBE, 0xA1, 0x00,
	            0xFF, 0x9B, 0x7A, 0x11, 0x18, 0x17, 0xE7, 0xB9, 0x86, 0x9A, 0x97, 0x42, 0xB1, 0x9E, 0x5C, 0x9D,
	            0xAC, 0x56, 0xF8, 0xB8, 0x82, 0x7F, 0x11, 0xB0, 0x5A, 0x08, 0xEC, 0xCF, 0x9E, 0x8D, 0x5E, 0x85,
	            0xB0, 0xF7, 0xCF, 0xA6, 0x44, 0xEF, 0xF3, 0xE9, 0xB7, 0x96, 0x68, 0x8F, 0x38, 0xE0, 0x06, 0xDE,
	            0xB2, 0x1E, 0x10, 0x1C, 0x01, 0x02, 0x89, 0x03, 0xA0, 0x60, 0x23, 0xAC, 0x5A, 0xAB, 0x86, 0x35,
	            0xF8, 0xE3, 0x07, 0xA5, 0x3A, 0xC7, 0x42, 0xBD, 0xCE, 0x6A, 0x28, 0x3F, 0x58, 0x5F, 0x48, 0xEF};
		IntArray2ByteArray(tmp, 0, capk05.Modul, 0, tmp.length);
		capk05.ExpLen = 1;
		tmp = new int[]{0x03};
		IntArray2ByteArray(tmp, 0, capk05.Exp, 0, tmp.length);
		tmp = new int[]{0x30, 0x12, 0x30};
		IntArray2ByteArray(tmp, 0, capk05.ExpDate, 0, tmp.length);
		tmp = new int[]{0xC8, 0x8B, 0xE6, 0xB2, 0x41, 0x7C, 0x4F, 0x94, 0x1C, 0x93, 0x71, 0xEA, 0x35, 0xA3, 0x77, 0x15, 0x87, 0x67,
	            0xE4, 0xE3};
		IntArray2ByteArray(tmp, 0, capk05.CheckSum, 0, tmp.length);
		
		
		//init capk06  CUP 1024 bits Test Key 02
		capk06 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk06.RID, 0, tmp.length);
		capk06.KeyID = 0x03;
		capk06.HashInd = 0x01;
		capk06.ArithInd = 0x01;
		capk06.ModulLen = (byte) 128;
		tmp = new int[]{0xE2,0x3B,0xB3,0xC8,0xE5,0x65,0x9F,0x19,0xFC,0x7B,0xC4,0xDC,0x7A,0xAE,0xD1,0x86,
                0xD7,0xC0,0x6B,0xA2,0x66,0x20,0x6C,0x45,0xA8,0x7C,0x63,0xCA,0x8E,0xEE,0x8D,0x27,
                0x3F,0x70,0x35,0xE3,0x3F,0x94,0x8A,0xD1,0x00,0xA5,0x1A,0xD0,0x5B,0x71,0x43,0x41,
                0xBB,0x31,0x14,0x81,0x28,0x2A,0x0C,0x6A,0x6D,0x4A,0x09,0xF1,0x54,0xB9,0xE6,0x08,
                0x80,0x7F,0x1F,0xAE,0x15,0xD7,0x8D,0x68,0x1F,0xA8,0xB1,0xA3,0x8F,0xB7,0xF6,0x9C,
                0xB5,0x62,0x17,0x12,0xC6,0xBA,0x49,0xD0,0xE0,0x81,0x0D,0xE9,0x47,0xB8,0xCC,0x03,
                0x64,0x31,0x6F,0x5B,0xBA,0x2D,0x3A,0x30,0x4F,0xB9,0x7A,0x9F,0x9C,0xB1,0xC2,0x64,
                0x46,0x7C,0xE3,0x6A,0x84,0xC0,0x1D,0x4E,0x42,0x73,0xC8,0x93,0xE5,0xC7,0xF8,0x4B};
		IntArray2ByteArray(tmp, 0, capk06.Modul, 0, tmp.length);
		capk06.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk06.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk06.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk06.CheckSum, 0, tmp.length);
		
		
		//init capk07   CUP 1024 bits Test Key 03
		capk07 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk07.RID, 0, tmp.length);
		capk07.KeyID = 0x06;
		capk07.HashInd = 0x01;
		capk07.ArithInd = 0x01;
		capk07.ModulLen = (byte) 128;
		tmp = new int[]{0xD2,0xC4,0xD6,0x3B,0x23,0xB8,0xAB,0x4E,0xFF,0xBF,0x02,0xF0,0x04,0x8A,0x9E,0x87,
                0x68,0x95,0x30,0x2C,0xDF,0xEF,0x38,0xFF,0x89,0xD3,0x63,0x3C,0x68,0x52,0x56,0xA0,
                0x6F,0x3B,0x84,0xF6,0x67,0x0A,0x1F,0x6D,0x36,0x3F,0x83,0xE1,0x07,0x09,0xD7,0xC3,
                0x49,0x1A,0xED,0x1B,0x87,0xFF,0x66,0x3B,0x3D,0x5A,0xCE,0xA0,0x3A,0xF5,0xFA,0x4D,
                0x41,0x5C,0xCA,0x6B,0x79,0x37,0x62,0x2B,0x5F,0x51,0xAC,0x1A,0x8C,0x9C,0x77,0x0A,
                0xB7,0xF4,0xC4,0x34,0xCE,0xB6,0x27,0x63,0xD4,0x09,0xAE,0xB8,0x0F,0xCF,0x83,0xEA,
                0x74,0xD9,0x70,0x15,0x64,0x7C,0x4F,0x36,0x15,0x58,0x96,0x75,0xE8,0x30,0x0A,0xEA,
                0x21,0x83,0xD6,0x9F,0x52,0x8F,0x46,0x89,0x9B,0x41,0x09,0x92,0xF1,0xC6,0x89,0x53};
		IntArray2ByteArray(tmp, 0, capk07.Modul, 0, tmp.length);
		capk07.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk07.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk07.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk07.CheckSum, 0, tmp.length);
		
		
		//init capk08   CUP 1024 bits Test Key 04
		capk08 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk08.RID, 0, tmp.length);
		capk08.KeyID = 0x09;
		capk08.HashInd = 0x01;
		capk08.ArithInd = 0x01;
		capk08.ModulLen = (byte) 128;
		tmp = new int[]{0xA9,0xAE,0xCE,0xB5,0x29,0xA7,0x14,0xC1,0x4A,0xCC,0xEC,0xCB,0xE5,0x32,0x8A,0x8A,
                0x0C,0x6D,0x4E,0xCF,0x49,0x0D,0xC5,0x0C,0xA3,0xE4,0xA2,0x1D,0xC6,0xE4,0x21,0x06,
                0xC9,0x3A,0x0C,0x7B,0x6D,0xD5,0x59,0xAE,0xD9,0x8B,0x67,0x2E,0x38,0x22,0xB0,0x43,
                0x76,0x8E,0xB4,0xFD,0x7E,0xAD,0x0D,0xAD,0x39,0xCE,0x0B,0x56,0x30,0x44,0xE3,0x14,
                0x03,0xA0,0x93,0x46,0x1E,0x4F,0x59,0xEA,0x69,0x54,0xC9,0x10,0xB5,0x73,0xE7,0xA3,
                0xBF,0x58,0x41,0x47,0xD1,0x2F,0xE3,0x39,0xCE,0x73,0x98,0x0B,0x23,0x7D,0xA1,0xA3,
                0x13,0x1E,0xDA,0x01,0xA2,0xD8,0x8A,0xAD,0x17,0xB4,0x99,0x70,0x49,0x07,0x23,0xA9,
                0x2F,0x54,0xBA,0x30,0x68,0x5C,0x13,0xFF,0x90,0xBB,0xD5,0xBA,0xE2,0x82,0x61,0x13};
		IntArray2ByteArray(tmp, 0, capk08.Modul, 0, tmp.length);
		capk08.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk08.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk08.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk08.CheckSum, 0, tmp.length);
		
		
		//init capk09   CUP 1024 bits Test Key 05
		capk09 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk09.RID, 0, tmp.length);
		capk09.KeyID = 0x0c;
		capk09.HashInd = 0x01;
		capk09.ArithInd = 0x01;
		capk09.ModulLen = (byte) 128;
		tmp = new int[]{0xA8,0xB5,0x61,0xE3,0x70,0xA2,0xBC,0x23,0x0A,0xEC,0x62,0x46,0xEC,0x5D,0xC3,0x3F,
                0x65,0x06,0xC1,0x42,0x12,0xAB,0x6D,0x14,0x6A,0xA0,0xB1,0x8B,0xE8,0x6A,0xDE,0x35,
                0x32,0x7E,0x2C,0x5D,0x2C,0x34,0xEB,0x61,0x2E,0x6F,0x17,0x09,0xB6,0xAA,0xB3,0x11,
                0x9E,0x95,0x67,0xAB,0x47,0xF2,0x4A,0x8A,0xC1,0x0D,0x85,0xE2,0xC8,0xC7,0x9B,0x97,
                0x1D,0x34,0x8D,0x18,0x46,0x30,0x5D,0xA2,0x89,0xB2,0x4A,0x47,0x84,0xD0,0x44,0xC2,
                0x25,0x33,0x53,0x9A,0x2A,0xA2,0x8D,0x4D,0x55,0x8C,0x0A,0xAD,0xDB,0x70,0x4A,0x8D,
                0x22,0xA2,0x49,0x2A,0xB7,0x43,0xED,0x0F,0x1D,0x5D,0x4D,0x51,0xD7,0x12,0xAF,0xA1,
                0x04,0xBC,0x48,0x66,0x74,0xFC,0x31,0xB3,0x32,0xAD,0xE7,0x80,0xFD,0xAA,0x96,0xDD};
		IntArray2ByteArray(tmp, 0, capk09.Modul, 0, tmp.length);
		capk09.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk09.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk09.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk09.CheckSum, 0, tmp.length);
		
		
		//init capk10   CUP 1024 bits Test Key 06
		capk10 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk10.RID, 0, tmp.length);
		capk10.KeyID = 0x0f;
		capk10.HashInd = 0x01;
		capk10.ArithInd = 0x01;
		capk10.ModulLen = (byte) 128;
		tmp = new int[]{0xBA,0x92,0x51,0xFE,0x0D,0x6F,0x26,0xFF,0x17,0x67,0x67,0x95,0x0B,0xD9,0x1B,0xAC,
                0xE9,0x09,0xC9,0x7E,0xB7,0x22,0xD5,0xE2,0x8D,0x30,0xC1,0xD5,0x3D,0x5F,0x77,0xB5,
                0xA0,0xEC,0x30,0x48,0x88,0x0D,0xEE,0x3B,0xDD,0x99,0x8C,0x3A,0xE8,0x6C,0x98,0x8E,
                0x64,0x0F,0x56,0x28,0x5D,0x42,0xC5,0x30,0x88,0xAD,0x89,0x60,0x9D,0xB5,0x3C,0x30,
                0x4D,0x72,0x46,0xDE,0xD8,0xB2,0xA4,0x88,0x69,0x14,0xA8,0x08,0x70,0xB6,0x32,0xDA,
                0x18,0x06,0x65,0xB1,0xB3,0xDF,0x6B,0x28,0x87,0xD1,0x69,0x4F,0xC3,0xDC,0x73,0x2F,
                0x9B,0xE4,0x71,0xB6,0x2E,0xFF,0xA5,0x21,0x03,0x51,0xF9,0x8B,0x69,0xE1,0xD9,0xFE,
                0x1D,0x1A,0x37,0x66,0x59,0x95,0xB8,0x5C,0x68,0x8C,0xB8,0xD9,0x3E,0x25,0xDB,0xEB};
		IntArray2ByteArray(tmp, 0, capk10.Modul, 0, tmp.length);
		capk10.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk10.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk10.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk10.CheckSum, 0, tmp.length);
		
		
		//init capk11   CUP 1024 bits Test Key 07
		capk11 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk11.RID, 0, tmp.length);
		capk11.KeyID = 0x12;
		capk11.HashInd = 0x01;
		capk11.ArithInd = 0x01;
		capk11.ModulLen = (byte) 128;
		tmp = new int[]{0xBE,0xD3,0x8F,0xA6,0x4C,0x65,0xCA,0x65,0xDE,0xC8,0xEF,0x44,0x18,0xC2,0xF2,0x38,
                0xFD,0x2A,0x8B,0xE0,0x31,0xFA,0x57,0x1F,0x47,0xA4,0xD4,0x6D,0x62,0x5B,0xC0,0x1F,
                0x6D,0x08,0x35,0x62,0xC1,0xDC,0xC2,0xA0,0xDD,0x0C,0xDA,0xA0,0x72,0x3A,0xA8,0xEA,
                0x75,0xDD,0x65,0x2B,0xE1,0x75,0xEF,0x37,0x35,0xBF,0x0E,0x84,0xAA,0x17,0x75,0xC7,
                0x92,0xBC,0xCD,0x51,0x08,0x84,0x2F,0x1C,0x26,0xDB,0xEB,0x56,0x8E,0x96,0xFA,0xDF,
                0x04,0xD2,0x96,0xC5,0x34,0x47,0xBD,0x06,0xAA,0x9F,0x58,0xC9,0x11,0x98,0x56,0x99,
                0x6F,0x63,0x25,0x51,0xA7,0xA8,0x4E,0xBF,0x5E,0x8D,0x51,0x0E,0xB2,0x8B,0x5A,0xA0,
                0xEC,0x65,0x53,0xF4,0x38,0x82,0x05,0x17,0x13,0xE7,0xC1,0xBD,0x81,0x97,0xFC,0x57};
		IntArray2ByteArray(tmp, 0, capk11.Modul, 0, tmp.length);
		capk11.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk11.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk11.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk11.CheckSum, 0, tmp.length);
		
		
		//init capk12   CUP 1024 bits Test Key 08
		capk12 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk12.RID, 0, tmp.length);
		capk12.KeyID = 0x15;
		capk12.HashInd = 0x01;
		capk12.ArithInd = 0x01;
		capk12.ModulLen = (byte) 128;
		tmp = new int[]{0xBF,0xEC,0xCA,0x54,0xD7,0xF1,0xC7,0xC3,0xA2,0x6D,0x70,0x4A,0x77,0xBC,0x83,0xB0,
                0xB3,0x78,0xF5,0x2C,0xCC,0x85,0xB9,0x6A,0x06,0x26,0x13,0xEC,0xF2,0x43,0x16,0x7D,
                0x3D,0x29,0xBD,0xAA,0x2B,0x59,0x76,0x4C,0x3D,0x09,0x23,0x2B,0xF2,0x3E,0xBF,0xA4,
                0x7E,0x33,0x06,0x59,0xA3,0xAE,0xBF,0xE0,0xA5,0xC7,0x32,0x94,0xD6,0x18,0x37,0xA0,
                0x03,0xFA,0xE7,0xF7,0xF6,0x03,0x85,0xCA,0xDE,0xA1,0x09,0x0D,0x9D,0xCF,0x5D,0xDF,
                0x2F,0x4A,0xE0,0xE1,0x2C,0xA9,0x14,0x5C,0x0D,0xD2,0x70,0x27,0x16,0xA9,0x74,0x13,
                0xFF,0xF6,0xF1,0x19,0xDF,0x8D,0xC7,0x5E,0x29,0x38,0x1F,0x47,0x3A,0x42,0x69,0x1F,
                0xAF,0x11,0xE8,0xD8,0x86,0x05,0xD9,0xD4,0x4D,0xC0,0x7F,0x11,0x40,0x15,0x81,0xF7};
		IntArray2ByteArray(tmp, 0, capk12.Modul, 0, tmp.length);
		capk12.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk12.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk12.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk12.CheckSum, 0, tmp.length);
		
		
		//init capk13   CUP 1024 bits Test Key 09
		capk13 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk13.RID, 0, tmp.length);
		capk13.KeyID = 0x15;
		capk13.HashInd = 0x01;
		capk13.ArithInd = 0x01;
		capk13.ModulLen = (byte) 128;
		tmp = new int[]{0xBF,0xEC,0xCA,0x54,0xD7,0xF1,0xC7,0xC3,0xA2,0x6D,0x70,0x4A,0x77,0xBC,0x83,0xB0,
                0xB3,0x78,0xF5,0x2C,0xCC,0x85,0xB9,0x6A,0x06,0x26,0x13,0xEC,0xF2,0x43,0x16,0x7D,
                0x3D,0x29,0xBD,0xAA,0x2B,0x59,0x76,0x4C,0x3D,0x09,0x23,0x2B,0xF2,0x3E,0xBF,0xA4,
                0x7E,0x33,0x06,0x59,0xA3,0xAE,0xBF,0xE0,0xA5,0xC7,0x32,0x94,0xD6,0x18,0x37,0xA0,
                0x03,0xFA,0xE7,0xF7,0xF6,0x03,0x85,0xCA,0xDE,0xA1,0x09,0x0D,0x9D,0xCF,0x5D,0xDF,
                0x2F,0x4A,0xE0,0xE1,0x2C,0xA9,0x14,0x5C,0x0D,0xD2,0x70,0x27,0x16,0xA9,0x74,0x13,
                0xFF,0xF6,0xF1,0x19,0xDF,0x8D,0xC7,0x5E,0x29,0x38,0x1F,0x47,0x3A,0x42,0x69,0x1F,
                0xAF,0x11,0xE8,0xD8,0x86,0x05,0xD9,0xD4,0x4D,0xC0,0x7F,0x11,0x40,0x15,0x81,0xF7};
		IntArray2ByteArray(tmp, 0, capk13.Modul, 0, tmp.length);
		capk13.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk13.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk13.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk13.CheckSum, 0, tmp.length);
		
		
		//init capk14   CUP 1024 bits Test Key 10
		capk14 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk14.RID, 0, tmp.length);
		capk14.KeyID = 0x1b;
		capk14.HashInd = 0x01;
		capk14.ArithInd = 0x01;
		capk14.ModulLen = (byte) 128;
		tmp = new int[]{0xCB,0xFD,0xA4,0x4E,0x39,0x20,0xF6,0x95,0xC8,0x96,0x66,0x51,0xC1,0x75,0x94,0xBA,
                0xBB,0x36,0x23,0xCB,0xE1,0x47,0x8D,0xFC,0xFF,0xA5,0x8E,0x94,0xA7,0xB5,0xBE,0xB3,
                0x49,0xB3,0x4E,0x0B,0xF1,0x75,0x7F,0xA1,0xA9,0xB4,0x6C,0xC3,0x9C,0xA9,0x7B,0xE5,
                0x04,0xCD,0x4E,0x21,0xE3,0x21,0x62,0x36,0xD6,0x23,0x00,0x17,0xC4,0x98,0x93,0x42,
                0x4E,0x25,0x5F,0xD6,0x53,0xC5,0x02,0x27,0x20,0xAF,0x7F,0x56,0xF5,0x4F,0x5D,0x40,
                0xCC,0xB1,0x24,0xF1,0xD7,0x78,0xD7,0x22,0xAB,0xCD,0xEF,0x57,0x3B,0xC5,0x8F,0x75,
                0xEC,0x4F,0xE7,0x60,0x64,0xC5,0x9F,0x99,0xAB,0x7F,0x07,0x91,0x79,0x61,0x36,0xA9,
                0xF3,0x4C,0x02,0x8E,0xC5,0x08,0xD5,0x95,0x2E,0x1F,0xDD,0x5A,0x9F,0xC6,0x2A,0xED};
		IntArray2ByteArray(tmp, 0, capk14.Modul, 0, tmp.length);
		capk14.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk14.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk14.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk14.CheckSum, 0, tmp.length);
		
		
		//init capk15   CUP 1024 bits Test Key 11
		capk15 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk15.RID, 0, tmp.length);
		capk15.KeyID = 0x1e;
		capk15.HashInd = 0x01;
		capk15.ArithInd = 0x01;
		capk15.ModulLen = (byte) 128;
		tmp = new int[]{0x9F,0x90,0x1D,0x24,0xEB,0xE2,0x1B,0xB0,0x93,0xBB,0x93,0x31,0x93,0xB5,0x67,0x6B,
                0x3A,0xE0,0x9A,0x6E,0x98,0x15,0x41,0x62,0x5A,0xF2,0x92,0x16,0x9D,0x65,0x8C,0xE1,
                0xDC,0x9D,0x39,0xAD,0xDD,0xC8,0x9D,0x6E,0x2B,0xCE,0x69,0x97,0xB6,0xC1,0x2C,0xDF,
                0x67,0x89,0xA5,0x9B,0xA0,0xF8,0xF6,0x4F,0x94,0x22,0x68,0xC0,0xB4,0x26,0xC0,0x53,
                0x53,0x4A,0x7E,0xFE,0x68,0x45,0x4D,0x46,0x7E,0xBB,0x88,0x29,0xE9,0x29,0x38,0x07,
                0x52,0x0F,0x66,0xD0,0x7D,0xD5,0xDF,0xB5,0xFE,0x7C,0x74,0x6D,0xA2,0x9D,0x32,0xCA,
                0x3B,0xAF,0xDD,0x07,0xB3,0x0E,0x53,0xFE,0x5B,0xA3,0x04,0xDC,0xCB,0xA3,0x87,0xA1,
                0x6F,0x89,0xEF,0x76,0x1E,0xBE,0x22,0x4E,0xEE,0x5F,0xDE,0x3A,0x8F,0x26,0x04,0xFB};
		IntArray2ByteArray(tmp, 0, capk15.Modul, 0, tmp.length);
		capk15.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk15.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk15.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk15.CheckSum, 0, tmp.length);
		
		

		//init capk16   CUP 1024 bits Test Key 12
		capk16 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk16.RID, 0, tmp.length);
		capk16.KeyID = 0x21;
		capk16.HashInd = 0x01;
		capk16.ArithInd = 0x01;
		capk16.ModulLen = (byte) 128;
		tmp = new int[]{0xE3,0x07,0xF4,0x59,0x42,0x3B,0xA5,0xC0,0x26,0xFE,0x6D,0xF2,0x66,0x81,0xC1,0x3A,
                0xC2,0x6C,0x5A,0xF1,0xB5,0x0B,0x1D,0x9C,0x7B,0xD1,0x87,0x22,0x3A,0x73,0x38,0xB9,
                0xFD,0x29,0xFC,0x3D,0x65,0x13,0x98,0x8B,0xF7,0x67,0xE6,0xE9,0xE3,0x66,0xC2,0x8F,
                0x9A,0x62,0xBE,0x04,0x11,0x88,0xBF,0x7F,0x46,0xD8,0x47,0xBA,0x4B,0xBB,0x4D,0x38,
                0xAE,0xD6,0x2E,0x6E,0x33,0x96,0xC8,0x2E,0xD1,0x32,0x9C,0x1F,0x23,0x1A,0x57,0xD7,
                0x17,0x9A,0x07,0xC8,0x39,0x2B,0xB7,0xED,0xE6,0xC8,0x98,0xE7,0x92,0x58,0x93,0xCD,
                0xE5,0x3D,0xBE,0xDD,0x25,0xDE,0x43,0x1D,0x42,0x2D,0xAA,0xF5,0xD7,0xE1,0x9B,0x9B,
                0xB9,0xD1,0xEF,0x70,0xC4,0x3A,0x7B,0x77,0x71,0x14,0x6F,0x65,0x18,0x88,0xE3,0x41};
		IntArray2ByteArray(tmp, 0, capk16.Modul, 0, tmp.length);
		capk16.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk16.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk16.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk16.CheckSum, 0, tmp.length);
		
		
		//init capk17   CUP 1024 bits Test Key 13
		capk17 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk17.RID, 0, tmp.length);
		capk17.KeyID = 0x24;
		capk17.HashInd = 0x01;
		capk17.ArithInd = 0x01;
		capk17.ModulLen = (byte) 128;
		tmp = new int[]{0xBD,0xE3,0x0C,0xAC,0xCE,0xF1,0xB8,0xCC,0xCF,0xE6,0x55,0x44,0xC2,0x0D,0xA2,0x8A,
                0xA4,0x2F,0x61,0xEB,0xAB,0x51,0x5B,0x69,0x64,0xFB,0x6A,0xFF,0x97,0x9A,0x11,0x5A,
                0x13,0x70,0xBA,0xD4,0x0A,0x3D,0x32,0xEC,0x37,0x63,0xB7,0x62,0xD2,0xD2,0xD6,0xED,
                0x52,0xB5,0xBC,0xD6,0x0A,0xDA,0xCB,0x9D,0x8F,0x26,0x62,0x11,0xE1,0x4B,0xED,0xD7,
                0x27,0xCE,0xD3,0x15,0xF5,0xF4,0x6B,0x95,0x15,0x4D,0xB9,0xD2,0x7C,0x21,0xC4,0x22,
                0x27,0x80,0x53,0x32,0xE8,0xB9,0xA8,0x14,0x87,0xF7,0x8B,0xB6,0xE7,0xC0,0x57,0x99,
                0x0C,0x53,0x52,0xD3,0x24,0x89,0xC6,0x89,0x5A,0x04,0x18,0x81,0x94,0x2F,0xFD,0xA2,
                0x22,0xD6,0x2C,0x07,0x86,0x8E,0x21,0x0C,0x81,0x8C,0xFF,0x90,0x31,0xB9,0x10,0x27};
		IntArray2ByteArray(tmp, 0, capk17.Modul, 0, tmp.length);
		capk17.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk17.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk17.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk17.CheckSum, 0, tmp.length);
	
		
		//init capk18   CUP 1024 bits Test Key 14
		capk18 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk18.RID, 0, tmp.length);
		capk18.KeyID = 0x27;
		capk18.HashInd = 0x01;
		capk18.ArithInd = 0x01;
		capk18.ModulLen = (byte) 128;
		tmp = new int[]{0xC9,0xAD,0x14,0xFF,0xB1,0xE5,0xCC,0x81,0x71,0xD5,0xD1,0xD9,0xD6,0x7A,0x5A,0xDC,
                0x6A,0x6E,0x95,0x20,0xC7,0xB2,0xB4,0xE1,0x8B,0x5B,0xE6,0x2B,0xF9,0xCB,0xCD,0xC7,
                0xD7,0x42,0x29,0xB4,0xC2,0xB3,0x67,0xDA,0x2D,0x0C,0xDF,0x09,0xF1,0x51,0x08,0xB1,
                0xF7,0xA0,0x52,0x05,0x9A,0x9D,0xC7,0x30,0x60,0x50,0x99,0xC6,0xAF,0x21,0xB7,0xC5,
                0x20,0x37,0xCA,0xC0,0xF9,0xDF,0xA6,0x4C,0x3D,0x7C,0x3A,0x79,0x27,0xD7,0x91,0x6E,
                0x40,0x04,0x7A,0xB9,0xA1,0x65,0x09,0xA8,0x95,0x93,0x18,0xBC,0xBC,0x9D,0x6E,0x43,
                0x0A,0x04,0xE0,0xB4,0xA6,0xA8,0x5F,0x64,0x48,0x98,0x9D,0xB1,0x1D,0x6A,0xF9,0x78,
                0xE1,0xD2,0xFB,0xAF,0xA7,0xB5,0xE4,0x2C,0xC8,0x3A,0xB8,0x33,0x78,0xF8,0x48,0x9F};
		IntArray2ByteArray(tmp, 0, capk18.Modul, 0, tmp.length);
		capk18.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk18.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk18.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk18.CheckSum, 0, tmp.length);
		
		
		
		//init capk19   CUP 1024 bits Test Key 15
		capk19 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk19.RID, 0, tmp.length);
		capk19.KeyID = 0x2A;
		capk19.HashInd = 0x01;
		capk19.ArithInd = 0x01;
		capk19.ModulLen = (byte) 128;
		tmp = new int[]{0xD7,0x1F,0x4C,0x69,0xA0,0x3F,0x7A,0x77,0x9D,0x7E,0x1A,0x09,0x9B,0xE4,0x50,0xAE,
                0x3C,0x40,0x3E,0xE3,0xD0,0x56,0x47,0xF8,0xBE,0xAE,0x23,0xDF,0xBE,0xB8,0xB0,0xD6,
                0xB6,0x95,0x62,0x9A,0x3A,0xB8,0xAB,0x18,0xC9,0x2C,0xE8,0x19,0x56,0xD2,0xF0,0x07,
                0x4A,0xF2,0x17,0xCA,0x76,0xCF,0xC6,0xE3,0xE6,0x13,0xA6,0xF8,0xB1,0x44,0xD4,0xC0,
                0x61,0xC3,0x67,0xC2,0x44,0x90,0x6F,0x84,0xE8,0xEA,0xC7,0xE6,0x55,0x09,0xB2,0xEF,
                0xBF,0x7A,0x48,0xA8,0xCB,0xEF,0xA3,0x8B,0x37,0xA9,0x12,0x79,0xC3,0xBD,0x27,0xF4,
                0x03,0xFA,0xBE,0xA6,0x5B,0x62,0x5F,0xEA,0xD6,0x56,0x05,0x99,0x1C,0x2E,0x6A,0x1F,
                0x65,0x95,0xAC,0xA2,0x7F,0x78,0x15,0xAB,0x7D,0x19,0x86,0xE9,0x65,0xEE,0xA7,0x5D};
		IntArray2ByteArray(tmp, 0, capk19.Modul, 0, tmp.length);
		capk19.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk19.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk19.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk19.CheckSum, 0, tmp.length);
		
		
		//init capk20   CUP 1024 bits Test Key 16
		capk20 = new EMV_CAPK();
		tmp = new int[]{0xD1,0x56,0x00,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk20.RID, 0, tmp.length);
		capk20.KeyID = 0x2D;
		capk20.HashInd = 0x01;
		capk20.ArithInd = 0x01;
		capk20.ModulLen = (byte) 144;
		tmp = new int[]{0xCB,0x3F,0x75,0x89,0x3A,0xF6,0x3E,0xDE,0x19,0xF0,0x9E,0x67,0x4D,0xEC,0xBC,0x51,
                0x7B,0x3E,0x51,0x68,0x40,0x65,0xD3,0xD4,0x1C,0xC4,0x65,0xEC,0xF2,0xFE,0xDF,0xBF,
                0x6A,0xFD,0x7F,0x10,0x5F,0xB0,0x99,0x3A,0x21,0x04,0x95,0x92,0xE8,0xDE,0xFB,0xE6,
                0x66,0x6B,0x8C,0x88,0xDC,0x79,0xCC,0x93,0xA6,0xA5,0x5B,0x09,0x6D,0xD7,0x73,0xBE,
                0x16,0xDE,0x85,0x07,0x71,0xAF,0xB3,0x0F,0xB0,0xF0,0x9D,0x02,0x64,0xAA,0x91,0xE2,
                0x4D,0x46,0x70,0x04,0x87,0xB8,0x84,0x85,0x9B,0x9D,0x36,0xC9,0x9C,0x66,0xC3,0x63,
                0x4F,0xD5,0x51,0xCE,0xCC,0xAD,0xAD,0x33,0x54,0x63,0xCF,0x4F,0xD1,0x92,0x63,0xAC,
                0x29,0x50,0x80,0x3D,0x57,0x91,0xA6,0xD7,0x06,0x86,0x68,0x2E,0x90,0xB8,0xFB,0x26,
                0x25,0x12,0x9C,0xC7,0x32,0x8B,0xC8,0x53,0x25,0x24,0xEE,0x94,0x24,0x40,0xCF,0x3F};
		IntArray2ByteArray(tmp, 0, capk20.Modul, 0, tmp.length);
		capk20.ExpLen = 3;
		tmp = new int[]{0x01,0x00,0x01};
		IntArray2ByteArray(tmp, 0, capk20.Exp, 0, tmp.length);
		tmp = new int[]{0x05,0x10,0x31};
		IntArray2ByteArray(tmp, 0, capk20.ExpDate, 0, tmp.length);
		tmp = new int[]{0x76,0x16,0xE9,0xAC,0x8B,0xE0,0x14,0xAF,0x88,0xCA,0x11,0xA8,0xFB,0x17,0x96,0x7B,
                0x73,0x94,0x03,0x0E};
		IntArray2ByteArray(tmp, 0, capk20.CheckSum, 0, tmp.length);
		
		gCapklist = new EMV_CAPK[MAX_CAPK_NUM];
		gCapklist[0] = new EMV_CAPK();
		CopyCAPK(capk01, gCapklist[0]);
		gCapklist[1] = new EMV_CAPK();
		CopyCAPK(capk02, gCapklist[1]);
		gCapklist[2] = new EMV_CAPK();
		CopyCAPK(capk03, gCapklist[2]);
		gCapklist[3] = new EMV_CAPK();
		CopyCAPK(capk04, gCapklist[3]);
		gCapklist[4] = new EMV_CAPK();
		CopyCAPK(capk05, gCapklist[4]);
		gCapklist[5] = new EMV_CAPK();
		CopyCAPK(capk06, gCapklist[5]);
		gCapklist[6] = new EMV_CAPK();
		CopyCAPK(capk07, gCapklist[6]);
		gCapklist[7] = new EMV_CAPK();
		CopyCAPK(capk08, gCapklist[7]);
		gCapklist[8] = new EMV_CAPK();
		CopyCAPK(capk09, gCapklist[8]);
		gCapklist[9] = new EMV_CAPK();
		CopyCAPK(capk10, gCapklist[9]);
		gCapklist[10] = new EMV_CAPK();
		CopyCAPK(capk11, gCapklist[10]);
		gCapklist[11] = new EMV_CAPK();
		CopyCAPK(capk12, gCapklist[11]);
		gCapklist[12] = new EMV_CAPK();
		CopyCAPK(capk13, gCapklist[12]);
		gCapklist[13] = new EMV_CAPK();
		CopyCAPK(capk14, gCapklist[13]);
		gCapklist[14] = new EMV_CAPK();
		CopyCAPK(capk15, gCapklist[14]);
		gCapklist[15] = new EMV_CAPK();
		CopyCAPK(capk16, gCapklist[15]);
		gCapklist[16] = new EMV_CAPK();
		CopyCAPK(capk17, gCapklist[16]);
		gCapklist[17] = new EMV_CAPK();
		CopyCAPK(capk18, gCapklist[17]);
		gCapklist[18] = new EMV_CAPK();
		CopyCAPK(capk19, gCapklist[18]);
		gCapklist[19] = new EMV_CAPK();
		CopyCAPK(capk20, gCapklist[19]);
	}
	
	private int CopyCAPK(EMV_CAPK stCAPK1, EMV_CAPK stCAPK2)
	{
		int len = 0;
		
		System.arraycopy(stCAPK1.RID, 0, stCAPK2.RID, 0, stCAPK1.RID.length);
		len += stCAPK1.RID.length;
		stCAPK2.KeyID = stCAPK1.KeyID;
		len++;
		stCAPK2.HashInd = stCAPK1.HashInd;
		len++;
		stCAPK2.ArithInd = stCAPK1.ArithInd;
		len++;
		stCAPK2.ModulLen = stCAPK1.ModulLen;
		len++;
		System.arraycopy(stCAPK1.Modul, 0, stCAPK2.Modul, 0, stCAPK1.Modul.length);
		len += stCAPK1.Modul.length;
		stCAPK2.ExpLen = stCAPK1.ExpLen;
		len++;
		System.arraycopy(stCAPK1.Exp, 0, stCAPK2.Exp, 0, stCAPK1.Exp.length);
		len += stCAPK1.Exp.length;
		System.arraycopy(stCAPK1.ExpDate, 0, stCAPK2.ExpDate, 0, stCAPK1.ExpDate.length);
		len += stCAPK1.ExpDate.length;
		System.arraycopy(stCAPK1.CheckSum, 0, stCAPK2.CheckSum, 0, stCAPK1.CheckSum.length);
		len += stCAPK1.CheckSum.length;
		
		return len;
	}
	
	private int SimulStart (byte uPort)
	{
		try{
			portmg.portOpen(uPort,"115200,8,N,1");
		}catch(Exception exception){
			return IFD_ConnectError;
		}
		return IFD_OK;
	}
	
	private int SimulEnd (byte uPort)
	{
		try {
			portmg.portClose(uPort);
		} catch (Exception exception) {
			return IFD_ConnectError;
		}
		return IFD_OK;		
	}

	private int RecvCmd(byte iPort, byte[] RcvBuf)
	{
	    int  i, Len;
	    byte LRC;
	    byte tmp[];
	
	
		//PortReset(iPort);//¼ÓÉÏÕâ¾ä»°¾Í²»ÄÜ½ÓÊÕR50µÄÊý¾ÝÁË£¬Òò´ËÆÁ±Î´Ê¾ä
	
		while(true)
		{
			try {
				RcvBuf = portmg.portRecvs(iPort, 1, 500);
			} catch (Exception exception) {
				return IFD_ConnectError;
			}
			if(RcvBuf[0] == STX)
			{
				break;
			}
		}
	
		
		//get the next 2 char
	    for (i = 1; i < 3; i++)   //DATA LEN
		{
	    	try{
				tmp = portmg.portRecvs(iPort, 1, 200);
				System.arraycopy(tmp, 0, RcvBuf, i, 1);
			}catch(Exception exception){
				SimulStart(iPort);
				return 0xf0;
			}
		}
	
		//check the length is valid
		Len = RcvBuf[1] * 256 + RcvBuf[2];
	    if (Len > 10239) 
		{
			SimulEnd(iPort);
			return 0xf1;
		}
	
		
		//LRC code check, receiving data
	    LRC = (byte)(RcvBuf[1] ^ RcvBuf[2]);
	    for (i = 0; i < Len + 1; i++) 
		{
	    	try{
				tmp = portmg.portRecvs(iPort, 1, 200);
				System.arraycopy(tmp, 0, RcvBuf, i, 1);
			}catch(Exception exception){
				SimulEnd(iPort);
				return 0xf2;
			}
			
	        LRC ^= RcvBuf[i];
	    }
	
	    if (LRC != 0)
		{
	    	try {
				portmg.portClose(iPort);
			} catch (Exception exception) {
				return IFD_ConnectError;
			}
			return 0xf3;
		}
	    SimulEnd(iPort);
	    return 0;
	}
	
	private int SendResp(byte iPort, byte[] SendBuf, int OutLen)
	{
		byte LRC, TmpBuf[], SendTmp[];
	    int i;
	
	    SendTmp = new byte[1];
	    TmpBuf = new byte[3];
	    TmpBuf[0] = STX;
	    TmpBuf[1] = (byte)(OutLen / 256);
	    TmpBuf[2] = (byte)(OutLen % 256);
	
		SimulStart(iPort);
		try {
			portmg.portSends(iPort, TmpBuf);
		} catch (Exception exception) {
			return IFD_ConnectError;
		}
		
	    LRC = (byte)(TmpBuf[1] ^ TmpBuf[2]);
	    for (i = 0; i < OutLen; i++) 
		{
	    	try{
				System.arraycopy(SendBuf, i, SendTmp, 0, 1);
				portmg.portSends(iPort, SendTmp);
			}catch(Exception exception){
				SimulEnd(iPort);
				return IFD_ConnectError;
			}
	        
	        LRC ^= SendBuf[i];
	    }
	    
	    try{
	    	SendTmp[0] = LRC;
			portmg.portSends(iPort, SendTmp);
		}catch(Exception exception){
			SimulEnd(iPort);
			return IFD_ConnectError;
		}
	   
		return IFD_OK;
	}

	//µÚÒ»¸ö²ÎÊýºÍµÚ¶þ¸ö²ÎÊý£¬ÊÇ·¢ËÍ£¬ÊäÈë²ÎÊý
	//µÚÈý¸ö²ÎÊýºÍµÚËÄ¸ö²ÎÊý£¬ÊÇ½ÓÊÕ£¬Êä³ö²ÎÊý
	int POS_SendRecv(byte[] sendstr, int sendlen, byte[] rcvstr, int RcvTimeOut)
	{
		int j=0;


		for (j=0;j<3;j++)
		{
			SystemClock.sleep(10);
			if (SendResp(glPort,sendstr,sendlen)==0)
			{
				break;
			}
		
		}

		for (j=0;j<3;j++)
		{
		   //	DelayMs(10);
			//½ÓÊÕÊý¾ÝÖ®Ç°×îºÃÏÈÇå¿Õ½ÓÊÕ»º³åÇø
			if (RecvCmd(glPort,rcvstr)==0)
			{
				break;
			}
		
		}

		if (j==3)
		{
			return IFD_ConnectError;
		}

		return 0;
	}
	
	private void CopyPreProcInfo(CLSS_PRE_PROC_INFO info1, CLSS_PRE_PROC_INFO info2)
	{
		info2.ulTermFLmt = info1.ulTermFLmt;
		info2.ulRdClssTxnLmt = info1.ulRdClssTxnLmt;
		info2.ulRdCVMLmt = info1.ulRdCVMLmt;
		info2.ulRdClssFLmt = info1.ulRdClssFLmt; 
		info2.ucKernType = info1.ucKernType; 	
		info2.ucCrypto17Flg = info1.ucCrypto17Flg;
		info2.ucZeroAmtNoAllowed = info1.ucZeroAmtNoAllowed;
		info2.ucStatusCheckFlg = info1.ucStatusCheckFlg;
		System.arraycopy(info1.aucReaderTTQ, 0, info2.aucReaderTTQ, 0, info1.aucReaderTTQ.length);
		info2.ucTermFLmtFlg = info1.ucTermFLmtFlg;
		info2.ucRdClssTxnLmtFlg = info1.ucRdClssTxnLmtFlg;
		info2.ucRdCVMLmtFlg = info1.ucRdCVMLmtFlg;
		info2.ucRdClssFLmtFlg = info1.ucRdClssFLmtFlg;
	}
	
	private void InitAID()
	{
		int tmp[];

		// Clss_SetPreProcInfo_Entry
		gClss_PreProcInfo = new CLSS_PRE_PROC_INFO();

		//for PreProcInfo, relevent with APP
		gClss_PreProcInfo.ulTermFLmt = 30000;
		gClss_PreProcInfo.ulRdClssTxnLmt = 50000; //this one is for contactless card, 
		//once transaction amomunt is over than this value, tip "using contact card" automatically
		gClss_PreProcInfo.ulRdCVMLmt = 20000;
		gClss_PreProcInfo.ulRdClssFLmt = 30000; 
		gClss_PreProcInfo.ucKernType = KERNTYPE_DEF; 	
		gClss_PreProcInfo.ucCrypto17Flg = 1;
		gClss_PreProcInfo.ucZeroAmtNoAllowed = 0;
		gClss_PreProcInfo.ucStatusCheckFlg = 0;
		tmp = new int[]{0x36, 0x00, 0x00, 0x00};
		IntArray2ByteArray(tmp, 0, gClss_PreProcInfo.aucReaderTTQ, 0, tmp.length);
		gClss_PreProcInfo.ucTermFLmtFlg = 1;
		gClss_PreProcInfo.ucRdClssTxnLmtFlg = 0;
		gClss_PreProcInfo.ucRdCVMLmtFlg = 1;
		gClss_PreProcInfo.ucRdClssFLmtFlg=1;

		VISA_VSDC_APP = new CLSS_PRE_PROC_INFO();
		CopyPreProcInfo(gClss_PreProcInfo, VISA_VSDC_APP);
		tmp = new int[]{0xA0, 0x00, 0x00, 0x00, 0x03, 0x10, 0x10};
		IntArray2ByteArray(tmp, 0, VISA_VSDC_APP.aucAID, 0, tmp.length);
		VISA_VSDC_APP.ucAidLen = 7;

		VISA_ELECTRON_APP = new CLSS_PRE_PROC_INFO();
		CopyPreProcInfo(gClss_PreProcInfo, VISA_ELECTRON_APP);
		tmp = new int[]{0xA0, 0x00, 0x00, 0x00, 0x03, 0x20, 0x10};
		IntArray2ByteArray(tmp, 0, VISA_ELECTRON_APP.aucAID, 0, tmp.length);
		VISA_ELECTRON_APP.ucAidLen = 7;

		MASTER_MCHIP = new CLSS_PRE_PROC_INFO();
		CopyPreProcInfo(gClss_PreProcInfo, MASTER_MCHIP);
		tmp = new int[]{0xA0, 0x00, 0x00, 0x00, 0x04, 0x10, 0x10};
		IntArray2ByteArray(tmp, 0, MASTER_MCHIP.aucAID, 0, tmp.length);
		MASTER_MCHIP.ucAidLen = 7;

		MASTER_MAESTRO_APP = new CLSS_PRE_PROC_INFO();
		CopyPreProcInfo(gClss_PreProcInfo, MASTER_MAESTRO_APP);
		tmp = new int[]{0xA0, 0x00, 0x00, 0x00, 0x04, 0x30, 0x60};
		IntArray2ByteArray(tmp, 0, MASTER_MAESTRO_APP.aucAID, 0, tmp.length);
		MASTER_MAESTRO_APP.ucAidLen = 7;

		MASTER_CIRRUS_APP = new CLSS_PRE_PROC_INFO();
		CopyPreProcInfo(gClss_PreProcInfo, MASTER_CIRRUS_APP);
		tmp = new int[]{0xA0, 0x00, 0x00, 0x00, 0x04, 0x60, 0x00};
		IntArray2ByteArray(tmp, 0, MASTER_CIRRUS_APP.aucAID, 0, tmp.length);
		MASTER_CIRRUS_APP.ucAidLen = 7;
	}
	
	private void InitClssParam(CLSS_READER_PARAM ClssParam)
	{
		int tmp[];

		tmp = new int[]{0xe0, 0xe1, 0xc8};
		IntArray2ByteArray(tmp, 0, ClssParam.aucTmCap, 0, tmp.length);
		tmp = new int[]{0xe0, 0x00, 0xf0, 0xa0, 0x01};
		IntArray2ByteArray(tmp, 0, ClssParam.aucTmCapAd, 0, tmp.length);
		ClssParam.ucTmType = 0x22;
		tmp = new int[]{0x08, 0x40};
		IntArray2ByteArray(tmp, 0, ClssParam.aucTmCntrCode, 0, tmp.length);
		tmp = new int[]{0x08, 0x40};
		IntArray2ByteArray(tmp, 0, ClssParam.aucTmRefCurCode, 0, tmp.length);
		tmp = new int[]{0x08, 0x40};
		IntArray2ByteArray(tmp, 0, ClssParam.aucTmTransCur, 0, tmp.length);
	}

	private void InitClssParamMC(CLSS_READER_PARAM_MC ClssParamMC)
	{
		int tmp[];

		ClssParamMC.ucTmType = 0x22;
		tmp = new int[]{0xe0, 0xe1, 0xc8};
		IntArray2ByteArray(tmp, 0, ClssParamMC.aucTmCap, 0, tmp.length);
		tmp = new int[]{0xe0, 0x00, 0xf0, 0xa0, 0x01};
		IntArray2ByteArray(tmp, 0, ClssParamMC.aucTmCapAd, 0, tmp.length);
		ClssParamMC.ucTmCntrCodeFlg = 1;
		tmp = new int[]{0x08, 0x40};
		IntArray2ByteArray(tmp, 0, ClssParamMC.aucTmCntrCode, 0, tmp.length);
	    ClssParamMC.ucTmTransCurFlg = 1;
		tmp = new int[]{0x08, 0x40};
		IntArray2ByteArray(tmp, 0, ClssParamMC.aucTmTransCur, 0, tmp.length);
	}

	private void InitClssAidParamMC(CLSS_MC_AID_PARAM_MC ClssAidParamMC)
	{
		int tmp[];

		ClssAidParamMC.Threshold = 0;
		ClssAidParamMC.usUDOLLen = 3;
		tmp = new int[]{0x9f, 0x6a, 0x04};
		IntArray2ByteArray(tmp, 0, ClssAidParamMC.uDOL, 0, tmp.length);
		ClssAidParamMC.TargetPer = 0;         
		ClssAidParamMC.MaxTargetPer	= 0;  
		ClssAidParamMC.FloorLimitCheck = 1; 
		ClssAidParamMC.RandTransSel	= 1; 
		ClssAidParamMC.VelocityCheck = 1;
		tmp = new int[]{0xff, 0xff, 0xff, 0xff, 0xff};
		IntArray2ByteArray(tmp, 0, ClssAidParamMC.TACDenial, 0, tmp.length);
		tmp = new int[]{0xf8, 0x50, 0xac, 0xf8, 0x00};
		IntArray2ByteArray(tmp, 0, ClssAidParamMC.TACOnline, 0, tmp.length);
		tmp = new int[]{0xfc, 0x50, 0xac, 0xa0, 0x00};
		IntArray2ByteArray(tmp, 0, ClssAidParamMC.TACDefault, 0, tmp.length);
		tmp = new int[]{0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
		IntArray2ByteArray(tmp, 0, ClssAidParamMC.AcquirerId, 0, tmp.length);
		tmp = new int[]{0x03, 0x9f, 0x37, 0x04};
		IntArray2ByteArray(tmp, 0, ClssAidParamMC.dDOL, 0, tmp.length);
		tmp = new int[]{0x0F, 0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02, 0x9A, 0x03, 0x9C, 0x01, 0x95, 0x05, 0x9F, 0x37, 0x04};
		IntArray2ByteArray(tmp, 0, ClssAidParamMC.tDOL, 0, tmp.length);
		tmp = new int[]{0x00, 0x00};
		IntArray2ByteArray(tmp, 0, ClssAidParamMC.Version, 0, tmp.length);
		ClssAidParamMC.ForceOnline = 0;
		tmp = new int[]{0x00, 0x00};
		IntArray2ByteArray(tmp, 0, ClssAidParamMC.MagAvn, 0, tmp.length);
		ClssAidParamMC.ucMagSupportFlg = 1;
	}

	private void InitClssTMConfigMC(CLSS_TERM_CONFIG_MC ClssTMConfigMC)
	{
		int tmp[];

		ClssTMConfigMC.ucMaxLifeTimeTornFlg = 1;
		tmp = new int[]{0x01, 0x2c};
		IntArray2ByteArray(tmp, 0, ClssTMConfigMC.aucMaxLifeTimeTorn, 0, tmp.length);
		ClssTMConfigMC.ucMaxNumberTornFlg = 1;
		ClssTMConfigMC.ucMaxNumberTorn = 0x00;
		ClssTMConfigMC.ucHoldTimeValue = 0x0D;
		ClssTMConfigMC.ucHoldTimeValueFlg = 1;
		ClssTMConfigMC.ucKernelIDFlg = 1;
		ClssTMConfigMC.ucKernelID = 0x02;
	}
	
	private void InitProcInfo(POSLOG stProcInfo)
	{
		int tmp[];
		byte by[];
		
		stProcInfo.uiTimeOut = 1000;
		stProcInfo.ucPreTransType = 0x20;
		by = "123456".getBytes();
		System.arraycopy(by, 0, stProcInfo.ulSTAN, 0, by.length);
		by = "100".getBytes();
		System.arraycopy(by, 0, stProcInfo.szAmount, 0, by.length);
		by = "20140112152615".getBytes();
		System.arraycopy(by, 0, stProcInfo.szDateTime, 0, by.length);
		stProcInfo.stTranCurrency.ucDecimal = 2;
		stProcInfo.stTranCurrency.ucIgnoreDigit = 0;
		tmp = new int[]{0x08, 0x40};
		IntArray2ByteArray(tmp, 0, stProcInfo.stTranCurrency.sCountryCode, 0, tmp.length);
		tmp = new int[]{0x08, 0x40};
		IntArray2ByteArray(tmp, 0, stProcInfo.stTranCurrency.sCurrencyCode, 0, tmp.length);
		by = "12345678".getBytes();
		System.arraycopy(by, 0, stProcInfo.uPosTID, 0, by.length);
		by = "0123456789".getBytes();
		System.arraycopy(by, 0, stProcInfo.uMerchantID, 0, by.length);
	}
	
	private void CopyProcInfo(SYS_PROC_INFO stProcInfo1, SYS_PROC_INFO stProcInfo2)
	{
		System.arraycopy(stProcInfo1.szTrack1, 0, stProcInfo2.szTrack1, 0, stProcInfo1.szTrack1.length);
		System.arraycopy(stProcInfo1.szTrack2, 0, stProcInfo2.szTrack2, 0, stProcInfo1.szTrack2.length);
		System.arraycopy(stProcInfo1.szTrack3, 0, stProcInfo2.szTrack3, 0, stProcInfo1.szTrack3.length);
		stProcInfo2.iClssStatus = stProcInfo1.iClssStatus;
		stProcInfo2.stTranLog.ucTranType = stProcInfo1.stTranLog.ucTranType;
		stProcInfo2.stTranLog.ucOrgTranType = stProcInfo1.stTranLog.ucOrgTranType;
		System.arraycopy(stProcInfo1.stTranLog.szPan, 0, stProcInfo2.stTranLog.szPan, 0, stProcInfo1.stTranLog.szPan.length);
		System.arraycopy(stProcInfo1.stTranLog.szExpDate, 0, stProcInfo2.stTranLog.szExpDate, 0, stProcInfo1.stTranLog.szExpDate.length);
		System.arraycopy(stProcInfo1.stTranLog.szAmount, 0, stProcInfo2.stTranLog.szAmount, 0, stProcInfo1.stTranLog.szAmount.length);
		System.arraycopy(stProcInfo1.stTranLog.szTipAmount, 0, stProcInfo2.stTranLog.szTipAmount, 0, stProcInfo1.stTranLog.szTipAmount.length);
		System.arraycopy(stProcInfo1.stTranLog.szOrgAmount, 0, stProcInfo2.stTranLog.szOrgAmount, 0, stProcInfo1.stTranLog.szOrgAmount.length);
		System.arraycopy(stProcInfo1.stTranLog.szDateTime, 0, stProcInfo2.stTranLog.szDateTime, 0, stProcInfo1.stTranLog.szDateTime.length);
		System.arraycopy(stProcInfo1.stTranLog.szAuthCode, 0, stProcInfo2.stTranLog.szAuthCode, 0, stProcInfo1.stTranLog.szAuthCode.length);
		System.arraycopy(stProcInfo1.stTranLog.szRspCode, 0, stProcInfo2.stTranLog.szRspCode, 0, stProcInfo1.stTranLog.szRspCode.length);
		System.arraycopy(stProcInfo1.stTranLog.szRRN, 0, stProcInfo2.stTranLog.szRRN, 0, stProcInfo1.stTranLog.szRRN.length);
		System.arraycopy(stProcInfo1.stTranLog.szHolderName, 0, stProcInfo2.stTranLog.szHolderName, 0, stProcInfo1.stTranLog.szHolderName.length);
		System.arraycopy(stProcInfo1.stTranLog.stTranCurrency.szName, 0, stProcInfo2.stTranLog.stTranCurrency.szName, 0, stProcInfo1.stTranLog.stTranCurrency.szName.length);
		System.arraycopy(stProcInfo1.stTranLog.stTranCurrency.sCurrencyCode, 0, stProcInfo2.stTranLog.stTranCurrency.sCurrencyCode, 0, stProcInfo1.stTranLog.stTranCurrency.sCurrencyCode.length);
		System.arraycopy(stProcInfo1.stTranLog.stTranCurrency.sCountryCode, 0, stProcInfo2.stTranLog.stTranCurrency.sCountryCode, 0, stProcInfo1.stTranLog.stTranCurrency.sCountryCode.length);
		stProcInfo2.stTranLog.stTranCurrency.ucDecimal = stProcInfo1.stTranLog.stTranCurrency.ucDecimal;
		stProcInfo2.stTranLog.stTranCurrency.ucIgnoreDigit = stProcInfo1.stTranLog.stTranCurrency.ucIgnoreDigit;
		System.arraycopy(stProcInfo1.stTranLog.stHolderCurrency.szName, 0, stProcInfo2.stTranLog.stHolderCurrency.szName, 0, stProcInfo1.stTranLog.stHolderCurrency.szName.length);
		System.arraycopy(stProcInfo1.stTranLog.stHolderCurrency.sCurrencyCode, 0, stProcInfo2.stTranLog.stHolderCurrency.sCurrencyCode, 0, stProcInfo1.stTranLog.stHolderCurrency.sCurrencyCode.length);
		System.arraycopy(stProcInfo1.stTranLog.stHolderCurrency.sCountryCode, 0, stProcInfo2.stTranLog.stHolderCurrency.sCountryCode, 0, stProcInfo1.stTranLog.stHolderCurrency.sCountryCode.length);
		stProcInfo2.stTranLog.stHolderCurrency.ucDecimal = stProcInfo1.stTranLog.stHolderCurrency.ucDecimal;
		stProcInfo2.stTranLog.stHolderCurrency.ucIgnoreDigit = stProcInfo1.stTranLog.stHolderCurrency.ucIgnoreDigit;
		stProcInfo2.stTranLog.uiStatus = stProcInfo1.stTranLog.uiStatus;
		stProcInfo2.stTranLog.uiEntryMode = stProcInfo1.stTranLog.uiEntryMode;
		System.arraycopy(stProcInfo1.stTranLog.szAppLabel, 0, stProcInfo2.stTranLog.szAppLabel, 0, stProcInfo1.stTranLog.szAppLabel.length);
		stProcInfo2.stTranLog.bPanSeqOK = stProcInfo1.stTranLog.bPanSeqOK;
		stProcInfo2.stTranLog.ucPanSeqNo = stProcInfo1.stTranLog.ucPanSeqNo;
		stProcInfo2.stTranLog.ucAidLen = stProcInfo1.stTranLog.ucAidLen;
		System.arraycopy(stProcInfo1.stTranLog.sAppCrypto, 0, stProcInfo2.stTranLog.sAppCrypto, 0, stProcInfo1.stTranLog.sAppCrypto.length);
		System.arraycopy(stProcInfo1.stTranLog.sTVR, 0, stProcInfo2.stTranLog.sTVR, 0, stProcInfo1.stTranLog.sTVR.length);
		System.arraycopy(stProcInfo1.stTranLog.sTSI, 0, stProcInfo2.stTranLog.sTSI, 0, stProcInfo1.stTranLog.sTSI.length);
		System.arraycopy(stProcInfo1.stTranLog.sATC, 0, stProcInfo2.stTranLog.sATC, 0, stProcInfo1.stTranLog.sATC.length);
		stProcInfo2.stTranLog.uiIccDataLen = stProcInfo1.stTranLog.uiIccDataLen;
		System.arraycopy(stProcInfo1.stTranLog.sAID, 0, stProcInfo2.stTranLog.sAID, 0, stProcInfo1.stTranLog.sAID.length);
		System.arraycopy(stProcInfo1.stTranLog.szAppPreferName, 0, stProcInfo2.stTranLog.szAppPreferName, 0, stProcInfo1.stTranLog.szAppPreferName.length);
		System.arraycopy(stProcInfo1.stTranLog.sIccData, 0, stProcInfo2.stTranLog.sIccData, 0, stProcInfo1.stTranLog.sIccData.length);
		stProcInfo2.stTranLog.ulInvoiceNo = stProcInfo1.stTranLog.ulInvoiceNo;
		stProcInfo2.stTranLog.ulSTAN = stProcInfo1.stTranLog.ulSTAN;
		stProcInfo2.stTranLog.ulOrgSTAN = stProcInfo1.stTranLog.ulOrgSTAN;
		System.arraycopy(stProcInfo1.stTranLog.uMerchantID, 0, stProcInfo2.stTranLog.uMerchantID, 0, stProcInfo1.stTranLog.uMerchantID.length);
		System.arraycopy(stProcInfo1.stTranLog.uPosTID, 0, stProcInfo2.stTranLog.uPosTID, 0, stProcInfo1.stTranLog.uPosTID.length);
		System.arraycopy(stProcInfo1.sPinBlock, 0, stProcInfo2.sPinBlock, 0, stProcInfo1.sPinBlock.length);
	}
	
	private int AddParameters(byte[] buff, int offset, int nIndex, byte[] pValue, int len, int type)
	{
		int s_nLastIndex = 0;
		int s_nOffset = 0;
		int s_len = buff.length - offset;
		byte[] s_buff = new byte[s_len];
		
		if(null == buff || null == pValue)
		{
			return -1;
		}

		if (nIndex == 1)	// process the first parameter, so initialize varibales
		{
			s_nLastIndex = 0;
			s_nOffset = 2;		// reserve 2 bytes to hold the total count of parameters
			System.arraycopy(buff, offset, s_buff, 0, s_len);
		}

		// validation check
		if(nIndex != s_nLastIndex + 1)	// add parameters one by one
			return -1;

		s_nLastIndex = nIndex;

		if(buff[offset] != s_buff[0])				// add to the same buffer
			return -1;
		
		// total parameters count
		Utils.short2ByteArray((short)nIndex, s_buff, 0);

		// set parameter's length
		Utils.short2ByteArray((short)len, s_buff, s_nOffset);
		s_nOffset += 2;

		// copy parameter data to buffer
		if(type == PT_CHAR)
		{
			System.arraycopy(pValue, 0, s_buff, s_nOffset, len);
			s_nOffset += len;
		}
		else if(type == PT_USHORT)
		{
			if(len != 2)
			{
				return -1;
			}
			Utils.short2ByteArray((short)(pValue[0] & 0xff), s_buff, s_nOffset);
			s_nOffset += Short.SIZE / 8;
		}
		else if(type == PT_UINT)
		{
			if(len != 4)
			{
				return -1;
			}
			Utils.int2ByteArray((int)(pValue[0] & 0xff), s_buff, s_nOffset);
			s_nOffset += Integer.SIZE / 8;
		}
		else if (type == PT_INT)
		{
			if(len != 4)
			{
				return -1;
			}
			Utils.int2ByteArray((int)(pValue[0] & 0xff), s_buff, s_nOffset);
			s_nOffset += Integer.SIZE / 8;
		}
		else
		{
			return -1; //Error parameter type.
		}
		System.arraycopy(s_buff, 0, buff, offset, s_nOffset);

		return s_nOffset;
	}

	private int GetParameters(byte[] buff, int offset, int nIndex, byte[] pValue, int type)
	{
		int nOffset = offset + 2;	// first parameter's offset
		int nParamLen;
		int nCount = buff[offset]*255 + buff[offset + 1];		// total count of parameters in the buffer
		if(nIndex > nCount)
			return -1;

		// search the start position of the parameter.
		while (nIndex>1)
		{
			nParamLen = buff[nOffset]*256 + buff[nOffset+1];
			nOffset += nParamLen + 2;
			nIndex--;
		}

		nParamLen = buff[nOffset]*256 + buff[nOffset+1];
		nOffset += 2;

		// retrieve parameter data
		if(type == PT_CHAR)
		{
			System.arraycopy(buff, nOffset, pValue, 0, nParamLen);
		}
		else if(type == PT_USHORT)
		{
			if(nParamLen != 2)
			{
				return -1;
			}

			short sTmp = Utils.shortFromByteArray(buff, nOffset);
			Utils.short2ByteArray(sTmp, pValue, 0);
		}
		else if(type == PT_UINT)
		{
			if(nParamLen != 4)
			{
				return -1;
			}

			int iTmp = Utils.intFromByteArray(buff, nOffset);
			Utils.int2ByteArray(iTmp, pValue, 0);
		}
		else if(type == PT_INT)
		{
			if(nParamLen != 4)
			{
				return -1;
			}

			int iTmp = Utils.intFromByteArray(buff, nOffset);
			Utils.int2ByteArray(iTmp, pValue, 0);
		}
		else
		{
			return -1;	// error types.
		}

		return nParamLen;
	}

	private int PedGetPinBlock(byte KeyIdx, byte[] ExpPinLenIn, byte[] DataIn, 
			byte[] PinBlockOut, byte Mode, int TimeoutMs)
	{
		byte buffer[] = new byte[256];
		int len = 0;
		int ret = PED_RET_OK;
		byte by[];
		// command type and ID
		buffer[0] = PED_FUN;
		buffer[1] = PED_GET_PIN_BLOCK;
	
		// marshal parameters
		by = new byte[1];
		AddParameters(buffer, 2, 1, by, by.length, PT_CHAR);
		KeyIdx = by[0];
		AddParameters(buffer, 2, 2, ExpPinLenIn, ExpPinLenIn.length, PT_CHAR);
		AddParameters(buffer, 2, 3, DataIn, DataIn.length, PT_CHAR);
		by = new byte[1];
		AddParameters(buffer, 2, 4, by, by.length, PT_CHAR);
		Mode = by[0];
		by = new byte[Integer.SIZE / 8];
		len = AddParameters(buffer, 2, 5, by, by.length, PT_UINT);
		TimeoutMs = Utils.intFromByteArray(by, 0);
		
		// send command request and get reply
		
		POS_SendRecv(buffer, len + 2, buffer, TIME_OUT*10);

		by = new byte[Integer.SIZE / 8];
		GetParameters(buffer, 1, 1, by, PT_CHAR);
		ret = by[0] & 0xff;
	
		if((buffer[0] == POS_COMMAND_OK)  && (len > 0))
		{
			if (PinBlockOut != null)
			{
				GetParameters(buffer, 1, 2, PinBlockOut, PT_CHAR);
			}
		}
		return ret;	
	
	}

	//this function should be finished by customer
	private int ClssAfterProc(SYS_PROC_INFO stProcInfo)
	{
		
		int iRetryPIN = 0, iRet, i = 0;

		//the data is just for test,we should get the script from host, and put it in field 55 of ISO - 8583 in BCD format
		byte uScriptData[] = {0x12, 0x34, 0x56}; 
		byte uPinblockOut[] = new byte[20];
		byte szSignPoint[] = new byte[8192];
		byte[] ExpPinLenIn, DataIn;
		int ulLength[] = new int[1];

		SYS_PROC_INFO stProcInfoTemp = new SYS_PROC_INFO();

		CopyProcInfo(stProcInfo, stProcInfoTemp);

		if (stProcInfoTemp.iClssStatus == CT_OFFLINE_APPV || stProcInfoTemp.iClssStatus == CT_OFFLINE_DECLINE)
		{
			//finish the transaction
			CopyProcInfo(stProcInfoTemp, stProcInfo);
			return 0;
		}
		else if (stProcInfoTemp.iClssStatus == CT_NEED_ONLINE)
		{    
			//1. Connect to hosts
			//2. Save Filed 55 of ISo-8583 data 
			//3. send ISo-8583 data back to pos
			//and judge whether wavescript is included in Filed 55 of ISo-8583
	        
			//if wavescript is included , then download wavescript into pos and finishi wave transaction
	        if (new String(uScriptData).length() > 0)
			{
		   		//4. download visascript to pos, and finish the wave transaction
		    	try {
					stProcInfoTemp = clss.integrate.transWaveFinish(uScriptData);
			    	CopyProcInfo(stProcInfoTemp, stProcInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//if wavescript is not included, finish the transaction
			else
			{
		    	CopyProcInfo(stProcInfoTemp, stProcInfo);
			}

			return com232_revbuf[0];
		}
		else if (stProcInfoTemp.iClssStatus == CT_NEED_PIN)
		{
			//1. send command to pos to get pin
			//at the same time, other transaction result should also be sent back from pos in ISO-8583

			ExpPinLenIn = new byte[]{4, 6};
			DataIn = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
	    	iRet = PedGetPinBlock((byte)1,ExpPinLenIn,DataIn,uPinblockOut,(byte)1,12000);

			if (iRet == 0)
			{
				
				while(true)
				{		
					//2. connect to host , to judge the pin (uPinblockOut) 
					//iRet = SendRecvPacket();
					//if( iRet!=0 )
					//{
					//	break;
					//}
					
					//3. according to the ISO-8583 glRecvPack.szRspCode to judge the pin right or wrong
					//if( memcmp(glRecvPack.szRspCode, "55", 2)!=0 || ++iRetryPIN>3)
					//{
					//	break;
				    // }
					
					//4. if the pin is wrong, try to input pin three times
						ExpPinLenIn = new byte[]{4, 6};
						DataIn = new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08};
						iRet = PedGetPinBlock((byte)1,ExpPinLenIn,DataIn,uPinblockOut,(byte)1,12000);
						if( iRet!=0 )
						{
							return iRet;
						}

					//DelayMs(30);
				//	memcpy(&glSendPack.sPINData[0], "\x00\x08", 2);
				//	memcpy(&glSendPack.sPINData[2], glProcInfo.sPinBlock, 8);
				}

			}
			// 5. if the pin is wrong at last time, return back
			//if( memcmp(glRecvPack.szRspCode, "00", LEN_RSP_CODE)!=0 )
	    	//	{
	    	//		return ERR_TRAN_FAIL;
		   //	}

	        //6. if the pin is right, should download the visascript to pos, and finish the transaction
			if (new String(uScriptData).length() > 0)
			{
				try {
					stProcInfoTemp = clss.integrate.transWaveFinish(uScriptData);
			    	CopyProcInfo(stProcInfoTemp, stProcInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
			{
				CopyProcInfo(stProcInfoTemp, stProcInfo);
			}

			return com232_revbuf[0];
		}

		return 0;
	}

	private void TestDownloadCAPKFUN1()
	{
		String record = "";
		int step = 0;
		try{
			InitCapk();
			step++;

			ui.scrCls();
			ui.scrShowText("%P0505DownLoad Capk Begin");
			SystemClock.sleep(1000);
			step++;
			
			for (int i = 0; i < 20; i++)
			{
				clss.integrate.downloadCAPK(gCapklist[i]);
				step++;
				
				ui.scrCls();
				ui.scrShowText("%P0505ClssDownLoadCAPK = " + i);
				ui.scrShowText("%P0525RID: " + Utils.byte2HexStr(gCapklist[i].RID, 0, gCapklist[i].RID.length));
				record += "ClssDownLoadCAPK = " + i + "\n";
				record += "RID: " + Utils.byte2HexStr(gCapklist[i].RID, 0, gCapklist[i].RID.length) + "\n";
				SystemClock.sleep(1000);
				step++;
				
				if (null == gCapklist[i].RID)
					break;
			}

			ui.scrCls();
			ui.scrShowText("%P0505DownLoad Capk Finish");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			record += "Base test end"; 

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}catch(Exception exception){
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void TestDownloadAPPFUN1()
	{
		String record = "";
		int step = 0;
		try{
			InitAID();
			step++;
			
			ui.scrCls();
			ui.scrShowText("%P0505DownLoad APP Begin");
			SystemClock.sleep(1000);
			step++;
			
			clss.integrate.downloadApp(PART_MATCH, VISA_VSDC_APP);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505ClssDownLoadAPP = 1 ...");
			SystemClock.sleep(1000);
			step++;
			
			clss.integrate.downloadApp(PART_MATCH, VISA_ELECTRON_APP);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505ClssDownLoadAPP = 2 ...");
			SystemClock.sleep(1000);
			step++;
			
			clss.integrate.downloadApp(PART_MATCH, MASTER_MCHIP);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505ClssDownLoadAPP = 3 ...");
			SystemClock.sleep(1000);
			step++;
			
			clss.integrate.downloadApp(PART_MATCH, MASTER_MAESTRO_APP);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505ClssDownLoadAPP = 4 ...");
			SystemClock.sleep(1000);
			step++;
			
			clss.integrate.downloadApp(PART_MATCH, MASTER_CIRRUS_APP);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505ClssDownLoadAPP = 5 ...");
			ui.scrShowText("%P0525DownLoad APP Finish");
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			record += "Base test end"; 

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}catch(Exception exception){
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void TestDownloadParamFUN1()
	{
		String record = "";
		int step = 0;
		CLSS_READER_PARAM ClssParam = new CLSS_READER_PARAM();
		CLSS_VISA_AID_PARAM stVisaAidParam = new CLSS_VISA_AID_PARAM();
		CLSS_READER_PARAM_MC ClssParamMC = new CLSS_READER_PARAM_MC();
		CLSS_MC_AID_PARAM_MC ClssAidParamMC = new CLSS_MC_AID_PARAM_MC();
		CLSS_TERM_CONFIG_MC ClssTMConfigMC = new CLSS_TERM_CONFIG_MC();
		
		try{
			ui.scrCls();
			ui.scrShowText("%P0505visareader begin");
			step++;
			InitClssParam(ClssParam);
			clss.integrate.downloadVisaReaderParam(ClssParam); 
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505visareader finish");
			step++;

			ui.scrCls();
			ui.scrShowText("%P0505ClssDownVisaAidParam begin");
			step++;
			clss.integrate.downloadVisaAidParam(stVisaAidParam);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505ClssDownVisaAidParam finish");
			step++;

			ui.scrCls();
			ui.scrShowText("%P0505ClssParamMC begin");
			step++;
			InitClssParamMC(ClssParamMC);
			clss.integrate.downloadMCReaderParam(ClssParamMC); 
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505ClssParamMC finish");
			step++;

			ui.scrCls();
			ui.scrShowText("%P0505ClssAidParamMC begin");
			step++;
			InitClssAidParamMC(ClssAidParamMC);
			clss.integrate.downloadMCAidParam(ClssAidParamMC); 
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505ClssAidParamMC finish");
			step++;

			ui.scrCls();
			ui.scrShowText("%P0505ClssTMConfigMC begin");
			step++;
			InitClssTMConfigMC(ClssTMConfigMC);
			clss.integrate.downloadMCTermConfig(ClssTMConfigMC); 
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505ClssTMConfigMC finish");
			step++;

			record += "Base test end"; 

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}catch(Exception exception){
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void TestClssTransFUN1()
	{
		String record = "";
		int iRet, step = 0;
		POSLOG stProcInfo = new POSLOG();
		SYS_PROC_INFO stProcInfox;
		
		try{
			ui.scrCls();
			ui.scrShowText("%P0505trans parameter begin");
			SystemClock.sleep(1000);
			step++;
			InitProcInfo(stProcInfo);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505trans parameter finish");
			SystemClock.sleep(1000);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505trans begin");
			SystemClock.sleep(1000);
			step++;

			clss.integrate.transInit(stProcInfo);
			step++;
			
			iRet = clss.integrate.waitCardStatus();
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505ClssWaitCardStatus = " + iRet);
			record += "ClssWaitCardStatus = " + iRet + "\n";
			SystemClock.sleep(1000);
			step++;
			
			if (iRet == 0)
			{
				stProcInfox = clss.integrate.transStart();
				step++;
				ui.scrCls();
				ui.scrShowText("%P0505ClssTransStart.status = " + stProcInfox.iClssStatus);
				ui.scrShowText("%P0525track2 = " + stProcInfox.szTrack2);
				ui.scrShowText("%P0545track1 = " + stProcInfox.szTrack1);
				ui.scrShowText("%P0565szExpDate = " + stProcInfox.stTranLog.szExpDate);
				ui.scrShowText("%P0585szPan = " + stProcInfox.stTranLog.szPan);
				record += "ClssTransStart.status = " + stProcInfox.iClssStatus + "\n";
				record += "track2 = " + stProcInfox.szTrack2 + "\n";
				record += "track1 = " + stProcInfox.szTrack1 + "\n";
				record += "szExpDate = " + stProcInfox.stTranLog.szExpDate + "\n";
				record += "szPan = " + stProcInfox.stTranLog.szPan + "\n";
				SystemClock.sleep(3000);
				ui.scrCls();
				ui.scrShowText("%P0505szAmount = " + stProcInfox.stTranLog.szAmount);
				ui.scrShowText("%P0525ucTranType = " + stProcInfox.stTranLog.ucTranType);
				ui.scrShowText("%P0545szDateTime = " + stProcInfox.stTranLog.szDateTime);
				ui.scrShowText("%P0565currency = " + stProcInfox.stTranLog.stTranCurrency.sCurrencyCode[0] + " " + stProcInfox.stTranLog.stTranCurrency.sCurrencyCode[1]);
				record += "szAmount = " + stProcInfox.stTranLog.szAmount + "\n";
				record += "ucTranType = " + stProcInfox.stTranLog.ucTranType + "\n";
				record += "szDateTime = " + stProcInfox.stTranLog.szDateTime + "\n";
				record += "currency = " + stProcInfox.stTranLog.stTranCurrency.sCurrencyCode[0] + " " + stProcInfox.stTranLog.stTranCurrency.sCurrencyCode[1] + "\n";
				SystemClock.sleep(3000);
				step++;
				
				record += "IccData: ";
				for (int i = 0; i < 250; i++)
				{
					record += stProcInfox.stTranLog.sIccData[i] + " ";
				}
				record += "\n";

				if (iRet == 0)
				{
					//stProcInfox.iClssStatus = CT_NEED_SIGN;  //Ç©Ãû
					iRet = ClssAfterProc(stProcInfox);
					step++;
					ui.scrCls();
					ui.scrShowText("%P0505ClssAfterProc.status = " + stProcInfox.iClssStatus);
					record += "ClssAfterProc.status = " + stProcInfox.iClssStatus + "\n";
					SystemClock.sleep(1000);
				}
				
				ui.scrCls();
				ui.scrShowText("%P0505Transation over");
				SystemClock.sleep(1000);
				step++;
			}
			
			record += "Base test end"; 

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}catch(Exception exception){
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}
}
