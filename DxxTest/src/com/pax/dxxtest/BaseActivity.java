package com.pax.dxxtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import utils.RSAUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pax.mposapi.BaseSystemManager;
import com.pax.mposapi.ConfigManager;
import com.pax.mposapi.EmvManager;
import com.pax.mposapi.KeyboardManager;
import com.pax.mposapi.PortManager;
import com.pax.mposapi.UIManager;
import com.pax.mposapi.comm.Comm;
import com.pax.mposapi.comm.Proto;
import com.pax.mposapi.control.EMVPack;
import com.pax.mposapi.model.EMV_APPLIST;
import com.pax.mposapi.model.EMV_CAPK;
import com.pax.mposapi.model.EMV_PARAM;
//import com.pax.dxxRSADemo.R;
import com.pax.mposapi.util.MyLog;

public class BaseActivity extends Activity {
	
	static public Map<String, Object> keyMap;
	public static int  sequenceID=0;
	private String mInterface;
	private String mMethod;
	private String mNo;
	
	public static String maskP;
	public static int rsakeylength;
	
	private UIManager ui;
	private KeyboardManager kbd;
	private BaseSystemManager base;
	private ConfigManager config;
	private Comm comm;
	private TextView text;
	private ProgressDialog progressDialog;
	private EmvManager emv;
	private PortManager portmg;
	private Proto proto;
	private static final String TAG = "BaseActivity";
	
	private RSAUtil rsaUtil;
	private Context context;
	
	private static final String FilePath = "mnt/sdcard/EMVPARA";
	
	public static boolean sta = true;
	
	private final int NORMALRESULT = 1;
	private final int EXCEPTRESULT = 2;
	private final int MAX_CAPK_NUM     = 45;  
//	
	private EMV_CAPK gCapklist[];
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
    
    
	private LinearLayout mLayout;
	
	public String tags;
	
	public int time = 60000;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			int func = data.getIntExtra("FUNC", 0);
			String idx = data.getStringExtra("index");
			Bundle bundle = new Bundle();
			bundle.putString("index", idx);
			bundle.putInt("func", func);
			Message msg = handler.obtainMessage();
			msg.what = 1234;
			msg.setData(bundle);
			handler.sendMessage(msg);
			switch (func) {
			case 1:
				TestOpenFUN1(idx);
				break;
			case 2:
				TestCloseFUN1(idx);
				break;
			case 3:
				TestabortFUN1(idx);
				break;
			case 4:
				TestsetSessionKeyFUN1(idx);
				break;
			case 5:
				TestenableKeypadFUN1(idx);
				break;
			case 6:
				TestdisableKeyPadFUN1(idx);
				break;
			case 7:
				TestpromptPINFUN1(idx);
				break;
			case 8:
				readCardDataSwipeFUN1(idx);
				break;
			case 9:
				readCardDataICFUN1(idx);
				break;
			case 10:
				readCardDataManualEntryFUN1(idx);
				break;
			case 11:
				readCardDataAllFUN1(idx);
				break;
			case 12:
				getBatteryLevelFUN1(idx);
				break;
			case 13:
				setLowBatteryThresholdFUN1(idx);
				break;
			case 14:
				getLowBatteryThresholdFUN1(idx);
				break;
			case 15:
				setEMVTagsFUN1(idx);
				break;
			case 16:
				getEMVTagsFUN1(idx);
				break;
			case 17:
				createMACFUN1(idx);
				break;
			case 18:
				validateMACFUN1(idx);
				break;
			case 19:
				completeOnLineEMVFUN1(idx);
				break;
			case 20:
				authorizeCardFUN1(idx);
				break;
			case 21:
				promptAdditionalInfoFUN1(idx);
				break;
			case 22:
				removeCardFUN1(idx);
				break;
			case 23:
				setParameterFUN1(idx);
				break;
			case 24:
				getParameterFUN1(idx);
				break;
			case 25:
				TestpromptMenuFUN1(idx);
				break;
			case 26:
				TestpromptMessageFUN1(idx);
				break;
			case 27:
				getEncryptedData1(idx);
				break;
			case 28:
				getDateTime(idx);
				break;
			case 29:
				setDateTime(idx);
				break;
			case 30:
				getStatusUpdate(idx);
				break;
			case 31:
				downloadKeyFUN1(idx);
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private EditText editText;
	private EditText editText1;
	private Thread   thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        
        Intent intent = getIntent();
        mInterface = intent.getStringExtra("Interface");
        mNo = intent.getStringExtra("No"); // 
        
        TextView tv = (TextView)findViewById(R.id.base_title);
        tv.setText(mInterface + "_" + "FUN"+mNo);
        
	    ui = UIManager.getInstance(this);
	    kbd = KeyboardManager.getInstance(this);
	    base = BaseSystemManager.getInstance(this);
	    config = ConfigManager.getInstance(this);
	    comm = Comm.getInstance(this);
	    
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
		progressDialog = new ProgressDialog(BaseActivity.this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Processing");
		progressDialog.setIndeterminate(true);
				
		thread = new Thread(new Runnable(){
			
			public void run(){
				Looper.prepare();				
				if(mInterface.equals("Open"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "open|1|APP2|0|D180EMDK");
							getIndex(1, val);							
						}
						if(mNo.equals("2"))
						{
							TestOpenFUN2();
						}
						if(mNo.equals("3"))
						{
							TestOpenFUN3();
						}
						if(mNo.equals("4"))
						{
							TestOpenFUN4();
						}
						if(mNo.equals("5"))
						{
							TestOpenFUN5();
						}
						if(mNo.equals("6"))
						{
							TestOpenFUN6();
						}
						if(mNo.equals("7"))
						{
							TestOpenFUN7();
						}
						if(mNo.equals("8"))
						{
							TestOpenFUN8();
						}
						if(mNo.equals("9"))
						{
							TestOpenFUN9();
						}
						if(mNo.equals("10"))
						{
							TestOpenFUN10();
						}
						if(mNo.equals("11"))
						{
							TestOpenFUN11();
						}
						if(mNo.equals("12"))
						{
							TestOpenFUN12();
						}
						if(mNo.equals("13"))
						{
							TestOpenFUN13();
						}
				}
				else if(mInterface.equals("Close"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "close|1|D180EMDK");
							getIndex(2, val);			
						}
						if(mNo.equals("2"))
						{
							TestCloseFUN2();
						}
						if(mNo.equals("3"))
						{
							TestCloseFUN3();
						}
						if(mNo.equals("4"))
						{
							TestCloseFUN4();
						}
				}
				else if(mInterface.equals("abort"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "abort|1|D180EMDK");
							getIndex(3, val);			
							
						}
						if(mNo.equals("2"))
						{
							TestabortFUN2();
						}
						if(mNo.equals("3"))
						{
							TestabortFUN3();
						}
						if(mNo.equals("4"))
						{
							TestabortFUN4();
						}
						if(mNo.equals("5"))
						{
							TestabortFUN5();
						}
						if(mNo.equals("6"))
						{
							TestabortFUN6();
						}
						if(mNo.equals("7"))
						{
							TestabortFUN7();
						}
						if(mNo.equals("8"))
						{
							TestabortFUN8();
						}
						if(mNo.equals("9"))
						{
							TestabortFUN9();
						}
						
				}
				else if(mInterface.equals("setSessionKey"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "setSessionKey|1|D180EMDK");
							getIndex(4, val);	
						}
						if(mNo.equals("2"))
						{
							TestsetSessionKeyFUN2();
						}
						if(mNo.equals("3"))
						{
							TestsetSessionKeyFUN3();
						}
						if(mNo.equals("4"))
						{
							TestsetSessionKeyFUN4();
						}
				}
				else if(mInterface.equals("enableKeypad"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "enableKeypad|1|D180EMDK|20000");
							getIndex(5, val);	
						}
						if(mNo.equals("2"))
						{
							TestenableKeypadFUN2();
						}
						if(mNo.equals("3"))
						{
							TestenableKeypadFUN3();
						}
				}
				else if(mInterface.equals("disableKeyPad"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "disableKeypad|1|D180EMDK");
							getIndex(6, val);	
						}
						if(mNo.equals("2"))
						{
							TestdisableKeyPadFUN2();
						}
				}
				else if(mInterface.equals("promptPIN"))
				{
						if(mNo.equals("1"))
						{
							//ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							//String val = cfg.getValueByTag(mInterface, "promptPIN|1|D180EMDK|"+maskP+"||4|8|Enter PIN|$1,000.00|$1.00|60000");
							String val = "promptPIN|1|D180EMDK|"+maskP+"||4|8|Enter PIN|$1,000.00|$1.00|60000";
							getIndex(7, val);	
						}
						if(mNo.equals("2"))
						{
							TestpromptPINFUN2();
						}
						if(mNo.equals("3"))
						{
							TestpromptPINFUN3();
						}
						if(mNo.equals("4"))
						{
							TestpromptPINFUN4();
						}
						if(mNo.equals("5"))
						{
							TestpromptPINFUN5();
						}
						if(mNo.equals("6"))
						{
							TestpromptPINFUN6();
						}
						if(mNo.equals("7"))
						{
							TestpromptPINFUN7();
						}
						if(mNo.equals("8"))
						{
							TestpromptPINFUN8();
						}
				}
				else if(mInterface.equals("readCardDataSwipe"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "readCardData|1|D180EMDK|Sale|100.00|5.00|0|20000|$2000.00|Swipe Card-->");
							getIndex(8, val);
							
						}
						if(mNo.equals("2"))
						{
							readCardDataSwipeFUN2();
						}
						if(mNo.equals("3"))
						{
							readCardDataSwipeFUN3();
						}
						if(mNo.equals("4"))
						{
							readCardDataSwipeFUN4();
						}
						if(mNo.equals("5"))
						{
							readCardDataSwipeFUN5();
						}
						if(mNo.equals("6"))
						{
							readCardDataSwipeFUN6();
						}
						if(mNo.equals("7"))
						{
							readCardDataSwipeFUN7();
						}
						if(mNo.equals("8"))
						{
							readCardDataSwipeFUN8();
						}
						if(mNo.equals("9"))
						{
							readCardDataSwipeFUN9();
						}
				}
				
				else if(mInterface.equals("downloadFile_EMVPARA"))
				{
						if(mNo.equals("1"))
						{
							downloadFile_EMVPARA1();
						}
						if(mNo.equals("2"))
						{
							downloadFile_EMVPARA2();
						}
						if(mNo.equals("3"))
						{
							downloadFile_EMVPARA3();
						}
						if(mNo.equals("4"))
						{
							downloadFile_EMVPARA4();
						}
						if(mNo.equals("5"))
						{
							downloadFile_EMVPARA5();
						}
						if(mNo.equals("6"))
						{
							downloadFile_EMVPARA6();
						}
						if(mNo.equals("7"))
						{
							downloadFile_EMVPARA7();
						}
						if(mNo.equals("8"))
						{
							downloadFile_EMVPARA8();
						}
						if(mNo.equals("9"))
						{
							downloadFile_EMVPARA9();
						}
						if(mNo.equals("10"))
						{
							downloadFile_EMVPARA10();
						}
						if(mNo.equals("11"))
						{
							downloadFile_EMVPARA11();
						}
						if(mNo.equals("12"))
						{
							downloadFile_EMVPARA12();
						}
						if(mNo.equals("13"))
						{
							downloadFile_EMVPARA13();
						}
				}
				else if(mInterface.equals("downloadFile_APP"))
				{
						if(mNo.equals("1"))
						{
							downloadFile_APP1();
						}
						if(mNo.equals("2"))
						{
							downloadFile_APP2();
						}
						if(mNo.equals("3"))
						{
							downloadFile_APP3();
						}
						if(mNo.equals("4"))
						{
							downloadFile_APP4();
						}
						if(mNo.equals("5"))
						{
							downloadFile_APP5();
						}
						if(mNo.equals("6"))
						{
							downloadFile_APP6();
						}
				}
				else if(mInterface.equals("downloadFile_FONT"))
				{
						if(mNo.equals("1"))
						{
							downloadFile_FONT1();
						}
//						if(mNo.equals("2"))
//						{
//							downloadFile_FONT2();
//						}
				}
				else if(mInterface.equals("readCardDataIC"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "readCardData|1|D180EMDK|Sale|100.00|5.00|1|20000|$2000|Insert Card-->");
							getIndex(9, val);
						}	
						if(mNo.equals("2"))
						{
							readCardDataICFUN2();
						}	
						if(mNo.equals("3"))
						{
							readCardDataICFUN3();
						}	
						if(mNo.equals("4"))
						{
							readCardDataICFUN4();
						}	
						if(mNo.equals("5"))
						{
							readCardDataICFUN5();
						}
						if(mNo.equals("6"))
						{
							readCardDataICFUN6();
						}
						if(mNo.equals("7"))
						{
							readCardDataICFUN7();
						}
						if(mNo.equals("8"))
						{
							readCardDataICFUN8();
						}
						
//					}
				}
				
				else if(mInterface.equals("readCardDataManualEntry"))
				{
//					if(mMethod.equals("FUN"))
//					{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "readCardData|1|D180EMDK|Sale|100.00|5.00|3|20000|$2000.00|Enter Number");
							getIndex(10,val);
						}	
						if(mNo.equals("2"))
						{
							readCardDataManualEntryFUN2();
						}	
						if(mNo.equals("3"))
						{
							readCardDataManualEntryFUN3();
						}	
						if(mNo.equals("4"))
						{
							readCardDataManualEntryFUN4();
						}	
				}
				
				else if(mInterface.equals("readCardDataAll"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "readCardData|1|D180EMDK|Sale|100.00|5.00|256|20000|$2000.00|Enter Number");
							getIndex(11, val);
							
						}
						if(mNo.equals("2"))
						{
							readCardDataAllFUN2();
						}
						if(mNo.equals("3"))
						{
							readCardDataAllFUN3();
						}
						if(mNo.equals("4"))
						{
							readCardDataAllFUN4();
						}
						if(mNo.equals("5"))
						{
							readCardDataAllFUN5();
						}
						if(mNo.equals("6"))
						{
							readCardDataAllFUN6();
						}
						if(mNo.equals("7"))
						{
							readCardDataAllFUN7();
						}
						if(mNo.equals("8"))
						{
							readCardDataAllFUN8();
						}
						if(mNo.equals("9"))
						{
							readCardDataAllFUN9();
						}
						if(mNo.equals("10"))
						{
							readCardDataAllFUN10();
						}
						if(mNo.equals("11"))
						{
							readCardDataAllFUN11();
						}
						if(mNo.equals("12"))
						{
							readCardDataAllFUN12();
						}
						if(mNo.equals("13"))
						{
							readCardDataAllFUN13();
						}
						if(mNo.equals("14"))
						{
							readCardDataAllFUN14();
						}
						if(mNo.equals("15"))
						{
							readCardDataAllFUN15();
						}
						if(mNo.equals("16"))
						{
							readCardDataAllFUN16();
						}
						if(mNo.equals("17"))
						{
							readCardDataAllFUN17();
						}
						if(mNo.equals("18"))
						{
							readCardDataAllFUN18();
						}
						if(mNo.equals("19"))
						{
							readCardDataAllFUN19();
						}
						if(mNo.equals("20"))
						{
							readCardDataAllFUN20();
						}
						if(mNo.equals("21"))
						{
							readCardDataAllFUN21();
						}
						if(mNo.equals("22"))
						{
							readCardDataAllFUN22();
						}
						if(mNo.equals("23"))
						{
							readCardDataAllFUN23();
						}
						if(mNo.equals("24"))
						{
							readCardDataAllFUN24();
						}
						if(mNo.equals("25"))
						{
							readCardDataAllFUN25();
						}
						if(mNo.equals("26"))
						{
							readCardDataAllFUN26();
						}
						
				}
				
				else if(mInterface.equals("getBatteryLevel"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "getBatteryLevel|1|D180EMDK");
							getIndex(12, val);
						}
						if(mNo.equals("2"))
						{
							getBatteryLevelFUN2();
						}
						if(mNo.equals("3"))
						{
							getBatteryLevelFUN3();
						}
						if(mNo.equals("4"))
						{
							getBatteryLevelFUN4();
						}
				}
				else if(mInterface.equals("setLowBatteryThreshold"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "setLowBatteryTRhreshold|1|D180EMDK|1|Low battery!");
							getIndex(13, val);
						}
						if(mNo.equals("2"))
						{
							setLowBatteryThresholdFUN2();
						}
						if(mNo.equals("3"))
						{
							setLowBatteryThresholdFUN3();
						}
						if(mNo.equals("4"))
						{
							setLowBatteryThresholdFUN4();
						}
						if(mNo.equals("5"))
						{
							setLowBatteryThresholdFUN5();
						}
						if(mNo.equals("6"))
						{
							setLowBatteryThresholdFUN6();
						}
						if(mNo.equals("7"))
						{
							setLowBatteryThresholdFUN7();
						}
						if(mNo.equals("8"))
						{
							setLowBatteryThresholdFUN8();
						}
						if(mNo.equals("9"))
						{
							setLowBatteryThresholdFUN9();
						}
						if(mNo.equals("10"))
						{
							setLowBatteryThresholdFUN10();
						}
				}
				else if(mInterface.equals("getLowBatteryThreshold"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "getLowBatteryTRhreshold|1|D180EMDK");
							getIndex(14, val);
						}
						if(mNo.equals("2"))
						{
							getLowBatteryThresholdFUN2();
						}
				}
				else if(mInterface.equals("setEMVTags"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "setEmvTags|1|D180EMDK|5A085413330089601075");
							getIndex(15, val);
						}
						if(mNo.equals("2"))
						{
							setEMVTagsFUN2();
						}
						if(mNo.equals("3"))
						{
							setEMVTagsFUN3();
						}
						if(mNo.equals("4"))
						{
							setEMVTagsFUN4();
						}
						if(mNo.equals("5"))
						{
							setEMVTagsFUN5();
						}
						if(mNo.equals("6"))
						{
							setEMVTagsFUN6();
						}
						if(mNo.equals("7"))
						{
							setEMVTagsFUN7();
						}
				}
				else if(mInterface.equals("getEMVTags"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "getEmvTags|1|D180EMDK|5A");
							getIndex(16, val);
						}
						if(mNo.equals("2"))
						{
							getEMVTagsFUN2();
						}
						if(mNo.equals("3"))
						{
							getEMVTagsFUN3();
						}
						if(mNo.equals("4"))
						{
							getEMVTagsFUN4();
						}
						if(mNo.equals("5"))
						{
							getEMVTagsFUN5();
						}
				}
			
				else if(mInterface.equals("createMAC"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "createMAC|1|D180EMDK|1A2B3C4D1A2B3C4D");
							getIndex(17, val);
						}
						if(mNo.equals("2"))
						{
							createMACFUN2();
						}
						if(mNo.equals("3"))
						{
							createMACFUN3();
						}
						if(mNo.equals("4"))
						{
							createMACFUN4();
						}
						if(mNo.equals("5"))
						{
							createMACFUN5();
						}
						if(mNo.equals("6"))
						{
							createMACFUN6();
						}
						if(mNo.equals("7"))
						{
							createMACFUN7();
						}
				}
				else if(mInterface.equals("validateMAC"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "validateMAC|1|D180EMDK|F87BCBE88883243D||||message1|message2|1A2B3C4D1A2B3C4D");
							getIndex(18, val);
						}
						if(mNo.equals("2"))
						{
							validateMACFUN2();
						}
						if(mNo.equals("3"))
						{
							validateMACFUN3();
						}
						if(mNo.equals("4"))
						{
							validateMACFUN4();
						}
						if(mNo.equals("5"))
						{
							validateMACFUN5();
						}
						if(mNo.equals("6"))
						{
							validateMACFUN6();
						}
				}
				else if(mInterface.equals("completeOnLineEMV"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "completeOnLineEMV|1|D180EMDK|0|true|");
							getIndex(19, val);
						}
						if(mNo.equals("2"))
						{
							completeOnLineEMVFUN2();
						}
						if(mNo.equals("3"))
						{
							completeOnLineEMVFUN3();
						}
						if(mNo.equals("4"))
						{
							completeOnLineEMVFUN4();
						}
						if(mNo.equals("5"))
						{
							completeOnLineEMVFUN5();
						}
						if(mNo.equals("6"))
						{
							completeOnLineEMVFUN6();
						}
				}
				else if(mInterface.equals("authorizeCard"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "authorizeCard|1|D180EMDK|999.00|5.00|0|true|true|true|true|20000|91053A4B123C1E|71023B1C|72010A");
							getIndex(20, val);
						}
						if(mNo.equals("2"))
						{
							authorizeCardFUN2();
						}
						if(mNo.equals("3"))
						{
							authorizeCardFUN3();
						}
						if(mNo.equals("4"))
						{
							authorizeCardFUN4();
						}
						if(mNo.equals("5"))
						{
							authorizeCardFUN5();
						}
						if(mNo.equals("6"))
						{
							authorizeCardFUN6();
						}
						if(mNo.equals("7"))
						{
							authorizeCardFUN7();
						}
						if(mNo.equals("8"))
						{
							authorizeCardFUN8();
						}
						if(mNo.equals("9"))
						{
							authorizeCardFUN9();
						}
						if(mNo.equals("10"))
						{
							authorizeCardFUN10();
						}
						if(mNo.equals("11"))
						{
							authorizeCardFUN11();
						}
						if(mNo.equals("12"))
						{
							authorizeCardFUN12();
						}
						if(mNo.equals("13"))
						{
							authorizeCardFUN13();
						}
						if(mNo.equals("14"))
						{
							authorizeCardFUN14();
						}
						if(mNo.equals("15"))
						{
							authorizeCardFUN15();
						}
						if(mNo.equals("16"))
						{
							authorizeCardFUN16();
						}
						if(mNo.equals("17"))
						{
							authorizeCardFUN17();
						}
						if(mNo.equals("18"))
						{
							authorizeCardFUN18();
						}
				}
				else if(mInterface.equals("ICTest"))
				{

						if(mNo.equals("1"))
						{
							ICTestFUN1();
						}
						if(mNo.equals("2"))
						{
							ICTestFUN2();
						}
						if(mNo.equals("3"))
						{
							ICTestFUN3();
						}
						if(mNo.equals("4"))
						{
							ICTestFUN4();
						}
						
				}
				
				else if(mInterface.equals("promptAdditionalInfo"))
				{

						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "promptAdditionalInfo|1|D180EMDK|99.99|6228481090369874587|TRUE|TRUE|1.11|20000");
							getIndex(21, val);
						}
						if(mNo.equals("2"))
						{
							promptAdditionalInfoFUN2();
						}
						if(mNo.equals("3"))
						{
							promptAdditionalInfoFUN3();
						}
						if(mNo.equals("4"))
						{
							promptAdditionalInfoFUN4();
						}
						if(mNo.equals("5"))
						{
							promptAdditionalInfoFUN5();
						}
				}
				else if(mInterface.equals("removeCard"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "removeCard|1|D180EMDK|EMV card|Please pull out the card...");
							getIndex(22,val);
						}
						if(mNo.equals("2"))
						{
							removeCardFUN2();
						}
				}
				else if(mInterface.equals("setParameter"))
				{
					if(mNo.equals("1"))
					{
						ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
						String val = cfg.getValueByTag(mInterface, "setParameter|1|D180EMDK|idleMsg=Welcome|sleepModeTimeout=20000|dataEncryptionKeySlot=2|dataEncryptionType=4|maskFirstDigits=6|LanguageType=0|FallbackAllowedFlag=0|AIDFilterAllowedFlag=0|BTDiscoveryAllowedFlag=0|DukptKeySlot=0");
						getIndex(23, val);
					}
						if(mNo.equals("2"))
						{
							setParameterFUN2();
						}
						if(mNo.equals("3"))
						{
							setParameterFUN3();
						}
						if(mNo.equals("4"))
						{
							setParameterFUN4();
						}
						if(mNo.equals("5"))
						{
							setParameterFUN5();
						}
						if(mNo.equals("6"))
						{
							setParameterFUN6();
						}
						if(mNo.equals("7"))
						{
							setParameterFUN7();
						}
						if(mNo.equals("8"))
						{
							setParameterFUN8();
						}
						if(mNo.equals("9"))
						{
							setParameterFUN9();
						}
						if(mNo.equals("10"))
						{
							setParameterFUN10();
						}
						if(mNo.equals("11"))
						{
							setParameterFUN11();
						}
						if(mNo.equals("12"))
						{
							setParameterFUN12();
						}
				}
				else if(mInterface.equals("getParameter"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "getParameter|1|D180EMDK|idleMsg||||");
							getIndex(24, val);
						}
						if(mNo.equals("2"))
						{
							getParameterFUN2();
						}
						if(mNo.equals("3"))
						{
							getParameterFUN3();
						}
						if(mNo.equals("4"))
						{
							getParameterFUN4();
						}
						if(mNo.equals("5"))
						{
							getParameterFUN5();
						}
						if(mNo.equals("6"))
						{
							getParameterFUN6();
						}
						if(mNo.equals("7"))
						{
							getParameterFUN7();
						}
						if(mNo.equals("8"))
						{
							getParameterFUN8();
						}
						if(mNo.equals("9"))
						{
							getParameterFUN9();
						}
				}
				else if(mInterface.equals("promptMenu"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "promptMenu|1|D180EMDK|Prompt Menu|Please Select|sale|void|offline|REFUND|20000|1");
							getIndex(25, val);
						}
						if(mNo.equals("2"))
						{
							TestpromptMenuFUN2();
						}
						if(mNo.equals("3"))
						{
							TestpromptMenuFUN3();
						}
						if(mNo.equals("4"))
						{
							TestpromptMenuFUN4();
						}
						if(mNo.equals("5"))
						{
							TestpromptMenuFUN5();
						}
						if(mNo.equals("6"))
						{
							TestpromptMenuFUN6();
						}
						if(mNo.equals("7"))
						{
							TestpromptMenuFUN7();
						}
						if(mNo.equals("8"))
						{
							TestpromptMenuFUN8();
						}
						if(mNo.equals("9"))
						{
							TestpromptMenuFUN9();
						}
						if(mNo.equals("10"))
						{
							TestpromptMenuFUN10();
						}
