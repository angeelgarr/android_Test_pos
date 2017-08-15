package com.pax.dxxtest;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.pax.mposapi.ConfigManager;
import com.pax.mposapi.KeyboardManager;
import com.pax.mposapi.UIManager;
import com.pax.mposapi.control.EMVPack;
import com.pax.mposapi.model.EMV_APPLIST;
import com.pax.mposapi.model.EMV_CAPK;
import com.pax.mposapi.model.EMV_PARAM;
import com.pax.mposapi.util.MyLog;

public class EmvParaActivity extends Activity {
	
	private String mInterface;
	private String mMethod;
	private String mNo;
	
	private UIManager ui;
	private KeyboardManager kbd;
	private TextView text;
	private ProgressDialog progressDialog;
	
	private final int NORMALRESULT = 1;
	private final int EXCEPTRESULT = 2;

	private EMVPack androidemv;
	private EMV_PARAM androidpara;
	private EMV_APPLIST androidapp;
	private EMV_CAPK androidcapk;
//	private static final String FilePath = "mnt/sdcard/EMVPARA";
	private static final String TAG = "Proto";
	private static final byte PART_MATCH = 0;
    
	
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
				EMVAddAIDParameterFUN1(idx);
				break;
			case 2:
				EMVGetAIDParameterFUN1(idx);
				break;
			case 3:
				EMVDeleteAIDParameterFUN1(idx);
				break;
			case 4:
				EMVAddCAPKFUN1(idx);
				break;	
			case 5:
				EMVGetCAPKFUN1(idx);
				break;
			case 6:
				EMVDelCAPKFUN1(idx);
				break;
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        
        Intent intent = getIntent();
        mInterface = intent.getStringExtra("Interface");
    //    mMethod = intent.getStringExtra("Method");
        mNo = intent.getStringExtra("No");
        
        TextView tv = (TextView)findViewById(R.id.base_title);
//        tv.setText(mInterface + "_" + mMethod + mNo);
        tv.setText(mInterface + "_FUN" + mNo);
        
	    ui = UIManager.getInstance(this);
	    kbd = KeyboardManager.getInstance(this);
	    
