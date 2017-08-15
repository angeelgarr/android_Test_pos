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
import com.pax.mposapi.KeyboardManager;
import com.pax.mposapi.UIManager;

public class KeyboardActivity extends Activity {
	
	private String mInterface;
	private String mMethod;
	private String mNo;
	
	private UIManager ui;
	private KeyboardManager kbd;
	private TextView text;
	private ProgressDialog progressDialog;
	
	private final int NORMALRESULT = 1;
	private final int EXCEPTRESULT = 2;

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
		progressDialog = new ProgressDialog(KeyboardActivity.this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Processing");
		progressDialog.setIndeterminate(true);
		progressDialog.show();
				
		new Thread(new Runnable(){
			
			public void run(){
				Looper.prepare();				
				if(mInterface.equals("KbCheck"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestKbCheckFUN1();
						}
						else if(mNo.equals("2"))
						{
							TestKbCheckFUN2();
						}
						else if(mNo.equals("3"))
						{
							TestKbCheckFUN3();
						}
						else if(mNo.equals("4"))
						{
							TestKbCheckFUN4();
						}
					}
				}
				else if(mInterface.equals("KbGetHzString"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestKbGetHzStringFUN1();
						}
						else if(mNo.equals("2"))
						{
							TestKbGetHzStringFUN2();
						}
						else if(mNo.equals("3"))
						{
							TestKbGetHzStringFUN3();
						}
						else if(mNo.equals("4"))
						{
							TestKbGetHzStringFUN4();
						}
					}
				}
				else if(mInterface.equals("KbGetString"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestKbGetStringFUN1();
						}
						else if(mNo.equals("2"))
						{
							TestKbGetStringFUN2();
						}
						else if(mNo.equals("3"))
						{
							TestKbGetStringFUN3();
						}
					}
				}
				else if(mInterface.equals("Kblight"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestKblightFUN1();
						}
					}
				}
				else if(mInterface.equals("KbGetkey"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestKbGetkeyFUN1();
						}
						else if(mNo.equals("2"))
						{
							TestKbGetkeyFUN2();
						}
						else if(mNo.equals("3"))
						{
							TestKbGetkeyFUN3();
						}
						else if(mNo.equals("4"))
						{
							TestKbGetkeyFUN4();
						}
					}
				}
				else if(mInterface.equals("Kbmute"))
				{
					if(mMethod.equals("FUN"))
					{
						if(mNo.equals("1"))
						{
							TestKbmuteFUN1();
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
	
	private void TestKbCheckFUN1()
	{
		String record = "";
		int iRet, step = 0;
		try{
			while(true)
			{
				kbd.kbLock(0);
				step++;
				
				iRet = kbd.kbCheck(0);
				ui.scrCls();
				ui.scrShowText("After kblock(0), kbcheck(0) returns: " + iRet);
				step++;
				
				if(KeyboardManager.KEY_CANCEL == kbd.kbGetkey(3000))
				{
					step++;
					break;
				}
				step++;
			}

			while(true)
			{
				kbd.kbLock(1);
				step++;
				
				iRet = kbd.kbCheck(0);
				ui.scrCls();
				ui.scrShowText("After kblock(1), kbcheck(0) returns: " + iRet);
				step++;
				
				if(KeyboardManager.KEY_CANCEL == kbd.kbGetkey(3000))
				{
					step++;
					break;
				}
				step++;
			}

			while(true)
			{
				kbd.kbLock(2);
				step++;
				
				iRet = kbd.kbCheck(0);
				ui.scrCls();
				ui.scrShowText("After kblock(2), kbcheck(0) returns: " + iRet);
				step++;
				
				if(KeyboardManager.KEY_CANCEL == kbd.kbGetkey(3000))
				{
					step++;
					break;
				}
				step++;
			}

			record += "Keyboard test end"; 

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

	private void TestKbCheckFUN2()
	{
		String record = "";
		int iRet, step = 0;
		try{
			iRet = kbd.kbCheck(1);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505kbcheck(1) returns: " + iRet);
			ui.scrShowText("%P0545Wait 3 seconds...");
			record += "kbcheck(1) returns: " + iRet + "\n";
			SystemClock.sleep(3000);
			step++;

			ui.scrCls();
			ui.scrShowText("%P0505Press 32 keys");
			step++;
			for(int i = 0; i < 32; i++)
			{
				kbd.kbGetkey(3000);
			}
			step++;
			iRet = kbd.kbCheck(1);
			step++;
			ui.scrShowText("%P0545After pressing, kbcheck(1) returns: " + iRet);
			ui.scrShowText("%P0585Wait 3 seconds...");
			record += "After pressing 32 keys, kbcheck(1) returns: " + iRet + "\n";
			SystemClock.sleep(3000);
			step++;

			ui.scrCls();
			ui.scrShowText("%P0505One more pressing");
			step++;
			kbd.kbGetkey(3000);
			step++;
			iRet = kbd.kbCheck(1);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0545After pressing, kbcheck(1) returns: " + iRet);
			ui.scrShowText("%P0585Wait 3 seconds...");
			record += "After pressing one more key, kbcheck(1) returns: " + iRet + "\n";
			SystemClock.sleep(3000);
			step++;

			ui.scrCls();
			ui.scrShowText("%P0505Get key: ");
			step++;
			iRet = kbd.kbGetkey(3000);
			ui.scrCls();
			ui.scrShowText(Integer.toString(iRet));
			ui.scrShowText("%P0545Wait 3 seconds...");
			record += "Get key: " + iRet + "\n";
			SystemClock.sleep(3000);
			step++;

			record += "Keyboard test end"; 

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

	private void TestKbCheckFUN3()
	{
		String record = "";
		int iRet, step = 0;
		try{
			kbd.kbmute((byte) 0);
			step++;
			iRet = kbd.kbCheck(2);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505After kbmute(0), kbcheck(2) returns: " + iRet);
			ui.scrShowText("%P0545Press Cancel Key to continue");
			record += "After kbmute(0), kbcheck(2) returns: " + iRet + "\n";
			step++;
			
			while(true)
			{
				if(KeyboardManager.KEY_CANCEL == kbd.kbGetkey(3000))
				{
					step++;
					break;
				}
				step++;
			}

			kbd.kbmute((byte) 1);
			step++;
			iRet = kbd.kbCheck(2);
			step++;
			ui.scrCls();
			ui.scrShowText("P0100After kbmute(1), kbcheck(2) returns: " + iRet);
			ui.scrShowText("P0300Press Cancel Key to exit");
			record += "After kbmute(1), kbcheck(2) returns: " + iRet + "\n";
			step++;

			while(true)
			{
				if(KeyboardManager.KEY_CANCEL == kbd.kbGetkey(3000))
				{
					step++;
					break;
				}
				step++;
			}
			
			record += "Keyboard test end"; 

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

	private void TestKbCheckFUN4()
	{
		String record = "";
		int iRet, step = 0;
		try{
			kbd.kblight(0);
			step++;
			iRet = kbd.kbCheck(3);
			step++;
			ui.scrCls();
			ui.scrShowText("After kblight(0), kbcheck(3) returns: " + iRet);
			record += "After kblight(0), kbcheck(3) returns: " + iRet + "\n";
			step++;
			kbd.kbGetkey(3000);
			step++;

			kbd.kblight(1);
			step++;
			iRet = kbd.kbCheck(3);
			step++;
			ui.scrCls();
			ui.scrShowText("After kblight(1), kbcheck(3) returns: " + iRet);
			record += "After kblight(1), kbcheck(3) returns: " + iRet + "\n";
			step++;
			kbd.kbGetkey(3000);
			step++;

			kbd.kblight(2);
			step++;
			iRet = kbd.kbCheck(3);
			step++;
			ui.scrCls();
			ui.scrShowText("After kblight(2), kbcheck(3) returns: " + iRet);
			record += "After kblight(2), kbcheck(3) returns: " + iRet + "\n";
			step++;
			kbd.kbGetkey(3000);
			step++;

			kbd.kblight(3);
			step++;
			iRet = kbd.kbCheck(3);
			step++;
			ui.scrCls();
			ui.scrShowText("After kblight(3), kbcheck(3) returns: " + iRet);
			record += "After kblight(3), kbcheck(3) returns: " + iRet + "\n";
			step++;
			kbd.kbGetkey(3000);
			step++;
			
			record += "Keyboard test end"; 

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

	private void TestKbGetHzStringFUN1()
	{
		String record = "";
		byte[] status = new byte[1];
		int step = 0;
		try{
			String hzString = kbd.kbGetHzString(128, 0, "", status);
			step++;
			record += "get hz str status: " + status[0] + ", string is: " + hzString + "\n";					
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input:\n" + hzString);
				kbd.kbGetkey(3000);
			}
			step++;
			
			record += "Keyboard test end"; 

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

	private void TestKbGetHzStringFUN2()
	{
		String record = "";
		byte[] status = new byte[1];
		int step = 0;
		try{
			String hzString = kbd.kbGetHzString(1, 0, "百富科技PAXTechnology", status);
			step++;
			record += "get hz str status: " + status[0] + ", string is: " + hzString + "\n";					
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input:\n" + hzString);
				kbd.kbGetkey(3000);
			}
			step++;
			
			hzString = kbd.kbGetHzString(6, 0, "百富科技PAXTechnology", status);
			step++;
			record += "get hz str status: " + status[0] + ", string is: " + hzString + "\n";					
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input:\n" + hzString);
				kbd.kbGetkey(3000);
			}
			step++;

			hzString = kbd.kbGetHzString(128, 0, "百富科技PAXTechnology", status);
			step++;
			record += "get hz str status: " + status[0] + ", string is: " + hzString + "\n";					
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input:\n" + hzString);
				kbd.kbGetkey(3000);
			}
			step++;
			
			record += "Keyboard test end"; 

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

	private void TestKbGetHzStringFUN3()
	{
		String record = "";
		byte[] status = new byte[1];
		int step = 0;
		try{
			String hzString = kbd.kbGetHzString(128, 0, "", status);
			step++;
			record += "get hz str status: " + status[0] + ", string is: " + hzString + "\n";					
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input:\n" + hzString);
				kbd.kbGetkey(3000);
			}
			step++;

			hzString = kbd.kbGetHzString(128, 100, "", status);
			step++;
			record += "get hz str status: " + status[0] + ", string is: " + hzString + "\n";					
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input:\n" + hzString);
				kbd.kbGetkey(3000);
			}
			step++;

			hzString = kbd.kbGetHzString(128, 3000, "", status);
			step++;
			record += "get hz str status: " + status[0] + ", string is: " + hzString + "\n";					
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input:\n" + hzString);
				kbd.kbGetkey(3000);
			}
			step++;

			hzString = kbd.kbGetHzString(128, 65535, "", status);
			step++;
			record += "get hz str status: " + status[0] + ", string is: " + hzString + "\n";					
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input:\n" + hzString);
				kbd.kbGetkey(3000);
			}
			step++;

			record += "Keyboard test end"; 

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
	
	private void TestKbGetHzStringFUN4()
	{
		String record = "";
		byte[] status = new byte[1];
		int step = 0;
		try{
			String hzString = kbd.kbGetHzString(128, 0, "", status);
			step++;
			record += "get hz str status: " + status[0] + ", string is: " + hzString + "\n";					
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input:\n" + hzString);
				kbd.kbGetkey(3000);
			}
			step++;

			record += "Keyboard test end"; 

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

	private void TestKbGetStringFUN1()
	{
		String record = "";
		byte[] status = new byte[1];
		int step = 0;
		try{
			String retString = kbd.kbGetString(0xa5, 0, 128, 0, 0, "abc", status);
			step++;
			record += "input pwd status: " + status[0] + ", string is: " + retString + "\n";
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input\n" + retString);
				kbd.kbGetkey(3000);
			}
			step++;
			
			retString = kbd.kbGetString(0x25, 0, 128, 0, 0, "abc", status);
			step++;
			record += "input pwd status: " + status[0] + ", string is: " + retString + "\n";
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input\n" + retString);
				kbd.kbGetkey(3000);
			}
			step++;

			retString = kbd.kbGetString(0x51, 0, 128, 0, 0, "abc", status);
			step++;
			record += "input pwd status: " + status[0] + ", string is: " + retString + "\n";
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input\n" + retString);
				kbd.kbGetkey(3000);
			}
			step++;

			retString = kbd.kbGetString(0x11, 0, 128, 0, 0, "abc", status);
			step++;
			record += "input pwd status: " + status[0] + ", string is: " + retString + "\n";
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input\n" + retString);
				kbd.kbGetkey(3000);
			}
			step++;

			retString = kbd.kbGetString(0x17, 0, 128, 0, 0, "abc", status);
			step++;
			record += "input pwd status: " + status[0] + ", string is: " + retString + "\n";
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input\n" + retString);
				kbd.kbGetkey(3000);
			}
			step++;

			retString = kbd.kbGetString(0x27, 0, 128, 0, 0, "abc", status);
			step++;
			record += "input pwd status: " + status[0] + ", string is: " + retString + "\n";
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input\n" + retString);
				kbd.kbGetkey(3000);
			}
			step++;

			retString = kbd.kbGetString(0x27, 0, 128, 0, 0, "abc", status);
			step++;
			record += "input pwd status: " + status[0] + ", string is: " + retString + "\n";
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input\n" + retString);
				kbd.kbGetkey(3000);
			}
			step++;

			retString = kbd.kbGetString(0x17, 0, 128, 0, 0, "abc", status);
			step++;
			record += "input pwd status: " + status[0] + ", string is: " + retString + "\n";
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input\n" + retString);
				kbd.kbGetkey(3000);
			}
			step++;

			retString = kbd.kbGetString(0x6d, 0, 128, 0, 0, "abc", status);
			step++;
			record += "input pwd status: " + status[0] + ", string is: " + retString + "\n";
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input\n" + retString);
				kbd.kbGetkey(3000);
			}
			step++;
			
			record += "Keyboard test end"; 

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

	private void TestKbGetStringFUN2()
	{
		String record = "";
		byte[] status = new byte[1];
		int step = 0;
		try{
			String retString = kbd.kbGetString(0xa5, 6, 20, 0, 0, "", status);
			step++;
			record += "input pwd status: " + status[0] + ", string is: " + retString + "\n";
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input\n" + retString);
				kbd.kbGetkey(3000);
			}
			step++;
			
			retString = kbd.kbGetString(0x25, 6, 20, 0, 0, "", status);
			step++;
			record += "input pwd status: " + status[0] + ", string is: " + retString + "\n";
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input\n" + retString);
				kbd.kbGetkey(3000);
			}
			step++;
			
			record += "Keyboard test end"; 

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

	private void TestKbGetStringFUN3()
	{
		String record = "";
		byte[] status = new byte[1];
		int step = 0;
		try{
			String retString = kbd.kbGetString(0xfe, 6, 20, 0, 0, "", status);
			step++;
			record += "input pwd status: " + status[0] + ", string is: " + retString + "\n";
			if (0 == status[0]) {
				ui.scrCls();
				ui.scrShowText("you just input\n" + retString);
				kbd.kbGetkey(3000);
			}
			step++;
			
			record += "Keyboard test end"; 

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

	private void TestKblightFUN1()
	{
		String record = "";
		int step = 0;
		try{
			kbd.kblight(0);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505Set kblight(0)");
			ui.scrShowText("%P0545Press Cancel Key to continue");
			step++;

			while(true)
			{
				if(KeyboardManager.KEY_CANCEL == kbd.kbGetkey(3000))
				{
					step++;
					break;
				}
				step++;
			}

			kbd.kblight(1);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505Set kblight(1)");
			ui.scrShowText("%P0545Press Cancel Key to continue");
			step++;

			while(true)
			{
				if(KeyboardManager.KEY_CANCEL == kbd.kbGetkey(3000))
				{
					step++;
					break;
				}
				step++;
			}

			kbd.kblight(2);
			step++;
			ui.scrCls();
			ui.scrShowText("%P0505Set kblight(2)");
			ui.scrShowText("%P0545Press Cancel Key to continue");
			step++;

			while(true)
			{
				if(KeyboardManager.KEY_CANCEL == kbd.kbGetkey(3000))
				{
					step++;
					break;
				}
				step++;
			}
			
			record += "Keyboard test end"; 

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

	private void TestKbGetkeyFUN1()
	{
		String record = "";
		int iRet, step = 0;
		try{
			record += "Get key: ";
			while(true)
			{
				ui.scrCls();
				ui.scrShowText("%P0505Press Cancel key to exit");
				ui.scrShowText("%P0525Wait key...");
				step++;
				iRet = kbd.kbGetkey(5000);
				step++;
				ui.scrShowText("%P0545Get key: " + iRet);
				ui.scrShowText("%P0565Wait 3 seconds...");
				record += Integer.toString(iRet) + ", ";
				SystemClock.sleep(3000);
				step++;
				
				if(KeyboardManager.KEY_CANCEL == iRet)
				{
					step++;
					break;
				}
				step++;
			}
			
			record += "Keyboard test end"; 

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

	private void TestKbGetkeyFUN2()
	{
		String record = "";
		int iRet, step = 0;
		try{
			while(true)
			{
				ui.scrCls();
				ui.scrShowText("%P0502Press Cancel key to exit");
				ui.scrShowText("%P0520Wait key...");
				step++;
				iRet = kbd.kbGetkey(5000);
				step++;
				ui.scrShowText("%P0538Get first key: " + iRet);
				record += "Get first key: " + iRet + ", ";
				step++;

				if(KeyboardManager.KEY_CANCEL == iRet)
				{
					break;
				}

				ui.scrShowText("%P0556Wait key...");
				step++;
				iRet = kbd.kbGetkey(5000);
				step++;
				ui.scrShowText("%P0574Get second key: " + iRet);
				ui.scrShowText("%P0592Wait 3 seconds...");
				record += "Get second key: " + iRet + "\n";
				SystemClock.sleep(3000);
				step++;
				
				if(KeyboardManager.KEY_CANCEL == iRet)
				{
					break;
				}
			}
			
			record += "Keyboard test end"; 

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

	private void TestKbGetkeyFUN3()
	{
		String record = "";
		int iRet, step = 0;
		try{
			while(true)
			{
				ui.scrCls();
				ui.scrShowText("%P0502Press Cancel key to exit");
				ui.scrShowText("%P0520Wait key...");
				step++;
				iRet = kbd.kbGetkey(5000);
				step++;
				ui.scrShowText("%P0538Get first key: " + iRet);
				record += "Get first key: " + iRet + ", ";
				step++;

				if(KeyboardManager.KEY_CANCEL == iRet)
				{
					break;
				}
				
				ui.scrShowText("%P0556Wait key...");
				step++;
				iRet = kbd.kbGetkey(5000);
				step++;
				ui.scrShowText("%P0574Get second key: " + iRet);
				ui.scrShowText("%P0592Wait 3 seconds...");
				record += "Get second key: " + iRet + "\n";
				SystemClock.sleep(3000);
				step++;
				
				if(KeyboardManager.KEY_CANCEL == iRet)
				{
					break;
				}
			}
			
			record += "Keyboard test end"; 

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

	private void TestKbGetkeyFUN4()
	{
		String record = "";
		int iRet, step = 0;
		try{
			ui.scrCls();
			ui.scrShowText("%P0505Get key: ");
			record += "Get key: ";
			step++;
			for(int i = 0; i < 33; i++)
			{
				iRet = kbd.kbGetkey(3000);
				step++;
				ui.scrShowText(Integer.toString(iRet) + ", ");
				record += Integer.toString(iRet) + ", ";
				step++;
			}
			kbd.kbGetkey(5000);
			step++;
			
			record += "Keyboard test end"; 

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

	private void TestKbmuteFUN1()
	{
		String record = "";
		int step = 0;
		try{
			for(int i = 0; i < 3; i++)
			{
				kbd.kbmute((byte) 0);
				step++;
				
				ui.scrCls();
				ui.scrShowText("%P0505Set kbmute(0)");
				ui.scrShowText("%P0545Press Cancel key to continue");
				step++;
				
				while(true)
				{
					if(KeyboardManager.KEY_CANCEL == kbd.kbGetkey(3000))
					{
						step++;
						break;
					}
					step++;
				}

				kbd.kbmute((byte) 1);
				step++;
				
				ui.scrCls();
				ui.scrShowText("%P0505Set kbmute(1)");
				ui.scrShowText("%P0545Press Cancel key to continue");
				step++;
				
				while(true)
				{
					if(KeyboardManager.KEY_CANCEL == kbd.kbGetkey(3000))
					{
						step++;
						break;
					}
					step++;
				}
			}
			
			record += "Keyboard test end"; 

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