//					}
				}
				else if(mInterface.equals("promptMessage"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "promptMessage|1|D180EMDK|Prompt Menu|Select|A.OK|B.Cancel|False|20000");
							getIndex(26, val);
						}
						if(mNo.equals("2"))
						{
							TestpromptMessageFUN2();
						}
						if(mNo.equals("3"))
						{
							TestpromptMessageFUN3();
						}
						if(mNo.equals("4"))
						{
							TestpromptMessageFUN4();
						}
						if(mNo.equals("5"))
						{
							TestpromptMessageFUN5();
						}
						if(mNo.equals("6"))
						{
							TestpromptMessageFUN6();
						}
						if(mNo.equals("7"))
						{
							TestpromptMessageFUN7();
						}
						if(mNo.equals("8"))
						{
							TestpromptMessageFUN8();
						}
						if(mNo.equals("9"))
						{
							TestpromptMessageFUN9();
						}
				}
				else if(mInterface.equals("downloadKey"))
				{

						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "downloadKey|1|D180EMDK|1|16|12345678911234567892123456789312|2|0|0|0|0|0");
							getIndex(31, val);			
						}
						if(mNo.equals("2"))
						{
							downloadKeyFUN2();
						}
						if(mNo.equals("3"))
						{
							downloadKeyFUN3();
						}
						if(mNo.equals("4"))
						{
							downloadKeyFUN4();
						}
						if(mNo.equals("5"))
						{
							downloadKeyFUN5();
						}
						if(mNo.equals("6"))
						{
							downloadKeyFUN6();
						}
						if(mNo.equals("7"))
						{
							downloadKeyFUN7();
						}
						if(mNo.equals("8"))
						{
							downloadKeyFUN8();
						}
						if(mNo.equals("9"))
						{
							downloadKeyFUN9();
						}
				}
				else if(mInterface.equals("getEncryptedData"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "getEncryptedData|1|D180EMDK|0");
							getIndex(27,val);
						}
						if(mNo.equals("2"))
						{
							getEncryptedData2();
						}
						if(mNo.equals("3"))
						{
							getEncryptedData3();
						}
						if(mNo.equals("4"))
						{
							getEncryptedData4();
						}
						if(mNo.equals("5"))
						{
							getEncryptedData5();
						}
						if(mNo.equals("6"))
						{
							getEncryptedData6();
						}
				}
				else if(mInterface.equals("syntheticTest"))
				{
					if(mNo.equals("1"))
					{
						try {
							syntheticTest1();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if(mNo.equals("2"))
					{
						syntheticTest2();
					}
				}
				else if(mInterface.equals("getDateTime"))
				{
					if(mNo.equals("1"))
					{
						ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
						String val = cfg.getValueByTag(mInterface, "getDateTime|1|D180EMDK");
						getIndex(28,val);
					}
				}
				else if(mInterface.equals("setDateTime"))
				{
					if(mNo.equals("1"))
					{
						ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
						String val = cfg.getValueByTag(mInterface, "setDateTime|1|D180EMDK|03112015|134702");
						getIndex(29,val);
					}
				}
			
				else if(mInterface.equals("getStatusUpdate"))
				{
					if(mNo.equals("1"))
					{
						ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
						String val = cfg.getValueByTag(mInterface, "getStatusUpdate|1|D180EMDK");
						getIndex(30,val);
					}
				}
			}
		});
		thread.start();
	}
	public void getIndex(int func, String index) {
		Intent it     = new Intent();
		Intent intent = getIntent();
		String mInterface1 = intent.getStringExtra("Interface");
		it.putExtra("Interface", mInterface1);
		it.putExtra("FUNC", func);
		it.putExtra("index", index);
		it.setClass(getApplicationContext(), InputActivity.class);
		startActivityForResult(it, 0);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		  if (keyCode == KeyEvent.KEYCODE_BACK) {
			  finish();
		  }
		  return true;
	}	
	
	private void TestOpenFUN1(String index) {
		int i = 0; 
		int step = 0;
		int resendTimes = 10;
		byte[] by = null;
		final boolean[] isTimeout = new boolean[]{false};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				isTimeout[0] = true;
			}
		};
		String record = "";

		sequenceID++;
		timer.schedule(task, time);
		for (;;) {
			try {
				Log.i("lxg", "send ---------------- ");
				step++;
				by = base.open111(index);		
				break;

			}catch (Exception exception) {
				exception.printStackTrace();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(isTimeout[0])
					break;
				else
					continue;
				
			} finally {
				progressDialog.dismiss();
			}
		}
		timer.cancel();
		if(isTimeout[0] == false){
			record += "open ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;

			record += "Open test end";

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}else{
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "BT Not Connected...");
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}	

	private void TestOpenFUN2() {
		int i = 0; 
		int step = 0;
		int resendTimes = 10;
		byte[] by = null;
		final boolean[] isTimeout = new boolean[]{false};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isTimeout[0] = true;
			}
		};
		String record = "";

		sequenceID++;
		timer.schedule(task, time);
		for (;;) {
			try {
				Log.i("lxg", "send ---------------- ");
				step++;
				by = base.open(String.valueOf(sequenceID),"APP2","0","D180EMDK");			
				break;

			}catch (Exception exception) {
				exception.printStackTrace();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isTimeout[0])
					break;
				else
					continue;
				
			} finally {
				progressDialog.dismiss();
			}
		}
		timer.cancel();
		if(isTimeout[0] == false){
			record += "open ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;

			record += "Open test end";

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}else{
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "BT Not Connected...");
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}	

	
	private void TestOpenFUN3() {
		int i = 0; 
		int step = 0;
		int resendTimes = 10;
		byte[] by = null;
		final boolean[] isTimeout = new boolean[]{false};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isTimeout[0] = true;
			}
		};
		String record = "";

		sequenceID++;
		timer.schedule(task, time);
		for (;;) {
			try {
				Log.i("lxg", "send ---------------- ");
				step++;
				by = base.open(String.valueOf(sequenceID),"APPAPPAPPAPPAPP","0","D180EMDK");		
				break;

			}catch (Exception exception) {
				exception.printStackTrace();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isTimeout[0])
					break;
				else
					continue;
				
			} finally {
				progressDialog.dismiss();
			}
		}
		timer.cancel();
		if(isTimeout[0] == false){
			record += "open ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;

			record += "Open test end";

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}else{
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "BT Not Connected...");
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}	

	private void TestOpenFUN4() {
		int i = 0; 
		int step = 0;
		int resendTimes = 10;
		byte[] by = null;
		final boolean[] isTimeout = new boolean[]{false};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isTimeout[0] = true;
			}
		};
		String record = "";

		sequenceID++;
		timer.schedule(task, time);
		for (;;) {
			try {
				Log.i("lxg", "send ---------------- ");
				step++;
				by = base.open(String.valueOf(sequenceID),"abc","1","D180EMDK");		
				break;

			}catch (Exception exception) {
				exception.printStackTrace();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isTimeout[0])
					break;
				else
					continue;
				
			} finally {
				progressDialog.dismiss();
			}
		}
		timer.cancel();
		if(isTimeout[0] == false){
			record += "open ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;

			record += "Open test end";

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}else{
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "BT Not Connected...");
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}	

	private void TestOpenFUN5() {
		int i = 0; 
		int step = 0;
		int resendTimes = 10;
		byte[] by = null;
		final boolean[] isTimeout = new boolean[]{false};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isTimeout[0] = true;
			}
		};
		String record = "";

		sequenceID++;
		timer.schedule(task, time);
		for (;;) {
			try {
				Log.i("lxg", "send ---------------- ");
				step++;
				by = base.open(String.valueOf(sequenceID),"abc","0","D180EMDK1");	
				break;

			}catch (Exception exception) {
				exception.printStackTrace();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isTimeout[0])
					break;
				else
					continue;
				
			} finally {
				progressDialog.dismiss();
			}
		}
		timer.cancel();
		if(isTimeout[0] == false){
			record += "open ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;

			record += "Open test end";

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}else{
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "BT Not Connected...");
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}
	
	private void TestOpenFUN6() {
		int i = 0; 
		int step = 0;
		int resendTimes = 10;
		byte[] by = null;
		final boolean[] isTimeout = new boolean[]{false};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isTimeout[0] = true;
			}
		};
		String record = "";

		sequenceID++;
		timer.schedule(task, time);
		for (;;) {
			try {
				Log.i("lxg", "send ---------------- ");
				step++;
				by = base.open(String.valueOf(sequenceID),"abc","0","D180EMDK");
				break;

			}catch (Exception exception) {
				exception.printStackTrace();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isTimeout[0])
					break;
				else
					continue;
				
			} finally {
				progressDialog.dismiss();
			}
		}
		timer.cancel();
		if(isTimeout[0] == false){
			record += "open ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;

			record += "Open test end";

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}else{
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "BT Not Connected...");
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	private void TestOpenFUN7() {
		String record = "";

		int i; 
		int step = 0;
		int resendTimes = 3;
		byte[] by = null;
		sequenceID++;
		for(int j = 0; j < 100; j++)
		{	
			for (i = 0; i < resendTimes; i++) {
				try {
					Log.i("lxg", "send ---------------- ");
					by = base.open(String.valueOf(sequenceID),"abc","0","D180EMDK");
					break;
	
				} catch (Exception exception) {
					exception.printStackTrace();
					if(i < resendTimes) continue;
				} finally {
					progressDialog.dismiss();
				}
			}
			if(i < resendTimes){
				record += "open ret: " + new String(by) + "\n";
				System.out.println(new String(by));
	
				record += "Open test end";
	
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
			}else{
				Bundle bundle = new Bundle();
				bundle.putString("result", record + "BT Not Connected...");
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		}
	}
	
	private void TestOpenFUN8() {
		int i = 0; 
		int step = 0;
		int resendTimes = 10;
		byte[] by = null;
		final boolean[] isTimeout = new boolean[]{false};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isTimeout[0] = true;
			}
		};
		String record = "";

		sequenceID++;
		timer.schedule(task, time);
		for (;;) {
			try {
				Log.i("lxg", "send ---------------- ");
				step++;
				by = base.open(String.valueOf(sequenceID),"","0","D180EMDK");
				break;

			}catch (Exception exception) {
				exception.printStackTrace();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isTimeout[0])
					break;
				else
					continue;
				
			} finally {
				progressDialog.dismiss();
			}
		}
		timer.cancel();
		if(isTimeout[0] == false){
			record += "open ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;

			record += "Open test end";

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}else{
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "BT Not Connected...");
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	private void TestOpenFUN9() {
		int i = 0; 
		int step = 0;
		int resendTimes = 10;
		byte[] by = null;
		final boolean[] isTimeout = new boolean[]{false};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isTimeout[0] = true;
			}
		};
		String record = "";

		sequenceID++;
		timer.schedule(task, time);
		for (;;) {
			try {
				Log.i("lxg", "send ---------------- ");
				step++;
				by = base.open(String.valueOf(sequenceID),"APP1","0","D180EMDK");
				break;

			}catch (Exception exception) {
				exception.printStackTrace();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isTimeout[0])
					break;
				else
					continue;
				
			} finally {
				progressDialog.dismiss();
			}
		}
		timer.cancel();
		if(isTimeout[0] == false){
			record += "open ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;

			record += "Open test end";

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}else{
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "BT Not Connected...");
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}
	
	private void TestOpenFUN10() {
		int i = 0; 
		int step = 0;
		int resendTimes = 10;
		byte[] by = null;
		final boolean[] isTimeout = new boolean[]{false};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isTimeout[0] = true;
			}
		};
		String record = "";

		sequenceID++;
		timer.schedule(task, time);
		for (;;) {
			try {
				Log.i("lxg", "send ---------------- ");
				step++;
				SimpleDateFormat df = new
				SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
				record += "Time1: " + df.format(new Date())+"\n";
				by = base.open(String.valueOf(sequenceID),"APP1","0","D180EMDK");
				record += "Time2: " + df.format(new Date())+"\n";
				break;

			}catch (Exception exception) {
				exception.printStackTrace();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isTimeout[0])
					break;
				else
					continue;
				
			} finally {
				progressDialog.dismiss();
			}
		}
		timer.cancel();
		if(isTimeout[0] == false){
			record += "open ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;

			record += "Open test end";

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}else{
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "BT Not Connected...");
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	private void TestOpenFUN11() {
		int i = 0; 
		int step = 0;
		int resendTimes = 10;
		
		byte[] by = null;
		String record = "";
		for(int j = 0; j < 200; j++)
		{
			final boolean[] isTimeout = new boolean[]{false};
			Timer timer = new Timer();
			TimerTask task = new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					isTimeout[0] = true;
				}
			};
	
			sequenceID++;
		
			timer.schedule(task, time);
			for (;;) {
				try {
					Log.i("lxg", "send ---------------- ");
					step++;
					by = base.open(String.valueOf(sequenceID), "APP1", "0",
							"D180EMDK");				
					break;
	
				}catch (Exception exception) {
					exception.printStackTrace();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(isTimeout[0])
						break;
					else
						continue;
					
				} finally {
					progressDialog.dismiss();
				}
			}
			timer.cancel();
			if(isTimeout[0] == false){
				
				byte[] by2;
				try {
					by2 = base.close(String.valueOf(sequenceID),"D180EMDK");
					record += "open ret: " + new String(by) + "\n" + "close ret: " + new String(by2) +"    " +  sequenceID + "\n";
					Thread.sleep(1000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println(new String(by));
				step++;
	
				record += "Open test end";
	
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
			}else{
				Bundle bundle = new Bundle();
				bundle.putString("result", record + "BT Not Connected...");
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		}
	}

	private void TestOpenFUN12() {
		int i = 0; 
		int step = 0;
		int resendTimes = 10;
		byte[] by = null;
		final boolean[] isTimeout = new boolean[]{false};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isTimeout[0] = true;
			}
		};
		String record = "";

		sequenceID++;
		timer.schedule(task, time);
		for (;;) {
			try {
				Log.i("lxg", "send ---------------- ");
				step++;
				by = base.open(String.valueOf(sequenceID), "APP1", "0",
						"D180EMDK");				
				break;

			}catch (Exception exception) {
				exception.printStackTrace();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isTimeout[0])
					break;
				else
					continue;
				
			} finally {
				progressDialog.dismiss();
			}
		}
		timer.cancel();
		if(isTimeout[0] == false){
			record += "open ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;

			record += "Open test end";

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}else{
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "BT Not Connected...");
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}	
	
	private void TestOpenFUN13() {
		int i = 0; 
		int step = 0;
		int resendTimes = 10;
		byte[] by = null;
		final boolean[] isTimeout = new boolean[]{false};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isTimeout[0] = true;
			}
		};
		String record = "";

		sequenceID++;
		timer.schedule(task, time);
		for (;;) {
			try {
				Log.i("lxg", "send ---------------- ");
				step++;
				by = base.open(String.valueOf(sequenceID), "APP1", "0",
						"D180EMDK");				
				break;

			}catch (Exception exception) {
				exception.printStackTrace();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isTimeout[0])
					break;
				else
					continue;
				
			} finally {
				progressDialog.dismiss();
			}
		}
		timer.cancel();
		if(isTimeout[0] == false){
			record += "open ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;

			record += "Open test end";

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}else{
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "BT Not Connected...");
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}	
	private void TestCloseFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			sequenceID++;
			step++;
			byte[] by = base.close111(index);
			record += "close ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			String content = new String(by);
			String suc="success";
			String w1=content.substring(0, 7);
			if(!w1.equals(suc)){
				proto.setisBTConnected();
			}
			
			record += "close test end"; 

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
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
	private void TestCloseFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.close1(String.valueOf(sequenceID),"abcedf");
			record += "close ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			String content = new String(by);
			String suc="success";
			String w1=content.substring(0, 7);
			if(!w1.equals(suc)){
				proto.setisBTConnected();
			}
			
			record += "close test end"; 

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
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void TestCloseFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			sequenceID++;
			step++;
			byte[] by = base.close(String.valueOf(sequenceID),"D180EMDK");
			record += "close ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			String content = new String(by);
			String suc="success";
			String w1=content.substring(0, 7);
			if(!w1.equals(suc)){
				proto.setisBTConnected();
			}
			
			record += "close test end"; 

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
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void TestCloseFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			sequenceID++;
			step++;
			byte[] by = base.close(String.valueOf(sequenceID),"D180EMDK");
			record += "close ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			String content = new String(by);
			String suc="success";
			String w1=content.substring(0, 7);
			if(!w1.equals(suc)){
				proto.setisBTConnected();
			}
			
			record += "close test end"; 

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
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void TestabortFUN1(String index)
	{
		
			String record = "";
			sequenceID++;
			int step = 0;
			try{
				step++;
				byte[] by = base.abort111(index);
				record += "abort ret: " + new String(by) + "\n";
				System.out.println(new String(by));
				step++;
				
				record += "abort test end"; 
	
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
			}catch(Exception exception){
				exception.printStackTrace();
				Bundle bundle = new Bundle();
				
				bundle.putString("result", "Current command is not able to be aborted0" );
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} finally {
				progressDialog.dismiss();
			}
	}
	