	    //init emv class
	    androidemv = new EMVPack();
	    androidpara = new EMV_PARAM();
	    androidapp = new EMV_APPLIST();
	    androidcapk = new EMV_CAPK();
	    
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
		progressDialog = new ProgressDialog(EmvParaActivity.this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Processing");
		progressDialog.setIndeterminate(true);
	//	progressDialog.show();
				
		new Thread(new Runnable(){
			public void run(){
				Looper.prepare();				
				if(mInterface.equals("EMVInitializeParameter"))
				{
					
						if(mNo.equals("1"))
						{
							EMVInitializeParameterFUN1();
						}
						if(mNo.equals("2"))
						{
							EMVInitializeParameterFUN2();
						}
						if(mNo.equals("3"))
						{
							EMVInitializeParameterFUN3();
						}
						if(mNo.equals("4"))
						{
							EMVInitializeParameterFUN4();
						}
						if(mNo.equals("5"))
						{
							EMVInitializeParameterFUN5();
						}
						if(mNo.equals("6"))
						{
							EMVInitializeParameterFUN6();
						}
						if(mNo.equals("7"))
						{
							EMVInitializeParameterFUN7();
						}
				
				}
				else if(mInterface.equals("EMVAddAIDParameter"))
				{
					
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "1");
							getIndex(1, val);
						}
						if(mNo.equals("2"))
						{
							EMVAddAIDParameterFUN2();
						}
						if(mNo.equals("3"))
						{
							EMVAddAIDParameterFUN3();
						}
						if(mNo.equals("4"))
						{
							EMVAddAIDParameterFUN4();
						}
						if(mNo.equals("5"))
						{
							EMVAddAIDParameterFUN5();
						}
						if(mNo.equals("6"))
						{
							EMVAddAIDParameterFUN6();
						}
						if(mNo.equals("7"))
						{
							EMVAddAIDParameterFUN7();
						}
				
				}
				else if(mInterface.equals("EMVGetTotalAIDNumber"))
				{
					
						if(mNo.equals("1"))
						{
							EMVGetTotalAIDNumberFUN1();
						}
						if(mNo.equals("2"))
						{
							EMVGetTotalAIDNumberFUN2();
						}
						if(mNo.equals("3"))
						{
							EMVGetTotalAIDNumberFUN3();
						}
						if(mNo.equals("4"))
						{
							EMVGetTotalAIDNumberFUN4();
						}
						if(mNo.equals("5"))
						{
							EMVGetTotalAIDNumberFUN5();
						}
					
				}
				else if(mInterface.equals("EMVGetAIDParameter"))
				{
				
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "1");
							getIndex(2, val);
						}
						if(mNo.equals("2"))
						{
							EMVGetAIDParameterFUN2();
						}
						if(mNo.equals("3"))
						{
							EMVGetAIDParameterFUN3();
						}
					
				}
				else if(mInterface.equals("EMVDeleteAIDParameter"))
				{
				
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "1");
							getIndex(3, val);
						}
						if(mNo.equals("2"))
						{
							EMVDeleteAIDParameterFUN2();
						}
						if(mNo.equals("3"))
						{
							EMVDeleteAIDParameterFUN3();
						}
						if(mNo.equals("4"))
						{
							EMVDeleteAIDParameterFUN4();
						}
				}
				else if(mInterface.equals("EMVSetParameter"))
				{
						if(mNo.equals("1"))
						{
							EMVSetParameterFUN1();
						}
						if(mNo.equals("2"))
						{
							EMVSetParameterFUN2();
						}
						if(mNo.equals("3"))
						{
							EMVSetParameterFUN3();
						}
				}
				else if(mInterface.equals("EMVGetParameter"))
				{
						if(mNo.equals("1"))
						{
							EMVGetParameterFUN1();
						}
						if(mNo.equals("2"))
						{
							EMVGetParameterFUN2();
						}
				}
				else if(mInterface.equals("EMVAddCAPK"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "1");
							getIndex(4, val);
						}
						if(mNo.equals("2"))
						{
							EMVAddCAPKFUN2();
						}
						if(mNo.equals("3"))
						{
							EMVAddCAPKFUN3();
						}
						if(mNo.equals("4"))
						{
							EMVAddCAPKFUN4();
						}
						if(mNo.equals("5"))
						{
							EMVAddCAPKFUN5();
						}
				}
				else if(mInterface.equals("EMVGetTotalCAPKNumber"))
				{
						if(mNo.equals("1"))
						{
							EMVGetTotalCAPKNumberFUN1();
						}
						if(mNo.equals("2"))
						{
							EMVGetTotalCAPKNumberFUN2();
						}
						if(mNo.equals("3"))
						{
							EMVGetTotalCAPKNumberFUN3();
						}
						if(mNo.equals("4"))
						{
							EMVGetTotalCAPKNumberFUN4();
						}
						if(mNo.equals("5"))
						{
							EMVGetTotalCAPKNumberFUN5();
						}
				}
				else if(mInterface.equals("EMVGetCAPK"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "1");
							getIndex(5, val);
						}
						if(mNo.equals("2"))
						{
							EMVGetCAPKFUN2();
						}
				}
				
				else if(mInterface.equals("EMVDelCAPK"))
				{
						if(mNo.equals("1"))
						{
							ConfigManager cfg = ConfigManager.getInstance(getApplicationContext());
							String val = cfg.getValueByTag(mInterface, "1");
							getIndex(6, val);
						}
						if(mNo.equals("2"))
						{
							EMVDelCAPKFUN2();
						}
						if(mNo.equals("3"))
						{
							EMVDelCAPKFUN3();
						}
						
				}
			}
		}).start();
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
		  return false;
	}	
	
	private void EMVInitializeParameterFUN1() {
		
//		String record = "";
//		File file = this.getApplicationContext().getFilesDir();
//		String fileName = file.getAbsolutePath();
//		fileName += "/EMVPARA";
//		Log.e("EMV", "file name: " + fileName);

		String record = "";
		String FilePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/EMVPARA"; 
		int i = androidemv.EMVInitializeParameter(FilePath);
		
//		int i = androidemv.EMVInitializeParameter(fileName);
		
		record = "Initialize:"+String.valueOf(i);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	private void EMVInitializeParameterFUN2() {
		
		String record = "";
		String FilePath = "C/EMVPARA";
		int i = androidemv.EMVInitializeParameter(FilePath);
		
		
		record = "Initialize:"+String.valueOf(i);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	private void EMVInitializeParameterFUN3() {
		
		String record = "";
		String FilePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/EMVPARA"; 
		int i = androidemv.EMVInitializeParameter(FilePath);
		
		
		record = "Initialize:"+String.valueOf(i);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	private void EMVInitializeParameterFUN4() {
		
		String record = "";
		String FilePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/TestEMVPARA.txt";
		int i = androidemv.EMVInitializeParameter(FilePath);
		
		
		record = "Initialize:"+String.valueOf(i);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	private void EMVInitializeParameterFUN5() {
		
		String record = "";
		String FilePath = getApplicationContext().getFilesDir().getAbsolutePath() +"/TestEMVPARA";
		int i = androidemv.EMVInitializeParameter(FilePath);
		
		record = "Initialize:"+String.valueOf(i);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	private void EMVInitializeParameterFUN6() {
		
		String record = "";
		String FilePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/EMVPARA"; 
		int i = androidemv.EMVInitializeParameter(FilePath);
		
		
		record = "Initialize:"+String.valueOf(i);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	private void EMVInitializeParameterFUN7() {
		
		String record = "";
		String FilePath = "";
		int i = androidemv.EMVInitializeParameter(FilePath);
		
		
		record = "Initialize:"+String.valueOf(i);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	
//	private void EMVAddAIDParameterFUN1(String index) {
//		
//		byte[] appName = new byte[] {0x01, 0x02};
//		System.arraycopy(appName, 0, androidapp.AppName, 0, appName.length);
//		
//		int i = Integer.parseInt(index);
//		int[] iAid = new int[] {0xA0, 0x00, 0x00, 0x00, 0x25, 0x01};
//		iAid[5] = iAid[5]+i;
//		byte[] bAid = new byte[iAid.length];
//		util.ConvIntA2ByteA(iAid, bAid);
//		System.arraycopy(bAid, 0, androidapp.AID, 0, bAid.length);
//		
//		byte aidLen = 6;
//		androidapp.AidLen = aidLen;
//		
//		byte selFlag = 0;
//		androidapp.SelFlag = selFlag;
//		
//		byte priority = 0;
//		androidapp.Priority = priority;
//		
//		byte targetPer = 0;
//		androidapp.TargetPer = targetPer;
//		
//		byte maxTargetPer = 0;
//		androidapp.MaxTargetPer = maxTargetPer;
//		
//		byte floorLimitCheck = 1;
//		androidapp.FloorLimitCheck = floorLimitCheck;
//		
//		byte randTransSel = 1;
//		androidapp.RandTransSel = randTransSel;
//		
//		byte velocityCheck = 1;
//		androidapp.VelocityCheck = velocityCheck;
//		
//		int floorLimit = 5000;
//		androidapp.FloorLimit = floorLimit;
//		
//		int threshold = 0;
//		androidapp.Threshold = threshold;
//		
//		int[] iTACDenial = new int[]{0x00, 0x10, 0x00, 0x00, 0x00};
//		byte[] bTACDenial = new byte[iTACDenial.length];
//		util.ConvIntA2ByteA(iTACDenial, bTACDenial);
//		System.arraycopy(bTACDenial, 0, androidapp.TACDenial, 0, bTACDenial.length);
//		
//		int[] iTACOnline = new int[]{0xD8, 0x40, 0x04, 0xF8, 0x00};
//		byte[] bTACOnline = new byte[iTACOnline.length];
//		util.ConvIntA2ByteA(iTACOnline, bTACOnline);
//		System.arraycopy(bTACOnline, 0, androidapp.TACOnline, 0, bTACOnline.length);
//		
//		int[] iTACDefault = new int[]{0xD8, 0x40, 0x00, 0xA8, 0x00};
//		byte[] bTACDefault = new byte[iTACDefault.length];
//		util.ConvIntA2ByteA(iTACDefault, bTACDefault);
//		System.arraycopy(bTACDefault, 0, androidapp.TACDefault, 0, bTACDefault.length);
//		
//		int[] iAcquierId = new int[]{0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
//		byte[] bAcquierId = new byte[iAcquierId.length];
//		util.ConvIntA2ByteA(iAcquierId, bAcquierId);
//		System.arraycopy(bAcquierId, 0, androidapp.AcquierId, 0, bAcquierId.length);
//		
//		int[] idDOL = new int[]{0x03, 0x9F, 0x37, 0x04};
//		byte[] bdDOL = new byte[idDOL.length];
//		util.ConvIntA2ByteA(idDOL, bdDOL);
//		System.arraycopy(bdDOL, 0, androidapp.dDOL, 0, bdDOL.length);
//		
//		int[] itDOL = new int[]{0x0F, 0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02, 0x9A, 0x03, 0x9C, 0x01, 0x95, 0x05, 0x9F, 0x37, 0x04};
//		byte[] btDOL = new byte[itDOL.length];
//		util.ConvIntA2ByteA(itDOL, btDOL);
//		System.arraycopy(btDOL, 0, androidapp.tDOL, 0, btDOL.length);
//		
//		int[] iversion = new int[]{0x00, 0x8C};
//		byte[] bversion = new byte[iversion.length];
//		util.ConvIntA2ByteA(iversion, bversion);
//		System.arraycopy(bversion, 0, androidapp.Version, 0, bversion.length);
//		
//		byte[] riskManData = new byte[]{};
//		System.arraycopy(riskManData, 0, androidapp.RiskManData, 0, riskManData.length);
//		
//		int iRet = androidemv.EMVAddAIDParameter(androidapp);
//		String record = "APP ADD: " + String.valueOf(iRet);
//		
//		Bundle bundle = new Bundle();
//		bundle.putString("result", record);
//		Message msg = handler.obtainMessage();
//		msg.what = 1;
//		msg.setData(bundle);
//		handler.sendMessage(msg);
//	}
	
private void EMVAddAIDParameterFUN1(String index) {
		
		byte[] appName = new byte[] {};
		System.arraycopy(appName, 0, androidapp.AppName, 0, appName.length);
		
		
		int[] iAid = new int[] {0xA0, 0x00, 0x00, 0x00, 0x03, 0x10,0x10};

		try {
			int i = Integer.parseInt(index);
			iAid[5] = iAid[6]+i;
			byte[] bAid = new byte[iAid.length];
			util.ConvIntA2ByteA(iAid, bAid);
			System.arraycopy(bAid, 0, androidapp.AID, 0, bAid.length);
			
			byte aidLen = 7;
			androidapp.AidLen = aidLen;
			
			byte selFlag = PART_MATCH;
			androidapp.SelFlag = selFlag;
			
			byte priority = 0;
			androidapp.Priority = priority;
			
			byte targetPer = 0;
			androidapp.TargetPer = targetPer;
			
			byte maxTargetPer = 0;
			androidapp.MaxTargetPer = maxTargetPer;
			
			byte floorLimitCheck = 1;
			androidapp.FloorLimitCheck = floorLimitCheck;
			
			byte randTransSel = 1;
			androidapp.RandTransSel = randTransSel;
			
			byte velocityCheck = 1;
			androidapp.VelocityCheck = velocityCheck;
			
			int floorLimit = 20000;
			androidapp.FloorLimit = floorLimit;
			
			int threshold = 0;
			androidapp.Threshold = threshold;
			
			int[] iTACDenial = new int[]{0x00, 0x10, 0x00, 0x00, 0x00};
			byte[] bTACDenial = new byte[iTACDenial.length];
			util.ConvIntA2ByteA(iTACDenial, bTACDenial);
			System.arraycopy(bTACDenial, 0, androidapp.TACDenial, 0, bTACDenial.length);
			
			int[] iTACOnline = new int[]{0xD8, 0x40, 0x04, 0xF8, 0x00};
			byte[] bTACOnline = new byte[iTACOnline.length];
			util.ConvIntA2ByteA(iTACOnline, bTACOnline);
			System.arraycopy(bTACOnline, 0, androidapp.TACOnline, 0, bTACOnline.length);
			
			int[] iTACDefault = new int[]{0xD8, 0x40, 0x00, 0xA8, 0x00};
			byte[] bTACDefault = new byte[iTACDefault.length];
			util.ConvIntA2ByteA(iTACDefault, bTACDefault);
			System.arraycopy(bTACDefault, 0, androidapp.TACDefault, 0, bTACDefault.length);
			
			int[] iAcquierId = new int[]{0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
			byte[] bAcquierId = new byte[iAcquierId.length];
			util.ConvIntA2ByteA(iAcquierId, bAcquierId);
			System.arraycopy(bAcquierId, 0, androidapp.AcquierId, 0, bAcquierId.length);
			
			int[] idDOL = new int[]{0x03, 0x9F, 0x37, 0x04};
			byte[] bdDOL = new byte[idDOL.length];
			util.ConvIntA2ByteA(idDOL, bdDOL);
			System.arraycopy(bdDOL, 0, androidapp.dDOL, 0, bdDOL.length);
			
			int[] itDOL = new int[]{0x0F, 0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02, 0x9A, 0x03, 0x9C, 0x01, 0x95, 0x05, 0x9F, 0x37, 0x04};
			byte[] btDOL = new byte[itDOL.length];
			util.ConvIntA2ByteA(itDOL, btDOL);
			System.arraycopy(btDOL, 0, androidapp.tDOL, 0, btDOL.length);
			
			int[] iversion = new int[]{0x00, 0x8C};
			byte[] bversion = new byte[iversion.length];
			util.ConvIntA2ByteA(iversion, bversion);
			System.arraycopy(bversion, 0, androidapp.Version, 0, bversion.length);
			
			byte[] riskManData = new byte[]{};
			System.arraycopy(riskManData, 0, androidapp.RiskManData, 0, riskManData.length);
			
			int iRet = androidemv.EMVAddAIDParameter(androidapp);
			String record = "APP ADD: " + String.valueOf(iRet);
			
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception e) {
			
			Bundle bundle = new Bundle();
			bundle.putString("result", "Please enter the number");
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
		
		
		
	}
	
//	private void EMVAddAIDParameterFUN2() {
//		
//		byte[] appName = new byte[] {};
//		System.arraycopy(appName, 0, androidapp.AppName, 0, appName.length);
//		
//		int[] iAid = new int[] {0xA0, 0x00, 0x00, 0x00, 0x25, 0x01};
//		byte[] bAid = new byte[iAid.length];
//		util.ConvIntA2ByteA(iAid, bAid);
//		System.arraycopy(bAid, 0, androidapp.AID, 0, bAid.length);
//		
//		byte aidLen = 0;
//		androidapp.AidLen = aidLen;
//		
//		byte selFlag = 0;
//		androidapp.SelFlag = selFlag;
//		
//		byte priority = 0;
//		androidapp.Priority = priority;
//		
//		byte targetPer = 0;
//		androidapp.TargetPer = targetPer;
//		
//		byte maxTargetPer = 0;
//		androidapp.MaxTargetPer = maxTargetPer;
//		
//		byte floorLimitCheck = 1;
//		androidapp.FloorLimitCheck = floorLimitCheck;
//		
//		byte randTransSel = 1;
//		androidapp.RandTransSel = randTransSel;
//		
//		byte velocityCheck = 1;
//		androidapp.VelocityCheck = velocityCheck;
//		
//		int floorLimit = 5000;
//		androidapp.FloorLimit = floorLimit;
//		
//		int threshold = 0;
//		androidapp.Threshold = threshold;
//		
//		int[] iTACDenial = new int[]{0x00, 0x10, 0x00, 0x00, 0x00};
//		byte[] bTACDenial = new byte[iTACDenial.length];
//		util.ConvIntA2ByteA(iTACDenial, bTACDenial);
//		System.arraycopy(bTACDenial, 0, androidapp.TACDenial, 0, bTACDenial.length);
//		
//		int[] iTACOnline = new int[]{0xD8, 0x40, 0x04, 0xF8, 0x00};
//		byte[] bTACOnline = new byte[iTACOnline.length];
//		util.ConvIntA2ByteA(iTACOnline, bTACOnline);
//		System.arraycopy(bTACOnline, 0, androidapp.TACOnline, 0, bTACOnline.length);
//		
//		int[] iTACDefault = new int[]{0xD8, 0x40, 0x00, 0xA8, 0x00};
//		byte[] bTACDefault = new byte[iTACDefault.length];
//		util.ConvIntA2ByteA(iTACDefault, bTACDefault);
//		System.arraycopy(bTACDefault, 0, androidapp.TACDefault, 0, bTACDefault.length);
//		
//		int[] iAcquierId = new int[]{0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
//		byte[] bAcquierId = new byte[iAcquierId.length];
//		util.ConvIntA2ByteA(iAcquierId, bAcquierId);
//		System.arraycopy(bAcquierId, 0, androidapp.AcquierId, 0, bAcquierId.length);
//		
//		int[] idDOL = new int[]{0x03, 0x9F, 0x37, 0x04};
//		byte[] bdDOL = new byte[idDOL.length];
//		util.ConvIntA2ByteA(idDOL, bdDOL);
//		System.arraycopy(bdDOL, 0, androidapp.dDOL, 0, bdDOL.length);
//		
//		int[] itDOL = new int[]{0x0F, 0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02, 0x9A, 0x03, 0x9C, 0x01, 0x95, 0x05, 0x9F, 0x37, 0x04};
//		byte[] btDOL = new byte[itDOL.length];
//		util.ConvIntA2ByteA(itDOL, btDOL);
//		System.arraycopy(btDOL, 0, androidapp.tDOL, 0, btDOL.length);
//		
//		int[] iversion = new int[]{0x00, 0x8C};
//		byte[] bversion = new byte[iversion.length];
//		util.ConvIntA2ByteA(iversion, bversion);
//		System.arraycopy(bversion, 0, androidapp.Version, 0, bversion.length);
//		
//		byte[] riskManData = new byte[]{};
//		System.arraycopy(riskManData, 0, androidapp.RiskManData, 0, riskManData.length);
//		
//		int iRet = androidemv.EMVAddAIDParameter(androidapp);
//		String record = "APP ADD: " + String.valueOf(iRet);
//		
//		Bundle bundle = new Bundle();
//		bundle.putString("result", record);
//		Message msg = handler.obtainMessage();
//		msg.what = 1;
//		msg.setData(bundle);
//		handler.sendMessage(msg);
//	}


private void EMVAddAIDParameterFUN2() {
	
	byte[] appName = new byte[] {};
	System.arraycopy(appName, 0, androidapp.AppName, 0, appName.length);
	
	int[] iAid = new int[] {0xA0, 0x00, 0x00, 0x00, 0x04, 0x10,0x10};
	byte[] bAid = new byte[iAid.length];
	util.ConvIntA2ByteA(iAid, bAid);
	System.arraycopy(bAid, 0, androidapp.AID, 0, bAid.length);
	
	
	byte aidLen = 7;
	androidapp.AidLen = aidLen;
	
	byte selFlag = PART_MATCH;
	androidapp.SelFlag = selFlag;
	
	byte priority = 10;
	androidapp.Priority = priority;
	
	byte targetPer = 0;
	androidapp.TargetPer = targetPer;
	
	byte maxTargetPer = 0;
	androidapp.MaxTargetPer = maxTargetPer;
	
	byte floorLimitCheck = 1;
	androidapp.FloorLimitCheck = floorLimitCheck;
	
	byte randTransSel = 1;
	androidapp.RandTransSel = randTransSel;
	
	byte velocityCheck = 1;
	androidapp.VelocityCheck = velocityCheck;
	
	int floorLimit = 0;
	androidapp.FloorLimit = floorLimit;
	
	int threshold = 0;
	androidapp.Threshold = threshold;
	
	int[] iTACDenial = new int[]{0x04, 0x00, 0x00, 0x00, 0x00};
	byte[] bTACDenial = new byte[iTACDenial.length];
	util.ConvIntA2ByteA(iTACDenial, bTACDenial);
	System.arraycopy(bTACDenial, 0, androidapp.TACDenial, 0, bTACDenial.length);
	
	int[] iTACOnline = new int[]{0xF8, 0x50, 0xAC, 0xF8, 0x00};
	byte[] bTACOnline = new byte[iTACOnline.length];
	util.ConvIntA2ByteA(iTACOnline, bTACOnline);
	System.arraycopy(bTACOnline, 0, androidapp.TACOnline, 0, bTACOnline.length);
	
	int[] iTACDefault = new int[]{0xFC, 0x50, 0xAC, 0xA0, 0x00};
	byte[] bTACDefault = new byte[iTACDefault.length];
	util.ConvIntA2ByteA(iTACDefault, bTACDefault);
	System.arraycopy(bTACDefault, 0, androidapp.TACDefault, 0, bTACDefault.length);
	
	int[] iAcquierId = new int[]{0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
	byte[] bAcquierId = new byte[iAcquierId.length];
	util.ConvIntA2ByteA(iAcquierId, bAcquierId);
	System.arraycopy(bAcquierId, 0, androidapp.AcquierId, 0, bAcquierId.length);
	
	int[] idDOL = new int[]{0x03, 0x9F, 0x37, 0x04};
	byte[] bdDOL = new byte[idDOL.length];
	util.ConvIntA2ByteA(idDOL, bdDOL);
	System.arraycopy(bdDOL, 0, androidapp.dDOL, 0, bdDOL.length);
	
	int[] itDOL = new int[]{0x0F, 0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02, 0x9A, 0x03, 0x9C, 0x01, 0x95, 0x05, 0x9F, 0x37, 0x04};
	byte[] btDOL = new byte[itDOL.length];
	util.ConvIntA2ByteA(itDOL, btDOL);
	System.arraycopy(btDOL, 0, androidapp.tDOL, 0, btDOL.length);
	
	int[] iversion = new int[]{0x00, 0x02};
	byte[] bversion = new byte[iversion.length];
	util.ConvIntA2ByteA(iversion, bversion);
	System.arraycopy(bversion, 0, androidapp.Version, 0, bversion.length);
	
	byte[] riskManData = new byte[]{};
	System.arraycopy(riskManData, 0, androidapp.RiskManData, 0, riskManData.length);
	
	
	
	int iRet = androidemv.EMVAddAIDParameter(androidapp);
	
	
	String record = "APP ADD: " + String.valueOf(iRet);
	
	Bundle bundle = new Bundle();
	bundle.putString("result", record);
	Message msg = handler.obtainMessage();
	msg.what = 1;
	msg.setData(bundle);
	handler.sendMessage(msg);
}
	
	private void EMVAddAIDParameterFUN3() {
		String record="";
		
		for(int i = 0; i<110; i++)
		{
			byte[] appName = new byte[] {};
			System.arraycopy(appName, 0, androidapp.AppName, 0, appName.length);
			
			int[] iAid = new int[] {0xA0, 0x00, 0x00, 0x00, 0x25, 0x01};
			iAid[5] = iAid[5]+i;
			byte[] bAid = new byte[iAid.length];
			util.ConvIntA2ByteA(iAid, bAid);
			System.arraycopy(bAid, 0, androidapp.AID, 0, bAid.length);
			
			byte aidLen = 6;
			androidapp.AidLen = aidLen;
			
			byte selFlag = 0;
			androidapp.SelFlag = selFlag;
			
			byte priority = 0;
			androidapp.Priority = priority;
			
			byte targetPer = 0;
			androidapp.TargetPer = targetPer;
			
			byte maxTargetPer = 0;
			androidapp.MaxTargetPer = maxTargetPer;
			
			byte floorLimitCheck = 1;
			androidapp.FloorLimitCheck = floorLimitCheck;
			
			byte randTransSel = 1;
			androidapp.RandTransSel = randTransSel;
			
			byte velocityCheck = 1;
			androidapp.VelocityCheck = velocityCheck;
			
			int floorLimit = 5000;
			androidapp.FloorLimit = floorLimit;
			
			int threshold = 0;
			androidapp.Threshold = threshold;
			
			int[] iTACDenial = new int[]{0x00, 0x10, 0x00, 0x00, 0x00};
			byte[] bTACDenial = new byte[iTACDenial.length];
			util.ConvIntA2ByteA(iTACDenial, bTACDenial);
			System.arraycopy(bTACDenial, 0, androidapp.TACDenial, 0, bTACDenial.length);
			
			int[] iTACOnline = new int[]{0xD8, 0x40, 0x04, 0xF8, 0x00};
			byte[] bTACOnline = new byte[iTACOnline.length];
			util.ConvIntA2ByteA(iTACOnline, bTACOnline);
			System.arraycopy(bTACOnline, 0, androidapp.TACOnline, 0, bTACOnline.length);
			
			int[] iTACDefault = new int[]{0xD8, 0x40, 0x00, 0xA8, 0x00};
			byte[] bTACDefault = new byte[iTACDefault.length];
			util.ConvIntA2ByteA(iTACDefault, bTACDefault);
			System.arraycopy(bTACDefault, 0, androidapp.TACDefault, 0, bTACDefault.length);
			
			int[] iAcquierId = new int[]{0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
			byte[] bAcquierId = new byte[iAcquierId.length];
			util.ConvIntA2ByteA(iAcquierId, bAcquierId);
			System.arraycopy(bAcquierId, 0, androidapp.AcquierId, 0, bAcquierId.length);
			
			int[] idDOL = new int[]{0x03, 0x9F, 0x37, 0x04};
			byte[] bdDOL = new byte[idDOL.length];
			util.ConvIntA2ByteA(idDOL, bdDOL);
			System.arraycopy(bdDOL, 0, androidapp.dDOL, 0, bdDOL.length);
			
			int[] itDOL = new int[]{0x0F, 0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02, 0x9A, 0x03, 0x9C, 0x01, 0x95, 0x05, 0x9F, 0x37, 0x04};
			byte[] btDOL = new byte[itDOL.length];
			util.ConvIntA2ByteA(itDOL, btDOL);
			System.arraycopy(btDOL, 0, androidapp.tDOL, 0, btDOL.length);
			
			int[] iversion = new int[]{0x00, 0x8C};
			byte[] bversion = new byte[iversion.length];
			util.ConvIntA2ByteA(iversion, bversion);
			System.arraycopy(bversion, 0, androidapp.Version, 0, bversion.length);
			
			byte[] riskManData = new byte[]{};
			System.arraycopy(riskManData, 0, androidapp.RiskManData, 0, riskManData.length);
			
			int iRet = androidemv.EMVAddAIDParameter(androidapp);
			record += "APP ADD "+(i+1)+":" + String.valueOf(iRet)+"\n";
			
		}
	
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	
	private void EMVAddAIDParameterFUN4() {
		
		byte[] appName = new byte[] {};
		System.arraycopy(appName, 0, androidapp.AppName, 0, appName.length);
		
		int[] iAid = new int[] {0xA0, 0x00, 0x00, 0x00, 0x25, 0x01};
		byte[] bAid = new byte[iAid.length];
		util.ConvIntA2ByteA(iAid, bAid);
		System.arraycopy(bAid, 0, androidapp.AID, 0, bAid.length);
		
		byte aidLen = 3;
		androidapp.AidLen = aidLen;
		
		byte selFlag = 0;
		androidapp.SelFlag = selFlag;
		
		byte priority = 0;
		androidapp.Priority = priority;
		
		byte targetPer = 0;
		androidapp.TargetPer = targetPer;
		
		byte maxTargetPer = 0;
		androidapp.MaxTargetPer = maxTargetPer;
		
		byte floorLimitCheck = 1;
		androidapp.FloorLimitCheck = floorLimitCheck;
		
		byte randTransSel = 1;
		androidapp.RandTransSel = randTransSel;
		
		byte velocityCheck = 1;
		androidapp.VelocityCheck = velocityCheck;
		
		int floorLimit = 5000;
		androidapp.FloorLimit = floorLimit;
		
		int threshold = 0;
		androidapp.Threshold = threshold;
		
		int[] iTACDenial = new int[]{0x00, 0x10, 0x00, 0x00, 0x00};
		byte[] bTACDenial = new byte[iTACDenial.length];
		util.ConvIntA2ByteA(iTACDenial, bTACDenial);
		System.arraycopy(bTACDenial, 0, androidapp.TACDenial, 0, bTACDenial.length);
		
		int[] iTACOnline = new int[]{0xD8, 0x40, 0x04, 0xF8, 0x00};
		byte[] bTACOnline = new byte[iTACOnline.length];
		util.ConvIntA2ByteA(iTACOnline, bTACOnline);
		System.arraycopy(bTACOnline, 0, androidapp.TACOnline, 0, bTACOnline.length);
		
		int[] iTACDefault = new int[]{0xD8, 0x40, 0x00, 0xA8, 0x00};
		byte[] bTACDefault = new byte[iTACDefault.length];
		util.ConvIntA2ByteA(iTACDefault, bTACDefault);
		System.arraycopy(bTACDefault, 0, androidapp.TACDefault, 0, bTACDefault.length);
		
		int[] iAcquierId = new int[]{0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
		byte[] bAcquierId = new byte[iAcquierId.length];
		util.ConvIntA2ByteA(iAcquierId, bAcquierId);
		System.arraycopy(bAcquierId, 0, androidapp.AcquierId, 0, bAcquierId.length);
		
		int[] idDOL = new int[]{0x03, 0x9F, 0x37, 0x04};
		byte[] bdDOL = new byte[idDOL.length];
		util.ConvIntA2ByteA(idDOL, bdDOL);
		System.arraycopy(bdDOL, 0, androidapp.dDOL, 0, bdDOL.length);
		
		int[] itDOL = new int[]{0x0F, 0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02, 0x9A, 0x03, 0x9C, 0x01, 0x95, 0x05, 0x9F, 0x37, 0x04};
		byte[] btDOL = new byte[itDOL.length];
		util.ConvIntA2ByteA(itDOL, btDOL);
		System.arraycopy(btDOL, 0, androidapp.tDOL, 0, btDOL.length);
		
		int[] iversion = new int[]{0x00, 0x8C};
		byte[] bversion = new byte[iversion.length];
		util.ConvIntA2ByteA(iversion, bversion);
		System.arraycopy(bversion, 0, androidapp.Version, 0, bversion.length);
		
		byte[] riskManData = new byte[]{};
		System.arraycopy(riskManData, 0, androidapp.RiskManData, 0, riskManData.length);
		
		int iRet = androidemv.EMVAddAIDParameter(androidapp);
		String record = "APP ADD: " + String.valueOf(iRet);
		
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	private void EMVAddAIDParameterFUN5() {
		
		byte[] appName = new byte[] {};
		System.arraycopy(appName, 0, androidapp.AppName, 0, appName.length);
		
		int[] iAid = new int[] {0xA0, 0x00, 0x00, 0x00, 0x25, 0x03};
		byte[] bAid = new byte[iAid.length];
		util.ConvIntA2ByteA(iAid, bAid);
		System.arraycopy(bAid, 0, androidapp.AID, 0, bAid.length);
		
		byte aidLen = 6;
		androidapp.AidLen = aidLen;
		
		byte selFlag = 0;
		androidapp.SelFlag = selFlag;
		
		byte priority = 0;
		androidapp.Priority = priority;
		
		byte targetPer = 0;
		androidapp.TargetPer = targetPer;
		
		byte maxTargetPer = 0;
		androidapp.MaxTargetPer = maxTargetPer;
		
		byte floorLimitCheck = 1;
		androidapp.FloorLimitCheck = floorLimitCheck;
		
		byte randTransSel = 1;
		androidapp.RandTransSel = randTransSel;
		
		byte velocityCheck = 1;
		androidapp.VelocityCheck = velocityCheck;
		
		int floorLimit = 5000;
		androidapp.FloorLimit = floorLimit;
		
		int threshold = 0;
		androidapp.Threshold = threshold;
		
		int[] iTACDenial = new int[]{0x00, 0x10, 0x00, 0x00, 0x00};
		byte[] bTACDenial = new byte[iTACDenial.length];
		util.ConvIntA2ByteA(iTACDenial, bTACDenial);
		System.arraycopy(bTACDenial, 0, androidapp.TACDenial, 0, bTACDenial.length);
		
		int[] iTACOnline = new int[]{0xD8, 0x40, 0x04, 0xF8, 0x00};
		byte[] bTACOnline = new byte[iTACOnline.length];
		util.ConvIntA2ByteA(iTACOnline, bTACOnline);
		System.arraycopy(bTACOnline, 0, androidapp.TACOnline, 0, bTACOnline.length);
		
		int[] iTACDefault = new int[]{0xD8, 0x40, 0x00, 0xA8, 0x00};
		byte[] bTACDefault = new byte[iTACDefault.length];
		util.ConvIntA2ByteA(iTACDefault, bTACDefault);
		System.arraycopy(bTACDefault, 0, androidapp.TACDefault, 0, bTACDefault.length);
		
		int[] iAcquierId = new int[]{0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
		byte[] bAcquierId = new byte[iAcquierId.length];
		util.ConvIntA2ByteA(iAcquierId, bAcquierId);
		System.arraycopy(bAcquierId, 0, androidapp.AcquierId, 0, bAcquierId.length);
		
		int[] idDOL = new int[]{0x03, 0x9F, 0x37, 0x04};
		byte[] bdDOL = new byte[idDOL.length];
		util.ConvIntA2ByteA(idDOL, bdDOL);
		System.arraycopy(bdDOL, 0, androidapp.dDOL, 0, bdDOL.length);
		
		int[] itDOL = new int[]{0x0F, 0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02, 0x9A, 0x03, 0x9C, 0x01, 0x95, 0x05, 0x9F, 0x37, 0x04};
		byte[] btDOL = new byte[itDOL.length];
		util.ConvIntA2ByteA(itDOL, btDOL);
		System.arraycopy(btDOL, 0, androidapp.tDOL, 0, btDOL.length);
		
		int[] iversion = new int[]{0x00, 0x8C};
		byte[] bversion = new byte[iversion.length];
		util.ConvIntA2ByteA(iversion, bversion);
		System.arraycopy(bversion, 0, androidapp.Version, 0, bversion.length);
		
		byte[] riskManData = new byte[]{};
		System.arraycopy(riskManData, 0, androidapp.RiskManData, 0, riskManData.length);
		
		int iRet = androidemv.EMVAddAIDParameter(androidapp);
		String record = "APP ADD: " + String.valueOf(iRet);
		
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	private void EMVAddAIDParameterFUN6() {
		
		byte[] appName = new byte[] {};
		System.arraycopy(appName, 0, androidapp.AppName, 0, appName.length);
		
		int[] iAid = new int[] {0xA0, 0x00, 0x00, 0x00, 0x25, 0x03};
		byte[] bAid = new byte[iAid.length];
		util.ConvIntA2ByteA(iAid, bAid);
		System.arraycopy(bAid, 0, androidapp.AID, 0, bAid.length);
		
		byte aidLen = 6;
		androidapp.AidLen = aidLen;
		
		byte selFlag = 0;
		androidapp.SelFlag = selFlag;
		
		byte priority = 0;
		androidapp.Priority = priority;
		
		byte targetPer = 0;
		androidapp.TargetPer = targetPer;
		
		byte maxTargetPer = 0;
		androidapp.MaxTargetPer = maxTargetPer;
		
		byte floorLimitCheck = 1;
		androidapp.FloorLimitCheck = floorLimitCheck;
		
		byte randTransSel = 1;
		androidapp.RandTransSel = randTransSel;
		
		byte velocityCheck = 1;
		androidapp.VelocityCheck = velocityCheck;
		
		int floorLimit = 5000;
		androidapp.FloorLimit = floorLimit;
		
		int threshold = 0;
		androidapp.Threshold = threshold;
		
		int[] iTACDenial = new int[]{0x00, 0x10, 0x00, 0x00, 0x00};
		byte[] bTACDenial = new byte[iTACDenial.length];
		util.ConvIntA2ByteA(iTACDenial, bTACDenial);
		System.arraycopy(bTACDenial, 0, androidapp.TACDenial, 0, bTACDenial.length);
		
		int[] iTACOnline = new int[]{0xD8, 0x40, 0x04, 0xF8, 0x00};
		byte[] bTACOnline = new byte[iTACOnline.length];
		util.ConvIntA2ByteA(iTACOnline, bTACOnline);
		System.arraycopy(bTACOnline, 0, androidapp.TACOnline, 0, bTACOnline.length);
		
		int[] iTACDefault = new int[]{0xD8, 0x40, 0x00, 0xA8, 0x00};
		byte[] bTACDefault = new byte[iTACDefault.length];
		util.ConvIntA2ByteA(iTACDefault, bTACDefault);
		System.arraycopy(bTACDefault, 0, androidapp.TACDefault, 0, bTACDefault.length);
		
		int[] iAcquierId = new int[]{0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
		byte[] bAcquierId = new byte[iAcquierId.length];
		util.ConvIntA2ByteA(iAcquierId, bAcquierId);
		System.arraycopy(bAcquierId, 0, androidapp.AcquierId, 0, bAcquierId.length);
		
		int[] idDOL = new int[]{0x03, 0x9F, 0x37, 0x04};
		byte[] bdDOL = new byte[idDOL.length];
		util.ConvIntA2ByteA(idDOL, bdDOL);
		System.arraycopy(bdDOL, 0, androidapp.dDOL, 0, bdDOL.length);
		
		int[] itDOL = new int[]{0x0F, 0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02, 0x9A, 0x03, 0x9C, 0x01, 0x95, 0x05, 0x9F, 0x37, 0x04};
		byte[] btDOL = new byte[itDOL.length];
		util.ConvIntA2ByteA(itDOL, btDOL);
		System.arraycopy(btDOL, 0, androidapp.tDOL, 0, btDOL.length);
		
		int[] iversion = new int[]{0x00, 0x8C};
		byte[] bversion = new byte[iversion.length];
		util.ConvIntA2ByteA(iversion, bversion);
		System.arraycopy(bversion, 0, androidapp.Version, 0, bversion.length);
		
		byte[] riskManData = new byte[]{};
		System.arraycopy(riskManData, 0, androidapp.RiskManData, 0, riskManData.length);
		
		int iRet = androidemv.EMVAddAIDParameter(androidapp);
		String record = "APP ADD: " + String.valueOf(iRet);
		
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	private void EMVAddAIDParameterFUN7() {
		String record = "";
		for(int i = 0; i<50; i++)
		{
			byte[] appName = new byte[] {};
			System.arraycopy(appName, 0, androidapp.AppName, 0, appName.length);
			
			int[] iAid = new int[] {0xA0, 0x00, 0x00, 0x00, 0x25, 0x01};
			byte[] bAid = new byte[iAid.length];
			util.ConvIntA2ByteA(iAid, bAid);
			System.arraycopy(bAid, 0, androidapp.AID, 0, bAid.length);
			
			byte aidLen = 6;
			androidapp.AidLen = aidLen;
			
			byte selFlag = 0;
			androidapp.SelFlag = selFlag;
			
			byte priority = 0;
			androidapp.Priority = priority;
			
			byte targetPer = 0;
			androidapp.TargetPer = targetPer;
			
			byte maxTargetPer = 0;
			androidapp.MaxTargetPer = maxTargetPer;
			
			byte floorLimitCheck = 1;
			androidapp.FloorLimitCheck = floorLimitCheck;
			
			byte randTransSel = 1;
			androidapp.RandTransSel = randTransSel;
			
			byte velocityCheck = 1;
			androidapp.VelocityCheck = velocityCheck;
			
			int floorLimit = 5000;
			androidapp.FloorLimit = floorLimit;
			
			int threshold = i;
			androidapp.Threshold = threshold;
			
			int[] iTACDenial = new int[]{0x00, 0x10, 0x00, 0x00, 0x00};
			byte[] bTACDenial = new byte[iTACDenial.length];
			util.ConvIntA2ByteA(iTACDenial, bTACDenial);
			System.arraycopy(bTACDenial, 0, androidapp.TACDenial, 0, bTACDenial.length);
			
			int[] iTACOnline = new int[]{0xD8, 0x40, 0x04, 0xF8, 0x00};
			byte[] bTACOnline = new byte[iTACOnline.length];
			util.ConvIntA2ByteA(iTACOnline, bTACOnline);
			System.arraycopy(bTACOnline, 0, androidapp.TACOnline, 0, bTACOnline.length);
			
			int[] iTACDefault = new int[]{0xD8, 0x40, 0x00, 0xA8, 0x00};
			byte[] bTACDefault = new byte[iTACDefault.length];
			util.ConvIntA2ByteA(iTACDefault, bTACDefault);
			System.arraycopy(bTACDefault, 0, androidapp.TACDefault, 0, bTACDefault.length);
			
			int[] iAcquierId = new int[]{0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
			byte[] bAcquierId = new byte[iAcquierId.length];
			util.ConvIntA2ByteA(iAcquierId, bAcquierId);
			System.arraycopy(bAcquierId, 0, androidapp.AcquierId, 0, bAcquierId.length);
			
			int[] idDOL = new int[]{0x03, 0x9F, 0x37, 0x04};
			byte[] bdDOL = new byte[idDOL.length];
			util.ConvIntA2ByteA(idDOL, bdDOL);
			System.arraycopy(bdDOL, 0, androidapp.dDOL, 0, bdDOL.length);
			
			int[] itDOL = new int[]{0x0F, 0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02, 0x9A, 0x03, 0x9C, 0x01, 0x95, 0x05, 0x9F, 0x37, 0x04};
			byte[] btDOL = new byte[itDOL.length];
			util.ConvIntA2ByteA(itDOL, btDOL);
			System.arraycopy(btDOL, 0, androidapp.tDOL, 0, btDOL.length);
			
			int[] iversion = new int[]{0x00, 0x8C};
			byte[] bversion = new byte[iversion.length];
			util.ConvIntA2ByteA(iversion, bversion);
			System.arraycopy(bversion, 0, androidapp.Version, 0, bversion.length);
			
			byte[] riskManData = new byte[]{};
			System.arraycopy(riskManData, 0, androidapp.RiskManData, 0, riskManData.length);
				
			int iRet = androidemv.EMVAddAIDParameter(androidapp);
			record += "APP ADD" +(i+1)+":"+ String.valueOf(iRet)+"\n";
		}
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	private void EMVGetTotalAIDNumberFUN1() {
		
		EMVPack androidemv1 = new EMVPack();
		
		String record = "";
		
		int[] TotalAIDNumber = new int[1];
		int iRet = androidemv1.EMVGetTotalAIDNumber(TotalAIDNumber);
		
		record = "iRet:" + String.valueOf(iRet)+"\n TotalAIDNumber:" + TotalAIDNumber[0];
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	private void EMVGetTotalAIDNumberFUN2() {
		
		String record = "";
		
		int[] TotalAIDNumber = new int[1];
		int iRet = androidemv.EMVGetTotalAIDNumber(TotalAIDNumber);
		
		record = "iRet:" + String.valueOf(iRet)+"\n TotalAIDNumber:" + TotalAIDNumber[0];
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	private void EMVGetTotalAIDNumberFUN3() {
		
		String record = "";
		
		int[] TotalAIDNumber = new int[1];
		int iRet = androidemv.EMVGetTotalAIDNumber(TotalAIDNumber);
		
		record = "iRet:" + String.valueOf(iRet)+"\n TotalAIDNumber:" + TotalAIDNumber[0];
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	private void EMVGetTotalAIDNumberFUN4() {
		
		String record = "";
		
		int[] TotalAIDNumber = new int[1];
		int iRet = androidemv.EMVGetTotalAIDNumber(TotalAIDNumber);
		
		record = "iRet:" + String.valueOf(iRet)+"\n TotalAIDNumber:" + TotalAIDNumber[0];
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	private void EMVGetTotalAIDNumberFUN5() {
		
		String record = "";
		
		int[] TotalAIDNumber = new int[1];
		int iRet = androidemv.EMVGetTotalAIDNumber(TotalAIDNumber);
		
		record = "iRet:" + String.valueOf(iRet)+"\n TotalAIDNumber:" + TotalAIDNumber[0];
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	public String bcd2Str(byte[] bytes) {  
        char temp[] = new char[bytes.length * 2], val;  
  
        for (int i = 0; i < bytes.length; i++) {  
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);  
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
  
            val = (char) (bytes[i] & 0x0f);  
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
        }  
        return new String(temp);  
    }
	
	private void EMVGetAIDParameterFUN1(String index) {
		
		EMVPack androidemv1 = new EMVPack();
		EMV_APPLIST androidapp1 = new EMV_APPLIST();
		
		String record = "";
		int AidNo = Integer.parseInt(index);
		int rec = androidemv1.EMVGetAIDParameter(AidNo, androidapp1);
		
		//鑾峰彇鍊�
		byte[] appName = new byte[androidapp1.AppName.length];
		System.arraycopy(androidapp1.AppName, 0, appName, 0, appName.length);
	
		record = record +"appName:"+bcd2Str(appName)+"\n";
		
		byte[] bAid = new byte[androidapp1.AID.length];
		System.arraycopy(androidapp1.AID, 0, bAid, 0, bAid.length);
	
		record = record +"bAid:"+bcd2Str(bAid)+"\n";
		
		byte aidLen = 0;
		aidLen = androidapp1.AidLen;
		record = record +"aidLen:"+aidLen+"\n";
		
		byte selFlag = 0;
		selFlag = androidapp1.SelFlag;
		record = record +"selFlag:"+selFlag+"\n";
		
		byte priority = 0;
		priority = androidapp1.Priority;
		record = record +"priority:"+priority+"\n";
		
		byte targetPer = 0;
		targetPer = androidapp1.TargetPer;
		record = record +"targetPer:"+targetPer+"\n";
		
		byte maxTargetPer = 0;
		maxTargetPer = androidapp1.MaxTargetPer;
		record = record +"maxTargetPer:"+maxTargetPer+"\n";
		
		byte floorLimitCheck = 0;
		floorLimitCheck = androidapp1.FloorLimitCheck;
		record = record +"floorLimitCheck:"+floorLimitCheck+"\n";
		
		byte randTransSel = 0;
		randTransSel = androidapp1.RandTransSel;
		record = record +"randTransSel:"+randTransSel+"\n";
		
		byte velocityCheck = 0;
		velocityCheck = androidapp1.VelocityCheck;
		record = record +"velocityCheck:"+velocityCheck+"\n";
		
		int floorLimit = 0;
		floorLimit = androidapp1.FloorLimit;
		record = record +"floorLimit:"+floorLimit+"\n";
		
		int threshold = 0;
		threshold = androidapp1.Threshold;
		record = record +"threshold:"+threshold+"\n";
		
		byte[] bTACDenial = new byte[androidapp1.TACDenial.length];
		System.arraycopy(androidapp1.TACDenial, 0, bTACDenial, 0, bTACDenial.length);
		record = record +"bTACDenial:"+bcd2Str(bTACDenial)+"\n";
		
		byte[] bTACOnline = new byte[androidapp1.TACOnline.length];
		System.arraycopy(androidapp1.TACOnline, 0, bTACOnline, 0, bTACOnline.length);
		record = record +"bTACOnline:"+bcd2Str(bTACOnline)+"\n";
		
		byte[] bTACDefault = new byte[androidapp1.TACDefault.length];
		System.arraycopy(androidapp1.TACDefault, 0, bTACDefault, 0, bTACDefault.length);
		record = record +"bTACDefault:"+bcd2Str(bTACDefault)+"\n";
		
		byte[] bAcquierId = new byte[androidapp1.AcquierId.length];
		System.arraycopy(androidapp1.AcquierId, 0, bAcquierId, 0, bAcquierId.length);
		record = record +"bAcquierId:"+bcd2Str(bAcquierId)+"\n";
		
		byte[] bdDOL = new byte[androidapp1.dDOL.length];
		System.arraycopy(androidapp1.dDOL, 0, bdDOL, 0, bdDOL.length);
		record = record +"bdDOL:"+bcd2Str(bdDOL)+"\n";
		
		byte[] btDOL = new byte[androidapp1.tDOL.length];
		System.arraycopy(androidapp1.tDOL, 0, btDOL, 0, btDOL.length);
		record = record +"btDOL:"+bcd2Str(btDOL)+"\n";
		
		byte[] bversion = new byte[androidapp1.Version.length];
		System.arraycopy(androidapp1.Version, 0, bversion, 0, bversion.length);
		
		record = record +"bversion:"+bcd2Str(bversion)+"\n";
		
		byte[] riskManData = new byte[androidapp1.RiskManData.length];
		System.arraycopy(androidapp1.RiskManData, 0, riskManData, 0, riskManData.length);
		record = record +"riskManData:"+bcd2Str(riskManData)+"\n";
		
		MyLog.i(TAG, "recv success");
		MyLog.i(TAG, "<<<< Recv Data: " + record);
		
		
		record = record+"GetAIDParameter:" + String.valueOf(rec);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	
	private void EMVGetAIDParameterFUN2() {
		
		String record = "";
		int rec = androidemv.EMVGetAIDParameter(1000, androidapp);
		
		record = "GetAIDParameter:" + String.valueOf(rec);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	private void EMVGetAIDParameterFUN3() {
		
		String record = "";
		int rec = androidemv.EMVGetAIDParameter(1, androidapp);
		
		record = "GetAIDParameter:" + String.valueOf(rec);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	
	
	private void EMVDeleteAIDParameterFUN1(String index) {
		
		String record = "";
		int AidNo = Integer.parseInt(index);
		int rec = androidemv.EMVDeleteAIDParameter(AidNo);
		
		record = "DeleteAIDParameter:"+String.valueOf(rec);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	private void EMVDeleteAIDParameterFUN2() {
		
		String record = "";
		int AidNo = 1000;
		int rec = androidemv.EMVDeleteAIDParameter(AidNo);
		
		record = "DeleteAIDParameter:"+String.valueOf(rec);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	private void EMVDeleteAIDParameterFUN3() {
		
		String record = "";
		int AidNo = 0;
		int rec = androidemv.EMVDeleteAIDParameter(AidNo);
		
		record = "DeleteAIDParameter:"+String.valueOf(rec);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	private void EMVDeleteAIDParameterFUN4() {
		
		for(int i = 0; i<50; i++)
		{
			byte[] appName = new byte[] {};
			System.arraycopy(appName, 0, androidapp.AppName, 0, appName.length);
			
			int[] iAid = new int[] {0xA0, 0x00, 0x00, 0x00, 0x25, 0x01};
			byte[] bAid = new byte[iAid.length];
			util.ConvIntA2ByteA(iAid, bAid);
			System.arraycopy(bAid, 0, androidapp.AID, 0, bAid.length);
			
			byte aidLen = 6;
			androidapp.AidLen = aidLen;
			
			byte selFlag = 0;
			androidapp.SelFlag = selFlag;
			
			byte priority = 0;
			androidapp.Priority = priority;
			
			byte targetPer = 0;
			androidapp.TargetPer = targetPer;
			
			byte maxTargetPer = 0;
			androidapp.MaxTargetPer = maxTargetPer;
			
			byte floorLimitCheck = 1;
			androidapp.FloorLimitCheck = floorLimitCheck;
			
			byte randTransSel = 1;
			androidapp.RandTransSel = randTransSel;
			
			byte velocityCheck = 1;
			androidapp.VelocityCheck = velocityCheck;
			
			int floorLimit = 5000;
			androidapp.FloorLimit = floorLimit;
			
			int threshold = i;
			androidapp.Threshold = threshold;
			
			int[] iTACDenial = new int[]{0x00, 0x10, 0x00, 0x00, 0x00};
			byte[] bTACDenial = new byte[iTACDenial.length];
			util.ConvIntA2ByteA(iTACDenial, bTACDenial);
			System.arraycopy(bTACDenial, 0, androidapp.TACDenial, 0, bTACDenial.length);
			
			int[] iTACOnline = new int[]{0xD8, 0x40, 0x04, 0xF8, 0x00};
			byte[] bTACOnline = new byte[iTACOnline.length];
			util.ConvIntA2ByteA(iTACOnline, bTACOnline);
			System.arraycopy(bTACOnline, 0, androidapp.TACOnline, 0, bTACOnline.length);
			
			int[] iTACDefault = new int[]{0xD8, 0x40, 0x00, 0xA8, 0x00};
			byte[] bTACDefault = new byte[iTACDefault.length];
			util.ConvIntA2ByteA(iTACDefault, bTACDefault);
			System.arraycopy(bTACDefault, 0, androidapp.TACDefault, 0, bTACDefault.length);
			
			int[] iAcquierId = new int[]{0x00, 0x00, 0x00, 0x12, 0x34, 0x56};
			byte[] bAcquierId = new byte[iAcquierId.length];
			util.ConvIntA2ByteA(iAcquierId, bAcquierId);
			System.arraycopy(bAcquierId, 0, androidapp.AcquierId, 0, bAcquierId.length);
			
			int[] idDOL = new int[]{0x03, 0x9F, 0x37, 0x04};
			byte[] bdDOL = new byte[idDOL.length];
			util.ConvIntA2ByteA(idDOL, bdDOL);
			System.arraycopy(bdDOL, 0, androidapp.dDOL, 0, bdDOL.length);
			
			int[] itDOL = new int[]{0x0F, 0x9F, 0x02, 0x06, 0x5F, 0x2A, 0x02, 0x9A, 0x03, 0x9C, 0x01, 0x95, 0x05, 0x9F, 0x37, 0x04};
			byte[] btDOL = new byte[itDOL.length];
			util.ConvIntA2ByteA(itDOL, btDOL);
			System.arraycopy(btDOL, 0, androidapp.tDOL, 0, btDOL.length);
			
			int[] iversion = new int[]{0x00, 0x8C};
			byte[] bversion = new byte[iversion.length];
			util.ConvIntA2ByteA(iversion, bversion);
			System.arraycopy(bversion, 0, androidapp.Version, 0, bversion.length);
			
			byte[] riskManData = new byte[]{};
			System.arraycopy(riskManData, 0, androidapp.RiskManData, 0, riskManData.length);
				
			int iRet = androidemv.EMVAddAIDParameter(androidapp);
			String record = "APP ADD: " + String.valueOf(iRet);
				
			int AidNo = 1;
			int rec = androidemv.EMVDeleteAIDParameter(AidNo);
			
			record = "DeleteAIDParameter:"+String.valueOf(rec)+"/n";
			
			Bundle bundle = new Bundle();
			bundle.putString("result", record);
			Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.setData(bundle);
			handler.sendMessage(msg);
			
		}
	}
	
	
	private void EMVSetParameterFUN1() {
		
		byte[] bMerchName = "LINING".getBytes();
		System.arraycopy(bMerchName, 0, androidpara.MerchName, 0, bMerchName.length);
		
		int[] iMerchCateCode = new int[]{0x00, 0x00};
		byte[] bMerchCateCode = new byte[iMerchCateCode.length];
		util.ConvIntA2ByteA(iMerchCateCode, bMerchCateCode);
		System.arraycopy(bMerchCateCode, 0, androidpara.MerchCateCode, 0, bMerchCateCode.length);
		
		byte[] MerchId = "12345".getBytes();
		System.arraycopy(MerchId, 0, androidpara.MerchId, 0, MerchId.length);
		
		byte[] bTermId = "12345".getBytes();
		System.arraycopy(bTermId, 0, androidpara.TermId, 0, bTermId.length);
		
		byte TerminalType = 0x22;
		androidpara.TerminalType = TerminalType;
		
		int[] iCapability = new int[]{0xE0, 0xf1, 0xc8};
		byte[] bCapability = new byte[iCapability.length];
		util.ConvIntA2ByteA(iCapability, bCapability);
		System.arraycopy(bCapability, 0, androidpara.Capability, 0, bCapability.length);
		
		int[] iExCapability = new int[]{0xE0, 0x00, 0xF0, 0xA0, 0x01};
		byte[] bExCapability = new byte[iExCapability.length];
		util.ConvIntA2ByteA(iExCapability, bExCapability);
		System.arraycopy(bExCapability, 0, androidpara.ExCapability, 0, bExCapability.length);
		
		byte TransCurrExp = 0x02;
		androidpara.TransCurrExp = TransCurrExp;
		
		byte ReferCurrExp = 0x02;
		androidpara.ReferCurrExp = ReferCurrExp;
		
		int[] iReferCurrCode = new int[]{0x08, 0x40};
		byte[] bReferCurrCode = new byte[iReferCurrCode.length];
		util.ConvIntA2ByteA(iReferCurrCode, bReferCurrCode);
		System.arraycopy(bReferCurrCode, 0, androidpara.ReferCurrCode, 0, bReferCurrCode.length);
		
		byte[] bCountryCode = bReferCurrCode;
		System.arraycopy(bCountryCode, 0, androidpara.CountryCode, 0, bCountryCode.length);
		
		byte[] bTransCurrCode = bReferCurrCode;
		System.arraycopy(bTransCurrCode, 0, androidpara.TransCurrCode, 0, bTransCurrCode.length);
		
		int ReferCurrCon = 1000;
		androidpara.ReferCurrCon = ReferCurrCon;
		
		byte TransType = 0x02;
		androidpara.TransType = TransType;
		
		byte ForceOnline = 0;
		androidpara.ForceOnline = ForceOnline;
		
		byte GetDataPIN = 3;
		androidpara.GetDataPIN = GetDataPIN;
		
		byte SurportPSESel = 1;
		androidpara.SurportPSESel = SurportPSESel;
	
		int iRet = androidemv.EMVSetParameter(androidpara);
		String record = "EMV PARAM ADD: " + String.valueOf(iRet);
		
		record = String.valueOf(record);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	
	private void EMVSetParameterFUN2() {
		
		byte[] bMerchName = "LINING".getBytes();
		System.arraycopy(bMerchName, 0, androidpara.MerchName, 0, bMerchName.length);
		
		int[] iMerchCateCode = new int[]{0x00, 0x00};
		byte[] bMerchCateCode = new byte[iMerchCateCode.length];
		util.ConvIntA2ByteA(iMerchCateCode, bMerchCateCode);
		System.arraycopy(bMerchCateCode, 0, androidpara.MerchCateCode, 0, bMerchCateCode.length);
		
		byte[] MerchId = "12345".getBytes();
		System.arraycopy(MerchId, 0, androidpara.MerchId, 0, MerchId.length);
		
		byte[] bTermId = "12345".getBytes();
		System.arraycopy(bTermId, 0, androidpara.TermId, 0, bTermId.length);
		
		byte TerminalType = 0x22;
		androidpara.TerminalType = TerminalType;
		
		int[] iCapability = new int[]{0xE0, 0xf1, 0xc8};
		byte[] bCapability = new byte[iCapability.length];
		util.ConvIntA2ByteA(iCapability, bCapability);
		System.arraycopy(bCapability, 0, androidpara.Capability, 0, bCapability.length);
		
		int[] iExCapability = new int[]{0xE0, 0x00, 0xF0, 0xA0, 0x01};
		byte[] bExCapability = new byte[iExCapability.length];
		util.ConvIntA2ByteA(iExCapability, bExCapability);
		System.arraycopy(bExCapability, 0, androidpara.ExCapability, 0, bExCapability.length);
		
		byte TransCurrExp = 0x02;
		androidpara.TransCurrExp = TransCurrExp;
		
		byte ReferCurrExp = 0x02;
		androidpara.ReferCurrExp = ReferCurrExp;
		
		int[] iReferCurrCode = new int[]{0x08, 0x40};
		byte[] bReferCurrCode = new byte[iReferCurrCode.length];
		util.ConvIntA2ByteA(iReferCurrCode, bReferCurrCode);
		System.arraycopy(bReferCurrCode, 0, androidpara.ReferCurrCode, 0, bReferCurrCode.length);
		
		byte[] bCountryCode = bReferCurrCode;
		System.arraycopy(bCountryCode, 0, androidpara.CountryCode, 0, bCountryCode.length);
		
		byte[] bTransCurrCode = bReferCurrCode;
		System.arraycopy(bTransCurrCode, 0, androidpara.TransCurrCode, 0, bTransCurrCode.length);
		
		int ReferCurrCon = 1000;
		androidpara.ReferCurrCon = ReferCurrCon;
		
		byte TransType = 0x02;
		androidpara.TransType = TransType;
		
		byte ForceOnline = 0;
		androidpara.ForceOnline = ForceOnline;
		
		byte GetDataPIN = 3;
		androidpara.GetDataPIN = GetDataPIN;
		
		byte SurportPSESel = 1;
		androidpara.SurportPSESel = SurportPSESel;
	
		int iRet = androidemv.EMVSetParameter(androidpara);
		String record = "EMV PARAM ADD: " + String.valueOf(iRet);
		
		record = String.valueOf(record);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	private void EMVSetParameterFUN3() {
		
		byte[] bMerchName = "LINING".getBytes();
		System.arraycopy(bMerchName, 0, androidpara.MerchName, 0, bMerchName.length);
		
		int[] iMerchCateCode = new int[]{0x00, 0x00};
		byte[] bMerchCateCode = new byte[iMerchCateCode.length];
		util.ConvIntA2ByteA(iMerchCateCode, bMerchCateCode);
		System.arraycopy(bMerchCateCode, 0, androidpara.MerchCateCode, 0, bMerchCateCode.length);
		
		byte[] MerchId = "12345".getBytes();
		System.arraycopy(MerchId, 0, androidpara.MerchId, 0, MerchId.length);
		
		byte[] bTermId = "12345".getBytes();
		System.arraycopy(bTermId, 0, androidpara.TermId, 0, bTermId.length);
		
		byte TerminalType = 0x22;
		androidpara.TerminalType = TerminalType;
		
		int[] iCapability = new int[]{0xE0, 0xf1, 0xc8};
		byte[] bCapability = new byte[iCapability.length];
		util.ConvIntA2ByteA(iCapability, bCapability);
		System.arraycopy(bCapability, 0, androidpara.Capability, 0, bCapability.length);
		
		int[] iExCapability = new int[]{0xE0, 0x00, 0xF0, 0xA0, 0x01};
		byte[] bExCapability = new byte[iExCapability.length];
		util.ConvIntA2ByteA(iExCapability, bExCapability);
		System.arraycopy(bExCapability, 0, androidpara.ExCapability, 0, bExCapability.length);
		
		byte TransCurrExp = 0x02;
		androidpara.TransCurrExp = TransCurrExp;
		
		byte ReferCurrExp = 0x02;
		androidpara.ReferCurrExp = ReferCurrExp;
		
		int[] iReferCurrCode = new int[]{0x08, 0x40};
		byte[] bReferCurrCode = new byte[iReferCurrCode.length];
		util.ConvIntA2ByteA(iReferCurrCode, bReferCurrCode);
		System.arraycopy(bReferCurrCode, 0, androidpara.ReferCurrCode, 0, bReferCurrCode.length);
		
		byte[] bCountryCode = bReferCurrCode;
		System.arraycopy(bCountryCode, 0, androidpara.CountryCode, 0, bCountryCode.length);
		
		byte[] bTransCurrCode = bReferCurrCode;
		System.arraycopy(bTransCurrCode, 0, androidpara.TransCurrCode, 0, bTransCurrCode.length);
		
		int ReferCurrCon = 1000;
		androidpara.ReferCurrCon = ReferCurrCon;
		
		byte TransType = 0x02;
		androidpara.TransType = TransType;
		
		byte ForceOnline = 0;
		androidpara.ForceOnline = ForceOnline;
		
		byte GetDataPIN = 3;
		androidpara.GetDataPIN = GetDataPIN;
		
		byte SurportPSESel = 1;
		androidpara.SurportPSESel = SurportPSESel;
	
		int iRet = androidemv.EMVSetParameter(androidpara);
		String record = "EMV PARAM ADD: " + String.valueOf(iRet);
		
		record = String.valueOf(record);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	
	private void EMVGetParameterFUN1() {
		String record = "";
		
		int iRet = androidemv.EMVGetParameter(androidpara);
		
		
		byte[] bMerchName = new byte[androidpara.MerchName.length];
		System.arraycopy(androidpara.MerchName, 0, bMerchName, 0, bMerchName.length);
		record = record +"bMerchName:"+bMerchName+"\n";
		
		byte[] bMerchCateCode = new byte[androidpara.MerchCateCode.length];
		System.arraycopy(androidpara.MerchCateCode, 0, bMerchCateCode, 0, bMerchCateCode.length);
		record = record +"bMerchCateCode:"+bMerchCateCode+"\n";
		
		byte[] MerchId = new byte[androidpara.MerchId.length];
		System.arraycopy(androidpara.MerchId, 0, MerchId, 0, MerchId.length);
		record = record +"MerchId:"+MerchId+"\n";
		
		byte[] bTermId = new byte[androidpara.TermId.length];
		System.arraycopy(androidpara.TermId, 0, bTermId, 0, bTermId.length);
		record = record +"bTermId:"+bTermId+"\n";
		
		byte TerminalType = androidpara.TerminalType;
		record = record +"TerminalType:"+TerminalType+"\n";
		
		byte[] bCapability = androidpara.Capability;
		record = record +"bCapability:"+bCapability+"\n";
		
		byte[] bExCapability = androidpara.ExCapability;
		record = record +"bExCapability:"+bExCapability+"\n";
		
		byte TransCurrExp =androidpara.TransCurrExp;
		record = record +"TransCurrExp:"+TransCurrExp+"\n";
		
		byte ReferCurrExp = androidpara.ReferCurrExp;
		record = record +"ReferCurrExp:"+ReferCurrExp+"\n";
		
		byte[] bReferCurrCode = new byte[androidpara.ReferCurrCode.length];
		System.arraycopy(androidpara.ReferCurrCode, 0, bReferCurrCode, 0, bReferCurrCode.length);
		record = record +"bReferCurrCode:"+bReferCurrCode+"\n";
		
		byte[] bCountryCode = new byte[androidpara.CountryCode.length];
		System.arraycopy(androidpara.CountryCode, 0, bCountryCode, 0, bCountryCode.length);
		record = record +"bCountryCode:"+bCountryCode+"\n";
		
		byte[] bTransCurrCode = new byte[androidpara.TransCurrCode.length];
		System.arraycopy(androidpara.TransCurrCode, 0, bTransCurrCode, 0, bTransCurrCode.length);
		record = record +"bTransCurrCode:"+bTransCurrCode+"\n";
		
		int ReferCurrCon = androidpara.ReferCurrCon;
		record = record +"ReferCurrCon:"+ReferCurrCon+"\n";
		
		byte TransType = androidpara.TransType;
		record = record +"TransType:"+TransType+"\n";
		
		byte ForceOnline =androidpara.ForceOnline;
		record = record +"ForceOnline:"+ForceOnline+"\n";
		
		byte GetDataPIN = androidpara.GetDataPIN;
		record = record +"GetDataPIN:"+GetDataPIN+"\n";
		
		byte SurportPSESel = androidpara.SurportPSESel;
		record = record +"SurportPSESel:"+SurportPSESel+"\n\n";
		
	
		record = record + "GetParameter:" + String.valueOf(iRet);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	private void EMVGetParameterFUN2() {
		String record = "";
		
		for(int i = 0;i<100;i++)
		{
			int iRet = androidemv.EMVGetParameter(androidpara);
		
			record += "GetParameter" + (i+1) + ":" + String.valueOf(iRet)+"\n";
		}
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	
	
	private void EMVAddCAPKFUN1(String index) {
		
		int i = Integer.parseInt(index);
		int[] iRID = new int[]{0xA0,0x00,0x00,0x00,0x03};
		iRID[4] = iRID[4]+i;
		byte[] bRID = new byte[iRID.length];
		util.ConvIntA2ByteA(iRID, bRID);
		System.arraycopy(bRID, 0, androidcapk.RID, 0, bRID.length);
		
		byte KeyID = 0x03;
		androidcapk.KeyID = KeyID;
		
		byte HashInd = 0x01;
		androidcapk.HashInd = HashInd;
		
		byte ArithInd = 0x01;
		androidcapk.ArithInd = ArithInd;
		
		byte ModulLen = 112;
		androidcapk.ModulLen = ModulLen;
		
		int[] iModul = new int[]{0xB3,0xE5,0xE6,0x67,0x50,0x6C,0x47,0xCA,0xAF,0xB1,0x2A,0x26,0x33,0x81,0x93,0x50,
				0x84,0x66,0x97,0xDD,0x65,0xA7,0x96,0xE5,0xCE,0x77,0xC5,0x7C,0x62,0x6A,0x66,0xF7,
				0x0B,0xB6,0x30,0x91,0x16,0x12,0xAD,0x28,0x32,0x90,0x9B,0x80,0x62,0x29,0x1B,0xEC,
				0xA4,0x6C,0xD3,0x3B,0x66,0xA6,0xF9,0xC9,0xD4,0x8C,0xED,0x8B,0x4F,0xC8,0x56,0x1C,
				0x8A,0x1D,0x8F,0xB1,0x58,0x62,0xC9,0xEB,0x60,0x17,0x8D,0xEA,0x2B,0xE1,0xF8,0x22,
				0x36,0xFF,0xCF,0xF4,0xF3,0x84,0x3C,0x27,0x21,0x79,0xDC,0xDD,0x38,0x4D,0x54,0x10,
				0x53,0xDA,0x6A,0x6A,0x0D,0x3C,0xE4,0x8F,0xDC,0x2D,0xC4,0xE3,0xE0,0xEE,0xE1,0x5F};
		byte[] bModul = new byte[iModul.length];
		util.ConvIntA2ByteA(iModul, bModul);
		System.arraycopy(bModul, 0, androidcapk.Modul, 0, bModul.length);
		
		byte ExponentLen = 1;
		androidcapk.ExpLen = ExponentLen;
		
		int[] iExponent = new int[]{0x03};
		byte[] bExponent = new byte[iExponent.length];
		util.ConvIntA2ByteA(iExponent, bExponent);
		System.arraycopy(bExponent, 0, androidcapk.Exp, 0, bExponent.length);
		
		int[] iExpDate = new int[]{0x15,0x12,0x31};
		byte[] bExpDate = new byte[iExpDate.length];
		util.ConvIntA2ByteA(iExpDate, bExpDate);
		System.arraycopy(bExpDate, 0, androidcapk.ExpDate, 0, bExpDate.length);
		
		int[] iCheckSum = new int[]{0xFE,0x70,0xAB,0x3B,0x4D,0x5A,0x1B,0x99,0x24,0x22,0x8A,0xDF,0x80,0x27,0xC7,0x58,
				0x48,0x3A,0x8B,0x7E};
		byte[] bCheckSum = new byte[iCheckSum.length];
		util.ConvIntA2ByteA(iCheckSum, bCheckSum);
		System.arraycopy(bCheckSum, 0, androidcapk.CheckSum, 0, bCheckSum.length);
		
		int iRet = androidemv.EMVAddCAPK(androidcapk);
		String record = "CAPK ADD: " + String.valueOf(iRet);
		
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	private void EMVAddCAPKFUN2() {
		
		int[] iRID = new int[]{0xA0,0x00,0x00,0x00,0x03};
		byte[] bRID = new byte[iRID.length];
		util.ConvIntA2ByteA(iRID, bRID);
		System.arraycopy(bRID, 0, androidcapk.RID, 0, bRID.length);
		
		byte KeyID = 0x03;
		androidcapk.KeyID = KeyID;
		
		byte HashInd = 0x01;
		androidcapk.HashInd = HashInd;
		
		byte ArithInd = 0x01;
		androidcapk.ArithInd = ArithInd;
		
		byte ModulLen = 112;
		androidcapk.ModulLen = ModulLen;
		
		int[] iModul = new int[]{0xB3,0xE5,0xE6,0x67,0x50,0x6C,0x47,0xCA,0xAF,0xB1,0x2A,0x26,0x33,0x81,0x93,0x50,
				0x84,0x66,0x97,0xDD,0x65,0xA7,0x96,0xE5,0xCE,0x77,0xC5,0x7C,0x62,0x6A,0x66,0xF7,
				0x0B,0xB6,0x30,0x91,0x16,0x12,0xAD,0x28,0x32,0x90,0x9B,0x80,0x62,0x29,0x1B,0xEC,
				0xA4,0x6C,0xD3,0x3B,0x66,0xA6,0xF9,0xC9,0xD4,0x8C,0xED,0x8B,0x4F,0xC8,0x56,0x1C,
				0x8A,0x1D,0x8F,0xB1,0x58,0x62,0xC9,0xEB,0x60,0x17,0x8D,0xEA,0x2B,0xE1,0xF8,0x22,
				0x36,0xFF,0xCF,0xF4,0xF3,0x84,0x3C,0x27,0x21,0x79,0xDC,0xDD,0x38,0x4D,0x54,0x10,
				0x53,0xDA,0x6A,0x6A,0x0D,0x3C,0xE4,0x8F,0xDC,0x2D,0xC4,0xE3,0xE0,0xEE,0xE1,0x5F};
		byte[] bModul = new byte[iModul.length];
		util.ConvIntA2ByteA(iModul, bModul);
		System.arraycopy(bModul, 0, androidcapk.Modul, 0, bModul.length);
		
		byte ExponentLen = 1;
		androidcapk.ExpLen = ExponentLen;
		
		int[] iExponent = new int[]{0x03};
		byte[] bExponent = new byte[iExponent.length];
		util.ConvIntA2ByteA(iExponent, bExponent);
		System.arraycopy(bExponent, 0, androidcapk.Exp, 0, bExponent.length);
		
		int[] iExpDate = new int[]{0x15,0x12,0x31};
		byte[] bExpDate = new byte[iExpDate.length];
		util.ConvIntA2ByteA(iExpDate, bExpDate);
		System.arraycopy(bExpDate, 0, androidcapk.ExpDate, 0, bExpDate.length);
		
		int[] iCheckSum = new int[]{0xFE,0x70,0xAB,0x3B,0x4D,0x5A,0x1B,0x99,0x24,0x22,0x8A,0xDF,0x80,0x27,0xC7,0x58,
				0x48,0x3A,0x8B,0x7E};
		byte[] bCheckSum = new byte[iCheckSum.length];
		util.ConvIntA2ByteA(iCheckSum, bCheckSum);
		System.arraycopy(bCheckSum, 0, androidcapk.CheckSum, 0, bCheckSum.length);
		
		int iRet = androidemv.EMVAddCAPK(androidcapk);
		String record = "CAPK ADD: " + String.valueOf(iRet);
		
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	private void EMVAddCAPKFUN3() {
		String record ="";
	
		for(int i = 0;i< 64;i++)
		{
			int[] iRID = new int[]{0xA0,0x00,0x00,0x00,0x03};
			iRID[4] = iRID[4] + i;
			byte[] bRID = new byte[iRID.length];
			util.ConvIntA2ByteA(iRID, bRID);
			System.arraycopy(bRID, 0, androidcapk.RID, 0, bRID.length);
			
			byte KeyID = 0x03;
			androidcapk.KeyID = KeyID;
			
			byte HashInd = 0x01;
			androidcapk.HashInd = HashInd;
			
			byte ArithInd = 0x01;
			androidcapk.ArithInd = ArithInd;
			
			byte ModulLen = 112;
			androidcapk.ModulLen = ModulLen;
			
			int[] iModul = new int[]{0xB3,0xE5,0xE6,0x67,0x50,0x6C,0x47,0xCA,0xAF,0xB1,0x2A,0x26,0x33,0x81,0x93,0x50,
					0x84,0x66,0x97,0xDD,0x65,0xA7,0x96,0xE5,0xCE,0x77,0xC5,0x7C,0x62,0x6A,0x66,0xF7,
					0x0B,0xB6,0x30,0x91,0x16,0x12,0xAD,0x28,0x32,0x90,0x9B,0x80,0x62,0x29,0x1B,0xEC,
					0xA4,0x6C,0xD3,0x3B,0x66,0xA6,0xF9,0xC9,0xD4,0x8C,0xED,0x8B,0x4F,0xC8,0x56,0x1C,
					0x8A,0x1D,0x8F,0xB1,0x58,0x62,0xC9,0xEB,0x60,0x17,0x8D,0xEA,0x2B,0xE1,0xF8,0x22,
					0x36,0xFF,0xCF,0xF4,0xF3,0x84,0x3C,0x27,0x21,0x79,0xDC,0xDD,0x38,0x4D,0x54,0x10,
					0x53,0xDA,0x6A,0x6A,0x0D,0x3C,0xE4,0x8F,0xDC,0x2D,0xC4,0xE3,0xE0,0xEE,0xE1,0x5F};
			byte[] bModul = new byte[iModul.length];
			util.ConvIntA2ByteA(iModul, bModul);
			System.arraycopy(bModul, 0, androidcapk.Modul, 0, bModul.length);
			
			byte ExponentLen = 1;
			androidcapk.ExpLen = ExponentLen;
			
			int[] iExponent = new int[]{0x03};
			byte[] bExponent = new byte[iExponent.length];
			util.ConvIntA2ByteA(iExponent, bExponent);
			System.arraycopy(bExponent, 0, androidcapk.Exp, 0, bExponent.length);
			
			int[] iExpDate = new int[]{0x15,0x12,0x31};
			byte[] bExpDate = new byte[iExpDate.length];
			util.ConvIntA2ByteA(iExpDate, bExpDate);
			System.arraycopy(bExpDate, 0, androidcapk.ExpDate, 0, bExpDate.length);
			
			int[] iCheckSum = new int[]{0xFE,0x70,0xAB,0x3B,0x4D,0x5A,0x1B,0x99,0x24,0x22,0x8A,0xDF,0x80,0x27,0xC7,0x58,
					0x48,0x3A,0x8B,0x7E};
			byte[] bCheckSum = new byte[iCheckSum.length];
			util.ConvIntA2ByteA(iCheckSum, bCheckSum);
			System.arraycopy(bCheckSum, 0, androidcapk.CheckSum, 0, bCheckSum.length);
			
			int iRet = androidemv.EMVAddCAPK(androidcapk);
			record = record + "CAPK ADD"+(i+1)+":"+ String.valueOf(iRet)+"\n";
		}
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	private void EMVAddCAPKFUN4() {
		
		int[] iRID = new int[]{0xA0,0x00,0x00,0x00,0x03};
		byte[] bRID = new byte[iRID.length];
		util.ConvIntA2ByteA(iRID, bRID);
		System.arraycopy(bRID, 0, androidcapk.RID, 0, bRID.length);
		
		byte KeyID = 0x03;
		androidcapk.KeyID = KeyID;
		
		byte HashInd = 0x01;
		androidcapk.HashInd = HashInd;
		
		byte ArithInd = 0x01;
		androidcapk.ArithInd = ArithInd;
		
		byte ModulLen = 100;//OCean 112 to 100
		androidcapk.ModulLen = ModulLen;
		
		int[] iModul = new int[]{0xB3,0xE5,0xE6,0x67,0x50,0x6C,0x47,0xCA,0xAF,0xB1,0x2A,0x26,0x33,0x81,0x93,0x50,
				0x84,0x66,0x97,0xDD,0x65,0xA7,0x96,0xE5,0xCE,0x77,0xC5,0x7C,0x62,0x6A,0x66,0xF7,
				0x0B,0xB6,0x30,0x91,0x16,0x12,0xAD,0x28,0x32,0x90,0x9B,0x80,0x62,0x29,0x1B,0xEC,
				0xA4,0x6C,0xD3,0x3B,0x66,0xA6,0xF9,0xC9,0xD4,0x8C,0xED,0x8B,0x4F,0xC8,0x56,0x1C,
				0x8A,0x1D,0x8F,0xB1,0x58,0x62,0xC9,0xEB,0x60,0x17,0x8D,0xEA,0x2B,0xE1,0xF8,0x22,
				0x36,0xFF,0xCF,0xF4,0xF3,0x84,0x3C,0x27,0x21,0x79,0xDC,0xDD,0x38,0x4D,0x54,0x10,
				0x53,0xDA,0x6A,0x6A,0x0D,0x3C,0xE4,0x8F,0xDC,0x2D,0xC4,0xE3,0xE0,0xEE,0xE1,0x5F};
		byte[] bModul = new byte[iModul.length];
		util.ConvIntA2ByteA(iModul, bModul);
		System.arraycopy(bModul, 0, androidcapk.Modul, 0, bModul.length);
		
		byte ExponentLen = 1;
		androidcapk.ExpLen = ExponentLen;
		
		int[] iExponent = new int[]{0x03};
		byte[] bExponent = new byte[iExponent.length];
		util.ConvIntA2ByteA(iExponent, bExponent);
		System.arraycopy(bExponent, 0, androidcapk.Exp, 0, bExponent.length);
		
		int[] iExpDate = new int[]{0x15,0x12,0x31};
		byte[] bExpDate = new byte[iExpDate.length];
		util.ConvIntA2ByteA(iExpDate, bExpDate);
		System.arraycopy(bExpDate, 0, androidcapk.ExpDate, 0, bExpDate.length);
		
		int[] iCheckSum = new int[]{0xFE,0x70,0xAB,0x3B,0x4D,0x5A,0x1B,0x99,0x24,0x22,0x8A,0xDF,0x80,0x27,0xC7,0x58,
				0x48,0x3A,0x8B,0x7E};
		byte[] bCheckSum = new byte[iCheckSum.length];
		util.ConvIntA2ByteA(iCheckSum, bCheckSum);
		System.arraycopy(bCheckSum, 0, androidcapk.CheckSum, 0, bCheckSum.length);
		
		int iRet = androidemv.EMVAddCAPK(androidcapk);
		String record = "CAPK ADD: " + String.valueOf(iRet);
		
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	private void EMVAddCAPKFUN5() {
		String record = "";
		
		for(int i = 0;i<70;i++)
		{
			int[] iRID = new int[]{0xA0,0x00,0x00,0x00,0x03};
			iRID[4] = iRID[4] + (i+60);
			byte[] bRID = new byte[iRID.length];
			util.ConvIntA2ByteA(iRID, bRID);
			System.arraycopy(bRID, 0, androidcapk.RID, 0, bRID.length);
			
			byte KeyID = 0x03;
			androidcapk.KeyID = KeyID;
			
			byte HashInd = 0x01;
			androidcapk.HashInd = HashInd;
			
			byte ArithInd = 0x01;
			androidcapk.ArithInd = ArithInd;
			
			byte ModulLen = 112;
			androidcapk.ModulLen = ModulLen;
			
			int[] iModul = new int[]{0xB3,0xE5,0xE6,0x67,0x50,0x6C,0x47,0xCA,0xAF,0xB1,0x2A,0x26,0x33,0x81,0x93,0x50,
					0x84,0x66,0x97,0xDD,0x65,0xA7,0x96,0xE5,0xCE,0x77,0xC5,0x7C,0x62,0x6A,0x66,0xF7,
					0x0B,0xB6,0x30,0x91,0x16,0x12,0xAD,0x28,0x32,0x90,0x9B,0x80,0x62,0x29,0x1B,0xEC,
					0xA4,0x6C,0xD3,0x3B,0x66,0xA6,0xF9,0xC9,0xD4,0x8C,0xED,0x8B,0x4F,0xC8,0x56,0x1C,
					0x8A,0x1D,0x8F,0xB1,0x58,0x62,0xC9,0xEB,0x60,0x17,0x8D,0xEA,0x2B,0xE1,0xF8,0x22,
					0x36,0xFF,0xCF,0xF4,0xF3,0x84,0x3C,0x27,0x21,0x79,0xDC,0xDD,0x38,0x4D,0x54,0x10,
					0x53,0xDA,0x6A,0x6A,0x0D,0x3C,0xE4,0x8F,0xDC,0x2D,0xC4,0xE3,0xE0,0xEE,0xE1,0x5F};
			byte[] bModul = new byte[iModul.length];
			util.ConvIntA2ByteA(iModul, bModul);
			System.arraycopy(bModul, 0, androidcapk.Modul, 0, bModul.length);
			
			byte ExponentLen = 1;
			androidcapk.ExpLen = ExponentLen;
			
			int[] iExponent = new int[]{0x03};
			byte[] bExponent = new byte[iExponent.length];
			util.ConvIntA2ByteA(iExponent, bExponent);
			System.arraycopy(bExponent, 0, androidcapk.Exp, 0, bExponent.length);
			
			int[] iExpDate = new int[]{0x15,0x12,0x31};
			byte[] bExpDate = new byte[iExpDate.length];
			util.ConvIntA2ByteA(iExpDate, bExpDate);
			System.arraycopy(bExpDate, 0, androidcapk.ExpDate, 0, bExpDate.length);
			
			int[] iCheckSum = new int[]{0xFE,0x70,0xAB,0x3B,0x4D,0x5A,0x1B,0x99,0x24,0x22,0x8A,0xDF,0x80,0x27,0xC7,0x58,
					0x48,0x3A,0x8B,0x7E};
			byte[] bCheckSum = new byte[iCheckSum.length];
			util.ConvIntA2ByteA(iCheckSum, bCheckSum);
			System.arraycopy(bCheckSum, 0, androidcapk.CheckSum, 0, bCheckSum.length);
				
			int iRet = androidemv.EMVAddCAPK(androidcapk);
			record = record +"CAPK ADD"+(i+1)+": " + String.valueOf(iRet)+"\n";
			
		}
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	
	private void EMVGetTotalCAPKNumberFUN1() {
		String record = "";
		int[] TotalNo = new int[1];
		int iRet = androidemv.EMVGetTotalCAPKNumber(TotalNo);
		
		record = "iRet:" + String.valueOf(iRet)+"\n TotalNo:" + TotalNo[0];
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	private void EMVGetTotalCAPKNumberFUN2() {
		String record = "";
		int[] TotalNo = new int[1];
		int iRet = androidemv.EMVGetTotalCAPKNumber(TotalNo);
		
		record = "iRet:" + String.valueOf(iRet)+"\n TotalNo:" + TotalNo[0];
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	private void EMVGetTotalCAPKNumberFUN3() {
		String record = "";
		int[] TotalNo = new int[1];
		int iRet = androidemv.EMVGetTotalCAPKNumber(TotalNo);
		
		record = "iRet:" + String.valueOf(iRet)+"\n TotalNo:" + TotalNo[0];
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	private void EMVGetTotalCAPKNumberFUN4() {
		String record = "";
		int[] TotalNo = new int[1];
		int iRet = androidemv.EMVGetTotalCAPKNumber(TotalNo);
		
		record = "iRet:" + String.valueOf(iRet)+"\n TotalNo:" + TotalNo[0];
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	private void EMVGetTotalCAPKNumberFUN5() {
		String record = "";
		int[] TotalNo = new int[1];
		int iRet = androidemv.EMVGetTotalCAPKNumber(TotalNo);
		
		record = "iRet:" + String.valueOf(iRet)+"\n TotalNo:" + TotalNo[0];
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
		
	}
	
	
	private void EMVGetCAPKFUN1(String index) {
		String record = "";
		int capkID = Integer.parseInt(index);
		int iRet = androidemv.EMVGetCAPK(capkID, androidcapk);
		
	
		byte[] bRID = new byte[androidcapk.RID.length];
		System.arraycopy(androidcapk.RID, 0, bRID, 0, bRID.length);
		record = record +"bRID:"+bRID+"\n";
		
		byte KeyID = androidcapk.KeyID;
		record = record +"KeyID:"+KeyID+"\n";
		
		byte HashInd = androidcapk.HashInd;
		record = record +"HashInd:"+HashInd+"\n";
		
		byte ArithInd = androidcapk.ArithInd;
		record = record +"ArithInd:"+ArithInd+"\n";
		
		byte ModulLen = androidcapk.ModulLen;
		record = record +"ModulLen:"+ModulLen+"\n";
		
		byte[] bModul = new byte[androidcapk.Modul.length];
		System.arraycopy(androidcapk.Modul, 0, bModul, 0, bModul.length);
		record = record +"bModul:"+bModul+"\n";
		
		byte ExponentLen = androidcapk.ExpLen;
		record = record +"ExponentLen:"+ExponentLen+"\n";
		
		byte[] bExponent = new byte[androidcapk.Exp.length];
		System.arraycopy(androidcapk.Exp, 0, bExponent, 0, bExponent.length);
		record = record +"bExponent:"+bExponent+"\n";
		
		byte[] bExpDate = new byte[androidcapk.ExpDate.length];
		System.arraycopy(androidcapk.ExpDate, 0, bExpDate, 0, bExpDate.length);
		record = record +"bExpDate:"+bExpDate+"\n";
		
		byte[] bCheckSum = new byte[androidcapk.ExpDate.length];
		System.arraycopy(androidcapk.CheckSum, 0, bCheckSum, 0, bCheckSum.length);
		record = record +"bCheckSum:"+bCheckSum+"\n\n";
		
		
		record = record + "GetCAPK:" + String.valueOf(iRet);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	private void EMVGetCAPKFUN2() {
		String record = "";
		
		int iRet = androidemv.EMVGetCAPK(1, androidcapk);
		
		record = "GetCAPK:" + String.valueOf(iRet);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	
	private void EMVDelCAPKFUN1(String index) {
		String record = "";
		
		int AidNo = Integer.parseInt(index);;
		int iRet = androidemv.EMVDelCAPK(AidNo);
		
		record = "DelCAPK:" + String.valueOf(iRet);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	private void EMVDelCAPKFUN2() {
		String record = "";
		
		int iRet = androidemv.EMVDelCAPK(64);
		
		record = "DelCAPK:" + String.valueOf(iRet);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	
	private void EMVDelCAPKFUN3() {
		String record = "";
		
		int iRet = androidemv.EMVDelCAPK(64);
		
		record = "DelCAPK:" + String.valueOf(iRet);
		Bundle bundle = new Bundle();
		bundle.putString("result", record);
		Message msg = handler.obtainMessage();
		msg.what = 1;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

}