//	private void TestabortFUN2()
//	{
//		int a =100;
//		for(int i =0;i<a;i++)
//		{
//			if(sta)
//			{
//				String record = "";
//				sequenceID++;
//				try{
//					byte[] by = base.abort(String.valueOf(sequenceID),"D180EMDK");
//					record += "abort ret: " + new String(by) + "\n";
//					System.out.println(new String(by));
//					
//					record += "abort test end"; 
//		
//					Bundle bundle = new Bundle();
//					bundle.putString("result", record);
//					Message msg = handler.obtainMessage();
//					msg.what = 1;
//					msg.setData(bundle);
//					handler.sendMessage(msg);
//				}catch(Exception exception){
//					exception.printStackTrace();
//					Bundle bundle = new Bundle();
//					bundle.putString("result", "Current command is not able to be aborted" );
//					Message msg = handler.obtainMessage();
//					msg.what = 2;
//					msg.setData(bundle);
//					handler.sendMessage(msg);
//				} finally {
//					progressDialog.dismiss();
//				}
//			}
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				a=0;
//			}
//		}
//	}
	
	private void TestabortFUN2()
	{
				String record = "";
				sequenceID++;
				try{
					byte[] by = base.abort(String.valueOf(sequenceID),"D180EMDK");
					record += "abort ret: " + new String(by) + "\n";
					System.out.println(new String(by));
					
					record += "abort test end"; 
		
					Bundle bundle = new Bundle();
					bundle.putString("result", record);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.setData(bundle);
					handler.sendMessage(msg);
				}catch(Exception exception){
					exception.printStackTrace();
					Bundle bundle = new Bundle();
					bundle.putString("result", "Current command is not able to be aborted.." );
					Message msg = handler.obtainMessage();
					msg.what = 2;
					msg.setData(bundle);
					handler.sendMessage(msg);
				} finally {
					progressDialog.dismiss();
				}
	}
	private void TestabortFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.abort(String.valueOf(sequenceID),"D180EMDK");
			record += "abort ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "abort test end"; 

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}catch(Exception exception){
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result", "Current command is not able to be aborted" );
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}
	private void TestabortFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.abort(String.valueOf(sequenceID),"D180EMDK");
			record += "abort ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorized test end"; 

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}catch(Exception exception){
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result", "Current command is not able to be aborted" );
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}
	
	private void TestabortFUN5()
	{
		String record = "";
		sequenceID++;
		try{
			byte[] by = base.abort(String.valueOf(sequenceID),"D180EMDK");
			record += "abort ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			
			record += "promptAdditionallnfo test end"; 

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}catch(Exception exception){
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result", "Current command is not able to be aborted" );
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}
	private void TestabortFUN6()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		for(int i = 0; i<200; i++)
		{
			try{
				byte[] by2 = ui.promptMenu111Step("promptMenu|1|D180EMDK|Prompt Menu|Please Select|sale|void|offline|REFUND|20000");
				Thread.sleep(500);
				byte[] by = base.abort(String.valueOf(sequenceID),"D180EMDK");
				record += "abort ret: " + new String(by) + "\n";
				record += "promptMenu-abort test end,numer:"+(i+1); 		
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
				Thread.sleep(500);
			}catch(Exception exception){
				exception.printStackTrace();
				Bundle bundle = new Bundle();
				bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} finally {
				//progressDialog.dismiss();
			}
		}
	}

	private void TestabortFUN7()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		for(int i = 0; i<200; i++)
		{
			try{
					byte[] by2 = ui.promptMessage111Step("promptMessage|1|D180EMDK|Prompt Menu|Select|A.OK|B.Cancel|TRUE|20000");
					Thread.sleep(500);
					byte[] by = base.abort(String.valueOf(sequenceID),"D180EMDK");
					
					record += "abort ret: " + new String(by) + "\n";
					record += "promptMessage-abort test end,numer:"+(i+1); 
		
					Bundle bundle = new Bundle();
					bundle.putString("result", record);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.setData(bundle);
					handler.sendMessage(msg);
					Thread.sleep(500);
			}catch(Exception exception){
				exception.printStackTrace();
				Bundle bundle = new Bundle();
				bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} finally {
				//progressDialog.dismiss();
			}
		}
	}

	private void TestabortFUN8()
	{
			String record = "";
			sequenceID++;
			int step = 0;
			for(int i = 0; i<200; i++)
			{
				try{
						byte[] by2 = base.readCardData111Step("readCardData|1|D180EMDK|Sale|100.00|5.00|256|20000|$2000.00|Transaction...");
						String content = new String(by2);
						String w1=content.substring(0, 7);
						
						String suc="success";
						String aidlist="AIDList";
						if(w1.equals(aidlist)) {
							content = content.trim();
							String AIDList = content.split("\\|")[0];
							String AID=content.substring(content.indexOf("|",1), content.length());
							String str = AIDList+"|1|D180EMDK"+AID;
								
							byte[] by4 = base.readCardData1111(str);
							String content1 = new String(by4);
							maskP = content1.split("\\|")[11]; 
							record += "readCardData ret: " + new String(by4) + "\n";
						}
						else if(w1.equals(suc))
						{
							maskP = content.split("\\|")[11]; 
							record += "readCardData ret: " + new String(by2) + "\n";
						}	
						else
						{
							record += "readCardData ret: " + new String(by2) + "\n";
						}
						
						Thread.sleep(500);
						byte[] by = base.abort(String.valueOf(sequenceID),"D180EMDK");
						record += "abort ret: " + new String(by) + "\n";
						record += "readCardData-abort test end,numer:"+(i+1); 
			
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						Thread.sleep(500);
				}catch(Exception exception){
					exception.printStackTrace();
					Bundle bundle = new Bundle();
					bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
					Message msg = handler.obtainMessage();
					msg.what = 2;
					msg.setData(bundle);
					handler.sendMessage(msg);
				} finally {
					//progressDialog.dismiss();
				}
		}
	}
	private void TestabortFUN9()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		for(int i = 0; i<200; i++)
		{
			try{
					byte[] by2 = base.readCardData111("readCardData|1|D180EMDK|Sale|100.00|5.00|256|20000|$2000.00|Transaction...");
					String content = new String(by2);
					String w1=content.substring(0, 7);
					
					String suc="success";
					String aidlist="AIDList";
					if(w1.equals(aidlist)) {
						content = content.trim();
						String AIDList = content.split("\\|")[0];
						String AID=content.substring(content.indexOf("|",1), content.length());
						String str = AIDList+"|1|D180EMDK"+AID;
							
						byte[] by4 = base.readCardData1111(str);
						String content1 = new String(by4);
						maskP = content1.split("\\|")[11]; 
						record += "readCardData ret: " + new String(by4) + "\n";
					}
					else if(w1.equals(suc))
					{
						maskP = content.split("\\|")[11]; 
						record += "readCardData ret: " + new String(by2) + "\n";
					}	
					else
					{
						record += "readCardData ret: " + new String(by2) + "\n";
					}
					byte[] by3 = base.authorizeCard111Step("authorizeCard|1|D180EMDK|999.00|5.00|1|true|true|true|true|20000|91053A4B123C1E|71023B1C|72010A");
					Thread.sleep(500);
					byte[] by = base.abort(String.valueOf(sequenceID),"D180EMDK");
					record += "abort ret: " + new String(by) + "\n";
					record += "authorizeCard-abort test end,numer:"+(i+1); 
					
					Bundle bundle = new Bundle();
					bundle.putString("result", record);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.setData(bundle);
					handler.sendMessage(msg);
					Thread.sleep(500);
			}catch(Exception exception){
				exception.printStackTrace();
				Bundle bundle = new Bundle();
				bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} finally {
				//progressDialog.dismiss();
			}
		}
	}

	private void TestsetSessionKeyFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.setSessionKey111(index);
			record += "setSessionKey ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setSessionKey test end"; 

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
	

	private void TestsetSessionKeyFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.setSessionKey(String.valueOf(sequenceID), "D180EMDK");
			record += "setSessionKey ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setSessionKey test end"; 

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
	private void TestsetSessionKeyFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505setSessionKey API");
			step++;
			byte[] by = base.setSessionKey1(String.valueOf(sequenceID), "123456");
			record += "setSessionKey ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setSessionKey test end"; 

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
	private void TestsetSessionKeyFUN4()
	{
		
			String record = "";
			sequenceID++;
			int step = 0;
			try{
				step++;
				for(int i = 0;i<200; i++)
				{
					byte[] by = base.setSessionKey(String.valueOf(sequenceID), "D180EMDK");
					record += "setSessionKey ret: " + new String(by) +"    number:"+(i+1)+ "\n";
					System.out.println(new String(by));
					step++;
					
					record += "setSessionKey test end"; 
		
					Bundle bundle = new Bundle();
					bundle.putString("result", record);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.setData(bundle);
					handler.sendMessage(msg);
				}
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

	
	private void TestenableKeypadFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.enableKeypad111(index);
			record += "enableKeypad ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "enableKeypad test end"; 

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
	private void TestenableKeypadFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.enableKeyPad1(String.valueOf(sequenceID), "D180EMDK","30000");
			record += "enableKeypad ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			record += "enableKeypad test end"; 

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
	private void TestenableKeypadFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		for(int i=0;i<200;i++)
		{
			try{
				step++;
				byte[] by = base.enableKeypad(String.valueOf(sequenceID), "D180EMDK","10000");
				byte[] by1 = base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
				record += "enableKeypad ret: " + new String(by) + "      disableKeyPad ret" + new String(by1) + "   times:" + (i+1) + "\n";
				step++;
				
				record += "enableKeypad and disableKeyPad test end"; 
	
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
				Thread.sleep(500);
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
	private void TestdisableKeyPadFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.disableKeyPad111(index);
			record += "disableKeyPad ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "disableKeyPad test end"; 

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
	private void TestdisableKeyPadFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.disableKeyPad1(String.valueOf(sequenceID), "D180EMDK");
			record += "disableKeyPad ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "disableKeyPad test end"; 

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
	private void TestpromptPINFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.promptPIN111(index);
			record += "promptPIN ret: " + new String(by) +"\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptPIN test end"; 

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
			//progressDialog.dismiss();
		}
	}

	private void TestpromptPINFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.promptPIN(String.valueOf(sequenceID), "D180EMDK", maskP, "", "4", "8", "Enter PIN", "", "", "15000");
			record += "promptPIN ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptPIN test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptPINFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.promptPIN(String.valueOf(sequenceID), "D180EMDK", maskP, "", "4", "8", "Enter PIN", "$1,000.00", "$1.00", "15000");
			record += "promptPIN ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptPIN test end"; 

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
			//progressDialog.dismiss();
		}
	}
	
	private void TestpromptPINFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.promptPIN(String.valueOf(sequenceID), "D180EMDK", "", "", "4", "8", "Pin-input", "$1,000.00", "$1.00", "30000");
			record +="promptPIN ret:"+new String(by)+"\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptPIN test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptPINFUN5()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.promptPIN(String.valueOf(sequenceID), "D180EMDK", maskP, "", "0", "12", "Enter PIN", "$1,000.00", "$1.00", "15000");
			record +="promptPIN ret:"+new String(by)+"\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptPIN test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptPINFUN6()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.promptPIN(String.valueOf(sequenceID), "D180EMDK", maskP, "", "1", "13", "Enter PIN", "$1,000.00", "$1.00", "15000");
			record +="promptPIN ret:"+new String(by)+"\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptPIN test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptPINFUN7()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.promptPIN(String.valueOf(sequenceID), "D180EMDK", maskP, "1", "", "", "Enter PIN", "$1,000.00", "$1.00", "15000");
			record +="promptPIN ret:"+new String(by)+"\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptPIN test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptPINFUN8()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.promptPIN(String.valueOf(sequenceID), "D180EMDK", maskP, "1", "4", "8", "Enter PIN", "$1,000.00", "$1.00", "20000");
			record +="promptPIN ret:"+new String(by)+"\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptPIN test end"; 

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
			//progressDialog.dismiss();
		}
	}

	
	private void downLoadParaFUN1()
	{
		String record = "";
		sequenceID++;
		emv = EmvManager.getInstance(BaseActivity.this);
		int step = 0;
		try{
			
			EMV_PARAM param = emv.getParameter();
			//set merchant id
			System.arraycopy(new String("111111111111111").getBytes(), 0, param.MerchId, 0, 15);
			//set term id
			System.arraycopy(new String("12345678").getBytes(), 0, param.TermId, 0, 8);
			//set country code / currency code
			byte[] countryCode = new byte[] { 0x01, 0x24 };  
			byte[] currencyCode = new byte[] { 0x01, 0x24 };
			System.arraycopy(countryCode, 0, param.CountryCode, 0, 2);
			System.arraycopy(currencyCode, 0, param.TransCurrCode, 0, 2);
			
			byte[] termCap = new byte[] {(byte)0xe0, (byte)0xF8, (byte)0xE8}; 
			System.arraycopy(termCap, 0, param.Capability, 0, 3);
			
			record += "downloadParam ret: "+"\n";
			byte[] by=emv.setParameter(param);
			step++;
			
			record +=new String(by)+"\n";
			System.out.println(new String(by));
			step++;
			
			record += "downloadParam test end"; 

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
	
	private void downLoadAppFUN1()
	{
		String record = "";
		sequenceID++;
		emv = EmvManager.getInstance(BaseActivity.this);
		int step = 0;
		try{
			
			//锟斤拷锟斤拷锟斤拷APP锟斤拷锟斤拷锟斤拷
			EMV_APPLIST app4 = new EMV_APPLIST();
			byte[] appName4 = new String("pboc").getBytes();
			System.arraycopy(appName4, 0, app4.AppName, 0, appName4.length);
			byte[] aid4 = new byte[] { (byte)0xA0, 0x00, 0x00, 0x03, 0x33 };	//pboc rid
			System.arraycopy(aid4, 0, app4.AID, 0, aid4.length);
			app4.AidLen = (byte)0x05;
			app4.FloorLimit = 100000;
			app4.Threshold = 50000;

			EMV_APPLIST app = new EMV_APPLIST();
			byte[] appName = new String("visa").getBytes();
			System.arraycopy(appName, 0, app.AppName, 0, appName.length);
			byte[] aid = new byte[] { (byte)0xA0, 0x00, 0x00, 0x00, 0x03 ,0x20,0x10};	//visa rid
			System.arraycopy(aid, 0, app.AID, 0, aid.length);
			app.AidLen = (byte)0x07;
			app.FloorLimit = 100000;
			app.Threshold = 50000;
			
			byte[] actDenial = new byte[] {0x00, 0x10, 0x00, 0x00, 0x00};
			System.arraycopy(actDenial, 0, app.TACDenial, 0, actDenial.length);
			byte[] actOnline = new byte[] {(byte)0xD8, 0x40, 0x04, (byte)0xF8, 0x00};
			System.arraycopy(actOnline, 0, app.TACOnline, 0, actOnline.length);
			byte[] actDefault = new byte[] {(byte)0xD8, 0x40, 0x00, (byte)0xA8, 0x00};
			System.arraycopy(actDefault, 0, app.TACDefault, 0, actDefault.length);
			
			byte[] aquirerId = new byte[] {0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
			System.arraycopy(aquirerId, 0, app.AcquierId, 0, aquirerId.length);
			
			byte[] dDol = new byte[] {0x03, (byte)0x9F, 0x37, 0x04};
			System.arraycopy(dDol, 0, app.dDOL, 0, dDol.length);
			
			byte[] tDol = new byte[] {(byte)0x0F, (byte)0x9F, 0x02, 0x06, (byte)0x5F, (byte)0x2A, 0x02, (byte)0x9A,
					0x03, (byte)0x9C, 0x01, (byte)0x95, 0x05, (byte)0x9F, 0x37, 0x04};
			System.arraycopy(tDol, 0, app.dDOL, 0, tDol.length);
			
			//master card application definition//
			EMV_APPLIST app_master = new EMV_APPLIST();
			byte[] appName_master = new String("master").getBytes();
			System.arraycopy(appName_master, 0, app_master.AppName, 0, appName_master.length);
			byte[] aid_master = new byte[] {(byte)0xA0, 0x00, 0x00, 0x00, 0x04};		//master card
			System.arraycopy(aid_master, 0, app_master.AID, 0, aid_master.length);
			app_master.AidLen = (byte)0x05;
			app_master.FloorLimit = 100000;
			app_master.Threshold = 50000;
			//?end master definition?//
			//jcb card application definition//
			EMV_APPLIST app_jcb = new EMV_APPLIST();
			byte[] appName_jcb = new String("jcb").getBytes();
			System.arraycopy(appName_jcb, 0, app_jcb.AppName, 0, appName_jcb.length);
			byte[] aid_jcb = new byte[] {(byte)0xA0, 0x00, 0x00, 0x00, 0x65};		//jcb
			System.arraycopy(aid_jcb, 0, app_jcb.AID, 0, aid_jcb.length);
			app_jcb.AidLen = (byte)0x05;
			app_jcb.FloorLimit = 100000;
			app_jcb.Threshold = 50000;		
			//?end jcb definition?//
			
			
			EMV_APPLIST app1 = new EMV_APPLIST();
			byte[] appName1 = new String("flash").getBytes();
			System.arraycopy(appName1, 0, app1.AppName, 0, appName1.length);
			byte[] aid1 = new byte[] { (byte)0xA0, 0x00, 0x00, 0x00, 0x03 ,0x10,0x10};	
			System.arraycopy(aid1, 0, app1.AID, 0, aid1.length);
			app1.AidLen = (byte)0x07;
			app1.FloorLimit = 100000;
			app1.Threshold = 50000;
			
			byte[] actDenial1 = new byte[] {0x00, 0x10, 0x00, 0x00, 0x00};
			System.arraycopy(actDenial1, 0, app1.TACDenial, 0, actDenial1.length);
			byte[] actOnline1 = new byte[] {(byte)0xD8, 0x40, 0x04, (byte)0xF8, 0x00};
			System.arraycopy(actOnline1, 0, app1.TACOnline, 0, actOnline1.length);
			byte[] actDefault1 = new byte[] {(byte)0xD8, 0x40, 0x00, (byte)0xA8, 0x00};
			System.arraycopy(actDefault1, 0, app1.TACDefault, 0, actDefault1.length);
			byte[] aquirerId1 = new byte[] {(byte)0x00, 0x00, 0x00, (byte)0x21, 0x34, 0x56};
			System.arraycopy(aquirerId1, 0, app1.AcquierId, 0, aquirerId1.length);		
			byte[] dDol1 = new byte[] {(byte)0x03, (byte)0x9F, 0x37, (byte)0x04};
			System.arraycopy(dDol1, 0, app1.dDOL, 0, dDol1.length);			
			byte[] tDol1 = new byte[] {(byte)0x0F, (byte)0x9F, (byte)0x02, (byte)0x06, (byte)0x5F, (byte)0x2A, (byte)0x02, (byte)0x9A,
					(byte)0x03, (byte)0x9C, (byte)0x01, (byte)0x95, (byte)0x05, (byte)0x9F, (byte)0x37, (byte)0x04};
			System.arraycopy(tDol1, 0, app1.dDOL, 0, tDol1.length);
			
			
			//APP2
			EMV_APPLIST app2 = new EMV_APPLIST();
			byte[] appName2 = new String("flash").getBytes();
			System.arraycopy(appName2, 0, app2.AppName, 0, appName2.length);
			byte[] aid2 = new byte[] { (byte)0xA0, 0x00, 0x00, 0x03, 0x33,0x00,0x00};	//pboc rid
			System.arraycopy(aid2, 0, app2.AID, 0, aid2.length);
			app2.AidLen = (byte)0x07;
			app2.FloorLimit = 100000;
			app2.Threshold = 50000;
			
			byte[] actDenial2 = new byte[] {0x00, 0x10, 0x00, 0x00, 0x00};
			System.arraycopy(actDenial2, 0, app2.TACDenial, 0, actDenial2.length);
			byte[] actOnline2 = new byte[] {(byte)0xD8, 0x40, 0x04, (byte)0xF8, 0x00};
			System.arraycopy(actOnline2, 0, app2.TACOnline, 0, actOnline2.length);
			byte[] actDefault2 = new byte[] {(byte)0xD8, 0x40, 0x00, (byte)0xA8, 0x00};
			System.arraycopy(actDefault2, 0, app2.TACDefault, 0, actDefault2.length);			
			byte[] aquirerId2 = new byte[] {(byte)0x01, 0x02, 0x03, (byte)0x04, 0x05, 0x06};
			System.arraycopy(aquirerId2, 0, app2.AcquierId, 0, aquirerId2.length);			
			byte[] dDol2 = new byte[] {(byte)0x03, (byte)0x9F, 0x37, (byte)0x04};
			System.arraycopy(dDol2, 0, app2.dDOL, 0, dDol2.length);			
			byte[] tDol2 = new byte[] {(byte)0x0F, (byte)0x9F, (byte)0x02, (byte)0x06, (byte)0x5F, (byte)0x2A, (byte)0x02, (byte)0x9A,
					(byte)0x03, (byte)0x9C, (byte)0x01, (byte)0x95, (byte)0x05, (byte)0x9F, (byte)0x37, (byte)0x04};
			System.arraycopy(tDol2, 0, app2.dDOL, 0, tDol2.length);
			
			
			//APP3
			EMV_APPLIST app3 = new EMV_APPLIST();
			byte[] appName3 = new String("flash").getBytes();
			System.arraycopy(appName3, 0, app3.AppName, 0, appName3.length);
			byte[] aid3 = new byte[] { (byte)0xA0, 0x00, 0x00, 0x00, 0x04 };	//pboc rid
			System.arraycopy(aid3, 0, app3.AID, 0, aid3.length);
			app3.AidLen = (byte)0x05;
			app3.FloorLimit = 100000;
			app3.Threshold = 50000;
			
			byte[] actDenial3 = new byte[] {0x04, 0x00, 0x00, 0x00, 0x00};
			System.arraycopy(actDenial3, 0, app3.TACDenial, 0, actDenial3.length);
			byte[] actOnline3 = new byte[] {(byte)0xF8, 0x50, (byte)0xAC, (byte)0xF8, 0x00};
			System.arraycopy(actOnline3, 0, app3.TACOnline, 0, actOnline3.length);
			byte[] actDefault3 = new byte[] {(byte)0xFC, 0x50, (byte)0xAC, (byte)0xA0, 0x00};
			System.arraycopy(actDefault3, 0, app3.TACDefault, 0, actDefault3.length);	
			byte[] aquirerId3 = new byte[] {(byte)0x00, 0x00, 0x00, (byte)0x12, 0x34, 0x56};
			System.arraycopy(aquirerId3, 0, app3.AcquierId, 0, aquirerId3.length);
			byte[] dDol3 = new byte[] {(byte)0x03, (byte)0x9F, 0x37, (byte)0x04};
			System.arraycopy(dDol3, 0, app3.dDOL, 0, dDol3.length);
			byte[] tDol3 = new byte[] {(byte)0x0F, (byte)0x9F, (byte)0x02, (byte)0x06, (byte)0x5F, (byte)0x2A, (byte)0x02, (byte)0x9A,
					(byte)0x03, (byte)0x9C, (byte)0x01, (byte)0x95, (byte)0x05, (byte)0x9F, (byte)0x37, (byte)0x04};
			System.arraycopy(tDol3, 0, app3.dDOL, 0, tDol3.length);
			
			
			//APP5   VISA_VSDC_APP
			EMV_APPLIST app5 = new EMV_APPLIST();
			byte[] appName5 = new String("VISA CREDIT").getBytes();
			System.arraycopy(appName5, 0, app5.AppName, 0, appName5.length);
			byte[] aid5 = new byte[] {(byte)0xA0, 0x00, 0x00, 0x00, 0x03, 0x10, 0x10};	//visa rid
			System.arraycopy(aid5, 0, app5.AID, 0, aid5.length);
			app5.AidLen = 0x07;
			app5.SelFlag= 0x00;
			app5.Priority=0x00;
			app5.TargetPer=0x00;
			app5.MaxTargetPer=0x00;
			app5.FloorLimitCheck=0x01;
			app5.RandTransSel=0x01;
			app5.VelocityCheck=0x01;
			app5.FloorLimit = 0;
			app5.Threshold = 0;
			
			byte[] actDenial5 = new byte[] {0x00, 0x10, 0x00, 0x00, 0x00};
			System.arraycopy(actDenial5, 0, app5.TACDenial, 0, actDenial5.length);
			byte[] actOnline5 = new byte[] {(byte)0xD8, 0x40, 0x04, (byte)0xF8, 0x00};
			System.arraycopy(actOnline5, 0, app5.TACOnline, 0, actOnline5.length);
			byte[] actDefault5 = new byte[] {(byte)0xD8, 0x40, 0x00, (byte)0xA8, 0x00};
			System.arraycopy(actDefault5, 0, app5.TACDefault, 0, actDefault5.length);
			
			byte[] aquirerId5 = new byte[] {0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
			System.arraycopy(aquirerId5, 0, app5.AcquierId, 0, aquirerId5.length);
			
			byte[] dDol5 = new byte[] {0x03, (byte)0x9F, 0x37, 0x04};
			System.arraycopy(dDol5, 0, app5.dDOL, 0, dDol5.length);
			
			byte[] tDol5 = new byte[] {0x0F, (byte)0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02, (byte)0x9A, 0x03, (byte)0x9C, 0x01, (byte)0x95, 0x05, (byte)0x9F, 0x37, 0x04};
			System.arraycopy(tDol5, 0, app5.tDOL, 0, tDol5.length);
			byte[] verion5 = new byte[] {0x00, (byte)0x8c};
			System.arraycopy(verion5, 0, app5.Version, 0, verion5.length);
			
			//APP6   VISA_ELECTRON_APP
			EMV_APPLIST app6 = new EMV_APPLIST();
			byte[] appName6 = new String("VISA ELECTRON").getBytes();
			System.arraycopy(appName6, 0, app6.AppName, 0, appName6.length);
			byte[] aid6 = new byte[] {(byte)0xA0, 0x00, 0x00, 0x00, 0x03, 0x20, 0x10};	//visa rid
			System.arraycopy(aid6, 0, app6.AID, 0, aid6.length);
			app6.AidLen = 0x07;
			app6.SelFlag= 0x00;
			app6.Priority=0x00;
			app6.TargetPer=0x00;
			app6.MaxTargetPer=0x00;
			app6.FloorLimitCheck=0x01;
			app6.RandTransSel=0x01;
			app6.VelocityCheck=0x01;
			app6.FloorLimit = 0;
			app6.Threshold = 0;
			
			byte[] actDenial6 = new byte[] {0x00, 0x10, 0x00, 0x00, 0x00};
			System.arraycopy(actDenial6, 0, app6.TACDenial, 0, actDenial6.length);
			byte[] actOnline6 = new byte[] {(byte)0xD8, 0x40, 0x04, (byte)0xF8, 0x00};
			System.arraycopy(actOnline6, 0, app6.TACOnline, 0, actOnline6.length);
			byte[] actDefault6 = new byte[] {(byte)0xD8, 0x40, 0x00, (byte)0xA8, 0x00};
			System.arraycopy(actDefault6, 0, app6.TACDefault, 0, actDefault6.length);
			
			byte[] aquirerId6 = new byte[] {0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
			System.arraycopy(aquirerId6, 0, app6.AcquierId, 0, aquirerId6.length);
			
			byte[] dDol6 = new byte[] {0x03, (byte)0x9F, 0x37, 0x04};
			System.arraycopy(dDol6, 0, app6.dDOL, 0, dDol6.length);
			
			byte[] tDol6 = new byte[] {0x0F, (byte)0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02, (byte)0x9A, 0x03, (byte)0x9C, 0x01, (byte)0x95, 0x05, (byte)0x9F, 0x37, 0x04};
			System.arraycopy(tDol6, 0, app6.tDOL, 0, tDol6.length);
			byte[] verion6 = new byte[] {0x00, (byte)0x8c};
			System.arraycopy(verion6, 0, app6.Version, 0, verion6.length);
			
			
			//APP7   
			EMV_APPLIST app7 = new EMV_APPLIST();
			byte[] appName7 = new String("").getBytes();
			System.arraycopy(appName7, 0, app7.AppName, 0, appName7.length);
			byte[] aid7 = new byte[] {(byte)0xA0, 0x00, 0x00, 0x00, 0x25, 0x01};	//visa rid
			System.arraycopy(aid7, 0, app7.AID, 0, aid7.length);
			app7.AidLen = 0x06;
			app7.SelFlag= 0x00;
			app7.Priority=0x00;
			app7.TargetPer=0x00;
			app7.MaxTargetPer=0x00;
			app7.FloorLimitCheck=0x01;
			app7.RandTransSel=0x00;
			app7.VelocityCheck=0x01;
			app7.FloorLimit = 0;
			app7.Threshold = 0;
			
			byte[] actDenial7 = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00};
			System.arraycopy(actDenial7, 0, app7.TACDenial, 0, actDenial7.length);
			byte[] actOnline7 = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00};
			System.arraycopy(actOnline7, 0, app7.TACOnline, 0, actOnline7.length);
			byte[] actDefault7 = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00};
			System.arraycopy(actDefault7, 0, app7.TACDefault, 0, actDefault7.length);
			
			byte[] aquirerId7 = new byte[] {0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
			System.arraycopy(aquirerId7, 0, app7.AcquierId, 0, aquirerId7.length);
			
			byte[] dDol7 = new byte[] {0x03, (byte)0x9F, 0x37, 0x04};
			System.arraycopy(dDol7, 0, app7.dDOL, 0, dDol7.length);
			
			byte[] tDol7 = new byte[] {(byte)0x9F, 0x37, 0x04};
			System.arraycopy(tDol7, 0, app7.tDOL, 0, tDol7.length);
			byte[] verion7 = new byte[] {0x00, 0x01};
			System.arraycopy(verion7, 0, app7.Version, 0, verion7.length);
			
			//APP8  
			EMV_APPLIST app8 = new EMV_APPLIST();
			byte[] appName8 = new String("").getBytes();
			System.arraycopy(appName8, 0, app8.AppName, 0, appName8.length);
			byte[] aid8 = new byte[] {(byte)0xA0, 0x00, 0x00, 0x00, 0x25, 0x01};	//visa rid
			System.arraycopy(aid8, 0, app8.AID, 0, aid8.length);
			app8.AidLen = 0x06;
			app8.SelFlag= 0x00;
			app8.Priority=0x00;
			app8.TargetPer=0x00;
			app8.MaxTargetPer=0x00;
			app8.FloorLimitCheck=0x01;
			app8.RandTransSel=0x01;
			app8.VelocityCheck=0x01;
			app8.FloorLimit = 0;
			app8.Threshold = 0;
			
			byte[] actDenial8 = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00};
			System.arraycopy(actDenial8, 0, app8.TACDenial, 0, actDenial8.length);
			byte[] actOnline8 = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00};
			System.arraycopy(actOnline8, 0, app8.TACOnline, 0, actOnline8.length);
			byte[] actDefault8 = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00};
			System.arraycopy(actDefault8, 0, app8.TACDefault, 0, actDefault8.length);
			
			byte[] aquirerId8 = new byte[] {0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
			System.arraycopy(aquirerId8, 0, app8.AcquierId, 0, aquirerId8.length);
			
			byte[] dDol8 = new byte[] {0x03, (byte)0x9F, 0x37, 0x04};
			System.arraycopy(dDol8, 0, app8.dDOL, 0, dDol8.length);
			
			byte[] tDol8 = new byte[] {(byte)0x9F, 0x37, 0x04};
			System.arraycopy(tDol8, 0, app8.tDOL, 0, tDol8.length);
			byte[] verion8 = new byte[] {0x00, 0x01};
			System.arraycopy(verion8, 0, app8.Version, 0, verion8.length);
			
	
			byte[] by=null;
			record += "downloadAPP ret: "+"\n";
			by=emv.addApp(app);
			record +=new String(by)+ "\n";
			by=emv.addApp(app1);
			record +=" "+ new String(by)+ "\n";
			by=emv.addApp(app2);
			record +=" "+ new String(by)+ "\n";
			by=emv.addApp(app3);
			record +=" "+ new String(by)+ "\n";
			by=emv.addApp(app4);
			record +=" "+ new String(by)+ "\n";
			by=emv.addApp(app7);
			record +=" "+ new String(by)+ "\n";
			by=emv.addApp(app8);
			record +=" "+ new String(by)+ "\n";
			by=emv.addApp(app_master);
			record +=" "+ new String(by)+ "\n";
			by=emv.addApp(app_jcb);
			record +=" "+ new String(by) + "\n";
			System.out.println(new String(by));
			step++; 
			
			record += "downloadAPP test end"; 
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
	private void downLoadCAPKFUN1()
	{
		String record = "";
		emv = EmvManager.getInstance(BaseActivity.this);
		int step = 0;
		sequenceID++;
		try{
			
			//锟斤拷锟斤拷CAPK锟斤拷写锟斤拷
			int tmp[];
			//capk01   capk_visa_v01
			capk01 = new EMV_CAPK();
			tmp = new int[]{0xA0,0x00,0x00,0x00,0x03};
			IntArray2ByteArray(tmp, 0, capk01.RID, 0, tmp.length);
			capk01.KeyID = 0x01;
			capk01.HashInd = 0x01;
			capk01.ArithInd = 0x01;
			capk01.ModulLen = (byte) 128;
			tmp = new int[]{0xC6,0x96,0x03,0x42,0x13,0xD7,0xD8,0x54,0x69,0x84,0x57,0x9D,0x1D,0x0F,0x0E,0xA5,
			         0x19,0xCF,0xF8,0xDE,0xFF,0xC4,0x29,0x35,0x4C,0xF3,0xA8,0x71,0xA6,0xF7,0x18,0x3F,
			         0x12,0x28,0xDA,0x5C,0x74,0x70,0xC0,0x55,0x38,0x71,0x00,0xCB,0x93,0x5A,0x71,0x2C,
			         0x4E,0x28,0x64,0xDF,0x5D,0x64,0xBA,0x93,0xFE,0x7E,0x63,0xE7,0x1F,0x25,0xB1,0xE5,
			         0xF5,0x29,0x85,0x75,0xEB,0xE1,0xC6,0x3A,0xA6,0x17,0x70,0x69,0x17,0x91,0x1D,0xC2,
			         0xA7,0x5A,0xC2,0x8B,0x25,0x1C,0x7E,0xF4,0x0F,0x23,0x65,0x91,0x24,0x90,0xB9,0x39,
			         0xBC,0xA2,0x12,0x4A,0x30,0xA2,0x8F,0x54,0x40,0x2C,0x34,0xAE,0xCA,0x33,0x1A,0xB6,
			         0x7E,0x1E,0x79,0xB2,0x85,0xDD,0x57,0x71,0xB5,0xD9,0xFF,0x79,0xEA,0x63,0x0B,0x75};
			IntArray2ByteArray(tmp, 0, capk01.Modul, 0, tmp.length);
			capk01.ExpLen = 1;
			tmp = new int[]{0x03};
			IntArray2ByteArray(tmp, 0, capk01.Exp, 0, tmp.length);
			tmp = new int[]{0x09,0x12,0x31};
			IntArray2ByteArray(tmp, 0, capk01.ExpDate, 0, tmp.length);
			tmp = new int[]{0xD3,0x4A,0x6A,0x77,0x60,0x11,0xC7,0xE7,0xCE,0x3A,0xEC,0x5F,0x03,0xAD,0x2F,0x8C,
			         0xFC,0x55,0x03,0xCC};
			IntArray2ByteArray(tmp, 0, capk01.CheckSum, 0, tmp.length);
			
			
			//capk02  VISA 1152 bits Live Key 07
			capk02 = new EMV_CAPK();
			tmp = new int[]{0xA0,0x00,0x00,0x00,0x03};
			IntArray2ByteArray(tmp, 0, capk02.RID, 0, tmp.length);
			capk02.KeyID = 0x07;
			capk02.HashInd = 0x01;
			capk02.ArithInd = 0x01;
			capk02.ModulLen = (byte) 144;
			tmp = new int[]{0xA8,0x9F,0x25,0xA5,0x6F,0xA6,0xDA,0x25,0x8C,0x8C,0xA8,0xB4,0x04,0x27,0xD9,0x27,
			         0xB4,0xA1,0xEB,0x4D,0x7E,0xA3,0x26,0xBB,0xB1,0x2F,0x97,0xDE,0xD7,0x0A,0xE5,0xE4,
			         0x48,0x0F,0xC9,0xC5,0xE8,0xA9,0x72,0x17,0x71,0x10,0xA1,0xCC,0x31,0x8D,0x06,0xD2,
			         0xF8,0xF5,0xC4,0x84,0x4A,0xC5,0xFA,0x79,0xA4,0xDC,0x47,0x0B,0xB1,0x1E,0xD6,0x35,
			         0x69,0x9C,0x17,0x08,0x1B,0x90,0xF1,0xB9,0x84,0xF1,0x2E,0x92,0xC1,0xC5,0x29,0x27,
			         0x6D,0x8A,0xF8,0xEC,0x7F,0x28,0x49,0x20,0x97,0xD8,0xCD,0x5B,0xEC,0xEA,0x16,0xFE,
			         0x40,0x88,0xF6,0xCF,0xAB,0x4A,0x1B,0x42,0x32,0x8A,0x1B,0x99,0x6F,0x92,0x78,0xB0,
			         0xB7,0xE3,0x31,0x1C,0xA5,0xEF,0x85,0x6C,0x2F,0x88,0x84,0x74,0xB8,0x36,0x12,0xA8,
			         0x2E,0x4E,0x00,0xD0,0xCD,0x40,0x69,0xA6,0x78,0x31,0x40,0x43,0x3D,0x50,0x72,0x5F};
			IntArray2ByteArray(tmp, 0, capk02.Modul, 0, tmp.length);
			capk02.ExpLen = 1;
			tmp = new int[]{0x03};
			IntArray2ByteArray(tmp, 0, capk02.Exp, 0, tmp.length);
			tmp = new int[]{0x12,0x12,0x31};
			IntArray2ByteArray(tmp, 0, capk02.ExpDate, 0, tmp.length);
			tmp = new int[]{0xB4,0xBC,0x56,0xCC,0x4E,0x88,0x32,0x49,0x32,0xCB,0xC6,0x43,0xD6,0x89,0x8F,0x6F,
			         0xE5,0x93,0xB1,0x72};
			IntArray2ByteArray(tmp, 0, capk02.CheckSum, 0, tmp.length);
			
			
			//init capk03   VISA 1408 BITS LIVE KEY 08
			capk03 = new EMV_CAPK();
			tmp = new int[]{0xA0,0x00,0x00,0x00,0x03};
			IntArray2ByteArray(tmp, 0, capk03.RID, 0, tmp.length);
			capk03.KeyID = 0x08;
			capk03.HashInd = 0x01;
			capk03.ArithInd = 0x01;
			capk03.ModulLen = (byte) 176;
			tmp = new int[]{0xD9,0xFD,0x6E,0xD7,0x5D,0x51,0xD0,0xE3,0x06,0x64,0xBD,0x15,0x70,0x23,0xEA,0xA1, 
			         0xFF,0xA8,0x71,0xE4,0xDA,0x65,0x67,0x2B,0x86,0x3D,0x25,0x5E,0x81,0xE1,0x37,0xA5, 
			         0x1D,0xE4,0xF7,0x2B,0xCC,0x9E,0x44,0xAC,0xE1,0x21,0x27,0xF8,0x7E,0x26,0x3D,0x3A, 
			         0xF9,0xDD,0x9C,0xF3,0x5C,0xA4,0xA7,0xB0,0x1E,0x90,0x70,0x00,0xBA,0x85,0xD2,0x49, 
			         0x54,0xC2,0xFC,0xA3,0x07,0x48,0x25,0xDD,0xD4,0xC0,0xC8,0xF1,0x86,0xCB,0x02,0x0F, 
			         0x68,0x3E,0x02,0xF2,0xDE,0xAD,0x39,0x69,0x13,0x3F,0x06,0xF7,0x84,0x51,0x66,0xAC, 
			         0xEB,0x57,0xCA,0x0F,0xC2,0x60,0x34,0x45,0x46,0x98,0x11,0xD2,0x93,0xBF,0xEF,0xBA, 
			         0xFA,0xB5,0x76,0x31,0xB3,0xDD,0x91,0xE7,0x96,0xBF,0x85,0x0A,0x25,0x01,0x2F,0x1A, 
			         0xE3,0x8F,0x05,0xAA,0x5C,0x4D,0x6D,0x03,0xB1,0xDC,0x2E,0x56,0x86,0x12,0x78,0x59, 
			         0x38,0xBB,0xC9,0xB3,0xCD,0x3A,0x91,0x0C,0x1D,0xA5,0x5A,0x5A,0x92,0x18,0xAC,0xE0, 
			         0xF7,0xA2,0x12,0x87,0x75,0x26,0x82,0xF1,0x58,0x32,0xA6,0x78,0xD6,0xE1,0xED,0x0B};
			IntArray2ByteArray(tmp, 0, capk03.Modul, 0, tmp.length);
			capk03.ExpLen = 1;
			tmp = new int[]{0x03};
			IntArray2ByteArray(tmp, 0, capk03.Exp, 0, tmp.length);
			tmp = new int[]{0x14,0x12,0x31};
			IntArray2ByteArray(tmp, 0, capk03.ExpDate, 0, tmp.length);
			tmp = new int[]{0x20,0xD2,0x13,0x12,0x69,0x55,0xDE,0x20,0x5A,0xDC,0x2F,0xD2,0x82,0x2B,0xD2,0x2D,
			         0xE2,0x1C,0xF9,0xA8};
			IntArray2ByteArray(tmp, 0, capk03.CheckSum, 0, tmp.length);
		
			//init capk04   VISA 1984 BITS LIVE KEY 09
			capk04 = new EMV_CAPK();
			tmp = new int[]{0xA0,0x00,0x00,0x00,0x03};
			IntArray2ByteArray(tmp, 0, capk04.RID, 0, tmp.length);
			capk04.KeyID = 0x09;
			capk04.HashInd = 0x01;
			capk04.ArithInd = 0x01;
			capk04.ModulLen = (byte) 248;
			tmp = new int[]{0x9D,0x91,0x22,0x48,0xDE,0x0A,0x4E,0x39,0xC1,0xA7,0xDD,0xE3,0xF6,0xD2,0x58,0x89, 
			         0x92,0xC1,0xA4,0x09,0x5A,0xFB,0xD1,0x82,0x4D,0x1B,0xA7,0x48,0x47,0xF2,0xBC,0x49, 
			         0x26,0xD2,0xEF,0xD9,0x04,0xB4,0xB5,0x49,0x54,0xCD,0x18,0x9A,0x54,0xC5,0xD1,0x17, 
			         0x96,0x54,0xF8,0xF9,0xB0,0xD2,0xAB,0x5F,0x03,0x57,0xEB,0x64,0x2F,0xED,0xA9,0x5D, 
			         0x39,0x12,0xC6,0x57,0x69,0x45,0xFA,0xB8,0x97,0xE7,0x06,0x2C,0xAA,0x44,0xA4,0xAA, 
			         0x06,0xB8,0xFE,0x6E,0x3D,0xBA,0x18,0xAF,0x6A,0xE3,0x73,0x8E,0x30,0x42,0x9E,0xE9, 
			         0xBE,0x03,0x42,0x7C,0x9D,0x64,0xF6,0x95,0xFA,0x8C,0xAB,0x4B,0xFE,0x37,0x68,0x53, 
			         0xEA,0x34,0xAD,0x1D,0x76,0xBF,0xCA,0xD1,0x59,0x08,0xC0,0x77,0xFF,0xE6,0xDC,0x55, 
			         0x21,0xEC,0xEF,0x5D,0x27,0x8A,0x96,0xE2,0x6F,0x57,0x35,0x9F,0xFA,0xED,0xA1,0x94, 
			         0x34,0xB9,0x37,0xF1,0xAD,0x99,0x9D,0xC5,0xC4,0x1E,0xB1,0x19,0x35,0xB4,0x4C,0x18, 
			         0x10,0x0E,0x85,0x7F,0x43,0x1A,0x4A,0x5A,0x6B,0xB6,0x51,0x14,0xF1,0x74,0xC2,0xD7, 
			         0xB5,0x9F,0xDF,0x23,0x7D,0x6B,0xB1,0xDD,0x09,0x16,0xE6,0x44,0xD7,0x09,0xDE,0xD5, 
			         0x64,0x81,0x47,0x7C,0x75,0xD9,0x5C,0xDD,0x68,0x25,0x46,0x15,0xF7,0x74,0x0E,0xC0, 
			         0x7F,0x33,0x0A,0xC5,0xD6,0x7B,0xCD,0x75,0xBF,0x23,0xD2,0x8A,0x14,0x08,0x26,0xC0, 
			         0x26,0xDB,0xDE,0x97,0x1A,0x37,0xCD,0x3E,0xF9,0xB8,0xDF,0x64,0x4A,0xC3,0x85,0x01, 
			         0x05,0x01,0xEF,0xC6,0x50,0x9D,0x7A,0x41};
			IntArray2ByteArray(tmp, 0, capk04.Modul, 0, tmp.length);
			capk04.ExpLen = 1;
			tmp = new int[]{0x03};
			IntArray2ByteArray(tmp, 0, capk04.Exp, 0, tmp.length);
			tmp = new int[]{0x16,0x12,0x31};
			IntArray2ByteArray(tmp, 0, capk04.ExpDate, 0, tmp.length);
			tmp = new int[]{0x1F,0xF8,0x0A,0x40,0x17,0x3F,0x52,0xD7,0xD2,0x7E,0x0F,0x26,0xA1,0x46,0xA1,0xC8, 
			         0xCC,0xB2,0x90,0x46};
			IntArray2ByteArray(tmp, 0, capk04.CheckSum, 0, tmp.length);
		
			
			//init capk05   Mastercard 1024 bits Live Key 03
			capk05 = new EMV_CAPK();
			tmp = new int[]{0xA0,0x00,0x00,0x00,0x04};
			IntArray2ByteArray(tmp, 0, capk05.RID, 0, tmp.length);
			capk05.KeyID = 0x03;
			capk05.HashInd = 0x01;
			capk05.ArithInd = 0x01;
			capk05.ModulLen = (byte) 128;
			tmp = new int[]{0xC2,0x49,0x07,0x47,0xFE,0x17,0xEB,0x05,0x84,0xC8,0x8D,0x47,0xB1,0x60,0x27,0x04,
			         0x15,0x0A,0xDC,0x88,0xC5,0xB9,0x98,0xBD,0x59,0xCE,0x04,0x3E,0xDE,0xBF,0x0F,0xFE,
			         0xE3,0x09,0x3A,0xC7,0x95,0x6A,0xD3,0xB6,0xAD,0x45,0x54,0xC6,0xDE,0x19,0xA1,0x78,
			         0xD6,0xDA,0x29,0x5B,0xE1,0x5D,0x52,0x20,0x64,0x5E,0x3C,0x81,0x31,0x66,0x6F,0xA4,
			         0xBE,0x5B,0x84,0xFE,0x13,0x1E,0xA4,0x4B,0x03,0x93,0x07,0x63,0x8B,0x9E,0x74,0xA8,
			         0xC4,0x25,0x64,0xF8,0x92,0xA6,0x4D,0xF1,0xCB,0x15,0x71,0x2B,0x73,0x6E,0x33,0x74,
			         0xF1,0xBB,0xB6,0x81,0x93,0x71,0x60,0x2D,0x89,0x70,0xE9,0x7B,0x90,0x07,0x93,0xC7,
			         0xC2,0xA8,0x9A,0x4A,0x16,0x49,0xA5,0x9B,0xE6,0x80,0x57,0x4D,0xD0,0xB6,0x01,0x45};
			IntArray2ByteArray(tmp, 0, capk05.Modul, 0, tmp.length);
			capk05.ExpLen = 1;
			tmp = new int[]{0x03};
			IntArray2ByteArray(tmp, 0, capk05.Exp, 0, tmp.length);
			tmp = new int[]{0x09,0x12,0x31};
			IntArray2ByteArray(tmp, 0, capk05.ExpDate, 0, tmp.length);
			tmp = new int[]{0x5A,0xDD,0xF2,0x1D,0x09,0x27,0x86,0x61,0x14,0x11,0x79,0xCB,0xEF,0xF2,0x72,0xEA,
			         0x38,0x4B,0x13,0xBB};
			IntArray2ByteArray(tmp, 0, capk05.CheckSum, 0, tmp.length);
			
			
			//init capk06  Mastercard 1152 bits Live Key 04
			capk06 = new EMV_CAPK();
			tmp = new int[]{0xA0,0x00,0x00,0x00,0x04};
			IntArray2ByteArray(tmp, 0, capk06.RID, 0, tmp.length);
			capk06.KeyID = 0x04;
			capk06.HashInd = 0x01;
			capk06.ArithInd = 0x01;
			capk06.ModulLen = 114;
			tmp = new int[]{0xA6,0xDA,0x42,0x83,0x87,0xA5,0x02,0xD7,0xDD,0xFB,0x7A,0x74,0xD3,0xF4,0x12,0xBE,
			         0x76,0x26,0x27,0x19,0x7B,0x25,0x43,0x5B,0x7A,0x81,0x71,0x6A,0x70,0x01,0x57,0xDD,
			         0xD0,0x6F,0x7C,0xC9,0x9D,0x6C,0xA2,0x8C,0x24,0x70,0x52,0x7E,0x2C,0x03,0x61,0x6B,
			         0x9C,0x59,0x21,0x73,0x57,0xC2,0x67,0x4F,0x58,0x3B,0x3B,0xA5,0xC7,0xDC,0xF2,0x83,
			         0x86,0x92,0xD0,0x23,0xE3,0x56,0x24,0x20,0xB4,0x61,0x5C,0x43,0x9C,0xA9,0x7C,0x44,
			         0xDC,0x9A,0x24,0x9C,0xFC,0xE7,0xB3,0xBF,0xB2,0x2F,0x68,0x22,0x8C,0x3A,0xF1,0x33,
			         0x29,0xAA,0x4A,0x61,0x3C,0xF8,0xDD,0x85,0x35,0x02,0x37,0x3D,0x62,0xE4,0x9A,0xB2,
			         0x56,0xD2,0xBC,0x17,0x12,0x0E,0x54,0xAE,0xDC,0xED,0x6D,0x96,0xA4,0x28,0x7A,0xCC,
			         0x5C,0x04,0x67,0x7D,0x4A,0x5A,0x32,0x0D,0xB8,0xBE,0xE2,0xF7,0x75,0xE5,0xFE,0xC5};
			IntArray2ByteArray(tmp, 0, capk06.Modul, 0, tmp.length);
			capk06.ExpLen = 1;
			tmp = new int[]{0x03};
			IntArray2ByteArray(tmp, 0, capk06.Exp, 0, tmp.length);
			tmp = new int[]{0x12,0x12,0x31};
			IntArray2ByteArray(tmp, 0, capk06.ExpDate, 0, tmp.length);
			tmp = new int[]{0x38,0x1A,0x03,0x5D,0xA5,0x8B,0x48,0x2E,0xE2,0xAF,0x75,0xF4,0xC3,0xF2,0xCA,0x46,
			         0x9B,0xA4,0xAA,0x6C};
			IntArray2ByteArray(tmp, 0, capk06.CheckSum, 0, tmp.length);
			
			
			//init capk07   Mastercard 1408 bits live Key 05
			capk07 = new EMV_CAPK();
			tmp = new int[]{0xA0,0x00,0x00,0x00,0x04};
			IntArray2ByteArray(tmp, 0, capk07.RID, 0, tmp.length);
			capk07.KeyID = 0x05;
			capk07.HashInd = 0x01;
			capk07.ArithInd = 0x01;
			capk07.ModulLen = (byte) 176;
			tmp = new int[]{0xB8,0x04,0x8A,0xBC,0x30,0xC9,0x0D,0x97,0x63,0x36,0x54,0x3E,0x3F,0xD7,0x09,0x1C,
			         0x8F,0xE4,0x80,0x0D,0xF8,0x20,0xED,0x55,0xE7,0xE9,0x48,0x13,0xED,0x00,0x55,0x5B,
			         0x57,0x3F,0xEC,0xA3,0xD8,0x4A,0xF6,0x13,0x1A,0x65,0x1D,0x66,0xCF,0xF4,0x28,0x4F,
			         0xB1,0x3B,0x63,0x5E,0xDD,0x0E,0xE4,0x01,0x76,0xD8,0xBF,0x04,0xB7,0xFD,0x1C,0x7B,
			         0xAC,0xF9,0xAC,0x73,0x27,0xDF,0xAA,0x8A,0xA7,0x2D,0x10,0xDB,0x3B,0x8E,0x70,0xB2,
			         0xDD,0xD8,0x11,0xCB,0x41,0x96,0x52,0x5E,0xA3,0x86,0xAC,0xC3,0x3C,0x0D,0x9D,0x45,
			         0x75,0x91,0x64,0x69,0xC4,0xE4,0xF5,0x3E,0x8E,0x1C,0x91,0x2C,0xC6,0x18,0xCB,0x22,
			         0xDD,0xE7,0xC3,0x56,0x8E,0x90,0x02,0x2E,0x6B,0xBA,0x77,0x02,0x02,0xE4,0x52,0x2A,
			         0x2D,0xD6,0x23,0xD1,0x80,0xE2,0x15,0xBD,0x1D,0x15,0x07,0xFE,0x3D,0xC9,0x0C,0xA3,
			         0x10,0xD2,0x7B,0x3E,0xFC,0xCD,0x8F,0x83,0xDE,0x30,0x52,0xCA,0xD1,0xE4,0x89,0x38,
			         0xC6,0x8D,0x09,0x5A,0xAC,0x91,0xB5,0xF3,0x7E,0x28,0xBB,0x49,0xEC,0x7E,0xD5,0x97};
			IntArray2ByteArray(tmp, 0, capk07.Modul, 0, tmp.length);
			capk07.ExpLen = 1;
			tmp = new int[]{0x03};
			IntArray2ByteArray(tmp, 0, capk07.Exp, 0, tmp.length);
			tmp = new int[]{0x14,0x12,0x31};
			IntArray2ByteArray(tmp, 0, capk07.ExpDate, 0, tmp.length);
			tmp = new int[]{0xEB,0xFA,0x0D,0x5D,0x06,0xD8,0xCE,0x70,0x2D,0xA3,0xEA,0xE8,0x90,0x70,0x1D,0x45,
			         0xE2,0x74,0xC8,0x45};
			IntArray2ByteArray(tmp, 0, capk07.CheckSum, 0, tmp.length);
			
			
			//init capk08   Mastercard 1948 bits live Key 06
			capk08 = new EMV_CAPK();
			tmp = new int[]{0xA0,0x00,0x00,0x00,0x04};
			IntArray2ByteArray(tmp, 0, capk08.RID, 0, tmp.length);
			capk08.KeyID = 0x06;
			capk08.HashInd = 0x01;
			capk08.ArithInd = 0x01;
			capk08.ModulLen = (byte) 248;
			tmp = new int[]{0xCB,0x26,0xFC,0x83,0x0B,0x43,0x78,0x5B,0x2B,0xCE,0x37,0xC8,0x1E,0xD3,0x34,0x62,
			         0x2F,0x96,0x22,0xF4,0xC8,0x9A,0xAE,0x64,0x10,0x46,0xB2,0x35,0x34,0x33,0x88,0x3F,
			         0x30,0x7F,0xB7,0xC9,0x74,0x16,0x2D,0xA7,0x2F,0x7A,0x4E,0xC7,0x5D,0x9D,0x65,0x73,
			         0x36,0x86,0x5B,0x8D,0x30,0x23,0xD3,0xD6,0x45,0x66,0x76,0x25,0xC9,0xA0,0x7A,0x6B,
			         0x7A,0x13,0x7C,0xF0,0xC6,0x41,0x98,0xAE,0x38,0xFC,0x23,0x80,0x06,0xFB,0x26,0x03,
			         0xF4,0x1F,0x4F,0x3B,0xB9,0xDA,0x13,0x47,0x27,0x0F,0x2F,0x5D,0x8C,0x60,0x6E,0x42,
			         0x09,0x58,0xC5,0xF7,0xD5,0x0A,0x71,0xDE,0x30,0x14,0x2F,0x70,0xDE,0x46,0x88,0x89,
			         0xB5,0xE3,0xA0,0x86,0x95,0xB9,0x38,0xA5,0x0F,0xC9,0x80,0x39,0x3A,0x9C,0xBC,0xE4,
			         0x4A,0xD2,0xD6,0x4F,0x63,0x0B,0xB3,0x3A,0xD3,0xF5,0xF5,0xFD,0x49,0x5D,0x31,0xF3,
			         0x78,0x18,0xC1,0xD9,0x40,0x71,0x34,0x2E,0x07,0xF1,0xBE,0xC2,0x19,0x4F,0x60,0x35,
			         0xBA,0x5D,0xED,0x39,0x36,0x50,0x0E,0xB8,0x2D,0xFD,0xA6,0xE8,0xAF,0xB6,0x55,0xB1,
			         0xEF,0x3D,0x0D,0x7E,0xBF,0x86,0xB6,0x6D,0xD9,0xF2,0x9F,0x6B,0x1D,0x32,0x4F,0xE8,
			         0xB2,0x6C,0xE3,0x8A,0xB2,0x01,0x3D,0xD1,0x3F,0x61,0x1E,0x7A,0x59,0x4D,0x67,0x5C,
			         0x44,0x32,0x35,0x0E,0xA2,0x44,0xCC,0x34,0xF3,0x87,0x3C,0xBA,0x06,0x59,0x29,0x87,
			         0xA1,0xD7,0xE8,0x52,0xAD,0xC2,0x2E,0xF5,0xA2,0xEE,0x28,0x13,0x20,0x31,0xE4,0x8F,
			         0x74,0x03,0x7E,0x3B,0x34,0xAB,0x74,0x7F};
			IntArray2ByteArray(tmp, 0, capk08.Modul, 0, tmp.length);
			capk08.ExpLen = 1;
			tmp = new int[]{0x03};
			IntArray2ByteArray(tmp, 0, capk08.Exp, 0, tmp.length);
			tmp = new int[]{0x16,0x12,0x31};
			IntArray2ByteArray(tmp, 0, capk08.ExpDate, 0, tmp.length);
			tmp = new int[]{0xF9,0x10,0xA1,0x50,0x4D,0x5F,0xFB,0x79,0x3D,0x94,0xF3,0xB5,0x00,0x76,0x5E,0x1A,
			         0xBC,0xAD,0x72,0xD9};
			IntArray2ByteArray(tmp, 0, capk08.CheckSum, 0, tmp.length);
			
			
			//init capk09   
			capk09 = new EMV_CAPK();
			tmp = new int[]{0xA0, 0x00, 0x00, 0x00, 0x25};
			IntArray2ByteArray(tmp, 0, capk09.RID, 0, tmp.length);
			capk09.KeyID = 0x03;
			capk09.HashInd = 0x01;
			capk09.ArithInd = 0x01;
			capk09.ModulLen = (byte) 0x80;
			tmp = new int[]{0xB0, 0xC2, 0xC6, 0xE2, 0xA6, 0x38, 0x69, 0x33, 0xCD, 0x17, 0xC2, 0x39, 0x49, 0x6B, 0xF4, 0x8C,
			         0x57, 0xE3, 0x89, 0x16, 0x4F, 0x2A, 0x96, 0xBF, 0xF1, 0x33, 0x43, 0x9A, 0xE8, 0xA7, 0x7B, 0x20,
			         0x49, 0x8B, 0xD4, 0xDC, 0x69, 0x59, 0xAB, 0x0C, 0x2D, 0x05, 0xD0, 0x72, 0x3A, 0xF3, 0x66, 0x89,
			         0x01, 0x93, 0x7B, 0x67, 0x4E, 0x5A, 0x2F, 0xA9, 0x2D, 0xDD, 0x5E, 0x78, 0xEA, 0x9D, 0x75, 0xD7,
			         0x96, 0x20, 0x17, 0x3C, 0xC2, 0x69, 0xB3, 0x5F, 0x46, 0x3B, 0x3D, 0x4A, 0xAF, 0xF2, 0x79, 0x4F,
			         0x92, 0xE6, 0xC7, 0xA3, 0xFB, 0x95, 0x32, 0x5D, 0x8A, 0xB9, 0x59, 0x60, 0xC3, 0x06, 0x6B, 0xE5,
			         0x48, 0x08, 0x7B, 0xCB, 0x6C, 0xE1, 0x26, 0x88, 0x14, 0x4A, 0x8B, 0x4A, 0x66, 0x22, 0x8A, 0xE4,
			         0x65, 0x9C, 0x63, 0x4C, 0x99, 0xE3, 0x60, 0x11, 0x58, 0x4C, 0x09, 0x50, 0x82, 0xA3, 0xA3, 0xE3};
			IntArray2ByteArray(tmp, 0, capk09.Modul, 0, tmp.length);
			capk09.ExpLen = 1;
			tmp = new int[]{0x03};
			IntArray2ByteArray(tmp, 0, capk09.Exp, 0, tmp.length);
			tmp = new int[]{0x09, 0x12, 0x31};
			IntArray2ByteArray(tmp, 0, capk09.ExpDate, 0, tmp.length);
			tmp = new int[]{0x87, 0x08, 0xA3, 0xE3, 0xBB, 0xC1, 0xBB, 0x0B, 0xE7, 0x3E, 0xBD, 0x8D, 0x19, 0xD4, 0xE5, 0xD2,
			         0x01, 0x66, 0xBF, 0x6C};
			IntArray2ByteArray(tmp, 0, capk09.CheckSum, 0, tmp.length);
			
			
			//init capk10   CUP 1024 bits Test Key 06
			capk10 = new EMV_CAPK();
			tmp = new int[]{0xA0, 0x00, 0x00, 0x00, 0x25};
			IntArray2ByteArray(tmp, 0, capk10.RID, 0, tmp.length);
			capk10.KeyID = 0x0E;
			capk10.HashInd = 0x01;
			capk10.ArithInd = 0x01;
			capk10.ModulLen = (byte) 144;
			tmp = new int[]{0xAA, 0x94, 0xA8, 0xC6, 0xDA, 0xD2, 0x4F, 0x9B, 0xA5, 0x6A, 0x27, 0xC0, 0x9B, 0x01, 0x02, 0x08,
			         0x19, 0x56, 0x8B, 0x81, 0xA0, 0x26, 0xBE, 0x9F, 0xD0, 0xA3, 0x41, 0x6C, 0xA9, 0xA7, 0x11, 0x66,
			         0xED, 0x50, 0x84, 0xED, 0x91, 0xCE, 0xD4, 0x7D, 0xD4, 0x57, 0xDB, 0x7E, 0x6C, 0xBC, 0xD5, 0x3E,
			         0x56, 0x0B, 0xC5, 0xDF, 0x48, 0xAB, 0xC3, 0x80, 0x99, 0x3B, 0x6D, 0x54, 0x9F, 0x51, 0x96, 0xCF,
			         0xA7, 0x7D, 0xFB, 0x20, 0xA0, 0x29, 0x61, 0x88, 0xE9, 0x69, 0xA2, 0x77, 0x2E, 0x8C, 0x41, 0x41,
			         0x66, 0x5F, 0x8B, 0xB2, 0x51, 0x6B, 0xA2, 0xC7, 0xB5, 0xFC, 0x91, 0xF8, 0xDA, 0x04, 0xE8, 0xD5,
			         0x12, 0xEB, 0x0F, 0x64, 0x11, 0x51, 0x6F, 0xB8, 0x6F, 0xC0, 0x21, 0xCE, 0x7E, 0x96, 0x9D, 0xA9,
			         0x4D, 0x33, 0x93, 0x79, 0x09, 0xA5, 0x3A, 0x57, 0xF9, 0x07, 0xC4, 0x0C, 0x22, 0x00, 0x9D, 0xA7,
			         0x53, 0x2C, 0xB3, 0xBE, 0x50, 0x9A, 0xE1, 0x73, 0xB3, 0x9A, 0xD6, 0xA0, 0x1B, 0xA5, 0xBB, 0x85};
			IntArray2ByteArray(tmp, 0, capk10.Modul, 0, tmp.length);
			capk10.ExpLen = 1;
			tmp = new int[]{0x03};
			IntArray2ByteArray(tmp, 0, capk10.Exp, 0, tmp.length);
			tmp = new int[]{0x12, 0x12, 0x31};
			IntArray2ByteArray(tmp, 0, capk10.ExpDate, 0, tmp.length);
			tmp = new int[]{0xA7, 0x26, 0x6A, 0xBA, 0xE6, 0x4B, 0x42, 0xA3, 0x66, 0x88, 0x51, 0x19, 0x1D, 0x49, 0x85, 0x6E,
			         0x17, 0xF8, 0xFB, 0xCD};
			IntArray2ByteArray(tmp, 0, capk10.CheckSum, 0, tmp.length);
			
			
			//init capk11   
			capk11 = new EMV_CAPK();
			tmp = new int[]{0xA0, 0x00, 0x00, 0x00, 0x25};
			IntArray2ByteArray(tmp, 0, capk11.RID, 0, tmp.length);
			capk11.KeyID = 0x0F;
			capk11.HashInd = 0x01;
			capk11.ArithInd = 0x01;
			capk11.ModulLen = (byte) 176;
			tmp = new int[]{0xC8, 0xD5, 0xAC, 0x27, 0xA5, 0xE1, 0xFB, 0x89, 0x97, 0x8C, 0x7C, 0x64, 0x79, 0xAF, 0x99, 0x3A,
			         0xB3, 0x80, 0x0E, 0xB2, 0x43, 0x99, 0x6F, 0xBB, 0x2A, 0xE2, 0x6B, 0x67, 0xB2, 0x3A, 0xC4, 0x82,
			         0xC4, 0xB7, 0x46, 0x00, 0x5A, 0x51, 0xAF, 0xA7, 0xD2, 0xD8, 0x3E, 0x89, 0x4F, 0x59, 0x1A, 0x23,
			         0x57, 0xB3, 0x0F, 0x85, 0xB8, 0x56, 0x27, 0xFF, 0x15, 0xDA, 0x12, 0x29, 0x0F, 0x70, 0xF0, 0x57,
			         0x66, 0x55, 0x2B, 0xA1, 0x1A, 0xD3, 0x4B, 0x71, 0x09, 0xFA, 0x49, 0xDE, 0x29, 0xDC, 0xB0, 0x10,
			         0x96, 0x70, 0x87, 0x5A, 0x17, 0xEA, 0x95, 0x54, 0x9E, 0x92, 0x34, 0x7B, 0x94, 0x8A, 0xA1, 0xF0,
			         0x45, 0x75, 0x6D, 0xE5, 0x6B, 0x70, 0x7E, 0x38, 0x63, 0xE5, 0x9A, 0x6C, 0xBE, 0x99, 0xC1, 0x27,
			         0x2E, 0xF6, 0x5F, 0xB6, 0x6C, 0xBB, 0x4C, 0xFF, 0x07, 0x0F, 0x36, 0x02, 0x9D, 0xD7, 0x62, 0x18,
			         0xB2, 0x12, 0x42, 0x64, 0x5B, 0x51, 0xCA, 0x75, 0x2A, 0xF3, 0x7E, 0x70, 0xBE, 0x1A, 0x84, 0xFF,
			         0x31, 0x07, 0x9D, 0xC0, 0x04, 0x8E, 0x92, 0x88, 0x83, 0xEC, 0x4F, 0xAD, 0xD4, 0x97, 0xA7, 0x19,
			         0x38, 0x5C, 0x2B, 0xBB, 0xEB, 0xC5, 0xA6, 0x6A, 0xA5, 0xE5, 0x65, 0x5D, 0x18, 0x03, 0x4E, 0xC5};
			IntArray2ByteArray(tmp, 0, capk11.Modul, 0, tmp.length);
			capk11.ExpLen = 1;
			tmp = new int[]{0x03};
			IntArray2ByteArray(tmp, 0, capk11.Exp, 0, tmp.length);
			tmp = new int[]{0x14, 0x12, 0x31};
			IntArray2ByteArray(tmp, 0, capk11.ExpDate, 0, tmp.length);
			tmp = new int[]{0xA7, 0x34, 0x72, 0xB3, 0xAB, 0x55, 0x74, 0x93, 0xA9, 0xBC, 0x21, 0x79, 0xCC, 0x80, 0x14, 0x05,
			         0x3B, 0x12, 0xBA, 0xB4};
			IntArray2ByteArray(tmp, 0, capk11.CheckSum, 0, tmp.length);
			
			
			//init capk12   AMEX 1984 bits Live Key 10
			capk12 = new EMV_CAPK();
			tmp = new int[]{0xA0, 0x00, 0x00, 0x00, 0x25};
			IntArray2ByteArray(tmp, 0, capk12.RID, 0, tmp.length);
			capk12.KeyID = 0x10;
			capk12.HashInd = 0x01;
			capk12.ArithInd = 0x01;
			capk12.ModulLen = (byte) 248;
			tmp = new int[]{0xCF, 0x98, 0xDF, 0xED, 0xB3, 0xD3, 0x72, 0x79, 0x65, 0xEE, 0x77, 0x97, 0x72, 0x33, 0x55, 0xE0,
			         0x75, 0x1C, 0x81, 0xD2, 0xD3, 0xDF, 0x4D, 0x18, 0xEB, 0xAB, 0x9F, 0xB9, 0xD4, 0x9F, 0x38, 0xC8,
			         0xC4, 0xA8, 0x26, 0xB9, 0x9D, 0xC9, 0xDE, 0xA3, 0xF0, 0x10, 0x43, 0xD4, 0xBF, 0x22, 0xAC, 0x35,
			         0x50, 0xE2, 0x96, 0x2A, 0x59, 0x63, 0x9B, 0x13, 0x32, 0x15, 0x64, 0x22, 0xF7, 0x88, 0xB9, 0xC1,
			         0x6D, 0x40, 0x13, 0x5E, 0xFD, 0x1B, 0xA9, 0x41, 0x47, 0x75, 0x05, 0x75, 0xE6, 0x36, 0xB6, 0xEB,
			         0xC6, 0x18, 0x73, 0x4C, 0x91, 0xC1, 0xD1, 0xBF, 0x3E, 0xDC, 0x2A, 0x46, 0xA4, 0x39, 0x01, 0x66,
			         0x8E, 0x0F, 0xFC, 0x13, 0x67, 0x74, 0x08, 0x0E, 0x88, 0x80, 0x44, 0xF6, 0xA1, 0xE6, 0x5D, 0xC9,
			         0xAA, 0xA8, 0x92, 0x8D, 0xAC, 0xBE, 0xB0, 0xDB, 0x55, 0xEA, 0x35, 0x14, 0x68, 0x6C, 0x6A, 0x73,
			         0x2C, 0xEF, 0x55, 0xEE, 0x27, 0xCF, 0x87, 0x7F, 0x11, 0x06, 0x52, 0x69, 0x4A, 0x0E, 0x34, 0x84,
			         0xC8, 0x55, 0xD8, 0x82, 0xAE, 0x19, 0x16, 0x74, 0xE2, 0x5C, 0x29, 0x62, 0x05, 0xBB, 0xB5, 0x99,
			         0x45, 0x51, 0x76, 0xFD, 0xD7, 0xBB, 0xC5, 0x49, 0xF2, 0x7B, 0xA5, 0xFE, 0x35, 0x33, 0x6F, 0x7E,
			         0x29, 0xE6, 0x8D, 0x78, 0x39, 0x73, 0x19, 0x94, 0x36, 0x63, 0x3C, 0x67, 0xEE, 0x5A, 0x68, 0x0F,
			         0x05, 0x16, 0x0E, 0xD1, 0x2D, 0x16, 0x65, 0xEC, 0x83, 0xD1, 0x99, 0x7F, 0x10, 0xFD, 0x05, 0xBB,
			         0xDB, 0xF9, 0x43, 0x3E, 0x8F, 0x79, 0x7A, 0xEE, 0x3E, 0x9F, 0x02, 0xA3, 0x42, 0x28, 0xAC, 0xE9,
			         0x27, 0xAB, 0xE6, 0x2B, 0x8B, 0x92, 0x81, 0xAD, 0x08, 0xD3, 0xDF, 0x5C, 0x73, 0x79, 0x68, 0x50,
			         0x45, 0xD7, 0xBA, 0x5F, 0xCD, 0xE5, 0x86, 0x37};
			IntArray2ByteArray(tmp, 0, capk12.Modul, 0, tmp.length);
			capk12.ExpLen = 1;
			tmp = new int[]{0x03};
			IntArray2ByteArray(tmp, 0, capk12.Exp, 0, tmp.length);
			tmp = new int[]{0x16, 0x12, 0x31};
			IntArray2ByteArray(tmp, 0, capk12.ExpDate, 0, tmp.length);
			tmp = new int[]{0xC7, 0x29, 0xCF, 0x2F, 0xD2, 0x62, 0x39, 0x4A, 0xBC, 0x4C, 0xC1, 0x73, 0x50, 0x65, 0x02, 0x44,
			         0x6A, 0xA9, 0xB9, 0xFD};
			IntArray2ByteArray(tmp, 0, capk12.CheckSum, 0, tmp.length);
	
			record += "downloadCAPK ret: "+"\n";
			byte[] by=null;
			record += "downloadCAPK ret: ";
			by=emv.addCAPK(capk01);
			record +=new String(by)+ "\n";
			record += "downloadCAPK ret: ";
			by=emv.addCAPK(capk02);
			record +=new String(by)+ "\n";
			record += "downloadCAPK ret: ";
			by=emv.addCAPK(capk03);
			record +=new String(by)+ "\n";
			record += "downloadCAPK ret: ";
			by=emv.addCAPK(capk04);
			record +=new String(by)+ "\n";
			record += "downloadCAPK ret: ";
			by=emv.addCAPK(capk05);
			record +=new String(by)+ "\n";
			record += "downloadCAPK ret: ";
			by=emv.addCAPK(capk07);
			record +=new String(by)+ "\n";
			record += "downloadCAPK ret: ";
			by=emv.addCAPK(capk08);
			record +=new String(by)+ "\n";
			record += "downloadCAPK ret: ";
			by=emv.addCAPK(capk09);
			record +=new String(by)+ "\n";
			record += "downloadCAPK ret: ";
			by=emv.addCAPK(capk10);
			record +=new String(by)+ "\n";
			record += "downloadCAPK ret: ";
			by=emv.addCAPK(capk11);
			record +=new String(by)+ "\n";
			record += "downloadCAPK ret: ";
			by=emv.addCAPK(capk12);
			record +=new String(by)+ "\n";
		
			step++; 
			
			record += "downloadCAPK test end"; 
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
	//锟斤拷锟斤拷CAPK锟侥凤拷锟斤拷
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
	
	private void deleteAllAPPFUN1()
	{
		String record = "";
		sequenceID++;
		emv = EmvManager.getInstance(BaseActivity.this);
		int step = 0; 
		try{
			step++;
			byte[] by = emv.deleteAllAPP(String.valueOf(sequenceID), "D180EMDK");
			
			record += "deleteAllAPP ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++; 
			
			record += "deleteAllAPP test end"; 

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
	private void deleteAllCAPKFUN1()
	{
		String record = "";
		sequenceID++;
		emv = EmvManager.getInstance(BaseActivity.this);
		int step = 0; 
		try{
			step++;
			byte[] by = emv.deleteAllCAPK(String.valueOf(sequenceID), "D180EMDK");
			
			record += "deleteAllCAPK ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++; 
			
			record += "deleteAllCAPK test end"; 

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
	
	private void readCardDataSwipeFUN1(String index) {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please swipe card...");
		try {
			step++;

			byte[] by = base.readCardData111(index);
			record += "readCardData ret: " + new String(by) + "\n";
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			if (suc.equals(w1)) {
				maskP = content.split("\\|")[11];
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataSwipeFUN2() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please swipe card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "500.00", "1.00", "0", "10000",
					"$2000.00", "Swipe Card-->");
			record += "readCardData ret: " + new String(by) + "\n";
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			if (suc.equals(w1)) {
				maskP = content.split("\\|")[11];
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataSwipeFUN3() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please swipe card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "100.00", "tip@123456789", "0",
					"10000", "", "");
			record += "readCardData ret: " + new String(by) + "\n";
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			if (suc.equals(w1)) {
				maskP = content.split("\\|")[11];
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataSwipeFUN4() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please swipe card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "100.00", "5.00", "0", "10000",
					"$2000.00", "Swipe Card-->");
			record += "readCardData ret: " + new String(by) + "\n";
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			if (suc.equals(w1)) {
				maskP = content.split("\\|")[11];
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataSwipeFUN5() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please swipe card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "", "", "5.00", "0", "10000", "$2000.00",
					"swipe card-->");
			record += "readCardData ret: " + new String(by) + "\n";
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			if (suc.equals(w1)) {
				maskP = content.split("\\|")[11];
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataSwipeFUN6() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please swipe card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "00.00", "0.00", "0", "20000", "",
					"Swipe Card-->");
			record += "readCardData ret: " + new String(by) + "\n";
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			if (suc.equals(w1)) {
				maskP = content.split("\\|")[11];
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataSwipeFUN7() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please swipe card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "AAAAA", "aaaaa", "0", "20000", "", "");
			record += "readCardData ret: " + new String(by) + "\n";
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			if (suc.equals(w1)) {
				maskP = content.split("\\|")[11];
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataSwipeFUN8() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please swipe card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "123456789.321", "0", "0", "20000",
					"$2000.00", "Swipe Card-->");
			record += "readCardData ret: " + new String(by) + "\n";

			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			if (suc.equals(w1)) {
				maskP = content.split("\\|")[11];
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataSwipeFUN9() {

		String record = "";
		sequenceID++;
		int step = 0;
		for (int i = 0; i < 1000; i++) {
			try {
				step++;
				byte[] by = base.readCardData(String.valueOf(sequenceID),
						"D180EMDK", "Sale", "500.00", "1.00", "0", "10000",
						"$2000.00", "Swipe Card-->");

				String content = new String(by);
				String w1 = content.substring(0, 7);

				String suc = "success";
				if (suc.equals(w1)) {
					maskP = content.split("\\|")[11];
				}

				byte[] by3 = base
						.getBatteryLevel111("getBatteryLevel|1|D180EMDK");
				record += "BatteryLevel: " + new String(by3) + "\n";

				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss:SSS");// 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鑺傞潻鎷峰紡
				record += "Time" + df.format(new Date()) + "\n" + "NUMBER:"
						+ (i + 1) + "\n\n";

				File file = new File("mnt/sdcard/swipelog.txt");
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(record.getBytes());
				fos.flush();
				fos.close();
				Thread.sleep(300000);
				step++;
			} catch (Exception exception) {
				exception.printStackTrace();
				Bundle bundle = new Bundle();
				bundle.putString("result",
						record + "Exception: " + exception.toString()
								+ " on step: " + step);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} finally {
				progressDialog.dismiss();
			}
		}
	}
	
	
	private void IntArray2ByteArray(int[] in, int iOffset, byte[] by, int bOffset, int len)
	{
		for(int i = 0; i < len; i++)
		{
			by[bOffset + i] = (byte) (in[iOffset + i] & 0xff);
		}
	}
	
	private void readCardDataICFUN1(String index) {
		String record = "";
		sequenceID++;
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Insert card...");
		try {
			step++;
			byte[] by = base.readCardData111(index);
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataICFUN2() {
		String record = "";
		sequenceID++;
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Insert card...");
		try {
			step++;
			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "100.00", "5.00", "1", "20000",
					"$2000", "Insert Card-->");
			String content = new String(by);
			String w1 = content.substring(0, 7);
			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataICFUN3() {
		String record = "";
		sequenceID++;
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Insert card...");
		try {
			step++;
			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "100.00", "5.00", "1", "20000",
					"$2000", "Insert Card-->");

			String content = new String(by);
			String w1 = content.substring(0, 7);
			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataICFUN4() {
		String record = "";
		sequenceID++;
		int step = 0;
		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Insert card...");
		try {
			step++;
			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "100.00", "5.00", "1", "20000",
					"$2000", "Insert Card-->");

			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataICFUN5() {
		String record = "";
		sequenceID++;
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Insert card...");
		try {
			step++;
			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "$100.00", "$5.00", "1", "20000",
					"$2000.00!@#", "￥{}+-Insert Card-->");
			
			

			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataICFUN6() {
		String record = "";
		sequenceID++;
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Insert card...");
		try {
			step++;
			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "100.00", "5.00", "1", "20000",
					"", "Insert Card-->");

			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataICFUN7() {
		String record = "";
		sequenceID++;
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Insert card...");
		try {
			step++;
			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "", "", "1", "20000", "$2000.00",
					"Insert Card-->");

			String content = new String(by);
			String w1 = content.substring(0, 7);
			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataICFUN8() {
		String record = "";
		sequenceID++;
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Insert card...");
		try {
			step++;
			byte[] by = base.readCardData("2", "D180EMDK", "Sale", "100.00",
					"5.00", "1", "100000", "$2000", "Insert Card-->");

			String content = new String(by);
			String w1 = content.substring(0, 7);
			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataManualEntryFUN1(String index) {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Input Number...");
		try {
			step++;

			byte[] by = base.readCardData111(index);
			record += "readCardData ret: " + new String(by) + "\n";

			String suc = "success";
			String str = new String(by);
			String w2 = str.substring(0, 7);
			if (w2.equals(suc)) {
				maskP = str.split("\\|")[11];
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataManualEntryFUN2() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Input Number...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "100.00", "5.00", "3", "20000",
					"$2000.00", "Enter Number-->");
			record += "readCardData ret: " + new String(by) + "\n";

			String suc = "success";
			String str = new String(by);
			String w2 = str.substring(0, 7);
			if (w2.equals(suc)) {
				maskP = str.split("\\|")[11];
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataManualEntryFUN3() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Input Number...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "$1.00", "tip@123456789", "3",
					"20000", "$2000.00r", "Enter Number-->");
			record += "readCardData ret: " + new String(by) + "\n";

			String suc = "success";
			String str = new String(by);
			String w2 = str.substring(0, 7);
			if (w2.equals(suc)) {
				maskP = str.split("\\|")[11];
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataManualEntryFUN4() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Input Number...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "100.00", "5.00", "3", "20000",
					"$2000.00", "Enter Number-->");
			record += "readCardData ret: " + new String(by) + "\n";

			String suc = "success";
			String str = new String(by);
			String w2 = str.substring(0, 7);
			if (w2.equals(suc)) {
				maskP = str.split("\\|")[11];
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN1(String index) {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		try {
			step++;

			byte[] by = base.readCardData111(index);

			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;
				record = record +"AIDListLog:"+content+"\n\n";
				byte[] by4 = base.readCardData1111(str);
				
				String twocontent = new String(by4);
				String twow1 = twocontent.substring(0, 7);
				if(twow1.equals(suc))
				{
					String content1 = new String(by4);
					maskP = content1.split("\\|")[11];
				}
					record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllStep(String index) {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		try {
			step++;

			byte[] by = base.readCardData111Step(index);
			
			String pubKeystring = by.toString();
			File file = new File("mnt/sdcard/readCardData.txt");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(pubKeystring.getBytes());
			fos.flush();
			fos.close();

			String content = new String(by);
			String w1 = content.substring(0, 7);
			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN2() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "100.00", "5.00", "256", "20000",
					"$2000.00", "Transaction...");
			
			String pubKeystring = new String(by);
			
			MyLog.i(TAG, "return:"+new String(by));
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			
			
			
			File file = new File("mnt/sdcard/red.txt");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(pubKeystring.getBytes());
			fos.flush();
			fos.close();
			
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN3() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "$1.00", "tip@123456789", "256",
					"20000", "$2000.00", "Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN4() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "100.00", "5.00", "256", "20000",
					"$2000.00", "Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}

			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN5() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "100.00", "5.00", "256", "20000",
					"$2000.00", "Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN6() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "100.00", "", "256", "20000",
					"$2000.00", "Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN7() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "1.00", "tip", "256", "20000",
					"$2000.00", "Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN8() {

		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		for (int j = 0; j < 200; j++) {
			try {
				step++;

				byte[] by = base.readCardData(String.valueOf(sequenceID),
						"D180EMDK", "Sale", "100.00", "5.00", "256", "20000",
						"$2000.00", "Transaction...");
				String content = new String(by);
				String w1 = content.substring(0, 7);

				String suc = "success";
				String aidlist = "AIDList";
				if (w1.equals(aidlist)) {
					content = content.trim();
					String AIDList = content.split("\\|")[0];
					String AID = content.substring(content.indexOf("|", 1),
							content.length());
					String str = AIDList + "|1|D180EMDK" + AID;

					byte[] by4 = base.readCardData1111(str);
					String content1 = new String(by4);
					String w2 = content1.substring(0, 7);
					if (w2.equals(suc)) {
						maskP = content1.split("\\|")[11];
					}
					record += "readCardData ret: " + new String(by4) + "\n";
				} else if (w1.equals(suc)) {
					maskP = content.split("\\|")[11];
					record += "readCardData ret: " + new String(by) + "\n";
				} else {
					record += "readCardData ret: " + new String(by) + "\n";
				}
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				Bundle bundle = new Bundle();
				bundle.putString("result",
						record + "Exception: " + exception.toString()
								+ " on step: " + step);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} finally {
				progressDialog.dismiss();
			}
		}
	}

	private void readCardDataAllFUN9() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "1", "1", "256", "20000", "$2000.00",
					"Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN10() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "10", "10", "256", "20000", "$2000.00",
					"Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN11() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "1.50", "15.0", "256", "20000",
					"$2000.00", "Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN12() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "1.01", "1.01", "256", "20000",
					"$2000.00", "Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN13() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "10.00", "10.00", "256", "20000",
					"$2000.00", "Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN14() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "10.10", "10.10", "256", "20000",
					"$2000.00", "Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN15() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "10.1111", "10.1111", "256", "20000",
					"$2000.00", "Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN16() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "", "", "256", "20000", "$2000.00",
					"Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN17() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "1.1", "1.1", "256", "20000",
					"$2000.00", "Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN18() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;

			byte[] by = base.readCardData(String.valueOf(sequenceID),
					"D180EMDK", "Sale", "1.", "1.", "256", "20000", "$2000.00",
					"Transaction...");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN19() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		try {
			step++;
			byte[] by = base.readCardData1("12348", "D180EMDK", "Sale", "1",
					"1", "256", "8000", "$2000.00", "Transaction...",
					"manualEntryMessage1", "manualEntryMessage2");
			String content = new String(by);
			String w1 = content.substring(0, 7);

			String suc = "success";
			String aidlist = "AIDList";
			if (w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID = content.substring(content.indexOf("|", 1),
						content.length());
				String str = AIDList + "|1|D180EMDK" + AID;

				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				String w2 = content1.substring(0, 7);
				if (w2.equals(suc)) {
					maskP = content1.split("\\|")[11];
				}
				record += "readCardData ret: " + new String(by4) + "\n";
			} else if (w1.equals(suc)) {
				maskP = content.split("\\|")[11];
				record += "readCardData ret: " + new String(by) + "\n";
			} else {
				record += "readCardData ret: " + new String(by) + "\n";
			}
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			progressDialog.dismiss();
		}
	}

	private void readCardDataAllFUN20() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		for (int j = 0; j < 200; j++) {
			try {
				step++;

				byte[] by = base.readCardData(String.valueOf(sequenceID),
						"D180EMDK", "Sale", "100.00", "5.00", "256", "20000",
						"$2000.00", "Transaction...");
				String content = new String(by);
				String w1 = content.substring(0, 7);

				String suc = "success";
				String aidlist = "AIDList";
				if (w1.equals(aidlist)) {
					content = content.trim();
					String AIDList = content.split("\\|")[0];
					String AID = content.substring(content.indexOf("|", 1),
							content.length());
					String str = AIDList + "|1|D180EMDK" + AID;

					byte[] by4 = base.readCardData1111(str);
					String content1 = new String(by4);
					String w2 = content1.substring(0, 7);
					if (w2.equals(suc)) {
						maskP = content1.split("\\|")[11];
					}
					record += "readCardData ret: " + new String(by4) + "\n";
				} else if (w1.equals(suc)) {
					maskP = content.split("\\|")[11];
					record += "readCardData ret: " + new String(by) + "\n";
				} else {
					record += "readCardData ret: " + new String(by) + "\n";
				}
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} catch (Exception exception) {
				exception.printStackTrace();
				Bundle bundle = new Bundle();
				bundle.putString("result",
						record + "Exception: " + exception.toString()
								+ " on step: " + step);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} finally {
				progressDialog.dismiss();
			}
		}
	}

	private void readCardDataAllFUN21() {
		String record = "";
		sequenceID++;
		byte[] result = new byte[1024];
		int step = 0;

		sequenceID++;
		TextView txt = (TextView) findViewById(R.id.textViewBase);
		txt.setText("Please Swipe/Insert/Input card...");
		for (int j = 0; j < 200; j++) {
			try {
				step++;

				byte[] by = base.readCardData(String.valueOf(sequenceID),
						"D180EMDK", "Sale", "100.00", "5.00", "256", "20000",
						"$2000.00", "Transaction...");
				String content = new String(by);
				String w1 = content.substring(0, 7);

				String suc = "success";
				String aidlist = "AIDList";
				if (w1.equals(aidlist)) {
					content = content.trim();
					String AIDList = content.split("\\|")[0];
					String AID = content.substring(content.indexOf("|", 1),
							content.length());
					String str = AIDList + "|1|D180EMDK" + AID;

					byte[] by4 = base.readCardData1111(str);
					String content1 = new String(by4);
					String w2 = content1.substring(0, 7);
					if (w2.equals(suc)) {
						maskP = content1.split("\\|")[11];
					}
					record += "readCardData ret: " + new String(by4) + "\n";
				} else if (w1.equals(suc)) {
					maskP = content.split("\\|")[11];
					record += "readCardData ret: " + new String(by) + "\n";
				} else {
					record += "readCardData ret: " + new String(by) + "\n";
				}
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} catch (Exception exception) {
				exception.printStackTrace();
				Bundle bundle = new Bundle();
				bundle.putString("result",
						record + "Exception: " + exception.toString()
								+ " on step: " + step);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} finally {
				progressDialog.dismiss();
			}
		}
	}

	private void readCardDataAllFUN22() {
		String record = "";
		int n = (int) (Math.random() * 100) + 100;
		for (int i = 0; i < n; i++) {
			try {
				byte[] by = base.open(String.valueOf(sequenceID), "APP2", "0",
						"D180EMDK");
				record += "open ret: " + new String(by) + "\n";

				byte[] by1 = base.setSessionKey111("setSessionKey|1|D180EMDK");
				record += "setSessionKey ret: " + new String(by1) + "\n";

				byte[] by2 = base.setParameter(String.valueOf(sequenceID),
						"D180EMDK", "idleMsg=PAXPAX", "sleepModeTimeout=45000",
						"dataEncryptionKeySlot=2","dataEncryptionType=0",
						"maskFirstDigits=6", "LanguageType=0","AIDFilterAllowedFlag=0",
						"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
				
				
				
				
				
				record += "setParameter ret: " + new String(by2) + "\n";

				byte[] by3 = base
						.readCardData111("readCardData|1|D180EMDK|Sale|100.00|5.00|256|20000|$2000.00|Transaction...");
				String content = new String(by3);
				String w1 = content.substring(0, 7);

				String suc = "success";
				String aidlist = "AIDList";
				if (w1.equals(aidlist)) {
					content = content.trim();
					String AIDList = content.split("\\|")[0];
					String AID = content.substring(content.indexOf("|", 1),
							content.length());
					String str = AIDList + "|1|D180EMDK" + AID;

					byte[] by4 = base.readCardData1111(str);
					String content1 = new String(by4);
					String w2 = content1.substring(0, 7);
					if (w2.equals(suc)) {
						maskP = content1.split("\\|")[11];
					}
					record += "readCardData ret: " + new String(by4) + "\n";
				} else if (w1.equals(suc)) {
					maskP = content.split("\\|")[11];
					record += "readCardData ret: " + new String(by) + "\n";
				} else {
					record += "readCardData ret: " + new String(by) + "\n";
				}

				byte[] by4 = base
						.getBatteryLevel111("getBatteryLevel|1|D180EMDK");
				record += "getBatteryLevel ret: " + new String(by4) + "\n";

				byte[] by5 = base.disableKeyPad111("disableKeypad|1|D180EMDK");
				record += "disableKeyPad ret: " + new String(by5) + "\n";

				byte[] by6 = base
						.enableKeypad111("enableKeypad|1|D180EMDK|20000");
				record += "enableKeypad ret: " + new String(by6) + "\n";

				byte[] by7 = base.close111("close|1|D180EMDK");
				record += "close ret: " + new String(by7) + "\n";

				record += "no encryption Number: " + (i + 1) + "\n";
				base.beep();
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
				// Thread.sleep(5000);
				Thread.sleep(2000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void readCardDataAllFUN23() {
		String record = "";
		for (int i = 0; i < 200; i++) {
			try {
				byte[] by = base.open(String.valueOf(sequenceID), "APP2", "0",
						"D180EMDK");
				record += "open ret: " + new String(by) + "\n";
				Thread.sleep(1000);

				byte[] by1 = base.setSessionKey111("setSessionKey|1|D180EMDK");
				record += "setSessionKey ret: " + new String(by1) + "\n";
				Thread.sleep(1000);

				byte[] by2 = base.setParameter(String.valueOf(sequenceID),
						"D180EMDK", "idleMsg=Welcome", "sleepModeTimeout=0",
						"dataEncryptionKeySlot=2","dataEncryptionType=3", 
						"maskFirstDigits=6", "LanguageType=0","AIDFilterAllowedFlag=0",
						"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
				
				
				
				record += "setParameter ret: " + new String(by2) + "\n";
				Thread.sleep(1000);

				byte[] by3 = base.readCardData(String.valueOf(sequenceID),
						"D180EMDK", "Sale", "100.00", "5.00", "256", "20000",
						"$2000.00", "Transaction...");
				record += "readCardData ret: " + new String(by3) + "\n";

				String content = new String(by3);
				String w1 = content.substring(0, 7);

				String suc = "success";
				String aidlist = "AIDList";
				if (w1.equals(aidlist)) {
					content = content.trim();
					String AIDList = content.split("\\|")[0];
					String AID = content.substring(content.indexOf("|", 1),
							content.length());
					String str = AIDList + "|1|D180EMDK" + AID;

					byte[] by4 = base.readCardData1111(str);
					String content1 = new String(by4);
					String w2 = content1.substring(0, 7);
					if (w2.equals(suc)) {
						maskP = content1.split("\\|")[11];
					}
					record += "readCardData ret: " + new String(by4) + "\n";
				} else if (w1.equals(suc)) {
					maskP = content.split("\\|")[11];
					record += "readCardData ret: " + new String(by) + "\n";
				} else {
					record += "readCardData ret: " + new String(by) + "\n";
				}
				Thread.sleep(1000);
				byte[] by4 = base
						.getBatteryLevel111("getBatteryLevel|1|D180EMDK");
				record += "getBatteryLevel ret: " + new String(by4) + "\n";
				Thread.sleep(1000);
				byte[] by5 = ui
						.promptMenu111("promptMenu|1|D180EMDK|Prompt Menu|Please Select|sale|void|offline|REFUND|20000");
				record += "promptMenu ret: " + new String(by5) + "\n";
				Thread.sleep(1000);
				byte[] by6 = ui
						.promptMessage111("promptMessage|1|D180EMDK|Prompt Menu|Select|A.OK|B.Cancel|False|20000");
				record += "promptMessage ret: " + new String(by6) + "\n";
				byte[] by7 = base
						.enableKeypad111("enableKeypad|1|D180EMDK|20000");
				record += "enableKeypad ret: " + new String(by7) + "\n";
				Thread.sleep(1000);
				byte[] by8 = base.close111("close|1|D180EMDK");
				record += "close ret: " + new String(by8) + "\n";

				record += "RSA1024 Number: " + (i + 1) + "\n";
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void readCardDataAllFUN24() {
		String record = "";
		for (int i = 0; i < 200; i++) {
			try {
				byte[] by = base.open(String.valueOf(sequenceID), "APP2", "0",
						"D180EMDK");
				record += "open ret: " + new String(by) + "\n";
				Thread.sleep(1000);

				byte[] by1 = base.setSessionKey111("setSessionKey|1|D180EMDK");
				record += "setSessionKey ret: " + new String(by1) + "\n";
				Thread.sleep(1000);

				byte[] by2 = base
						.setParameter111("setParameter|1|D180EMDK|idleMsg=Welcome|sleepModeTimeout=60000|dataEncryptionType=4|maskFirstDigits=6");
				record += "setParameter ret: " + new String(by2) + "\n";
				Thread.sleep(1000);

				byte[] by3 = base.readCardData(String.valueOf(sequenceID),
						"D180EMDK", "Sale", "100.00", "5.00", "256", "20000",
						"$2000.00", "Transaction...");
				String content = new String(by3);
				String w1 = content.substring(0, 7);

				String suc = "success";
				String aidlist = "AIDList";
				if (w1.equals(aidlist)) {
					content = content.trim();
					String AIDList = content.split("\\|")[0];
					String AID = content.substring(content.indexOf("|", 1),
							content.length());
					String str = AIDList + "|1|D180EMDK" + AID;

					byte[] by4 = base.readCardData1111(str);
					String content1 = new String(by4);
					String w2 = content1.substring(0, 7);
					if (w2.equals(suc)) {
						maskP = content1.split("\\|")[11];
					}
					record += "readCardData ret: " + new String(by4) + "\n";
				} else if (w1.equals(suc)) {
					maskP = content.split("\\|")[11];
					record += "readCardData ret: " + new String(by) + "\n";
				} else {
					record += "readCardData ret: " + new String(by) + "\n";
				}
				Thread.sleep(1000);
				byte[] by4 = base
						.getBatteryLevel111("getParameter|1|D180EMDK|idleMsg||||");
				record += "getBatteryLevel ret: " + new String(by4) + "\n";
				Thread.sleep(1000);
				byte[] by5 = base
						.getParameter111("getParameter|1|D180EMDK|idleMsg||||");
				record += "getParameter ret: " + new String(by5) + "\n";
				Thread.sleep(1000);
				byte[] by6 = base.close111("close|1|D180EMDK");
				record += "close ret: " + new String(by6) + "\n";

				record += "RSA2048 Number: " + (i + 1) + "\n";
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void readCardDataAllFUN25() {
		String record = "";
		for (int i = 0; i < 200; i++) {
			try {
				byte[] by = base.open(String.valueOf(sequenceID), "APP2", "0",
						"D180EMDK");
				record += "open ret: " + new String(by) + "\n";
				Thread.sleep(1000);

				byte[] by1 = base.setSessionKey111("setSessionKey|1|D180EMDK");
				record += "setSessionKey ret: " + new String(by1) + "\n";
				Thread.sleep(1000);

				byte[] by2 = base.setParameter(String.valueOf(sequenceID),
						"D180EMDK", "idleMsg=Welcome", "sleepModeTimeout=0",
						"dataEncryptionKeySlot=2","dataEncryptionType=2",
						"maskFirstDigits=6", "LanguageType=0","AIDFilterAllowedFlag=0",
						"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
			
				
				record += "setParameter ret: " + new String(by2) + "\n";
				Thread.sleep(1000);

				byte[] by3 = base.readCardData(String.valueOf(sequenceID),
						"D180EMDK", "Sale", "100.00", "5.00", "256", "20000",
						"$2000.00", "Transaction...");
				String content = new String(by3);
				String w1 = content.substring(0, 7);

				String suc = "success";
				String aidlist = "AIDList";
				if (w1.equals(aidlist)) {
					content = content.trim();
					String AIDList = content.split("\\|")[0];
					String AID = content.substring(content.indexOf("|", 1),
							content.length());
					String str = AIDList + "|1|D180EMDK" + AID;

					byte[] by4 = base.readCardData1111(str);
					String content1 = new String(by4);
					String w2 = content1.substring(0, 7);
					if (w2.equals(suc)) {
						maskP = content1.split("\\|")[11];
					}
					record += "readCardData ret: " + new String(by4) + "\n";
				} else if (w1.equals(suc)) {
					maskP = content.split("\\|")[11];
					record += "readCardData ret: " + new String(by) + "\n";
				} else {
					record += "readCardData ret: " + new String(by) + "\n";
				}
				Thread.sleep(1000);
				byte[] by4 = base
						.getBatteryLevel111("getBatteryLevel|1|D180EMDK");
				record += "getBatteryLevel ret: " + new String(by4) + "\n";
				Thread.sleep(1000);
				byte[] by5 = base
						.setEMVTags111("setEmvTags|1|D180EMDK|5A085413330089601075");
				record += "setEMVTags ret: " + new String(by5) + "\n";
				Thread.sleep(1000);
				byte[] by6 = base.getEMVTags111("getEmvTags|1|D180EMDK|5A");
				record += "getEMVTags ret: " + new String(by6) + "\n";
				Thread.sleep(1000);
				byte[] by7 = base.close111("close|1|D180EMDK");
				record += "close ret: " + new String(by7) + "\n";

				record += "AES Number:" + (i + 1) + "\n";
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void readCardDataAllFUN26() {
		String record = "";
		for (int i = 0; i < 200; i++) {
			try {
				byte[] by = base.open(String.valueOf(sequenceID), "APP2", "0",
						"D180EMDK");
				record += "open ret: " + new String(by) + "\n";
				Thread.sleep(1000);

				byte[] by1 = base.setSessionKey111("setSessionKey|1|D180EMDK");
				record += "setSessionKey ret: " + new String(by1) + "\n";
				Thread.sleep(1000);

				byte[] by2 = base.setParameter(String.valueOf(sequenceID),
						"D180EMDK", "idleMsg=Welcome", "sleepModeTimeout=0",
						"dataEncryptionKeySlot=2","dataEncryptionType=1",
						"maskFirstDigits=6", "LanguageType=0","AIDFilterAllowedFlag=0",
						"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
				
				
				record += "setParameter ret: " + new String(by2) + "\n";
				Thread.sleep(1000);
				byte[] by3 = base.readCardData(String.valueOf(sequenceID),
						"D180EMDK", "Sale", "100.00", "5.00", "256", "20000",
						"$2000.00", "Transaction...");
				String content = new String(by3);
				String w1 = content.substring(0, 7);

				String suc = "success";
				String aidlist = "AIDList";
				if (w1.equals(aidlist)) {
					content = content.trim();
					String AIDList = content.split("\\|")[0];
					String AID = content.substring(content.indexOf("|", 1),
							content.length());
					String str = AIDList + "|1|D180EMDK" + AID;

					byte[] by4 = base.readCardData1111(str);
					String content1 = new String(by4);
					String w2 = content1.substring(0, 7);
					if (w2.equals(suc)) {
						maskP = content1.split("\\|")[11];
					}
					record += "readCardData ret: " + new String(by4) + "\n";
				} else if (w1.equals(suc)) {
					maskP = content.split("\\|")[11];
					record += "readCardData ret: " + new String(by) + "\n";
				} else {
					record += "readCardData ret: " + new String(by) + "\n";
				}
				Thread.sleep(1000);
				byte[] by4 = base
						.getBatteryLevel111("getBatteryLevel|1|D180EMDK");
				record += "getBatteryLevel ret: " + new String(by4) + "\n";
				Thread.sleep(1000);
				byte[] by5 = base
						.authorizeCard111("authorizeCard|1|D180EMDK|999.00|5.00|0|true|true|true|true|91053A4B123C1E|71023B1C|72010A");
				record += "authorizeCard ret: " + new String(by5) + "\n";
				Thread.sleep(1000);
				byte[] by6 = base
						.completeOnLineEMV111("completeOnLineEMV|1|D180EMDK|0|true|");
				record += "completeOnLineEMV ret: " + new String(by6) + "\n";
				byte[] by7 = base.promptPIN111("promptPIN|1|D180EMDK|" + maskP
						+ "||4|8|Enter PIN|$1,000.00|$1.00|60000");
				record += "promptPIN ret: " + new String(by7) + "\n";
				Thread.sleep(1000);
				byte[] by8 = base
						.createMAC111("createMAC|1|D180EMDK|1A2B3C4D1A2B3C4D");
				record += "createMAC ret: " + new String(by8) + "\n";
				byte[] by9 = base
						.validateMAC111("validateMAC|1|D180EMDK|F87BCBE88883243D||||message1|message2|1A2B3C4D1A2B3C4D");
				record += "validateMAC ret: " + new String(by9) + "\n";
				byte[] by10 = base
						.promptAdditionalInfo111("promptAdditionalInfo|1|D180EMDK|99.99|6228481090369874587|TRUE|TRUE|1.11|20000");
				record += "promptAdditionalInfo ret: " + new String(by10)
						+ "\n";
				byte[] by11 = base.close111("close|1|D180EMDK");
				record += "close ret: " + new String(by11) + "\n";

				record += "3DES Number: " + (i + 1) + "\n";
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void getBatteryLevelFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.getBatteryLevel111(index);
			record += "getBatteryLevel ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getBatteryLevel test end"; 

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
	private void getBatteryLevelFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.getBatteryLevel1(String.valueOf(sequenceID), "D180EMDK");
			record += "getBatteryLevel ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getBatteryLevel test end"; 

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
	private void getBatteryLevelFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		for(int i = 0; i<100;i++)
		{
			try{
				byte[] by1 = base.open(String.valueOf(sequenceID),"APP2","0","D180EMDK");	
				byte[] by = base.getBatteryLevel(String.valueOf(sequenceID), "D180EMDK");
				byte[] by3 = base.close(String.valueOf(sequenceID),"D180EMDK");
		
				record += "getBatteryLevel test end!"+ "NUMBER:"+(i+1)+"\n\n"; 
				
//				File file = new File("mnt/sdcard/BatteryLevelTest.txt");
//				FileOutputStream fos = new FileOutputStream(file);
//				fos.write(record.getBytes());
//				fos.flush();
//				fos.close();
			
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
				Thread.sleep(5000);
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
	
	private void TestOpenFUN222() {
		int i = 0; 
		int step = 0;
		int resendTimes = 10;
		byte[] by = null;
		final boolean[] isTimeout = new boolean[]{false};
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				isTimeout[0] = true;
			}
		};
		String record = "";

		sequenceID++;
		timer.schedule(task, time);
		for (;;) {
			try {
				Log.i("lxg", "send ---------------- ");
				step++;
				by = base.open(String.valueOf(sequenceID),"APP2","0","D180EMDK");			
				break;

			}catch (Exception exception) {
				exception.printStackTrace();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isTimeout[0])
					break;
				else
					continue;
				
			} finally {
				progressDialog.dismiss();
			}
		}
		timer.cancel();
		if(isTimeout[0] == false){
			record += "open ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;

			record += "Open test end";
		}else{
		}
	}	
	private void TestCloseFUN333()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			sequenceID++;
			step++;
			byte[] by = base.close(String.valueOf(sequenceID),"D180EMDK");
			record += "close ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			String content = new String(by);
			String suc="success";
			String w1=content.substring(0, 7);
			if(!w1.equals(suc)){
				proto.setisBTConnected();
			}
			
			record += "close test end"; 
		}catch(Exception exception){
			exception.printStackTrace();
		} finally {
			progressDialog.dismiss();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void TestsetSessionKeyFUN555()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.setSessionKey(String.valueOf(sequenceID), "D180EMDK");

		}catch(Exception exception){
			exception.printStackTrace();
		} finally {
			progressDialog.dismiss();
		}
	}
	
	private void getBatteryLevelFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		for(int i = 0;i<6000;i++)
		{
			try{
				step++;
	
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
				record += "Time:" + df.format(new Date())+"\n";
				TestOpenFUN222();
			
				TestsetSessionKeyFUN555();
				
				byte[] by = base.getBatteryLevel("1", "D180EMDK");
				
				TestCloseFUN333();
				
				record+= "getBatteryLevel number:"+new String(by)+"\n"+ "NUMBER:"+(i+1)+"\n\n";
				
				File file = new File("mnt/sdcard/batterylog.txt");
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(record.getBytes());
				fos.flush();
				fos.close();
				Thread.sleep(3000);
				step++;
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
	
	
	
	
	
	

	private void setLowBatteryThresholdFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.setBatteryThreshhold111(index);
			record += "setLowBatteryThreshold ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setLowBatteryThreshold test end"; 

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
	private void setLowBatteryThresholdFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.setBatteryThreshhold(String.valueOf(sequenceID), "D180EMDK", "2", "Low battery!");
			record += "setLowBatteryThreshold ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setLowBatteryThreshold test end"; 

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
	private void setLowBatteryThresholdFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.setBatteryThreshhold(String.valueOf(sequenceID), "D180EMDK", "45", "Low battery!");
			record += "setLowBatteryThreshold ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setLowBatteryThreshold test end"; 

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
	private void setLowBatteryThresholdFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.setBatteryThreshhold(String.valueOf(sequenceID), "D180EMDK", "4", "Low battery!");
			record += "setLowBatteryThreshold ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setLowBatteryThreshold test end"; 

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
	private void setLowBatteryThresholdFUN5()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.setBatteryThreshhold(String.valueOf(sequenceID), "D180EMDK", "5", "Low battery!");
			record += "setLowBatteryThreshold ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setLowBatteryThreshold test end"; 

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
	private void setLowBatteryThresholdFUN6()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.setBatteryThreshhold(String.valueOf(sequenceID), "D180EMDK", "6", "Low battery!");
			record += "setLowBatteryThreshold ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setLowBatteryThreshold test end"; 

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
	private void setLowBatteryThresholdFUN7()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.setBatteryThreshhold(String.valueOf(sequenceID), "D180EMDK", "0", "");
			record += "setLowBatteryThreshold ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setLowBatteryThreshold test end"; 

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
	
	private void setLowBatteryThresholdFUN8()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.setBatteryThreshhold(String.valueOf(sequenceID), "D180EMDK", "0", "Low battery!");
			record += "setLowBatteryThreshold ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setLowBatteryThreshold test end"; 

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
	private void setLowBatteryThresholdFUN9()
	{
		for(int i =0;i<1000;i++)
		{
			TestOpenFUN1("open|1|APP2|0|D180EMDK");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TestsetSessionKeyFUN1("setSessionKey|1|D180EMDK");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getBatteryLevelFUN1("getBatteryLevel|1|D180EMDK");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setLowBatteryThresholdFUN4();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			readCardDataAllFUN1("readCardData|1|D180EMDK|Sale|100.00|5.00|256|20000|$2000.00|Transaction...");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getLowBatteryThresholdFUN1("getLowBatteryTRhreshold|1|D180EMDK");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TestCloseFUN1("close|1|D180EMDK");
		}
	}
	private void setLowBatteryThresholdFUN10()
	{
		for(int i=0;i<200;i++)
		{
			TestOpenFUN1("open|1|APP2|0|D180EMDK");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TestsetSessionKeyFUN1("setSessionKey|1|D180EMDK");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getBatteryLevelFUN1("getBatteryLevel|1|D180EMDK");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setLowBatteryThresholdFUN1("setLowBatteryTRhreshold|1|D180EMDK|1|The power is too low...");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			readCardDataAllFUN1("readCardData|1|D180EMDK|Sale|100.00|5.00|256|20000|$2000.00|Transaction...");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getLowBatteryThresholdFUN1("getLowBatteryTRhreshold|1|D180EMDK");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TestCloseFUN1("close|1|D180EMDK");
		}
	}
	private void getLowBatteryThresholdFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.getLowBatteryThreshhold111(index);
			record += "getLowBatteryTRhreshold ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getLowBatteryTRhreshold test end"; 

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
	
	private void getLowBatteryThresholdFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getLowBatteryThreshold API");
			step++;
			byte[] by = base.getLowBatteryThreshhold1(String.valueOf(sequenceID), "D180EMDK");
			record += "getLowBatteryTRhreshold ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getLowBatteryTRhreshold test end"; 

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
	private void setEMVTagsFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505setEMVTags API");
			step++;
			byte[] by = base.setEMVTags111(index);
			record += "setEMVTags ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setEMVTags test end"; 

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
	private void setEMVTagsFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505setEMVTags API");
			step++;
			byte[] by = base.setEMVTags(String.valueOf(sequenceID), "D180EMDK", "5A085413330089601075|9F41021234|9F02051252478796|9F1B03123456");
			record += "setEMVTags ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setEMVTags test end"; 

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
	
	
	private void setEMVTagsFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.setEMVTags1(String.valueOf(sequenceID), "D180EMDK", "5A085413330089601075|9F02020101|9F41021234|9F02051252478796");
			record += "setEMVTags ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setEMVTags test end"; 

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
	private void setEMVTagsFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.setEMVTags(String.valueOf(sequenceID), "D180EMDK", "");
			record += "setEMVTags ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setEMVTags test end"; 

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
	
	private void setEMVTagsFUN5()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505setEMVTags API");
			step++;
			byte[] by = base.setEMVTags("860821", "PAXEMDK012345", "setEmvTags|5A085413330089601075|5A085413330089601075");
			record += "setEMVTags ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setEMVTags test end"; 

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
	private void setEMVTagsFUN6()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505setEMVTags API");
			step++;
			byte[] by = base.setEMVTags(String.valueOf(sequenceID), "D180EMDK", "5F25021A2B");
			record += "setEMVTags ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setEMVTags test end"; 

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
	private void setEMVTagsFUN7()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		for (int i = 0; i < 100; i++) {
			try{
				//ui.scrCls();
				//ui.scrShowText("%P0505setEMVTags API");
				step++;
				byte[] by = base.setEMVTags(String.valueOf(sequenceID), "D180EMDK", "5A085413330089601075");
				record += "setEMVTags ret: " + new String(by) + "     number:" +(i+1) + "\n";
				System.out.println(new String(by));
				step++;
				
				record += "setEMVTags test end"; 
	
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
//	
//	private void setEMVTagsFUN6()
//	{
//		String record = "";
//		sequenceID++;
//		int step = 0;
//		try{
//			//ui.scrCls();
//			//ui.scrShowText("%P0505setEMVTags API");
//			step++;
//			byte[] by = base.setEMVTags(String.valueOf(sequenceID), "D180EMDK", "9F14830101FF0000");
//			record += "setEMVTags ret: " + new String(by) + "\n";
//			System.out.println(new String(by));
//			step++;
//			
//			record += "setEMVTags test end"; 
//
//			Bundle bundle = new Bundle();
//			bundle.putString("result", record);
//			Message msg = handler.obtainMessage();
//			msg.what = 1;
//			msg.setData(bundle);
//			handler.sendMessage(msg);
//		}catch(Exception exception){
//			exception.printStackTrace();
//			Bundle bundle = new Bundle();
//			bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
//			Message msg = handler.obtainMessage();
//			msg.what = 2;
//			msg.setData(bundle);
//			handler.sendMessage(msg);
//		} finally {
//			progressDialog.dismiss();
//		}
//	}
//	
//	private void setEMVTagsFUN7()
//	{
//		String record = "";
//		sequenceID++;
//		int step = 0;
//		try{
//			//ui.scrCls();
//			//ui.scrShowText("%P0505setEMVTags API");
//			step++;
//			byte[] by = base.setEMVTags(String.valueOf(sequenceID), "D180EMDK", "4F81FF000|9F14830101FF0000");
//			record += "setEMVTags ret: " + new String(by) + "\n";
//			System.out.println(new String(by));
//			step++;
//			
//			record += "setEMVTags test end"; 
//
//			Bundle bundle = new Bundle();
//			bundle.putString("result", record);
//			Message msg = handler.obtainMessage();
//			msg.what = 1;
//			msg.setData(bundle);
//			handler.sendMessage(msg);
//		}catch(Exception exception){
//			exception.printStackTrace();
//			Bundle bundle = new Bundle();
//			bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
//			Message msg = handler.obtainMessage();
//			msg.what = 2;
//			msg.setData(bundle);
//			handler.sendMessage(msg);
//		} finally {
//			progressDialog.dismiss();
//		}
//	}
//	
	private void getEMVTagsFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getEMVTags API");
			step++;
			byte[] by = base.getEMVTags111(index);
			record += "getEMVTags ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getEMVTags test end"; 

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
	private void getEMVTagsFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getEMVTags API");
			step++;
			byte[] by = base.getEMVTags(String.valueOf(sequenceID), "D180EMDK","getEmvTags|9F41|9F02|9F1B");
			record += "getEMVTags ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getEMVTags test end"; 

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
	private void getEMVTagsFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getEMVTags API");
			step++;
			byte[] by = base.getEMVTags1(String.valueOf(sequenceID), "D180EMDK","5A|9F41|9F02|9F1B");
			record += "getEMVTags ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getEMVTags test end"; 

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
	private void getEMVTagsFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getEMVTags API");
			step++;
			byte[] by = base.getEMVTags(String.valueOf(sequenceID), "D180EMDK","5F25|5F24");
			record += "getEMVTags ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getEMVTags test end"; 

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
	private void getEMVTagsFUN5()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		for(int i=0;i<100;i++)
		{
			try{
				//ui.scrCls();
				//ui.scrShowText("%P0505getEMVTags API");
				step++;
				byte[] by = base.getEMVTags(String.valueOf(sequenceID), "D180EMDK","5A");
				record += "getEMVTags ret: " + new String(by) + "     number:" +(i+1) + "\n";
				System.out.println(new String(by));
				step++;
				
				record += "getEMVTags test end"; 
	
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
	private void createMACFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505createMAC API");
			step++;
			byte[] by = base.createMAC111(index);
			record += "createMAC ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "createMAC test end"; 

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
	
	private void createMACFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505createMAC API");
			step++;
			byte[] by = base.createMAC(String.valueOf(sequenceID), "D180EMDK", "1A2B3C4D1A2B3C4D1A2B3C4D1A2B3C4D");
			record += "createMAC ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "createMAC test end"; 

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
	
	private void createMACFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505createMAC API");
			step++;
			byte[] by = base.createMAC(String.valueOf(sequenceID), "D180EMDK", "1A2B3C4D1A2B3C4D1A2B3C4D1A2B3C4D1A2B3C4D1A2B3C4D");
			record += "createMAC ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "createMAC test end"; 

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
	
	private void createMACFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505createMAC API");
			step++;
			byte[] by = base.createMAC1(String.valueOf(sequenceID), "D180EMDK", "1A2B3C4D1A2B3C4D");
			record += "createMAC ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "createMAC test end"; 

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
	
	private void createMACFUN5()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505createMAC API");
			step++;
			byte[] by = base.createMAC(String.valueOf(sequenceID), "D180EMDK", "");
			record += "createMAC ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "createMAC test end"; 

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
	private void createMACFUN6()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505createMAC API");
			step++;
			byte[] by = base.createMAC(String.valueOf(sequenceID), "D180EMDK", "1A2B3C4D2E6A7A555A5A1A3C2C2B");
			record += "createMAC ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "createMAC test end"; 

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
	private void createMACFUN7()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		for(int i=0;i<200;i++)
		{
			try{
				//ui.scrCls();
				//ui.scrShowText("%P0505createMAC API");
				step++;
				byte[] by = base.createMAC(String.valueOf(sequenceID), "D180EMDK", "1A2B3C4D1A2B3C4D");
				record += "createMAC ret: " + new String(by) + "     number:" +(i+1) + "\n";
				System.out.println(new String(by));
				step++;
				
				record += "createMAC test end"; 
	
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
	private void validateMACFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505validateMAC API");
			step++;
			byte[] by = base.validateMAC111(index);
			record += "validateMAC ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "validateMAC test end"; 

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
	private void validateMACFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505validateMAC API");
			step++;
			byte[] by = base.validateMAC(String.valueOf(sequenceID), "D180EMDK", "1C3E6F9449CB49B4", "", 
					    "", "", "message1", "message2", "1A2B3C4D1A2B3C4D1A2B3C4D1A2B3C4D");
			record += "validateMAC ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "validateMAC test end"; 

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
	private void validateMACFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505validateMAC API");
			step++;
			byte[] by = base.validateMAC(String.valueOf(sequenceID), "D180EMDK", "F87BCBE88883243D", "", 
					    "", "", "", "", "1A2B3C4D1A2B3C4D1A2B3C4D1A2B3C4D1A2B3C4D1A2B3C4D");
			record += "validateMAC ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "validateMAC test end"; 

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
	private void validateMACFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505validateMAC API");
			step++;
			byte[] by = base.validateMAC1(String.valueOf(sequenceID), "D180EMDK", "F87BCBE88883243D", "", 
					    "", "", "message1", "message2", "1A2B3C4D1A2B3C4D");
			record += "validateMAC ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "validateMAC test end"; 

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
	private void validateMACFUN5()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505validateMAC API");
			step++;
			byte[] by = base.validateMAC(String.valueOf(sequenceID), "D180EMDK", "", "", 
					    "", "", "PAX", "SZ", "1A2B3C4D1A2B3C4D");
			record += "validateMAC ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "validateMAC test end"; 

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
	private void validateMACFUN6()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505validateMAC API");
			step++;
			byte[] by = base.validateMAC(String.valueOf(sequenceID), "D180EMDK", "", "F87BCBE88883243D", 
					    "", "", "", "1234567890123456789", "");
			record += "validateMAC ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "validateMAC test end"; 

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
	
	private void completeOnLineEMVFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505completeOnLineEMV API");
			step++;
			byte[] by = base.completeOnLineEMV111(index);
			record += "completeOnLineEMV ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "completeOnLineEMV test end"; 

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
	
	private void completeOnLineEMVFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505completeOnLineEMV API");
			step++;
			byte[] by = base.completeOnLineEMV(String.valueOf(sequenceID), "D180EMDK", "1", "False", "91053A4B123C1F71023B1C72010A");
			record += "completeOnLineEMV ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "completeOnLineEMV test end"; 

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
	private void completeOnLineEMVFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505completeOnLineEMV API");
			step++;
			byte[] by = base.completeOnLineEMV(String.valueOf(sequenceID), "D180EMDK", "2", "true", "91053A4B123C1F71023B1C72010A");
			record += "completeOnLineEMV ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "completeOnLineEMV test end"; 

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
	private void completeOnLineEMVFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505completeOnLineEMV API");
			step++;
			byte[] by = base.completeOnLineEMV1(String.valueOf(sequenceID), "D180EMDK", "0", "true", "91053A4B123C1F71023B1C72010A");
			record += "completeOnLineEMV ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "completeOnLineEMV test end"; 

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
	private void completeOnLineEMVFUN5()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505completeOnLineEMV API");
			step++;
			byte[] by = base.completeOnLineEMV2(String.valueOf(sequenceID), "D180EMDK", "hostDecision", "displayResult", "TagIDs=1|2|3", "Values=111|222|333");
			record += "completeOnLineEMV ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "completeOnLineEMV test end"; 

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
	
	private void completeOnLineEMVFUN6()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			
			byte[] by2 = base.readCardData(String.valueOf(sequenceID), "D180EMDK", "Sale", "100.00", "5.00","1", "20000","$2000", "Insert Card-->");
			String content = new String(by2);
			String w1=content.substring(0, 7);
			
			String suc="success";
			String aidlist="AIDList";
			if(w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID=content.substring(content.indexOf("|",1), content.length());
				String str = AIDList+"|1|D180EMDK"+AID;
					
				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				maskP = content1.split("\\|")[11]; 
				record += "readCardData ret: " + new String(by4) + "\n";
			}
			else if(w1.equals(suc))
			{
				maskP = content.split("\\|")[11]; 
				record += "readCardData ret: " + new String(by2) + "\n";
			}		
			else
			{
				record += "readCardData ret: " + new String(by2) + "\n";
			}
			
			Thread.sleep(500);
			byte[] by = base.completeOnLineEMV(String.valueOf(sequenceID), "D180EMDK", "1", "False", "91053A4B123C1F71023B1C72010A");
			record += "completeOnLineEMV ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard111(index);
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardStep(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard111Step(index);
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "999.00", 
					"5.00", "1", "true", "true", "true", "true","20000","91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	
	private void authorizeCardFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "999.00", 
					"5.00", "2", "true", "true", "true", "true","20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard1(String.valueOf(sequenceID), "D180EMDK", "999.00", 
					"5.00", "0", "false", "false", "false", "false","20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN5()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "999.00", 
					"5.00", "0", "true", "true", "true", "true", "20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN6()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "999.00", 
					"", "0", "true", "true", "true", "true", "20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN7()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "999.00", 
					"tip", "0", "true", "true", "true", "true", "20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN8()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "1", 
					"1", "0", "true", "true", "true", "true", "20000","91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN9()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "10", 
					"10", "0", "true", "true", "true", "true","20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN10()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "1.50", 
					"15.0", "0", "true", "true", "true", "true", "20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN11()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "1.01", 
					"1.01", "0", "true", "true", "true", "true", "20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN12()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "10.00", 
					"10.00", "0", "true", "true", "true", "true", "20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN13()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "10.10", 
					"10.10", "0", "true", "true", "true", "true", "20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN14()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "10.111", 
					"10.111", "0", "true", "true", "true", "true","20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN15()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "", 
					"", "0", "true", "true", "true", "true","20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN16()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "1.1", 
					"1.1", "0", "true", "true", "true", "true", "20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	private void authorizeCardFUN17()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "1.", 
					"1.", "0", "true", "true", "true", "true", "20000", "91053A4B123C1E|71023B1C|72010A");
			record += "authorizeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	
	private void authorizeCardFUN18()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			for(int i = 0;i<100;i++)
			{
				step++;
				byte[] by2 = base.readCardData(String.valueOf(sequenceID), "D180EMDK", "Sale", "100.00", "5.00","1", "20000","$2000", "Insert Card-->");
				String content = new String(by2);
				String w1=content.substring(0, 7);
				
				String suc="success";
				String aidlist="AIDList";
				if(w1.equals(aidlist)) {
					content = content.trim();
					String AIDList = content.split("\\|")[0];
					String AID=content.substring(content.indexOf("|",1), content.length());
					String str = AIDList+"|1|D180EMDK"+AID;
						
					byte[] by4 = base.readCardData1111(str);
					String content1 = new String(by4);
					maskP = content1.split("\\|")[11]; 
					record += "readCardData ret: " + new String(by4) + "\n";
				}
				else if(w1.equals(suc))
				{
					maskP = content.split("\\|")[11]; 
					record += "readCardData ret: " + new String(by2) + "\n";
				}
				else
				{
					record += "readCardData ret: " + new String(by2) + "\n";
				}
				Thread.sleep(500);
				byte[] by = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "999.00", 
						"5.00", "1", "true", "true", "true", "true","20000","91053A4B123C1E|71023B1C|72010A");
				record += "authorizeCard ret: " + new String(by)+"     number:"+i + "\n";
				System.out.println(new String(by));
				step++;
				
				record += "authorizeCard test end"; 
	
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
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
	
	private void ICTestFUN1()
	{
		ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
		String val = cfg.getValueByTag(mInterface, "readCardData|1|D180EMDK|Sale|100.00|5.00|1|20000|$2000|Insert Card-->");
		getIndex(9, val);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConfigManager cfg2 = ConfigManager.getInstance(getApplicationContext());
		String val2 = cfg2.getValueByTag(mInterface, "authorizeCard|1|D180EMDK|999.00|5.00|0|true|true|true|true|91053A4B123C1E|71023B1C|72010A");
		getIndex(20, val);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConfigManager cfg3 = ConfigManager.getInstance(getApplicationContext());
		String val3 = cfg3.getValueByTag(mInterface, "completeOnLineEMV|1|D180EMDK|0|true|");
		getIndex(19, val);
	}
	private void ICTestFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505authorizeCard API");
			step++;
			
			byte[] by2 = base.readCardData(String.valueOf(sequenceID), "D180EMDK", "Sale", "100.00", "5.00","1", "20000","$2000", "Insert Card-->");
			String content = new String(by2);
			String w1=content.substring(0, 7);
			
			String suc="success";
			String aidlist="AIDList";
			if(w1.equals(aidlist)) {
				content = content.trim();
				String AIDList = content.split("\\|")[0];
				String AID=content.substring(content.indexOf("|",1), content.length());
				String str = AIDList+"|1|D180EMDK"+AID;
					
				byte[] by4 = base.readCardData1111(str);
				String content1 = new String(by4);
				maskP = content1.split("\\|")[11]; 
				record += "readCardData ret: " + new String(by4) + "\n";
			}
			else if(w1.equals(suc))
			{
				maskP = content.split("\\|")[11]; 
				record += "readCardData ret: " + new String(by2) + "\n";
			}	
			else
			{
				record += "readCardData ret: " + new String(by2) + "\n";
			}
			Thread.sleep(500);
			byte[] by = base.completeOnLineEMV(String.valueOf(sequenceID), "D180EMDK", "1", "False", "91053A4B123C1F71023B1C72010A");
			record += "completeOnLineEMV ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "authorizeCard test end"; 

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
	
	private void ICTestFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		for(int i = 0;i<1000;i++)
		{
			try{
				step++;
				byte[] by2 = base.readCardData(String.valueOf(sequenceID), "D180EMDK", "Sale", "100.00", "5.00","1", "20000","$2000", "Insert Card-->");
				String content = new String(by2);
				String w1=content.substring(0, 7);
				
				String suc="success";
				String aidlist="AIDList";
				if(w1.equals(aidlist)) {
					content = content.trim();
					String AIDList = content.split("\\|")[0];
					String AID=content.substring(content.indexOf("|",1), content.length());
					String str = AIDList+"|1|D180EMDK"+AID;
						
					byte[] by4 = base.readCardData1111(str);
					String content1 = new String(by4);
					maskP = content1.split("\\|")[11]; 
					record += "readCardData ret: " + new String(by4) + "\n";
				}
				else if(w1.equals(suc))
				{
					maskP = content.split("\\|")[11]; 
					record += "readCardData ret: " + new String(by2) + "\n";
				}		
				else
				{
					record += "readCardData ret: " + new String(by2) + "\n";
				}
				byte[] by1 = base.authorizeCard(String.valueOf(sequenceID), "D180EMDK", "999.00", 
						"5.00", "1", "false", "false", "false", "false","20000","91053A4B123C1E|71023B1C|72010A");
				
				byte[] by = base.completeOnLineEMV(String.valueOf(sequenceID), "D180EMDK", "1", "False", "91053A4B123C1F71023B1C72010A");
				
				byte[] by3 = base.getBatteryLevel111("getBatteryLevel|1|D180EMDK");
				record += "BatteryLevel: " + new String(by3) + "\n";
				
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
				record += "Time" + df.format(new Date())+"\n"+ "NUMBER:"+(i+1)+"\n\n";
			
				File file = new File("mnt/sdcard/log.txt");
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(record.getBytes());
				fos.flush();
				fos.close();
				Thread.sleep(300000);
				step++;
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
	private void ICTestFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		for(int i = 0;i<1000;i++)
		{
			try{
				step++;
				byte[] by2 = base.readCardData(String.valueOf(sequenceID), "D180EMDK", "Sale", "100.00", "5.00","1", "20000","$2000", "Insert Card-->");
				String content = new String(by2);
				String w1=content.substring(0, 7);
				
				String suc="success";
				String aidlist="AIDList";
				if(w1.equals(aidlist)) {
					content = content.trim();
					String AIDList = content.split("\\|")[0];
					String AID=content.substring(content.indexOf("|",1), content.length());
					String str = AIDList+"|1|D180EMDK"+AID;
						
					byte[] by4 = base.readCardData1111(str);
					String content1 = new String(by4);
					maskP = content1.split("\\|")[11]; 
					record += "readCardData ret: " + new String(by4) + "\n";
				}
				else if(w1.equals(suc))
				{
					maskP = content.split("\\|")[11]; 
					record += "readCardData ret: " + new String(by2) + "\n";
				}		
				else
				{
					record += "readCardData ret: " + new String(by2) + "\n";
				}
				
				byte[] by3 = base.getBatteryLevel111("getBatteryLevel|1|D180EMDK");
				record += "BatteryLevel: " + new String(by3) + "\n";
				
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
				record += "Time" + df.format(new Date())+"\n"+ "NUMBER:"+(i+1)+"\n\n";
			
				File file = new File("mnt/sdcard/log.txt");
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(record.getBytes());
				fos.flush();
				fos.close();
				Thread.sleep(300000);
				step++;
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
	
	
	private void promptAdditionalInfoFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505promptAdditionalInfo API");
			step++;
			byte[] by = base.promptAdditionalInfo111(index);
			record += "promptAdditionalInfo ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptAdditionalInfo test end"; 

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
	private void promptAdditionalInfoFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505promptAdditionalInfo API");
			step++;
			byte[] by = base.promptAdditionalInfo(String.valueOf(sequenceID), "D180EMDK", "", "", "", "", "", "20000");
			record += "promptAdditionalInfo ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptAdditionalInfo test end"; 

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
	private void promptAdditionalInfoFUN3()
	{
		String record = "";
		sequenceID++;
		boolean a=true;
		int step = 0;
		try{
			step++;
			byte[] by = base.promptAdditionalInfo(String.valueOf(sequenceID), "D180EMDK", "1.00", "123456", "TRUE", "FALSE", "3.00", "20000");
			record += "promptAdditionalInfo ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptAdditionalInfo test end"; 

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
	private void promptAdditionalInfoFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505promptAdditionalInfo API");
			step++;
			byte[] by = base.promptAdditionalInfo(String.valueOf(sequenceID), "D180EMDK", "0.01", "123456", "FALSE", "TRUE", "1.21", "20000");
			record += "promptAdditionalInfo ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptAdditionalInfo test end"; 

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
	private void promptAdditionalInfoFUN5()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505promptAdditionalInfo API");
			step++;
			byte[] by = base.promptAdditionalInfo(String.valueOf(sequenceID), "D180EMDK", "970.00", "123", "FALSE", "FALSE", "3.00", "20000");
			record += "promptAdditionalInfo ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptAdditionalInfo test end"; 

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
	private void removeCardFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505removeCard API");
			step++;
			byte[] by = base.removeCard111(index);
			record += "removeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "removeCard test end"; 

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
	private void removeCardFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505removeCard API");
			step++;
			byte[] by = base.removeCard(String.valueOf(sequenceID), "D180EMDK", "EMV card", "Please pull out the card...");
			record += "removeCard ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "removeCard test end"; 

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
	private void setParameterFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = base.setParameter111(index);
			record += "setParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setParameter test end"; 

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
	private void setParameterFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505setParameter API");
			step++;
			byte[] by = base.setParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg=Welcome","sleepModeTimeout=0",
					"dataEncryptionKeySlot=8","dataEncryptionType=4","maskFirstDigits=4","LanguageType=0","AIDFilterAllowedFlag=0",
					"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
			
			
			
			
			record += "setParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setParameter test end"; 

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
	private void setParameterFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505setParameter API");
			step++;
			byte[] by = base.setParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg=Hello","sleepModeTimeout=10000",
					"dataEncryptionKeySlot=8","dataEncryptionType=4","maskFirstDigits=6","LanguageType=0","AIDFilterAllowedFlag=0",
					"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
			
			record += "setParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setParameter test end"; 

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
	private void setParameterFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505setParameter API");
			step++;
			byte[] by = base.setParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg=Welcome", "sleepModeTimeout=0",
					"dataEncryptionKeySlot=8","dataEncryptionType=4","maskFirstDigits=1","LanguageType=0","AIDFilterAllowedFlag=0",
					"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
			
			
			record += "setParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setParameter test end"; 

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
	private void setParameterFUN5()
	{
		String record = "";

		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505setParameter API");
			step++;
			byte[] by = base.setParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg=PAXPAX", "sleepModeTimeout=45000",
					"dataEncryptionKeySlot=2","dataEncryptionType=0","maskFirstDigits=6","LanguageType=0","AIDFilterAllowedFlag=0",
					"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
			
			
			record += "setParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setParameter test end"; 

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
	private void setParameterFUN55()
	{
		String record = "";

		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505setParameter API");
			step++;
			byte[] by = base.setParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg=PAXPAX", "sleepModeTimeout=45",
					"dataEncryptionKeySlot=2","dataEncryptionType=0","maskFirstDigits=6","LanguageType=0","AIDFilterAllowedFlag=0",
					"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
			

			
			
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
	
	private void setParameterFUN6()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505setParameter API");
			step++;
			byte[] by = base.setParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg=IDLE TEST MESSAGE OK", "sleepModeTimeout=0",
					"dataEncryptionKeySlot=8","dataEncryptionType=4","maskFirstDigits=1","LanguageType=0","AIDFilterAllowedFlag=0",
					"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
			
			
			record += "setParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setParameter test end"; 

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
	private void setParameterFUN7()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
		//	ui.scrCls();
			//ui.scrShowText("%P0505setParameter API");
			step++;
			byte[] by = base.setParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg=IDLE TEST MESSAGE OK", "sleepModeTimeout=10000",
					"dataEncryptionKeySlot=8","dataEncryptionType=4","maskFirstDigits=1","LanguageType=0","AIDFilterAllowedFlag=0",
					"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
			
			
			
			record += "setParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setParameter test end"; 

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
	private void setParameterFUN8()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
		//	ui.scrCls();
			//ui.scrShowText("%P0505setParameter API");
			step++;
			byte[] by = base.setParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg=Welcome", "sleepModeTimeout=9000",
					"dataEncryptionKeySlot=8","dataEncryptionType=4","maskFirstDigits=6","LanguageType=0","AIDFilterAllowedFlag=0",
					"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
			
			
			
			record += "setParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setParameter test end"; 

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
	
	private void setParameterFUN9()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
		//	ui.scrCls();
			//ui.scrShowText("%P0505setParameter API");
			step++;
			byte[] by = base.setParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg=Welcome", "sleepModeTimeout=0",
					"dataEncryptionKeySlot=9","dataEncryptionType=3","maskFirstDigits=6","LanguageType=0","AIDFilterAllowedFlag=0",
					"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
			
	
			record += "setParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setParameter test end"; 

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
	private void setParameterFUN10()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
		//	ui.scrCls();
			//ui.scrShowText("%P0505setParameter API");
			step++;
			byte[] by = base.setParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg=Welcome", "sleepModeTimeout=0",
					"dataEncryptionKeySlot=7","dataEncryptionType=2","maskFirstDigits=6","LanguageType=0","AIDFilterAllowedFlag=0",
					"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
			
			
			
			record += "setParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setParameter test end"; 

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
	
	private void setParameterFUN11()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
		//	ui.scrCls();
			//ui.scrShowText("%P0505setParameter API");
			step++;
			byte[] by = base.setParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg=Welcome", "sleepModeTimeout=0",
					"dataEncryptionKeySlot=6","dataEncryptionType=1","maskFirstDigits=6","LanguageType=0","AIDFilterAllowedFlag=0",
					"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
			
			
			record += "setParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setParameter test end"; 

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
	private void setParameterFUN12()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
		//	ui.scrCls();
			//ui.scrShowText("%P0505setParameter API");
			step++;
			byte[] by = base.setParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg=Welcome", "sleepModeTimeout=20000",
					"dataEncryptionKeySlot=8","dataEncryptionType=4","maskFirstDigits=6","LanguageType=1","AIDFilterAllowedFlag=0",
					"BTDiscoveryAllowedFlag=0","DukptKeySlot=1");
		
			
			record += "setParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "setParameter test end"; 

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

	private void getParameterFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getParameter API");
			step++;
			byte[] by = base.getParameter111(index);
			record += "getParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getParameter test end"; 

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
	private void getParameterFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getParameter API");
			step++;
			byte[] by = base.getParameter(String.valueOf(sequenceID), "D180EMDK", "sleepModeTimeout","","","","");
			record += "getParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getParameter test end"; 

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
	private void getParameterFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getParameter API");
			step++;																	
			byte[] by = base.getParameter(String.valueOf(sequenceID), "D180EMDK", "dataEncryptionType","","","","");
			record += "getParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getParameter test end"; 

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
	private void getParameterFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getParameter API");
			step++;
			byte[] by = base.getParameter(String.valueOf(sequenceID), "D180EMDK", "dataEncryptionKeySlot","","","","");
			record += "getParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getParameter test end"; 

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
	private void getParameterFUN5()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getParameter API");
			step++;
			byte[] by = base.getParameter(String.valueOf(sequenceID), "D180EMDK", "maskFirstDigits","","","","");
			record += "getParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getParameter test end"; 

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
	private void getParameterFUN6()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getParameter API");
			step++;
			byte[] by = base.getParameter(String.valueOf(sequenceID), "D180EMDK", "idleMsg","sleepModeTimeout","dataEncryptionType","dataEncryptionKeySlot","maskFirstDigits");
			record += "getParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getParameter test end"; 

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
	private void getParameterFUN7()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getParameter API");
			step++;
			byte[] by = base.getParameter(String.valueOf(sequenceID), "D180EMDK", "", "", "", "", "");
			record += "getParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getParameter test end"; 

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
	private void getParameterFUN8()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getParameter API");
			step++;
			byte[] by = base.getParameter1(String.valueOf(sequenceID), "D180EMDK", "idleMsg","","","","");
			record += "getParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getParameter test end"; 

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
	
	
	private void getParameterFUN9()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505getParameter API");
			step++;
			byte[] by = base.getParameter(String.valueOf(sequenceID), "D180EMDK", "Welcome","","","","");
			record += "getParameter ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "getParameter test end"; 

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
	
	private void TestpromptMenuFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMenu111(index);
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUNStep(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMenu111Step(index);
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMenu(String.valueOf(sequenceID),"D180EMDK","Prompt% 1Menu","",
		    		"choice1","choice2","","","20000");
			
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
		//	progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
					
			byte[] by = ui.promptMenu(String.valueOf(sequenceID),"D180EMDK","Prompt Menu","Please Select",
		    		"","choice2","choice3","","20000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
		//	progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMenu(String.valueOf(sequenceID),"D180EMDK","Prompt Menu","Please Select",
		    		"","","choice3","choice4","10000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUN5()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMenu(String.valueOf(sequenceID),"D180EMDK","Prompt Menu","Please Select",
		    		"sale","","offline","","10000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUN6()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMenu(String.valueOf(sequenceID),"D180EMDK","Prompt Menu","Please Select",
		    		"","void","","REFUND","10000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUN7()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMenu(String.valueOf(sequenceID),"D180EMDK","","",
		    		"","void","","","20000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
			//progressDialog.dismiss();
		}
	}
	
	private void TestpromptMenuFUN8()
	{
		String record = "";

		int step = 0;
		try{
			byte[] by = ui.promptMenu(String.valueOf(sequenceID),"D180EMDK","Prompt Menu","Please Select",
		    		"","","","","20000");
			
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
			//progressDialog.dismiss();
		}
	}
	
	private void TestpromptMenuFUN9()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMenu(String.valueOf(sequenceID),"D180EMDK","Prompt% 1Men","Please Select",
		    		"Œœ","ÀÂÆ","ÇÈÉÊ","ÎÏÔÖÙÛÜ","20000");
//			"fafju","脌脗脝","脟脠脡脢","脦脧脭脰脵脹脺","20000");
			record += "promptMenu ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMenu test end"; 

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
		//	progressDialog.dismiss();
		}
	}
	private void TestpromptMenuFUN10()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			for(int i = 0 ;i<200; i++)
			{
				byte[] by = ui.promptMenu(String.valueOf(sequenceID),"D180EMDK","Prompt Menu","Please Select",
		    		"sale","void","offline","REFUND","5000");
				record += "promptMenu ret: " + new String(by) + "    step:"+(i+1)+"\n";
				System.out.println(new String(by));
				step++;
				record += "promptMenu test end"; 
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
			
		}catch(Exception exception){
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMessageFUN1(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMessage111(index);
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMessageFUNStep(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMessage111Step(index);
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMessageFUN2()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMessage(String.valueOf(sequenceID), "D180EMDK", "Prompt Menu", "Select",
					"A.OK", "B.Cancel", "TRUE","20000");
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMessageFUN3()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMessage(String.valueOf(sequenceID), "D180EMDK", "Prompt Menu", "Select",
					"A.OK", "B.Cancel", "TRUE","20000");
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMessageFUN4()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMessage(String.valueOf(sequenceID), "D180EMDK", "", "Select",
					"", "", "TRUE","20000");	
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			record += "promptMessage test end"; 
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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMessageFUN5()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMessage(String.valueOf(sequenceID), "D180EMDK", "", "",
					"", "", "TRUE","10000");
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMessageFUN6()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMessage(String.valueOf(sequenceID), "D180EMDK", "Prompt Messsage", "Select aaaaaaaa",
					"Aaaaaaaaaaaaaaaaaaaa", "Aaaaaaaaaaaaaaaaaaaa", "faLse","20000");
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMessageFUN7()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			byte[] by = ui.promptMessage(String.valueOf(sequenceID), "D180EMDK", "Prompt Message", "Select aaaaaaaa",
					"Aaaaaaaaaaaaaaaaaaaa", "Bbbbbbbbbbbbbbb", "tRuE","20000");
			
			record += "promptMessage ret: " + new String(by) + "\n";
			System.out.println(new String(by));
			step++;
			
			record += "promptMessage test end"; 

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
			//progressDialog.dismiss();
		}
	}
	
	private void TestpromptMessageFUN8()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			for(int i =0;i<200; i++)
			{
				byte[] by = ui.promptMessage(String.valueOf(sequenceID), "D180EMDK", "Prompt Menu", "Select",
					"A.OK", "B.Cancel", "TRUE","20000");
				
				record += "promptMessage ret: " + new String(by) + "       number:"+(i+1)+"\n";
				System.out.println(new String(by));
				step++;
				
				record += "promptMessage test end"; 
	
				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
			}
		}catch(Exception exception){
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			//progressDialog.dismiss();
		}
	}
	private void TestpromptMessageFUN9()
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			
				byte[] by = ui.promptMessage(String.valueOf(sequenceID), "D180EMDK", "脿芒忙莽", "猫茅锚毛",
					"卯茂么枚", "霉没眉", "TRUE","20000");
				record += "promptMessage ret: " + new String(by) + "\n";
				System.out.println(new String(by));
				step++;
				
				record += "promptMessage test end"; 
	
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
			//progressDialog.dismiss();
		}
	}
	
	//锟斤拷锟斤拷APP锟斤拷一锟轿达拷1024锟斤拷小
	private void downloadFile_EMVPARA1() {
		String record = "";
		sequenceID++;
		int step = 0;

		// File file = new File(fileName);
	//	FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/EMVPARA");
			//String fileName = "E:/D180/a.txt";
		//	out = new FileOutputStream(fileName);
		//	out.close();
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[2048];
			int s = length / 2048;
			int y = length % 2048;
			int a = 1;
			int b = 0;
			//base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				// System.out.write(tempbytes, 0, byteread);

				String val = String.valueOf(tempbytes);
				if (1 == a & 2048==byteread) {
					b++;
					
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"START"+String.valueOf(length), "2048", tempbytes);
				
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile START ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}
				else if (byteread<2048) {
					
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"START"+String.valueOf(length), "0", null);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				} else if(0 == y & s ==a){
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes); 
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) + "\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				}else {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"LOADING", "2048", tempbytes);
				//	record += "downloadFile ret: " + new String(by) + "\n";
				//	System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					//record += "downloadFile ret: " + new String(by) + "w=" + w + "\n";
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}

				a++;
				step++;

				//record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	//锟斤拷锟截诧拷锟斤拷一锟轿达拷2048锟斤拷小
	private void downloadFile_EMVPARA2() {
		String record = "";
		sequenceID++;
		int step = 0;

		// File file = new File(fileName);
	//	FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/EMVPARA");
			//String fileName = "E:/D180/a.txt";
		//	out = new FileOutputStream(fileName);
		//	out.close();
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[1024];
			int s = length / 1024;
			int y = length % 1024;
			int a = 1;
			int b = 0;
			//base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				// System.out.write(tempbytes, 0, byteread);

				String val = String.valueOf(tempbytes);
			if (1 == a & 1024==byteread) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"START"+String.valueOf(length), "1024", tempbytes);
				
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile START ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				} else if (byteread<1024) {
					
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"START"+String.valueOf(length), "0", null);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				}
//				else if(0 == y & s ==a){
//					b++;
//					String str = String.valueOf(byteread);
//					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
//							"END", str, tempbytes); 
//					
//					System.out.println(new String(by));
//					String str1=new String(by);
//					String str2="success";
//					String w=str1.substring(0, 7);
//					record += "downloadFile END ret: " + new String(by) + "\n";
//					if(!w.equals(str2)){
//						Bundle bundle = new Bundle();
//						bundle.putString("result", record);
//						Message msg = handler.obtainMessage();
//						msg.what = 1;
//						msg.setData(bundle);
//						handler.sendMessage(msg);
//						break;
//					}
//					record += "downloadFile test end";
//				}
				else {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"LOADING", "1024", tempbytes);
				//	record += "downloadFile ret: " + new String(by) + "\n";
				//	System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					//record += "downloadFile ret: " + new String(by) + "w=" + w + "\n";
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}

				a++;
				step++;

				//record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	
	private void downloadFile_EMVPARA3() {
		String record = "";
		sequenceID++;
		int step = 0;

		// File file = new File(fileName);
	//	FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/EMVPARA");
			//String fileName = "E:/D180/a.txt";
		//	out = new FileOutputStream(fileName);
		//	out.close();
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[1024];
			int s = length / 1024;
			int y = length % 1024;
			int a = 1;
			int b = 0;
			//base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				// System.out.write(tempbytes, 0, byteread);

				String val = String.valueOf(tempbytes);
				if (1 == a & 1024==byteread) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"START"+String.valueOf(length), "1024", tempbytes);
				
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile START ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				} else if(a < (s+1)/2){
					
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"START"+String.valueOf(length), "0", null);
					}
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"LOADING", "1024", tempbytes);
				//	record += "downloadFile ret: " + new String(by) + "\n";
				//	System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					//record += "downloadFile ret: " + new String(by) + "w=" + w + "\n";
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}

				a++;
				step++;

				//record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	private void downloadFile_EMVPARA4() {
		String record = "";
		sequenceID++;
		int step = 0;

		// File file = new File(fileName);
	//	FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/EMVPARA");
			//String fileName = "E:/D180/a.txt";
		//	out = new FileOutputStream(fileName);
		//	out.close();
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[512];
			int s = length / 512;
			int y = length % 512;
			int a = 1;
			int b = 0;
			//base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				// System.out.write(tempbytes, 0, byteread);

				String val = String.valueOf(tempbytes);
			if (1 == a & 512==byteread) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"START"+String.valueOf(length), "512", tempbytes);
				
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile START ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				} else if (byteread<512) {
					
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"START"+String.valueOf(length), "0", null);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				} else if(0 == y & s ==a){
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes); 
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) + "\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				}else {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"LOADING", "512", tempbytes);
				//	record += "downloadFile ret: " + new String(by) + "\n";
				//	System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					//record += "downloadFile ret: " + new String(by) + "w=" + w + "\n";
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}

				a++;
				step++;

				//record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	private void downloadFile_EMVPARA5() {
		String record = "";
		sequenceID++;
		int step = 0;

		// File file = new File(fileName);
	//	FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/EMVPARA");
			//String fileName = "E:/D180/a.txt";
		//	out = new FileOutputStream(fileName);
		//	out.close();
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[256];
			int s = length / 256;
			int y = length % 256;
			int a = 1;
			int b = 0;
			//base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				// System.out.write(tempbytes, 0, byteread);

				String val = String.valueOf(tempbytes);
			if (1 == a & 256==byteread) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"START"+String.valueOf(length), "256", tempbytes);
				
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile START ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				} else if (byteread<256) {
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"START"+String.valueOf(length), "0", null);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				} else if(0 == y & s ==a){
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes); 
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) + "\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				}else {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"LOADING", "256", tempbytes);
				//	record += "downloadFile ret: " + new String(by) + "\n";
				//	System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					//record += "downloadFile ret: " + new String(by) + "w=" + w + "\n";
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}

				a++;
				step++;

				//record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	private void downloadFile_EMVPARA6() {
		for(int i = 0;i<10;i++)
		{
			String record = "";
			sequenceID++;
			int step = 0;
	
			// File file = new File(fileName);
		//	FileOutputStream out = null;
			FileInputStream in = null;
			try {
				// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
				// 一锟轿秜锟斤拷锟街斤拷
				
				int byteread = 0;
				File fileName = new File("mnt/sdcard/EMVPARA");
				//String fileName = "E:/D180/a.txt";
			//	out = new FileOutputStream(fileName);
			//	out.close();
				in = new FileInputStream(fileName);
				// ReadFromFile.showAvailableBytes(in);
				// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
				int length = in.available();
				byte[] tempbytes = new byte[1024];
				int s = length / 1024;
				int y = length % 1024;
				int a = 1;
				int b = 0;
				//base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
				while ((byteread = in.read(tempbytes)) != -1) {
					// System.out.write(tempbytes, 0, byteread);
	
					String val = String.valueOf(tempbytes);
					if (1 == a & 1024==byteread) {
						b++;
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"START"+String.valueOf(length), "1024", tempbytes);
					
						System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						record += "downloadFile START ret: " + new String(by) +"\n";
						if(!w.equals(str2)){
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
					}
					else if (byteread<1024) {
						
						if(1 == a)
						{
//							Thread.sleep(100);
							byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
									"START"+String.valueOf(length), "0", null);
						}
						b++;
						String str = String.valueOf(byteread);
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"END", str, tempbytes);
						
						System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						record += "downloadFile END ret: " + new String(by) +"\n";
						if(!w.equals(str2)){
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
						record += "downloadFile test end";
					} else if(0 == y & s ==a){
						b++;
						String str = String.valueOf(byteread);
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"END", str, tempbytes); 
						
						System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						record += "downloadFile END ret: " + new String(by) + "\n";
						if(!w.equals(str2)){
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
						record += "downloadFile test end";
					}else {
						b++;
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"LOADING", "1024", tempbytes);
					//	record += "downloadFile ret: " + new String(by) + "\n";
					//	System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						//record += "downloadFile ret: " + new String(by) + "w=" + w + "\n";
						if(!w.equals(str2)){
							record += "downloadFile ret: " + new String(by) + "\n";
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
					}
	
					a++;
					step++;
	
					//record += "downloadFile test end";
	
					Bundle bundle = new Bundle();
					bundle.putString("result", record);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.setData(bundle);
					handler.sendMessage(msg);
	
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				Bundle bundle = new Bundle();
				bundle.putString("result",
						record + "Exception: " + exception.toString()
								+ " on step: " + step);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e1) {
					}
				}
			}
		}
	}
	private void downloadFile_EMVPARA7() {
		for(int i = 0;i<10;i++)
		{
			String record = "";
			sequenceID++;
			int step = 0;
	
			// File file = new File(fileName);
		//	FileOutputStream out = null;
			FileInputStream in = null;
			try {
				// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
				// 一锟轿秜锟斤拷锟街斤拷
				
				int byteread = 0;
				File fileName = new File("mnt/sdcard/EMVPARA");
				//String fileName = "E:/D180/a.txt";
			//	out = new FileOutputStream(fileName);
			//	out.close();
				in = new FileInputStream(fileName);
				// ReadFromFile.showAvailableBytes(in);
				// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
				int length = in.available();
				byte[] tempbytes = new byte[2048];
				int s = length / 2048;
				int y = length % 2048;
				int a = 1;
				int b = 0;
				//base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
				while ((byteread = in.read(tempbytes)) != -1) {
					// System.out.write(tempbytes, 0, byteread);
	
					String val = String.valueOf(tempbytes);
					if (1 == a & 2048==byteread) {
						b++;
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"START"+String.valueOf(length), "2048", tempbytes);
					
						System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						record += "downloadFile START ret: " + new String(by) +"\n";
						if(!w.equals(str2)){
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
					}
					else if (byteread<2048) {
						
						if(1 == a)
						{
//							Thread.sleep(100);
							byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
									"START"+String.valueOf(length), "0", null);
						}
						b++;
						String str = String.valueOf(byteread);
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"END", str, tempbytes);
						
						System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						record += "downloadFile END ret: " + new String(by) +"\n";
						if(!w.equals(str2)){
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
						record += "downloadFile test end";
					} else if(0 == y & s ==a){
						b++;
						String str = String.valueOf(byteread);
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"END", str, tempbytes); 
						
						System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						record += "downloadFile END ret: " + new String(by) + "\n";
						if(!w.equals(str2)){
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
						record += "downloadFile test end";
					}else {
						b++;
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"LOADING", "2048", tempbytes);
					//	record += "downloadFile ret: " + new String(by) + "\n";
					//	System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						//record += "downloadFile ret: " + new String(by) + "w=" + w + "\n";
						if(!w.equals(str2)){
							record += "downloadFile ret: " + new String(by) + "\n";
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
					}
	
					a++;
					step++;
	
					//record += "downloadFile test end";
	
					Bundle bundle = new Bundle();
					bundle.putString("result", record);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.setData(bundle);
					handler.sendMessage(msg);
	
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				Bundle bundle = new Bundle();
				bundle.putString("result",
						record + "Exception: " + exception.toString()
								+ " on step: " + step);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e1) {
					}
				}
			}
		}
	}
	private void downloadFile_EMVPARA8() {
		String record = "";
		sequenceID++;
		int step = 0;

		// File file = new File(fileName);
	//	FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/EMVPARA");
			//String fileName = "E:/D180/a.txt";
		//	out = new FileOutputStream(fileName);
		//	out.close();
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[2048];
			int s = length / 2048;
			int y = length % 2048;
			int a = 1;
			int b = 0;
			//base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				// System.out.write(tempbytes, 0, byteread);

				String val = String.valueOf(tempbytes);
			if (1 == a & 2048==byteread) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"START"+String.valueOf(length), "2048", tempbytes);
				
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile START ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				} else if (byteread<2048) {
					
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"START"+String.valueOf(length), "0", null);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				}
//				else if(0 == y & s ==a){
//					b++;
//					String str = String.valueOf(byteread);
//					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
//							"END", str, tempbytes); 
//					
//					System.out.println(new String(by));
//					String str1=new String(by);
//					String str2="success";
//					String w=str1.substring(0, 7);
//					record += "downloadFile END ret: " + new String(by) + "\n";
//					if(!w.equals(str2)){
//						Bundle bundle = new Bundle();
//						bundle.putString("result", record);
//						Message msg = handler.obtainMessage();
//						msg.what = 1;
//						msg.setData(bundle);
//						handler.sendMessage(msg);
//						break;
//					}
//					record += "downloadFile test end";
//				}
				else {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"LOADING", "2048", tempbytes);
				//	record += "downloadFile ret: " + new String(by) + "\n";
				//	System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					//record += "downloadFile ret: " + new String(by) + "w=" + w + "\n";
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}

				a++;
				step++;

				//record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	private void downloadFile_EMVPARA9() {
		String record = "";
		sequenceID++;
		int step = 0;

		// File file = new File(fileName);
	//	FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/EMVPARA");
			//String fileName = "E:/D180/a.txt";
		//	out = new FileOutputStream(fileName);
		//	out.close();
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[2048];
			int s = length / 2048;
			int y = length % 2048;
			int a = 1;
			int b = 0;
			//base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				// System.out.write(tempbytes, 0, byteread);

				String val = String.valueOf(tempbytes);
			if (1 == a & 2048==byteread) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"START"+String.valueOf(length), "2048", tempbytes);
				
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile START ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				} else if (byteread<2048) {
					
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"START"+String.valueOf(length), "0", null);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				}
//				else if(0 == y & s ==a){
//					b++;
//					String str = String.valueOf(byteread);
//					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
//							"END", str, tempbytes); 
//					
//					System.out.println(new String(by));
//					String str1=new String(by);
//					String str2="success";
//					String w=str1.substring(0, 7);
//					record += "downloadFile END ret: " + new String(by) + "\n";
//					if(!w.equals(str2)){
//						Bundle bundle = new Bundle();
//						bundle.putString("result", record);
//						Message msg = handler.obtainMessage();
//						msg.what = 1;
//						msg.setData(bundle);
//						handler.sendMessage(msg);
//						break;
//					}
//					record += "downloadFile test end";
//				}
				else {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"LOADING", "2048", tempbytes);
				//	record += "downloadFile ret: " + new String(by) + "\n";
				//	System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					//record += "downloadFile ret: " + new String(by) + "w=" + w + "\n";
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}

				a++;
				step++;

				//record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	private void downloadFile_EMVPARA10() {
			String record = "";
			sequenceID++;
			int step = 0;
	
			// File file = new File(fileName);
		//	FileOutputStream out = null;
			FileInputStream in = null;
			try {
				// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
				// 一锟轿秜锟斤拷锟街斤拷
				
				int byteread = 0;
				File fileName = new File("mnt/sdcard/EMVPARA");
				//String fileName = "E:/D180/a.txt";
			//	out = new FileOutputStream(fileName);
			//	out.close();
				in = new FileInputStream(fileName);
				// ReadFromFile.showAvailableBytes(in);
				// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
				int length = in.available();
				byte[] tempbytes = new byte[2048];
				int s = length / 2048;
				int y = length % 2048;
				int a = 1;
				int b = 0;
				//base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
				while ((byteread = in.read(tempbytes)) != -1) {
					// System.out.write(tempbytes, 0, byteread);
	
					String val = String.valueOf(tempbytes);
					if (1 == a & 2048==byteread) {
						b++;
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"LOADING", "2048", tempbytes);
					
						System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						record += "downloadFile LOADING ret: " + new String(by) +"\n";
						if(!w.equals(str2)){
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
					}
					else if (byteread<2048) {
						
						if(1 == a)
						{
//							Thread.sleep(100);
							byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
									"LOADING", "2048", tempbytes);
						}
						b++;
						String str = String.valueOf(byteread);
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"END", str, tempbytes);
						
						System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						record += "downloadFile END ret: " + new String(by) +"\n";
						if(!w.equals(str2)){
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
						record += "downloadFile test end";
					} else if(0 == y & s ==a){
						b++;
						String str = String.valueOf(byteread);
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"END", str, tempbytes); 
						
						System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						record += "downloadFile END ret: " + new String(by) + "\n";
						if(!w.equals(str2)){
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
						record += "downloadFile test end";
					}else {
						b++;
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"LOADING", "2048", tempbytes);
					//	record += "downloadFile ret: " + new String(by) + "\n";
					//	System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						//record += "downloadFile ret: " + new String(by) + "w=" + w + "\n";
						if(!w.equals(str2)){
							record += "downloadFile ret: " + new String(by) + "\n";
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
					}
	
					a++;
					step++;
	
					//record += "downloadFile test end";
	
					Bundle bundle = new Bundle();
					bundle.putString("result", record);
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.setData(bundle);
					handler.sendMessage(msg);
	
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				Bundle bundle = new Bundle();
				bundle.putString("result",
						record + "Exception: " + exception.toString()
								+ " on step: " + step);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e1) {
					}
				}
			}
	}
	private void downloadFile_EMVPARA11() {
		String record = "";
		sequenceID++;
		int step = 0;

		// File file = new File(fileName);
	//	FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/EMVPARA");
			//String fileName = "E:/D180/a.txt";
		//	out = new FileOutputStream(fileName);
		//	out.close();
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[1024];
			int s = length / 1024;
			int y = length % 1024;
			int a = 1;
			int b = 0;
			//base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				// System.out.write(tempbytes, 0, byteread);

				String val = String.valueOf(tempbytes);
				if (1 == a & 1024==byteread) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"LOADING", "1024", tempbytes);
				
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile LOADING ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}
				else if (byteread<1024) {
					
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"LOADING", "1024", tempbytes);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				} else if(0 == y & s ==a){
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes); 
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) + "\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				}else {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"LOADING", "1024", tempbytes);
				//	record += "downloadFile ret: " + new String(by) + "\n";
				//	System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					//record += "downloadFile ret: " + new String(by) + "w=" + w + "\n";
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}

				a++;
				step++;

				//record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
}
	
	private void downloadFile_EMVPARA12() {
		String record = "";
		sequenceID++;
		int step = 0;

		// File file = new File(fileName);
	//	FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/EMVPARA");
			//String fileName = "E:/D180/a.txt";
		//	out = new FileOutputStream(fileName);
		//	out.close();
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[512];
			int s = length / 512;
			int y = length % 512;
			int a = 1;
			int b = 0;
			//base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				// System.out.write(tempbytes, 0, byteread);

				String val = String.valueOf(tempbytes);
				if (1 == a & 512==byteread) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"LOADING", "512", tempbytes);
				
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile LOADING ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}
				else if (byteread<512) {
					
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"LOADING", "512", tempbytes);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				} else if(0 == y & s ==a){
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes); 
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) + "\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				}else {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"LOADING", "512", tempbytes);
				//	record += "downloadFile ret: " + new String(by) + "\n";
				//	System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					//record += "downloadFile ret: " + new String(by) + "w=" + w + "\n";
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}

				a++;
				step++;

				//record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
}
	
	private void downloadFile_EMVPARA13() {
		String record = "";
		sequenceID++;
		int step = 0;

		// File file = new File(fileName);
	//	FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/EMVPARA");
			//String fileName = "E:/D180/a.txt";
		//	out = new FileOutputStream(fileName);
		//	out.close();
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[256];
			int s = length / 256;
			int y = length % 256;
			int a = 1;
			int b = 0;
			//base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				// System.out.write(tempbytes, 0, byteread);

				String val = String.valueOf(tempbytes);
				if (1 == a & 256==byteread) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"LOADING", "256", tempbytes);
				
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile LOADING ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}
				else if (byteread<256) {
					
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
								"LOADING", "256", tempbytes);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) +"\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				} else if(0 == y & s ==a){
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"END", str, tempbytes); 
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					record += "downloadFile END ret: " + new String(by) + "\n";
					if(!w.equals(str2)){
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile test end";
				}else {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "EMVPARA",
							"LOADING", "256", tempbytes);
				//	record += "downloadFile ret: " + new String(by) + "\n";
				//	System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					//record += "downloadFile ret: " + new String(by) + "w=" + w + "\n";
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}

				a++;
				step++;

				//record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
}
	//锟斤拷锟斤拷APP锟斤拷一锟轿达拷2048锟斤拷小
	private void downloadFile_APP1() {
		String record = "";
		int step = 0;
		sequenceID++;
		FileInputStream in = null;
		try {
			int byteread = 0;
			File fileName = new File("mnt/sdcard/D180_update_test.bin");
			in = new FileInputStream(fileName);
			int length = in.available();
			byte[] tempbytes = new byte[2048];
			int s = length / 2048;
			int y = length % 2048;
			int a = 1;
			int b = 0;
			
			
		//	base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				
				sta = false;
				
				String val = new String(tempbytes);

					if ((1 == a) && (2048 == byteread)) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"START"+String.valueOf(length), "2048", tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
				
					if(!w.equals(str2)){
						record += "downloadFile START ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile START ret: " + new String(by) + "\n";
					
				}else if (byteread<2048) {//锟斤拷锟揭伙拷锟�
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
								"START"+String.valueOf(length), "0", null);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile END ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile END ret: " + new String(by) + "\n";
					record += "downloadFile test end";
				} else if((0 == y) && (s ==a)){//锟斤拷锟揭伙拷锟�
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile END ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile END ret: " + new String(by) + "\n";
					record += "downloadFile test end";
				}else {//锟叫硷拷
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"LOADING", "2048", tempbytes);
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile LOAD ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}

				a++;
				step++;

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);
				
				sta = true;
				Thread.sleep(100);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	//锟斤拷锟斤拷APP锟斤拷一锟轿达拷1024锟斤拷小
	private void downloadFile_APP2() {
		String record = "";
		int step = 0;
		sequenceID++;
		// File file = new File(fileName);
		//FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/D180_update_test.bin");
		//	out =new FileOutputStream(fileName);
		//	out.close();
			//String fileName = "a.txt";
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[1024];
			int s = length / 1024;
			int y = length % 1024;
			int a = 1;
			int b = 0;
		//	base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {

				String val = new String(tempbytes);
				//锟斤拷一锟斤拷
//				if(length<2048)
//				{
//					record +="missing data.";
//					Bundle bundle = new Bundle();
//					bundle.putString("result", record);
//					Message msg = handler.obtainMessage();
//					msg.what = 1;
//					msg.setData(bundle);
//					handler.sendMessage(msg);
//				}
//				else 
					if ((1 == a) && (1024==byteread)) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"START"+String.valueOf(length), "1024", tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
				
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile START ret: " + new String(by) + "\n";
					
				} else if (byteread<1024) {//锟斤拷锟揭伙拷锟�
					
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
								"START"+String.valueOf(length), "0", null);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile END ret: " + new String(by) + "\n";
					record += "downloadFile test end";
				} else if((0 == y) && (s ==a)){//锟斤拷锟揭伙拷锟�
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile END ret: " + new String(by) + "\n";
					record += "downloadFile test end";
				}else {//锟叫硷拷
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"LOADING", "1024", tempbytes);
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					//record += "downloadFile END ret: " + new String(by) + "\n";
				}

				a++;
				step++;

			//	record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	private void downloadFile_APP3() {
		String record = "";
		int step = 0;
		sequenceID++;
		// File file = new File(fileName);
		//FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/D180_update_test.bin");
		//	out =new FileOutputStream(fileName);
		//	out.close();
			//String fileName = "a.txt";
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[512];
			int s = length / 512;
			int y = length % 512;
			int a = 1;
			int b = 0;
		//	base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				// System.out.write(tempbytes, 0, byteread);

				String val = new String(tempbytes);
				//String temp= String.valueOf(length);
				//锟斤拷一锟斤拷
				if ((1 == a) && (512==byteread)) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"START"+String.valueOf(length), "512", tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
				
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile START ret: " + new String(by) + "\n";
					
				} else if (byteread<512) {//锟斤拷锟揭伙拷锟�
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
								"START"+String.valueOf(length), "0", null);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile END ret: " + new String(by) + "\n";
					record += "downloadFile test end";
				} else if((0 == y) && (s ==a)){//锟斤拷锟揭伙拷锟�
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile END ret: " + new String(by) + "\n";
					record += "downloadFile test end";
				}else {//锟叫硷拷
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"LOADING", "512", tempbytes);
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					//record += "downloadFile END ret: " + new String(by) + "\n";
				}

				a++;
				step++;

			//	record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	private void downloadFile_APP4() {
		String record = "";
		int step = 0;
		sequenceID++;
		// File file = new File(fileName);
		//FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/D180_update_test.bin");
		//	out =new FileOutputStream(fileName);
		//	out.close();
			//String fileName = "a.txt";
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[256];
			int s = length / 256;
			int y = length % 256;
			int a = 1;
			int b = 0;
		//	base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				// System.out.write(tempbytes, 0, byteread);

				String val = new String(tempbytes);
				//String temp= String.valueOf(length);
				//锟斤拷一锟斤拷
				if ((1 == a) && (256==byteread)) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"START"+String.valueOf(length), "256", tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
				
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile START ret: " + new String(by) + "\n";
					
				} else if (byteread<256) {//锟斤拷锟揭伙拷锟�
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
								"START"+String.valueOf(length), "0", null);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile END ret: " + new String(by) + "\n";
					record += "downloadFile test end";
				} else if((0 == y) && (s ==a)){//锟斤拷锟揭伙拷锟�
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile END ret: " + new String(by) + "\n";
					record += "downloadFile test end";
				}else {//锟叫硷拷
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"LOADING", "256", tempbytes);
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					//record += "downloadFile END ret: " + new String(by) + "\n";
				}

				a++;
				step++;

			//	record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	
	private void downloadFile_APP5() {
		String record = "";
		int step = 0;
		sequenceID++;
		// File file = new File(fileName);
		//FileOutputStream out = null;
		FileInputStream in = null;
		try {
			// System.out.println("锟斤拷锟街斤拷为锟斤拷位锟斤拷取锟侥硷拷锟斤拷锟捷ｏ拷一锟轿秜锟斤拷锟街节ｏ拷");
			// 一锟轿秜锟斤拷锟街斤拷
			
			int byteread = 0;
			File fileName = new File("mnt/sdcard/D180_update_test1.bin");
		//	out =new FileOutputStream(fileName);
		//	out.close();
			//String fileName = "a.txt";
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 锟斤拷锟斤拷锟斤拷锟街节碉拷锟街斤拷锟斤拷锟斤拷锟叫ｏ拷byteread为一锟轿讹拷锟斤拷锟斤拷纸锟斤拷锟�
			int length = in.available();
			byte[] tempbytes = new byte[256];
			int s = length / 256;
			int y = length % 256;
			int a = 1;
			int b = 0;
		//	base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {
				// System.out.write(tempbytes, 0, byteread);

				String val = new String(tempbytes);
				//String temp= String.valueOf(length);
				//锟斤拷一锟斤拷
				if ((1 == a) && (256==byteread)) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"START"+String.valueOf(length), "256", tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
				
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile START ret: " + new String(by) + "\n";
					
				} else if (byteread<256) {//锟斤拷锟揭伙拷锟�
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
								"START"+String.valueOf(length), "0", null);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile END ret: " + new String(by) + "\n";
					record += "downloadFile test end";
				} else if((0 == y) && (s ==a)){//锟斤拷锟揭伙拷锟�
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile END ret: " + new String(by) + "\n";
					record += "downloadFile test end";
				}else {//锟叫硷拷
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
							"LOADING", "256", tempbytes);
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					//record += "downloadFile END ret: " + new String(by) + "\n";
				}

				a++;
				step++;

			//	record += "downloadFile test end";

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	
	private void downloadFile_APP6() {
		for(int i = 0; i < 1000; i++)
		{
			File file = new File("mnt/sdcard/downloadlog.txt");
			String record = "";
			int step = 0;
			sequenceID++;
			FileInputStream in = null;
			try {
				
				TestOpenFUN2();
				TestsetSessionKeyFUN2();
				
				
				int byteread = 0;
				File fileName = new File("mnt/sdcard/D180_update_test.bin");
				in = new FileInputStream(fileName);
				int length = in.available();
				byte[] tempbytes = new byte[2048];
				int s = length / 2048;
				int y = length % 2048;
				int a = 1;
				int b = 0;
				
				
			//	base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
				while ((byteread = in.read(tempbytes)) != -1) {
					
					sta = false;
					
					String val = new String(tempbytes);

						if ((1 == a) && (2048 == byteread)) {
						b++;
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
								"START"+String.valueOf(length), "2048", tempbytes);
						
						System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
					
						if(!w.equals(str2)){
							record += "downloadFile START ret: " + new String(by) + "\n";
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
						record += "downloadFile number: " + new String(by)+"\n"+ "NUMBER:"+(i+1)+"\n\n";
						
					}else if (byteread<2048) {//锟斤拷锟揭伙拷锟�
						if(1 == a)
						{
//							Thread.sleep(100);
							byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
									"START"+String.valueOf(length), "0", null);
						}
						b++;
						String str = String.valueOf(byteread);
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
								"END", str, tempbytes);
						
						System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						if(!w.equals(str2)){
							record += "downloadFile END ret: " + new String(by) + "\n";
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
						record += "downloadFile number: " + new String(by)+"\n"+ "NUMBER:"+(i+1)+"\n\n";
					} else if((0 == y) && (s ==a)){//锟斤拷锟揭伙拷锟�
						b++;
						String str = String.valueOf(byteread);
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
								"END", str, tempbytes);
						
						System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						if(!w.equals(str2)){
							record += "downloadFile END ret: " + new String(by) + "\n";
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
						record += "downloadFile number: " + new String(by)+"\n"+ "NUMBER:"+(i+1)+"\n\n";
					}else {//锟叫硷拷
						b++;
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "APP",
								"LOADING", "2048", tempbytes);
						System.out.println(new String(by));
						String str1=new String(by);
						String str2="success";
						String w=str1.substring(0, 7);
						if(!w.equals(str2)){
							record += "downloadFile LOAD ret: " + new String(by) + "\n";
							Bundle bundle = new Bundle();
							bundle.putString("result", record);
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.setData(bundle);
							handler.sendMessage(msg);
							break;
						}
					}

					a++;
					step++;
					sta = true;
					Thread.sleep(30);
				}
				
				
					
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(record.getBytes());
					fos.flush();
					fos.close();
					Thread.sleep(30000);
			} catch (Exception exception) {
				exception.printStackTrace();
				Bundle bundle = new Bundle();
				bundle.putString("result",
						record + "Exception: " + exception.toString()
								+ " on step: " + step);
				Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.setData(bundle);
				handler.sendMessage(msg);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (Exception e1) {
					}
				}
			}
		}
	}
	
	
	private void downloadFile_FONT1() {
		String record = "";
		int step = 0;
		sequenceID++;
		FileInputStream in = null;
		try {
			int byteread = 0;
			File fileName = new File("mnt/sdcard/D180_Font_V1.00.bin");
			in = new FileInputStream(fileName);
			int length = in.available();
			byte[] tempbytes = new byte[2048];
			int s = length / 2048;
			int y = length % 2048;
			int a = 1;
			int b = 0;
			
		//	base.disableKeyPad(String.valueOf(sequenceID), "D180EMDK");
			while ((byteread = in.read(tempbytes)) != -1) {

				String val = new String(tempbytes);

					if ((1 == a) && (2048 == byteread)) {
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "FONT",
							"START"+String.valueOf(length), "2048", tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
				
					if(!w.equals(str2)){
						record += "downloadFile START ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile START ret: " + new String(by) + "\n";
					
				} else if (byteread<2048) {//锟斤拷锟揭伙拷锟�
					if(1 == a)
					{
//						Thread.sleep(100);
						byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "FONT",
								"START"+String.valueOf(length), "0", null);
					}
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "FONT",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile END ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile END ret: " + new String(by) + "\n";
					record += "downloadFile test end";
				} else if((0 == y) && (s ==a)){//锟斤拷锟揭伙拷锟�
					b++;
					String str = String.valueOf(byteread);
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "FONT",
							"END", str, tempbytes);
					
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile END ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
					record += "downloadFile END ret: " + new String(by) + "\n";
					record += "downloadFile test end";
				}else {//锟叫硷拷
					b++;
//					Thread.sleep(100);
					byte[] by = base.downloadFile(String.valueOf(sequenceID), "D180EMDK", "FONT",
							"LOADING", "2048", tempbytes);
					System.out.println(new String(by));
					String str1=new String(by);
					String str2="success";
					String w=str1.substring(0, 7);
					if(!w.equals(str2)){
						record += "downloadFile LOAD ret: " + new String(by) + "\n";
						Bundle bundle = new Bundle();
						bundle.putString("result", record);
						Message msg = handler.obtainMessage();
						msg.what = 1;
						msg.setData(bundle);
						handler.sendMessage(msg);
						break;
					}
				}

				a++;
				step++;

				Bundle bundle = new Bundle();
				bundle.putString("result", record);
				Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.setData(bundle);
				handler.sendMessage(msg);

			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Bundle bundle = new Bundle();
			bundle.putString("result",
					record + "Exception: " + exception.toString()
							+ " on step: " + step);
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	
	private void downloadKeyFUN1(String index)
	{
		String record = "";

		sequenceID++;
		int step = 0; 
		try{
			TextView txt=(TextView)findViewById(R.id.textViewBase);
			txt.setText("Generating RSA KEY,Please wait...");
			rsakeylength=2048;
			 SimpleDateFormat df1 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time1: " + df1.format(new Date())+"\n";
			keyMap = rsaUtil.genKeyPair(rsakeylength);
			 SimpleDateFormat df2 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time2: " + df2.format(new Date())+"\n";
			
			RSAPublicKey pubKey=(RSAPublicKey)keyMap.get("RSAPublicKey");
		
			String pubKeystring = pubKey.toString();
			
			String    sModulus=pubKey.getModulus().toString(16);
	        if ( (sModulus.length())%2 == 1) {
	        	sModulus = "0" + sModulus;
	         }
			String    sExponent=pubKey.getPublicExponent().toString(16);
	        if ( (sExponent.length())%2 == 1) {
	        	sExponent = "0" + sExponent;
	         }
			step++; 
			//写锟斤拷锟侥硷拷pubKey
			String pKey = "Modulus:" + sModulus +"        \n" + "Exponent:" + sExponent + "\n";
			File file = new File("mnt/sdcard/PubKey(2048).txt");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(pubKeystring.getBytes());
			fos.flush();
			fos.close();
			
			//写锟斤拷锟侥硷拷priKey	
			RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get("RSAPrivateKey");
			
			String privateKeystring = privateKey.toString();
			
			String pModulus = privateKey.getModulus().toString(16);
			String pPrivateExponent = privateKey.getPrivateExponent().toString(16);
			String priv = "pModulus:" + pModulus+ "           \n" + "pPrivateExponent:" + pPrivateExponent;
		//	String priv = privateKey.toString();
			File file1 = new File("mnt/sdcard/PriKey(2048).txt");
			FileOutputStream fos1 = new FileOutputStream(file1);
			fos1.write(privateKeystring.getBytes());
			fos1.flush();
			fos1.close();
			
			if(keyMap != null)
			{
				
				sequenceID++;
				byte[] by = base.downloadKey111(index);
			
		    record += "Download Response:" + new String(by) + '\n';
	
			step++; 
			}
			record += "downloadKey test end"; 

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
	
	private void downloadKeyFUN2()
	{
		String record = "";

		sequenceID++;
		int step = 0; 
		try{
			TextView txt=(TextView)findViewById(R.id.textViewBase);
			txt.setText("Generating RSA KEY,Please wait...");
			rsakeylength=2048;
			 SimpleDateFormat df1 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time1: " + df1.format(new Date())+"\n";
			keyMap = rsaUtil.genKeyPair(rsakeylength);
			 SimpleDateFormat df2 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time2: " + df2.format(new Date())+"\n";
			
			RSAPublicKey pubKey=(RSAPublicKey)keyMap.get("RSAPublicKey");
		
			String pubKeystring = pubKey.toString();
			
			String    sModulus=pubKey.getModulus().toString(16);
	        if ( (sModulus.length())%2 == 1) {
	        	sModulus = "0" + sModulus;
	         }
			String    sExponent=pubKey.getPublicExponent().toString(16);
	        if ( (sExponent.length())%2 == 1) {
	        	sExponent = "0" + sExponent;
	         }
			step++; 
			//写锟斤拷锟侥硷拷pubKey
			String pKey = "Modulus:" + sModulus +"        \n" + "Exponent:" + sExponent + "\n";
			File file = new File("mnt/sdcard/PubKey(2048).txt");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(pKey.getBytes());
			fos.flush();
			fos.close();
			
			//写锟斤拷锟侥硷拷priKey	
			RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get("RSAPrivateKey");
			
			String privateKeystring = privateKey.toString();
			
			String pModulus = privateKey.getModulus().toString(16);
			String pPrivateExponent = privateKey.getPrivateExponent().toString(16);
			String priv = "pModulus:" + pModulus+ "           \n" + "pPrivateExponent:" + pPrivateExponent;
		//	String priv = privateKey.toString();
			File file1 = new File("mnt/sdcard/PriKey(2048).txt");
			FileOutputStream fos1 = new FileOutputStream(file1);
			fos1.write(priv.getBytes());
			fos1.flush();
			fos1.close();
			
			if(keyMap != null)
			{
				
				sequenceID++;
				byte[] by = base.downloadKey(String.valueOf(sequenceID), "D180EMDK","3", pModulus,pPrivateExponent,"2","0","0","0","0","0");
			
		    record += "Download Response:" + new String(by) + '\n';
	
			step++; 
			}
			record += "downloadKey test end"; 

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
	
	private void downloadKeyFUN3()
	{
		String record = "";

		sequenceID++;
		int step = 0; 
		try{
			TextView txt=(TextView)findViewById(R.id.textViewBase);
			txt.setText("Generating RSA KEY,Please wait...");
			rsakeylength = 1024;
			 SimpleDateFormat df1 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time1: " + df1.format(new Date())+"\n";
			keyMap = rsaUtil.genKeyPair(rsakeylength);
			 SimpleDateFormat df2 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time2: " + df2.format(new Date())+"\n";
			RSAPublicKey pubKey=(RSAPublicKey)keyMap.get("RSAPublicKey");
			
			String pubKeystring = pubKey.toString();
			
			String    sModulus=pubKey.getModulus().toString(16);
	        if ( (sModulus.length())%2 == 1) {
	        	sModulus = "0" + sModulus;
	         }
			String    sExponent=pubKey.getPublicExponent().toString(16);
	        if ( (sExponent.length())%2 == 1) {
	        	sExponent = "0" + sExponent;
	         }
			step++; 
			//写锟斤拷锟侥硷拷pubKey
			String pKey = "Modulus:" + sModulus +"        \n" + "Exponent:" + sExponent + "\n";
			File file = new File("mnt/sdcard/PubKey(1024).txt");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(pubKeystring.getBytes());
			fos.flush();
			fos.close();
			
			//写锟斤拷锟侥硷拷priKey
			RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get("RSAPrivateKey");
			
			String pruvateKeystring = privateKey.toString();
			
			String pModulus = privateKey.getModulus().toString(16);
			String pPrivateExponent = privateKey.getPrivateExponent().toString(16);
			String priv = "pModulus:" + pModulus+ "           \n" + "pPrivateExponent:" + pPrivateExponent;
		//	String priv = privateKey.toString();
			File file1 = new File("mnt/sdcard/PriKey(1024).txt");
			FileOutputStream fos1 = new FileOutputStream(file1);
			fos1.write(pruvateKeystring.getBytes());
			fos1.flush();
			fos1.close();
			
			if(keyMap != null)
			{
				
				sequenceID++;
				byte[] by = base.downloadKey(String.valueOf(sequenceID), "D180EMDK","3", pModulus,pPrivateExponent,"3","0","0","0","0","0");
												
			
		    record += "Download Response:" + new String(by) + '\n';
	
			step++; 
			}
			record += "downloadKey test end"; 

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
	
	
	private void downloadKeyFUN4()
	{

		String record = "";
		sequenceID++;
		emv = EmvManager.getInstance(BaseActivity.this);
		int step = 0;
		
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505promptPIN API");
			byte[] by = base.downloadKey(String.valueOf(sequenceID), "D180EMDK","1", "16","11111111111111111111111111111111","6","0","3","1","4","82E13665");
//			byte[] by = base.downloadRSAKey(String.valueOf(sequenceID), "D180EMDK","1", "8","1111111111111111","6","2","3","1","82E13665");
			record += "downloadKey ret: " + new String(by) + "\n";
//			record += "downloadKey ret(track1 data): " + new String(by) + "\n";

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
	private void downloadKeyFUN5()
	{
		
		String record = "";
		sequenceID++;
		emv = EmvManager.getInstance(BaseActivity.this);
		int step = 0;
		
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505promptPIN API");
			byte[] by = base.downloadKey(String.valueOf(sequenceID), "D180EMDK","2", "16","11111111111111111111111111111111","7","0","3","1","4","82E13665");
//			byte[] by = base.downloadRSAKey(String.valueOf(sequenceID), "D180EMDK","2", "16","11111111111111111111111111111111","7","2","3","1","82E13665");
			record += "downloadKey ret: " + new String(by) + "\n";

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
	
	private void downloadKeyFUN6()
	{
		String record = "";

		sequenceID++;
		int step = 0; 
		try{
			TextView txt=(TextView)findViewById(R.id.textViewBase);
			txt.setText("Generating RSA KEY,Please wait...");
			rsakeylength=2048;
			 SimpleDateFormat df1 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time1: " + df1.format(new Date())+"\n";
			keyMap = rsaUtil.genKeyPair(rsakeylength);
			 SimpleDateFormat df2 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time2: " + df2.format(new Date())+"\n";
			
			RSAPublicKey pubKey=(RSAPublicKey)keyMap.get("RSAPublicKey");
		
			String pubKeystring = pubKey.toString();
			
			String    sModulus=pubKey.getModulus().toString(16);
			
	        if ( (sModulus.length())%2 == 1) {
	        	sModulus = "0" + sModulus;
	         }
			String    sExponent=pubKey.getPublicExponent().toString(16);
	        if ( (sExponent.length())%2 == 1) {
	        	sExponent = "0" + sExponent;
	         }
			step++; 
			//写锟斤拷锟侥硷拷pubKey
			String pKey = "Modulus:" + sModulus +"        \n" + "Exponent:" + sExponent + "\n";
			File file = new File("mnt/sdcard/PubKey(2048).txt");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(pubKeystring.getBytes());
			fos.flush();
			fos.close();
			
			//写锟斤拷锟侥硷拷priKey	
			RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get("RSAPrivateKey");
			
			String privateKeystring = privateKey.toString();
			
			String pModulus = privateKey.getModulus().toString(16);
			String pPrivateExponent = privateKey.getPrivateExponent().toString(16);
			String priv = "pModulus:" + pModulus+ "           \n" + "pPrivateExponent:" + pPrivateExponent;
		//	String priv = privateKey.toString();
			File file1 = new File("mnt/sdcard/PriKey(2048).txt");
			FileOutputStream fos1 = new FileOutputStream(file1);
			fos1.write(privateKeystring.getBytes());
			fos1.flush();
			fos1.close();
			
			if(keyMap != null)
			{
				
				sequenceID++;
				byte[] by = base.downloadKey(String.valueOf(sequenceID), "D180EMDK","3", sModulus,sExponent,"8","0","0","0","0","0");
//				byte[] by = base.downloadKey(String.valueOf(sequenceID), "D180EMDK","2", "16","11111111111111111111111111111111","7","0","3","1","4","82E13665");
			
		    record += "Download Response:" + new String(by) + '\n';
	
			step++; 
			}
			record += "downloadKey test end"; 

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
	
	private void downloadKeyFUN7()
	{
		String record = "";

		sequenceID++;
		int step = 0; 
		try{
			TextView txt=(TextView)findViewById(R.id.textViewBase);
			txt.setText("Generating RSA KEY,Please wait...");
			rsakeylength=1024;
			 SimpleDateFormat df1 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time1: " + df1.format(new Date())+"\n";
			keyMap = rsaUtil.genKeyPair(rsakeylength);
			 SimpleDateFormat df2 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time2: " + df2.format(new Date())+"\n";
			
			RSAPublicKey pubKey=(RSAPublicKey)keyMap.get("RSAPublicKey");
		
			String pubKeystring = pubKey.toString();
			
			String    sModulus=pubKey.getModulus().toString(16);
			
	        if ( (sModulus.length())%2 == 1) {
	        	sModulus = "0" + sModulus;
	         }
			String    sExponent=pubKey.getPublicExponent().toString(16);
	        if ( (sExponent.length())%2 == 1) {
	        	sExponent = "0" + sExponent;
	         }
			step++; 
			//写锟斤拷锟侥硷拷pubKey
			String pKey = "Modulus:" + sModulus +"        \n" + "Exponent:" + sExponent + "\n";
			File file = new File("mnt/sdcard/PubKey(1024).txt");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(pubKeystring.getBytes());
			fos.flush();
			fos.close();
			
			//写锟斤拷锟侥硷拷priKey	
			RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get("RSAPrivateKey");
			
			String privateKeystring = privateKey.toString();
			
			String pModulus = privateKey.getModulus().toString(16);
			String pPrivateExponent = privateKey.getPrivateExponent().toString(16);
			String priv = "pModulus:" + pModulus+ "           \n" + "pPrivateExponent:" + pPrivateExponent;
		//	String priv = privateKey.toString();
			File file1 = new File("mnt/sdcard/PriKey(1024).txt");
			FileOutputStream fos1 = new FileOutputStream(file1);
			fos1.write(privateKeystring.getBytes());
			fos1.flush();
			fos1.close();
			
			if(keyMap != null)
			{
				
				sequenceID++;
				byte[] by = base.downloadKey(String.valueOf(sequenceID), "D180EMDK","3", sModulus,sExponent,"9","0","0","0","0","0");
//				byte[] by = base.downloadKey(String.valueOf(sequenceID), "D180EMDK","2", "16","11111111111111111111111111111111","7","0","3","1","4","82E13665");
			
		    record += "Download Response:" + new String(by) + '\n';
	
			step++; 
			}
			record += "downloadKey test end"; 

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
	private void downloadKeyFUN8()
	{
		String record = "";

		sequenceID++;
		int step = 0; 
		try{
			TextView txt=(TextView)findViewById(R.id.textViewBase);
			txt.setText("Generating RSA KEY,Please wait...");
			rsakeylength=2048;
			 SimpleDateFormat df1 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time1: " + df1.format(new Date())+"\n";
			keyMap = rsaUtil.genKeyPair(rsakeylength);
			 SimpleDateFormat df2 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time2: " + df2.format(new Date())+"\n";
			
			RSAPublicKey pubKey=(RSAPublicKey)keyMap.get("RSAPublicKey");
		
			String pubKeystring = pubKey.toString();
			
			String    sModulus=pubKey.getModulus().toString(16);
			
	        if ( (sModulus.length())%2 == 1) {
	        	sModulus = "0" + sModulus;
	         }
			String    sExponent=pubKey.getPublicExponent().toString(16);
	        if ( (sExponent.length())%2 == 1) {
	        	sExponent = "0" + sExponent;
	         }
			step++; 
			//写锟斤拷锟侥硷拷pubKey
			String pKey = "Modulus:" + sModulus +"        \n" + "Exponent:" + sExponent + "\n";
			File file = new File("mnt/sdcard/PubKey(2048).txt");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(pubKeystring.getBytes());
			fos.flush();
			fos.close();
			
			//写锟斤拷锟侥硷拷priKey	
			RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get("RSAPrivateKey");
			
			String privateKeystring = privateKey.toString();
			
			String pModulus = privateKey.getModulus().toString(16);
			String pPrivateExponent = privateKey.getPrivateExponent().toString(16);
			String priv = "pModulus:" + pModulus+ "           \n" + "pPrivateExponent:" + pPrivateExponent;
		//	String priv = privateKey.toString();
			File file1 = new File("mnt/sdcard/PriKey(2048).txt");
			FileOutputStream fos1 = new FileOutputStream(file1);
			fos1.write(privateKeystring.getBytes());
			fos1.flush();
			fos1.close();
			
			if(keyMap != null)
			{
				
				sequenceID++;
				byte[] by = base.downloadKey(String.valueOf(sequenceID), "D180EMDK","3", sModulus,sExponent,"1","0","0","0","0","0");
//				byte[] by = base.downloadKey(String.valueOf(sequenceID), "D180EMDK","2", "16","11111111111111111111111111111111","7","0","3","1","4","82E13665");
			
		    record += "Download Response:" + new String(by) + '\n';
	
			step++; 
			}
			record += "downloadKey test end"; 

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
	
	private void downloadKeyFUN9()
	{
		String record = "";

		sequenceID++;
		int step = 0; 
		try{
			TextView txt=(TextView)findViewById(R.id.textViewBase);
			txt.setText("Generating RSA KEY,Please wait...");
			rsakeylength=1024;
			 SimpleDateFormat df1 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time1: " + df1.format(new Date())+"\n";
			keyMap = rsaUtil.genKeyPair(rsakeylength);
			 SimpleDateFormat df2 = new
					 SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//锟斤拷锟斤拷锟斤拷锟节革拷式
					 record += "Time2: " + df2.format(new Date())+"\n";
			
			RSAPublicKey pubKey=(RSAPublicKey)keyMap.get("RSAPublicKey");
		
			String pubKeystring = pubKey.toString();
			
			String    sModulus=pubKey.getModulus().toString(16);
			
	        if ( (sModulus.length())%2 == 1) {
	        	sModulus = "0" + sModulus;
	         }
			String    sExponent=pubKey.getPublicExponent().toString(16);
	        if ( (sExponent.length())%2 == 1) {
	        	sExponent = "0" + sExponent;
	         }
			step++; 
			//写锟斤拷锟侥硷拷pubKey
			String pKey = "Modulus:" + sModulus +"        \n" + "Exponent:" + sExponent + "\n";
			File file = new File("mnt/sdcard/PubKey(1024).txt");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(pubKeystring.getBytes());
			fos.flush();
			fos.close();
			
			//写锟斤拷锟侥硷拷priKey	
			RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get("RSAPrivateKey");
			
			String privateKeystring = privateKey.toString();
			
			String pModulus = privateKey.getModulus().toString(16);
			String pPrivateExponent = privateKey.getPrivateExponent().toString(16);
			String priv = "pModulus:" + pModulus+ "           \n" + "pPrivateExponent:" + pPrivateExponent;
		//	String priv = privateKey.toString();
			File file1 = new File("mnt/sdcard/PriKey(1024).txt");
			FileOutputStream fos1 = new FileOutputStream(file1);
			fos1.write(privateKeystring.getBytes());
			fos1.flush();
			fos1.close();
			
			if(keyMap != null)
			{
				
				sequenceID++;
				byte[] by = base.downloadKey(String.valueOf(sequenceID), "D180EMDK","3", sModulus,sExponent,"2","0","0","0","0","0");
//				byte[] by = base.downloadKey(String.valueOf(sequenceID), "D180EMDK","2", "16","11111111111111111111111111111111","7","0","3","1","4","82E13665");
			
		    record += "Download Response:" + new String(by) + '\n';
	
			step++; 
			}
			record += "downloadKey test end"; 

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
	
	
	
	private void getEncryptedData1(String index)
	{

		String record = "";
		sequenceID++;
		emv = EmvManager.getInstance(BaseActivity.this);
		int step = 0;
		
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505promptPIN API");
			byte[] by = base.getEncryptedData111(index);
			record += "getEncryptedData ret(track1 data): " + new String(by) + "\n";

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
	
	private void getEncryptedData2()
	{

		String record = "";
		sequenceID++;
		emv = EmvManager.getInstance(BaseActivity.this);
		int step = 0;
		
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505promptPIN API");
			byte[] by = base.getEncryptedData(String.valueOf(sequenceID), "D180EMDK", "1");
			record += "getEncryptedData ret(track2 data): " + new String(by) + "\n";

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
	private void getEncryptedData3()
	{

		String record = "";
		sequenceID++;
		emv = EmvManager.getInstance(BaseActivity.this);
		int step = 0;
		
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505promptPIN API");
			byte[] by = base.getEncryptedData(String.valueOf(sequenceID), "D180EMDK", "2");
			record += "getEncryptedData ret(track3 data): " + new String(by) + "\n";

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
	private void getEncryptedData4()
	{

		String record = "";
		sequenceID++;
		emv = EmvManager.getInstance(BaseActivity.this);
		int step = 0;
		
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505promptPIN API");
			byte[] by = base.getEncryptedData(String.valueOf(sequenceID), "D180EMDK", "3");
			record += "getEncryptedData ret(EMV tag data1): " + new String(by) + "\n";

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
	private void getEncryptedData5()
	{

		String record = "";
		sequenceID++;
		emv = EmvManager.getInstance(BaseActivity.this);
		int step = 0;
		
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505promptPIN API");
			byte[] by = base.getEncryptedData(String.valueOf(sequenceID), "D180EMDK", "4");
			record += "getEncryptedData ret(EMV tag data2): " + new String(by) + "\n";

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
	private void getEncryptedData6()
	{

		String record = "";
		sequenceID++;
		emv = EmvManager.getInstance(BaseActivity.this);
		int step = 0;
		
		try{
			//ui.scrCls();
			//ui.scrShowText("%P0505promptPIN API");
			byte[] by = base.getEncryptedData(String.valueOf(sequenceID), "D180EMDK", "5");
			record += "getEncryptedData ret(account number): " + new String(by) + "\n";


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
	
	
	private void syntheticTest1() throws Exception
	{
		TestOpenFUN1("open|1|APP2|0|D180EMDK");
		Thread.sleep(3000);
		TestsetSessionKeyFUN1("setSessionKey|1|D180EMDK");
		Thread.sleep(3000);
		TestdisableKeyPadFUN1("disableKeypad|1|D180EMDK");
		Thread.sleep(3000);
		TestenableKeypadFUN1("enableKeypad|1|D180EMDK|20000");
		Thread.sleep(3000);
		TestpromptMenuFUN1("promptMenu|1|D180EMDK|Prompt Menu|Please Select|sale|void|offline|REFUND|20000");
		Thread.sleep(3000);
		readCardDataAllFUN1("readCardData|1|D180EMDK|Sale|100.00|5.00|256|20000|$2000.00|Transaction...");
		Thread.sleep(3000);
		TestpromptPINFUN1("promptPIN|1|D180EMDK|"+maskP+"||4|8|Enter PIN|$1,000.00|$1.00|60000");
		Thread.sleep(3000);
		setEMVTagsFUN1("setEmvTags|1|D180EMDK|5A085413330089601075");
		Thread.sleep(3000);
		getEMVTagsFUN1("getEmvTags|1|D180EMDK|5A");
		Thread.sleep(3000);
		createMACFUN1("createMAC|1|D180EMDK|1A2B3C4D1A2B3C4D");
		Thread.sleep(3000);
		validateMACFUN1("validateMAC|1|D180EMDK|F87BCBE88883243D||||message1|message2|1A2B3C4D1A2B3C4D");
		Thread.sleep(3000);
		readCardDataAllFUN1("readCardData|1|D180EMDK|Sale|100.00|5.00|256|20000|$2000.00|Transaction...");
		Thread.sleep(3000);
		authorizeCardFUN1("authorizeCard|1|D180EMDK|999.00|5.00|0|true|true|true|true|91053A4B123C1E|71023B1C|72010A");
		Thread.sleep(3000);
		completeOnLineEMVFUN1("completeOnLineEMV|1|D180EMDK|0|true|");
		Thread.sleep(3000);
		promptAdditionalInfoFUN1("promptAdditionalInfo|1|D180EMDK|99.99|6228481090369874587|TRUE|TRUE|1.11|20000");
		Thread.sleep(3000);
		removeCardFUN1("removeCard|1|D180EMDK|EMV card|Please pull out the card...");
		Thread.sleep(3000);
		getBatteryLevelFUN1("getBatteryLevel|1|D180EMDK");
		Thread.sleep(3000);
		setLowBatteryThresholdFUN1("setLowBatteryTRhreshold|1|D180EMDK|1|The power is too low...");
		Thread.sleep(3000);
		getLowBatteryThresholdFUN1("getLowBatteryTRhreshold|1|D180EMDK");
		Thread.sleep(3000);
		setParameterFUN1("setParameter|1|D180EMDK|idleMsg=Welcome|sleepModeTimeout=60000|dataEncryptionType=4|maskFirstDigits=6");
		Thread.sleep(3000);
		getParameterFUN1("getParameter|1|D180EMDK|idleMsg||||");
		Thread.sleep(3000);
		TestCloseFUN1("close|1|D180EMDK");
	}
	
	private void syntheticTest2()
	{
		TestOpenFUN1("open|1|APP2|0|D180EMDK");
		TestsetSessionKeyFUN1("setSessionKey|1|D180EMDK");
		downloadKeyFUN2();
		readCardDataManualEntryFUN1("readCardData|1|D180EMDK|Sale|100.00|5.00|3|20000|$2000.00|Enter Number");
		readCardDataSwipeFUN1("readCardData|1|D180EMDK|Sale|100.00|5.00|0|20000|$2000.00|Swipe Card-->");
		readCardDataICFUN1("readCardData|1|D180EMDK|Sale|100.00|5.00|1|20000|$2000|Insert Card-->");
		TestCloseFUN1("close|1|D180EMDK");
	}
	
	private void getDateTime(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.getDateTime(index);
			record +="getDateTime ret:"+new String(by)+"\n";
			System.out.println(new String(by));
			step++;
			
			record += "getDateTime test end"; 

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
			//progressDialog.dismiss();
		}
	}
	
	
	private void setDateTime(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
		
			byte[] by = base.setDateTime(index);
			record +="setDateTime ret:"+new String(by)+"\n";
			System.out.println(new String(by));
			step++;
			
			record += "setDateTime test end"; 

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
			//progressDialog.dismiss();
		}
	}
	
	private void getStatusUpdate(String index)
	{
		String record = "";
		sequenceID++;
		int step = 0;
		try{
			step++;
			byte[] by = base.getStatusUpdate(index);
			record +="getStatusUpdate ret:"+new String(by)+"\n";
			System.out.println(new String(by));
			step++;
			
			record += "getStatusUpdate test end"; 

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
			//progressDialog.dismiss();
		}
	}
	
	//Test PIN  Ocean2015.10.13
//	private void pedGetPinBlock() 
//	{
//		String record = "";
//		sequenceID++;
//		int step = 0;
//		try{
//			step++;
//			byte[] by = base.getStatusUpdate(index);
//			record +="getStatusUpdate ret:"+new String(by)+"\n";
//			System.out.println(new String(by));
//			step++;
//			
//			record += "getStatusUpdate test end"; 
//
//			Bundle bundle = new Bundle();
//			bundle.putString("result", record);
//			Message msg = handler.obtainMessage();
//			msg.what = 1;
//			msg.setData(bundle);
//			handler.sendMessage(msg);
//		}catch(Exception exception){
//			exception.printStackTrace();
//			Bundle bundle = new Bundle();
//			bundle.putString("result", record + "Exception: " + exception.toString() + " on step: " + step);
//			Message msg = handler.obtainMessage();
//			msg.what = 2;
//			msg.setData(bundle);
//			handler.sendMessage(msg);
//		} finally {
//			//progressDialog.dismiss();
//		}
//	}
}



